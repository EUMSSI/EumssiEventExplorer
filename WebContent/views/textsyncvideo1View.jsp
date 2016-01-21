<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="/struts-tags" prefix="s"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <title>Amalia.js</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta http-equiv="pragma" content="no-cache">
        <meta http-equiv="expires" content="-1">
        <link rel="icon" href="images/favicon.ico"> 
        <link href="CSS/default.css" rel="stylesheet">
        <script src="scripts/bower_components/jquery/dist/jquery.js"></script>
        <script src="scripts/bower_components/jquery-ui/jquery-ui.min.js"></script>        
        <script src="scripts/bower_components/raphael/raphael.js"></script>
        <!-- style-player -->        
        <link href="scripts/css/amalia.js.min.css" rel="stylesheet">
        <!-- /style-player -->        
        <!-- script-player -->        
        <script src="scripts/js/amalia.js.min.js"></script>
        <script src="scripts/js/amalia.js-plugin-text-sync.min.js"></script>        
        <!-- /script-player -->
    </head>
    <body style="background-color:#e6e6e6;">
        <div class="container">
            <div class="header">
            <img height="108" width="300" src="Images/eumssi-logo.png">
                <h1>Second Screen Demo</h1>  
            </div>
            <div class="content">           
                <div class="demo">
                    <div style=" clear: both;">

                        <div style="width: 50%;float: left;">
                            <div style="height: 350px;">
                                <div id="defaultPlayer"></div>
                            </div>
 <!--                           <div>
                                <pre class="config">
{
    'className' : 'fr.ina.amalia.player.plugins.TextSyncPlugin',
    'container' : '#myplayer-tsync-tsync',
    'parameters' : {
        metadataId : 'text-amalia01',
        title : 'My title',
        description : 'A description I may have to put here',
        level : 1,
        displayLevel : 1,
        scrollAuto : true
    }
}                 
                                </pre>
                            </div> -->
                        </div>
                        <div style="width: 50%; float: left;">
                            <div id="text_sync_plugin" style="height: 500px;"></div>
                        </div>
                    </div>
<s:hidden  name="jsonFileName" id="jsonFileName" />
<s:hidden  name="videoUrl" id="videoUrl" />

                    <script>
                 
                        $( function () {
                        	 var fileName = document.getElementById("jsonFileName").value;
                        	 var videoUrl ="http://tv-download.dw.com/dwtv_video/flv/je/je20150401_frack15c_sd_avc.mp4";
                            $( "#defaultPlayer" ).mediaPlayer( {
                                autoplay : true,
                                src : videoUrl,
                                controlBar :
                                    {
                                        sticky : true
                                    },
                                plugins : {
                                    dataServices : [
                                        'scripts/'+fileName+'.json'
                                    ],
                                    list : [
                                        {
                                            'className' : 'fr.ina.amalia.player.plugins.TextSyncPlugin',
                                            'container' : '#text_sync_plugin',
                                            'parameters' : {
                                                metadataId : 'text-amalia01',
                                                title : 'Information Sidebar',
                                                description : 'Some Informations about the entites of this video',
                                                level : 1,
                                                displayLevel : 1,
                                                scrollAuto : true
                                            }
                                        }
                                    ]
                                }
                            } );
                        } );
                        
                       
                    </script>

                </div>
            </div>
            
        </div>
        <script>
       
        
        setTimeout(function(){$('p.text').each(function(){
        	var correctAns=0;
            var $this = $(this);
            var t = $this.text();
            $this.html(t.replace("&lt;br&gt;","<br>"));
           
        }); 
    	var correctAns=0;
        $("input[type='button']").click(function() {
        	
        	var selected = $(this).siblings("input[type='radio']:checked");
        	if (selected.length > 0) {
        	    selectedVal = selected.val();
        	}

        	if(selected.val()==selected.attr('name')){
        	$(this).val("Correct");
        	$(this).after("<img src=Images/tik.png>");
        	correctAns++;
            $('.correct').html("<strong>Correct Answers:"+correctAns+"</strong>");
        }
        	else{
        	$(this).val("False");
        	$(this).after("<img src=Images/cross.png>");
        }
        	$(this).attr("disabled",true);
        	});
        var questionCounter = $(':button').length;
        $('.quize').html("<strong>Number of Questions:"+questionCounter+"</strong>");
       
        
        }, 2500);
        
       
        
       
      

        	
      
        
        </script>
            <div class="quize"></div>
         <div class="correct"></div>
   <!--       
        <p>Entities of this video</p>
       <ul>
        <s:iterator value="entities" begin="1" >
        <li>
        <a href="https://en.wikipedia.org/wiki/<s:property/>" target="_blank"> <s:property/></a>
        </li>
      </s:iterator>
      </ul>
       -->
    </body>
</html>
