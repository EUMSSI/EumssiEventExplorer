/*
 *Composed by Mainul Quraishi
 * 
 */
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
import com.mongodb.DBObject;
import com.opensymphony.xwork2.Action;
import de.l3s.eumssi.dao.MongoDBManager;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.json.simple.parser.ParseException;

public class textsyncAction implements Action, ServletRequestAware{

	MongoDBManager mongo = new MongoDBManager();
	//get the collection of all person
	DBCollection personCollection = mongo.getCollection("person");
	//get the collection of all locations. 
	DBCollection locationCollection = mongo.getCollection("allLocations");

	private String myparam;
	private String videoUrl;
	private String url;
	public String jsonFileName;
	public String entities[];
	public int hour = 00;
	public int min = 00;
	public int sec = 00;
	public String dataContent = null;
	public String content1 = "{\"localisation\": [{ \"sublocalisations\": { \"localisation\": [";
	public String content8 = "]},\"type\": \"text\",\"tcin\": \"00:00:00.0000\",\"tcout\": \"02:00:00.0000\",\"tclevel\": 0}],\"id\": \"text-amalia01\",\"type\": \"text\",\"algorithm\": \"demo-video-generator\",\"processor\": \"Ina Research Department - N. HERVE\", \"processed\": 1421141589288, \"version\": 1}";
	// Hold the List of all the details of all entities extracted from mongoDB
	public ArrayList<BasicDBObject> mainArrayList = new ArrayList<BasicDBObject>();
	public ArrayList<String> thumbNailsList = new ArrayList<String>();
	HttpServletRequest request;

	// function for making data(by inserting image and time) to write in json file for amalia player
	//input is data and the image that should be shown 
		
