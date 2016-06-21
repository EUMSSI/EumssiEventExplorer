<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8"%>
    
    <%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
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
      <li><a><h3 style="font-family:serif;">Login</h3></a></li>
   
     </ul>
  </div>
</nav>
<s:hidden  name="videoUrl" id="videoUrl" />
 <div class="container-fluid">
<form id="login" action="video" method="get">
  Provide your user-id:<br>
  <input type="text" name="userId"><br>
  <s:hidden  name="videoUrl" id="videoUrl" />
  <s:hidden  name="subtitleName" id="subtitleName" />
  <input type="submit" value="Submit">
  </form>
</div>
</body>
</html>