package de.l3s.eumssi.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;


public class Second_screen_content_managerAction implements ServletRequestAware {
    private String video;
	HttpServletRequest request;
	public void execute() throws Exception {
		BufferedReader br = null;

		try {
              
			  ServletContext context = request.getServletContext();
				String path = context.getRealPath("/");
			String sCurrentLine;
			HttpSession session = request.getSession(false);
			if(video!=null){
			
			video=video.replaceAll(" ", "_");
			session.setAttribute("video", video);
			System.out.println(session.getAttribute("video"));
			
			}
			
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		}

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}
	
	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	
}
