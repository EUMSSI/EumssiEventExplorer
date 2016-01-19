/**
 * db manager for mongodb
 *
 * gtran@l3s.de
 * Nov 28 2014
 */
package de.l3s.eumssi.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.struts2.ServletActionContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import de.l3s.eumssi.core.Stopwords;

import de.l3s.eumssi.core.sortingMap;
import de.l3s.eumssi.model.Entity;
import de.l3s.eumssi.model.Event;
import de.l3s.eumssi.model.Reference;
import de.l3s.lemma.lemma;


class Gram {
	String u, v;
	int frq;
	public Gram (String word1, String word2, int count) {
		u = word1;
		v = word2;
		frq = count;
	}

	public String getWord1() { return u;}
	public String getWord2(){ return v;}
}
public class SolrDBManager {
	HttpSolrServer solr;
	public Properties conf;
	public SolrDBManager() {

		//lemma.init();

		try {
			loadConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
		}
		solr = new HttpSolrServer("http://demo.eumssi.eu/Solr_EUMSSI/content_items/");
	}


	public void loadConfiguration() throws FileNotFoundException, IOException{
		conf = new Properties();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		conf.load(classLoader.getResourceAsStream("DBHandler.properties"));
	}


	 public JSONObject toJSONObject(Event event, Object contextPath) {
			JSONObject eventObject = null;
			try {
				eventObject = new JSONObject();
				eventObject.put("startDate", new SimpleDateFormat("yyyy,MM,dd").format(event.getDate()));
				eventObject.put("endDate", new SimpleDateFormat("yyyy,MM,dd").format(event.getDate()));
				eventObject.put("headline", event.getHeadline());
				String text = "";
				
				eventObject.put("text", text);
//				if(event.getReferences()!=null){
//					if(!event.getReferences().isEmpty()){
//						JSONObject asset = new JSONObject();
//						asset.put("media", event.getReferences().get(0).getUrl());
//						asset.put("caption", event.getReferences().get(0).getSource());
//						eventO.put("asset", asset);
//					}
//				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return eventObject;
		}

	public JSONObject getTimelineJSON(List<Event> events, Object contextPath){

    	JSONObject timeline = null;

    	List<JSONObject> eventsJSObjects = new ArrayList<JSONObject>();
    	for (Event event: events){
    		eventsJSObjects.add(toJSONObject(event, contextPath));
    	}

		try {
			JSONObject content = new JSONObject();
			content.put("headline:", "The Main Timeline Headline");
			content.put("type", "default");
			content.put("text", "<p>Intro body text goes here, some HTML is ok</p>\\");
			content.put("date", new JSONArray(eventsJSObjects));
			timeline = new JSONObject();
			timeline.put("timeline", content);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		return timeline;
    }

	public void test() throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setQuery("source:\"Youtube-video-GeneralChannel\"");

		QueryResponse response = solr.query(query);
	    SolrDocumentList results = response.getResults();
	    System.out.println(results.size());
	    for (int i = 0; i < 10; ++i) {
	      System.out.println(results.get(i).getFieldValue("meta.source.rtspHigh"));
	    }
	}

	public ArrayList<String> getField(String source, String field) {
		ArrayList<String> array_results = new ArrayList<String> ();
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		query.addFilterQuery("source:" + source);
		query.setFields(field);
		query.setRows(20);
		System.out.println(query.toString());
		QueryResponse response;
		try {
			response = solr.query(query);
			 SolrDocumentList results = response.getResults();
		    System.out.println(results.size());
		    for (int i = 0; i < results.size(); ++i) {
		      array_results.add(results.get(i).getFieldValue(field).toString());
		    }
		} catch (SolrServerException e) {
			e.printStackTrace();
		}

	    return array_results;
	}

	public String formulateQueryMultipleFields(ArrayList<String> searchfields, String keyword) {
		ArrayList<String> qstr = new ArrayList<String> ();
		for (String s: searchfields) qstr.add(String.format("%s:\"*%s*\"\t", s, keyword));
		String[] tmp = (String[]) qstr.toArray(new String[qstr.size()]);
		String q = StringUtils.join(tmp, " OR ");
		return q;
	}

	private String formulateQuerySimple(ArrayList<String> sources) {
		ArrayList<String> qfilter = new ArrayList<String> ();
		for (String s: sources) {
			if (s.equals("NewsArticle")) {
				qfilter.add("\"News\"");
				qfilter.add("\"DW article\"");
			}
			if (s.equals("Video")) {
				qfilter.add("\"DW video\"");
				qfilter.add("\"Youtube\"");
				qfilter.add("\"DW (Youtube)\"");
				qfilter.add("\"Guardian (Youtube)\"");
			}
		}
		String[] tmp = (String[]) qfilter.toArray(new String[qfilter.size()]);
		String strqfilter = StringUtils.join(tmp, " OR ");
		System.out.println("Sources for filter: " + strqfilter);
		return "(" + strqfilter + ")";
	}
	/*
	 * get most recent n items
	 */
	public List<Event> searchByKeyword(String keyword, ArrayList<String> sources, ArrayList<String> searchfields, int n_items) {
		HashSet<String> selectedTitles = new HashSet<String> ();
		ArrayList<Event> itemList = new ArrayList<Event> ();
		SolrQuery query = new SolrQuery();
		String source = formulateQuerySimple(sources);

		if (keyword==null || keyword.equals("")) {
			for (String searchField: searchfields) {
				query.setQuery(String.format("%s:*\t", searchField));
			}
			System.out.println("debug: null/empty query");
		}
		else {
			String queryString = formulateQueryMultipleFields(searchfields, keyword);
			query.setQuery(queryString);
		}
		query.addFilterQuery("source:" + source);
		//query.addFilterQuery("meta.source.inLanguage:\"en\"");
		//--------------------------------------------------------------------
		query.setFields("meta.source.datePublished", "meta.source.headline", "meta.source.url", "meta.source.httpHigh",
				"meta.source.publisher");
		for (String searchField: searchfields) {query.addField(searchField);}


		//query.addFilterQuery("meta.source.inLanguage:\"en\"");
		query.setSort("meta.source.datePublished", ORDER.desc);
		query.setRows(n_items);
		System.out.println("SearchByKeyword " + query.toString());
		QueryResponse response;

		try {
			response = solr.query(query);
			 SolrDocumentList results = response.getResults();
		    for (int i = 0; i < results.size(); ++i) {
		    	StringBuffer sb = new StringBuffer();
		    	for (String searchField: searchfields) {
		    		Object fieldVal = results.get(i).getFieldValue(searchField);
		    		if (fieldVal!=null) {
			    		sb.append(fieldVal.toString());
			    		sb.append("\n");
		    		}
		    	}
		    	String fieldText = sb.toString();
		    	String headline = results.get(i).getFieldValue("meta.source.headline").toString();
		    	headline = clean(headline);
		    	String url = null;
		    	Object uObj = results.get(i).getFieldValue("meta.source.url");
		    	if (uObj==null) uObj = results.get(i).getFieldValue("meta.source.httpHigh");

		    	if (uObj!= null) {
		    		url  = uObj.toString();
		    	}
		    	String publisher = "";
		    	Object pObj = results.get(i).getFieldValue("meta.source.publisher");
		    	if (pObj!=null)
		    		publisher = pObj.toString();
		    	else {
		    		publisher = "Deutsche Welle";
		    	}

		    	Reference ref =  null;
		    	if (url != null) {
		    		ref = new Reference(url, url, publisher);
		    	}

		    	Date date = (Date) results.get(i).getFieldValue("meta.source.datePublished");
		    	java.sql.Date sqldate = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());


		    	Event e = new Event();
		    	e.setDescription(fieldText);
		    	e.setDate(sqldate);
		    	e.setHeadline(headline);
		    	if (ref!=null) e.addReference(ref);
		    	if (e.getDate().toString().compareTo("2050")<0) { //ensure there is not a date mistake when adding events to show
		    		if (!selectedTitles.contains(headline)) {
		    			itemList.add(e);
		    			selectedTitles.add(headline);
		    		}
		    	}
		    }
		} catch (SolrServerException ex) {
			ex.printStackTrace();
		}
	    return itemList;
	}



