<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Annoteted Videos</title>
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
<div class="container-fluid">
<a href="timed_annoteted?videoUrl=http://tv-download.dw.com/dwtv_video/flv/vdt/2016/beng160613_001_qwiinduweb_01i_sd_avc.mp4&subTitleName=beng160613_001_qwiinduweb_01i_sd_avc.vtt">Brexit and German Company</a>  
<br>
<a href="timed_annoteted?videoUrl=http://tv-download.dw.com/dwtv_video/flv/wse/wse20160604_usa-latinos_sd_avc.mp4&subTitleName=wse20160604_usa-latinos_sd_avc.vtt">Latinos against Trump</a>
<br>
<a href="timed_annoteted?videoUrl=http://tv-download.dw.com/dwtv_video/flv/je/je20160424_merkel11g_sd_avc.mp4&subTitleName=je20160424_merkel11g_sd_avc.vtt">Merkel praises migration deal with Turkey</a>
<br>
<a href="timed_annoteted?videoUrl=http://tv-download.dw.com/dwtv_video/flv/je/je20160424_merkel11g_sd_avc.mp4&subTitleName=test.vtt">Test</a>
</div>

</body>
</html>