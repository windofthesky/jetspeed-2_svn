package org.apache.jetspeed.portlets.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jetspeed.portlets.security.AddUserForm;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AddUserAction extends org.apache.struts.action.Action {
    
    // Local Forwards
    public static final String FORWARD_EDITUSER = "editUser";
    
    public AddUserAction() {
    }
    
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        String username = ((AddUserForm)form).getUsername();
        String password = ((AddUserForm)form).getPassword();
        
        return mapping.findForward(FORWARD_EDITUSER);
    }

}