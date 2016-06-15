package de.l3s.eumssi.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

public class Second_screen_updateAction implements ServletRequestAware {
	HttpServletRequest request;
	public void execute() throws Exception {
		 

		
		HttpSession session = request.getSession(false);
		  ServletContext context = request.getServletContext();
			String path = context.getRealPath("/");
			BufferedReader br = null;
			String sCurrentLine;
			HttpServletResponse response = ServletActionContext.getResponse();
			 
			response.setContentType("text/event-stream");
			//cache must be set to no-cache
	        response.setHeader("Cache-Control", "no-cache");     
	        //encoding is set to UTF-8
	        response.setCharacterEncoding("UTF-8");
	        AsyncContext asyncContext = request.startAsync(request, response);
	        
	       

			String userId=(String) session.getAttribute("userId");
			String videoName=(String) session.getAttribute("video");
		File file = new File(path + File.separator + "video_state" + File.separator +userId+"&&"+videoName+".txt");
	//	System.out.println(file);
		

	
		
		for(int i=0;i<10;i++){
		br = new BufferedReader(new FileReader(file));	
		sCurrentLine = br.readLine();
	 
		if(sCurrentLine!=null){
			   System.out.println(sCurrentLine);
			//response.setContentType("text/event-stream");
		// response.getWriter().write("data: "+ sCurrentLine +"\n\n");
		// response.getWriter().flush();
			asyncContext.getResponse().getWriter().write("data: "+ sCurrentLine +"\n\n");
			asyncContext.getResponse().getWriter().flush();
	//	 response.getWriter().write("data: "+ sCurrentLine +"\r\n");
        PrintWriter writer = new PrintWriter(file);
	    writer.print("");
		writer.close();
		  
		}
		Thread.sleep(1000);
		System.out.println(i);
			}
		asyncContext.complete();
		
		
	}
		
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}
	}


