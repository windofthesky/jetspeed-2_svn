/* Copyright 2004 Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

/*Created on: Dec 5, 2005 */

package org.apache.jetspeed.portlet.sso;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.security.auth.Subject;

import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOProvider;
import org.apache.portals.bridges.common.ScriptPostProcess;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

/**
 * SSOProxyPortlet
 * This portlet can be used as a bridge to any URL.
 * It acts as a http client and therefore it can store
 * cookies.
 * The main purpose is that the SSOProxy portlet authenticates
 * any SSO credential for the principal user not knowing in advance
 * what URL the user might select. No login prompt will appear for any url
 * in the portlet for that an SSO entry exists and the principal user has permissions.
 * 
 * @author Roger Ruttimann <rogerrut@apache.org>
 *
 */
public class SSOProxyPortlet extends GenericVelocityPortlet {
    private PortletContext context;
    private SSOProvider sso;
    
    /* Re-use Proxy client inside the SSO Component */
    private boolean isAuthenticated = false;
    
    /** Default encoding UTF-8*/
    public String defaultEncoding = "UTF-8";
    
    /** Block Size */
    static final int BLOCK_SIZE = 4096;
    
    /** ACTION_PARAMETER_SSOPROXY*/
    static final String ACTION_PARAMETER_SSOPROXY = "SSOProxy";

    
    /** Preference values */
    /** DestinationURL */
    static final String DESTINATION_URL = "DestinationURL";
    
    /** SSOSite */
    static final String SSO_SITE = "SSOSite";
    
    /** ForceSSORefresh*/
    static final String FORCE_SSO_REFRESH = "ForceSSORefresh";
    
    /** Encoding*/
    static final String ENCODING = "Encoding";

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
    
    public void processAction(ActionRequest request, ActionResponse actionResponse)
    throws PortletException, IOException
    {
        // save the prefs
        super.processAction(request, actionResponse);
    }
    
    public void doView(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
        String destinationURL = request.getPreferences().getValue(DESTINATION_URL, "");
        String ssoSite = request.getPreferences().getValue(SSO_SITE, "");
        String forceRefresh = request.getPreferences().getValue(FORCE_SSO_REFRESH, "false");
        String encoding = request.getPreferences().getValue(ENCODING, this.defaultEncoding);

        if (destinationURL == null)
        {
            // No destination configured Switch to configure View
             request.setAttribute(PARAM_VIEW_PAGE, this.getPortletConfig().getInitParameter(PARAM_EDIT_PAGE));
            setupPreferencesEdit(request, response);
            super.doView(request, response);
            return;
        }
        
        /*
         * Call into the SSO Proxy and process the result page
         */
        boolean doRefresh = false;
        BufferedInputStream bis = null;
        
        if ( (forceRefresh.compareToIgnoreCase("TRUE") == 0) || this.isAuthenticated == false)
        	doRefresh = true;
       
        try
        {
            Subject subject = getSubject(); 
            if (ssoSite == null || ssoSite.length() ==0)
            	bis = sso.useSSO(subject, destinationURL,doRefresh);
            else
            	bis = sso.useSSO(subject, destinationURL,ssoSite, doRefresh);
            
            // Authentication done at least once
            this.isAuthenticated = true;
            
            bis.mark(BLOCK_SIZE);
            String pageEncoding = getContentCharSet(bis);
            if (pageEncoding == null)
            {
            	pageEncoding = encoding;
            }
            
            Reader read = new InputStreamReader(bis, encoding);
            StringBuffer page= new StringBuffer();
            
			char[] bytes = new char[BLOCK_SIZE];
			
			int len = read.read(bytes, 0, BLOCK_SIZE);			
			while (len > 0)
			{
				page.append(bytes, 0, len);
				len = read.read(bytes, 0, BLOCK_SIZE);
			}
			
            //Done
            read.close();
            
            // Rewrite
			// Post Process for generated page		
			PortletURL actionURL = response.createActionURL();
			ScriptPostProcess processor = new ScriptPostProcess();
			processor.setInitalPage(page);
			processor.postProcessPage(actionURL, ACTION_PARAMETER_SSOPROXY);
			String finalPage = processor.getFinalizedPage();
			
			// Write the page
			response.getWriter().println(finalPage);
        
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
    }
    

    public void doEdit(RenderRequest request, RenderResponse response)
    throws PortletException, IOException
    {
         super.doEdit(request, response);
    }

    /*
     * Helper methods
     */
    private Subject getSubject()
    {
        AccessControlContext context = AccessController.getContext();
        return Subject.getSubject(context);         
    }
    
    private String getContentCharSet(InputStream is) throws IOException
    {
        if (!is.markSupported())
        {
            return null;
        }

        byte[] buf = new byte[BLOCK_SIZE];
        try
        {
            is.read(buf, 0, BLOCK_SIZE);
            String content = new String(buf, "ISO-8859-1");
            String lowerCaseContent = content.toLowerCase();
            int startIndex = lowerCaseContent.indexOf("<head");
            if (startIndex == -1)
            {
                startIndex = 0;
            }
            int endIndex = lowerCaseContent.indexOf("</head");
            if (endIndex == -1)
            {
                endIndex = content.length();
            }
            content = content.substring(startIndex, endIndex);

            StringTokenizer st = new StringTokenizer(content, "<>");
            while (st.hasMoreTokens())
            {
                String element = st.nextToken();
                String lowerCaseElement = element.toLowerCase();
                if (lowerCaseElement.startsWith("meta") && lowerCaseElement.indexOf("content-type") > 0)
                {
                    StringTokenizer est = new StringTokenizer(element, " =\"\';");
                    while (est.hasMoreTokens())
                    {
                        if (est.nextToken().equalsIgnoreCase("charset"))
                        {
                            if (est.hasMoreTokens())
                            {
                                is.reset();
                                return est.nextToken();
                            }
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
        }

        is.reset();

        return null;
    }
   
}
