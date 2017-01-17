<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>EUMSSI Second Screen</title>
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
        <!-- Link Swiper's CSS -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/Swiper/3.3.1/css/swiper.min.css">
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
        <!-- Swiper JS -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Swiper/3.3.1/js/swiper.min.js"></script>
    
     <style>
    html, body {
        position: relative;
        height: 100%;
    }
    body {
        background: #eee;
        font-family: Helvetica Neue, Helvetica, Arial, sans-serif;
        font-size: 14px;
        color:#000;
        margin: 0;
        padding: 0;
    }
    
    .swiper-container {
        width: 100%;
        height: 100%;
    }
    .swiper-slide{
    margin-left:0.5cm;
    }
    .custom-pagination{
     cursor: pointer;
    }
   /* 
    .swiper-slide {
        text-align: center;
        font-size: 18px;
        background: #fff;
        
        /* Center slide text vertically */
        display: -webkit-box;
        display: -ms-flexbox;
        display: -webkit-flex;
        display: flex;
   
        -webkit-box-pack: center;
        -ms-flex-pack: center;
        -webkit-justify-content: center;
        justify-content: center;
        -webkit-box-align: center;
        -ms-flex-align: center;
        -webkit-align-items: center;
        align-items: center;
        */
    }
    
    </style>

</head>
<body>
<nav class="navbar navbar-default">
  <div class="container-fluid">
     <ul class="nav navbar-nav">
      <li><a><img src="Images/eumssi_logo.png"><br>
      <h3 style="font-family:serif;">Hello <s:property value="userId"/></h3></a></li>
      <li><a><h4 style="font-family:serif;">Second Screen</h4></a></li>
        <li><a><div id="correct"></div></a></li>
        <li><a><div id="wrong"></div></a></li>
       <li >
       <a>
    <form action="logout">
        <input type="submit" class='btn btn-primary' value="logout">
        <s:hidden name="from" value="second_screen"/>
    </form></a>
    </li>

    </ul>
  </div>
</nav>
  <div class="container-fluid">
<div id="second_screen_content">


</div>
</div>

  <div class="swiper-container">
        <div class="swiper-wrapper"></div>
        <!-- Add Pagination -->
        <div class="swiper-pagination"></div>
   
  </div>
 
