<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="scripts/bower_components/jquery/dist/jquery.js"> </script>
</head>
<body>
<div id="content"></div>

</body>
<script>
var interval = 1000;  // 1000 = 1 second, 3000 = 3 seconds
function doAjax() {
	$.get("content", function(data, status){
		if(data!="")
		$("#content").html(data);
    });
}
setInterval(doAjax, interval);

</script>
</html>