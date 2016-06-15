<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.2/jquery.min.js"></script>
       
    <script type="text/javascript" src="http://cdnjs.cloudflare.com/ajax/libs/d3/3.5.6/d3.js"></script>
    <script src="scripts/lib/d3/d3.layout.cloud.js"></script>
    <script src="scripts/lib/d3/nv/nv.d3.min.js"></script>
    <link href="scripts/lib/d3/nv/nv.d3.min.css" rel="stylesheet">
    <script src="scripts/lib/underscore/underscore-min.js"></script>
    <script src="scripts/js/Utils.js"></script>
    <script src="scripts/js/EventManager.js"></script>
    <script src="scripts/js/FilterManager.js"></script>
    <script src="scripts/js/eumssiSolr.js"></script>
    <script src="scripts/lib/ajax-solr/Core.js"></script>
    <script src="scripts/lib/ajax-solr/AbstractManager.js"></script>
    <script src="scripts/lib/ajax-solr/Parameter.js"></script>
    <script src="scripts/lib/ajax-solr/ParameterStore.js"></script>
    <script src="scripts/lib/ajax-solr/AbstractWidget.js"></script>
    <script src="scripts/lib/ajax-solr/AbstractFacetWidget.js"></script>
    <script src="scripts/lib/ajax-solr/AbstractTextWidget.js"></script>
     <script src="scripts/js/Manager.jquery.js"></script>
    <script src="scripts/js/widgets/GenericGraphWidget.js"></script>
    
</head>
<body>

<br/>
<!-- 
<form id="videoList">
<s:iterator value="videoMap" status="statusVar">
  <input type="button" name="videoName" value=<s:property value="key"/> /> <br /><br />
  </s:iterator>
</form>
 -->
<br />

<div id="second_screen_content"></div>
<form action="logout">
  <br>
  <input type="submit" value="logout">
  </form>
</body>
<script>
	 $.post("chat1",
			    {
			        requestType: "second_screen"
			        
			    },
			    function(data, status){
			    	if(data!=""){
			    		
			    		$("#second_screen_content").html(data);
			    		$.get(this);
			    	
			    			
			    		}
			    	
			    });
	


</script>
</html>