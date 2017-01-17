<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.2/jquery.min.js"></script>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
 <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
</head>
<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
     <ul class="nav navbar-nav">
      <li><a><img src="Images/eumssi_logo.png"></a></li>
      <li><a><h3 style="font-family:serif;">First Screen</h3></a></li>
      <li><a><form action="annotated_videos"> <input type="submit" class='btn btn-primary' value="close"></form></a></li>
      <li><a><form action="logout"> <input type="submit" class='btn btn-primary' value="logout"></form></a><li>
     </ul>
  </div>
</nav>
<video width="1100" height="600" controls>
  <source id="videoSrc" src="http://tv-download.dw.de/dwtv_video/flv/me/me20140513_fracking_sd_avc.mp4" type="video/mp4">
  <track id="videoTrack" src="scripts/text.vtt" kind="subtitles" srclang="en" label="English" default/>
  
</video>


<div id="asdf"></div><br>


<script>



var trackElement = document.querySelector("track");
trackElement.addEventListener("load", function() {
 var textTrack = this.track; 
 textTrack.mode ="hidden";
 textTrack.oncuechange = function (){
	  var cue = this.activeCues[0];
	  document.getElementById("asdf").innerHTML=cue.text;
	
	
	  $.post("chat1",
		        {
		          entityName: cue.text,
                  videoName: "video1",	
                  headLine:"Alternative Fracking - Environmental risk or economic opportunity"
		        })
	}
 

}
)
$("#close").click(function(){
	 var newWindow = window.open('', '_self', '');
	 newWindow.close();
});

</script>
</body>
</html>