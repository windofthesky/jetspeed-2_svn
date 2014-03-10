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
package org.apache.jetspeed.headerresource.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceLib;
import org.apache.jetspeed.portlet.HeaderPhaseSupportConstants;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Default implementation for HeaderResource
 * 
 * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: HeaderResourceImpl.java 188569 2005-05-13 13:35:18Z weaver $
 */
public class HeaderResourceImpl implements HeaderResource
{
    protected final static Logger log = LoggerFactory.getLogger( HeaderResourceImpl.class );
    
    protected final static String EOL = "\r\n";   // html eol
    protected final static String UNNAMED_CONTENT_HEADER_NAME = "org.apache.jetspeed.headerresource.unnamed";
    
    private RequestContext requestContext;
    
    // base portal url to override default url server info from servlet
    private BasePortalURL baseUrlAccess = null;
    
    private boolean isDesktop;
    
    private Map<String, Object> headerConfiguration;
    

    // ... mutable output tracking
    //     - when depending on this feature, one HeaderResourceImpl instance must process all header inclusion
    private HashMap namedResourcesAlreadyOutput;
    
    // ... as needed, these are obtained from request attributes
    private Map headerDynamicConfiguration;
    private Map headerNamedResources;
    private Map headerNamedResourcesAddedFragments;
    private Map headerResourceRegistry;
    
    // ... save generated portal urls to avoid duplicate effort
    private String portalBaseUrl;
    private String portalUrl;
    
    /**
     * Default Constructor
     * 
     * @param context
     */
    public HeaderResourceImpl( RequestContext context )
    {
        this.requestContext = context;
    }
    public HeaderResourceImpl( RequestContext context, BasePortalURL baseUrlAccess, boolean isDesktop, Map<String, Object> headerConfiguration )
    {
        this.requestContext = context;
        this.baseUrlAccess = baseUrlAccess;
        
        this.isDesktop = isDesktop;
        
        this.headerConfiguration = headerConfiguration;
    }
        
    /**
     * Output all content (that has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String getContent()
    {
        StringBuffer header = new StringBuffer();
        getNamedResourceContent( null, false, header );
        getUnnamedContent( header );
        return header.toString();
    }
    
    /**
     * Output all content (that has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String toString()
    {
        return getContent();
    }

    /**
     * Output all unnamed (getHeaderInfoSet()) content (that has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String getUnnamedContent()
    {
        StringBuffer header = new StringBuffer();
        getUnnamedContent( header );
        return header.toString();
    }

    /**
     * Output all getHeaderSections() content (that has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String getNamedContent()
    {
        StringBuffer header = new StringBuffer();
        getNamedResourceContent( null, false, header );
        return header.toString();
    }
    
    /**
     * Output the one getHeaderSections() content entry with a key that matches headerName (if it has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String getNamedContent( String headerName )
    {
        StringBuffer header = new StringBuffer();
        getNamedResourceContent( headerName, false, header );
        return header.toString();
    }
    
    /**
     * Output getHeaderSections() content entries with key prefixes that match headerNamePrefix (if it has not already been output)
     * 
     * @return content string for inclusion in html &lt;head&gt;
     */
    public String getNamedContentForPrefix( String headerNamePrefix )
    {
        if ( headerNamePrefix == null )
            headerNamePrefix = "";
        if ( ! headerNamePrefix.endsWith( "." ) )
            headerNamePrefix = headerNamePrefix + ".";
        StringBuffer header = new StringBuffer();
        getNamedResourceContent( headerNamePrefix, true, header );
        return header.toString();
    }
    
    /*
     * Output all getHeaderInfoSet() content (that has not already been output)
     */
    protected void getUnnamedContent( StringBuffer header )
    {
        HashMap namedResourcesInOutput = getNamedResourcesAlreadyOutput();
        if ( namedResourcesInOutput == null )
        {
            namedResourcesInOutput = new HashMap();
            setNamedResourcesAlreadyOutput( namedResourcesInOutput );
        }
        if ( ! namedResourcesInOutput.containsKey( UNNAMED_CONTENT_HEADER_NAME ) )
        {
            namedResourcesInOutput.put( UNNAMED_CONTENT_HEADER_NAME, Boolean.TRUE );
            Set headerInfoSet = getHeaderInfoSet();
            for ( Iterator ite = headerInfoSet.iterator(); ite.hasNext(); )
            {
                header.append( ((HeaderInfo) ite.next()).toString() );
                header.append( EOL );
            }
        }
    }
    
