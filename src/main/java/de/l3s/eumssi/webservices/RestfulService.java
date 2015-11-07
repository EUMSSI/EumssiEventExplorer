package de.l3s.eumssi.webservices;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import de.l3s.eumssi.core.StoryDistribution;
import de.l3s.eumssi.dao.DatabaseManager;
import de.l3s.eumssi.dao.SolrDBManager;
import de.l3s.eumssi.model.*;
import de.l3s.eumssi.service.ContentHandling;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;

@Path("/API/")
public class RestfulService {
	
	@GET
	@Path("/getNews/json/{fields}/{sources}/{keyword}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getNews(@PathParam("keyword") String keyword, 
			@PathParam("fields") String fields, @PathParam("sources") String sources) {
		SolrDBManager db = new SolrDBManager();
		List<Event> events = new ArrayList<Event> ();
		ArrayList<String> searchField = formSearchField(fields);						//search field
		//Debug
		ArrayList<String> S = new ArrayList<String> ();
		if (sources.contains("video")) S.add("Video");
		if (sources.contains("news")) S.add("NewsArticle");
		try{
			int maxNumOfEventsToDisplay = Integer.parseInt(db.conf.getProperty("visualization_MaxTimelineSize"));
			events = db.searchByKeyword(keyword, S, searchField, maxNumOfEventsToDisplay);
		}catch(Exception e){
			e.printStackTrace();	
		}
		return events;
			
	}
	
	
	@GET
	@Path("/getImportantEvents/json/{n}/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getImportantEvents(@PathParam("query") String solrFormatedQuery,
			@PathParam("n") int n) { //solr formated query (q=...) and the number of events to be returned
		SolrDBManager db = new SolrDBManager();
		List<Event> events = new ArrayList<Event> ();
		try{
			events = db.searchBySolrQuery(n, solrFormatedQuery);
		}catch(Exception e){
			e.printStackTrace();	
		}
		
		return events;
	}
	
	@GET
	@Path("/getWikipediaEvents/json/{fromDate}/{toDate}/{keyword}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getWikipediaEvents(@PathParam("keyword") String keyword,
			@PathParam("fromDate") String fromDate, @PathParam("toDate") String toDate) { //solr formated query (q=...) and the number of events to be returned
		DatabaseManager db = new DatabaseManager(); //wcep events
		List<Event> events = new ArrayList<Event> ();
		try{
			if(keyword == null){
				events = db.getEvents(fromDate, toDate);
			}else if (keyword.isEmpty()){
				events = db.getEvents(fromDate, toDate);
			}else{
				events = db.searchEventsByKeyword(keyword, fromDate, toDate);
			}
		}catch(Exception e){
			e.printStackTrace();	
		}
		return events;
	}
	
	@GET
	@Path("/getWikipediaEventsByStory/json/{wikipediaUrl}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getWikipediaStory(@PathParam("wikipediaUrl") String wikipediaUrl) {
		DatabaseManager db = new DatabaseManager(); //wcep events  e.g story http://en.wikipedia.org/wiki/2003_invasion_of_Iraq
		Story story = null;
		List<Event> events = new ArrayList<Event>();
		try{
			if(wikipediaUrl == null){
				return events;
			}else if (wikipediaUrl.isEmpty()){
				return events;
			}else{
				story = db.getStoryByURL(wikipediaUrl);
				events = db.getEventsByStory(story.getId());
			}
		}catch(Exception e){
			e.printStackTrace();	
		}
		return events;
	}
	
	
	@GET
	@Path("/getWikipediaStoriesByCategory/json/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Story> getWikipediaStoriesByCategory(@PathParam("category") String category) {
		DatabaseManager db = new DatabaseManager(); //wcep events  e.g story http://en.wikipedia.org/wiki/2003_invasion_of_Iraq
		List<Story> stories = new ArrayList<Story>();
		try{
			if(category == null){
				return stories;
			}else if (category.isEmpty()){
				return stories;
			}else{
				stories =  db.getStoryByCategory(category);
			}
		}catch(Exception e){
			e.printStackTrace();	
		}
		return stories;
	}

	@GET
	@Path("/getWikipediaEventsByEntity/json/{wikipediaName}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> getWikipediaEventsByEntity(@PathParam("wikipediaName") String wikipediaName) {
		DatabaseManager db = new DatabaseManager(); //Wikipedia Name like "Warsaw",
		Story story = null;
		List<Event> events = new ArrayList<Event>();
		try{
			if(wikipediaName == null){
				return events;
			}else if (wikipediaName.isEmpty()){
				return events;
			}else{
				//events = db.getEventsByStory(story.getId());
				events = db.getEventsByEntityName(wikipediaName, "2000-01-01", "2015-12-31");
			}
		}catch(Exception e){
			e.printStackTrace();	
		}
		return events;
	}

	
	
