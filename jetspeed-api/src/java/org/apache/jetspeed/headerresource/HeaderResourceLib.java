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
package org.apache.jetspeed.headerresource;

import javax.servlet.http.HttpServletRequest;

import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.request.RequestContext;

/**
 * HeaderResourceLib static utility methods
 * 
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: HeaderResourceLib.java 188569 2006-10-21 13:35:18Z smilek $
 */
public class HeaderResourceLib
{
    public static int getHeaderTypeId( String headerType )
    {
        int headerTypeNumber = -1;
        if ( headerType != null )
        {
            if ( headerType.equals( HeaderResource.HEADER_TYPE_SCRIPT_BLOCK ) )
            {
                headerTypeNumber = HeaderResource.HEADER_TYPE_ID_SCRIPT_BLOCK;
            }
            else if ( headerType.equals( HeaderResource.HEADER_TYPE_SCRIPT_BLOCK_START ) )
            {
                headerTypeNumber = HeaderResource.HEADER_TYPE_ID_SCRIPT_BLOCK_START;
            }
            else if ( headerType.equals( HeaderResource.HEADER_TYPE_SCRIPT_TAG ) )
            {
                headerTypeNumber = HeaderResource.HEADER_TYPE_ID_SCRIPT_TAG;
            }
            else if ( headerType.equals( HeaderResource.HEADER_TYPE_SCRIPT_BLOCK_END ) )
            {
                headerTypeNumber = HeaderResource.HEADER_TYPE_ID_SCRIPT_BLOCK_END;
            }
            else if ( headerType.equals( HeaderResource.HEADER_TYPE_STYLE_BLOCK ) )
            {
                headerTypeNumber = HeaderResource.HEADER_TYPE_ID_STYLE_BLOCK;
            }
            else if ( headerType.equals( HeaderResource.HEADER_TYPE_LINK_TAG ) )
            {
                headerTypeNumber = HeaderResource.HEADER_TYPE_ID_LINK_TAG;
            }
            else if ( headerType.equals( HeaderResource.HEADER_TYPE_BASE_TAG ) )
            {
                headerTypeNumber = HeaderResource.HEADER_TYPE_ID_BASE_TAG;
            }
        }
        return headerTypeNumber;
    }
    
    public static String getHeaderType( Integer headerTypeId )
    {
        String headerType = null;
        if ( headerTypeId != null )
        {
            int typeid = headerTypeId.intValue();
            if ( typeid == HeaderResource.HEADER_TYPE_ID_SCRIPT_BLOCK )
            {
                headerType = HeaderResource.HEADER_TYPE_SCRIPT_BLOCK;
            }
            else if ( typeid == HeaderResource.HEADER_TYPE_ID_SCRIPT_BLOCK_START )
            {
                headerType = HeaderResource.HEADER_TYPE_SCRIPT_BLOCK_START ;
            }
            else if ( typeid == HeaderResource.HEADER_TYPE_ID_SCRIPT_TAG )
            {
                headerType = HeaderResource.HEADER_TYPE_SCRIPT_TAG;
            }
            else if ( typeid == HeaderResource.HEADER_TYPE_ID_SCRIPT_BLOCK_END )
            {
                headerType = HeaderResource.HEADER_TYPE_SCRIPT_BLOCK_END;
            }
            else if ( typeid == HeaderResource.HEADER_TYPE_ID_STYLE_BLOCK )
            {
                headerType = HeaderResource.HEADER_TYPE_STYLE_BLOCK;
            }
            else if ( typeid == HeaderResource.HEADER_TYPE_ID_LINK_TAG )
            {
                headerType = HeaderResource.HEADER_TYPE_LINK_TAG;
            }
            else if ( typeid == HeaderResource.HEADER_TYPE_ID_BASE_TAG )
            {
                headerType = HeaderResource.HEADER_TYPE_BASE_TAG;
            }
        }
        return headerType;
    }
    
