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

import java.lang.reflect.Constructor;
import java.security.Permission;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.impl.RolePrincipalImpl;

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
    protected static final Log log = LogFactory.getLog(SecurityPermissionAction.class);
    protected PermissionManager pm = null;
    protected Map permissionMap = null;

    public SecurityPermissionAction(String template, 
                            String errorTemplate, 
                            PermissionManager pm,
                            PortletActionSecurityBehavior securityBehavior,
                            Map permissionMap)
    {
        super(template, errorTemplate, securityBehavior); 
        this.pm = pm;
        this.permissionMap = permissionMap;
    }
    
    public boolean run(RequestContext requestContext, Map resultMap)
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
    
    protected int addPermission(RequestContext requestContext, Map resultMap)
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
            
            Permission permission = createPermissionFromClass(type, resource, actions);            
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

    protected int updatePermission(RequestContext requestContext, Map resultMap)
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
            Permission permission = null;
            if (!oldActions.equals(actions))
            {
                permission = createPermissionFromClass(type, resource, oldActions);
                pm.removePermission(permission);
                permission = createPermissionFromClass(type, resource, actions);
                pm.addPermission(permission);
            }   
            else
            {
                permission = createPermissionFromClass(type, resource, actions);
            }
            String roleNames = getActionParameter(requestContext, "roles");
            return updateRoles(permission, roleNames);
        }
        catch (SecurityException e)
        {
            throw new AJAXException(e.toString(), e);
        }        
    }
    
    protected int updateRoles(Permission permission, String roleNames)
    throws SecurityException
    {
        List principals = new LinkedList();
        if (roleNames != null)
        {
            StringTokenizer toke = new StringTokenizer(roleNames, ",");
            while (toke.hasMoreTokens())
            {
                String roleName = toke.nextToken();
                Principal role = new RolePrincipalImpl(roleName);
                principals.add(role);
            }                
        }
        return pm.updatePermission(permission, principals);                    
    }

    protected int removePermission(RequestContext requestContext, Map resultMap)
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
            Permission permission = createPermissionFromClass(type, resource, actions);            
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
    
    protected String mapTypeToClassname(String type)
    throws AJAXException
    {
        String classname = (String)this.permissionMap.get(type);
        if (classname != null)
            return classname;
        throw new AJAXException("Bad resource 'type' parameter: " + type);            
    }
    
    protected Permission createPermissionFromClass(String type, String resource, String actions)
    throws AJAXException
    {        
        String classname = this.mapTypeToClassname(type);
        try
        {
            Class permissionClass = Class.forName(classname);
            Class[] parameterTypes = { String.class, String.class };
            Constructor permissionConstructor = permissionClass.getConstructor(parameterTypes);
            Object[] initArgs = { resource, actions };
            return (Permission)permissionConstructor.newInstance(initArgs);
        }
        catch (Exception e)
        {
            throw new AJAXException("Failed to create permission: " + type, e);
        }
    }
    
}
