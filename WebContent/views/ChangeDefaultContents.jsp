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
<s:hidden  name="entityName" id="entityName" />
<s:hidden  name="documentNumber" id="documentNumber" />
<s:hidden  name="fileName" id="fileName" />
</body>
<script>
var contents=document.getElementById("contents").value;
var entityName=document.getElementById("entityName").value;
var documentNumber=document.getElementById("documentNumber").value;
var fileName=document.getElementById("fileName").value;
var jsonContent=jQuery.parseJSON(contents);
var i;
var content;
var default_type;
var default_number;
for(i=0;i<jsonContent.length;i++){

	var thumbnail=jsonContent[i].thumbnail;
	if(jsonContent[i].name==entityName && documentNumber-1==i){
		
			default_type=jsonContent[i].default_content.type;
			if(default_type=='questions' || default_type=='infos')
				default_number=jsonContent[i].default_content.number;
			
		$.each(jsonContent[i], function(key, value){
			if(key=="default_content"){
				default_type=value.type;
				if(value.type=='questions' || value.type=='questions')
					default_number=value.number;
			}
			if(key!="thumbnail" && key!="name" && key!="default_content" && key!="time"){
			  if(key=="questions"){
				  var j;
				  var length=jsonContent[i].questions.length;
				 for(j=0;j<jsonContent[i].questions.length;j++){
					     var questionArray=jsonContent[i].questions;
						 var questionObject=questionArray[j];

						 var question=questionObject.question;

						 var options=questionObject.options;
						 var correct=questionObject.correct;
						 
						 var k;
						 var optionList=null;
						 for(k=0;k<options.length;k++){
							 if(optionList==null){
								 if(options[k].toLowerCase()==correct || options[k]==correct)
									 optionList="<li><strong>"+options[k]+"</strong>";
							     else	 
								     optionList="<li>"+options[k];
							 }
						     else{
						    	 if(options[k].toLowerCase()==correct || options[k]==correct)
									 optionList+="<li><strong>"+options[k]+"</strong>";
							     else
							         optionList+="<li>"+options[k];
						     }
						 } 
						 var radioButton;
						 var editButton;
						 var deleteButton;
						 if(default_type=='questions'){
							 if(default_number==j){
								 radioButton='<input style="float:left" checked value={"type":"questions","number":"'+j+'"} type="radio" name="optradio">';
							     editButton='<a style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=editContentRequest&fileName='+fileName+
							 		'&editWhat={"type":"questions","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Edit</a>';
							 		
							     deleteButton='<a name="delete" style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=deleteContent&fileName='+fileName+
							 		'&deleteWhat={"type":"questions","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Delete</a>';
							 }
							  else{
								  radioButton= '<input style="float:left" value={"type":"questions","number":"'+j+'"} type="radio" name="optradio">';
								  editButton='<a  style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=editContentRequest&fileName='+fileName+
							 		'&editWhat={"type":"questions","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Edit</a>';
							 		
								  deleteButton='<a name="delete" style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=deleteContent&fileName='+fileName+
							 		'&deleteWhat={"type":"questions","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Delete</a>';
							  }
						}
						 else{
							 radioButton= '<input style="float:left" value={"type":"questions","number":"'+j+'"} type="radio" name="optradio">';
							 editButton='<a style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=editContentRequest&fileName='+fileName+
						 		'&editWhat={"type":"questions","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Edit</a>';
							 deleteButton='<a name="delete" style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=deleteContent&fileName='+fileName+
						 		'&deleteWhat={"type":"questions","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Delete</a>';
						 }
						 var questionNumber=j+1;
						content="<strong>Question:"+" "+ questionNumber +"</strong><br>"+question+"<br>"+optionList; 
						 $("#content").append("<div class='row' style='background-color: #fafafa;border-style: ridge;'><div style='padding:10px;' class='col-md-8'>"+
								 content+"<br><br>Select"+radioButton+"<br>"+editButton+"<br><br>"+deleteButton+"</div><div style='padding:10px;' class='col-md-4'></div></div>")
								 $("#content").append("<br><br>")
			     }
			  }
				
			  else if(key=="infos"){
					 var infoArray=jsonContent[i].infos;
					 for(j=0;j<jsonContent[i].infos.length;j++){
					 var info=infoArray[j];
					 var infoNumber=j+1;
					 if(default_type=='infos'){
						 if(default_number==j){
							 radioButton= '<input style="float:left" checked value={"type":"infos","number":"'+j+'"} type="radio" name="optradio">';
							 editButton='<a style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=editContentRequest&fileName='+fileName+
						 		'&editWhat={"type":"infos","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Edit</a>';
							 deleteButton='<a name="delete" style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=deleteContent&fileName='+fileName+
						 		'&deleteWhat={"type":"infos","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Delete</a>';
						 }
						  else{
							  radioButton= '<input style="float:left" value={"type":"infos","number":"'+j+'"} type="radio" name="optradio">';
							  editButton='<a style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=editContentRequest&fileName='+fileName+
						 		'&editWhat={"type":"infos","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Edit</a>';
							  deleteButton='<a name="delete" style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=deleteContent&fileName='+fileName+
						 		'&deleteWhat={"type":"infos","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Delete</a>';
						  }
					 }
					 else{
						 radioButton= '<input style="float:left" value={"type":"infos","number":"'+j+'"} type="radio" name="optradio">';
						 editButton='<a style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=editContentRequest&fileName='+fileName+
					 		'&editWhat={"type":"infos","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Edit</a>';
						 deleteButton='<a name="delete" style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=deleteContent&fileName='+fileName+
					 		'&deleteWhat={"type":"infos","number":"'+j+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Delete</a>';
					 }
					 content="<strong>Info: "+infoNumber+"</strong> <br>"+info;
					 $("#content").append("<div class='row' style='background-color:#fafafa; border-style: ridge;'><div style='padding:10px;' class='col-md-8'>"+
							 content+"<br><br>Select"+radioButton+"<br>"+editButton+"<br><br>"+deleteButton+"</div><divstyle='padding:10px;' class='col-md-4'></div></div>")
							 $("#content").append("<br><br>")
					 }
			    }
			  else {
				  if(default_type==key){
					  radioButton= '<input style="float:left" value={"type":"'+key+'"} checked type="radio" name="optradio">'; 
					  editButton='<a style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=editContentRequest&fileName='+fileName+
				 		'&editWhat={"type":"'+key+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Edit</a>';
					  deleteButton='<a name="delete" style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=deleteContent&fileName='+fileName+
				 		'&deleteWhat={"type":"'+key+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Delete</a>';
				  } 
				  else{
					  
					  radioButton= '<input style="float:left" value={"type":"'+key+'"} type="radio" name="optradio">';
					  editButton='<a style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=editContentRequest&fileName='+fileName+
				 		'&editWhat={"type":"'+key+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Edit</a>';
					  deleteButton='<a name="delete" style="float:left" href=editor?documentNumber='+documentNumber+'&actionType=deleteContent&fileName='+fileName+
				 		'&deleteWhat={"type":"'+key+'"}&entityName='+entityName+' class="btn btn-primary" role="button">Delete</a>';
				  }
				 content="<strong>"+key +"</strong> <br>"+value;
			     $("#content").append("<div class='row' style='background-color: #fafafa; border-style: ridge;'><div style='padding:10px;' class='col-md-8'>"+
							 content+"<br><br>Select"+radioButton+"<br>"+editButton+"<br><br>"+deleteButton+"</div><div style='padding:10px;' class='col-md-4'></div></div>")
							 $("#content").append("<br><br>")
			  }
			}
		})
	 break; 
	}
		
}
var checked_value=$('input[name="optradio"]:checked').val();
$("#content").append("<a id='addQuestion' style='float:left' href=addNew?documentNumber="+documentNumber+"&entityName="+entityName+
		"&actionType=addQuestion&fileName="+fileName+" class='btn btn-primary' role='button'>Add Question</a><br><br>")

$("#content").append("&nbsp;&nbsp;")

$("#content").append("<a id='addInfo' style='float:left' href=addNew?documentNumber="+documentNumber+"&entityName="+entityName+
		"&actionType=addInfo&fileName="+fileName+" class='btn btn-primary' role='button'>Add Info</a><br><br>")

$("#content").append("<a id='save' style='float:left' href=editor?documentNumber="+documentNumber+"&actionType=changeDefault&fileName="+fileName+
		"&updateString="+checked_value+" class='btn btn-primary' role='button'>Save</a>")

$('input:radio[name="optradio"]').on('click',
	    function(){
	    	checked_value=$('input[name="optradio"]:checked').val();
	    	var href="editor?documentNumber="+documentNumber+"&actionType=changeDefault&fileName="+fileName+"&updateString="+checked_value
	    	$('#save').attr("href", href);
	    });
	    
$("body").on("click",'[name="delete"]', function(){
	
	return confirm("Are you sure, you want to delete this content?")
});	    
</script>
</html>