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
package org.apache.jetspeed.aggregator.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.decoration.DecorationFactory;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.headerresource.HeaderResourceLib;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;

/**
 * HeaderAggregator builds the content required to render a page of portlets.
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: HeaderAggregatorImpl.java 359125 2005-12-26 23:16:39Z rwatler $
 */
public class HeaderAggregatorImpl implements PageAggregator
{
    protected final static Logger log = LoggerFactory.getLogger( HeaderAggregatorImpl.class );
    protected final static String EOL = "\r\n";   // html eol

    private PortletFactory factory;
    private HeaderResourceFactory headerResourceFactory;
    private DecorationFactory decorationFactory;
    
    private boolean isDesktop;
    
    private Map headerConfiguration;
    private Map headerResourceRegistry;
    private Map headerDynamicConfigurationDefault;
    private Map headerNamedResourcesDefault;
    private Map headerNamedResourcesAddedFragmentsDefault;
    
    /** base portal URL to override default URL server info from servlet */
    private BasePortalURL baseUrlAccess = null;
    
    
    public HeaderAggregatorImpl( PortletFactory factory,
                                 HeaderResourceFactory headerResourceFactory,
                                 boolean isDesktop,
                                 Map headerConfiguration,
                                 Map headerResourceRegistry,
                                 DecorationFactory decorationFactory )
    {
        this( factory, headerResourceFactory, isDesktop, headerConfiguration, headerResourceRegistry, decorationFactory, null );
    }
    
    public HeaderAggregatorImpl( PortletFactory factory,
                                 HeaderResourceFactory headerResourceFactory,
                                 boolean isDesktop,
                                 Map headerConfiguration,
                                 Map headerResourceRegistry,
                                 DecorationFactory decorationFactory,
                                 BasePortalURL baseUrlAccess )
    {
        this.factory = factory;
        this.headerResourceFactory = headerResourceFactory;
        this.isDesktop = isDesktop;
        this.baseUrlAccess = baseUrlAccess;
        this.decorationFactory = decorationFactory;
        initializeHeaderConfiguration( headerConfiguration, headerResourceRegistry );   
    }
    
    /**
     * Initialize header configuration, making immutable copies of the data structures and 
     * compiling as much finished static header content as possible (to minimize repetitive work per request)
     */
    private void initializeHeaderConfiguration( Map headerConfigArg, Map headerRsrcRegistryArg )
    {
        this.headerConfiguration = null;
        this.headerResourceRegistry = null;
        this.headerDynamicConfigurationDefault = null;
        this.headerNamedResourcesDefault = null;
        this.headerNamedResourcesAddedFragmentsDefault = null;
        
        if ( headerConfigArg != null && headerConfigArg.size() > 0 )
        {
            // attempt to make safe immutable copy of headerConfigArg
            HashMap headerConfig = new HashMap();
            Iterator headerConfigEntryIter = headerConfigArg.entrySet().iterator();
            while ( headerConfigEntryIter.hasNext() )
            {
                Map.Entry headerConfigEntry = (Map.Entry)headerConfigEntryIter.next();
                Object headerConfigKey = headerConfigEntry.getKey();
                Object headerConfigVal = headerConfigEntry.getValue();
                if ( headerConfigVal instanceof Map )
                {
                    headerConfig.put( headerConfigKey, Collections.unmodifiableMap( new HashMap( (Map)headerConfigVal ) ) );
                }
                else if ( headerConfigVal instanceof List )
                {
                    headerConfig.put( headerConfigKey, Collections.unmodifiableList( new ArrayList( (List)headerConfigVal ) ) );
                }
                else
                {
                    headerConfig.put( headerConfigKey, headerConfigVal );
                }
            }
            this.headerConfiguration = Collections.unmodifiableMap( headerConfig );
            
            // make modifiable copy of headerRsrcRegistryArg - is made immutable at end of initializeHeaderConfiguration()
            Map headerRsrcRegistry = null;
            if ( headerRsrcRegistryArg != null && headerRsrcRegistryArg.size() > 0 )
            {
                headerRsrcRegistry = new HashMap( headerRsrcRegistryArg );
                // leave modifiable during initializeHeaderConfigurationDefaults() protocol
                //    (so that entries can be removed - possibly leading to an empty map which will save a bunch of gratuitous lookups)
            }
            else
            {
                headerRsrcRegistry = new HashMap();
            }
            this.headerResourceRegistry = headerRsrcRegistry;   

            HashMap namedResourcesDefault = new HashMap();
            HashMap namedResourcesAddedFragmentsDefault = new HashMap();
            
            Map dynamicConfigurationDefault = initializeHeaderConfigurationDefaults( namedResourcesDefault, namedResourcesAddedFragmentsDefault );
            if ( dynamicConfigurationDefault != null )
                this.headerDynamicConfigurationDefault = Collections.unmodifiableMap( dynamicConfigurationDefault );
            
            this.headerNamedResourcesDefault = Collections.unmodifiableMap( namedResourcesDefault );
            this.headerNamedResourcesAddedFragmentsDefault = Collections.unmodifiableMap( namedResourcesAddedFragmentsDefault );
            
            this.headerResourceRegistry = null;
            if ( headerRsrcRegistry != null && headerRsrcRegistry.size() > 0 )
            {
                this.headerResourceRegistry = Collections.unmodifiableMap( headerRsrcRegistry );
            }
        }
    }

