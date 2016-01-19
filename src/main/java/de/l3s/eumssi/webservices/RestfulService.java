package de.l3s.eumssi.webservices;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


import de.l3s.eumssi.dao.SolrDBManager;
import de.l3s.eumssi.model.*;

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
		System.out.println("here");
		SolrDBManager db = new SolrDBManager();
		List<Event> events = new ArrayList<Event> ();
		try{
			events = db.getImportantEvents(n, solrFormatedQuery);
		}catch(Exception e){
			e.printStackTrace();	
		}
		
		return events;
	}
	
	
	

	
	
	
	
	/**
	 * 
	 * @param solrformatedQuery
	 * @param n: number of items to return
	 * @param type: entity keyword
	 * @param language: filter by en, de, es, fr language
	 * @param query: solr based query
	 * @return json style of [{item, frequency}]
	 
	 */
	@GET
	@Path("/storyTelling/json/{n}/{entity_a}/{entity_b}/{language}")
	@Produces(MediaType.APPLICATION_JSON)
	public String storyTelling( 
			@PathParam("n") int n,
			@PathParam("entity_a") String entity_a,
			@PathParam("entity_b") String entity_b,
			@PathParam("language") String language) {
	
		//n: so luong top words to display
		SolrDBManager db = new SolrDBManager();
		JSONArray coocc = new JSONArray();
				
		try{
			coocc = db.getStoryTellingGraph(entity_a, entity_b, n, language);
		}catch(Exception e){
			e.printStackTrace();	
		}
				
		return coocc.toString();
	}
	
	
	
	private ArrayList<String> formSearchField(String fields) {
		ArrayList<String> searchFields = new ArrayList<String> ();
		if (fields.contains("headline")) searchFields.add("meta.source.headline");
		if (fields.contains("text"))searchFields.add("meta.source.text");
		if (fields.contains("transcript")) searchFields.add("meta.extracted.audio_transcript");
		return searchFields;
	}
}