    /*
     * Output getHeaderSections() content (that has not already been output) with regard to optional match arguments
     */
    protected void getNamedResourceContent( String headerNameMatch, boolean headerNameMatchPrefixOnly, StringBuffer header )
    {
        List headerOrderList = getHeaderSectionOrderList( false );
        if ( headerOrderList != null && headerOrderList.size() > 0 )
        {
            HashMap namedResourcesInOutput = getNamedResourcesAlreadyOutput();
            if ( namedResourcesInOutput == null )
            {
                namedResourcesInOutput = new HashMap();
                setNamedResourcesAlreadyOutput( namedResourcesInOutput );
            }
            Map namedResources = getHeaderSections();
            Map dynamicConfig = getHeaderDynamicConfiguration();
            Map headerTypes = getHeaderSectionTypes( false );
            Map headerRsrcRegistry = getHeaderResourceRegistry();
            HashMap headerReqFlagResults = new HashMap();
            boolean inScriptBlock = false;
            boolean inStyleBlock = false;
            Iterator headerOrderListIter = headerOrderList.iterator();
            while ( headerOrderListIter.hasNext() )
            {
                String headerName = (String)headerOrderListIter.next();
                if ( namedResourcesInOutput.containsKey( headerName ) )
                {
                    continue;
                }
                if ( headerNameMatch != null )
                {
                    if ( headerNameMatchPrefixOnly )
                    {
                        if ( ! headerName.startsWith( headerNameMatch ) )
                        {
                            continue;
                        }
                    }
                    else
                    {
                        if ( ! headerName.equals( headerNameMatch ) )
                        {
                            continue;
                        }
                    }
                }
                boolean includeHeader = true;
                Object[] headerTypePair = ( ( headerTypes != null ) ? (Object[])headerTypes.get( headerName ) : (Object[])null );
                String headerReqFlag = ( ( headerTypePair != null ) ? (String)headerTypePair[1] : (String)null );
                if ( headerReqFlag != null && headerReqFlag.length() > 0 )
                {
                    Boolean headerReqFlagResult = (Boolean)headerReqFlagResults.get( headerReqFlag );
                    if ( headerReqFlagResult == null )
                    {
                        headerReqFlagResult = Boolean.FALSE;
                        Object headerReqFlagValObj = dynamicConfig.get( headerReqFlag );
                        if ( headerReqFlagValObj != null )
                            headerReqFlagResult = new Boolean( headerReqFlagValObj.toString() );
                        headerReqFlagResults.put( headerReqFlag, headerReqFlagResult );
                    }
                    includeHeader = headerReqFlagResult.booleanValue();
                }
                if ( includeHeader )
                {
                    namedResourcesInOutput.put( headerName, Boolean.TRUE );
                    Integer headerTypeIdObj = ( ( headerTypePair != null ) ? (Integer)headerTypePair[0] : (Integer)null );
                    int headerTypeId = ( ( headerTypeIdObj != null ) ? headerTypeIdObj.intValue() : HeaderResource.HEADER_TYPE_ID_SCRIPT_BLOCK );

                    boolean requiresScriptBlock = false;
                    boolean requiresStyleBlock = false;
                    boolean preCloseBlock = false;
                    boolean postCloseBlock = false;
                    
                    switch ( headerTypeId )
                    {
                        case HeaderResource.HEADER_TYPE_ID_SCRIPT_BLOCK:
                        {
                            requiresScriptBlock = true;
                            break;
                        }
                        case HeaderResource.HEADER_TYPE_ID_SCRIPT_BLOCK_START:
                        {
                            preCloseBlock = true;
                            requiresScriptBlock = true;
                            break;
                        }
                        case HeaderResource.HEADER_TYPE_ID_SCRIPT_TAG:
                        {
                            preCloseBlock = true;
                            break;
                        }
                        case HeaderResource.HEADER_TYPE_ID_SCRIPT_BLOCK_END:
                        {
                            postCloseBlock = true;
                            requiresScriptBlock = true;
                            break;
                        }
                        case HeaderResource.HEADER_TYPE_ID_STYLE_BLOCK:
                        {
                            requiresStyleBlock = true;
                            break;
                        }
                        case HeaderResource.HEADER_TYPE_ID_LINK_TAG:
                        {
                            preCloseBlock = true;
                            break;
                        }
                        case HeaderResource.HEADER_TYPE_ID_BASE_TAG:
                        {
                            preCloseBlock = true;
                            break;
                        }
                        default:
                        {
                            log.error( "HeaderResource.getNamedResourceContent() cannot include header section with unknown type; header-section-name=" + headerName + " header-section-type-id=" + headerTypeId );
                            includeHeader = false;
                            break;
                        }
                    }
                    if ( includeHeader )
                    {
                        if ( requiresScriptBlock && inStyleBlock )
                        {
                            preCloseBlock = true;
                        }
                        else if ( requiresStyleBlock && inScriptBlock )
                        {
                            preCloseBlock = true;
                        }
                        if ( preCloseBlock )
                        {
                            if ( inScriptBlock )
                            {
                                header.append( "</script>" ).append( EOL );
                                inScriptBlock = false;
                            }
                            else if ( inStyleBlock )
                            {
                                header.append( "</style>" ).append( EOL );
                                inStyleBlock = false;
                            }
                        }
                        
                        String headerText = (String)namedResources.get( headerName );
                        if ( headerText == null )
                        {
                            headerText = generateHeaderSection( headerName );
                            if ( headerText == null && headerRsrcRegistry != null )
                            {                                
                                headerText = (String)headerRsrcRegistry.get( headerName );
                                log.debug( "header resource registry text for header section=" + headerName + " headerText=" + headerText );
                            }
                        }
                        if ( headerText != null && headerText.length() > 0 )
                        {
                            if ( requiresScriptBlock && ! inScriptBlock )
                            {
                                header.append( "<script language=\"JavaScript\" type=\"text/javascript\" ")
                                .append( HeaderPhaseSupportConstants.HEAD_ELEMENT_CONTRIBUTION_MERGE_HINT_ATTRIBUTE ).append( "=\"" )
                                .append( headerName ).append( "\">" ).append( EOL );
                                inScriptBlock = true;
                            }
                            else if ( requiresStyleBlock && ! inStyleBlock )
                            {
                                header.append( "<style>" ).append( EOL );
                                inStyleBlock = true;
                            }
                            header.append( headerText ).append( EOL );
                        }
                        if ( postCloseBlock )
                        {
                            if ( inScriptBlock )
                            {
                                header.append( "</script>" ).append( EOL );
                                inScriptBlock = false;
                            }
                            else if ( inStyleBlock )
                            {
                                header.append( "</style>" ).append( EOL );
                                inStyleBlock = false;
                            }
                        }
                    }
                }   // if ( includeHeader )
            }   // while ( headerOrderListIter.hasNext() )
            if ( inScriptBlock )
            {
                header.append( "</script>" ).append( EOL );
                inScriptBlock = false;
            }
            else if ( inStyleBlock )
            {
                header.append( "</style>" ).append( EOL );
                inStyleBlock = false;
            }
        }   // if ( headerOrderList != null && headerOrderList.size() > 0 )
    }
    
