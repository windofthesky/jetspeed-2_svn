package org.apache.jetspeed.portalsite.impl;

import java.util.regex.Matcher;

import org.apache.jetspeed.om.page.DynamicPage;

/**
 * This class specifies a request mapping definition for use
 * by the portal-site content type mapper component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class RequestPathMapping extends AbstractPatternMapping
{
    private String serverName;
    private String contentType;
    private String mappedPath;
    
    /**
     * Construct request path mapping for any server name and
     * content type.
     * 
     * @param pattern request path find pattern
     * @param mappedPath resulting request path
     */
    public RequestPathMapping(String pattern, String mappedPath)
    {
        this(null, pattern, mappedPath);
    }

    /**
     * Construct request path mapping for specified content type and
     * any server name.
     * 
     * @param contentType request content path
     * @param pattern request path find pattern
     * @param mappedPath resulting request path
     */
    public RequestPathMapping(String contentType, String pattern, String mappedPath)
    {
        this(null, contentType, pattern, mappedPath);
    }

    /**
     * Construct request path mapping for specified content type and
     * server name or server domain name. Server domain names must
     * start with a '.' character.
     * 
     * @param serverName request server name or server domain name
     * @param contentType request content path
     * @param pattern request path find pattern
     * @param mappedPath resulting request path
     */
    public RequestPathMapping(String serverName, String contentType, String pattern, String mappedPath)
    {
        super(pattern);
        this.serverName = serverName;
        this.contentType = contentType;
        this.mappedPath = mappedPath;
    }
    
    /**
     * Match request path pattern against request path, replacing
     * all subsequence expressions in the mapped path.
     * 
     * @param requestServerName request server name to match
     * @param requestContentType request content type to match
     * @param requestPath request path to map
     * @return mapped request path or null if pattern not found
     */
    public String map(String requestServerName, String requestContentType, String requestPath)
    {
        // match server name or server domain name if specified
        if ((serverName != null) && (requestServerName != null) && !serverName.equals(requestServerName) && (!serverName.startsWith(".") || !requestServerName.endsWith(serverName)))
        {
            return null;
        }
        // match content type if specified
        if ((contentType != null) && !contentType.equals(DynamicPage.WILDCARD_CONTENT_TYPE) && (requestContentType != null) && !contentType.equals(requestContentType))
        {
            return null;
        }
        // find/replace pattern in request path
        Matcher patternMatcher = getPatternMatcher(requestPath);
        if (patternMatcher.find())
        {
            return patternMatcher.replaceAll(mappedPath);
        }
        return null;
    }
}