    /**
     * Initialize dynamic-header-configuration and call initializeHeaderConfigurationEntryDefaults() for
     * each key in headerConfiguration Map, allowing for each to add resources and settings to:
     * headerNamedResourcesDefault, headerNamedResourcesAddedFragmentsDefault and headerDynamicConfigurationDefault
     * 
     * If no specific handler is defined for a headerConfiguration key, the entry is copied to headerDynamicConfigurationDefault
     * otherwise the handler is responsible for adding information to headerDynamicConfigurationDefault
     * 
     * headerConfiguration handlers are currently defined for the headerConfiguration keys:
     *    "header.order"         - HeaderResource.HEADER_CONFIG_ORDER
     *    "header.types"         - HeaderResource.HEADER_CONFIG_TYPES
     *    "header.requiredflag"  - HeaderResource.HEADER_CONFIG_REQUIREDFLAG
     *    "dojo"                 - HeaderResource.HEADER_CONFIG_DOJO
     *    "desktop"              - HeaderResource.HEADER_CONFIG_DESKTOP
     */
    protected Map initializeHeaderConfigurationDefaults( HashMap namedResourcesDefault, HashMap namedResourcesAddedFragmentsDefault )
    {
        if ( this.headerConfiguration == null )
        {
            return null;
        }
        
        HashMap headerDynamicConfigurationDefault = new HashMap();
        
        initializeHeaderOrderConfigurationDefaults( namedResourcesDefault, namedResourcesAddedFragmentsDefault, headerDynamicConfigurationDefault );

        // setting header.basetag type - without adding it to order
        setNamedHeaderResourceProperties( HeaderResource.HEADER_SECTION_BASE_TAG, HeaderResource.HEADER_TYPE_BASE_TAG, null, headerDynamicConfigurationDefault );
        
        Iterator hConfigEntryIter = this.headerConfiguration.entrySet().iterator();
        while ( hConfigEntryIter.hasNext() )
        {
            Map.Entry hConfigEntry = (Map.Entry)hConfigEntryIter.next();
            Object hConfigKey = hConfigEntry.getKey();
            Object hConfigVal = hConfigEntry.getValue();
            
            if ( ! initializeHeaderConfigurationEntryDefaults( hConfigKey, hConfigVal, namedResourcesDefault, namedResourcesAddedFragmentsDefault, headerDynamicConfigurationDefault ) )
            {
                if ( hConfigVal instanceof Map )
                {
                    headerDynamicConfigurationDefault.put( hConfigKey, Collections.unmodifiableMap( new HashMap( (Map)hConfigVal ) ) );
                }
                else if ( hConfigVal instanceof List )
                {
                    headerDynamicConfigurationDefault.put( hConfigKey, Collections.unmodifiableList( new ArrayList( (List)hConfigVal ) ) );
                }
                else
                {
                    headerDynamicConfigurationDefault.put( hConfigKey, hConfigVal );
                }
            }
        }
        initializeMissingHeaderConfigurationEntryDefaults( namedResourcesDefault, namedResourcesAddedFragmentsDefault, headerDynamicConfigurationDefault );
        
        postinitializeHeaderOrderConfigurationDefaults( headerDynamicConfigurationDefault );
        
        return headerDynamicConfigurationDefault;
    }
    
    protected void initializeHeaderOrderConfigurationDefaults( HashMap namedResourcesDefault, HashMap namedResourcesAddedFragmentsDefault, HashMap headerDynamicConfigurationDefault )
    {
        if ( this.headerConfiguration != null )
        {
            List headerOrderConfigList = (List)this.headerConfiguration.get( HeaderResource.HEADER_CONFIG_ORDER );
            if ( headerOrderConfigList != null && headerOrderConfigList.size() > 0 )
            {
                ArrayList headerOrderList = new ArrayList();
                Map headerNames = new HashMap();
                Iterator headerOrderListIter = headerOrderConfigList.iterator();
                while ( headerOrderListIter.hasNext() )
                {
                    Object headerNameObj = headerOrderListIter.next();
                    if ( headerNameObj != null )
                    {
                        String headerName = headerNameObj.toString();
                        if ( headerName != null && headerName.length() > 0 )
                        {
                            headerOrderList.add( headerName );
                            headerNames.put( headerName, Boolean.TRUE );
                        }
                    }
                }
                // add modifiable structures at this point - so that later initialization steps can manipulate the defaults
                // needs to be made unmodifiable at end of processing by calling postinitializeHeaderOrderConfigurationDefaults()
                headerDynamicConfigurationDefault.put( HeaderResource.HEADER_CONFIG_ORDER, headerOrderList );
                headerDynamicConfigurationDefault.put( HeaderResource.HEADER_INTERNAL_INCLUDED_NAMES, headerNames );
            }
            
            Map headerTypes = null;
            Map headerTypesConfig = (Map)this.headerConfiguration.get( HeaderResource.HEADER_CONFIG_TYPES );
            if ( headerTypesConfig != null && headerTypesConfig.size() > 0 )
            {
                headerTypes = new HashMap();
                Iterator headerTypesConfigIter = headerTypesConfig.entrySet().iterator();
                while ( headerTypesConfigIter.hasNext() )
                {
                    Map.Entry headerTypeEntry = (Map.Entry)headerTypesConfigIter.next();
                    Object headerNameObj = headerTypeEntry.getKey();
                    Object headerTypeObj = headerTypeEntry.getValue();
                    if ( headerNameObj != null && headerTypeObj != null )
                    {
                        String headerName = headerNameObj.toString();
                        int headerTypeId = HeaderResourceLib.getHeaderTypeId( headerTypeObj.toString() );
                        if ( headerName != null )
                        {
                            if ( headerTypeId >= 0 )
                            {
                                headerTypes.put( headerName, new Object[] { new Integer( headerTypeId ), null } );
                            }
                            else
                            {
                                log.error( "HeaderAggregatorImpl.initializeHeaderOrderConfigurationDefaults() ignoring specification of unknown header section type; header-section-name=" + headerName + " header-section-type=" + headerTypeObj.toString() );
                            }
                        }
                    }
                }
            }
            
            Map headerRequiredFlagConfig = (Map)this.headerConfiguration.get( HeaderResource.HEADER_CONFIG_REQUIREDFLAG );
            if ( headerRequiredFlagConfig != null && headerRequiredFlagConfig.size() > 0 )
            {
                if ( headerTypes == null )
                {
                    headerTypes = new HashMap();
                }
                Iterator headerRequiredFlagConfigIter = headerRequiredFlagConfig.entrySet().iterator();
                while ( headerRequiredFlagConfigIter.hasNext() )
                {
                    Map.Entry headerRequiredFlagEntry = (Map.Entry)headerRequiredFlagConfigIter.next();
                    Object headerNameObj = headerRequiredFlagEntry.getKey();
                    Object headerReqFlagObj = headerRequiredFlagEntry.getValue();
                    if ( headerNameObj != null && headerReqFlagObj != null )
                    {
                        String headerName = headerNameObj.toString();
                        String headerReqFlag = headerReqFlagObj.toString();
                        if ( headerName != null && headerName.length() > 0 && headerReqFlag != null )
                        {
                            Object[] headerTypePair = (Object[])headerTypes.get( headerName );
                            if ( headerTypePair != null )
                            {
                                headerTypePair[1] = headerReqFlag;
                            }
                            else
                            {
                                headerTypePair = new Object[] { null, headerReqFlag };
                                headerTypes.put( headerName, headerTypePair );
                            }
                        }
                    }
                }
            }
            if ( headerTypes != null && headerTypes.size() > 0 )
            {
                headerDynamicConfigurationDefault.put( HeaderResource.HEADER_CONFIG_TYPES, headerTypes );
            }
        }
    }
    protected void postinitializeHeaderOrderConfigurationDefaults( HashMap headerDynamicConfigurationDefault )
    {
        if ( headerDynamicConfigurationDefault != null )
        {
            Map headerNames = (Map)headerDynamicConfigurationDefault.get( HeaderResource.HEADER_INTERNAL_INCLUDED_NAMES );
            if ( headerNames != null )
            {
                headerDynamicConfigurationDefault.put( HeaderResource.HEADER_INTERNAL_INCLUDED_NAMES, Collections.unmodifiableMap( headerNames ) );
            }
            Map headerTypes = (Map)headerDynamicConfigurationDefault.get( HeaderResource.HEADER_CONFIG_TYPES );
            if ( headerTypes != null )
            {
                headerDynamicConfigurationDefault.put( HeaderResource.HEADER_CONFIG_TYPES, Collections.unmodifiableMap( headerTypes ) );
            }
            List headerOrderList = (List)headerDynamicConfigurationDefault.get( HeaderResource.HEADER_CONFIG_ORDER );
            if ( headerOrderList != null )
            {
                headerDynamicConfigurationDefault.put( HeaderResource.HEADER_CONFIG_ORDER, Collections.unmodifiableList( headerOrderList ) );
            }
        }
    }
    
