/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.engine.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.jetspeed.container.url.PortalURL;
import org.apache.jetspeed.request.JetspeedRequestContext;

import org.apache.pluto.om.common.SecurityRole;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.common.SecurityRoleSet;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.window.PortletWindow;

/**
 * This request wrappers the servlet request and is used
 * within the container to communicate to the invoked servlet.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ServletRequestImpl extends HttpServletRequestWrapper
{
    PortletWindow portletWindow = null;

    private Map portletParameters;

    public ServletRequestImpl(javax.servlet.http.HttpServletRequest servletRequest, PortletWindow window)
    {
        super(servletRequest);

        this.portletWindow = window;
        System.out.println("Constructing SRI: " + window.getId());
    }

    private HttpServletRequest _getHttpServletRequest()
    {
        return (HttpServletRequest) super.getRequest();
    }

    //  ServletRequestWrapper overlay

    public String getParameter(String name)
    {       
        Object value = this.getParameterMap().get(name);
        if (value == null)
        {
            return (null);
        }
        else if (value instanceof String[])
        {
            return (((String[]) value)[0]);
        }
        else if (value instanceof String)
        {
            return ((String) value);
        }
        else
        {
            return (value.toString());
        }
    }

    public Map getParameterMap() 
    {
        //get control params
        if (portletParameters == null) 
        {
            portletParameters = new HashMap();

            JetspeedRequestContext context = (JetspeedRequestContext)
                  getAttribute("org.apache.jetspeed.request.RequestContext");
            if (context != null) 
            {
                PortalURL url = context.getPortalURL();
                Iterator iter = url.getRenderParamNames(portletWindow);
                while (iter.hasNext()) 
                {
                    String name = (String) iter.next();
                    String[] values = url.getRenderParamValues(
                            portletWindow, name);
                    portletParameters.put(name, values);

                }
            }

            //get request params
            for (Enumeration parameters = super.getParameterNames();  parameters.hasMoreElements(); ) 
            {
                String paramName = (String) parameters.nextElement();
                String[] paramValues = (String[]) super
                        .getParameterValues(paramName);
                String[] values = (String[]) portletParameters.get(paramName);

                if (values != null) 
                {
                    String[] temp = new String[paramValues.length
                            + values.length];
                    System.arraycopy(paramValues, 0, temp, 0,
                            paramValues.length);
                    System.arraycopy(values, 0, temp, paramValues.length,
                            values.length);
                    paramValues = temp;
                }
                portletParameters.put(paramName, paramValues);
            }
        }
        return Collections.unmodifiableMap(portletParameters);
        // return Collections.unmodifiableMap(super.getParameterMap().keySet());
        
    }
        
    public Enumeration getParameterNames()
    {
        return Collections.enumeration(this.getParameterMap().keySet());
    }

    public String[] getParameterValues(String name)
    {
        return (String[]) this.getParameterMap().get(name);
    }

    /**
     * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
     */
    public boolean isUserInRole(String roleName)
    {
        // will result in a NullPointerException if roleName == null which is
        // just as well I guess.
        if (roleName.length() > 0)
        {
            PortletDefinition portletDefinition = portletWindow
                    .getPortletEntity().getPortletDefinition();
            SecurityRoleRefSet roleRefSet = portletDefinition
                    .getInitSecurityRoleRefSet();
            SecurityRoleSet roleSet = portletDefinition
                    .getPortletApplicationDefinition()
                    .getWebApplicationDefinition().getSecurityRoles();

            Iterator roleRefIter = roleRefSet.iterator();
            while (roleRefIter.hasNext())
            {
                SecurityRoleRef roleRef = (SecurityRoleRef) roleRefIter.next();
                if (roleName.equals(roleRef.getRoleName()))
                {
                    String roleLinkName = roleRef.getRoleLink();
                    if (roleLinkName == null || roleLinkName.length() == 0)
                    {
                        roleLinkName = roleName;
                    }
                    Iterator roleIter = roleSet.iterator();
                    while (roleIter.hasNext())
                    {
                        SecurityRole role = (SecurityRole) roleIter.next();
                        if (roleLinkName.equals(role.getRoleName()))
                                return super.isUserInRole(roleLinkName);
                    }
                    return false;
                }

            }
        }
        return false;
    }
    
    /**
     * @see javax.servlet.http.HttpServletRequest#getAttribute(java.lang.String)
     */
    public Object getAttribute(String name)
    {
        Object value = super.getAttribute(name);
        if (name.equals(PortletRequest.USER_INFO))
        {
            JetspeedRequestContext context = (JetspeedRequestContext)
                getAttribute("org.apache.jetspeed.request.RequestContext");
            if (null != context)
            { 
                String entityID = "--NULL--";
                PortletEntity entity = portletWindow.getPortletEntity();
                if (entity != null)
                {
                    entityID = entity.getId().toString();
                }
                PortletApplicationEntity portletAppEntity = portletWindow.getPortletEntity().getPortletApplicationEntity();
                PortletApplicationDefinition portletAppDef = entity.getPortletDefinition().getPortletApplicationDefinition();
                
                // if (null != portletAppEntity)
                if (null != portletAppDef)
                {
                    // PortletApplicationDefinition portletAppDef = portletAppEntity.getPortletApplicationDefinition();
                    value = context.getUserInfoMap(portletAppDef.getId());
                    if ( value != null )
                    {
                        System.out.println("_____________HERE0: " + ((Map) value).size());
                    }
                    else
                    {
                        System.out.println("_____________HERE1: UserInfoMap NULL");
                    }
                }
                else
                {                    
                    System.out.println("_____________HERE2: Entity is null!!!! " + entityID);
                }
                  
            }
        }
        return value;
    }
}
