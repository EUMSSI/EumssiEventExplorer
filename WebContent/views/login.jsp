<%@ page language="java" contentType="text/html; charset=UTF-8"
     pageEncoding="UTF-8"%>
    
    <%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="scripts/bower_components/jquery/dist/jquery.js"> </script>
</head>
<body>
<s:hidden  name="videoUrl" id="videoUrl" />
<form id="login" action="video" method="get">
  Provide your user-id:<br>
  <input type="text" name="userId"><br>
  <s:hidden  name="videoUrl" id="videoUrl" />
  <s:hidden  name="subtitleName" id="subtitleName" />
  <input type="submit" value="Submit">
  </form>

</body>
</html>