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

/**
 * Special version of PathInfoEncodingPortalURL that plays nice with Turbine
 * path-info management and thus can be safely used with FUsion for encapsulation
 * within Jetspeed 1
 * *
 * @version $Id$
 */
public class FusionPathInfoEncodingPortalURL extends PathInfoEncodingPortalURL
{
    public FusionPathInfoEncodingPortalURL(HttpServletRequest request, String characterEncoding, NavigationalState navState)
    {
        super(request, characterEncoding, navState);
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
                    if (!foundNavState && token.equals(getNavigationalStateParameterName()))
					{
							// Remove any path component that exactly matchs the navigation
							// state name and is found before the state token. This is assumed
							// to be the Turbine compatible varaible name for Fusion.
					}
                    else
					{
						buffer.append("/");
						buffer.append(token);
					}
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
			// Duplication of parameter name to be compatible with Turbine 
			// handling of path info
			buffer.append("/");
            buffer.append(getNavigationalStateParameterName());
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
    }  }