    /*
     * Intended as derived class hook into late, auto-generated header resources
     */
    protected String generateHeaderSection( String headerName )
    {
        if ( headerName != null )
        {
            if ( headerName.equals( HEADER_SECTION_BASE_TAG ) )
            {
                return jetspeedGenerateBasetag();
            }
            else if ( headerName.startsWith( HEADER_SECTION_NAME_PREFIX_DOJO ) )
            {
                if ( headerName.equals( HEADER_SECTION_DOJO_PREINIT ) )
                {
                    return dojoGeneratePreinit();
                }
                else if ( headerName.equals( HEADER_SECTION_DOJO_INIT ) )
                {
                    return dojoGenerateInit();
                }
                else if ( headerName.equals( HEADER_SECTION_DOJO_WRITEINCLUDES ) )
                {
                    return dojoGenerateWriteincludes();
                }
                else if ( headerName.equals( HEADER_SECTION_DOJO_STYLE_BODYEXPAND ) )
                {
                    return dojoGenerateBodyExpandStyle();
                }
                else if ( headerName.equals( HEADER_SECTION_DOJO_STYLE_BODYEXPAND_NOSCROLL ) )
                {
                    return dojoGenerateBodyExpandNoScrollStyle();
                }
            }
        }
        return null;
    }
    
    /**
     * Add text argument to the getHeaderSections() content entry with a key that matches addToHeaderName argument
     * 
     */
    public void addHeaderSectionFragment( String addToHeaderName, String text )
    {
        addHeaderSectionFragment( null, addToHeaderName, text, false );
    }
    
    /**
     * If no previous call using value of headerFragmentName argument has been added to any getHeaderSections() content entry,
     * add text argument to the getHeaderSections() content entry with a key that matches addToHeaderName argument
     * 
     */
    public void addHeaderSectionFragment( String headerFragmentName, String addToHeaderName, String text )
    {
        addHeaderSectionFragment( headerFragmentName, addToHeaderName, text, false );
    }
    
    protected void addHeaderSectionFragment( String headerFragmentName, String addToHeaderName, String text, boolean alreadyCheckedFragName )
    {
        if ( addToHeaderName != null && text != null )
        {
            boolean addText = true;
            if ( ! alreadyCheckedFragName && headerFragmentName != null && hasHeaderSectionFragment( headerFragmentName, true ) )
            {
                addText = false;
            }
            if ( addText )
            {
                Map headerRsrcRegistry = getHeaderResourceRegistry();
                if ( headerRsrcRegistry != null )
                {
                    String overrideText = (String)headerRsrcRegistry.get( headerFragmentName );
                    if ( overrideText != null )
                    {
                        text = overrideText;
                    }
                }
                Map namedResources = getHeaderSections();
                String nText = (String)namedResources.get( addToHeaderName );
                if ( nText == null )
                {
                    nText = text + EOL;
                    orderHeaderSection( addToHeaderName );
                }
                else
                {
                    nText = nText + text + EOL;
                }
                namedResources.put( addToHeaderName, nText );
            }
        }
    }
    
