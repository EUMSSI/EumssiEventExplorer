<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
     <%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
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
      <li><a><form action="logout"> <input type="submit" class='btn btn-primary'  value="logout"></form></a><li>
     
    </ul>
  </div>
</nav>
<div class="container-fluid">
Choose from the following. It will be reflected to the second screen with other contents. If you want both, do not do anything. 

</br>

  <label class="radio-inline"><input type="radio" value="both" name="option" checked="checked">Question and Info</label>
   <label class="radio-inline"><input type="radio" value="question" name="option">Only Question</label>
  <label class="radio-inline"><input type="radio" value="info" name="option">Only Information</label>

<video width="1000" height="500" controls style="display: block;margin: auto;">
  <source id="videoSrc" src=<s:property value="videoUrl"/> type="video/mp4">
  <track id="videoTrack" src="vtt_files/<s:property value="subTitleName"/>" kind="subtitles" srclang="en" label="English" default/>
</video>

</div>
<img src="Images/qr_code.jpg" class="img-rounded"  width="100" height="100" style="display: block;margin: auto;"/>
</body>
<script>
var trackElement = document.querySelector("track");
trackElement.addEventListener("load", function() {
 var textTrack = this.track; 
 textTrack.mode ="hidden";
 textTrack.oncuechange = function (){
	  var cue = this.activeCues[0];
	 // document.getElementById("asdf").innerHTML=cue.text;
	
	
	  $.post("chat1",
		        {
		          entityName: cue.text,
		          infoOrQues:$('input[name="option"]:checked').val()
                
		        })
	}
 

}
)
</script>
</html>