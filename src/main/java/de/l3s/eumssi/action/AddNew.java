package de.l3s.eumssi.action;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;

public class AddNew {
	private String actionType;
	private String documentNumber;
	private String fileName;
	private String entityName;
	HttpServletRequest request = ServletActionContext.getRequest();	
	HttpSession session = request.getSession(false);
	private String userId;
	public String execute(){
		session = request.getSession(true);
	    userId=(String) session.getAttribute("userId");
	    if(actionType.equals("addQuestion"))
	    	return "addQuestion";
	    else
	    	return "addInfo";
	}
	
	public String getUserId() {
		return userId;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
}
