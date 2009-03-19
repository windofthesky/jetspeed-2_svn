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

package org.apache.jetspeed.container.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.container.PortletActionResponseContext;
import org.apache.pluto.container.PortletContainer;
import org.apache.jetspeed.container.PortletWindow;

/**
 * @version $Id$
 *
 */
public class PortletActionResponseContextImpl extends PortletStateAwareResponseContextImpl implements
                PortletActionResponseContext
{
    private boolean redirect;
    private String redirectLocation;
    private String renderURLParamName;
    
    public PortletActionResponseContextImpl(PortletContainer container, HttpServletRequest containerRequest,
                                            HttpServletResponse containerResponse, PortletWindow window)
    {
        super(container, containerRequest, containerResponse, window);
    }

    public String getResponseURL()
    {
        if (!isReleased())
        {
            close();
            if (!redirect || renderURLParamName != null)
            {               
                String portalURL = getRequestContext().getPortalURL().getPortalURL();
                if (redirect)
                {
                    if (redirectLocation.indexOf("://") != -1 && portalURL.indexOf("://")==-1)
                    {
                        portalURL = getRequestContext().getPortalURL().getBaseURL() + portalURL;
                    }
                    try
                    {
                        return redirectLocation + "?" + URLEncoder.encode(renderURLParamName, "UTF-8") + "=" + URLEncoder.encode(portalURL, "UTF-8");
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        // Cannot happen: UTF-8 is a buildin/required encoder
                        return null;
                    }
                }
                else
                {
                    return portalURL;
                }
            }
            else
            {
                return redirectLocation;
            }
        }
        return null;
    }

    public boolean isRedirect()
    {
        return redirect;
    }

    public void setRedirect(String location)
    {
        setRedirect(location, null);
    }

    public void setRedirect(String location, String renderURLParamName)
    {
        if (!isClosed())
        {
            this.redirectLocation = location;
            this.renderURLParamName = renderURLParamName;
            this.redirect = true;
        }
    }
}
