<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<script src="scripts/bower_components/jquery/dist/jquery.js"> </script>

</head>
<body>
<video width="1100" height="600" controls>
  <source id="videoSrc" src="http://tv-download.dw.de/dwtv_video/flv/me/me20140513_fracking_sd_avc.mp4" type="video/mp4">
  <track id="videoTrack" src="scripts/text.vtt" kind="subtitles" srclang="en" label="English" default/>
  
</video>


<div id="asdf"></div><br>

<button type="button" id="close">Close</button><br>
<form action="logout">
  <br>
  <input type="submit" value="logout">
  </form>
<script>



var trackElement = document.querySelector("track");
trackElement.addEventListener("load", function() {
 var textTrack = this.track; 
 textTrack.mode ="hidden";
 textTrack.oncuechange = function (){
	  var cue = this.activeCues[0];
	  document.getElementById("asdf").innerHTML=cue.text;
	
	
	  $.post("second_screen_content",
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