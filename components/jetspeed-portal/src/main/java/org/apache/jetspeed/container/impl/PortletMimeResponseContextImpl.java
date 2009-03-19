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

import java.util.Locale;

import javax.portlet.CacheControl;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.pluto.container.PortletContainer;
import org.apache.pluto.container.PortletMimeResponseContext;
import org.apache.pluto.container.PortletURLProvider;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.container.providers.PortletURLProviderImpl;
import org.apache.pluto.container.PortletURLProvider.TYPE;

/**
 * @version $Id$
 *
 */
public abstract class PortletMimeResponseContextImpl extends PortletResponseContextImpl implements PortletMimeResponseContext
{
    private static class CacheControlImpl implements CacheControl
    {
        private String eTag;
        private int expirationTime;
        private boolean publicScope;
        private boolean cachedContent;
        
        public CacheControlImpl()
        {
        }

        public boolean useCachedContent()
        {
            return cachedContent;
        }

        public String getETag()
        {
            return this.eTag;
        }

        public int getExpirationTime()
        {
            return expirationTime;
        }

        public boolean isPublicScope()
        {
            return publicScope;
        }

        public void setETag(String eTag)
        {
            this.eTag = eTag;
        }

        public void setExpirationTime(int expirationTime)
        {
            this.expirationTime = expirationTime;
        }

        public void setPublicScope(boolean publicScope)
        {
            this.publicScope = publicScope;
        }

        public void setUseCachedContent(boolean cachedContent)
        {
            this.cachedContent = cachedContent;
        }
    }
    
    private CacheControl cacheControl;
    
    public PortletMimeResponseContextImpl(PortletContainer container, HttpServletRequest containerRequest,
                                          HttpServletResponse containerResponse, PortletWindow window)
    {
        super(container, containerRequest, containerResponse, window);
    }
    
    public void close()
    {
        cacheControl = null;
        super.close();
    }

    public CacheControl getCacheControl()
    {
        if (isClosed())
        {
            return null;
        }        
        if (cacheControl == null)
        {
            cacheControl = new CacheControlImpl();
        }
        return cacheControl;
    }

    public String getCharacterEncoding()
    {
        return isClosed() ? null : getServletResponse().getCharacterEncoding();
    }

    public String getContentType()
    {
        return isClosed() ? null : getServletResponse().getContentType();
    }

    public Locale getLocale()
    {
        return isClosed() ? null : getServletResponse().getLocale();
    }

    public PortletURLProvider getPortletURLProvider(TYPE type)
    {
        if (!isClosed())
        {
            return new PortletURLProviderImpl(getRequestContext().getPortalURL(), getPortletWindow(), type);
        }
        return null;
    }
}
