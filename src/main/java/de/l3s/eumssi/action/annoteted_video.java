package de.l3s.eumssi.action;

public class annoteted_video {
	 private String videoUrl;
	 private String subTitleName;
	 
	 public String execute(){
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
		
		
}