    /**
     * Intended as derived class hook into header configuration process
     * 
     * @return true if headerConfigKey has been processed or false if default processing should occur
     */
    protected boolean initializeHeaderConfigurationEntryDefaults( Object headerConfigKey, Object headerConfigValue, HashMap namedResourcesDefault, HashMap namedResourcesAddedFragmentsDefault, HashMap headerDynamicConfigurationDefault )
    {
        if ( headerConfigKey.equals( HeaderResource.HEADER_CONFIG_ORDER ) || headerConfigKey.equals( HeaderResource.HEADER_CONFIG_TYPES ) || headerConfigKey.equals( HeaderResource.HEADER_CONFIG_REQUIREDFLAG ) )
        {
            // do nothing - processed earlier with call to initializeHeaderOrderConfigurationDefaults()
            return true;
        }
        else if ( headerConfigKey.equals( HeaderResource.HEADER_CONFIG_DOJO ) )
        {
            initializeDojoHeaderConfigurationDefaults( (Map)headerConfigValue, namedResourcesDefault, namedResourcesAddedFragmentsDefault, headerDynamicConfigurationDefault );
            return true;
        }
        else if ( headerConfigKey.equals( HeaderResource.HEADER_CONFIG_DESKTOP ) )
        {
            initializeDesktopHeaderConfigurationDefaults( (Map)headerConfigValue, namedResourcesDefault, namedResourcesAddedFragmentsDefault, headerDynamicConfigurationDefault );
            return true;
        }
        return false;
    }
    
    protected void initializeMissingHeaderConfigurationEntryDefaults( HashMap namedResourcesDefault, HashMap namedResourcesAddedFragmentsDefault, HashMap headerDynamicConfigurationDefault )
    {
        if ( isDesktop() )
        {
            if ( this.headerConfiguration.get( HeaderResource.HEADER_CONFIG_DOJO ) == null )
            {
                initializeDojoHeaderConfigurationDefaults( null, namedResourcesDefault, namedResourcesAddedFragmentsDefault, headerDynamicConfigurationDefault );
            }
            if ( this.headerConfiguration.get( HeaderResource.HEADER_CONFIG_DESKTOP ) == null )
            {
                initializeDesktopHeaderConfigurationDefaults( null, namedResourcesDefault, namedResourcesAddedFragmentsDefault, headerDynamicConfigurationDefault );
            }
        }
    }
    
    protected void registerAndOrderNamedHeaderResource( String headerName, String headerType, String headerReqFlag, Map headerDynamicConfigurationDefault )
    {
        orderNamedHeaderResource( headerName, headerDynamicConfigurationDefault );
        setNamedHeaderResourceProperties( headerName, headerType, headerReqFlag, headerDynamicConfigurationDefault );
    }
    
    protected void orderNamedHeaderResource( String headerName, Map headerDynamicConfigurationDefault )
    {
        if ( headerName != null )
        {
            Map headerNames = (Map)headerDynamicConfigurationDefault.get( HeaderResource.HEADER_INTERNAL_INCLUDED_NAMES );
            if ( headerNames == null )
            {
                headerNames = new HashMap();
                headerDynamicConfigurationDefault.put( HeaderResource.HEADER_INTERNAL_INCLUDED_NAMES, headerNames );
            }
            
            Object headerNamesVal = headerNames.get( headerName );
            if ( headerNamesVal == null )
            {
                List headerOrderList = (List)headerDynamicConfigurationDefault.get( HeaderResource.HEADER_CONFIG_ORDER );
                if ( headerOrderList == null )
                {
                    headerOrderList = new ArrayList();
                    headerDynamicConfigurationDefault.put( HeaderResource.HEADER_CONFIG_ORDER, headerOrderList );
                }
                
                headerOrderList.add( headerName );
                headerNames.put( headerName, Boolean.TRUE );
            }
        }
    }
    
    protected void setNamedHeaderResourceProperties( String headerName, String headerType, String headerReqFlag, Map headerDynamicConfigurationDefault )
    {
        if ( headerName != null )
        {
            int headerTypeId = HeaderResourceLib.getHeaderTypeId( headerType );
            
            boolean headerRefFlagSpecified = ( headerReqFlag != null && headerReqFlag.length() > 0 );
            if ( headerTypeId < 0 && ! headerRefFlagSpecified )
            {
                log.error( "HeaderAggregatorImpl.registerAndOrderNamedHeaderResource() ignoring specification of unknown header section type; header-section-name=" + headerName + " header-section-type=" + headerType );
            }
            
            if ( ( headerTypeId >= 0 ) || headerRefFlagSpecified )
            {
                Map headerTypes = (Map)headerDynamicConfigurationDefault.get( HeaderResource.HEADER_CONFIG_TYPES );
                if ( headerTypes == null )
                {
                    headerTypes = new HashMap();
                    headerDynamicConfigurationDefault.put( HeaderResource.HEADER_CONFIG_TYPES, headerTypes );
                }
                
                Object[] headerTypePair = (Object[])headerTypes.get( headerName );
                if ( headerTypePair == null )
                {
                    headerTypePair = new Object[] { null, null };
                    headerTypes.put( headerName, headerTypePair );
                }
                if ( headerTypePair[0] == null && headerTypeId >= 0 )
                {   // change only if value from configuration is null
                    headerTypePair[0] = new Integer( headerTypeId );
                }
                if ( headerTypePair[1] == null && headerReqFlag != null && headerReqFlag.length() > 0 )
                {   // change only if value from configuration is null
                    headerTypePair[1] = headerReqFlag;
                }
            }
        }
    }
    