    /**
     * Indicate whether value of headerFragmentName argument has been used to add to any getHeaderSections() content entry
     * 
     * @return true if headerFragmentName argument has been used to add to any getHeaderSections() content entry
     */
    public boolean hasHeaderSectionFragment( String headerFragmentName )
    {
        return hasHeaderSectionFragment( headerFragmentName, false );
    }
    protected boolean hasHeaderSectionFragment( String headerFragmentName, boolean setToTrue )
    {
        if ( headerFragmentName != null )
        {
            Map namedResourcesAddedFragments = getHeaderSectionsAddedFragments();
            if ( namedResourcesAddedFragments.containsKey( headerFragmentName ) )
            {
                return true;
            }
            else if ( setToTrue )
            {
                namedResourcesAddedFragments.put( headerFragmentName, Boolean.TRUE );
            }
        }
        return false;
    }
    
    protected void orderHeaderSection( String headerName )
    {
        if ( headerName != null )
        {
            Map headerNames = getHeaderSectionNames( true );
            if ( ! headerNames.containsKey( headerName ) )
            {
                List headerOrderList = getHeaderSectionOrderList( true );
                
                headerOrderList.add( headerName );
                headerNames.put( headerName, Boolean.TRUE );
            }
        }
    }
    
    /**
     * Indicate whether value of headerName is an included header section
     * 
     * @return true if headerName argument is an included header section
     */
    public boolean isHeaderSectionIncluded( String headerName )
    {
        if ( headerName != null )
        {
            Map headerNames = getHeaderSectionNames( false );
            if ( headerNames != null && headerNames.get( headerName ) != null )
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get the type of the getHeaderSections() content entry with a key that matches headerName argument
     * 
     * @return type of header section
     */
    public String getHeaderSectionType( String headerName )
    {
        if ( headerName != null )
        {
            Map headerTypes = getHeaderSectionTypes( false );
            if ( headerTypes != null )
            {
                Object[] headerTypePair = (Object[])headerTypes.get( headerName );
                if ( headerTypePair != null )
                {
                    Integer headerTypeId = (Integer)headerTypePair[0];
                    return HeaderResourceLib.getHeaderType( headerTypeId );
                }
            }
        }
        return null;
    }
    
    /**
     * Set the type of the getHeaderSections() content entry with a key that matches headerName argument
     * to the value of the headerType argument
     */
    public void setHeaderSectionType( String headerName, String headerType  )
    {
        if ( headerName != null )
        {
            int headerTypeId = HeaderResourceLib.getHeaderTypeId( headerType );
            if ( headerTypeId < 0 )
            {
                log.error( "HeaderResourceImpl.setHeaderSectionType() ignoring specification of unknown header section type; header-section-name=" + headerName + " header-section-type=" + headerType );
            }
            else
            {
                Map headerTypes = getHeaderSectionTypes( true );
                Object[] headerTypePair = (Object[])headerTypes.get( headerName );
                if ( headerTypePair == null )
                {
                    if ( headerType != null )
                    {
                        headerTypePair = new Object[] { new Integer( headerTypeId ), null };
                        headerTypes.put( headerName, headerTypePair );
                    }
                }
                else
                {
                    headerTypePair[0] = new Integer( headerTypeId );
                }
            }
        }
    }
    
    /**
     * Get the requiredflag of the getHeaderSections() content entry with a key that matches headerName argument
     * 
     * @return requiredflag for header section
     */
    public String getHeaderSectionRequiredFlag( String headerName )
    {
        if ( headerName != null )
        {
            Map headerTypes = getHeaderSectionTypes( false );
            if ( headerTypes != null )
            {
                Object[] headerTypePair = (Object[])headerTypes.get( headerName );
                if ( headerTypePair != null )
                {
                    return (String)headerTypePair[1];
                }
            }
        }
        return null;
    }
    
    /**
     * Set the requiredflag of the getHeaderSections() content entry with a key that matches headerName argument
     * to the value of the headerReqFlag argument
     */
    public void setHeaderSectionRequiredFlag( String headerName, String headerReqFlag )
    {
        if ( headerName != null )
        {
            if ( headerReqFlag != null && headerReqFlag.length() == 0 )
                headerReqFlag = null;
            
            Map headerTypes = getHeaderSectionTypes( true );
            Object[] headerTypePair = (Object[])headerTypes.get( headerName );
            if ( headerTypePair == null )
            {
                if ( headerReqFlag != null )
                {
                    headerTypePair = new Object[] { null, headerReqFlag };
                    headerTypes.put( headerName, headerTypePair );
                }
            }
            else
            {
                headerTypePair[1] = headerReqFlag;
            }
        }
    }
    
    protected Map getHeaderSectionTypes( boolean create )
    {
        Map dynamicConfig = getHeaderDynamicConfiguration();
        Map headerTypes = (Map)dynamicConfig.get( HEADER_CONFIG_TYPES );
        if ( headerTypes == null && create )
        {
            headerTypes = new HashMap();
            dynamicConfig.put( HEADER_CONFIG_TYPES, headerTypes );
        }
        return headerTypes;
    }
    protected Map getHeaderSectionNames( boolean create )
    {
        Map dynamicConfig = getHeaderDynamicConfiguration();
        Map headerNames = (Map)dynamicConfig.get( HEADER_INTERNAL_INCLUDED_NAMES );
        if ( headerNames == null && create )
        {
            headerNames = new HashMap();
            dynamicConfig.put( HEADER_INTERNAL_INCLUDED_NAMES, headerNames );
        }
        return headerNames;
    }
    protected List getHeaderSectionOrderList( boolean create )
    {
        Map dynamicConfig = getHeaderDynamicConfiguration();
        List headerOrderList = (List)dynamicConfig.get( HEADER_CONFIG_ORDER );
        if ( headerOrderList == null )
        {
            headerOrderList = new ArrayList();
            dynamicConfig.put( HEADER_CONFIG_ORDER, headerOrderList );
        }
        return headerOrderList;
    }
    
    /**
     * Access modifiable header configuration settings
     * 
     * @return Map containing modifiable header configuration settings 
     */
    public Map<String, Object> getHeaderDynamicConfiguration()
    {
        if ( this.headerDynamicConfiguration == null )
        {
            this.headerDynamicConfiguration = (Map)requestContext.getAttribute( PortalReservedParameters.HEADER_CONFIGURATION_ATTRIBUTE );
            if ( this.headerDynamicConfiguration == null )
            {
                this.headerDynamicConfiguration = new HashMap();
                requestContext.setAttribute( PortalReservedParameters.HEADER_CONFIGURATION_ATTRIBUTE, this.headerDynamicConfiguration );
            }
        }
        return this.headerDynamicConfiguration;
    }
    protected Map getHeaderSections()
    {
        if ( this.headerNamedResources == null )
        {
            this.headerNamedResources = (Map)requestContext.getAttribute( PortalReservedParameters.HEADER_NAMED_RESOURCE_ATTRIBUTE );
            if ( this.headerNamedResources == null )
            {
                this.headerNamedResources = new HashMap();
                requestContext.setAttribute( PortalReservedParameters.HEADER_NAMED_RESOURCE_ATTRIBUTE, this.headerNamedResources );
            }
        }
        return this.headerNamedResources;
    }
    protected Map getHeaderSectionsAddedFragments()
    {
        if ( this.headerNamedResourcesAddedFragments == null )
        {
            this.headerNamedResourcesAddedFragments = (Map)requestContext.getAttribute( PortalReservedParameters.HEADER_NAMED_RESOURCE_ADDED_FRAGMENTS_ATTRIBUTE );
            if ( this.headerNamedResourcesAddedFragments == null )
            {
                this.headerNamedResourcesAddedFragments = new HashMap();
                requestContext.setAttribute( PortalReservedParameters.HEADER_NAMED_RESOURCE_ADDED_FRAGMENTS_ATTRIBUTE, this.headerNamedResourcesAddedFragments );
            }
        }
        return this.headerNamedResourcesAddedFragments;
    }
    protected Map getHeaderResourceRegistry()
    {
        if ( this.headerResourceRegistry == null )
        {
            this.headerResourceRegistry = (Map)requestContext.getAttribute( PortalReservedParameters.HEADER_NAMED_RESOURCE_REGISTRY_ATTRIBUTE );
            if ( this.headerResourceRegistry == null )
            {
                this.headerResourceRegistry = new HashMap();
            }
        }
        return this.headerResourceRegistry;
    }
    
    protected RequestContext getRequestContext()
    {
        return this.requestContext;
    }    
    protected BasePortalURL getBaseUrlAccess()
    {
        return this.baseUrlAccess;
    }
    
    /**
     * Is request for /desktop rather than /portal
     * 
     * @return true if request is for /desktop, false if request is for /portal
     */
    public boolean isDesktop()
    {
        return this.isDesktop;
    }
    
    /**
     * Access complete header configuration settings
     * 
     * @return unmodifiable Map containing complete header configuration settings
     */
    public Map<String, Object> getHeaderConfiguration()
    {
        return this.headerConfiguration;
    }
    
    protected HashMap getNamedResourcesAlreadyOutput()
    {
        return this.namedResourcesAlreadyOutput;
    }
    protected void setNamedResourcesAlreadyOutput( HashMap newOne )
    {
        this.namedResourcesAlreadyOutput = newOne;
    }

    
    // get portal urls - a copy of each of these methods exists in JetspeedDesktopContextImpl.java
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl()
    {
        if ( this.portalBaseUrl == null )
        {
            this.portalBaseUrl = HeaderResourceLib.getPortalBaseUrl( this.requestContext, this.baseUrlAccess );
        }
        return this.portalBaseUrl;
    }
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl( boolean encode )
    {
        String baseurl = getPortalBaseUrl();
        if ( ! encode )
        {
            return baseurl;
        }
        else
        {
            return requestContext.getResponse().encodeURL( baseurl );
        }
    }
        
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( String relativePath )
    {
        return getPortalResourceUrl( relativePath, false );
    }
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( String relativePath, boolean encode )
    {
        return HeaderResourceLib.getPortalResourceUrl( relativePath, getPortalBaseUrl(), encode, this.requestContext );
    }
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl()
    {
        if ( this.portalUrl == null )
        {
            this.portalUrl = HeaderResourceLib.getPortalUrl( getPortalBaseUrl(), this.requestContext );
        }
        return this.portalUrl;
    }
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl( boolean encode )
    {
        return getPortalUrl( null, encode );
    }
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( String relativePath )
    {
        return getPortalUrl( relativePath, false );
    }
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( String relativePath, boolean encode )
    {
        return HeaderResourceLib.getPortalResourceUrl( relativePath, getPortalUrl(), encode, this.requestContext );
    }
    

    //  jetspeed - special convenience methods
    
    protected String jetspeedGenerateBasetag()
    {
        StringBuffer basetagOut = new StringBuffer();
        // <script type="text/javascript" src='http://localhost:8080/jetspeed/javascript/dojo/dojo.js'></script>
        // src='$jetspeedDesktop.getPortalResourceUrl("/javascript/dojo/dojo.js")'
        String fullPortalBaseUrl = HeaderResourceLib.getPortalBaseUrl( this.requestContext, this.baseUrlAccess, true );
        String href = HeaderResourceLib.getPortalResourceUrl( "/", fullPortalBaseUrl, false, this.requestContext );
        basetagOut.append( "<base href=\"" ).append( href ).append( "\">" );
        return basetagOut.toString();
    }
    
    // dojo - special convenience methods
    
    /**
     * If no previous call using value of dojoRequire argument has been added to any getHeaderSections() content entry,
     * add text argument to the getHeaderSections() content entry for dojo core require statements
     * 
     */
    public void dojoAddCoreLibraryRequire( String dojoRequire )
    {
        dojoAddRequire( dojoRequire, HEADER_SECTION_DOJO_REQUIRES_CORE );
    }
    
    /**
     * Split dojoRequires argument using ';' delimiter and for each resulting dojoRequire value, if no previous call
     * using dojoRequire value has been added to any getHeaderSections() content entry,
     * add text argument to the getHeaderSections() content entry for dojo core require statements
     * 
     */
    public void dojoAddCoreLibraryRequires( String dojoRequires )
    {
        dojoAddRequires( dojoRequires, HEADER_SECTION_DOJO_REQUIRES_CORE );
    }
    
    /**
     * If no previous call using value of dojoRequire argument has been added to any getHeaderSections() content entry,
     * add text argument to the getHeaderSections() content entry for dojo library module require statements
     * 
     */
    public void dojoAddModuleLibraryRequire( String dojoRequire )
    {
        dojoAddRequire( dojoRequire, HEADER_SECTION_DOJO_REQUIRES_MODULES );
    }
    
    /**
     * Split dojoRequires argument using ';' delimiter and for each resulting dojoRequire value, if no previous call
     * using dojoRequire value has been added to any getHeaderSections() content entry,
     * add text argument to the getHeaderSections() content entry for dojo library module require statements
     * 
     */
    public void dojoAddModuleLibraryRequires( String dojoRequires )
    {
        dojoAddRequires( dojoRequires, HEADER_SECTION_DOJO_REQUIRES_MODULES );
    }
    
    /**
     * Assure that header section name for dojo body expand style is included
     * 
     */
    public void dojoAddBodyExpandStyle( boolean omitWindowScrollbars )
    {
        if ( isHeaderSectionIncluded( HEADER_SECTION_DOJO_STYLE_BODYEXPAND ) || isHeaderSectionIncluded( HEADER_SECTION_DOJO_STYLE_BODYEXPAND_NOSCROLL ) )
        {
            // already included - first inclusion wins
        }
        else
        {
            if ( ! omitWindowScrollbars )
            {
                orderHeaderSection( HEADER_SECTION_DOJO_STYLE_BODYEXPAND );
            }
            else
            {
                orderHeaderSection( HEADER_SECTION_DOJO_STYLE_BODYEXPAND_NOSCROLL );
            }
        }
    }
    
    /**
     * Enable dojo by setting appropriate modifiable header configuration setting
     * 
     */
    public void dojoEnable()
    {
        getHeaderDynamicConfiguration().put( HEADER_CONFIG_DOJO_ENABLE, "true" );
    }
    
    protected void dojoDisable()
    {
        getHeaderDynamicConfiguration().put( HEADER_CONFIG_DOJO_ENABLE, "false" );
    }
    protected String dojoGetPath()
    {
        return (String)getHeaderDynamicConfiguration().get( HEADER_CONFIG_DOJO_PATH );
    }
    protected void dojoAddRequire( String dojoRequire, String addToHeaderName )
    {
        if ( dojoRequire != null && addToHeaderName != null && ! hasHeaderSectionFragment( dojoRequire, true ) )
        {
            String requireStatement = "    dojo.require(\"" + dojoRequire + "\");";
            addHeaderSectionFragment( dojoRequire, addToHeaderName, requireStatement, true );
        }
    }
    protected void dojoAddRequires( String dojoRequires, String addToHeaderName )
    {
        String[] reqStatements = StringUtils.split( dojoRequires, ';' );
        int reqStatementsLen = ( reqStatements == null ) ? 0 : reqStatements.length;
        if ( reqStatementsLen > 0 )
        {   
            for ( int i = 0 ; i < reqStatementsLen ; i++ )
            {
                dojoAddRequire( reqStatements[i], addToHeaderName );
            }
        }
    }
    protected String dojoGeneratePreinit()
    {
        StringBuffer preinitOut = new StringBuffer();
        //preinitOut.append( "    " ).append( "function de_jsessionid_url(url){var tEnds = url.indexOf(';jsessionid=');if (tEnds > 0) url = url.substring(0, tEnds);return url;}" ).append( EOL );
        // presence of ;jsessionid in dojo baseScriptUri is bad news
        preinitOut.append( "    " ).append( "djConfig.baseScriptUri = \"" ).append( getPortalResourceUrl( dojoGetPath(), false ) ).append( "\";" ).append( EOL );
        if (this.requestContext.getRequest().getContextPath().length()==0)
        {
            preinitOut.append( "    " ).append( "djConfig.jetspeed.rootContext = \"true\";" ).append( EOL );
        }
        preinitOut.append( "    " ).append( "djConfig.jetspeed.servletPath = \"" ).append( this.requestContext.getRequest().getServletPath() ).append( "\";" );
        return preinitOut.toString();
    }
    protected String dojoGenerateInit()
    {
        StringBuffer initOut = new StringBuffer();
        // <script type="text/javascript" src='http://localhost:8080/jetspeed/javascript/dojo/dojo.js'></script>
        // src='$jetspeedDesktop.getPortalResourceUrl("/javascript/dojo/dojo.js")'
        initOut.append( "<script type=\"text/javascript\" src=\"" ).append( getPortalResourceUrl( dojoGetPath(), false ) ).append( "dojo.js" )
        .append( "\" id=\"").append(HeaderPhaseSupportConstants.HEAD_ELEMENT_CONTRIBUTION_ELEMENT_ID_DOJO_LIBRARY_INCLUDE).append("\"></script>" );
        return initOut.toString();
    }
    protected String dojoGenerateWriteincludes()
    {
        return "    dojo.hostenv.writeIncludes();";
    }
    protected String dojoGenerateBodyExpandStyle()
    {   // if not defined as getHeaderResourceRegistry(), generate default text
        Map headerRsrcRegistry = getHeaderResourceRegistry();
        String headerText = (String)headerRsrcRegistry.get( HEADER_SECTION_DOJO_STYLE_BODYEXPAND );
        if ( headerText == null )
        {
            headerText = "html, body { width: 100%; height: 100%; padding: 0 0 0 0; margin: 0 0 0 0; }";
        }
        return headerText;
    }
    protected String dojoGenerateBodyExpandNoScrollStyle()
    {   // if not defined as getHeaderResourceRegistry(), generate default text
        Map headerRsrcRegistry = getHeaderResourceRegistry();
        String headerText = (String)headerRsrcRegistry.get( HEADER_SECTION_DOJO_STYLE_BODYEXPAND_NOSCROLL );
        if ( headerText == null )
        {
            headerText = "html, body { width: 100%; height: 100%; overflow: hidden; padding: 0 0 0 0; margin: 0 0 0 0; }";
        }
        return headerText;
    }

    
    // older content implementation - using HeaderInfo set 
    
    /**
     * Gets HeaderInfo set from the request.
     * 
     * @return HeaderInfo set containing content for inclusion in html &lt;head&gt;
     */
    private Set getHeaderInfoSet()
    {
        Set headerInfoSet = (Set) requestContext.getAttribute(PortalReservedParameters.HEADER_RESOURCE_ATTRIBUTE);
        if (headerInfoSet == null)
        {
            headerInfoSet = new LinkedHashSet();
            requestContext.setAttribute(PortalReservedParameters.HEADER_RESOURCE_ATTRIBUTE, headerInfoSet);
        }
        return headerInfoSet;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.headerresource.impl.HeaderResource#addHeaderInfo(java.lang.String)
     */
    public void addHeaderInfo(String text)
    {
        HeaderInfo headerInfo = new HeaderInfo(null, null, text);
        if (!containsHeaderInfo(headerInfo))
        {
            Set headerInfoSet = getHeaderInfoSet();
            headerInfoSet.add(headerInfo);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.headerresource.impl.HeaderResource#addHeaderInfo(java.lang.String,
     *      java.util.Map,java.lang.String)
     */
    public void addHeaderInfo(String elementName, Map<String, String> attributes, String text)
    {
        HeaderInfo headerInfo = new HeaderInfo(elementName, attributes, text);
        if (!containsHeaderInfo(headerInfo))
        {
            Set headerInfoSet = getHeaderInfoSet();
            headerInfoSet.add(headerInfo);
        }
    }

    /**
     * Returns true if this set contains the specified HeaderInfo.
     * 
     * @param headerInfo
     * @return
     */
    private boolean containsHeaderInfo(HeaderInfo headerInfo)
    {
        Set headerInfoSet = getHeaderInfoSet();
        for (Iterator ite = headerInfoSet.iterator(); ite.hasNext();)
        {
            HeaderInfo hInfo = (HeaderInfo) ite.next();
            if (headerInfo.equals(hInfo))
            {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.headerresource.impl.HeaderResource#addJavaScript(java.lang.String,
     *      boolean)
     */
    public void addJavaScript(String path, boolean defer)
    {
        HashMap attrs = new HashMap();
        attrs.put("src", requestContext.getResponse().encodeURL( path ) );
        attrs.put("type", "text/javascript");
        if (defer)
        {
            attrs.put("defer", "true");
        }
        addHeaderInfo("script", attrs, "");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.headerresource.impl.HeaderResource#addJavaScript(java.lang.String)
     */
    public void addJavaScript(String path)
    {
        addJavaScript(path, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.headerresource.impl.HeaderResource#addStyleSheet(java.lang.String)
     */
    public void addStyleSheet(String path)
    {
        HashMap attrs = new HashMap();
        attrs.put("rel", "stylesheet");
        attrs.put("href", requestContext.getResponse().encodeURL( path ) );
        attrs.put("type", "text/css");
        addHeaderInfo("link", attrs, null);
    }
    
    /**
     * This class represents tag information for HeaderResouce component
     * 
     * @author <a href="mailto:shinsuke@yahoo.co.jp">Shinsuke Sugaya</a>
     */
    private class HeaderInfo
    {
        /**
         * Tag's name
         */
        private String elementName;

        /**
         * Tag's attributes
         */
        private Map attributes;

        /**
         * Tag's content
         */
        private String text;

        public HeaderInfo(String elementName)
        {
            this(elementName, new HashMap());
        }

        public HeaderInfo(String elementName, Map attr)
        {
            this(elementName, attr, null);
        }

        public HeaderInfo(String elementName, Map attr, String text)
        {
            setElementName(elementName);
            setAttributes(attr);
            setText(text);
        }

        public void addAttribute(String key, String value)
        {
            attributes.put(key, value);
        }

        public String toString()
        {
            StringBuffer buf = new StringBuffer();
            
            String elmtName = getElementName();
            if ( elmtName != null && elmtName.length() > 0 )
            {
                buf.append("<");
                buf.append(getElementName());
                buf.append(" ");

                Map attrMap = getAttributes();
                if ( attrMap != null )
                {
                    Set keySet = attrMap.keySet();
                    for (Iterator ite = keySet.iterator(); ite.hasNext();)
                    {
                        String key = (String) ite.next();
                        buf.append(key);
                        buf.append("=\"");
                        buf.append((String) attrMap.get(key));
                        buf.append("\" ");
                    }
                }
                if (getText() != null)
                {
                    buf.append(">" + getText() + "</" + getElementName() + ">");
                }
                else
                {
                    buf.append("/>");
                }
            }
            else
            {
                if (getText() != null)
                {
                    buf.append( getText() );
                }
            }
            return buf.toString();
        }

        public boolean equals(Object o)
        {
            if (o instanceof HeaderInfo)
            {
                HeaderInfo headerInfo = (HeaderInfo) o;
                if (compareString(headerInfo.getElementName(), getElementName())
                        && compareString(headerInfo.getText(), getText())
                        && compareAttributes(headerInfo.getAttributes(), getAttributes()))
                {
                    return true;
                }
            }
            return false;
        }

        private boolean compareString(String str0, String str1)
        {
            if (str0 == null)
            {
                if (str1 == null)
                {
                    return true;
                }

            }
            else if ( str1 != null )
            {
                if (str0.equals(str1))
                {
                    return true;
                }
            }
            return false;
        }
        
        private boolean compareAttributes(Map attr0, Map attr1)
        {
            if (attr0 == null)
            {
                if (attr1 == null)
                {
                    return true;
                }
            }
            else if ( attr1 != null )
            {
                if (attr0.equals(attr1))
                {
                    return true;
                }
            }
            return false;
        }

        /**
         * @return Returns the attributes.
         */
        public Map getAttributes()
        {
            return attributes;
        }

        /**
         * @param attributes The attributes to set.
         */
        public void setAttributes(Map attributes)
        {
            this.attributes = attributes;
        }

        /**
         * @return Returns the elementName.
         */
        public String getElementName()
        {
            return elementName;
        }

        /**
         * @param elementName The elementName to set.
         */
        public void setElementName(String elementName)
        {
            this.elementName = elementName;
        }

        /**
         * @return Returns the text.
         */
        public String getText()
        {
            return text;
        }

        /**
         * @param text The text to set.
         */
        public void setText(String text)
        {
            this.text = text;
        }
    }
}
