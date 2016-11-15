package de.l3s.eumssi.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class annoteted_videos_login implements ServletRequestAware {
	HttpServletRequest request;
	public String userId=null;
	private JSONArray recentVideos=new JSONArray(); 
	public String execute() throws Exception{
		HttpSession session = request.getSession(false);
		 userId=request.getParameter("userId");
	
	    HttpServletResponse response = ServletActionContext.getResponse();
	    
		if (session==null ||session.getAttribute("userId") == null) {
			
			if(userId==null){
			System.out.println("no user id");
				return "login";
			}
			else{
		    session = request.getSession(true);
		   session.setAttribute("userId", userId);
		  System.out.println(session.getAttribute("userId"));
		  getRecentVideosAndCreateVttFile();
			return "success";
			}
		} else {
		  System.out.println(session.getAttribute("userId"));
		  userId=(String) session.getAttribute("userId");
		  getRecentVideosAndCreateVttFile();
		  return "success";
		}
		
		
		
	}

	private void getRecentVideosAndCreateVttFile() throws Exception{
		HttpSolrServer solr = new HttpSolrServer("http://demo.eumssi.eu/Solr_EUMSSI/content_items/");
		SolrQuery query = new SolrQuery();
		query.setQuery( "meta.extracted.audio_transcript-dbpedia:* AND meta.source.datePublished:[NOW-180DAY/DAY TO NOW]");
		query.setFields("meta.source.mediaurl AND meta.extracted.audio_transcript-dbpedia AND meta.source.headline");
		query.setStart(0);
		query.setRows(20);
		QueryResponse response = solr.query(query);
	    SolrDocumentList results = response.getResults();
	    for(int documentCounter=0;documentCounter<results.size();documentCounter++ ){
	    	String videoUrl=(String) results.get(documentCounter).getFieldValue("meta.source.mediaurl");
	    	String transcript=(String) results.get(documentCounter).getFieldValue("meta.extracted.audio_transcript-dbpedia");
	    	String headLine=(String) results.get(documentCounter).getFieldValue("meta.source.headline");
	    	ServletContext context = request.getServletContext();
		    String path = context.getRealPath("/");
    		String fileNameWithoutExtension = FilenameUtils.removeExtension(videoUrl);
	    //  fileNameWithoutExtension=fileNameWithoutExtension.replaceAll("\\", "\\\\");
    	    String[] fileNames=videoUrl.split("/");
		    String subtitleName=fileNames[fileNames.length-1];
		    subtitleName = FilenameUtils.removeExtension(subtitleName);
		    subtitleName=subtitleName+".vtt";
		    JSONObject tempJsonObj=new JSONObject();
		    tempJsonObj.put("videoUrl", videoUrl);
		    tempJsonObj.put("subtitleName", subtitleName);
		    tempJsonObj.put("headLine", headLine);
		    recentVideos.add(tempJsonObj);
		    File file = new File(path + File.separator + "vtt_files" + File.separator + subtitleName);
		    System.out.println(file);
		    Map<Long,String> timedEntities=new TreeMap<Long,String>(); 
		    transcript=transcript.replaceAll("\n", "");
			JSONParser parser = new JSONParser();
			JSONArray transcriptJarray=(JSONArray) parser.parse(transcript);
			for(int i=0;i<transcriptJarray.size();i++){
				JSONObject eachJsonObject=(JSONObject) transcriptJarray.get(i); 
				Long beginTime=(Long) eachJsonObject.get("beginTime");
			    String entity=	(String) eachJsonObject.get("text");
				timedEntities.put(beginTime, entity);
			}
		    timedEntities=ensureTenSecondsDelay((TreeMap<Long, String>) timedEntities);
	    	if(timedEntities!=null)
		    if (!file.exists()) {
			 file.createNewFile();	
		     if(timedEntities!=null){
		       String fileContent="WEBVTT #"+headLine+"\n";
	           Second_screen_contentAction second_screen_content=new Second_screen_contentAction();
		       for ( Long key : timedEntities.keySet() ) {
			   long millis=key;
			   String timeFrom= String.format("%02d:%02d:%02d.%03d",
					TimeUnit.MILLISECONDS.toHours(millis),
				    TimeUnit.MILLISECONDS.toMinutes(millis),
				    TimeUnit.MILLISECONDS.toSeconds(millis) - 
				    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
				    TimeUnit.MILLISECONDS.toMillis(millis)-TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis))
				);
		      System.out.println(timeFrom);
			  DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
		      Date timeTo = df.parse(timeFrom);
		      Calendar cal = Calendar.getInstance();
		      cal.setTime(timeTo);
		      cal.add(Calendar.SECOND, 5);
		      timeTo = cal.getTime();
		      System.out.println("time+5: " + df.format(timeTo));
		      String tempEntityName=timedEntities.get(key);
		      String[] tempEntityName_part=tempEntityName.split(" ");
		      String searchable_entity_Name=null;
		      for(int partCounter=0;partCounter<tempEntityName_part.length;partCounter++){
		    	if(searchable_entity_Name==null)
		    	searchable_entity_Name=tempEntityName_part[partCounter].substring(0, 1).toUpperCase()+tempEntityName_part[partCounter].substring(1);
		    	else
		    		searchable_entity_Name=searchable_entity_Name+'_'+tempEntityName_part[partCounter].substring(0, 1).toUpperCase()+tempEntityName_part[partCounter].substring(1);
		       }
			  fileContent=fileContent+"\n"+timedEntities.get(key)+"\n"+timeFrom+" --> "+
		      df.format(timeTo)+"\n"+second_screen_content.contentGenerator(searchable_entity_Name)+"\n";
		      }
		
	         FileWriter fw = new FileWriter(file);
	       	 BufferedWriter bw = new BufferedWriter(fw);
		     bw.write(fileContent);
		     bw.close();
	    
		   }
		 }
	    }
	    System.out.println("recentVideos: "+ recentVideos);
	}
		
private  Map<Long, String> ensureTenSecondsDelay(TreeMap<Long, String> timedEntities) {
	mainLoop:
	while(true){
	for ( Long key : timedEntities.keySet() ){
		Long higherKey=timedEntities.higherKey(key);
		if(higherKey==null){
			break mainLoop;
		}
		try{
		if(higherKey-key<10000){
			Long nextHigherKey=timedEntities.higherKey(higherKey);
			if(nextHigherKey==null){
				timedEntities.remove(higherKey);
				break mainLoop;
			}
			Long d=higherKey-key;
			
			if((10000+(10000-d))<(nextHigherKey-higherKey)){
			 String valueOfHigherKey=timedEntities.get(higherKey);
			 timedEntities.remove(higherKey);
			 timedEntities.put(key+10000, valueOfHigherKey);
			}
			else{
				timedEntities.remove(higherKey);
			}
			
			break;
		}
		}
		catch(NullPointerException e){
			System.out.println("timedEntities: "+timedEntities);
			System.out.println("key: "+key);
			System.out.println("higherKey: "+higherKey);
			//System.out.println("nextHigherKey: "+nextHigherKey);
		}
	}
  
  }

		return timedEntities;
}

	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		System.out.println("request from anneted videos: " +this.request);
		
	}

	public JSONArray getRecentVideos() {
		return recentVideos;
	}

	

}
