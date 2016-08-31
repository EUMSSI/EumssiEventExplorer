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
      <li><a><img src="Images/eumssi_logo.png"><br>
      <h3 style="font-family:serif;">Hello <s:property value="userId"/></h3> </a></li>
      <li><a><h3 style="font-family:serif;">Annoted Videos</h3></a></li>
      <li><a><button type="button" class='btn btn-primary'  id="close">Close</button></a></li>
      <li><a><s:form method="get" action="logout"> <input type="submit" class='btn btn-primary'  value="logout"><s:hidden name="from" value="timed_annoteted"/></s:form></a><li>
     </ul>
  </div>
</nav>
<h3>Second Screen content for Video:  <s:property value="headLine" /></h3>
<div class="container-fluid">
<div id="content">

</div>
</div>
<s:hidden  name="defaultContents" id="defaultContents" />
</body>
<script>
var defaultContents=document.getElementById("defaultContents").value;
var jsonContent=jQuery.parseJSON(defaultContents);
var i;
var content;
for(i=0;i<jsonContent.length;i++){
	var name=jsonContent[i].name;
	var default_content=jsonContent[i].default_content.type;
//	$("#content").append("<div class="col-md-8"><li>"+jsonContent[i][default_content]+"</li></div>").css({"width":"70%","float":"left"})
 if(default_content=="questions"){
	 var questionArray=jsonContent[i][default_content];
	 var questionNumber=jsonContent[i].default_content.number;
	 var questionObject=questionArray[questionNumber];

	 var question=questionObject.question;

	 var options=questionObject.options;
	 var correct=questionObject.correct;
	 
	 var j;
	 var optionList=null;
	 for(j=0;j<options.length;j++){
		 if(optionList==null)
			 optionList="<li>"+options[j];
	     else
		     optionList+="<li>"+options[j];
	 } 
	content="Entity name: "+name+"   Default: "+default_content +"<br>"+question+"<br>"+optionList+"<br>Correct: "+correct; 
 }
 else if(default_content=="infos"){
	 var infoArray=jsonContent[i][default_content];
	 var infoNumber=jsonContent[i].default_content.number;
	 var info=infoArray[infoNumber];
	 content="Entity name: "+name+"   Default: "+default_content +"<br>"+info;
 }
 else{
	 content="Entity name: "+name+"   Default: "+default_content +"<br>"+jsonContent[i][default_content];
 }
   
 $("#content").append("<div class='row' style='border-style: ridge;'><div class='col-md-8'>"+
 content+"</div><div class='col-md-4'><button type='button' class='btn btn-primary' style='float:right'>Change</button></div></div>")
 $("#content").append("<br><br>")
}
</script>
</html>