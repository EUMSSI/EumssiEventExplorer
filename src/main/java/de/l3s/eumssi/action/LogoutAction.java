package de.l3s.eumssi.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;


public class LogoutAction  implements ServletRequestAware{
	private String from;
	HttpServletRequest request;
	public String execute() throws Exception {
		System.out.println("from : "+from);
		  HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession(false);
		if(session != null)
			session.setAttribute("userId", null);
		    session.invalidate();
	if(from.equals("video"))	    
	   return "login";
	else if(from.equals("second_screen"))
		return "second_screen_login";
	else
		return "annoteted_video";
 				
	//	response.getWriter().println("you are now logged out");
	
	
	}

	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}

}
