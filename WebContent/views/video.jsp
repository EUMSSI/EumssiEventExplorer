<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta name="viewport" content="width=device-width, initial-scale=1">
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
      <li><a><button type="button" class='btn btn-primary'  id="close">Close</button></a></li>
      <li><a><s:form method="get" action="logout"> <input type="submit" class='btn btn-primary'  value="logout"><s:hidden name="from" value="video"/></s:form></a><li>
     
    </ul>
  </div>
</nav>
<div class="container-fluid">
Choose from the following. It will be reflected to the second screen with other contents. If you want both, do not do anything. 
 <label class="radio-inline"><input type="radio" value="both" name="option" checked="checked">Question and Info</label>
 <!--    <label class="radio-inline"><input type="radio" value="question" name="option">Only Question</label> -->
  <label class="radio-inline"><input type="radio" value="info" name="option">Only Information</label>

<video width="1000" height="500" controls style="display: block;margin: auto;">
  <source id="videoSrc" src=<s:property value="videoUrl"/> type="video/mp4">
  <track id="videoTrack" src="vtt_files/<s:property value="subTitleName"/>" kind="subtitles" srclang="en" label="English" default/>
</video>
<img src="Images/qr_code.jpg" class="img-rounded"  width="100" height="100" style="display: block;margin: auto;"/>
<s:hidden  name="videoUrl" id="videoUrl" />
<s:hidden  name="subtitleName" id="subtitleName" />
<s:hidden  name="headLine" id="headLine" />
<div id="asdf"></div><br>
<div>The timing of the information boxes is currently fixed. <br>
        Our goal is to show them at the exact time that a person appears or a location is mentioned. This is shown in:</div>
        <a href='<s:url action="annoteted_videos"  includeContext="true">
	     </s:url>'>Annoteted Videos</a><br>

</div>
<script>
var videoUrl = document.getElementById("videoUrl").value;

//$("#videoSrc").attr("src","http://tv-download.dw.com/dwtv_video/flv/ke/ke20160610_dianekruger_sd_avc.mp4");


var subtitleName = document.getElementById("subtitleName").value;
var headLine = document.getElementById("headLine").value;
$("#videoTrack").attr("src","vtt_files/"+subtitleName+".vtt");
var trackElement = document.querySelector("track");
trackElement.addEventListener("load", function() {
 var textTrack = this.track; 
 textTrack.mode ="hidden";
 textTrack.oncuechange = function (){
	  var cue = this.activeCues[0];
		 // document.getElementById("asdf").innerHTML=cue.text;
		var jsonObj = jQuery.parseJSON(cue.text);
		var content_type=jsonObj.default_content.type;
		 var content;
		 var mainContent;
		  if(content_type=="questions" || content_type=="infos"){
				var content_number=jsonObj.default_content.number;
				document.getElementById("asdf").innerHTML="type :"+jsonObj.default_content.type+"  number: "+jsonObj.default_content.number;
			  content=jsonObj[content_type][content_number];
			  if(typeof content=='object'){
				 
			  }

			}
			else{
				content=jsonObj[content_type];

			}
		  mainContent={name:jsonObj.name,type:jsonObj.default_content.type,thumbnail:jsonObj.thumbnail,content:content};
		  $.post("chat1",
			        {
			          content:JSON.stringify(mainContent),
			        
	              
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