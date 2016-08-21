package de.l3s.eumssi.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

public class annoteted_videos_login implements ServletRequestAware {
	HttpServletRequest request;
	public String userId=null;
	public String execute(){
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
		
			return "success";
			}
		} else {
		  System.out.println(session.getAttribute("userId"));
		  userId=(String) session.getAttribute("userId");
		  return "success";
		}
		
		
		
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		System.out.println("request from anneted videos: " +this.request);
		
	}

}
