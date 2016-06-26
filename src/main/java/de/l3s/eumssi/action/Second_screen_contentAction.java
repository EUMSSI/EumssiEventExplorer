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
/*
public Second_screen_contentAction(MongoDBManager mongoClient){
	mongo=mongoClient;
	personCollection = mongo.getCollection("person");
	 locationCollection = mongo.getCollection("allLocations");
}
*/
	public String contentGenerator(String entityName ) throws Exception {
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
					entity.put("type", "other");
					entity.put("name", entityName);
					}
				
				if(entity!=null){

			//entity variable is a json object which contains all the information that can be shown in second screen.
			
				String type = entity.getString("type");
				if(type.equals("city") || type.equals("country") || type.equals("location")){
					LocationContentGenerator locationObject=new LocationContentGenerator(entity);
					String decision=locationObject.makeDicision();
					if(decision!=null)
					if(decision.equals("question")){
						String question=locationObject.questionGenerator();
						if(question!=null){
							String thumbnail=entity.getString("thumbnail");
							return question;
						}
							
					}
					
					else if(decision.equals("info")){
						String info=locationObject.infoGenerator();
						if(info!=null){
							String thumbnail=entity.getString("thumbnail");
							return info;
						}
							
					}
					else if(decision.equals("abstract")){
						String abs=locationObject.abstractGenerator();
						if(abs!=null){
							String thumbnail=entity.getString("thumbnail");
							return abs;
						}
							
					}
					else if (decision.equals("map")){
						String googleMap=locationObject.mapGenerator(entity.getString("name"));
						String thumbnail=entity.getString("thumbnail");
						return googleMap;
					}
					
					else if (decision.equals("wordGraph")){
						String wordGraph=locationObject.wordGraphGenerator(entity.getString("name"));
						String thumbnail=entity.getString("thumbnail");
						return wordGraph;
					}	
				}
				
				else if(type.equals("person")){
					PersonContentGenerator personObject=new PersonContentGenerator(entity);
					String decision=personObject.makeDicision();
					if(decision!=null)
					if(decision.equals("question")){
						String question=personObject.questionGenerator();
						if(question!=null){
							String thumbnail=entity.getString("thumbnail");
							return question;
						}
							
					}
					
					else if(decision.equals("info")){
						String info=personObject.infoGenerator();
						if(info!=null){
							String thumbnail=entity.getString("thumbnail");
						    return info;
						}
							
					}
					else if(decision.equals("abstract")){
						String abs=personObject.abstractGenerator();
						if(abs!=null){
							String thumbnail=entity.getString("thumbnail");
							return abs;
						}
							
					}
					else if (decision.equals("wordGraph")){
						String wordGraph=personObject.wordGraphGenerator(entity.getString("name"));
						String thumbnail=entity.getString("thumbnail");
						return wordGraph;
					}
					
				}
				 
				else if(type.equals("other")) {
					String otherEntityName = (String) entity.get("name");
					otherEntityName = otherEntityName.replaceAll("[_]", " ");
					String entityAbstract = "<strong>" + otherEntityName + "</strong><br>"
							+ (String) entity.get("abstract");
					entityAbstract = entityAbstract.replaceAll("[\"]", "");
					entityAbstract = entityAbstract.replaceAll("\\(.+?\\)\\s*", "");
					return entityAbstract;
				}
		}
		
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return entityName;
        
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
