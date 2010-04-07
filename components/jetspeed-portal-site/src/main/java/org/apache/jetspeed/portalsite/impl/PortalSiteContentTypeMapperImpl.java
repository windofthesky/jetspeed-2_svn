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
package org.apache.jetspeed.portalsite.impl;

import java.util.List;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.portalsite.PortalSiteContentTypeMapper;

/**
 * This class implements the the content type mapper component
 * for use with the portal-site component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class PortalSiteContentTypeMapperImpl implements PortalSiteContentTypeMapper
{
    public static final String DEFAULT_PAGE_SYSTEM_TYPE_SUFFIX = Page.DOCUMENT_TYPE;
    public static final String [] DEFAULT_TEMPLATE_SYSTEM_TYPE_SUFFIXES = new String[]{PageTemplate.DOCUMENT_TYPE, FragmentDefinition.DOCUMENT_TYPE, DynamicPage.DOCUMENT_TYPE};

    private List<ContentTypeMapping> contentTypeMappings;
    private List<RequestPathMapping> systemRequestPathMappings;
    private List<RequestPathMapping> dynamicRequestPathMappings;
    private List<RequestPathMapping> contentRequestPathMappings;
    private boolean enableContentTypeFallback;
    
    /**
     * Construct default PortalSite content type mapper implementation.
     */
    public PortalSiteContentTypeMapperImpl()
    {        
        this(null);
    }

    /**
     * Construct default PortalSite content type mapper implementation.
     * 
     * @param contentTypeMappings mappings to determine content type from request path
     */
    public PortalSiteContentTypeMapperImpl(List<ContentTypeMapping> contentTypeMappings)
    {
        this(contentTypeMappings, null, null, null, false);
    }

    /**
     * Construct default PortalSite content type mapper implementation.
     * 
     * @param contentTypeMappings mappings to determine content type from request path
     * @param enableContentTypeFallback enable content type fallback for missing system
     *                                  type page, folder, etc. requests
     */
    public PortalSiteContentTypeMapperImpl(List<ContentTypeMapping> contentTypeMappings, boolean enableContentTypeFallback)
    {
        this(contentTypeMappings, null, null, null, enableContentTypeFallback);
    }

    /**
     * Construct default PortalSite content type mapper implementation.
     * 
     * @param contentTypeMappings mappings to determine content type from request path
     * @param dynamicPathMappings mappings to determine dynamic page path from server name,
     *                            content type, and request path
     */
    public PortalSiteContentTypeMapperImpl(List<ContentTypeMapping> contentTypeMappings, List<RequestPathMapping> dynamicPathMappings)
    {        
        this(contentTypeMappings, dynamicPathMappings, null, null, false);
    }

    /**
     * Construct default PortalSite content type mapper implementation.
     * 
     * @param contentTypeMappings mappings to determine content type from request path
     * @param dynamicPathMappings mappings to determine dynamic page path from server name,
     *                            content type, and request path
     * @param enableContentTypeFallback enable content type fallback for missing system
     *                                  type page, folder, etc. requests
     */
    public PortalSiteContentTypeMapperImpl(List<ContentTypeMapping> contentTypeMappings, List<RequestPathMapping> dynamicPathMappings, boolean enableContentTypeFallback)
    {        
        this(contentTypeMappings, dynamicPathMappings, null, null, enableContentTypeFallback);
    }

    /**
     * Construct default PortalSite content type mapper implementation.
     * 
     * @param contentTypeMappings mappings to determine content type from request path
     * @param dynamicPathMappings mappings to determine dynamic page path from server name,
     *                            content type, and request path
     * @param systemPathMappings mappings to determine system page path from server name,
     *                           content type, and request path
     * @param contentPathMappings mappings to determine external content path from server
     *                            name, content type, and request path
     * @param enableContentTypeFallback enable content type fallback for missing system
     *                                  type page, folder, etc. requests
     */
    public PortalSiteContentTypeMapperImpl(List<ContentTypeMapping> contentTypeMappings, List<RequestPathMapping> dynamicPathMappings, List<RequestPathMapping> systemPathMappings, List<RequestPathMapping> contentPathMappings, boolean enableContentTypeFallback)
    {        
        this.contentTypeMappings = contentTypeMappings;
        this.dynamicRequestPathMappings = dynamicPathMappings;
        this.systemRequestPathMappings = systemPathMappings;
        this.contentRequestPathMappings = contentPathMappings;
        this.enableContentTypeFallback = enableContentTypeFallback;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.portalsite.PortalSiteContentTypeMapper#mapContentType(java.lang.String)
     */
    public String mapContentType(String requestPath)
    {
        // match content type patterns to determine content types
        if (contentTypeMappings != null)
        {
            for (ContentTypeMapping mapping : contentTypeMappings)
            {
                String contentType = mapping.map(requestPath);
                if (contentType != null)
                {
                    return contentType;
                }
            }
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.portalsite.PortalSiteContentTypeMapper#isContentTypeFallbackEnabled()
     */
    public boolean isContentTypeFallbackEnabled()
    {
        return enableContentTypeFallback;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.portalsite.PortalSiteContentTypeMapper#mapSystemRequestPath(java.lang.String, java.lang.String, java.lang.String)
     */
    public String mapSystemRequestPath(String serverName, String contentType, String requestPath)
    {
        String mappedSystemRequestPath = mapRequestPath(systemRequestPathMappings, serverName, contentType, requestPath);
        if (mappedSystemRequestPath != null)
        {
            // replace or append page extension to mapped path
            if (!mappedSystemRequestPath.endsWith(Page.DOCUMENT_TYPE) && !mappedSystemRequestPath.endsWith(Folder.PATH_SEPARATOR))
            {
                // strip existing extension
                int lastPathSeparatorIndex = mappedSystemRequestPath.lastIndexOf(Folder.PATH_SEPARATOR_CHAR);
                int lastExtensionSeparatorIndex = mappedSystemRequestPath.lastIndexOf('.');
                if (lastExtensionSeparatorIndex > lastPathSeparatorIndex)
                {
                    mappedSystemRequestPath = mappedSystemRequestPath.substring(0, lastExtensionSeparatorIndex);
                }
                // append page extension
                mappedSystemRequestPath += Page.DOCUMENT_TYPE;
            }
        }
        return mappedSystemRequestPath;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.portalsite.PortalSiteContentTypeMapper#mapDynamicRequestPath(java.lang.String, java.lang.String, java.lang.String)
     */
    public String mapDynamicRequestPath(String serverName, String contentType, String requestPath)
    {
        return mapRequestPath(dynamicRequestPathMappings, serverName, contentType, requestPath);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.portalsite.PortalSiteContentTypeMapper#mapContentRequestPath(java.lang.String, java.lang.String, java.lang.String)
     */
    public String mapContentRequestPath(String serverName, String contentType, String requestPath)
    {
        return mapRequestPath(contentRequestPathMappings, serverName, contentType, requestPath);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.portalsite.PortalSiteContentTypeMapper#mapSystemType(java.lang.String)
     */
    public String mapSystemType(String requestPath)
    {
        // match request path suffixes to determine page requests
        if (requestPath.endsWith(DEFAULT_PAGE_SYSTEM_TYPE_SUFFIX))
        {
            return PAGE_SYSTEM_TYPE;
        }
        else
        {
            for (int i = 0; (i < DEFAULT_TEMPLATE_SYSTEM_TYPE_SUFFIXES.length); i++)
            {
                if (requestPath.endsWith(DEFAULT_TEMPLATE_SYSTEM_TYPE_SUFFIXES[i]))
                {
                    return TEMPLATE_SYSTEM_TYPE;
                }                
            }
        }
        return null;
    }

    /**
     * Map content request path using request path mappings.
     * 
     * @param requestPathMappings mappings
     * @param serverName server name
     * @param contentType mapped content type
     * @param requestPath original request path
     * @return mapped path
     */
    private String mapRequestPath(List<RequestPathMapping> requestPathMappings, String serverName, String contentType, String requestPath)
    {
        if (requestPathMappings != null)
        {
            for (RequestPathMapping mapping : requestPathMappings)
            {
                String mappedRequestPath = mapping.map(serverName, contentType, requestPath);
                if ((mappedRequestPath != null) && !mappedRequestPath.equals(requestPath))
                {
                    requestPath = mappedRequestPath;
                }
            }
            return requestPath;
        }
        return null;
    }
}
