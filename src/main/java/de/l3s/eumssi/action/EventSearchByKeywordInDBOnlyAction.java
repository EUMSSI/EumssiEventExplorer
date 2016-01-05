package de.l3s.eumssi.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.JSONArray;
import org.json.JSONObject;

import com.opensymphony.xwork2.ActionSupport;

import de.l3s.eumssi.dao.SolrDBManager;
import de.l3s.eumssi.model.*;


public class EventSearchByKeywordInDBOnlyAction  extends ActionSupport implements ServletRequestAware {
	private static final long serialVersionUID = 1L;
	private SolrDBManager db = new SolrDBManager();
	private String query;
	private String filterType = null;
	private String filterItemId = null;
	private int searchway;
	private boolean infer;
	
	private Object contextPath;
	private boolean useContextPath;
	private String location;

	private String itemType;
	private String itemId;
	private String itemName;
	private int searchsize;
	private String fromDate;
	private String toDate;
	private boolean hasWikipediaUrl;
	private String wikipediaUrl;
	private boolean usingTimeWindow;
	
	private boolean useVideo;
	private boolean useNewsArticle;
	private boolean useText;
	private boolean useHeadline;
	private boolean useTranscript;
	
	
	//private String fieldMenu;
	
	private JSONObject timeline;
	private org.json.JSONArray tfjson, coocc;
	
	
	private List<Event> events;
	private List<Entity> entities = null;

	
	private javax.servlet.http.HttpServletRequest request;
	
	//Distribution
	
	ArrayList<Pair<String, Short>> tf = null;
	
	@Override
	 public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
	
	
	private ArrayList<String> formSearchField() {
		ArrayList<String> searchFields = new ArrayList<String> ();
		
		/*
		switch (this.fieldMenu) {
		case "text":
			searchField ="meta.source.text";
			break;
		case "title":
			searchField ="meta.source.headline";
			break;
		case "transcript":
			searchField ="meta.extracted.audio_transcript";
			break;
		default:
			searchField ="meta.source.text";
			break;
		}
		*/
		
		if (this.useHeadline) searchFields.add("meta.source.headline");
		if (this.useText)searchFields.add("meta.source.text");
		if (this.useTranscript) searchFields.add("meta.extracted.audio_transcript");
		for (String s: searchFields) System.out.println("Search field:"  + s);
		return searchFields;
	}
	

	
	
	/** video contextualization with entities information taken from solr **/
	public String videoContextualize() throws Exception {
		ArrayList<String> searchField = formSearchField();						//search field
		contextPath = ServletActionContext.getServletContext().getContextPath();
		useContextPath = true;
		
		//Debug
		System.out.println("Use Text:" + this.useText);
		System.out.println("Use Headline:" + this.useHeadline);
		System.out.println("Use Transcript:" + this.useTranscript);
		System.out.println("---------------------------");
		
		ArrayList<String> sources = new ArrayList<String> ();
		sources.add("Video"); // only use video data
		
		for (String s: sources) System.out.println(s);
		try{
			
			itemType = "Query";
			itemId = null;
			if(query!=null && !query.equals("")){
				itemName = query;
			}else{
				itemName = " show all events";
			}
			
			hasWikipediaUrl = false;
			wikipediaUrl = "";
	
			int maxNumOfEventsToDisplay = Integer.parseInt(db.conf.getProperty("visualization_MaxTimelineSize"));
			
			//events = db.searchByKeyword(query, "Eumssi-News-Crawler OR DW-en_GB ", "meta.source.text", maxNumOfEventsToDisplay);
			System.out.println("\tDebug: " + query);
			events = db.videoSearch(query, sources, searchField, maxNumOfEventsToDisplay);
			searchsize = events.size();
			System.out.println("number of found videos: " + searchsize);
			// get the dates of the first and last events to show on results page:
			if (!events.isEmpty()){
				toDate = events.get(0).getDate().toString();
				fromDate = events.get(events.size()-1).getDate().toString();
			}			
			timeline = db.getTimelineJSON(events, contextPath);
		}catch(Exception e){
			e.printStackTrace();	
		}finally{
			
		}
		System.out.println("Successfully action performed");
		return "VideoContextualizeView";
	}
	
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	
	
