package org.apache.jetspeed.portlets.security;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

public class AddUserForm extends ActionForm {
	
    private String username = "";
    private String password = "";
	    
    public AddUserForm() {
    }
    
    public void reset(ActionMapping actionMapping, HttpServletRequest request) {
        this.username = "";
    }
    
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest request) {
        ActionErrors errs = new ActionErrors();
        return errs;
    }

    public String getUsername(){
        return this.username;    
    }
	
	public void setUsername(String username){
		this.username = (username == null ? "" : username);
	}
	
	public String getPassword(){
        return this.password;    
    }
	
	public void setPassword(String password){
		this.password = (password == null ? "" : password);
	}
}
