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
package org.apache.jetspeed.portlet;

import java.security.AccessControlContext;
import java.security.AccessController;
import javax.security.auth.Subject;

import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;

import org.apache.jetspeed.sso.SSOProvider;


/**
 * SSOIFramePortlet
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SSOIFramePortlet extends IFrameGenericPortlet
{
    public static final String SSO_TYPE = "sso.type";
    public static final String SSO_TYPE_URL = "url";
    public static final String SSO_TYPE_URL_BASE64 = "url.base64";
    public static final String SSO_TYPE_HTTP = "http";
    public static final String SSO_TYPE_CERTIFICATE = "certificate";
    
    public static final String SSO_TYPE_URL_USERNAME = "sso.url.param.username";
    public static final String SSO_TYPE_URL_PASSWORD = "sso.url.param.password";
    

    private PortletContext context;
    private SSOProvider sso;

    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        context = getPortletContext();
        sso = (SSOProvider)context.getAttribute("cps:SSO");
        if (null == sso)
        {
           throw new PortletException("Failed to find SSO Provider on portlet initialization");
        }        
    }
        
    public String getURLSource(RenderRequest request, PortletPreferences prefs)
    {
        String baseSource = super.getURLSource(request, prefs);
        String type = prefs.getValue(SSO_TYPE, SSO_TYPE_URL);
        if (type.equals(SSO_TYPE_URL))
        {
            String userNameParam = prefs.getValue("sso.url.param.username", "");
            String passwordParam = prefs.getValue("sso.url.param.password", "");
            StringBuffer source = new StringBuffer(baseSource);
            if (baseSource.indexOf("?") == -1)
            {
                source.append("?");
            }            
            else
            {
                source.append("&");
            }
            AccessControlContext context = AccessController.getContext();
            Subject subject = Subject.getSubject(context); 
            System.out.println("GOT A SUBJECT " + subject);
            source.append(userNameParam);
            source.append("=");
            
            // LEFT OFF HERE: get credentials from subject, and pass into SSO component
            
            source.append("joey");
            source.append("&");
            source.append(passwordParam);
            source.append("=");
            source.append("joeys-password");
            return source.toString();
        }
        else
        {
            return baseSource;
        }
    }
    
    
    
}
