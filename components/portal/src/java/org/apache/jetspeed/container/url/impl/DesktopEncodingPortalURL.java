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

import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.BasePortalURL;

/**
 * PathInfoEncodingPortalURL encodes the NavigationalState as PathInfo element
 * *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id: PathInfoEncodingPortalURL.java 367856 2006-01-11 01:04:09Z taylor $
 */
public class DesktopEncodingPortalURL extends AbstractPortalURL
{
    private final String javascriptDoRender;
    
    public DesktopEncodingPortalURL(NavigationalState navState, PortalContext portalContext, String javascriptDoRender, BasePortalURL base)
    {
        super(navState, portalContext, base);
        this.javascriptDoRender = javascriptDoRender;        
    }

    public DesktopEncodingPortalURL(NavigationalState navState, PortalContext portalContext, String javascriptDoRender)
    {
        super(navState, portalContext);
        this.javascriptDoRender = javascriptDoRender;                
    }

    public DesktopEncodingPortalURL(String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        super(characterEncoding, navState, portalContext);
        this.javascriptDoRender = null;
    }

    public DesktopEncodingPortalURL(HttpServletRequest request, String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        super(request, characterEncoding, navState, portalContext);
        this.javascriptDoRender = null;        
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
        StringBuffer buffer = new StringBuffer("");
        if (this.javascriptDoRender != null)
        {
            buffer.append(this.javascriptDoRender + "(\"");
        }        
        buffer.append(getBaseURL(secure));
        buffer.append(getBasePath());
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
        if (this.javascriptDoRender != null)
        {
            buffer.append("\")");
        }
        return buffer.toString();
    }        
}
