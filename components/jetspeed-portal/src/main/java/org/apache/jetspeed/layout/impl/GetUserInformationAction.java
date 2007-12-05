/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.layout.impl;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;

/**
 * Retrieve user information of the current user
 * 
 * AJAX action:
 * 		action = getuserinfo
 * 
 * AJAX Parameters:
 * 		none
 *     
 * @author <a href="mailto:mikko.wuokko@evtek.fi">Mikko Wuokko</a>
 * @version $Id: $
 */
public class GetUserInformationAction 
    extends BaseUserAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Log log = LogFactory.getLog(GetUserInformationAction.class);

    public GetUserInformationAction(String template, 
                            String errorTemplate, 
                            UserManager um,
                            RolesSecurityBehavior securityBehavior)                            
    {
        super(template, errorTemplate, um, securityBehavior); 
    }
    
    public boolean run(RequestContext requestContext, Map resultMap)
            throws AJAXException
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, "userinformation");
            // Get the necessary parameters off of the request
        	if(!requestContext.getUserPrincipal().getName().equals(userManager.getAnonymousUser()))
        	{        		
        		Principal principal = requestContext.getUserPrincipal();        		
                resultMap.put(USERNAME, principal.getName());
                resultMap.put(TYPE, principal.getClass().getName());
                
                // Loading the userinfo
                User user = userManager.getUser(principal.getName());
                if(user != null)
                {
                	Preferences prefs = user.getUserAttributes();
                	String[] prefKeys = prefs.keys();
                	Map prefsSet = new HashMap();
                	for(int i = 0; i<prefKeys.length; i++)
                	{
                		prefsSet.put(prefKeys[i], prefs.get(prefKeys[i], "No value"));                		
                	}
                	resultMap.put(USERINFO, prefsSet);

                }
                	
        	}
        	else
        	{
        		status = "failure";
        		resultMap.put(REASON, "Not logged in");
        		return false;
        	}
            resultMap.put(STATUS, status);
        } 
        catch (Exception e)
        {
            log.error("exception with user account access", e);
            resultMap.put(REASON, e.toString());
            success = false;
        }
        return success;
    }    

}