    /* function responsible for finding which question should be asked or which info should be shown according to defined templates. Inputs
    * is the main ArrayList which holds the all the details of the matched entities. Decisions(whether questions or info or abstract
    * and which question or info) are made randomly.    
    */
	
private String[] getEntitiesFromSolr() throws SolrServerException{
	
	HttpSolrServer solr = new HttpSolrServer("http://demo.eumssi.eu/Solr_EUMSSI/content_items/");
	SolrQuery query = new SolrQuery();
	 System.out.println(videoUrl);
	query.setQuery( "meta.source.mediaurl:"+"\""+videoUrl+"\"");
	query.setFields("meta.extracted.text_nerl.dbpedia.all");
	QueryResponse response = solr.query(query);
    SolrDocumentList results = response.getResults();
    System.out.println(results.size());
    ArrayList entities=null;
    for (int i = 0; i < results.size(); ++i) {
       entities= (ArrayList) results.get(i).getFieldValue("meta.extracted.text_nerl.dbpedia.all");
    }
    Set<String> hs = new HashSet<>();
    hs.addAll(entities);
    entities.clear();
    entities.addAll(hs);
    
    String[] entitiesArray=(String[]) entities.toArray(new String[entities.size()] );
    
 System.out.println(entitiesArray);
 return entitiesArray;
}
	public String execute() throws FileNotFoundException, IOException, ParseException, SolrServerException {
        ArrayList<String> subSubArray;
		ArrayList<ArrayList<String>> subArray;
		
		//entities = myparam.split(",");
		entities=getEntitiesFromSolr();
		AmaliaSidebarContent sidebarContent=new AmaliaSidebarContent();

		for (int entityCounter = 1; entityCounter <= entities.length; entityCounter++) {
			if (jsonFileName == null)
				jsonFileName = entities[entityCounter - 1];
			else
				jsonFileName = jsonFileName + entities[entityCounter - 1];
		}

		JSONParser parser = new JSONParser();
		try {
			for (int entityCounter = 0; entityCounter < entities.length; entityCounter++) {
				BasicDBObject whereQuery = new BasicDBObject();
				whereQuery.put("name", entities[entityCounter]);
				BasicDBObject projectionQuery = new BasicDBObject();
				projectionQuery.put("_id", 0);
				DBCursor locationCursor = locationCollection.find(whereQuery, projectionQuery);
				DBCursor personCursor = personCollection.find(whereQuery, projectionQuery);

				while (locationCursor.hasNext()) {

					BasicDBObject entity = (BasicDBObject) locationCursor.next();


					mainArrayList.add(entity);

				}
				while (personCursor.hasNext()) {

					BasicDBObject entity = (BasicDBObject) personCursor.next();

					mainArrayList.add(entity);
				}
				if (locationCursor.count() == 0 && personCursor.count() == 0) {
					BasicDBObject entityOther = abstractFinder((String) entities[entityCounter]);
					entityOther.put("type", "other");
					entityOther.put("name", (String) entities[entityCounter]);
					mainArrayList.add(entityOther);

				}

			}
			for (int i = 0; i < mainArrayList.size(); i++) {
		           
				BasicDBObject entity = mainArrayList.get(i);
			
				String type = entity.getString("type");
				if(type.equals("city") || type.equals("country") || type.equals("location")){
					LocationContentGenerator locationObject=new LocationContentGenerator(entity);
					String decision=locationObject.makeDicision();
					if(decision==null)
						continue;
					if(decision.equals("question")){
						String question=locationObject.questionGenerator();
						if(question==null)
							continue;
						else{
							String thumbnail=entity.getString("thumbnail");
							sidebarContent.makeData(question, thumbnail);
						}
							
					}
					
					else if(decision.equals("info")){
						String info=locationObject.infoGenerator();
						if(info==null)
							continue;
						else{
							String thumbnail=entity.getString("thumbnail");
							sidebarContent.makeData(info, thumbnail);
						}
							
					}
					else if(decision.equals("abstract")){
						String abs=locationObject.abstractGenerator();
						if(abs==null)
							continue;
						else{
							String thumbnail=entity.getString("thumbnail");
							sidebarContent.makeData(abs, thumbnail);
						}
							
					}
					else if (decision.equals("map")){
						String googleMap=locationObject.mapGenerator(entity.getString("name"));
						String thumbnail=entity.getString("thumbnail");
						sidebarContent.makeData(googleMap, thumbnail);
					}
					
					else if (decision.equals("wordGraph")){
						String wordGraph=locationObject.wordGraphGenerator(entity.getString("name"));
						String thumbnail=entity.getString("thumbnail");
						sidebarContent.makeData(wordGraph, thumbnail);
					}	
				}
				
				else if(type.equals("person")){
					PersonContentGenerator personObject=new PersonContentGenerator(entity);
					String decision=personObject.makeDicision();
					if(decision==null)
						continue;
					if(decision.equals("question")){
						String question=personObject.questionGenerator();
						if(question==null)
							continue;
						else{
							String thumbnail=entity.getString("thumbnail");
							sidebarContent.makeData(question, thumbnail);
						}
							
					}
					
					else if(decision.equals("info")){
						String info=personObject.infoGenerator();
						if(info==null)
							continue;
						else{
							String thumbnail=entity.getString("thumbnail");
							sidebarContent.makeData(info, thumbnail);
						}
							
					}
					else if(decision.equals("abstract")){
						String abs=personObject.abstractGenerator();
						if(abs==null)
							continue;
						else{
							String thumbnail=entity.getString("thumbnail");
							sidebarContent.makeData(abs, thumbnail);
						}
							
					}
					else if (decision.equals("wordGraph")){
						String wordGraph=personObject.wordGraphGenerator(entity.getString("name"));
						String thumbnail=entity.getString("thumbnail");
						sidebarContent.makeData(wordGraph, thumbnail);
					}
					
				}
				 
				else if(type.equals("other")) {
					String otherEntityName = (String) entity.get("name");
					otherEntityName = otherEntityName.replaceAll("[_]", " ");
					String entityAbstract = "<strong>" + otherEntityName + "</strong><br>"
							+ (String) entity.get("abstract");
					entityAbstract = entityAbstract.replaceAll("[\"]", "");
					entityAbstract = entityAbstract.replaceAll("\\(.+?\\)\\s*", "");
					sidebarContent.makeData(entityAbstract,(String) entity.get("thumbnail"));
				}
			}
			sidebarContent.writeContent(jsonFileName,request);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "test";
	}

	// Find the abstract of entity which is not found in mongodb
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
