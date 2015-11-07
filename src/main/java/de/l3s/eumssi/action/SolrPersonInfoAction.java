package de.l3s.eumssi.action;
import de.l3s.eumssi.dao.MongoDBManager;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.opensymphony.xwork2.Action;

public class SolrPersonInfoAction implements Action,ServletRequestAware {
	List person=new ArrayList();
	HashSet<String> hs;
	HttpServletRequest request;
	public String execute() throws Exception{
		MongoDBManager mongo=new MongoDBManager(); 
		BasicDBObject document = new BasicDBObject();
		DBCollection collection=mongo.getCollection("person");
		ServletContext context = request.getServletContext();
		String path = context.getRealPath("/");
		HttpSolrServer solr = new HttpSolrServer("http://eumssi.cloudapp.net/Solr_EUMSSI/content_items/");
		SolrQuery query = new SolrQuery();
		query.setQuery("source:\"DW video\"");
		query.addFilterQuery("meta.extracted.text.dbpedia.PERSON:*");
		query.setFields("meta.extracted.text.dbpedia.PERSON");
		query.setStart(0);
		query.setRows(8065);
		QueryResponse response = solr.query(query);
	    SolrDocumentList results = response.getResults();
	    System.out.println(results.size());
	    for (int i = 0; i < results.size(); ++i) {
	   ArrayList tempPerson= (ArrayList) results.get(i).getFieldValue("meta.extracted.text.dbpedia.PERSON");
	      for(int j=0;j<tempPerson.size();j++){
	    
	    	  person.add(tempPerson.get(j));
	      }
	    }
	  
	    hs=new HashSet<>(person);
	    System.out.println(hs);
	    
	    Iterator it =hs.iterator();
	    		
	    JSONParser parser = new JSONParser();
	    
	    JSONObject outerJsonObject=new JSONObject();
	    while(it.hasNext()){
	    	JSONObject innerJsonObject=new JSONObject();	
	    String personName=(String) it.next(); 	
   	 String outputString=readUrl("http://dbpedia.org/data/"+personName+".json");
   	 Object ob;
   	 JSONObject job1;
   	 JSONObject job2;
   	 JSONArray jarray;
   	 Object obj = parser.parse(outputString);    
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
   		        	     String mainValue=null;
   		    			 jarray= (JSONArray)job1.get(key1);
   		    			 String mainKey=null;
   		    			 String demoMainValue;
                           String[] splitKey1=(String[])key1.split("/");
		    			    mainKey=splitKey1[splitKey1.length-1].toString();
                              if(mainKey.equals("abstract") || mainKey.equals("birthPlace") || mainKey.equals("birthDate") || mainKey.equals("almaMater") || mainKey.equals("spouse") || mainKey.equals("thumbnail")){
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
   		    			 			mainValue=(String)sentenceList.get(0)+(String)sentenceList.get(1);
   		    					    	else
   		    					    		mainValue=(String)sentenceList.get(0);
   		    					    }
   		    					   }
   		    				    }
   		    				else if (mainKey.equals("birthPlace"))
   		    				{
   		    					if(mainValue==null){
   		    						String tempValue=job2.get("value").toString();
   		    					    String[] splitKey2=(String[])tempValue.split("/");
   			    			        mainValue=splitKey2[splitKey2.length-1].toString();
   			    			        mainValue=mainValue.replaceAll("[_]", " ");
   		    					
   		    					}
   		    					else{
   		    						String tempValue=job2.get("value").toString();
   		    					    String[] splitKey2=(String[])tempValue.split("/");
   			    			        mainValue=mainValue+","+splitKey2[splitKey2.length-1].toString();
   			    			        mainValue=mainValue.replaceAll("[_]", " ");
   		    					}
   		    					
   		    				}
   		    				else if (mainKey.equals("birthDate"))
   		    				{
   		    					
   		    					mainValue=job2.get("value").toString();
   		    				}
   		    				else if (mainKey.equals("almaMater"))
   		    				{
   		    					if(mainValue==null){ 
   		    						String tempValue=job2.get("value").toString();
		    					    String[] splitKey2=(String[])tempValue.split("/");
			    			        mainValue=splitKey2[splitKey2.length-1].toString();
			    			        mainValue=mainValue.replaceAll("[_]", " ");
   		    					}
   		    					else{
   		    						String tempValue=job2.get("value").toString();
   		    					    String[] splitKey2=(String[])tempValue.split("/");
   			    			        mainValue=mainValue+","+splitKey2[splitKey2.length-1].toString();
   			    			        mainValue=mainValue.replaceAll("[_]", " ");
   		    					}
   		    				}
   		    				else if (mainKey.equals("spouse"))
   		    				{
   		    					if(mainValue==null){ 
   		    						String tempValue=job2.get("value").toString();
		    					    String[] splitKey2=(String[])tempValue.split("/");
			    			        mainValue=splitKey2[splitKey2.length-1].toString();
			    			        if(mainValue.equals(personName)){
   			    			        	String[] splitKey3=(String[])key.split("/");
   	   			    			        mainValue=splitKey3[splitKey3.length-1].toString();
   	   			    			        mainValue=mainValue.replaceAll("[_]", " ");
   			    			        }
			    			       
			    			        	mainValue=mainValue.replaceAll("[_]", " ");
   		    					}
   		    					else{
   		    						String tempValue=job2.get("value").toString();
		    					    String[] splitKey2=(String[])tempValue.split("/");
			    			        mainValue=mainValue+","+splitKey2[splitKey2.length-1].toString();
			    			        if(mainValue.equals(personName)){
   			    			        	String[] splitKey3=(String[])key.split("/");
   	   			    			        mainValue=splitKey3[splitKey3.length-1].toString();	
   			    			        }
			    			        mainValue=mainValue.replaceAll("[_]", " ");
   		    					}
   		    				}
   		    				else if (mainKey.equals("thumbnail"))
   		    				{
   		    					
   		    					mainValue=job2.get("value").toString();
   		    				}
   		    			}
		    			        innerJsonObject.put(mainKey, mainValue);
		    			       // document.put(mainKey, mainValue);
   		    		 }
		    			    
   		    		 }
   		    }
   		    
   	 }
     //System.out.println(innerJsonObject);
   	// outerJsonObject.put(personName, innerJsonObject);
   	innerJsonObject.put("name", personName);
   	innerJsonObject.put("type", "person");
    collection.insert(new BasicDBObject(innerJsonObject));
   	 //document.put("name", personName);
	    }
	   /*
	    System.out.println(outerJsonObject);
		FileWriter file = new FileWriter(path+"scripts\\personinfo.json");
		System.out.println(path);
			file.write(outerJsonObject.toJSONString());
		
		
		file.flush();
		file.close();
		*/
	  System.out.println("THE END");
		return "success";
		
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

						@Override
						public void setServletRequest(HttpServletRequest request) {
							this.request = request;
							
						}

}
