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
package org.apache.jetspeed.portlet;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;

import org.apache.commons.codec.binary.Base64;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.sso.SSOContext;
import org.apache.jetspeed.sso.SSOException;
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
    
    public static final String SSO_TYPE_URL_USERNAME = "sso.url.Principal";
    public static final String SSO_TYPE_URL_PASSWORD = "sso.url.Credential";
    
    public static final String SSO_REQUEST_ATTRIBUTE_USERNAME = "sso.ra.username";
    public static final String SSO_REQUEST_ATTRIBUTE_PASSWORD = "sso.ra.password";

    /*
     * The constants must be used in your HTML form for the SSO principal and credential
     */
    public static final String SSO_FORM_PRINCIPAL = "ssoPrincipal";
    public static final String SSO_FORM_CREDENTIAL = "ssoCredential";
    
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

    public void doEdit(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        try
        {
            Subject subject = getSubject();                 
            String site = request.getPreferences().getValue("SRC", "");
            SSOContext context = sso.getCredentials(subject, site);
            getContext(request).put(SSO_FORM_PRINCIPAL, context.getRemotePrincipalName());
            getContext(request).put(SSO_FORM_CREDENTIAL, context.getRemoteCredential());
        }
        catch (SSOException e)
        {
            if (e.getMessage().equals(SSOException.NO_CREDENTIALS_FOR_SITE))
            {
                // no credentials configured in SSO store
                // switch to SSO Configure View
                getContext(request).put(SSO_FORM_PRINCIPAL, "");
                getContext(request).put(SSO_FORM_CREDENTIAL, "");
            }
            else
            {
                throw new PortletException(e);
            }
        }        
        
        super.doEdit(request, response);
    }
        
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        String site = request.getPreferences().getValue("SRC", null);
        if (site == null)
        {
            // no credentials configured in SSO store
            // switch to SSO Configure View
            request.setAttribute(PARAM_VIEW_PAGE, this.getPortletConfig().getInitParameter(PARAM_EDIT_PAGE));
            setupPreferencesEdit(request, response);
            super.doView(request, response);
            return;
        }
        
        try
        {
            Subject subject = getSubject();                 
            SSOContext context = sso.getCredentials(subject, site);
            request.setAttribute(SSO_REQUEST_ATTRIBUTE_USERNAME, context.getRemotePrincipalName());
            request.setAttribute(SSO_REQUEST_ATTRIBUTE_PASSWORD, context.getRemoteCredential());
        }
        catch (SSOException e)
        {
            if (e.getMessage().equals(SSOException.NO_CREDENTIALS_FOR_SITE))
            {
                // no credentials configured in SSO store
                // switch to SSO Configure View
                request.setAttribute(PARAM_VIEW_PAGE, this.getPortletConfig().getInitParameter(PARAM_EDIT_PAGE));
                setupPreferencesEdit(request, response);                
            }
            else
            {
                throw new PortletException(e);
            }
        }        
        
        super.doView(request, response);
    }
    
    public void processAction(ActionRequest request, ActionResponse actionResponse)
    throws PortletException, IOException
    {
        // save the prefs
        super.processAction(request, actionResponse);
        
        // get the POST params -- requires HTML post params named
        // ssoUserName 
        String ssoPrincipal = request.getParameter(SSO_FORM_PRINCIPAL);
        String ssoCredential = request.getParameter(SSO_FORM_CREDENTIAL);        
        /*
        if (ssoPrincipal == null || ssoCredential == null)
        {
            
            actionResponse.setPortletMode(PortletMode.EDIT); // stay on edit
        }
        */
        String site = request.getPreferences().getValue("SRC", "");
        try
        {
            Subject subject = getSubject();
            if (sso.hasSSOCredentials(subject, site))
            {
                sso.updateCredentialsForSite(getSubject(), ssoPrincipal, site, ssoCredential);
            }
            else
            {
                sso.addCredentialsForSite(getSubject(), ssoPrincipal, site, ssoCredential);
            }
        }
        catch (SSOException e)
        {
            throw new PortletException(e);
        }
        
    }
    
    public String getURLSource(RenderRequest request, RenderResponse response, PortletPreferences prefs)
    {
        String baseSource = super.getURLSource(request, response, prefs);
        String type = prefs.getValue(SSO_TYPE, SSO_TYPE_URL);
        if (type.equals(SSO_TYPE_URL) || type.equals(SSO_TYPE_URL_BASE64))
        {
            String userNameParam = prefs.getValue(SSO_TYPE_URL_USERNAME, "user");
            String passwordParam = prefs.getValue(SSO_TYPE_URL_PASSWORD, "password");
            StringBuffer source = new StringBuffer(baseSource);
            if (baseSource.indexOf("?") == -1)
            {
                source.append("?");
            }            
            else
            {
                source.append("&");
            }
            source.append(userNameParam);
            source.append("=");
            
            String userName = (String)request.getAttribute(SSO_REQUEST_ATTRIBUTE_USERNAME);
            if (userName == null) userName = "";
            String password = (String)request.getAttribute(SSO_REQUEST_ATTRIBUTE_PASSWORD);
            if (password == null) password = "";

            if (type.equals(SSO_TYPE_URL_BASE64))
            {
                Base64 encoder = new Base64() ;
                userName = new String(encoder.encode(userName.getBytes()));
                password = new String(encoder.encode(password.getBytes()));
            }            
            
            source.append(userName);
            source.append("&");
            source.append(passwordParam);
            source.append("=");
            source.append(password);
            
            return response.encodeURL(source.toString());
        }
        else
        {
            return baseSource;
        }
    }
    
    private Subject getSubject()
    {
        AccessControlContext context = AccessController.getContext();
        return JSSubject.getSubject(context);         
    }
    
}
