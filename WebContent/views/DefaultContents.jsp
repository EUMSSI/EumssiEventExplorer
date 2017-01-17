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
      <li><a><h3 style="font-family:serif;">First Screen</h3></a></li>
      <li><a><s:form method="get" action="annotated_videos"> <input type="submit" class='btn btn-primary'  value="To overview"></s:form></a></li>
      <li><a><s:form method="get" action="logout"> <input type="submit" class='btn btn-primary'  value="logout"><s:hidden name="from" value="timed_annotated"/></s:form></a><li>
     
    </ul>
  </div>
</nav>
<h3>Second Screen content for Video:  <s:property value="headLine" /></h3>
<div class="container-fluid">
<div id="content">


</div>
</div>
<s:hidden  name="contents" id="contents" />
<s:hidden  name="fileName" id="fileName" />
</body>
<script>
var contents=document.getElementById("contents").value;
var fileName=document.getElementById("fileName").value;
var jsonContent=jQuery.parseJSON(contents);
var i;
var content;
var documentNumber;
for(i=0;i<jsonContent.length;i++){
	var name=jsonContent[i].name;
	if(!jsonContent[i].hasOwnProperty("default_content"))
		continue;
	var thumbnail=jsonContent[i].thumbnail;
	var time=jsonContent[i].time;
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
		 if(optionList==null){
			 if(options[j].toLowerCase()==correct || options[j]==correct)
				 optionList="<li><strong>"+options[j]+"</strong>";
		     else	 
			     optionList="<li>"+options[j];
		 }
	     else{
	    	 if(options[j].toLowerCase()==correct || options[j]==correct)
				 optionList+="<li><strong>"+options[j]+"</strong>";
		     else
		         optionList+="<li>"+options[j];
	     }
	 } 
	 if(thumbnail!=null)
	content="<img src="+thumbnail+"  style='width:50px;height:50px;'> <strong>"+name+" </strong> <br>"+question+"<br>"+optionList+"<br>Correct: "+correct;
	else
		content="<strong>"+name+" </strong> <br>"+question+"<br>"+optionList+"<br>Correct: "+correct;
		
 }
 else if(default_content=="infos"){
	 var infoArray=jsonContent[i][default_content];
	 var infoNumber=jsonContent[i].default_content.number;
	 var info=infoArray[infoNumber];
	 if(thumbnail!=null)
	 content="<img src="+thumbnail+"  style='width:50px;height:50px;'> <strong>"+name+" </strong><br>"+info;
	 else
		 content="<strong>"+name+" </strong><br>"+info;
 }
 else{
	 if(thumbnail!=null)
	 content="<img src="+thumbnail+"  style='width:50px;height:60px;'><strong> "+name+" </strong><br>"+jsonContent[i][default_content];
	 else
		 content="<strong> "+name+" </strong><br>"+jsonContent[i][default_content];
 }
   
 documentNumber=i+1;
 $("#content").append("<div style='background-color: #fafafa;border-style: ridge;' class='row' style='border-style: ridge;'><div style='padding:10px;' class='col-md-8'>At: "+time
 +"<br>"+content+"</div><div style='padding:10px;' class='col-md-4'><a style='float:right' href=editor?fileName="+fileName+"&entityName="+name+"&actionType=changeDefaultRequest&documentNumber="+documentNumber
		 +" class='btn btn-primary' role='button'>Change Contents</a><br><br><a name='delete' style='float:right' href=editor?fileName="+fileName+"&entityName="+name+"&actionType=delete&documentNumber="+documentNumber
		 +" class='btn btn-primary' role='button'>Delete Entity</a></div></div>")
 $("#content").append("<br>");
 

}
$('a[data-content-type="map"]').hide();
$("body").on("click",'[name="delete"]', function(){
	
	return confirm("Are you sure, you want to delete this entity?")
});	    
</script>
</html>