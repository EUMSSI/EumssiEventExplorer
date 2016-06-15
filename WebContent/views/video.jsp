<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<script src="scripts/bower_components/jquery/dist/jquery.js"> </script>

</head>
<body>
<video width="1000" height="600" controls>
  <source id="videoSrc" src=scripts/sample.mp4 type="video/mp4">
  <track id="videoTrack" src="scripts/text.vtt" kind="subtitles" srclang="en" label="English" default/>
  
</video>
<s:hidden  name="videoUrl" id="videoUrl" />
<s:hidden  name="subtitleName" id="subtitleName" />
<s:hidden  name="headLine" id="headLine" />
<div id="asdf"></div><br>
<div>The timing of the information boxes is currently fixed. <br>
        Our goal is to show them at the exact time that a person appears or a location is mentioned. This is shown in:</div>
        <a href='<s:url action="video1" includeContext="true">
	     </s:url>'>1. Alternative Fracking - Environmental risk or economic opportunity? </a><br>
<button type="button" id="close">Close</button><br>
<form action="logout">
  <br>
  <input type="submit" value="logout">
  </form>
<script>
var videoUrl = document.getElementById("videoUrl").value;

$("#videoSrc").attr("src",videoUrl);

var subtitleName = document.getElementById("subtitleName").value;
var headLine = document.getElementById("headLine").value;
$("#videoTrack").attr("src","vtt_files/"+subtitleName+".vtt");
var trackElement = document.querySelector("track");
trackElement.addEventListener("load", function() {
 var textTrack = this.track; 
 textTrack.mode ="hidden";
 textTrack.oncuechange = function (){
	  var cue = this.activeCues[0];
	  document.getElementById("asdf").innerHTML=cue.text;
	
	
	  $.post("chat1",
		        {
		          entityName: cue.text
                 // videoName: subtitleName,	
                //  headLine:headLine
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