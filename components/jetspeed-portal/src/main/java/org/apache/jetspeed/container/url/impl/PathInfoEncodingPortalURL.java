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
package org.apache.jetspeed.container.url.impl;

import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.BasePortalURL;

import javax.servlet.http.HttpServletRequest;
import java.util.StringTokenizer;

/**
 * PathInfoEncodingPortalURL encodes the NavigationalState as PathInfo element
 * *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class PathInfoEncodingPortalURL extends AbstractPortalURL
{
    public PathInfoEncodingPortalURL(NavigationalState navState, PortalContext portalContext, BasePortalURL base)
    {
        super(navState, portalContext, base);
    }

    public PathInfoEncodingPortalURL(NavigationalState navState, PortalContext portalContext)
    {
        super(navState, portalContext);
    }

    public PathInfoEncodingPortalURL(String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        super(characterEncoding, navState, portalContext);
    }

    public PathInfoEncodingPortalURL(HttpServletRequest request, String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        super(request, characterEncoding, navState, portalContext);
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
            if (pathInfo.endsWith("/"))
            {
                buffer.append("/");                
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
        StringBuffer buffer = new StringBuffer(getBaseURL(base.isSecure()));
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
        return buffer.toString();
    }

    public boolean isPathInfoEncodingNavState()
    {
        return true;
    }        
}
