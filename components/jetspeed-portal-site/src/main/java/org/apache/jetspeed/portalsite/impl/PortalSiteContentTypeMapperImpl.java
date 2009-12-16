package org.apache.jetspeed.portalsite.impl;

import java.util.List;

import org.apache.jetspeed.om.page.Page;
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
    
    private List<ContentTypeMapping> contentTypeMappings;
    private List<RequestPathMapping> requestPathMappings;
    
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
        this(contentTypeMappings, null);
    }

    /**
     * Construct default PortalSite content type mapper implementation.
     * 
     * @param contentTypeMappings mappings to determine content type from request path
     * @param requestPathMappings mappings to determine request path from server name,
     *                            content type, and request path
     */
    public PortalSiteContentTypeMapperImpl(List<ContentTypeMapping> contentTypeMappings, List<RequestPathMapping> requestPathMappings)
    {        
        this.contentTypeMappings = contentTypeMappings;
        this.requestPathMappings = requestPathMappings;
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
     * @see org.apache.jetspeed.portalsite.PortalSiteContentTypeMapper#mapRequestPath(java.lang.String, java.lang.String, java.lang.String)
     */
    public String mapRequestPath(String serverName, String contentType, String requestPath)
    {
        if (requestPathMappings != null)
        {
            boolean requestPathMapped = false;
            for (RequestPathMapping mapping : requestPathMappings)
            {
                String mappedRequestPath = mapping.map(serverName, contentType, requestPath);
                if ((mappedRequestPath != null) && !mappedRequestPath.equals(requestPath))
                {
                    requestPath = mappedRequestPath;
                    requestPathMapped = true;
                }
            }
            if (requestPathMapped)
            {
                return requestPath;
            }
        }
        return null;
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
        return null;
    }
}
