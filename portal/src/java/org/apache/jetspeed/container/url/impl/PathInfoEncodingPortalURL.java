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

import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.request.RequestContext;

/**
 * PathInfoEncodingPortalURL encodes the NavigationalState as PathInfo element
 * *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class PathInfoEncodingPortalURL extends AbstractPortalURL
{
    public PathInfoEncodingPortalURL(RequestContext context, NavigationalState navState)
    {
        super(context, navState);
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
        StringBuffer buffer = new StringBuffer(getBaseURL(secure));
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
}
