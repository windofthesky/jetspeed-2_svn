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
package org.apache.jetspeed.services.information;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jetspeed.Jetspeed;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.services.information.ResourceURLProvider;

/**
 * <p>
 * ResourceURLProviderImpl
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class ResourceURLProviderImpl implements ResourceURLProvider
{
    private PortletWindow portletWindow = null;
    private String stringUrl = "";
    private String base = "";

    public ResourceURLProviderImpl(DynamicInformationProviderImpl provider, PortletWindow portletWindow)
    {
        this.portletWindow = portletWindow;
        // this.base = PortalEnvironment.getPortalEnvironment(provider.request).getRequestedPortalURL().getBaseURLexcludeContext();
        this.base = Jetspeed.getCurrentRequestContext().getRequestedPortalURL().getBaseURL();
    }

    // ResourceURLProvider implementation.

    public void setAbsoluteURL(String path)
    {
        stringUrl = path;
    }

    public void setFullPath(String path)
    {
        stringUrl = base + path;
    }

    public String toString()
    {
        URL url = null;

        if (!"".equals(stringUrl))
        {
            try
            {
                url = new URL(stringUrl);
            }
            catch (MalformedURLException e)
            {
                throw new java.lang.IllegalArgumentException("A malformed URL has occured");
            }
        }

        return ((url == null) ? "" : url.toString());

    }

}
