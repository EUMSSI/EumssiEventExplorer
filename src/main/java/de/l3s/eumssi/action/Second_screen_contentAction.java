package de.l3s.eumssi.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import de.l3s.eumssi.dao.MongoDBManager;

public class Second_screen_contentAction{
 private String entityName;
 private String videoName;
 private String headLine;
 
 HttpServletRequest request;
 public MongoDBManager mongo= MongoDBManager.getInstance() ;
	//get the collection of all person
DBCollection personCollection=mongo.getCollection("person");
	//get the collection of all locations. 
DBCollection locationCollection=mongo.getCollection("allLocations");
JSONObject content=new JSONObject();
/*
public Second_screen_contentAction(MongoDBManager mongoClient){
	mongo=mongoClient;
	personCollection = mongo.getCollection("person");
	 locationCollection = mongo.getCollection("allLocations");
}
*/
	public JSONObject contentGenerator(String entityName ) throws Exception {
		JSONObject content=new JSONObject();
		System.out.println(entityName);
		
    /*
        
        ServletContext context = request.getServletContext();
    	HttpSession session = request.getSession(false);
    	String userId=(String) session.getAttribute("userId");
       
        
		String path = context.getRealPath("/");
	
       headLine=headLine.replaceAll(" ", "_");
	*/	
		
		//use the entity names to make content
		try {
			
			
		       
				BasicDBObject whereQuery = new BasicDBObject();
				whereQuery.put("name", entityName);
				BasicDBObject projectionQuery = new BasicDBObject();
				projectionQuery.put("_id", 0);
				DBCursor locationCursor = locationCollection.find(whereQuery, projectionQuery);
				DBCursor personCursor = personCollection.find(whereQuery, projectionQuery);
				BasicDBObject entity=null ;
				while (locationCursor.hasNext()) {
                      entity = (BasicDBObject) locationCursor.next();
                 }
				locationCursor.close();
				while (personCursor.hasNext()) {
					entity = (BasicDBObject) personCursor.next();
                 }
				 personCursor.close();
				 
				 
				if (locationCursor.count() == 0 && personCursor.count() == 0) {
					
					entity = abstractFinder(entityName);
					if(entity!=null){
					entity.put("type", "other");
					entity.put("name", entityName);
					}
					}
				
				if(entity!=null){

			//entity variable is a json object which contains all the information that can be shown in second screen.
			
				String type = entity.getString("type");
				content.put("name",entity.getString("name") );
				String thumbnail=entity.getString("thumbnail");
				content.put("thumbnail", thumbnail);
				if(type.equals("city") || type.equals("country") || type.equals("location")){
					LocationContentGenerator locationObject=new LocationContentGenerator(entity);
					ArrayList decisionList=locationObject.makeDicision();
					System.out.println("Decision: "+decisionList);
					if(decisionList!=null){
					for(int i=0;i<decisionList.size();i++){
					if(decisionList.get(i).equals("question")){
						JSONArray questions=locationObject.questionGenerator();
						if(questions!=null){
						
							content.put("questions", questions);
						}
							
					}
					
					else if(decisionList.get(i).equals("info")){
						JSONArray infos=locationObject.infoGenerator();
						if(infos!=null){
							content.put("infos", infos);	
						}
							
					}
					else if(decisionList.get(i).equals("abstract")){
						String abs=locationObject.abstractGenerator();
						if(abs!=null){
							
							content.put("abstract", abs);
						}
							
					}
					else if (decisionList.get(i).equals("map")){
						String googleMap=locationObject.mapGenerator(entity.getString("name"));
					
						content.put("map", googleMap);
					}
					
					else if (decisionList.get(i).equals("wordGraph")){
						String wordGraph=locationObject.wordGraphGenerator(entity.getString("name"));
						content.put("wordGraph", wordGraph);
						 
					}
				 }
			   }
					
			}
				
				else if(type.equals("person")){
					PersonContentGenerator personObject=new PersonContentGenerator(entity);
					ArrayList<String> decisionList=personObject.makeDicision();
					if(decisionList!=null)
					for(int i=0;i<decisionList.size();i++){	
					if(decisionList.get(i).equals("question")){
						JSONArray questions=personObject.questionGenerator();
						if(questions!=null){
							content.put("questions", questions);
						}
							
					}
					
					else if(decisionList.get(i).equals("info")){
						JSONArray infos=personObject.infoGenerator();
						if(infos!=null){
							content.put("infos", infos);
						}
							
					}
					else if(decisionList.get(i).equals("abstract")){
						String abs=personObject.abstractGenerator();
						if(abs!=null){
						
							content.put("abstract", abs);
						}
							
					}
					else if (decisionList.get(i).equals("wordGraph")){
						String wordGraph=personObject.wordGraphGenerator(entity.getString("name"));
						
						content.put("wordGraph", wordGraph);
					}
				}
				}
				 
			else if(type.equals("other")) {
				
					String otherEntityName = (String) entity.get("name");
					otherEntityName = otherEntityName.replaceAll("[_]", " ");
					String entityAbstract =(String) entity.get("abstract");
					try{
					entityAbstract = entityAbstract.replaceAll("[\"]", "");
					entityAbstract = entityAbstract.replaceAll("\\(.+?\\)\\s*", "");
					content.put("abstract",entityAbstract );
					}
					catch(NullPointerException e){
						
					}
					
				  
					
				}
				content=DefaultFinder(content);
		}
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		
		return content;
        
    }
	
//select the default content	
	private  JSONObject DefaultFinder(JSONObject content){
		ArrayList<String>decisionList=new ArrayList<>();
		JSONObject defaultContent=new JSONObject();
		Random ran=new Random();
		for (Iterator iterator = content.keySet().iterator(); iterator.hasNext();){
		 String contentType=(String) iterator.next();
			if(!contentType.equals("name") && !contentType.equals("thumbnail")){
			decisionList.add(contentType);
		}
	  }
		String contentDecision=null;
		try{
		 contentDecision=decisionList.get(ran.nextInt(decisionList.size()));
		}
		catch(IllegalArgumentException e){
			System.out.println(decisionList.size());
		}

		defaultContent.put("type",contentDecision );
		if(contentDecision.equals("questions")){
			JSONArray questions= (JSONArray) content.get("questions");
			int questionNumber=0;
			try{
			questionNumber=ran.nextInt(questions.size());
			}
			catch(NullPointerException e){
				System.out.println(questions.size());
			}
			defaultContent.put("number",String.valueOf(questionNumber) );
		}
		if(contentDecision.equals("infos")){
			JSONArray infos= (JSONArray) content.get("infos");
			int infoNumber=ran.nextInt(infos.size());
			defaultContent.put("number",String.valueOf(infoNumber) );
		}
		
	   content.put("default_content", defaultContent);
		
		return content;
	}
	
	private static BasicDBObject abstractFinder(String entityName) throws Exception {
		String outputString = readUrl("http://dbpedia.org/data/" + entityName + ".json");
		Object ob;
		JSONObject job1;
		JSONObject job2;
		JSONArray jarray;
		BasicDBObject mainValue = new BasicDBObject();
		JSONParser parser = new JSONParser();
		Object obj = new Object();
		try {
			obj = parser.parse(outputString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JSONObject jsonObject = (JSONObject) obj;
		for (Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();

			ob = jsonObject.get(key);
			job1 = (JSONObject) ob;
			int indexOfmainArrayList = 0;
			for (Iterator iterator1 = job1.keySet().iterator(); iterator1.hasNext();) {
				String key1 = (String) iterator1.next();
				if (job1.get(key1) instanceof JSONArray) {

					jarray = (JSONArray) job1.get(key1);
					String mainKey = null;
					String demoMainValue;
					String[] splitKey1 = (String[]) key1.split("/");
					mainKey = splitKey1[splitKey1.length - 1].toString();
					if (mainKey.equals("abstract") || mainKey.equals("thumbnail")) {
						for (int i = 0; i < jarray.size(); i++) {
							job2 = (JSONObject) (jarray.get(i));
							if (mainKey.equals("abstract")) {
								if (job2.get("lang").equals("en")) {
									String abs = job2.get("value").toString();
									List sentenceList = new ArrayList();
									Pattern re = Pattern.compile(
											"[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[.!?]?['\"]?(?=\\s|$)",
											Pattern.MULTILINE | Pattern.COMMENTS);
									Matcher reMatcher = re.matcher(abs);
									while (reMatcher.find()) {
										sentenceList.add(reMatcher.group());
									}
									if (!sentenceList.isEmpty()) {

										if (sentenceList.size() > 1)
											mainValue.put("abstract",
													(String) sentenceList.get(0) + (String) sentenceList.get(1));

										else
											mainValue.put("abstract", (String) sentenceList.get(0));
									}

								}
							} else if (mainKey.equals("thumbnail")) {

								mainValue.put("thumbnail", job2.get("value").toString());
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

	
	

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}
	
	public String getHeadLine() {
		return headLine;
	}

	public void setHeadLine(String headLine) {
		this.headLine = headLine;
	}

	
}
