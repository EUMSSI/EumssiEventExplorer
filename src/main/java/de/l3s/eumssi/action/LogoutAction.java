package de.l3s.eumssi.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

public class LogoutAction  implements ServletRequestAware{
	HttpServletRequest request;
	public void execute() throws Exception {
		  HttpServletResponse response = ServletActionContext.getResponse();
		HttpSession session = request.getSession(false);
		if(session != null)
			session.setAttribute("userId", null);
		    session.invalidate();
	  // return "login";
		response.getWriter().println("you are now logged out");
	
	
	}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

}
