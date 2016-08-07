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
import java.io.IOException;
import java.io.InputStream;
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
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.opensymphony.xwork2.Action;

public class UpdatePerson implements Action,ServletRequestAware {
	List person=new ArrayList();
	HashSet<String> hs;
	HttpServletRequest request;
	BasicDBObject searchQuery=new BasicDBObject();
	   BasicDBObject updateQuery=new BasicDBObject();
	   BasicDBObject mainUpdateQuery=new BasicDBObject();
	public String execute() throws Exception{
		MongoDBManager mongo=MongoDBManager.getInstance(); 
		BasicDBObject document = new BasicDBObject();
		DBCollection collection=mongo.getCollection("person");
		//DBCollection location1Collection=mongo.getCollection("allLocations");
	
		
		// code from location info action
		BasicDBObject whereQuery = new BasicDBObject();
		BasicDBObject projectionQuery = new BasicDBObject();
		projectionQuery.put("_id", 0);
		projectionQuery.put("name", 1);
        DBCursor nameCursor = collection.find(whereQuery, projectionQuery);
		System.out.println(nameCursor.size());
		// get all the names
		while (nameCursor.hasNext()) {
			BasicDBObject nameObject = (BasicDBObject) nameCursor.next();
			String name  = nameObject.getString("name");
			
			person.add(name);
		}
		
		//make the list unique
		  hs=new HashSet<>(person);
		  System.out.println(hs);
       
	    //iterate over the list  	  
	     Iterator iterator =hs.iterator();
	     while( iterator.hasNext()){
	    	String personName=(String) iterator.next();
	    	UpdatePerson(personName,collection);
	     }
    
      System.out.println("THE END");
		return "success";

}

    private static String readUrl(String urlString, String locationName) throws Exception {
    	
   		    		        BufferedReader reader = null;
   		    		        try {
   		    		            URL url = new URL(urlString);
   		    		            InputStream inputstr=url.openStream();
   		    		         InputStreamReader inputStrRdr= new InputStreamReader(url.openStream());
   		    		       
   		    		            reader = new BufferedReader(inputStrRdr);
   		    		          
   		    		            StringBuffer buffer = new StringBuffer();
   		    		            int read;
   		    		            char[] chars = new char[1024];
   		    		            while ((read = reader.read(chars)) != -1) {
   		    		                buffer.append(chars, 0, read);
   		    		            }
   		    		            return buffer.toString();
   		    		        } 
   		    		        catch(IOException e){
   		    		        	return null;
   		    		        }
   		    		        finally {
   		    		            if (reader != null)
   		    		                reader.close();
   		    		            
   		    		        }
   		    		    }

	@Override
	public void setServletRequest(HttpServletRequest request) {
							this.request = request;

						}


// get location and update in mongo 
private static void UpdatePerson(String personName,DBCollection collection) throws Exception {
    JSONParser parser = new JSONParser();
    BasicDBObject searchQuery=new BasicDBObject();
    searchQuery.put("name", personName);
   BasicDBObject updateQuery=new BasicDBObject();
   BasicDBObject mainUpdateQuery=new BasicDBObject();
    
     String outputString=readUrl("http://dbpedia.org/data/"+personName+".json",personName);
 //    System.out.println(outputString);
 //    System.out.println(locationName);
	 if(outputString!=null){
     Object ob;
	 JSONObject job1;
	 JSONObject job2 = null;
	 JSONArray jarray;
	 Object obj = parser.parse(outputString);
    JSONObject jsonObject = (JSONObject) obj;
    searchQuery.put("name", personName);
    
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
    		 
    			  if(mainKey.equals("birthName")){
    				  job2=(JSONObject)(jarray.get(0));
    				  if(job2.get("type").toString().equals("literal")){
      					mainValue=job2.get("value").toString();
      					if(!personName.equals(mainValue))
      					updateQuery.put(mainKey,mainValue);
      				  }
    				  
    			  }
    			  else if(mainKey.equals("deathDate")){
    				  job2=(JSONObject)(jarray.get(0));
    				  if(job2.get("type").toString().equals("literal")){
    					mainValue=job2.get("value").toString();
    					updateQuery.put(mainKey,mainValue);
    				  }
    				  
    			  }
    			  else if(mainKey.equals("deathPlace")){
    				  job2=(JSONObject)(jarray.get(0));
    				  if(job2.get("type").toString().equals("literal")){
    					mainValue=job2.get("value").toString();
    					updateQuery.put(mainKey,mainValue);
    				  }
    				  
    			  }
    			  
    			  
	    		 }
	         }
          }
               if(!updateQuery.isEmpty()){
               mainUpdateQuery.put("$set", updateQuery); 
			   collection.update(searchQuery,mainUpdateQuery);
               } 

}
}
}