    protected boolean canAddHeaderNamedResourceFragment( String headerFragmentName, HashMap namedResourcesAddedFragmentsDefault, String[] registryContent )
    {
        if ( headerFragmentName != null && ! namedResourcesAddedFragmentsDefault.containsKey( headerFragmentName ) )
        {
            namedResourcesAddedFragmentsDefault.put( headerFragmentName, Boolean.TRUE );
            if ( registryContent != null )
            {
                String registryContentVal = (String)this.headerResourceRegistry.get( headerFragmentName );
                registryContent[0] = registryContentVal;
                if ( registryContentVal != null )
                {
                    this.headerResourceRegistry.remove( headerFragmentName );
                }
            }
            return true;
        }
        if ( registryContent != null )
        {
            registryContent[0] = null;
        }
        return false;
    }
        
    protected void initializeDesktopHeaderConfigurationDefaults( Map desktopConfigMap, HashMap namedResourcesDefault, HashMap namedResourcesAddedFragmentsDefault, HashMap headerDynamicConfigurationDefault )
    {
        if ( desktopConfigMap == null )
        {
            desktopConfigMap = new HashMap();
        }
        
        StringBuffer desktopDojoConfigContent = new StringBuffer();
        
        String layoutDecorationDefaultName = HeaderResource.HEADER_CONFIG_DESKTOP_LAYOUT_DECORATION_DEFAULT;
        String layoutDecoration = (String)desktopConfigMap.get( layoutDecorationDefaultName );
        if ( layoutDecoration != null && layoutDecoration.length() > 0 )
        {
            decorationFactory.setDefaultDesktopLayoutDecoration( layoutDecoration );
        }
        
        String portletDecorationDefaultName = HeaderResource.HEADER_CONFIG_DESKTOP_PORTLET_DECORATION_DEFAULT;
        String portletDecoration = (String)desktopConfigMap.get( portletDecorationDefaultName );
        if ( portletDecoration == null || portletDecoration.length() == 0 )
        {
            portletDecoration = decorationFactory.getDefaultDesktopPortletDecoration();
        }
        if ( portletDecoration != null && portletDecoration.length() > 0 )
        {
            if ( canAddHeaderNamedResourceFragment( portletDecorationDefaultName, namedResourcesAddedFragmentsDefault, null ) )
            {
                desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME ).append( ".windowDecoration = \"" ).append( portletDecoration ).append( "\";" ).append( EOL );
            }
            decorationFactory.setDefaultDesktopPortletDecoration( portletDecoration );
        }
        
