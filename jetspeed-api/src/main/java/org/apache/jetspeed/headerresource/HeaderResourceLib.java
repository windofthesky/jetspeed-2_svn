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
package org.apache.jetspeed.headerresource;

import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.request.RequestContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * HeaderResourceLib static utility methods
 * 
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: HeaderResourceLib.java 188569 2006-10-21 13:35:18Z smilek $
 */
public class HeaderResourceLib
{
    protected final static String EOL = "\r\n";   // html eol
    private final static String MAILTO_URL_SCHEME = "mailto";
    private final static int MAILTO_URL_SCHEME_LEN = MAILTO_URL_SCHEME.length();
    
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
        return getPortalBaseUrl(requestContext, baseUrlAccessOverride, false);
    }
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * The optional BasePortalURL argument is provided to allow the common BasePortalURL usage by various jetspeed components 
     * to be properly supported in this url generation
     * 
     * When the fullUrl parameter is true, the scheme, servername and port will be provided in the baseUrl,
     * regardless if global property portalurl.relative.only is set to true in jetspeed.properties.
     * This is needed for HeaderResourceImpl.jetspeedGenerateBasetag() for rendering a valid base tag (for which IE requires an absolute url to work).
     * <br/>
     * Note: if portalurl.relative.only is set to true to support a Proxy based front end, better remove de (default) "header.basetag" rendering setting
     * from assembly/headtag.xml, otherwise the desktop still won't work properly behind the Proxy.
     * 
     * @return portal base url
     */
    public static String getPortalBaseUrl( RequestContext requestContext, BasePortalURL baseUrlAccessOverride, boolean fullUrl )
    {
        HttpServletRequest request = requestContext.getRequest();
        StringBuffer baseurl = new StringBuffer();
        if ( fullUrl || !requestContext.getPortalURL().isRelativeOnly() )
        {
            if ( baseUrlAccessOverride == null )
            {
                baseurl.append( request.getScheme() ).append( "://" ).append( request.getServerName() ).append( ":" ).append( request.getServerPort() );
            }
            else
            {
                baseurl.append( baseUrlAccessOverride.getServerScheme() ).append( "://" ).append( baseUrlAccessOverride.getServerName() ).append( ":" ).append( baseUrlAccessOverride.getServerPort() );
            }
        }
        baseurl.append(request.getContextPath());
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
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * Expects portalBaseUrl argument to be defined (ie. it does not call getPortalBaseUrl)
     * Also expects servletPath argument to be defined
     * 
     * @return portal base servlet url
     */
    public static String getPortalUrl( String portalBaseUrl, RequestContext requestContext, String servletPath )
    {
        HttpServletRequest request = requestContext.getRequest();
        StringBuffer portalurl = new StringBuffer();
        return portalurl.append( portalBaseUrl ).append( ( servletPath == null ) ? request.getServletPath() : servletPath ).toString();
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
        boolean isPathRelative = true;
        int colonPos = relativePath.indexOf( ':' );
        if ( colonPos != -1 )
        {
            int pathLen = relativePath.length();
            if ( colonPos <= ( pathLen - 3 ) && relativePath.charAt( colonPos + 1 ) == '/' && relativePath.charAt( colonPos + 2 ) == '/' )
            {
                isPathRelative = false;
            }
            else if ( colonPos >= MAILTO_URL_SCHEME_LEN && relativePath.substring( colonPos - MAILTO_URL_SCHEME_LEN, colonPos ).equals( MAILTO_URL_SCHEME ) )
            {
                isPathRelative = false;
            }
        }
        if ( isPathRelative )
        {
            StringBuffer path = new StringBuffer();
            String resourceurl = path.append( portalBaseUrl ).append( relativePath.startsWith( "/" ) ? "" : "/" ).append( relativePath ).toString();
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
    
    public static StringBuffer makeJSONObject( Map objectMap, boolean whenEmptyReturnNewObject )
    {
    	return makeJSONObject( null, new Map[] { objectMap }, whenEmptyReturnNewObject );
    }
    public static StringBuffer makeJSONObject( Map[] objectMaps, boolean whenEmptyReturnNewObject )
    {
    	return makeJSONObject( null, objectMaps, whenEmptyReturnNewObject );
    }
    public static StringBuffer makeJSONObject( StringBuffer jsonBuffer, Map objectMap, boolean whenEmptyReturnNewObject )
    {
    	return makeJSONObject( jsonBuffer, new Map[] { objectMap }, whenEmptyReturnNewObject );
    }
    public static StringBuffer makeJSONObject( StringBuffer jsonBuffer, Map[] objectMaps, boolean whenEmptyReturnNewObject )
    {
    	if ( jsonBuffer == null )
    		jsonBuffer = new StringBuffer();
    	
    	int added = 0;
    	int objMapsLen = ( objectMaps == null ? 0 : objectMaps.length );
    	if ( objMapsLen > 0 )
    	{
    		for ( int i = 0 ; i < objMapsLen ; i++ )
    		{
    			Map objectMap = objectMaps[i];
    			if ( objectMap != null && objectMap.size() > 0 )
    	        {
    				if ( added == 0 )
    					jsonBuffer.append( "{" );
    	        	Map.Entry objEntry;
    	        	Object objKey, objVal;
    	        	Iterator objMapIter = objectMap.entrySet().iterator();
    	        	while ( objMapIter.hasNext() )
    	        	{
    	        		objEntry = (Map.Entry)objMapIter.next();
    	        		objKey = objEntry.getKey();
    	        		if ( objKey != null )
    	        		{
    	        			if ( added > 0 )
    	        				jsonBuffer.append( ", " );
    	        			jsonBuffer.append( "\"" ).append( objKey.toString() ).append( "\":" );
    	            		objVal = objEntry.getValue();
    	            		if ( objVal == null )
    	            			objVal = "";
    	            		jsonBuffer.append( "\"" ).append( objVal.toString() ).append( "\"" );
    	            		added++;
    	        		}
    	        	}
    	        }
    		}
    	}
    	if ( added > 0 )
    	{
			jsonBuffer.append( "}" );
    	}
		else if ( whenEmptyReturnNewObject )
        {
        	jsonBuffer.append( "{}" );
        }
        else
        {
        	return null;
        }
    	return jsonBuffer;
    }
    
    public static String makeJavascriptStatement( String statement, String indent, boolean addEOL )
    {
        StringBuffer statementOut = new StringBuffer();
        if ( statement != null )
        {
            statement = statement.trim();
            if ( statement.length() > 0 )
            {
                if ( indent != null )
                {
                    statementOut.append( indent );
                }
                statementOut.append( statement );
                if ( statement.charAt( statement.length()-1 ) != ';' )
                {
                    statementOut.append( ";" );
                }
                if ( addEOL )
                {
                    statementOut.append( EOL );
                }
            }
        }
        return statementOut.toString();
    }
    public static String makeJSONStringArray( Collection<String> stringList )
    {
        return makeJSONStringArray( stringList, null );
    }
    public static String makeJSONStringArray( Collection<String> stringList, List<String> compiledUniqueValues )
    {
        if ( stringList != null && stringList.size() > 0 )
        {
            StringBuffer stringListContent = new StringBuffer();
            Iterator stringListIter = stringList.iterator();
            while ( stringListIter.hasNext() )
            {
                String value = (String)stringListIter.next();
                if ( value != null && value.length() > 0 )
                {
                    if ( stringListContent.length() > 0 )
                    {
                        stringListContent.append( ", " );
                    }
                    else
                    {
                        stringListContent.append( "[ " );
                    }
                    stringListContent.append( "\"" ).append( value ).append( "\"" );
                    if ( compiledUniqueValues != null )
                    {
                        if ( ! compiledUniqueValues.contains( value ) )
                        {
                            compiledUniqueValues.add( value );
                        }
                    }
                }
            }
            if ( stringListContent.length() > 0 )
            {
                stringListContent.append( " ]" );
                return stringListContent.toString();
            }
        }
        return null;
    }
    public static String makeJSONInteger( Object source, boolean quote )
    {
        String sourceStr = ( ( source == null ) ? (String)null : source.toString() );
        if ( sourceStr != null )
        {
            try
            {
                Integer.parseInt( sourceStr );
                if ( quote )
                {
                    sourceStr = "\"" + sourceStr + "\"";
                }
            }
            catch ( NumberFormatException nex )
            {
                sourceStr = null;
            }
        }
        return sourceStr;
    }
    
    public static String makeJSONBoolean( Object source )
    {
        String boolStr = ( ( source == null ) ? (String)null : source.toString() );
        if ( boolStr != null && ( ! boolStr.equals( "false" ) ) && ( ! boolStr.equals( "true" ) ) )
        {
            boolStr = null;
        }
        return boolStr;
    }
}