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
      <li><a><s:form method="get" action="annoteted_videos"> <input type="submit" class='btn btn-primary'  value="To overview"></s:form></a></li>
      <li><a><s:form method="get" action="logout"> <input type="submit" class='btn btn-primary'  value="logout"><s:hidden name="from" value="timed_annoteted"/></s:form></a><li>
     </ul>
  </div>
</nav>
<h3>Second Screen content for Video:  <s:property value="headLine" /></h3>
<div class="container-fluid">
<div id="content">
<form action="editor" id="editFrm" method="post" style='background-color: #fafafa;border-style: ridge; padding:10px;'>
Time: <input type="text" name="time" id="time">
<div id="textboxes">

</br></br>

</div>
 <input type="hidden" name="actionType" value="editDefault">
 <s:hidden  name="entityName" id="entityName" />
<s:hidden  name="documentNumber" id="documentNumber" />
<s:hidden  name="fileName" id="fileName" />
<textarea id="updateString"  form="editFrm" name="updateString" cols="100" rows="15"></textarea>
</br></br>
 <input type="submit" class='btn btn-primary'/>       
</form>

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
var default_content=jsonContent[documentNumber-1].default_content;
var default_type=default_content.type;
var time=jsonContent[documentNumber-1].time
$('#time').val(time)
if(default_type=="questions" || default_type=="infos" ){
	var default_number=default_content.number;
	var questionOrInfo=jsonContent[documentNumber-1][default_type]
	var updateString=questionOrInfo[default_number];
    if(default_type=="questions"){
    	$('#updateString').hide();
    	$( "#textboxes" ).append( "Question: <input type='text' id='question' name='question' size='40'/></br></br>" )
    	$('#question').val(updateString.question)
    	var i;
    	for(i=0;i<updateString.options.length;i++){
    		var j=i+1;
    		var correctAnswers=updateString.correct.split(',')
    		if(jQuery.inArray(updateString.options[i],correctAnswers)== -1){
    		$( "#textboxes" ).append( "Option"+j+": <input type='text' id='option"+j+"' class='option' name='option"+j+
    				"' size='40'/>&nbsp;&nbsp;<input type='checkbox' name='correct'/>&nbsp;Correct</br></br>")
    	    }
    	else{
    		$( "#textboxes" ).append( "Option"+j+": <input type='text' id='option"+j+"' class='option' name='option"+j+
    				"' size='40'/>&nbsp;&nbsp;<input type='checkbox' checked name='correct'/>&nbsp;Correct</br></br>" )
    	}
    		$('#option'+j+'').val(updateString.options[i])
    	    $('#option'+j+'').append("<input type='radio' name='correct'/>")
    		
    	    
    	    
    	}
    	
    	/*
    	$('#question').val(updateString.question)
    	$('#option1').val(updateString.options[0])
    	$('#option2').val(updateString.options[1])
    	$('#option3').val(updateString.options[2])
    	$('#option4').val(updateString.options[3])
    	$('#correct').val(updateString.correct)
       */
        $('#updateString').val(JSON.stringify(updateString))
    }
    else{
    $('#updateString').val(updateString)
    $('#textboxes').hide();
    }
}
else{
    $('#textboxes').hide();
	var updateDocument=jsonContent[documentNumber-1];
	var updateString=updateDocument[default_type];
	$('#updateString').val(updateString)
}

if(default_type=="questions"){
$(document).ready(function() {
$("#editFrm").submit(function() {
	var options=[];
	$('.option').each(function(){
		options.push('"'+$(this).val()+'"')
	});
	var correctAns =null;
	$(':checkbox:checked').each(function(i){
        if(correctAns==null)
        	correctAns = $(this).prev().val();
        else
        	correctAns =correctAns+','+ $(this).prev().val();
      });

	updateString='{"question":"'+$("#question").val()+'" , "correct": "'+correctAns+'", "options":['+options+']}';
	/*
	updateString='{"question":"'+$("#question").val()+'" , "correct": "'+$("#correct").val()+'", "options":["'+$("#option1").val()+'","'+$("#option2").val()+'","'+
	$("#option3").val()+'","'+$("#option4").val()+'"]	}';
	*/
	updateString=jQuery.parseJSON(updateString);
	 $('#updateString').val(JSON.stringify(updateString))
	
	});
	
});
}
</script>
</html>