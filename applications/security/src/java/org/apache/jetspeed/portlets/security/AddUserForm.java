/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.portlets.security;

import javax.servlet.http.HttpServletRequest;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;

public class AddUserForm extends ActionForm {
	
    private String username = "";
    private String password = "";
	    
    /**
     * <p>Default constructor.</p>
     */
    public AddUserForm() {
    }
    
    /**
     * <p>Resets the form.</p>
     * 
     * @param actionMapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public void reset(ActionMapping actionMapping, HttpServletRequest request) {
        this.username = "";
    }
    
    /**
     * Validate the properties that have been set from this HTTP request,
     * and return an <code>ActionErrors</code> object that encapsulates any
     * validation errors that have been found.  If no errors are found, return
     * <code>null</code> or an <code>ActionErrors</code> object with no
     * recorded error messages.
     *
     * @param actionMapping The mapping used to select this instance
     * @param request The servlet request we are processing
     */
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest request) {
        ActionErrors errs = new ActionErrors();
        return errs;
    }

    /**
     * <p>Getter for the username.</p>
     * 
     * @return The username.
     */
    public String getUsername(){
        return this.username;    
    }
	
    /**
     * <p>Setter for the username.</p>
     * 
     * @param username The username.
     */
	public void setUsername(String username){
		this.username = (username == null ? "" : username);
	}
	
	/**
	 * <p>Getter for the password.</p>
	 * 
	 * @return The password.
	 */
	public String getPassword(){
        return this.password;    
    }
	
	/**
	 * <p>Setter for the password.</p>
	 * 
	 * @param password The password.
	 */
	public void setPassword(String password){
		this.password = (password == null ? "" : password);
	}
}