	public String getFilterType() {
		return filterType;
	}



	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}



	public String getFilterItemId() {
		return filterItemId;
	}



	public void setFilterItemId(String filterItemId) {
		this.filterItemId = filterItemId;
	}



	public int getSearchway() {
		return searchway;
	}


	public void setSearchway(int searchway) {
		this.searchway = searchway;
	}

	public int getSearchsize() {
		return searchsize;
	}



	public void setSearchsize(int searchsize) {
		this.searchsize = searchsize;
	}



	
	public javax.servlet.http.HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(javax.servlet.http.HttpServletRequest request) {
		this.request = request;
	}

	public List<Event> getEvents() {
		return events;
	}


	public void setEvents(List<Event> events) {
		this.events = events;
	}
	



	public String getFromDate() {
		return fromDate;
	}



	public void setFromDate(String qfromDate) {
		this.fromDate = qfromDate;
	}



	public String getToDate() {
		return toDate;
	}


	public void setToDate(String qtoDate) {
		this.toDate = qtoDate;
	}

	public Object getContextPath() {
		return contextPath;
	}

	public void setContextPath(Object contextPath) {
		this.contextPath = contextPath;
	}

	public boolean isUseContextPath() {
		return useContextPath;
	}

	public void setUseContextPath(boolean useContextPath) {
		this.useContextPath = useContextPath;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String loc) {
		location = loc;
	}

	public boolean isHasWikipediaUrl() {
		return hasWikipediaUrl;
	}

	public void setHasWikipediaUrl(boolean hasWikipediaUrl) {
		this.hasWikipediaUrl = hasWikipediaUrl;
	}

	public String getWikipediaUrl() {
		return wikipediaUrl;
	}

	public void setWikipediaUrl(String wikipediaUrl) {
		this.wikipediaUrl = wikipediaUrl;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}



	public List<Entity> getEntities() {
		return entities;
	}



	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}



	public String getItemId() {
		return itemId;
	}



	public void setItemId(String itemId) {
		this.itemId = itemId;
	}



	public boolean getInfer() {
		return infer;
	}



	public void setInfer(boolean infer) {
		this.infer = infer;
	}



	public JSONObject getTimeline() {
		return timeline;
	}



	public void setTimeline(JSONObject timeline) {
		this.timeline = timeline;
	}
	


	public boolean isUsingTimeWindow() {
		return usingTimeWindow;
	}



	public void setUsingTimeWindow(boolean usingTimeWindow) {
		this.usingTimeWindow = usingTimeWindow;
	}


	
	public String getTfjson() {
		return tfjson.toString().replace("'", "\\'");
	}
	
	public String getCoocc() {
		return coocc.toString().replace("'", "\\'");
	}

	public boolean getUseVideo() {
		return useVideo;
	}
	
	public void setUseVideo(boolean vid) {
		this.useVideo = vid;
	}

	public boolean getUseNewsArticle() {
		return this.useNewsArticle;
	}
	public void setUseNewsArticle(boolean art) {
		this.useNewsArticle = art;
	}
	
	public boolean getUseHeadline() {
		return this.useHeadline;
	}
	
	public void setUseHeadline(boolean useheadline) {
		this.useHeadline = useheadline;
	}
	
	public boolean getUseText() {
		return this.useText;
	}
	
	public void setUseText(boolean usetext) {
		this.useText = usetext;
	}
	
	public boolean getUseTranscript() {
		return this.useTranscript;
	}
	
	public void setUseTranscript(boolean usetranscript) {
		this.useTranscript = usetranscript;
	}
}
