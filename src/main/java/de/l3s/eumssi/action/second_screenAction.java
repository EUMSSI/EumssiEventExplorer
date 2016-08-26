package de.l3s.eumssi.action;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

public class second_screenAction implements ServletRequestAware{
	public String userId=null;
	public String subtitleName;
	public Map<String,String> videoMap=new HashMap<String,String>();
	HttpServletRequest request;
	public String execute() throws Exception  {
		
		//check for user session

		HttpSession session = request.getSession(false);
		 userId=request.getParameter("userId");
	     
	    
		if (session==null ||session.getAttribute("userId") == null) {
			
			if(userId==null){
			System.out.println("no user id");
				return "login";
			}
			else{
		    session = request.getSession(true);
		   session.setAttribute("userId", userId);
		  System.out.println(session.getAttribute("userId"));
		//  videoListCreator();
			return "success";
			}
		}
		else{
			userId=(String) session.getAttribute("userId");
		    return "success";	
		}
		
		 
	}
/*
	private void videoListCreator(){
		List fileList =new ArrayList();
      
		  ServletContext context = request.getServletContext();
			String path = context.getRealPath("/");
		String sCurrentLine;

//		File[] files = new File(path + File.separator + "video_state").listFiles((dir, name) -> !name.equals(".DS_Store"));
		
		// If this pathname does not denote a directory, then listFiles()
		// returns null.
//	 ;
		
		for (File file : files) {
			if (file.isFile()) {
				fileList.add(file.getName());
			}
		}
		System.out.println(fileList);
		System.out.println(userId);
		for(int i=0;i<fileList.size();i++){
			String fileName=(String) fileList.get(i);
		    fileName = FilenameUtils.removeExtension(fileName);
			String[] splitedNames=fileName.split("&&");
			if(splitedNames[0].equals(userId)){
				String mapValue=splitedNames[1].replaceAll("_", " ");
				videoMap.put(splitedNames[1],mapValue);
			}
		}
		System.out.println("videomap"+videoMap);
	} 
*/	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}
	
	
}
