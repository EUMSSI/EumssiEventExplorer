package de.l3s.eumssi.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Editor {
private String headLine;
private JSONArray contents=new JSONArray();
private String fileName;
private String userId;
private String actionType;
private String entityName;
private String updateString;
private String documentNumber;
private String time;
private String editWhat;
private String deleteWhat;
HttpServletRequest request = ServletActionContext.getRequest();	
HttpSession session = request.getSession(false);

public String execute() throws IOException, java.text.ParseException{
	session = request.getSession(true);
	userId=(String) session.getAttribute("userId");
	int documentNumberInt;
	
	//actionType=changeDefault means change and save the new default request in file	
		 if(actionType.equals("changeDefault") || actionType.equals("delete")){
			 documentNumberInt=Integer.valueOf(documentNumber);
			Edit(documentNumberInt);
			getJsonContent();
			return "default_contents";
		}
		 else if(actionType.equals("addQuestion") ||actionType.equals("addInfo") || actionType.equals("editContent")|| actionType.equals("deleteContent") ){
			 documentNumberInt=Integer.valueOf(documentNumber);
				Edit(documentNumberInt);
				getJsonContent();
				return "change_default_request";
		 }
	 
	/*in else section 2 types of request will be handaled.
	 * 1. request for change the default. where all the possible contents of a particular entity will be shown to admin 
	 * 2. request for edit  the default. admin can edit the default.
	 * Here getJsonContent() will get all the contents a vtt file and save it as a json object in content variable.
	 * Two particular jsp file(ChangeDefaultContents.jsp and EditDefaultContents.jsp) dedicated for above two types of requests. 
	 * Both will parse the "content" json object and show desired output accoring to their
	 * responsibility and other parameters  
	 * Return type "default_contents" is for showing the DefaultContents.jsp page, where all the defaults contents of a particular 
	 * file is shown.
	 * Return type "change_default_request" and "edit_default_request" will show the "ChangeDefaultContents.jsp" and "EditDefaultContents.jsp"
	 * Based on there actionType, respective pages will be redirected.    
	 */
	else{
		getJsonContent();
         
          if(actionType.equals("changeDefaultRequest")){
  			System.out.println(entityName);
  			System.out.println(contents);
  	        return "change_default_request";
  		}
          else if(actionType.equals("editContentRequest")){
    			System.out.println(entityName);
      	        return "edit_content_request";
      		}
   
  }
	 return "default_contents";
}
 
private void getJsonContent() throws IOException{
	 ServletContext context = request.getServletContext();
     InputStream input = context.getResourceAsStream("/vtt_files/"+fileName);
     BufferedReader reader = new BufferedReader(new InputStreamReader(input));
     String line;
     JSONParser parser = new JSONParser();
     JSONObject jsonObject=new JSONObject();
     int i=0;
     String[] times;
     String time=null;
     while((line = reader.readLine()) != null) {
          if(i==0){
	         String[] headlineArray=line.split("#");
	         System.out.println(headlineArray);
	         headLine=headlineArray[1];
	        }
          else{
        	  if(line.contains("-->"))
        	  {
        		  System.out.println("time found");
        		  times=line.split("-->");
        		  time=times[0].replace(" ","");
        		  System.out.println(times[0]);
        	  }
          }
     i=1;
     try {
	       jsonObject=(JSONObject) parser.parse(line);
	       jsonObject.put("time",time);
          contents.add(jsonObject);
	
     } catch (ParseException e) {
	
       }
     } 
}

private void Edit(int documentNumber) throws IOException, java.text.ParseException{
	ServletContext context = request.getServletContext();
	InputStream input = context.getResourceAsStream("/vtt_files/"+fileName);
	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	String line;
	JSONParser parser = new JSONParser();
	JSONObject jsonObject=new JSONObject();
	ArrayList<String> fileContent=new ArrayList<String>();
    int documentCounter=0;
    /*Read each line from the file, save it in a list(for writing letter) try to parse it in json. 
     */
	while((line = reader.readLine()) != null) {
	   try {
		   jsonObject=(JSONObject) parser.parse(line);
			JSONObject default_content=(JSONObject) jsonObject.get("default_content");
			documentCounter++;
			/*match with document number, if the request is change default then only change the default by update string which contains new 
			 * default request.
			 */
			if(documentNumber==documentCounter){
			  if(actionType.equals("delete")){
				  fileContent.remove(fileContent.size()-1);
				  fileContent.remove(fileContent.size()-1);
				  fileContent.remove(fileContent.size()-1);
				  continue;
			  }
			  else if(actionType.equals("addQuestion")){
	          	if(jsonObject.containsKey("questions")){
	          		JSONArray questions=(JSONArray) jsonObject.get("questions");
	          		JSONObject updateStringJson=(JSONObject) parser.parse(updateString);
	          		questions.add(updateStringJson);
	          		jsonObject.put("questions", questions);
	          		fileContent.add(jsonObject.toString());
	     
	          	}
	          	else{
	          		JSONArray questions=new JSONArray();
	          		 JSONObject updateStringJson=(JSONObject) parser.parse(updateString);
	          		questions.add(updateStringJson);
	          		jsonObject.put("questions", questions);
	          		fileContent.add(jsonObject.toString());
	          		
	          	}
			  }
              else if(actionType.equals("addInfo")){
            	  if(jsonObject.containsKey("infos")){
  	          		JSONArray infos=(JSONArray) jsonObject.get("infos");
  	          		infos.add(updateString);
  	          		jsonObject.put("infos", infos);
  	          		fileContent.add(jsonObject.toString());
  	          	}
  	          	else{
  	          		JSONArray infos=new JSONArray();
  	          		infos.add(updateString);
  	          		jsonObject.put("infos", infos);
  	          		fileContent.add(jsonObject.toString());
  	          		
  	          	} 
			  }
			  
			  else if(actionType.equals("changeDefault")){
					System.out.println(updateString);
					JSONObject jsonUpdateString=new JSONObject();
					jsonUpdateString=(JSONObject)parser.parse(updateString);
				    jsonObject.put("default_content", jsonUpdateString);
				    fileContent.add(jsonObject.toString());
				}
			  
			  /*If the request is edit default, if it is a question of info, take the number and then update is specified position. 
			   * if it is not a question or info, no need to look for the number.
			   */
				else if(actionType.equals("editContent")){
					System.out.println("time is :"+time);
					DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS");
				    Date timeTo = df.parse(time);
				    Calendar cal = Calendar.getInstance();
				    cal.setTime(timeTo);
				    cal.add(Calendar.SECOND, 3);
				    timeTo = cal.getTime();
				    System.out.println("time+5: " + df.format(timeTo));
				    String timeSpan= time+" --> "+df.format(timeTo);
				    fileContent.remove(fileContent.size()-1);
				    fileContent.add(timeSpan);
				    JSONObject editWhatJs=new JSONObject();
				       editWhatJs=(JSONObject) parser.parse(editWhat);
				   if(editWhatJs.get("type").equals("questions")||editWhatJs.get("type").equals("infos")){
					     
						   Integer content_number= Integer.valueOf((String) editWhatJs.get("number"));
					   JSONArray questionOrInfo=(JSONArray)jsonObject.get(editWhatJs.get("type"));
					   if(editWhatJs.get("type").equals("questions")){
					   JSONObject updateStringJson=(JSONObject) parser.parse(updateString);
					   questionOrInfo.set(content_number.intValue(), updateStringJson);
					   }
					   else
						   questionOrInfo.set(content_number.intValue(), updateString);
					   jsonObject.put(editWhatJs.get("type"), questionOrInfo);
					   fileContent.add(jsonObject.toString());   
				   }
				   else{
					   System.out.println(updateString);
					   jsonObject.put(editWhatJs.get("type"), updateString);
					   fileContent.add(jsonObject.toString());
				   }
				}
				else if(actionType.equals("deleteContent")){
					JSONObject deleteWhatJs=new JSONObject();
				       deleteWhatJs=(JSONObject) parser.parse(deleteWhat);
				   if(deleteWhatJs.get("type").equals("questions")||deleteWhatJs.get("type").equals("infos")){
					   Integer content_number= Integer.valueOf((String) deleteWhatJs.get("number"));
					   JSONArray questionOrInfo=(JSONArray)jsonObject.get(deleteWhatJs.get("type"));
					   if(questionOrInfo.size()>1){
					   questionOrInfo.remove(content_number.intValue());
					   jsonObject.put(deleteWhatJs.get("type"),questionOrInfo);
					   }
					   else
						   jsonObject.remove(questionOrInfo);
					   }
					   else
						   jsonObject.remove(deleteWhatJs.get("type"));
					   
					   fileContent.add(jsonObject.toString());   
				   
				   
				}
				
				else{
					fileContent.add(jsonObject.toString());
				}
				
			}
			else{
				fileContent.add(jsonObject.toString());
			}
		} catch (ParseException e) {
			 fileContent.add(line);
		}
	}
	input.close();
	reader.close();
	
	PrintWriter pw = null;
    try {
    	String pathName="/vtt_files/"+fileName;
    	String path=context.getRealPath(pathName);
    	
        pw= new PrintWriter(new File(path));
       for (String lineToWrite : fileContent) {
         pw.println(lineToWrite);
       }
    } catch (FileNotFoundException e) {
       e.printStackTrace();
    } finally {
       if (pw != null) {
          pw.close();
       }
    }
	
}

public String getHeadLine() {
	return headLine;
}

public JSONArray getContents() {
	return contents;
}

public String getFileName() {
	return fileName;
}

public void setFileName(String fileName) {
	this.fileName = fileName;
}

public String getActionType() {
	return actionType;
}

public void setActionType(String actionType) {
	this.actionType = actionType;
}

public String getUserId() {
	return userId;
}

public String getEntityName() {
	return entityName;
}

public void setEntityName(String entityName) {
	this.entityName = entityName;
}

public String getUpdateString() {
	return updateString;
}

public void setUpdateString(String updateString) {
	this.updateString = updateString;
}

public String getDocumentNumber() {
	return documentNumber;
}

public void setDocumentNumber(String documentNumber) {
	this.documentNumber = documentNumber;
}

public String getTime() {
	return time;
}

public void setTime(String time) {
	this.time = time;
}

public String getEditWhat() {
	return editWhat;
}

public void setEditWhat(String editWhat) {
	this.editWhat = editWhat;
}

public String getDeleteWhat() {
	return deleteWhat;
}

public void setDeleteWhat(String deleteWhat) {
	this.deleteWhat = deleteWhat;
}

}