        String desktopPageAjaxNavName = HeaderResource.HEADER_CONFIG_DESKTOP_PAGE_AJAXNAVIGATION;
        String desktopPageAjaxNav = HeaderResourceLib.makeJSONBoolean( desktopConfigMap.get( desktopPageAjaxNavName ) );
        if ( desktopPageAjaxNav != null && canAddHeaderNamedResourceFragment( desktopPageAjaxNavName, namedResourcesAddedFragmentsDefault, null ) )
        {
            desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME ).append( ".ajaxPageNavigation = " ).append( desktopPageAjaxNav ).append( ";" ).append( EOL );
        }
        
        String desktopWindowTilingName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_TILING;
        String desktopWindowTiling = HeaderResourceLib.makeJSONBoolean( desktopConfigMap.get( desktopWindowTilingName ) );
        if ( desktopWindowTiling != null && canAddHeaderNamedResourceFragment( desktopWindowTilingName, namedResourcesAddedFragmentsDefault, null ) )
        {
            desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME ).append( ".windowTiling = " ).append( desktopWindowTiling ).append( ";" ).append( EOL );
        }
        
        String desktopWindowHeightExpandName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_HEIGHT_EXPAND;
        String desktopWindowHeightExpand = HeaderResourceLib.makeJSONBoolean( desktopConfigMap.get( desktopWindowHeightExpandName ) );
        if ( desktopWindowHeightExpand != null && canAddHeaderNamedResourceFragment( desktopWindowHeightExpandName, namedResourcesAddedFragmentsDefault, null ) )
        {
            desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME ).append( ".windowHeightExpand = " ).append( desktopWindowHeightExpand ).append( ";" ).append( EOL );
        }

        String desktopWindowHeightName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_HEIGHT;
        String desktopWindowHeight = HeaderResourceLib.makeJSONInteger( desktopConfigMap.get( desktopWindowHeightName ), true );
        if ( desktopWindowHeight != null && canAddHeaderNamedResourceFragment( desktopWindowHeightName, namedResourcesAddedFragmentsDefault, null ) )
        {
            desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME ).append( ".windowHeight = " ).append( desktopWindowHeight ).append( ";" ).append( EOL );
        }
        
        String desktopWindowWidthName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_WIDTH;
        String desktopWindowWidth = HeaderResourceLib.makeJSONInteger( desktopConfigMap.get( desktopWindowWidthName ), true );
        if ( desktopWindowWidth != null && canAddHeaderNamedResourceFragment( desktopWindowWidthName, namedResourcesAddedFragmentsDefault, null ) )
        {
            desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME ).append( ".windowWidth = " ).append( desktopWindowWidth ).append( ";" ).append( EOL );
        }
        
        List actionList = new ArrayList();
        
        String windowActionButtonOrderName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ACTION_BUTTON_ORDER;
        String actionButtonOrderContent = HeaderResourceLib.makeJSONStringArray( (List)desktopConfigMap.get( windowActionButtonOrderName ), actionList );
        if ( actionButtonOrderContent != null && actionButtonOrderContent.length() > 0 )
        {
            if ( canAddHeaderNamedResourceFragment( windowActionButtonOrderName, namedResourcesAddedFragmentsDefault, null ) )
            {
                desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME_SCOPE ).append( HeaderResource.DESKTOP_JSON_WINDOW_ACTION_BUTTON_ORDER ).append( " = " ).append( actionButtonOrderContent ).append( ";" ).append( EOL );
            }
        }
        
        String windowActionNoImageName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ACTION_NOIMAGE;
        String actionNoImageContent = HeaderResourceLib.makeJSONStringArray( (List)desktopConfigMap.get( windowActionNoImageName ), actionList );
        if ( actionNoImageContent != null && actionNoImageContent.length() > 0 )
        {
            if ( canAddHeaderNamedResourceFragment( windowActionNoImageName, namedResourcesAddedFragmentsDefault, null ) )
            {
                desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME_SCOPE ).append( HeaderResource.DESKTOP_JSON_WINDOW_ACTION_NOIMAGE ).append( " = " ).append( actionNoImageContent ).append( ";" ).append( EOL );
            }
        }
        
        String windowActionMenuOrderName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ACTION_MENU_ORDER;
        String actionMenuOrderContent = HeaderResourceLib.makeJSONStringArray( (List)desktopConfigMap.get( windowActionMenuOrderName ), actionList );
        if ( actionMenuOrderContent != null && actionMenuOrderContent.length() > 0 )
        {
            if ( canAddHeaderNamedResourceFragment( windowActionMenuOrderName, namedResourcesAddedFragmentsDefault, null ) )
            {
                desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME_SCOPE ).append( HeaderResource.DESKTOP_JSON_WINDOW_ACTION_MENU_ORDER ).append( " = " ).append( actionMenuOrderContent ).append( ";" ).append( EOL );
            }
        }

        headerDynamicConfigurationDefault.put( HeaderResource.HEADER_INTERNAL_CONFIG_DESKTOP_WINDOW_ACTION, actionList );
        
        String windowActionButtonTooltipName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ACTION_BUTTON_TOOLTIP;
        String windowActionButtonTooltip = HeaderResourceLib.makeJSONBoolean( desktopConfigMap.get( windowActionButtonTooltipName ) );
        if ( windowActionButtonTooltip != null && canAddHeaderNamedResourceFragment( windowActionButtonTooltipName, namedResourcesAddedFragmentsDefault, null ) )
        {
            desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME_SCOPE ).append( HeaderResource.DESKTOP_JSON_WINDOW_ACTION_BUTTON_TOOLTIP ).append( " = " ).append( windowActionButtonTooltip ).append( ";" ).append( EOL );
        }

        String windowActionButtonMaxName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ACTION_BUTTON_MAX;
        String windowActionButtonMax = HeaderResourceLib.makeJSONInteger( desktopConfigMap.get( windowActionButtonMaxName ), false );
        if ( windowActionButtonMax != null && canAddHeaderNamedResourceFragment( windowActionButtonMaxName, namedResourcesAddedFragmentsDefault, null ) )
        {
            desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME_SCOPE ).append( HeaderResource.DESKTOP_JSON_WINDOW_ACTION_BUTTON_MAX ).append( " = " ).append( windowActionButtonMax ).append( ";" ).append( EOL );
        }
        
        String windowIconEnabledName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ICON_ENABLED;
        String iconEnabledContent = HeaderResourceLib.makeJSONBoolean( desktopConfigMap.get( windowIconEnabledName ) );
        if ( iconEnabledContent != null && iconEnabledContent.length() > 0 )
        {
            if ( canAddHeaderNamedResourceFragment( windowIconEnabledName, namedResourcesAddedFragmentsDefault, null ) )
            {
                desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME_SCOPE ).append( HeaderResource.DESKTOP_JSON_WINDOW_ICON_ENABLED ).append( " = " ).append( iconEnabledContent ).append( ";" ).append( EOL );
            }
        }
        
        String windowIconPathName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_ICON_PATH;
        String iconPathContent = (String)desktopConfigMap.get( windowIconPathName );
        if ( iconPathContent != null && iconPathContent.length() > 0 )
        {
            if ( canAddHeaderNamedResourceFragment( windowIconPathName, namedResourcesAddedFragmentsDefault, null ) )
            {
                desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME_SCOPE ).append( HeaderResource.DESKTOP_JSON_WINDOW_ICON_PATH ).append( " = \"" ).append( iconPathContent ).append( "\";" ).append( EOL );
            }
        }
        
        String windowTitlebarEnabledName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_TITLEBAR_ENABLED;
        String titlebarEnabledContent = HeaderResourceLib.makeJSONBoolean( desktopConfigMap.get( windowTitlebarEnabledName ) );
        if ( titlebarEnabledContent != null && titlebarEnabledContent.length() > 0 )
        {
            if ( canAddHeaderNamedResourceFragment( windowTitlebarEnabledName, namedResourcesAddedFragmentsDefault, null ) )
            {
                desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME_SCOPE ).append( HeaderResource.DESKTOP_JSON_WINDOW_TITLEBAR_ENABLED ).append( " = " ).append( titlebarEnabledContent ).append( ";" ).append( EOL );
            }
        }
        
        String windowResizebarEnabledName = HeaderResource.HEADER_CONFIG_DESKTOP_WINDOW_RESIZEBAR_ENABLED;
        String resizebarEnabledContent = HeaderResourceLib.makeJSONBoolean( desktopConfigMap.get( windowResizebarEnabledName ) );
        if ( resizebarEnabledContent != null && resizebarEnabledContent.length() > 0 )
        {
            if ( canAddHeaderNamedResourceFragment( windowResizebarEnabledName, namedResourcesAddedFragmentsDefault, null ) )
            {
                desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME_SCOPE ).append( HeaderResource.DESKTOP_JSON_WINDOW_TITLEBAR_ENABLED ).append( " = " ).append( resizebarEnabledContent ).append( ";" ).append( EOL );
            }
        }
        
        String pageActionButtonTooltipName = HeaderResource.HEADER_CONFIG_DESKTOP_PAGE_ACTION_BUTTON_TOOLTIP;
        String pageActionButtonTooltip = HeaderResourceLib.makeJSONBoolean( desktopConfigMap.get( pageActionButtonTooltipName ) );
        if ( pageActionButtonTooltip != null && canAddHeaderNamedResourceFragment( pageActionButtonTooltipName, namedResourcesAddedFragmentsDefault, null ) )
        {
            desktopDojoConfigContent.append( "    " ).append( HeaderResource.HEADER_INTERNAL_DOJO_CONFIG_JETSPEED_VAR_NAME ).append( ".pageActionButtonTooltip = " ).append( pageActionButtonTooltip ).append( ";" ).append( EOL );
        }

        if ( desktopDojoConfigContent.length() > 0 )
        {
            namedResourcesDefault.put( HeaderResource.HEADER_SECTION_DOJO_CONFIG, desktopDojoConfigContent.toString() );
        }
        
        StringBuffer desktopInitScript = new StringBuffer();
        desktopInitScript.append( "    function doRender(bindArgs,portletEntityId) { " );
        desktopInitScript.append( "jetspeed.doRender(bindArgs,portletEntityId); }" ).append( EOL );
        desktopInitScript.append( "    function doAction(bindArgs,portletEntityId) { " );
        desktopInitScript.append( "jetspeed.doAction(bindArgs,portletEntityId); }" ).append( EOL );
        desktopInitScript.append( "    dojo.addOnLoad( jetspeed.initializeDesktop );" ).append( EOL );
        if ( canAddHeaderNamedResourceFragment( "desktop.init", namedResourcesAddedFragmentsDefault, null ) )
        {
            namedResourcesDefault.put( HeaderResource.HEADER_SECTION_DESKTOP_INIT, desktopInitScript.toString() );
            setNamedHeaderResourceProperties( HeaderResource.HEADER_SECTION_DESKTOP_INIT, HeaderResource.HEADER_TYPE_SCRIPT_BLOCK_START, null, headerDynamicConfigurationDefault );
        }
    }
    
    /**
     * Read dojo header configuration settings and compile dojo header resource defaults
     */
    protected void initializeDojoHeaderConfigurationDefaults( Map dojoConfigMap, HashMap namedResourcesDefault, HashMap namedResourcesAddedFragmentsDefault, HashMap headerDynamicConfigurationDefault )
    {
        if ( dojoConfigMap == null )
        {
            dojoConfigMap = new HashMap();
        }
        String[] registryContent = new String[] { null };
        
        // add dojo.enable and dojo.path to dynamic configuration
        String dojoEnableName = HeaderResource.HEADER_CONFIG_DOJO_ENABLE;
        Object dojoEnableObj = dojoConfigMap.get( dojoEnableName );
        String dojoEnable = ( ( dojoEnableObj == null ) ? (String)null : dojoEnableObj.toString() );
        if ( dojoEnable == null || ! dojoEnable.equals( "true" ) )
        {
            dojoEnable = "false";
        }
        headerDynamicConfigurationDefault.put( dojoEnableName, dojoEnable );
        String dojoPath = (String)dojoConfigMap.get( HeaderResource.HEADER_CONFIG_DOJO_PATH );
        if ( dojoPath == null || dojoPath.length() == 0 )
        {
            dojoPath = "/javascript/dojo/";
        }
        headerDynamicConfigurationDefault.put( HeaderResource.HEADER_CONFIG_DOJO_PATH, dojoPath );
        
        // dojo parameters - djConfig parameters
        boolean dojoDebugEnabled = false;
        String dojoParamDebug = (String)dojoConfigMap.get( HeaderResource.HEADER_CONFIG_DOJO_PARAM_ISDEBUG );
        String dojoParamDebugAtAllCosts = null;
        if ( dojoParamDebug != null )
        	dojoParamDebug = dojoParamDebug.toLowerCase();
        if ( dojoParamDebug == null || dojoParamDebug.length() == 0 || dojoParamDebug.equals( "false" ) )
        {
        	dojoParamDebug = null;
        }
        else if ( dojoParamDebug.equals( "true" ) )
        {
        	dojoDebugEnabled = true;
        	dojoParamDebugAtAllCosts = (String)dojoConfigMap.get( HeaderResource.HEADER_CONFIG_DOJO_PARAM_DEBUGALLCOSTS );
        	if ( dojoParamDebugAtAllCosts != null )
        	{
        		dojoParamDebugAtAllCosts = dojoParamDebugAtAllCosts.toLowerCase();
        		if ( ! dojoParamDebugAtAllCosts.equals( "true") )
        		{
        			dojoParamDebugAtAllCosts = null;
        		}
        	}
        }
        
        String dojoParamPreventBackBtnFix = (String)dojoConfigMap.get( HeaderResource.HEADER_CONFIG_DOJO_PARAM_PREVENT_BACKBUTTON_FIX );
        String dojoParams = (String)dojoConfigMap.get( HeaderResource.HEADER_CONFIG_DOJO_PARAMS );
        if ( dojoParamDebug != null || dojoParamDebugAtAllCosts != null || dojoParamPreventBackBtnFix != null || dojoParams != null )
        {
            StringBuffer dojoConfigContent = new StringBuffer();
            boolean addedMembers = false;
            if ( dojoParams != null && dojoParams.length() > 0 )
            {
                dojoConfigContent.append( dojoParams );
                addedMembers = true;
            }
            if ( dojoParamDebug != null && dojoParamDebug.length() > 0 )
            {
                if ( addedMembers )
                {
                    dojoConfigContent.append( ", " );
                }
                dojoConfigContent.append( "isDebug: " ).append( dojoParamDebug ) ;
                addedMembers = true;
            }
            if ( dojoParamDebugAtAllCosts != null && dojoParamDebugAtAllCosts.length() > 0 )
            {
                if ( addedMembers )
                {
                    dojoConfigContent.append( ", " );
                }
                dojoConfigContent.append( "debugAtAllCosts: " ).append( dojoParamDebugAtAllCosts ) ;
                addedMembers = true;
            }
            if ( dojoParamPreventBackBtnFix != null && dojoParamPreventBackBtnFix.length() > 0 )
            {
                if ( addedMembers )
                {
                    dojoConfigContent.append( ", " );
                }
                dojoConfigContent.append( "preventBackButtonFix: " ).append( dojoParamPreventBackBtnFix ) ;
                addedMembers = true;
            }
            if ( addedMembers )
            {
                dojoConfigContent.append( ", " );
            }
            dojoConfigContent.append( HeaderResource.HEADER_INTERNAL_JETSPEED_VAR_NAME ).append( ": {}" ) ;
            addedMembers = true;
            
            if ( canAddHeaderNamedResourceFragment( HeaderResource.HEADER_CONFIG_DOJO_PARAMS, namedResourcesAddedFragmentsDefault, registryContent ) )
            {
                String dojoParamContent = dojoConfigContent.toString();
                if ( registryContent[0] != null )
                {
                    dojoParamContent = registryContent[0];
                }
                if ( dojoParamContent.length() > 0 )
                {
                    namedResourcesDefault.put( HeaderResource.HEADER_SECTION_DOJO_PARAMETERS, ( "    var djConfig = {" + dojoParamContent + "};" + EOL ) );
                }
            }
            registerAndOrderNamedHeaderResource( HeaderResource.HEADER_SECTION_DOJO_PARAMETERS, HeaderResource.HEADER_TYPE_SCRIPT_BLOCK_START, dojoEnableName, headerDynamicConfigurationDefault );
        }
        
        // dojo preinit - for automatically added members to djConfig (eg. djConfig.baseScriptUri="...")
        //    - adding to order only at this point
        //    - if header contains content, generated content will not be added
        registerAndOrderNamedHeaderResource( HeaderResource.HEADER_SECTION_DOJO_PREINIT, null, dojoEnableName, headerDynamicConfigurationDefault );
        
        // dojo config - for adding members to djConfig (eg. djConfig.parseWidgets=false)
        //    - adding to order only at this point
        registerAndOrderNamedHeaderResource( HeaderResource.HEADER_SECTION_DOJO_CONFIG, null, dojoEnableName, headerDynamicConfigurationDefault );
        
        // dojo init - script tag for dojo.js
        //    - adding to order only at this point
        //    - if header contains content, generated content will not be added
        registerAndOrderNamedHeaderResource( HeaderResource.HEADER_SECTION_DOJO_INIT, HeaderResource.HEADER_TYPE_SCRIPT_TAG, dojoEnableName, headerDynamicConfigurationDefault );
        
        // dojo requires - core libraries
        List dojoRequiresCore = (List)dojoConfigMap.get( HeaderResource.HEADER_CONFIG_DOJO_REQUIRES_CORE );
        if ( dojoRequiresCore != null && dojoRequiresCore.size() > 0 )
        {
            StringBuffer dojoRequiresContent = new StringBuffer();
            Iterator dojoRequiresCoreIter = dojoRequiresCore.iterator();
            while ( dojoRequiresCoreIter.hasNext() )
            {
                String dojoReq = (String)dojoRequiresCoreIter.next();
                if ( dojoReq != null && dojoReq.length() > 0 )
                {
                    if ( canAddHeaderNamedResourceFragment( dojoReq, namedResourcesAddedFragmentsDefault, registryContent ) )
                    {
                        if ( registryContent[0] != null )
                        {
                            String dojoReqFromRegistry = HeaderResourceLib.makeJavascriptStatement( registryContent[0], "    ", true );
                            if ( dojoReqFromRegistry.length() > 0 )
                            {
                                dojoRequiresContent.append( registryContent[0] );
                            }
                        }
                        else
                        {
                            dojoRequiresContent.append( "    dojo.require(\"").append( dojoReq ).append( "\");" ).append( EOL );
                        }
                    }
                }
            }
            namedResourcesDefault.put( HeaderResource.HEADER_SECTION_DOJO_REQUIRES_CORE, dojoRequiresContent.toString() );
            registerAndOrderNamedHeaderResource( HeaderResource.HEADER_SECTION_DOJO_REQUIRES_CORE, null, dojoEnableName, headerDynamicConfigurationDefault );
        }
        
        // dojo modules path definition
        List dojoModulesPath = (List)dojoConfigMap.get( HeaderResource.HEADER_CONFIG_DOJO_MODULES_PATH );
        if ( dojoModulesPath != null && dojoModulesPath.size() > 0 )
        {
            StringBuffer dojoModulesPathContent = new StringBuffer();
            boolean addedContent = false;
            Iterator dojoModulesPathIter = dojoModulesPath.iterator();
            while ( dojoModulesPathIter.hasNext() )
            {
                String dojoModule = (String)dojoModulesPathIter.next();
                if ( dojoModule != null && dojoModule.length() > 0 )
                {
                    if ( canAddHeaderNamedResourceFragment( dojoModule, namedResourcesAddedFragmentsDefault, registryContent ) )
                    {
                        String dojoModuleContent = null;
                        if ( registryContent[0] != null )
                        {
                            dojoModuleContent = registryContent[0];
                        }
                        else
                        {
                            dojoModuleContent = dojoModule;
                        }
                        dojoModuleContent = HeaderResourceLib.makeJavascriptStatement( dojoModuleContent, "    ", true );
                        if ( dojoModuleContent.length() > 0 )
                        {
                            dojoModulesPathContent.append( dojoModuleContent );
                            addedContent = true;
                        }
                    }
                }
            }
            if ( addedContent )
            {
                namedResourcesDefault.put( HeaderResource.HEADER_SECTION_DOJO_MODULES_PATH, dojoModulesPathContent.toString() );
                registerAndOrderNamedHeaderResource( HeaderResource.HEADER_SECTION_DOJO_MODULES_PATH, null, dojoEnableName, headerDynamicConfigurationDefault );
            }
        }
        
        // dojo modules namespace definition
        List dojoModulesNamespace = (List)dojoConfigMap.get( HeaderResource.HEADER_CONFIG_DOJO_MODULES_NAMESPACE );
        if ( dojoModulesNamespace != null && dojoModulesNamespace.size() > 0 )
        {
            StringBuffer dojoModulesNamespaceContent = new StringBuffer();
            boolean addedContent = false;
            Iterator dojoModulesNamespaceIter = dojoModulesNamespace.iterator();
            while ( dojoModulesNamespaceIter.hasNext() )
            {
                String dojoModuleWidget = (String)dojoModulesNamespaceIter.next();
                if ( dojoModuleWidget != null && dojoModuleWidget.length() > 0 )
                {
                    if ( canAddHeaderNamedResourceFragment( dojoModuleWidget, namedResourcesAddedFragmentsDefault, registryContent ) )
                    {
                        String dojoModuleContent = null;
                        if ( registryContent[0] != null )
                        {
                            dojoModuleContent = registryContent[0];
                        }
                        else
                        {
                            dojoModuleContent = dojoModuleWidget;
                        }
                        dojoModuleContent = HeaderResourceLib.makeJavascriptStatement( dojoModuleContent, "    ", true );
                        if ( dojoModuleContent.length() > 0 )
                        {
                            dojoModulesNamespaceContent.append( dojoModuleContent );
                            addedContent = true;
                        }
                    }
                }
            }
            if ( addedContent )
            {
                namedResourcesDefault.put( HeaderResource.HEADER_SECTION_DOJO_MODULES_NAMESPACE, dojoModulesNamespaceContent.toString() );
                // registerAndOrderNamedHeaderResource called below
            }
        }
        
        // dojo requires - module libraries (from add-on modules)
        List dojoRequiresModules = (List)dojoConfigMap.get( HeaderResource.HEADER_CONFIG_DOJO_REQUIRES_MODULES );
        if ( dojoRequiresModules != null && dojoRequiresModules.size() > 0 )
        {
        	HashMap addedReqs = null;
        	if ( dojoDebugEnabled )
        		addedReqs = new HashMap();
            StringBuffer dojoRequiresContent = new StringBuffer();
            Iterator dojoRequiresModulesIter = dojoRequiresModules.iterator();
            while ( dojoRequiresModulesIter.hasNext() )
            {
                String dojoReq = (String)dojoRequiresModulesIter.next();
                if ( dojoReq != null && dojoReq.length() > 0 )
                {
                    if ( canAddHeaderNamedResourceFragment( dojoReq, namedResourcesAddedFragmentsDefault, registryContent ) )
                    {
                        if ( registryContent[0] != null )
                        {
                            String dojoReqFromRegistry = HeaderResourceLib.makeJavascriptStatement( registryContent[0], "    ", true );
                            if ( dojoReqFromRegistry.length() > 0 )
                            {
                                dojoRequiresContent.append( registryContent[0] );
                            }
                        }
                        else
                        {
                            dojoRequiresContent.append( "    dojo.require(\"").append( dojoReq ).append( "\");" ).append( EOL );
                            if ( dojoDebugEnabled )
                            	addedReqs.put( dojoReq, dojoReq ); 
                        }
                    }
                }
            }
            if ( dojoDebugEnabled )
            {
            	if ( addedReqs.get( HeaderResource.HEADER_DEBUG_REQUIRES ) == null )
            	{
            		dojoRequiresContent.append( "    dojo.require(\"").append( HeaderResource.HEADER_DEBUG_REQUIRES ).append( "\");" ).append( EOL );
            	}
            }            
            namedResourcesDefault.put( HeaderResource.HEADER_SECTION_DOJO_REQUIRES_MODULES, dojoRequiresContent.toString() );
            registerAndOrderNamedHeaderResource( HeaderResource.HEADER_SECTION_DOJO_REQUIRES_MODULES, null, dojoEnableName, headerDynamicConfigurationDefault );
        }
        
        // dojo writeincludes
        //    - adding to order only at this point
        //    - if header contains content, generated content will not be added
        registerAndOrderNamedHeaderResource( HeaderResource.HEADER_SECTION_DOJO_WRITEINCLUDES, HeaderResource.HEADER_TYPE_SCRIPT_BLOCK_START, dojoEnableName, headerDynamicConfigurationDefault );
        
        // dojo widget module - register widget packages (eg. dojo.widget.manager.registerWidgetPackage('jetspeed.ui.widget'))
        //    - default resource added above
        registerAndOrderNamedHeaderResource( HeaderResource.HEADER_SECTION_DOJO_MODULES_NAMESPACE, HeaderResource.HEADER_TYPE_SCRIPT_BLOCK_START, dojoEnableName, headerDynamicConfigurationDefault );
        
        // dojo style bodyexpand
        setNamedHeaderResourceProperties( HeaderResource.HEADER_SECTION_DOJO_STYLE_BODYEXPAND, HeaderResource.HEADER_TYPE_STYLE_BLOCK, dojoEnableName, headerDynamicConfigurationDefault );
        
        // dojo style bodyexpand noscroll
        setNamedHeaderResourceProperties( HeaderResource.HEADER_SECTION_DOJO_STYLE_BODYEXPAND_NOSCROLL, HeaderResource.HEADER_TYPE_STYLE_BLOCK, dojoEnableName, headerDynamicConfigurationDefault );
    }

    
    /**
     * Builds the portlet set defined in the context into a portlet tree.
     * 
     * @return Unique Portlet Entity ID
     */
    public void build( RequestContext context ) throws JetspeedException, IOException
    {
        ContentPage page = context.getPage();
        if ( null == page )
        {
            throw new JetspeedException( "Failed to find PSML Pin ContentPageAggregator.build" );
        }

        ContentFragment root = page.getRootFragment();

        if ( root == null )
        {
            throw new JetspeedException( "No root ContentFragment found in ContentPage" );
        }

        // add named-resources and named-resources-added maps as request attributes
        Map dynamicConfigDefault = getHeaderDynamicConfigurationDefault();
        Map namedResourcesDefault = getHeaderNamedResourcesDefault();
        Map namedResourcesAddedFragmentsDefault = getHeaderNamedResourcesAddedFragmentsDefault();
        
        /*if ( log.isDebugEnabled() && namedResourcesDefault != null )
        {
            Iterator namedResourcesDefaultIter = namedResourcesDefault.entrySet().iterator();
            while ( namedResourcesDefaultIter.hasNext() )
            {
                Map.Entry rsrcEntry = (Map.Entry)namedResourcesDefaultIter.next();
                Object rsrcVal = rsrcEntry.getValue();
                log.debug( rsrcEntry.getKey().toString() + ": " + EOL + ( rsrcVal != null ? rsrcVal.toString() : "null" ) );
            }
        }*/
        
        if ( dynamicConfigDefault != null || namedResourcesDefault != null || namedResourcesAddedFragmentsDefault != null )
        {
            Map existingNamedResources = (Map)context.getAttribute( PortalReservedParameters.HEADER_NAMED_RESOURCE_ATTRIBUTE );
            if ( existingNamedResources == null )
            {
                if ( dynamicConfigDefault == null )
                {
                    context.setAttribute( PortalReservedParameters.HEADER_CONFIGURATION_ATTRIBUTE, new HashMap() );
                }
                else
                {
                    HashMap dynamicConfig = new HashMap();
                    Iterator hConfigEntryIter = dynamicConfigDefault.entrySet().iterator();
                    while ( hConfigEntryIter.hasNext() )
                    {
                        Map.Entry hConfigEntry = (Map.Entry)hConfigEntryIter.next();
                        Object hConfigKey = hConfigEntry.getKey();
                        Object hConfigVal = hConfigEntry.getValue();
                        if ( hConfigVal instanceof Map )
                        {
                            dynamicConfig.put( hConfigKey, new HashMap( (Map)hConfigVal ) );
                        }
                        else if ( hConfigVal instanceof List )
                        {
                            dynamicConfig.put( hConfigKey, new ArrayList( (List)hConfigVal ) );
                        }
                        else
                        {
                            dynamicConfig.put( hConfigKey, hConfigVal );
                        }
                    }
                    context.setAttribute( PortalReservedParameters.HEADER_CONFIGURATION_ATTRIBUTE, dynamicConfig );
                }
                
                if ( namedResourcesDefault != null )
                {
                    context.setAttribute( PortalReservedParameters.HEADER_NAMED_RESOURCE_ATTRIBUTE, new HashMap( namedResourcesDefault ) );
                }
                if ( namedResourcesAddedFragmentsDefault != null )
                {
                    context.setAttribute( PortalReservedParameters.HEADER_NAMED_RESOURCE_ADDED_FRAGMENTS_ATTRIBUTE, new HashMap( namedResourcesAddedFragmentsDefault ) );
                }
            }
        }
        if ( getHeaderResourceRegistry() != null )
        {
            context.setAttribute( PortalReservedParameters.HEADER_NAMED_RESOURCE_REGISTRY_ATTRIBUTE, getHeaderResourceRegistry() );
        }
    }

    protected PortletFactory getPortletFactory()
    {
        return this.factory;
    }
    protected HeaderResourceFactory getHeaderResourceFactory()
    {
        return this.headerResourceFactory;
    }
    protected boolean isDesktop()
    {
        return this.isDesktop;
    }
    protected Map getHeaderConfiguration()
    {
        return this.headerConfiguration;
    }
    protected Map getHeaderResourceRegistry()
    {
        return this.headerResourceRegistry;
    }
    protected Map getHeaderDynamicConfigurationDefault()
    {
        return this.headerDynamicConfigurationDefault;
    }
    protected Map getHeaderNamedResourcesDefault()
    {
        return this.headerNamedResourcesDefault;
    }
    protected Map getHeaderNamedResourcesAddedFragmentsDefault()
    {
        return this.headerNamedResourcesAddedFragmentsDefault;
    }
    protected BasePortalURL getBaseUrlAccess()
    {
        return this.baseUrlAccess;
    }
    public void renderContent(RequestContext context, ContentFragment root) throws JetspeedException, IOException
    {
    }
}
