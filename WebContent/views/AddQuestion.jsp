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
<form method="post" action="editor" id="editFrm">
Question:<input type="text" style="width: 300px;" id='question'/>
<br><br>
Option1:<input type="text" id='option1' style="width: 300px;" class='option'/>&nbsp;<input type='radio' name='correct'/>&nbsp;Correct
<br><br>
Option2:<input type="text" id='option2' style="width: 300px;" class='option'/>&nbsp;<input type='radio' name='correct'/>&nbsp;Correct
<br><br>
Option3:<input type="text" id='option3' style="width: 300px;" class='option'/>&nbsp;<input type='radio' name='correct'/>&nbsp;Correct
<br><br>
Option4:<input type="text" id='option4' style="width: 300px;" class='option'/>&nbsp;<input type='radio' name='correct'/>&nbsp;Correct
<br><br>
<textarea id="updateString"  form="editFrm" name="updateString" cols="100" rows="15"></textarea>
 <input type="hidden" name="actionType" value="addQuestion">
<s:hidden  name="documentNumber" id="documentNumber" />
<s:hidden  name="fileName" id="fileName" />
<s:hidden  name="entityName" id="entityName" />
<input type="submit"/>
</form>
</div>
</div>
</body>
<script>
$("#updateString").hide();
$("#editFrm").submit(function() {
	var options=[];
	$('.option').each(function(){
		options.push('"'+$(this).val()+'"')
	});
var correctAns=$("input[type='radio'][name='correct']:checked").prev().val();
var updateString='{"question":"'+$("#question").val()+'" , "correct": "'+correctAns+'", "options":['+options+']}';
/*
updateString='{"question":"'+$("#question").val()+'" , "correct": "'+$("#correct").val()+'", "options":["'+$("#option1").val()+'","'+$("#option2").val()+'","'+
$("#option3").val()+'","'+$("#option4").val()+'"]	}';
*/
updateString=jQuery.parseJSON(updateString);
 $('#updateString').val(JSON.stringify(updateString))
});
</script>
</html>