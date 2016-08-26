package de.l3s.eumssi.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionContext;

public class annoteted_video {
	 private String videoUrl;
	 private String subTitleName;
	 Map attributes = ActionContext.getContext().getSession();
     private String userId=(String) attributes.get("userId");
	 
     public String execute(){
		 System.out.println("userId: "+userId);
		 System.out.println("videoUrl "+videoUrl);
		 System.out.println("subTitleName "+subTitleName);
		 return "success";
	 }
	 
	 public String getVideoUrl() {
			return videoUrl;
		}

		public void setVideoUrl(String videoUrl) {
			this.videoUrl = videoUrl;
		}

		public String getSubTitleName() {
			return subTitleName;
		}

		public void setSubTitleName(String subTitleName) {
			this.subTitleName = subTitleName;
		}

		 public String getUserId() {
				return userId;
			}
		
		
}
