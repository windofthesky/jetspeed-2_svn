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
package org.apache.jetspeed.container.providers;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.jetspeed.request.RequestContext;
import org.apache.pluto.PortletWindow;
import org.apache.pluto.spi.ResourceURLProvider;

/**
 * <p>
 * ResourceURLProviderImpl
 * </p>
 * 
 * 
 * @ * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * 
 * @version $ $
 * 
 */
public class ResourceURLProviderImpl implements ResourceURLProvider
{

    private String stringUrl = "";

    private String base = "";

    public ResourceURLProviderImpl(RequestContext context,
            PortletWindow portletWindow)
    {
        // this.base = context.getPortalURL().getBaseURL();
    }

    public void setAbsoluteURL(String path)
    {
        // stringUrl = base + path;
        stringUrl = path;
    }

    public void setFullPath(String path)
    {
        stringUrl = path;
    }

    public String toString()
    {
/*      TODO: review if we actually do need the Pluto solution
              currently this breaks if using relative paths only
              as then base == "" resulting in a MalformedURLException
              
        URL url = null;

        if (!"".equals(stringUrl))
        {
            try
            {
                url = new URL(stringUrl);
            }
            catch (MalformedURLException e)
            {
                throw new java.lang.IllegalArgumentException(
                        "A malformed URL has occured");
            }
        }
        return ((url == null) ? "" : url.toString());
*/        
        return stringUrl;
    }

}