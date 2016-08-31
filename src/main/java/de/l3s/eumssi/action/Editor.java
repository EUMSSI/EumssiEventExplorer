package de.l3s.eumssi.action;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
private JSONArray defaultContents=new JSONArray();
private String fileName;
private String userId;
HttpServletRequest request = ServletActionContext.getRequest();	
HttpSession session = request.getSession(false);

public String execute() throws IOException{
	session = request.getSession(true);
	userId=(String) session.getAttribute("userId");
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
		defaultContents.add(jsonObject);
	} catch (ParseException e) {
		
	}
} 
 return "defaultcontents";
}

public String getHeadLine() {
	return headLine;
}

public JSONArray getDefaultContents() {
	return defaultContents;
}

public String getFileName() {
	return fileName;
}

public void setFileName(String fileName) {
	this.fileName = fileName;
}

public String getUserId() {
	return userId;
}


}
