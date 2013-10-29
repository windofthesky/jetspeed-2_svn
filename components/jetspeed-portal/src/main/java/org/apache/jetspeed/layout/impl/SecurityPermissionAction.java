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

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.TransientRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Security Permission action
 * 
 * AJAX Parameters: 
 *    action = permission
 *    method = add | update | delete 
 *    resource = name of the resource to modify
 *    type = portlet | page | folder
 *    roles = comma separated list of roles
 *    actions = comma separated list of actions
 *    oldactions = comma separated list of old actions
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class SecurityPermissionAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected static final Logger log = LoggerFactory.getLogger(SecurityPermissionAction.class);
    protected PermissionManager pm = null;

    public SecurityPermissionAction(String template, 
                            String errorTemplate, 
                            PermissionManager pm,
                            PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, securityBehavior); 
        this.pm = pm;
    }
    
    public boolean run(RequestContext requestContext, Map<String,Object> resultMap)
            throws AJAXException
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, "permissions");
            // Get the necessary parameters off of the request
            String method = getActionParameter(requestContext, "method");
            if (method == null) 
            { 
                throw new RuntimeException("Method not provided"); 
            }            
            resultMap.put("method", method);
            if (false == checkAccess(requestContext, JetspeedActions.EDIT))
            {
                success = false;
                resultMap.put(REASON, "Insufficient access to administer portal permissions");                
                return success;
            }           
            int count = 0;
            if (method.equals("add"))
            {
                count = addPermission(requestContext, resultMap);
            }
            else if (method.equals("update"))
            {
                count = updatePermission(requestContext, resultMap);
            }            
            else if (method.equals("remove"))
            {
                count = removePermission(requestContext, resultMap);
            }
            else
            {
                success = false;
                resultMap.put(REASON, "Unsupported portal permissions method: " + method);                
                return success;                
            }
            resultMap.put("count", Integer.toString(count));
            resultMap.put("resource", getActionParameter(requestContext, "resource"));
            resultMap.put("type", getActionParameter(requestContext, "type"));
            resultMap.put("actions", getActionParameter(requestContext, "actions"));
            resultMap.put("roles", getActionParameter(requestContext, "roles"));
            resultMap.put(STATUS, status);
        } 
        catch (Exception e)
        {
            log.error("exception administering portal permissions", e);
            resultMap.put(REASON, e.toString());
            success = false;
        }
        return success;
    }
    
    protected int addPermission(RequestContext requestContext, Map<String,Object> resultMap)
    throws AJAXException
    {
        try
        {
            String type = getActionParameter(requestContext, "type");
            if (type == null)
                throw new AJAXException("Missing 'type' parameter");
            String resource = getActionParameter(requestContext, "resource");
            if (resource == null)
                throw new AJAXException("Missing 'resource' parameter");
            String actions = getActionParameter(requestContext, "actions");
            if (actions == null)
                throw new AJAXException("Missing 'actions' parameter");
            
            JetspeedPermission permission = pm.newPermission(type, resource, actions);            
            if (pm.permissionExists(permission))
            {
                throw new AJAXException("Permission " + resource + " already exists");
            }   
            
            pm.addPermission(permission);            
            String roleNames = getActionParameter(requestContext, "roles");
            return updateRoles(permission, roleNames);
        }
        catch (SecurityException e)
        {
            throw new AJAXException(e.toString(), e);
        }        
    }

    protected int updatePermission(RequestContext requestContext, Map<String,Object> resultMap)
    throws AJAXException
    {
        try
        {
            String type = getActionParameter(requestContext, "type");
            if (type == null)
                throw new AJAXException("Missing 'type' parameter");
            String resource = getActionParameter(requestContext, "resource");
            if (resource == null)
                throw new AJAXException("Missing 'resource' parameter");
            String actions = getActionParameter(requestContext, "actions");
            if (actions == null)
                throw new AJAXException("Missing 'actions' parameter");
            String oldActions = getActionParameter(requestContext, "oldactions");
            if (oldActions == null)
            {
                // assume no change
                oldActions = actions;
            }
            JetspeedPermission permission = pm.newPermission(type, resource, actions);
            if (!oldActions.equals(actions))
            {
                pm.updatePermission(permission);
            }   
//            else
//            {
//                permission = pm.newPermission(type, resource, actions);
//            }
            String roleNames = getActionParameter(requestContext, "roles");
            return updateRoles(permission, roleNames);
        }
        catch (SecurityException e)
        {
            throw new AJAXException(e.toString(), e);
        }        
    }
    
    protected int updateRoles(JetspeedPermission permission, String roleNames)
    throws SecurityException
    {
        int count = 0;
        List<JetspeedPrincipal> principals = new LinkedList<JetspeedPrincipal>();
        if (roleNames != null)
        {
            StringTokenizer toke = new StringTokenizer(roleNames, ",");
            while (toke.hasMoreTokens())
            {
                principals.add(new TransientRole(toke.nextToken()));
                count++;
            }                
        }
        pm.grantPermissionOnlyTo(permission, JetspeedPrincipalType.ROLE, principals);
        return count;
    }

    protected int removePermission(RequestContext requestContext, Map<String,Object> resultMap)
    throws AJAXException
    {
        try
        {
            String type = getActionParameter(requestContext, "type");
            if (type == null)
                throw new AJAXException("Missing 'type' parameter");
            String resource = getActionParameter(requestContext, "resource");
            if (resource == null)
                throw new AJAXException("Missing 'resource' parameter");
            String actions = getActionParameter(requestContext, "actions");
            if (actions == null)
                throw new AJAXException("Missing 'actions' parameter");            
            JetspeedPermission permission = pm.newPermission(type, resource, actions);            
            if (pm.permissionExists(permission))
            {
                pm.removePermission(permission);
                return 1;
            }
            return 0;
        }
        catch (SecurityException e)
        {
            throw new AJAXException(e.toString(), e);
        }
    }
}
