/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.container.url.impl;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.pluto.om.window.PortletWindow;

/**
 * DesktopEncodingPortalURL encodes URLs as javascript calls 
 * The signature for the javascript call is based on teh constructor argument <code>javascriptDoRender</code>
 * The script method requires two parameters:
 *   1. URL the portlet pipeline URL
 *   2. the entity id of the portlet to render
 * Example URL for a javascript method doRender:  <code>doRender("http://localhost/jetspeed/portlet", "33")</code>
 * *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id: PathInfoEncodingPortalURL.java 367856 2006-01-11 01:04:09Z taylor $
 */
public class DesktopEncodingPortalURL extends AbstractPortalURL
{
    protected final String javascriptDoRender;
    protected final String javascriptDoAction;    
    protected String baseActionPath;;
    
    public DesktopEncodingPortalURL(NavigationalState navState, PortalContext portalContext, String javascriptDoRender, String javascriptDoAction, BasePortalURL base)
    {
        super(navState, portalContext, base);
        this.javascriptDoRender = javascriptDoRender;
        this.javascriptDoAction = javascriptDoAction;        
    }

    public DesktopEncodingPortalURL(NavigationalState navState, PortalContext portalContext, String javascriptDoRender, String javascriptDoAction)
    {
        super(navState, portalContext);
        this.javascriptDoRender = javascriptDoRender;                
        this.javascriptDoAction = javascriptDoAction;
    }

    public DesktopEncodingPortalURL(String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        super(characterEncoding, navState, portalContext);
        this.javascriptDoRender = null;
        this.javascriptDoAction = null;
    }

    public DesktopEncodingPortalURL(HttpServletRequest request, String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        super(request, characterEncoding, navState, portalContext);
        this.javascriptDoRender = null;
        this.javascriptDoAction = null;
    }

    protected void decodePathAndNavigationalState(HttpServletRequest request)
    {
        String path = null;
        String encodedNavState = null;

        String pathInfo = request.getPathInfo();
        if (pathInfo != null)
        {
            StringTokenizer tokenizer = new StringTokenizer(request.getPathInfo(),"/");
            StringBuffer buffer = new StringBuffer();
            String token;
            boolean foundNavState = false;
            String navStatePrefix = getNavigationalStateParameterName() +":";
            while (tokenizer.hasMoreTokens())
            {
                token = tokenizer.nextToken();
                if (!foundNavState && token.startsWith(navStatePrefix))
                {
                    foundNavState = true;
                    if ( token.length() > navStatePrefix.length() )
                    {
                        encodedNavState = token.substring(navStatePrefix.length());
                    }
                }
                else
                {
                    buffer.append("/");
                    buffer.append(token);
                }
            }
            if ( buffer.length() > 0 )
            {
                path = buffer.toString();
            }
            else
            {
                path = "/";
            }
        }
        setPath(path);
        setEncodedNavigationalState(encodedNavState);
    }

    protected String createPortletURL(String encodedNavState, boolean secure)
    {
        return createPortletURL(encodedNavState, secure, null, false);
    }
    
    protected String createPortletURL(String encodedNavState, boolean secure, PortletWindow window, boolean action)
    {   
        StringBuffer buffer = new StringBuffer("");
        if (action)
        {
            if (this.javascriptDoAction != null)
            {
                buffer.append(this.javascriptDoAction + "('");
            }            
        }
        else
        {
            if (this.javascriptDoRender != null)
            {
                buffer.append(this.javascriptDoRender + "(&quot;");
            }            
        }   
        buffer.append(getBaseURL(secure));
        if (action)
        {
            buffer.append(this.baseActionPath);
        }
        else
        {
            buffer.append(getBasePath());            
        }            
        if ( encodedNavState != null )
        {
            buffer.append("/");
            buffer.append(getNavigationalStateParameterName());
            buffer.append(":");
            buffer.append(encodedNavState);
        }
        if ( getPath() != null )
        {
            buffer.append(getPath());
        }
        if (action)
        {
            if (this.javascriptDoAction != null)            
            {
                if (window != null)
                {
                    buffer.append("&quot;,&quot;");
                    buffer.append(window.getPortletEntity().getId());
                    buffer.append("&quot;, this");                
                }
                buffer.append(")");
            }
        }
        else
        {
            if (this.javascriptDoRender != null)            
            {
                if (window != null)
                {
                    buffer.append("&quot;,&quot;");
                    buffer.append(window.getPortletEntity().getId());
                    buffer.append("&quot;");                
                }
                buffer.append(")");
            }            
        }
        System.out.println("*** " + buffer.toString());
        return buffer.toString();
    }        
    
    public String createPortletURL(PortletWindow window, Map parameters, PortletMode mode, WindowState state, boolean action, boolean secure)
    {
        try
        {
            return createPortletURL(this.getNavigationalState().encode(window,parameters,mode,state,action), secure, window, action);
        }
        catch (UnsupportedEncodingException e)
        {
            // should never happen
            e.printStackTrace();
            // to keep the compiler happy
            return null;
        }
    }
    
    protected void decodeBasePath(HttpServletRequest request)
    {
        super.decodeBasePath(request);
        this.baseActionPath = contextPath + "/action";
    }
    
}
