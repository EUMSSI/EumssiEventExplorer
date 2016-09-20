package de.l3s.eumssi.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;

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
HttpServletRequest request = ServletActionContext.getRequest();	
HttpSession session = request.getSession(false);

public String execute() throws IOException{
	session = request.getSession(true);
	userId=(String) session.getAttribute("userId");
	int documentNumberInt;
	
	//actionType=changeDefault means change and save the new default request in file	
		 if(actionType.equals("changeDefault") || actionType.equals("editDefault")){
			 documentNumberInt=Integer.valueOf(documentNumber);
			Edit(documentNumberInt);
			getJsonContent();
			return "default_contents";
		}	
	 
	/*in else section 2 types of request will be handaled.
	 * 1. request for change the default. where all the possible contents of a perticular entity will be shown to admin 
	 * 2. request for edit  the default. admin can edit the default.
	 * Here getJsonContent() will get all the contents a vtt file and save it as a json object in content varible.
	 * Two particular jsp file(ChangeDefaultContents.jsp and EditDefaultContents.jsp) dedicated for above two types of requests. 
	 * Both will parse the "content" json object and show desired output accoring to their
	 * resposibility and other parameters  
	 * Return type "default_contents" is for showing the DefaultContents.jsp page, where all the defaults contents of a particular 
	 * file is shown.
	 * Return type "change_default_request" and "edit_default_request" will show the "ChangeDefaultContents.jsp" and "EditDefaultContents.jsp"
	 * Based on there actionType, respective pages will be redirected.    
	 */
	else{
		getJsonContent();
         
          if(actionType.equals("changeDefaultRequest")){
  			System.out.println(entityName);
  	        return "change_default_request";
  		}
          else if(actionType.equals("editDefaultRequest")){
    			System.out.println(entityName);
      	        return "edit_default_request";
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
     while((line = reader.readLine()) != null) {
          if(i==0){
	         String[] headlineArray=line.split("#");
	         System.out.println(headlineArray);
	         headLine=headlineArray[1];
	        }
     i=1;
     try {
	       jsonObject=(JSONObject) parser.parse(line);
          contents.add(jsonObject);
	
     } catch (ParseException e) {
	
       }
     } 
}

private void Edit(int documentNumber) throws IOException{
	ServletContext context = request.getServletContext();
	InputStream input = context.getResourceAsStream("/vtt_files/"+fileName);
	BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	String line;
	JSONParser parser = new JSONParser();
	JSONObject jsonObject=new JSONObject();
	ArrayList<String> fileContent=new ArrayList<String>();
    int documentCounter=0;
    /*Read each line from the file, save it in a list(for writing letter) try toparse it in json. 
     */
	while((line = reader.readLine()) != null) {
	   try {
		   jsonObject=(JSONObject) parser.parse(line);
			JSONObject default_content=(JSONObject) jsonObject.get("default_content");
			documentCounter++;
			/*math with document number, if the request is change default then only change the default by update string which contains new 
			 * default request.
			 */
			if(documentNumber==documentCounter){
				if(actionType.equals("changeDefault")){
					System.out.println(updateString);
					JSONObject jsonUpdateString=new JSONObject();
					jsonUpdateString=(JSONObject)parser.parse(updateString);
				  jsonObject.put("default_content", jsonUpdateString);
				   fileContent.add(jsonObject.toString());
				}
			  /*If the request is edit default, if it is a question of info, take the number and then update is specified position. 
			   * if it is not a question or info, no need to look for the number.
			   */
				else if(actionType.equals("editDefault")){
				   if(default_content.get("type").equals("questions")||default_content.get("type").equals("infos")){
						   Integer default_content_number= Integer.valueOf((String) default_content.get("number"));
					   JSONArray questionOrInfo=(JSONArray)jsonObject.get(default_content.get("type"));
					   if(default_content.get("type").equals("questions")){
					   JSONObject updateStringJson=(JSONObject) parser.parse(updateString);
					   questionOrInfo.set(default_content_number.intValue(), updateStringJson);
					   }
					   else
						   questionOrInfo.set(default_content_number.intValue(), updateString);
					   jsonObject.put(default_content.get("type"), questionOrInfo);
					   fileContent.add(jsonObject.toString());   
				   }
				   else{
					   System.out.println(updateString);
					   jsonObject.put(default_content.get("type"), updateString);
					   fileContent.add(jsonObject.toString());
				   }
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


}