<div id="interval"></div>
</body>
<script>
//function interval() {
	
	var swiper = new Swiper('.swiper-container', {
        pagination: '.swiper-pagination',
        paginationClickable: true,
        direction: 'vertical',
     /*
        paginationBulletRender: function (swiper,index, className) {
            return '<span class="swiper-pagination-bullet">&nbsp;'+swiper+'</span>';
    //        alert(swiper)
        	//return '<span>&nbsp;' + (index + 1) + '</span>';
        }
	
  */ 
      
    });
    
	 $.post("chat1",
			    {
			        requestType: "second_screen"
			        
			    },
			    function(data, status){
			    	if(data!=""){
			    		data=jQuery.parseJSON(data);
			    		if(data.thumbnail!=null){
			    		var thumbnail="<img src="+data.thumbnail+" height='50' width='50'><br>";
			    		}
			    		
			    		var name=data.name;
			    		var content=data.content;
			    		var html;
			    		
			    		if(data.type=='questions'){
			    			name="<img src=Images" + "//" + "quiz.png><strong>" + data.name + 
			    			"</strong>&nbsp;<a href='https://en.wikipedia.org/wiki/"+data.name+"'><img src='http://image.flaticon.com/icons/svg/25/25284.svg' width='20px' height='20px'/></a><br>";
			    			var question=content.question;
				    		var options=content.options;
				    		var correct=content.correct;
				    		var correctAnsArray=correct.split(",")
				    		var ansButtonOption=null;
				    		
				    		if(correctAnsArray.length==1){
				    			var i;
				    			for(i=0;i<options.length;i++){
				    				if(ansButtonOption==null){
				    					ansButtonOption="<br><label class='btn btn-primary'><input type='radio' name=\'"
											+ correct + "\' value=\'" + options[i] + "\' class='options'>" + options[i]+"</label><br>";
				    				}
									else{
										ansButtonOption+="<br><label class='btn btn-primary'><input type='radio' name=\'"
											+ correct + "\' value=\'" + options[i] + "\'class='options'>" + options[i]+"</label><br>";
									}
				    			} 
				    		}
				    		/*
				    		else{
				    			var i;
				    			for(i=0;i<options.length;i++){
				    				if(radioOrCheckbox==null){
				    					radioOrCheckbox="<br><input type='checkbox' name=\'"
											+ correct + "\' value=\'" + options[i] + "\'><span>" + options[i]+"</span>";
				    				}
				    				else{
				    				radioOrCheckbox+="<br><input type='checkbox' name=\'"
									+ correct +"\' id=\'" + options[i]+ "\' value=\'" + options[i] + "\'><span>" + options[i]+"</span>";
				    				}
				    			}
				    			radioOrCheckbox+="<input type='hidden' value=\'" + correct + "\'>";
				    		}
				    		*/
				    		
				    		html=name+thumbnail+"<div class='btn-group' data-toggle='buttons'>"+question+ansButtonOption+"</div>";
			    		}
			    		else if(data.type=='infos'){
			    			name="<img src=Images" + "//" + "Info.png><strong>" + 
			    			data.name + "</strong>&nbsp;<a href='https://en.wikipedia.org/wiki/"+
			    			data.name+"'><img src='http://image.flaticon.com/icons/svg/25/25284.svg' width='20px' height='20px'/></a><br>";
			    			if(thumbnail!=null)
				    			html=name+thumbnail+content;
				    			else
				    				html=name+content
			    		}
			    		else{
			    			name="<strong>" + data.name + "</strong>&nbsp;<a href='https://en.wikipedia.org/wiki/"+data.name+"'><img src='http://image.flaticon.com/icons/svg/25/25284.svg' width='20px' height='20px'/></a><br>";
			    			if(thumbnail!=null)
			    			html=name+thumbnail+content;
			    			else
			    				html=name+content;	
			    		}
			    		if(swiper.isEnd){
			    		//	var inside=$("div.swiper-pagination-clickable").html();
			    		//	inside=inside.replace("swiper-pagination-bullet-active","");
			    		//	$( "div.swiper-pagination-clickable").empty();
			    			swiper.appendSlide("<div class='swiper-slide'><img class='fade_star' src='Images/fade-star.png'style='width:16px;height:16px;'><br>"+html+"</div>")
			        	//	$( "div span.swiper-pagination-bullet").html();
			    		//	$( "div.swiper-pagination-clickable").last().prepend(inside);
			
			        		swiper.slideNext();
			            }
			        	else{
			        		swiper.appendSlide("<div class='swiper-slide'><img class='fade_star' src='Images/fade-star.png'style='width:16px;height:16px;'><br>"+html+"</div>")
			        		$( "div span.swiper-pagination-bullet").last().html(data.name);
			        	}
			    		
			    		 $.get(this);	
			    		}
			    	else{
			    		 $.get(this);
			    	}
			      
			    });
	 
//}
//interval();

$(document).on("click",".fade_star", function () {
	$(this).attr("src","Images/light-star.png");
});



 	var correctAns=0;
 	var wrongAns=0;
    $(document.body).on("change","input.options",function() {
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
        var correctArray=correctans.split(",");
        $("input[type='checkbox']").each(function(){
        	if(jQuery.inArray($(this).val(),correctArray) !== -1){
     		   var span = $(this).next();
     		   span.css({"background":"#286090","font-weight":"bold"});
     		   
     	   }
     		
     	});
        
      
    	}
       // $(this).attr("disabled",true);
       
    	}
    	
    	else
    	{
    	var selected = $("input[type='radio']:checked");
    	
    	if (selected.length > 0) {
    	    selectedVal = selected.val();
    	
        
    	if(selected.val()==selected.attr('name')){
        correctAns++;
        $('#correct').html("<img src=Images/tik.png>&nbsp;<strong>"+correctAns+"</strong>");
        selected.parent().attr("class","btn btn-success")
    }
    	else{
         wrongAns++;
  	     $('#wrong').html("<img src=Images/cross.png>&nbsp;<strong>"+wrongAns+"</strong>");
  	     selected.parent().attr("class","btn btn-danger")
    	$("input[type='radio']").each(function(){
    	   if($(this).val()==$(this).attr("name")){
    		   var label = $(this).parent();
    		   label.attr("class","btn btn-warning")
    		   
    	   }
    		
    	});

    }
    	
    	// $(document).find('input.options').attr("disabled",true);
    	 $(this).parent().siblings().children().remove()
    	 $(this).remove()
    
    	 
    	}	
    	}
    	});
    
</script>
</html>