	@GET
	@Path("/getGraphX/json/{fields}/{sources}/{keyword}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCooccurences(@PathParam("keyword") String keyword, 
			@PathParam("fields") String fields, @PathParam("sources") String sources) {
		SolrDBManager db = new SolrDBManager();
		JSONArray coocc = null;
		ArrayList<String> searchField = formSearchField(fields);	
		ArrayList<String> S = new ArrayList<String> ();
		if (sources.contains("video")) S.add("Video");
		if (sources.contains("news")) S.add("NewsArticle");
		
		try{
			
			int maxNumOfWordsToDisplay = Integer.parseInt(db.conf.getProperty("visualization_MaxWordCloudSize"));
			int maxNumOfEvents = Integer.parseInt(db.conf.getProperty("visualization_MaxDocForClouds"));
			
			StoryDistribution distr = db.getDistribution(keyword, S, searchField, maxNumOfEvents);
			coocc = distr.getCoOccurenceOfTopTerms(maxNumOfWordsToDisplay);
			
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			
		}
		return coocc.toString();
	}
	
	
	
	@GET
	@Path("/getGraph/json/{n}/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getGraph(@PathParam("query") String solrformatedQuery, @PathParam("n") int n) {
		//n: so luong top words to display
		SolrDBManager db = new SolrDBManager();
		JSONArray coocc = null;
		ArrayList<String> S = new ArrayList<String> ();
		
		try{
			StoryDistribution distr = db.getDistribution(solrformatedQuery);
			coocc = distr.getCoOccurenceOfTopTerms(n);
			
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			
		}
		return coocc.toString();
	}
	
	@GET
	@Path("/getWordCloud/json/{n}/{query}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getWordCloud(@PathParam("query") String solrformatedQuery, 
			@PathParam("n") int n) {
		SolrDBManager db = new SolrDBManager();
		JSONArray tfjson = null;
		
		try{
			
			StoryDistribution distr = db.getDistribution(solrformatedQuery);
			tfjson =  distr.getTermFrequencies(n);
			
			System.out.println("Finish generating wordcloud");
			
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			
		}
		return tfjson.toString();
	}
	
	
	/**
	 * 
	 * @param solrformatedQuery
	 * @param n: number of items to return
	 * @param type: entity or keyword
	 * @param language: filter by en, de, es, fr language
	 * @return json style of [{item, frequency}]
	 */
	@GET
	@Path("/getSemanticCloud/json/{n}/{type}/{query}/{language}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getWordCloud(@PathParam("query") String solrformatedQuery, 
			@PathParam("n") int n, @PathParam("type") String type, @PathParam("language") String language) {
	
		SolrDBManager db = new SolrDBManager();
		JSONArray tfjson = null;
		
		try{
			
			HashMap<String, Integer> distr = db.getSemanticDistribution(solrformatedQuery, type, language);
			tfjson =  StoryDistribution.getTermFrequencies(distr, n);
			
			System.out.println("Finish generating cloud");
			
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			
		}
		return tfjson.toString();
	}
	
	@GET
	@Path("/getWordCloud/json/{fields}/{sources}/{keyword}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getWordCloud(@PathParam("keyword") String keyword, 
			@PathParam("fields") String fields, @PathParam("sources") String sources) {
		SolrDBManager db = new SolrDBManager();
		JSONArray tfjson = null;
		ArrayList<String> searchField = formSearchField(fields);	
		ArrayList<String> S = new ArrayList<String> ();
		if (sources.contains("video")) S.add("Video");
		if (sources.contains("news")) S.add("NewsArticle");
		
		try{
			
			int maxNumOfWordsToDisplay = Integer.parseInt(db.conf.getProperty("visualization_MaxWordCloudSize"));
			int maxNumOfEvents = Integer.parseInt(db.conf.getProperty("visualization_MaxDocForClouds"));
			
			StoryDistribution distr = db.getDistribution(keyword, S, searchField, maxNumOfEvents);
			tfjson =  distr.getTermFrequencies(maxNumOfWordsToDisplay);
			
			System.out.println("Finish generating wordcloud");
			
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			
		}
		return tfjson.toString();
	}
	
	
	
	
	
	private ArrayList<String> formSearchField(String fields) {
		ArrayList<String> searchFields = new ArrayList<String> ();
		if (fields.contains("headline")) searchFields.add("meta.source.headline");
		if (fields.contains("text"))searchFields.add("meta.source.text");
		if (fields.contains("transcript")) searchFields.add("meta.extracted.audio_transcript");
		return searchFields;
	}
}