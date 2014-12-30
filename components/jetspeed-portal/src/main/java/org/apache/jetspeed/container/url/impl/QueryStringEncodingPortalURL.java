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

/**
 * QueryStringEncodingPortalURL encodes the NavigationalState as query parameter
 * *
 * @author <a href="mailto:ate@apache.org">Ate Douma</a>
 * @version $Id$
 */
public class QueryStringEncodingPortalURL extends AbstractPortalURL
{
    public QueryStringEncodingPortalURL(NavigationalState navState, PortalContext portalContext, BasePortalURL base)
    {
        super(navState, portalContext, base);
    }

    public QueryStringEncodingPortalURL(NavigationalState navState, PortalContext portalContext)
    {
        super(navState, portalContext);
    }

    public QueryStringEncodingPortalURL(String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        super(characterEncoding, navState, portalContext);
    }

    public QueryStringEncodingPortalURL(HttpServletRequest request, String characterEncoding, NavigationalState navState, PortalContext portalContext)
    {
        super(request, characterEncoding, navState, portalContext);
    }

    protected void decodePathAndNavigationalState(HttpServletRequest request)
    {
        setEncodedNavigationalState(request.getParameter(getNavigationalStateParameterName()));
        String path = null;
        if (request.getPathInfo() != null)
        {
            path = request.getPathInfo();
            int length = path.length();
            if ( length > 1 && path.endsWith("/") )
            {
                path = path.substring(0, length-1);
            }
        }
        setPath(path);
    }
    
    protected String createPortletURL(String encodedNavigationalState, boolean secure)
    {
        StringBuffer buffer = new StringBuffer(getBaseURL(base.isSecure()));
        buffer.append(getBasePath());
        if ( getPath() != null )
        {
            buffer.append(getPath());
        }
        if ( encodedNavigationalState != null )
        {
            buffer.append("?");
            buffer.append(getNavigationalStateParameterName());
            buffer.append("=");
            buffer.append(encodedNavigationalState);
        }
        return buffer.toString();
    }    
}
