package de.l3s.eumssi.action;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.opensymphony.xwork2.Action;

import de.l3s.eumssi.dao.MongoDBManager;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class textsyncAction implements Action, ServletRequestAware{
	
	MongoDBManager mongo=new MongoDBManager(); 
    DBCollection personCollection=mongo.getCollection("person");
    DBCollection locationCollection=mongo.getCollection("location");
	private String myparam;
	private String videoUrl;
	private String url;
	public String jsonFileName;
    public	String entities[];
    public int hour=00;
    public int min=00;
    public int sec=00;
    public String dataContent=null;	
	public String content1 = "{\"localisation\": [{ \"sublocalisations\": { \"localisation\": [";
    public String content8="]},\"type\": \"text\",\"tcin\": \"00:00:00.0000\",\"tcout\": \"02:00:00.0000\",\"tclevel\": 0}],\"id\": \"text-amalia01\",\"type\": \"text\",\"algorithm\": \"demo-video-generator\",\"processor\": \"Ina Research Department - N. HERVE\", \"processed\": 1421141589288, \"version\": 1}";
	public ArrayList<BasicDBObject> mainArrayList = new ArrayList<BasicDBObject>(); //3d Array for holding all the property-value pair of entities.
	public ArrayList<String> thumbNailsList= new ArrayList<String>(); 
		HttpServletRequest request; 
	 

	//function for making data to write in json file for amalia player
		private void makeData(String data, int dataCounter, String thumbNailUrl ){
		 String content2= "{\"data\": {\"text\": [";
         String content3="]},";
         String content4="\"tcin\":";
         String content5=",\"tcout\":";
         
         String content6=",\"tclevel\": 1}";
         String content7=",";
         String dataContentDemo=null;
         String tcin;
		 String tcout;
		 
		
		 if(sec==60){
			  sec=00;
			  sec=sec+10;
			  min=min+1;
			  }
		  else
			  sec=sec+10;
		  if(min==60){
			  sec=00;
		      min=00;
		      hour=hour+1;
		   }
		  tcin=new DecimalFormat("00").format(hour)+":"+new DecimalFormat("00").format(min)+":"+new DecimalFormat("00").format(sec)+"."+"0000";
		  //tcin=hour+":"+min+":"+sec+"."+"0000";
		  if(sec<60)
		  sec=sec+10;
		  else{
			  sec=00;
			  min=min+1;
		  }
		  tcout=new DecimalFormat("00").format(hour)+":"+new DecimalFormat("00").format(min)+":"+new DecimalFormat("00").format(sec)+"."+"0000";
		  //System.out.println(data);
		 
		  if(dataContent!=null){
		      dataContentDemo=content7+content2+"\""+data+"\""+content3+content4+"\""+tcin+"\""+content5+"\""+tcout+"\""+",\"thumb\""+":"+"\""+thumbNailUrl+"\""+content6;
			  dataContent=dataContent+dataContentDemo;
		  }
		  else
		  {
		  dataContentDemo=content2+"\""+data+"\""+content3+content4+"\""+tcin+"\""+content5+"\""+tcout+"\""+",\"thumb\""+":"+"\""+thumbNailUrl+"\""+content6;
		  dataContent=dataContentDemo;
		  }
		 
		System.out.println(dataContent);
	}
	//Function for writting to json file for Amalia player
	
	private void jsonWriter(  ArrayList<BasicDBObject> mainArrayList) throws IOException{
		String mainContent;
		Map<String,String> locationMapQuestion =new HashMap<String,String>();
		Map<String,String> personMapQuestion =new HashMap<String,String>();
		
		Map<String,String> locationMapInfo =new HashMap<String,String>();
		Map<String,String> personMapInfo =new HashMap<String,String>();
		
		locationMapQuestion.put("currency", "What is the name of the currency?");
		locationMapQuestion.put("officialLanguage","What language is spoken here?");
		locationMapQuestion.put("capital","What is the name of the capital?");
		locationMapQuestion.put("country","In what country is it located?");
		
		personMapQuestion.put("birthPlace","Where was this person born?");
		personMapQuestion.put("almaMater","Which university or college did this person attend??");
		
		locationMapInfo.put("currency", "The local currency is ");
		locationMapInfo.put("officialLanguage","The language spoken is ");
		locationMapInfo.put("capital","The capital is ");
		locationMapInfo.put("country","This city is located in ");
		
		personMapInfo.put("birthPlace","City of birth: ");
		personMapInfo.put("almaMater","College attended: ");
		personMapInfo.put("birthdate","Date of birth: " );
		personMapInfo.put("spouse","Spouse: ");
		
	int  dataCounter=0;
	 for(int i=0;i<mainArrayList.size();i++){
		 String  dicision;
		 String type=null;
		 ArrayList<String> tempKeyArrayList=new ArrayList<String>(); 
		
		 Random ran = new Random();
		   int x = ran.nextInt(4) + 1;
           if(x==1)dicision="question";
           if(x==2)dicision="question";
           else if(x==3)dicision="info";
           else dicision="abstract"; 
		 
                 BasicDBObject entity = mainArrayList.get(i); 
				  
				  if((boolean) mainArrayList.get(i).get("type").equals("location"))
				  {
					 type="location"; 
				  }
				  else if((boolean) mainArrayList.get(i).get("type").equals("person")){
					  type="person";
				  }
				  else if((boolean) mainArrayList.get(i).get("type").equals("other")){
					  type="other";
				  }
				  
				  if(type.equals("location")){
					  String locationName= (String) entity.get("name"); 
						locationName=locationName.replaceAll("[_]"," ");
					  if(dicision.equals("question")){
						    for(Iterator iteratorForKeyIntersection = mainArrayList.get(i).keySet().iterator(); iteratorForKeyIntersection.hasNext();){ 
								  String keyForIntersection= (String) iteratorForKeyIntersection.next(); 				  
						 if(locationMapQuestion.containsKey(keyForIntersection)){
					       tempKeyArrayList.add(keyForIntersection); 		
						 } 
					  }
					if(tempKeyArrayList.size()==0)continue;
						    
						int questionSelectorNumber = ran.nextInt(tempKeyArrayList.size());
						String mainKeyForQuestion=tempKeyArrayList.get(questionSelectorNumber);
						double longi=Double.parseDouble((String) entity.get("long"));
						double lat=Double.parseDouble((String) entity.get("lat"));
                 		String mainKeyValue=(String)entity.get(mainKeyForQuestion);
						ArrayList options=GetLocationFalseAns(mainKeyForQuestion,mainKeyValue,longi,lat) ;
						
						
						
						String question="<div><img src=Images"+"//"+"quiz.png><strong>"+locationName+"</strong><br>"+locationMapQuestion.get(mainKeyForQuestion)+
								"<br><input type='radio' name=\'"+mainKeyValue+"\' value=\'"+options.get(0)+"\'>"+options.get(0)+
								"<br><input type='radio' name=\'"+mainKeyValue+"\' value=\'"+options.get(1)+"\'>"+options.get(1)+
								"<br><input type='radio' name=\'"+mainKeyValue+"\' value=\'"+options.get(2)+"\'>"+options.get(2)+
								"<br><input type='radio' name=\'"+mainKeyValue+"\' value=\'"+options.get(3)+"\'>"+options.get(3)+
								"<br><input type='button'  value='check'></div>";
						
					//	System.out.println(question);
						makeData(question,i+1,(String) entity.get("thumbnail"));
				  }
				    if(dicision.equals("info")){
				    	for(Iterator iteratorForKeyIntersection = mainArrayList.get(i).keySet().iterator(); iteratorForKeyIntersection.hasNext();){ 
								  String keyForIntersection= (String) iteratorForKeyIntersection.next(); 				  
						 if(locationMapQuestion.containsKey(keyForIntersection)){
					       tempKeyArrayList.add(keyForIntersection); 		
						 } 
					  }
				    	if(tempKeyArrayList.size()==0)continue;
				    	
						int infoSelectorNumber = ran.nextInt(tempKeyArrayList.size());
						String mainKeyForInfo=tempKeyArrayList.get(infoSelectorNumber);
					//	System.out.println("tempkeyarraylist:"+tempKeyArrayList);
						String mainKeyValue=(String)entity.get(mainKeyForInfo);
					    String info="<img src=Images"+"/"+"Info.png><strong>"+locationName+"</strong>"+"<br>"+locationMapInfo.get(mainKeyForInfo)+ mainKeyValue;
					//	System.out.println(info);
					    makeData(info,i+1,(String) entity.get("thumbnail"));
					    
			
				  }
				    if(dicision.equals("abstract")){
				    	System.out.println("dicision abstract");
				    	if((String) entity.get("abstract")==null)continue;
				    	String abs="<strong>"+locationName+"</strong>"+"<br>"+(String) entity.get("abstract");
				    	abs=abs.replaceAll("\\(.+?\\)\\s*", "");
				    	System.out.println(abs);
				    	makeData(abs,i+1,(String) entity.get("thumbnail"));
				    }
				  
				  
		}
				  else if(type.equals("person")){
					  String personName= (String) entity.get("name"); 
						personName=personName.replaceAll("[_]"," ");
					  if(dicision.equals("question")){
						    for(Iterator iteratorForKeyIntersection = mainArrayList.get(i).keySet().iterator(); iteratorForKeyIntersection.hasNext();){ 
								  String keyForIntersection= (String) iteratorForKeyIntersection.next(); 				  
						 if(personMapQuestion.containsKey(keyForIntersection)){
					       tempKeyArrayList.add(keyForIntersection); 		
						 } 
					  }
						    if(tempKeyArrayList.size()==0)continue;	   
						int questionSelectorNumber = ran.nextInt(tempKeyArrayList.size());
						String mainKeyForQuestion=tempKeyArrayList.get(questionSelectorNumber);
				
               		String mainKeyValue=(String)entity.get(mainKeyForQuestion);
						ArrayList options=GetPersonFalseAns(mainKeyForQuestion,mainKeyValue) ;
						
						String question="<div><img src=Images"+"//"+"quiz.png><strong>"+personName+"</strong><br>"+personMapQuestion.get(mainKeyForQuestion)+
								"<br><input type='radio' name=\'"+mainKeyValue+"\' value=\'"+options.get(0)+"\'>"+options.get(0)+
								"<br><input type='radio' name=\'"+mainKeyValue+"\' value=\'"+options.get(1)+"\'>"+options.get(1)+
								"<br><input type='radio' name=\'"+mainKeyValue+"\' value=\'"+options.get(2)+"\'>"+options.get(2)+
								"<br><input type='radio' name=\'"+mainKeyValue+"\' value=\'"+options.get(3)+"\'>"+options.get(3)+
								"<br><input type='button'  value='check'></div>";
						
						System.out.println(question);
						makeData(question,i+1,(String) entity.get("thumbnail"));
				  }
				    if(dicision.equals("info")){
				    	for(Iterator iteratorForKeyIntersection = mainArrayList.get(i).keySet().iterator(); iteratorForKeyIntersection.hasNext();){ 
								  String keyForIntersection= (String) iteratorForKeyIntersection.next(); 				  
						 if(personMapQuestion.containsKey(keyForIntersection)){
					       tempKeyArrayList.add(keyForIntersection); 		
						 } 
					  }
				    	if(tempKeyArrayList.size()==0)continue;
						int infoSelectorNumber = ran.nextInt(tempKeyArrayList.size());
						String mainKeyForInfo=tempKeyArrayList.get(infoSelectorNumber);
						String mainKeyValue=(String)entity.get(mainKeyForInfo);
					    String info="<img src=Images"+"/"+"Info.png><strong>"+personName+"</strong>"+"<br>"+personMapInfo.get(mainKeyForInfo)+ mainKeyValue;
						System.out.println(info);
					    makeData(info,i+1,(String) entity.get("thumbnail"));
					    
			
				  }
				    if(dicision.equals("abstract")){
				    	System.out.println("dicision abstract");
				    	if((String) entity.get("abstract")==null)continue;
				    	String abs="<strong>"+personName+"</strong>"+"<br>"+(String) entity.get("abstract");
				    	System.out.println(abs);
				    	abs=abs.replaceAll("\\(.+?\\)\\s*", "");
				    	makeData(abs,i+1,(String) entity.get("thumbnail"));
				    }
				  
	
					  
				  }
				  
				  else if(type.equals("other")){
					  String otherEntityName= (String) entity.get("name"); 
						otherEntityName=otherEntityName.replaceAll("[_]"," ");
					  String entityAbstract="<strong>"+otherEntityName+"</strong><br>"+(String) entity.get("abstract");
					  entityAbstract=entityAbstract.replaceAll("[\"]","");
					  entityAbstract=entityAbstract.replaceAll("\\(.+?\\)\\s*", "");
					  makeData(entityAbstract,i+1,(String) entity.get("thumbnail"));
				  }
				  
	 }
	    
		mainContent=content1+dataContent+content8;
   //  File file = new File("G:\\workspace\\eventsense\\WebContent\\scripts\\"+jsonFileName+".json");

		ServletContext context = request.getServletContext();
		String path = context.getRealPath("/");
	//	System.out.println(path);

		File file = new File(path+File.separator+"scripts"+File.separator+jsonFileName+".json");

	

		System.out.println("Local filename to write: " + file.getAbsolutePath());
	    // if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(mainContent);
		bw.close();

	 
	}
	
	private ArrayList GetPersonFalseAns(String keyName,String keyValue){
		ArrayList<String> options= new ArrayList<String>();
		BasicDBObject whereQuery = new BasicDBObject();
			
			whereQuery.put(keyName,java.util.regex.Pattern.compile("."));
		 BasicDBObject projectionQuery = new BasicDBObject();
			projectionQuery.put("_id", 0);
			projectionQuery.put(keyName, 1);
			

			 Random ran = new Random();
			   int x = ran.nextInt(440) + 1;
			DBCursor randomPersonCursor = personCollection.find(whereQuery,projectionQuery).limit(3).skip( x );
			
	//System.out.println("location cursor size:"+locationCursor);
			while(randomPersonCursor.hasNext()) {
			   BasicDBObject entity=(BasicDBObject) randomPersonCursor.next();
			   String tempValue= (String) entity.get(keyName);
			   String[] value=(String[])tempValue.split("[,]");
			   
			   options.add(value[0]);
			}
			options.add(keyValue);
			Collections.shuffle(options);
			return options;
	}
	
	private ArrayList GetLocationFalseAns(String keyName, String keyValue, double longi, double lat){
		ArrayList optionList=new ArrayList();
        ArrayList distances=new ArrayList();
        Map <Double,String> distanceToKeyValue=new HashMap<Double,String>();
		 BasicDBObject whereQuery = new BasicDBObject();
	
			whereQuery.put(keyName,java.util.regex.Pattern.compile("."));
		 BasicDBObject projectionQuery = new BasicDBObject();
			projectionQuery.put("_id", 0);
			projectionQuery.put(keyName, 1);
			projectionQuery.put("long", 1);
			projectionQuery.put("lat", 1);
			DBCursor locationCursor = locationCollection.find(whereQuery,projectionQuery);
			
	//System.out.println("location cursor size:"+locationCursor);
			while(locationCursor.hasNext()) {
			   BasicDBObject entity=(BasicDBObject) locationCursor.next();
			  if(entity.containsField("lat") && entity.containsField("long")){
			   double falseLat=Double.parseDouble((String) entity.get("lat"));
			   double falseLong=Double.parseDouble((String) entity.get("long"));
               double distance=getDistance(falseLat,falseLong,lat,longi);
               distances.add(distance);
               String tempValue=(String)entity.get(keyName);
			   distanceToKeyValue.put(distance,tempValue );
			  }
			  else
				  continue;
			}
		Collections.sort(distances);
		optionList.add(keyValue);
		
    
		for(int i=0;i<distances.size();i++){
        if(!optionList.contains(distanceToKeyValue.get(distances.get(i)))){
			optionList.add(distanceToKeyValue.get(distances.get(i)));
          if(optionList.size()>3){
        	  break;
          }		
		}
		}
	Collections.shuffle(optionList);
		
		return optionList;
		
	}
	
	private double getDistance(double submittedCountryLat,double submittedCountryLong,double countryLat,double countryLong){

		double distance = 0;
		double submittedCountryLatRadian=Math.toRadians(submittedCountryLat);		
		double submittedCountryLongRadian=Math.toRadians(submittedCountryLong);
		double countryLatRadian=Math.toRadians(countryLat);
		double countryLongRadian=Math.toRadians(countryLong);
		
		double absoluteDistanceLat=submittedCountryLatRadian-countryLatRadian;
		double absoluteDistanceLong=submittedCountryLongRadian-countryLongRadian;
		
		distance=Math.sin(absoluteDistanceLat/2)*Math.sin(absoluteDistanceLat/2) +
				Math.cos(submittedCountryLatRadian)*Math.cos(countryLatRadian)* Math.sin(absoluteDistanceLong/2)*Math.sin(absoluteDistanceLong/2);
		  
		distance=2* Math.asin(Math.sqrt(distance));
		return distance*6371;
		
	}

	public String execute() throws FileNotFoundException, IOException, ParseException
	{
		System.out.println(videoUrl);
	
		ArrayList<String> subSubArray;
		 ArrayList<ArrayList<String>> subArray;
		 entities=myparam.split(",");
		 
		 for(int entityCounter=1;entityCounter<=entities.length;entityCounter++){
			 if(jsonFileName==null)
				 jsonFileName=entities[entityCounter-1];
			 else
			    jsonFileName=jsonFileName+entities[entityCounter-1];
		 }
		 
         JSONParser parser = new JSONParser();
         try{
        	 for(int entityCounter=1;entityCounter<entities.length;entityCounter++ ){
        		 BasicDBObject whereQuery = new BasicDBObject();
        			whereQuery.put("name",entities[entityCounter]);
        			BasicDBObject projectionQuery = new BasicDBObject();
        			projectionQuery.put("_id", 0);
        			DBCursor locationCursor = locationCollection.find(whereQuery,projectionQuery);
        			DBCursor personCursor = personCollection.find(whereQuery,projectionQuery);
        	
        			while(locationCursor.hasNext()) {
        			   BasicDBObject entity=(BasicDBObject) locationCursor.next();
        		    
        			   mainArrayList.add(entity);
        			   
        			}
        			while(personCursor.hasNext()) {
         			   
        				  BasicDBObject entity=(BasicDBObject) personCursor.next();
           			  
           			   mainArrayList.add(entity);
         			}
         			if(locationCursor.count()==0 && personCursor.count()==0){
         			   BasicDBObject entityOther=abstractFinder((String)entities[entityCounter]);
                        entityOther.put("type", "other");
                        entityOther.put("name",(String)entities[entityCounter] );
         				mainArrayList.add(entityOther);
         				
         				
         			}
                     
        	        	 }
        	// reArrangeMainArrayList(mainArrayList);
       // 	 System.out.println(mainArrayList);
     jsonWriter(mainArrayList);
        	 
         }
         catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         

         
	    return "test";
	}
	
 private static BasicDBObject abstractFinder(String entityName) throws Exception{
   	 String outputString=readUrl("http://dbpedia.org/data/"+entityName+".json");
   	 Object ob;
   	 JSONObject job1;
   	 JSONObject job2;
   	 JSONArray jarray;
     BasicDBObject mainValue=new BasicDBObject();
     JSONParser parser = new JSONParser();
   	 Object obj=new Object();
	try {
		obj = parser.parse(outputString);
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}    
        JSONObject jsonObject = (JSONObject) obj;
   	 for(Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
   		    String key = (String) iterator.next();
   		   
   	        ob= jsonObject.get(key);
   		    job1 = (JSONObject) ob;
   		    int indexOfmainArrayList=0;
   		    for(Iterator iterator1 = job1.keySet().iterator(); iterator1.hasNext();){
   		    	 String key1 = (String) iterator1.next();
   		         if(job1.get(key1) instanceof JSONArray)
   		    		 {
   		        	   
   		    			 jarray= (JSONArray)job1.get(key1);
   		    			 String mainKey=null;
   		    			 String demoMainValue;
                           String[] splitKey1=(String[])key1.split("/");
		    			    mainKey=splitKey1[splitKey1.length-1].toString();
                              if(mainKey.equals("abstract") || mainKey.equals("thumbnail")){
		    			        for( int i=0;i<jarray.size(); i++){
   		    			         job2=(JSONObject)(jarray.get(i));
   		    			          if(mainKey.equals("abstract")){
   		    					if(job2.get("lang").equals("en")){
   		    					   String abs=job2.get("value").toString();
		    						   List sentenceList=new ArrayList();
		    						Pattern re = Pattern.compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)", Pattern.MULTILINE | Pattern.COMMENTS);
		    					    Matcher reMatcher = re.matcher(abs);
		    					    while (reMatcher.find()) {
		    					        sentenceList.add(reMatcher.group());
		    					    }
		    					    if(!sentenceList.isEmpty()){
		    					    
		    					    	if(sentenceList.size()>1)
		    			 			mainValue.put("abstract",(String)sentenceList.get(0)+(String)sentenceList.get(1) );
		    					    	
		    					    	else
		    					    		mainValue.put("abstract",(String)sentenceList.get(0));
		    					    }	

   		    					   }
   		    			          }
   		    			       else if (mainKey.equals("thumbnail"))
      		    				{
      		    					
      		    					mainValue.put("thumbnail",job2.get("value").toString());
      		    				}
		    			        }
                              }
	 
 }
   		    }
   	 }
   	 return mainValue;
 }
	
		    			        private static String readUrl(String urlString) throws Exception {
		   		    		        BufferedReader reader = null;
		   		    		        try {
		   		    		            URL url = new URL(urlString);
		   		    		            reader = new BufferedReader(new InputStreamReader(url.openStream()));
		   		    		            StringBuffer buffer = new StringBuffer();
		   		    		            int read;
		   		    		            char[] chars = new char[1024];
		   		    		            while ((read = reader.read(chars)) != -1) {
		   		    		                buffer.append(chars, 0, read); 
		   		    		            }
		   		    		            return buffer.toString();
		   		    		        } finally {
		   		    		            if (reader != null)
		   		    		                reader.close();
		   		    		        }
		   		    		    }
	public String getMyparam() {
		return myparam;
	}
	public void setMyparam(String myparam) {
		this.myparam = myparam;
	}
	
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		 this.request = request; 
		
	}
	


	
}
