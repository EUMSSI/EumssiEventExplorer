<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
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
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
</head>
<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
     <ul class="nav navbar-nav">
      <li><a><img src="Images/eumssi_logo.png"></a></li>
      <li><a><h4 style="font-family:serif;">Second Screen</h4></a></li>
        <li><a><div id="correct"></div></a></li>
        <li><a><div id="wrong"></div></a></li>
       <li >
       <a>
    <form action="logout">
        <input type="submit" class='btn btn-primary' value="logout">
    </form></a>
    </li>

    </ul>
  </div>
</nav>
  <div class="container-fluid">
<div id="second_screen_content"></div>
</div>
<div id="interval"></div>
</body>
<script>
//function interval() {
	 $.post("chat1",
			    {
			        requestType: "second_screen"
			        
			    },
			    function(data, status){
			    	if(data!=""){
			    		
			    		$("#second_screen_content").html(data);
			    		
			    		
			    		 $.get(this);	
			    		}
			    	else{
			    		 $.get(this);
			    	}
			      
			    });
	 
//}
//interval();
 	var correctAns=0;
 	var wrongAns=0;
    $(document.body).on("click","#check",function() {
    	var selected = $(this).siblings("input[type='hidden']");
    	if($(selected).is(':hidden')){
    	var correctans=selected.val();
        if( $(this).siblings("input[type='checkbox']:checked").map(function(i,v) { return v.id; }).get().join(',') == correctans ) {
        	$(this).after("<img src=Images/tik.png>");
        	correctAns++;
            $('#correct').html("<img src=Images/tik.png>&nbsp;<strong>"+correctAns+"</strong>");
        }
        else{
        	$(this).after("<img src=Images/cross.png>");
        wrongAns++;
        $('#wrong').html("<img src=Images/cross.png>&nbsp;<strong>"+wrongAns+"</strong>");
    	}
        $(this).attr("disabled",true);
        
    	}
    	
    	else
    	{
    	var selected = $(this).siblings("input[type='radio']:checked");
    	
    	
    
    	if (selected.length > 0) {
    	    selectedVal = selected.val();
    	}

    	if(selected.val()==selected.attr('name')){
    	$(this).val("Correct");
    	$(this).after("<img src=Images/tik.png>");
    	correctAns++;
        $('#correct').html("<img src=Images/tik.png>&nbsp;<strong>"+correctAns+"</strong>");
    }
    	else{
    	$(this).val("False");
    	$(this).after("<img src=Images/cross.png>");
    	wrongAns++;
    	$('#wrong').html("<img src=Images/cross.png>&nbsp;<strong>"+wrongAns+"</strong>");
    }
    	$(this).attr("disabled",true);
    	
    	}
    	});
    
</script>
</html>