	/*
	 * get most recent n items
	 */
	public List<Event> videoSearch(String keyword, ArrayList<String> sources, ArrayList<String> searchfields, int n_items) {
		System.out.println("DEBUG: in video search");
		HashSet<String> selectedTitles = new HashSet<String> ();
		ArrayList<Event> itemList = new ArrayList<Event> ();
		SolrQuery query = new SolrQuery();
		//String source = formulateQuerySimple(sources);
		String source = "\"DW video\"";
		if (keyword==null || keyword.equals("")) {
			for (String searchField: searchfields) {
				query.setQuery(String.format("%s:*\t", searchField));
			}
			System.out.println("debug: null/empty query");
		}
		/*
		else {
			String queryString = formulateQueryMultipleFields(searchfields, keyword);
			query.setQuery(queryString);
		}
		*/
		query.setQuery("source:" + source);
		query.addFilterQuery("meta.source.inLanguage:\"en\"");
		query.addFilterQuery("contentSearch:*" + keyword + "*");
		query.addFilterQuery("meta.extracted.text_nerl.dbpedia.all:*");
		
		//--------------------------------------------------------------------

		query.setFields("meta.source.datePublished", "meta.source.headline", "meta.source.url", "meta.source.mediaurl",
				"meta.source.publisher", "meta.extracted.text_nerl.dbpedia.all");
		for (String searchField: searchfields) {query.addField(searchField);}

		
		query.setSort("meta.source.datePublished", ORDER.desc);
		query.setRows(n_items);
		System.out.println("SearchByKeyword " + query.toString());
		QueryResponse response;

		try {
			
			response = solr.query(query);
			 SolrDocumentList results = response.getResults();
			 System.out.println("DEBUG: in video search - found videos " + results.size());
			 for (int i = 0; i < results.size(); ++i) {
		    	StringBuffer sb = new StringBuffer();
		    	for (String searchField: searchfields) {
		    		Object fieldVal = results.get(i).getFieldValue(searchField);
		    		if (fieldVal!=null) {
			    		sb.append(fieldVal.toString());
			    		sb.append("\n");
		    		}
		    	}
		    	String fieldText = sb.toString();
		    	String headline = results.get(i).getFieldValue("meta.source.headline").toString();
		    	headline = clean(headline);
		    	String url = null;
		    	Object uObj = results.get(i).getFieldValue("meta.source.url");
		    	if (uObj==null) uObj = results.get(i).getFieldValue("meta.source.mediaurl");

		    	if (uObj!= null) {
		    		url  = uObj.toString();
		    	}
		    	String publisher = "";
		    	Object pObj = results.get(i).getFieldValue("meta.source.publisher");
		    	if (pObj!=null)
		    		publisher = pObj.toString();
		    	else {
		    		publisher = "Deutsche Welle";
		    	}

		    	Reference ref =  null;
		    	if (url != null) {
		    		ref = new Reference(url, url, publisher);
		    	}

		    	Date date = (Date) results.get(i).getFieldValue("meta.source.datePublished");
		    	java.sql.Date sqldate = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());

		    	//dbpedia entities
		    	ArrayList<Entity> dbentities = new ArrayList<Entity> ();
		    	Collection<Object> entityObject = results.get(i).getFieldValues("meta.extracted.text_nerl.dbpedia.all");
		    	if (entityObject!=null)
		    	for (Object oe: entityObject) {
		    		String entityName = oe.toString();
		    		Entity e = new Entity();
		    		e.setName(entityName);
		    		dbentities.add(e);
		    	}



		    	Event e = new Event();
		    	e.setEntities(dbentities);
		    	e.setDescription(fieldText);
		    	e.setDate(sqldate);
		    	e.setHeadline(headline);
		    	if (ref!=null) e.addReference(ref);
		    	if (e.getDate().toString().compareTo("2050")<0) { //ensure there is not a date mistake when adding events to show
		    		if (!selectedTitles.contains(headline) && e.getEntities().size()>0) {
		    			itemList.add(e);
		    			selectedTitles.add(headline);
		    		}
		    		else {
		    			System.out.println(e.getHeadline());
		    		}
		    	}
		    	
		    }
		} catch (SolrServerException ex) {
			ex.printStackTrace();
		}
		System.out.println("item List: " + itemList.size());
	    return itemList;
	}



	public String clean(String headline) {
		int p = headline.indexOf("|");
		if (p>0) {
			return headline.substring(0, p);
		}
		return headline;
	}


	// using solrformated query to get the list of events from the solr db
	// and the filtering to keep n numebr of events
	public List<Event> getImportantEvents(int n, String solrFormatedQuery) {
		System.out.println("input query= " + solrFormatedQuery);
		ArrayList<String> qfilter = new ArrayList<String> ();		
		
		HashMap<String, Integer> datehash = new HashMap<String, Integer> (); 					// date count
		HashMap<String, ArrayList<Event>> eventhash = new HashMap<String, ArrayList<Event>> (); // event hash
 
		/** using manually filters for sources to avoid mismatches in format which is designed for timeline libs*/ 
		/*
		qfilter.add("\"News\"");
		qfilter.add("\"DW article\"");
		qfilter.add("\"DW video\"");
		qfilter.add("\"Youtube\"");
		
		String[] tmp = (String[]) qfilter.toArray(new String[qfilter.size()]);
		String strqfilter = "(source: " + StringUtils.join(tmp, " OR ") + " )";
		*/
		
		String strqfilter = "(meta.source.description:/.{1}.*/) OR (meta.source.text:/.{1}.*/)";  
		System.out.println("Sources for filter: " + strqfilter);
		
		
		ArrayList<Event> itemList = new ArrayList<Event> ();
		HashSet<String> selectedTitles = new HashSet<String> ();
		SolrQuery query = new SolrQuery();

		query.setFields("source", "meta.source.datePublished", "meta.source.headline", "meta.source.url",
				"meta.source.publisher", "meta.source.description", "meta.source.text", "meta.extracted.text_nerl.dbpedia.all");
		query.setQuery(solrFormatedQuery);
		query.setFilterQueries(strqfilter);

		query.setRows(20 * n);
		
		System.out.println("SearchByKeyword" + query.toString());
		QueryResponse response;

		try {
			response = solr.query(query);
			SolrDocumentList results = response.getResults();
			System.out.println(" Number of results "  + results.size());
		    for (int i = 0; i < results.size(); ++i) {
		    	StringBuffer sb = new StringBuffer();
		    	String headline = null;
		    	String url = null;
		    	String text = "";
    			String description = "";
    			String source = "";
		    	for (String searchField: new String[] {"source", "meta.source.text", "meta.source.description", "meta.source.datePublished",
		    			"meta.source.headline", "meta.source.url", "meta.source.publisher",
		    			"meta.extracted.text_nerl.dbpedia.all"})

		    	{
		    		Object fieldVal = results.get(i).getFieldValue(searchField);
		    		
		    		if (fieldVal!=null) {
		    			if (searchField.equals("source")) {
		    				source = results.get(i).getFieldValue("source").toString();
		    			}
		    			if (searchField.equals("meta.source.text")) {
			    			text = results.get(i).getFieldValue("meta.source.text").toString();
			    		}
		    			
		    			if (searchField.equals("meta.source.description")) {
			    			text = results.get(i).getFieldValue("meta.source.description").toString();
			    		}

			    		if (searchField.equals("meta.source.headline")) {
			    			headline = results.get(i).getFieldValue("meta.source.headline").toString();
			    			headline = clean(headline);
			    		}

			    		if (searchField.equals("meta.source.url")) {
			    			Object uObj = results.get(i).getFieldValue("meta.source.url");
			    			if (uObj==null) uObj = results.get(i).getFieldValue("meta.source.mediaurl");
			    			if (uObj==null) uObj = results.get(i).getFieldValue("meta.source.websiteUrl");
			    			if (uObj!= null) {
			    				url  = uObj.toString();
			    			}
			    		}
		    		}
		    	}
		    	if (description.length() > text.length()) sb.append(description);
		    	else sb.append(text);
		    	
		    	String fieldText = sb.toString().substring(0, Math.min(300, sb.length())) + "..."; // short description

		    	String publisher = "";
		    	Object pObj = results.get(i).getFieldValue("meta.source.publisher");
		    	if (pObj!=null)
		    		publisher = pObj.toString();
		    	else {
		    		publisher = "Deutsche Welle";
		    	}

		    	Reference ref =  null;
		    	if (url != null) {
		    		ref = new Reference(url, url, publisher);
		    	}

		    	Date date = (Date) results.get(i).getFieldValue("meta.source.datePublished");
		    	java.sql.Date sqldate = null;
		    	if (date!= null )
		    		sqldate = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());
		    	else continue;
		    	//dbpedia entities
		    	ArrayList<Entity> dbentities = new ArrayList<Entity> ();
		    	HashMap<String, Integer> en_hash = new HashMap<String, Integer> ();
		    	Collection<Object> entityObject = results.get(i).getFieldValues("meta.extracted.text_nerl.dbpedia.all");
		    	if (entityObject!=null)  {
			    	for (Object oe: entityObject) {
			    		String entityName = oe.toString();
			    		Entity e = new Entity();

			    		e.setName(entityName);
			    		if (!en_hash.containsKey(entityName) && dbentities.size()<10)
			    			dbentities.add(e);
			    		en_hash.put(entityName, 1);
			    	}
		    	}

		    	//form the event object
		    	Event e = new Event();
		    	e.setSource(source);
		    	e.setEntities(dbentities);
		    	e.setDescription(fieldText);
		    	e.setDate(sqldate);
		    	e.setHeadline(headline);
		    	String sqldstr = sqldate.toString();
		    	
		    	if (ref!=null) e.addReference(ref);
		    	if (e.getDate()!=null && e.getDate().toString().compareTo("2050")<0) {
		    		//ensure there is not a date mistake when adding events to show
		    		if (e.getDescription().length()>0 && (headline==null || headline.length()==0  ||
		    				(headline!=null &&headline.length()>0 && !selectedTitles.contains(headline)))) {
		    			
		    			selectedTitles.add(headline);
		    			
		    			int c = datehash.containsKey(sqldstr)? datehash.get(sqldstr):0;
		    			//update counting for datehash
		    			datehash.put(sqldstr, c+1);
		    			
		    			ArrayList<Event> eventsOdate = eventhash.containsKey(sqldstr)? eventhash.get(sqldstr): new ArrayList<Event> ();
		    			eventsOdate.add(e);
		    			eventhash.put(sqldstr, eventsOdate);
		    			
		    		}
		    	}
		    }
		    

	    	//selection of important dates
	    	ArrayList<String> dates = new ArrayList<String> ();
	    	dates.addAll(datehash.keySet());
	    	
	    	sortingMap.qsort(dates, datehash, 0, dates.size()-1);
	    	
	    	int number_of_dates = Math.min(n, dates.size());
	    	System.out.println("Number of dates: " + number_of_dates);
	    	//selection of important events
	    	for (int id = 0; id < number_of_dates; id++) {
	    		String datestr = dates.get(id);
	    		ArrayList<Event> imprtEvents = getImportantEvents(eventhash.get(datestr));
	    		itemList.addAll(imprtEvents);
	    	}
		} catch (Exception ex) {
			ex.printStackTrace();
		}


		System.out.println("successfully returns " + itemList.size());

		return itemList;
	}


	/**
	 * using diversity factor to select top events of the day
	 * @param arrayList
	 * @return
	 */
	private ArrayList<Event> getImportantEvents(ArrayList<Event> allevents) {
		//System.out.println("Number of events on this date: " + allevents.size() + " on " + allevents.get(0).getDate());
		ArrayList<Event> r = new ArrayList<Event> ();
		HashSet<String> hasSelected = new HashSet<String> ();
		for (Event e: allevents) {
			String source = e.sourceData;
			if (!hasSelected.contains(source)) {
				r.add(e);
				hasSelected.add(source);
			}
		}
		//System.out.println("Has selected : " + hasSelected.size() + " sources  with " + r.size() + " events");
		return r;
	}


	public List<Event> searchBySolrQuery(int n, String solrFormatedQuery) {
		ArrayList<Event> itemList = new ArrayList<Event> ();
		HashSet<String> selectedTitles = new HashSet<String> ();
		SolrQuery query = new SolrQuery();
		query.setFields("meta.source.datePublished", "meta.source.headline", "meta.source.url",
				"meta.source.publisher", "meta.source.text");
		query.setQuery(solrFormatedQuery);


		query.setRows(5*n);
		System.out.println("SearchByKeyword" + query.toString());
		QueryResponse response;

		try {
			response = solr.query(query);
			SolrDocumentList results = response.getResults();
		    for (int i = 0; i < results.size(); ++i) {
		    	StringBuffer sb = new StringBuffer();
		    	String headline = null;
		    	String url = null;
		    	for (String searchField: new String[] {"meta.source.text", "meta.source.datePublished", "meta.source.headline", "meta.source.url", "meta.source.publisher"})
		    	{
		    		Object fieldVal = results.get(i).getFieldValue(searchField);
		    		if (fieldVal!=null) {

		    			if (searchField.equals("meta.source.text")) {
			    			sb.append(results.get(i).getFieldValue("meta.source.text").toString());
			    		}

			    		if (searchField.equals("meta.source.headline")) {
			    			headline = results.get(i).getFieldValue("meta.source.headline").toString();
			    			headline = clean(headline);
			    		}

			    		if (searchField.equals("meta.source.url")) {
			    			Object uObj = results.get(i).getFieldValue("meta.source.url");
			    			if (uObj==null) uObj = results.get(i).getFieldValue("meta.source.mediaurl");
			    			if (uObj!= null) {
			    				url  = uObj.toString();
			    			}
			    		}
		    		}
		    	}
		    	String fieldText = sb.toString().substring(0, Math.min(300, sb.length())) + "..."; // short description

		    	String publisher = "";
		    	Object pObj = results.get(i).getFieldValue("meta.source.publisher");
		    	if (pObj!=null)
		    		publisher = pObj.toString();
		    	else {
		    		publisher = "Deutsche Welle";
		    	}

		    	Reference ref =  null;
		    	if (url != null) {
		    		ref = new Reference(url, url, publisher);
		    	}

		    	Date date = (Date) results.get(i).getFieldValue("meta.source.datePublished");
		    	java.sql.Date sqldate = null;
		    	if (date!= null )
		    		sqldate = new java.sql.Date(date.getYear(), date.getMonth(), date.getDate());

		    	//dbpedia entities
		    	ArrayList<Entity> dbentities = new ArrayList<Entity> ();
		    	Collection<Object> entityObject = results.get(i).getFieldValues("meta.extracted.text_nerl.dbpedia.all");
		    	if (entityObject!=null)  {
			    	for (Object oe: entityObject) {
			    		String entityName = oe.toString();
			    		Entity e = new Entity();
			    		e.setName(entityName);
			    		dbentities.add(e);
			    	}
		    	}


		    	Event e = new Event();
		    	e.setEntities(dbentities);
		    	e.setDescription(fieldText);
		    	e.setDate(sqldate);
		    	e.setHeadline(headline);
		    	if (ref!=null) e.addReference(ref);
		    	if (e.getDate()!=null && e.getDate().toString().compareTo("2050")<0) {
		    		//ensure there is not a date mistake when adding events to show
		    		if (!selectedTitles.contains(headline)) {
		    			itemList.add(e);
		    			selectedTitles.add(headline);
		    		}
		    	}
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}


		System.out.println("successfully returns " + itemList.size());

		return itemList;
	}

	/**
	 * 	 * @param solrquery
	 * @return
	 */
	private boolean isEntityBasedType(String solrquery) {
		
		String[] fields = new String[] {
				"meta.source.keywords",
				"meta.extracted.text_nerl.dbpedia.all",
				"meta.extracted.text_nerl.dbpedia.PERSON",
				"meta.extracted.text_nerl.dbpedia.ORGANIZATION",
				"meta.extracted.text_nerl.dbpedia.LOCATION",
				"meta.extracted.text_nerl.ner.PERSON",
				"meta.extracted.text_nerl.ner.ORGANIZATION",
				"meta.extracted.text_nerl.ner.LOCATION",
				"meta.extracted.text_nerl.ner.all"};
		for (String f: fields)
			if (solrquery.contains(f)) return true;
		return false;
	}


	private String getFieldFromQuery(String solrquery) {
		String[] fields = new String[] {
				"meta.source.headline",
				"meta.source.text",
				"meta.source.keywords",
				"meta.extracted.text_nerl.dbpedia.all",
				"meta.extracted.text_nerl.dbpedia.PERSON",
				"meta.extracted.text_nerl.dbpedia.ORGANIZATION",
				"meta.extracted.text_nerl.dbpedia.LOCATION",
				"meta.extracted.text_nerl.ner.PERSON",
				"meta.extracted.text_nerl.ner.ORGANIZATION",
				"meta.extracted.text_nerl.ner.LOCATION",
				"meta.extracted.text_nerl.ner.all"};
		for (String f: fields)
			if (solrquery.contains(f)) return f;
		return "meta.extracted.text_nerl.ner.all"; // by default
	}



	public ArrayList<String> getTop(HashMap<String, Integer> h, int n) {
		ArrayList<String> r = new ArrayList<String> ();
		for (String k: h.keySet()) r.add(k);
		if (r.size()>0)
			sortingMap.qsort(r, h, 0, r.size()-1);
		ArrayList<String> top = new ArrayList<String> ();
		for (int i = 0; i < Math.min(n, r.size()-1); i++) top.add(r.get(i));
		return top;
	}
	public  JSONArray getStoryTellingGraph(String entity_a, String entity_b,
			int n, String language) {
		entity_a = entity_a.replace("_", " ");
		entity_b = entity_b.replace("_", " ");
		HashMap<String, Integer> graph = new HashMap<String, Integer> ();
		SolrQuery query = new SolrQuery();
		//query.setFields(field);
		query.setFields(
				"meta.source.headline",
				"meta.source.text",
				"meta.source.keywords",
				"meta.extracted.text_nerl.ner.all"
				);
		query.setQuery("( meta.extracted.text_nerl.ner.all:*" + entity_a + "* OR *" + entity_b + "* ) OR " +
				"( meta.source.keywords:*" + entity_a + "* OR *" + entity_b + "* )");
		//query.addFilterQuery("meta.source.inLanguage:\"" + language + "\"");
		query.setRows(500);
		QueryResponse response;

		try {
			System.out.println(query);
			response = solr.query(query);
			SolrDocumentList results = response.getResults();
			int dset = results.size();
			if (dset ==0) return new JSONArray();

			int budget = Math.min(dset, n);
			boolean found = false;
			ArrayList<Integer> path = new ArrayList<Integer> ();
			HashSet<Integer> visitted = new HashSet<Integer> ();
			int start = 0;
			for (int i = 0; i< results.size(); i++) {
				if (checkExist(results.get(i), entity_a)){
					start = i;
					break;
				}
			}
			path.add(start);
			visitted.add(start);
			while (!found) {
				int j = getMaxCoherence(visitted, path, results);
				start = j;
				visitted.add(j);
				path.add(j);
				found = (checkExist(results.get(j), entity_b)
									&& visitted.size() >1)
									||visitted.size() ==Math.min(5*budget, dset);

			}

			System.out.println("Coherence path : " + path.size());
			SolrDocument startdoc = results.get(path.get(0));
			HashMap<String, Integer> prekeywords = getDistribution("meta.source.keywords", startdoc);
			HashMap<String, Integer> preentities = getDistribution("meta.extracted.text_nerl.ner.all", startdoc);

			ArrayList<String> topw = new ArrayList<String> ();
			for (String tmp: getTop(prekeywords,3)) topw.add(tmp);
			for (String tmp: getTop(preentities,3)) topw.add(tmp);

			boolean hasA = false;
			for (String tmp: topw) {
				if (tmp.equals(entity_a)) {hasA = true; break;}
			}
			if (!hasA) topw.add(entity_a);
			HashSet<String> indexed = new HashSet<String> ();
		    for (int i = 1; i < path.size(); ++i) {
		    	//make forced graph for meaningful representation
		    	SolrDocument currentDoc = results.get(path.get(i));
		    	HashMap<String, Integer> nextkeywords = getDistribution("meta.source.keywords", currentDoc);
				HashMap<String, Integer> nextentities = getDistribution("meta.extracted.text_nerl.ner.all", currentDoc);
				ArrayList<String> nexttopk = getTop(nextkeywords, 3);
				ArrayList<String> nexttope = getTop(nextentities, 3);
				ArrayList<String> nextw = new ArrayList<String> ();
				for (String tmp: nexttopk) nextw.add(tmp);
				for (String tmp: nexttope) nextw.add(tmp);

				if (i==path.size()-1) {
					boolean hasB= false;
					for (String tmp: nextw) {
						if (tmp.equals(entity_b)) {hasB = true; break;}
					}
					if (!hasB) nextw.add(entity_b);
				}

				for (String w1: topw) {
					for (String w2: nextw){
						String graphkey = w1 + ">>_<<" + w2;
		    			if (w2.compareTo(w1) <0) {
		    				graphkey =w2 + ">>_<<" + w1;
		    			}
		    			if (!indexed.contains(w2)) {
		    				int cur = graph.containsKey(graphkey)?graph.get(graphkey):0;
		    				graph.put(graphkey, cur+1);
		    				indexed.add(w2);
		    				indexed.add(w1);
		    			}
					}
				}
				topw = nextw;
		    }
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		ArrayList<String> all_items = new ArrayList<String> ();
		for (String s: graph.keySet()) all_items.add(s);
		System.out.println(all_items.size());
		JSONArray jsa = new JSONArray();
		if (all_items.size()==0) return jsa;
		sortingMap.qsort(all_items, graph, 0, all_items.size()-1);

		for (int j = 0; j < Math.min(500, graph.size()); j++) {
			JSONObject o = new JSONObject();
			String graphkey = all_items.get(j);
			String[] keysplt  = graphkey.split(">>_<<");
			int f = graph.get(all_items.get(j));
			if  (keysplt.length <2) continue;

			try {
				o.put("source", keysplt[0]);
				o.put("target", keysplt[1]);
				o.put("weight", Math.floor(1.0 * f/1));
				jsa.put(o);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		System.out.println(jsa.toString());
		return jsa;

	}



	private boolean checkExist(SolrDocument solrDocument, String entity) {
		Collection<Object> entityObject = solrDocument.getFieldValues("meta.extracted.text_nerl.ner.all");
    	HashSet<String> itemset = new HashSet<String> ();

	    	if (entityObject!=null)  {
		    	for (Object oe: entityObject) {
		    		String entityName = oe.toString();
		    		if (entityName.equals(entity)) {
		    			System.out.println("Found " + entityName);
		    			return true;
		    		}
		    	}

	    	}
		return false;
	}


	/** maximizes coherence */
	private int getMaxCoherence(HashSet<Integer> visitted,
			ArrayList<Integer> path, SolrDocumentList results) {
		int last = path.size()-1;
		double maxcoh = -1;
		int keep = 0;
		for (int i = 0; i< results.size(); i++) {
			if (!visitted.contains(i) ) {
				double coh = coherence(results.get(i), results.get(last));
				if (coh > maxcoh) {
					maxcoh = coh;
					keep = i;
				}
			}
		}
		return keep;
	}


	private double coherence(SolrDocument solrDocumentA,
			SolrDocument solrDocumentB) {
		HashMap<String, Integer> e1 = getDistribution("meta.extracted.text_nerl.ner.all", solrDocumentA);
		HashMap<String, Integer> e2 = getDistribution("meta.extracted.text_nerl.ner.all", solrDocumentB);
		HashMap<String, Integer> k1 = getDistribution("meta.source.keywords", solrDocumentA);
		HashMap<String, Integer> k2 = getDistribution("meta.source.keywords", solrDocumentB);
		return coherence(e1, e2) + coherence(k1, k2);
	}

	public double coherence(HashMap<String, Integer> v1, HashMap<String, Integer> v2) {
		int size = v1.size();
		if (size ==0) return 0;
		int sc = 0;
		for (String x: v2.keySet())
			if (v1.containsKey(x)) sc++;
		return 1.0 * sc / size;
	}

	//field: "meta.extracted.text_nerl.ner.all" or others
	public HashMap<String, Integer> getDistribution(String field, SolrDocument document) {
		Collection<Object> entityObject = document.getFieldValues(field);
    	HashMap<String, Integer> itemset = new HashMap<String, Integer> ();
	    	if (entityObject!=null)  {
		    	for (Object oe: entityObject) {
		    		String entityName = oe.toString();
		    		int c = itemset.containsKey(entityName)?itemset.get(entityName):0;
		    		itemset.put(entityName, c+1);
		    	}

	    	}
		return itemset;
	}

	public static void main(String[] args) {
		SolrDBManager sm = new SolrDBManager();
		String query = "*:*";
		sm.getStoryTellingGraph("Angela_Merkel", "Barack_Obama", 20, "en");
	}


}
