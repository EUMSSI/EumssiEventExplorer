package de.l3s.eumssi.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

public class VideoAction implements ServletRequestAware{
	public String videoUrl;
	public String subtitleName;
	public String headLine;
	HttpServletRequest request;
	public String userId=null;
		public String execute() throws Exception {
			
			
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
			 VttFileCreator();
				return "success";
				}
			} else {
			  System.out.println(session.getAttribute("userId"));
			  VttFileCreator();
			  return "success";
			}
			
		}
	 
	private void VttFileCreator() throws IOException, SolrServerException{
		ServletContext context = request.getServletContext();
		String path = context.getRealPath("/");
		String fileNameWithoutExtension = FilenameUtils.removeExtension(videoUrl);
		//fileNameWithoutExtension=fileNameWithoutExtension.replaceAll("\\", "\\\\");
    	String[] fileNames=fileNameWithoutExtension.split("/");
		subtitleName=fileNames[fileNames.length-1];
		File file = new File(path + File.separator + "vtt_files" + File.separator + subtitleName + ".vtt");
		System.out.println(file);
		if (!file.exists()) {
			file.createNewFile();
		}
		String [] entities= getEntitiesFromSolr();
		if(entities!=null){
		String fileContent="WEBVTT FILE\n";
		SimpleDateFormat formatter;

		formatter = new SimpleDateFormat("HH:mm:ss.SSS");
		

		Calendar calendar = Calendar.getInstance();
	    calendar.setTime(new Date());
	    calendar.set(Calendar.HOUR_OF_DAY,0);
	    calendar.set(Calendar.MINUTE,0);
	    calendar.set(Calendar.SECOND,0);
	    calendar.set(Calendar.MILLISECOND,0);
	    Calendar time1 = Calendar.getInstance();
	    Calendar time2 = Calendar.getInstance();
	    for(int i=0;i<entities.length;i++) {
			    time1.setTime(calendar.getTime());
			    time1.set(Calendar.HOUR_OF_DAY,0);
			    time1.set(Calendar.MINUTE,0);
			    time1.set(Calendar.SECOND,calendar.get(Calendar.SECOND)+i*5);
			    time1.set(Calendar.MILLISECOND,0);

			    time2.setTime(calendar.getTime());
			    time2.set(Calendar.HOUR_OF_DAY,0);
			    time2.set(Calendar.MINUTE,0);
			    time2.set(Calendar.SECOND,calendar.get(Calendar.SECOND)+i*5+3);
			    time2.set(Calendar.MILLISECOND,0);  
			    
			    System.out.println(formatter.format(time1.getTime()));
			    fileContent=fileContent+"\n"+entities[i]+"\n"+formatter.format(time1.getTime())+" --> "+formatter.format(time2.getTime())+"\n"+entities[i]+"\n";
	    }
	    FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(fileContent);
		bw.close();
	    
		}
	}
		

	
	private String[] getEntitiesFromSolr() throws SolrServerException{
			
			HttpSolrServer solr = new HttpSolrServer("http://demo.eumssi.eu/Solr_EUMSSI/content_items/");
			SolrQuery query = new SolrQuery();
			 System.out.println(videoUrl);
			query.setQuery( "meta.source.mediaurl:"+"\""+videoUrl+"\"");
			query.setFields("meta.extracted.text_nerl.dbpedia.all","meta.extracted.text_nerl.dbpedia.Country","meta.extracted.text_nerl.dbpedia.PERSON","meta.extracted.text_nerl.dbpedia.LOCATION","meta.source.headline");
			QueryResponse response = solr.query(query);
		    SolrDocumentList results = response.getResults();
		    System.out.println(results.size());
		    ArrayList entities=null;
		    ArrayList tempEntities=null;
		    int i=0;
		//    for (int i = 0; i < results.size(); ++i) {
		       entities= (ArrayList) results.get(i).getFieldValue("meta.extracted.text_nerl.dbpedia.all");
		       
		       tempEntities= (ArrayList) results.get(i).getFieldValue("meta.extracted.text_nerl.dbpedia.Country");
		       if(tempEntities!=null)
		       entities.addAll(tempEntities);
		       tempEntities= (ArrayList) results.get(i).getFieldValue("meta.extracted.text_nerl.dbpedia.PERSON");
		       if(tempEntities!=null)
		       entities.addAll(tempEntities);
		       tempEntities= (ArrayList) results.get(i).getFieldValue("meta.extracted.text_nerl.dbpedia.LOCATION");
		       if(tempEntities!=null)
		       entities.addAll(tempEntities);
		        headLine=(String) results.get(i).getFieldValue("meta.source.headline");
		       System.out.println(headLine);
		       
		//    }
		    if(entities!=null){   
		    Set<String> hs = new HashSet<>();
		    hs.addAll(entities);
		    entities.clear();
		    entities.addAll(hs);
		    
		    String[] entitiesArray=(String[]) entities.toArray(new String[entities.size()] );
		    
		
		 return entitiesArray;
		    }
		    else
		    	return null;
		}	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
       		
	}
	
	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
}