    // get portal urls - these are here as an attempt to reduce as much code duplication as possible
    //                 - some of the methods are constructed oddly due to their dual goal of reducing
    //                   duplication while allowing for caller caching
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public static String getPortalBaseUrl( RequestContext requestContext )
    {
        return getPortalBaseUrl( requestContext, null );
    }
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * The optional BasePortalURL argument is provided to allow the common BasePortalURL usage by various jetspeed components 
     * to be properly supported in this url generation
     * 
     * @return portal base url
     */
    public static String getPortalBaseUrl( RequestContext requestContext, BasePortalURL baseUrlAccessOverride )
    {
        HttpServletRequest request = requestContext.getRequest();
        StringBuffer baseurl = new StringBuffer();
        if ( baseUrlAccessOverride == null )
        {
            baseurl.append( request.getScheme() ).append( "://" ).append( request.getServerName() ).append( ":" ).append( request.getServerPort() ).append( request.getContextPath() );
        }
        else
        {
            baseurl.append( baseUrlAccessOverride.getServerScheme() ).append( "://" ).append( baseUrlAccessOverride.getServerName() ).append( ":" ).append( baseUrlAccessOverride.getServerPort() ).append( request.getContextPath() );
        }
        return baseurl.toString();
    }
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * Expects portalBaseUrl argument to be defined (ie. it does not call getPortalBaseUrl)
     * 
     * @return portal base servlet url
     */
    public static String getPortalUrl( String portalBaseUrl, RequestContext requestContext )
    {
        HttpServletRequest request = requestContext.getRequest();
        StringBuffer portalurl = new StringBuffer();
        return portalurl.append( portalBaseUrl ).append( request.getServletPath() ).toString();
    }
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * Expects portalUrl argument to be defined (ie. it does not call getPortalUrl)
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public static String getPortalUrl( String relativePath, String portalUrl )
    {
        return getPortalUrl( relativePath, portalUrl, false, null );
    }
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * Expects portalUrl argument to be defined (ie. it does not call getPortalUrl)
     * RequestContext argument is needed only when encode argument is true (it's needed to call HttpServletResponse.encodeURL())
     * 
     * Method signature/behavior is a bit strange because this is a static method trying to accomodate
     * callers that lazy cache portalUrl string
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public static String getPortalUrl( String relativePath, String portalUrl, boolean encode, RequestContext requestContext )
    {
        if ( relativePath == null )
            relativePath = "";
        if ( relativePath.indexOf( "://" ) == -1 && relativePath.indexOf( "mailto:" ) == -1 )
        {
            StringBuffer path = new StringBuffer();
            String portalurl = path.append( portalUrl ).append( relativePath ).toString();
            if ( encode && requestContext != null )
            {
                return requestContext.getResponse().encodeURL( portalurl );
            }
            else
            {
                return portalurl;
            }
        }
        return relativePath;
    }
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * Expects portalBaseUrl argument to be defined (ie. it does not call getPortalBaseUrl)
     * 
     * @return portal base url with relativePath argument appended
     */
    public static String getPortalResourceUrl( String relativePath, String portalBaseUrl )
    {
        return getPortalResourceUrl( relativePath, portalBaseUrl, false, null );
    }
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * Expects portalBaseUrl argument to be defined (ie. it does not call getPortalBaseUrl)
     * RequestContext argument is needed only when encode argument is true (it's needed to call HttpServletResponse.encodeURL())
     * 
     * Method signature/behavior is a bit strange because this is a static method trying to accomodate
     * callers that lazy cache portalBaseUrl string
     * 
     * @return portal base url with relativePath argument appended
     */
    public static String getPortalResourceUrl( String relativePath, String portalBaseUrl, boolean encode, RequestContext requestContext )
    {
        if ( relativePath == null )
            relativePath = "";
        if ( relativePath.indexOf( "://" ) == -1 && relativePath.indexOf( "mailto:" ) == -1 )
        {
            StringBuffer path = new StringBuffer();
            String resourceurl = path.append( portalBaseUrl ).append( relativePath ).toString();
            if ( encode && requestContext != null )
            {
                return requestContext.getResponse().encodeURL( resourceurl );
            }
            else
            {
                return resourceurl;
            }
        }
        return relativePath;
    }
}