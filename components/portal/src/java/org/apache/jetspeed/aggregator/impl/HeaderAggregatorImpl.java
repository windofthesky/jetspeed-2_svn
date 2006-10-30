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
package org.apache.jetspeed.aggregator.impl;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import javax.portlet.Portlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.aggregator.FailedToRenderFragmentException;
import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.container.state.NavigationalState;
import org.apache.jetspeed.container.url.BasePortalURL;
import org.apache.jetspeed.container.window.PortletWindowAccessor;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.headerresource.HeaderResourceLib;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.portlet.PortletHeaderRequest;
import org.apache.jetspeed.portlet.PortletHeaderResponse;
import org.apache.jetspeed.portlet.SupportsHeaderPhase;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.PortalReservedParameters;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;

/**
 * HeaderAggregator builds the content required to render a page of portlets.
 * 
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: PageAggregatorImpl.java 359125 2005-12-26 23:16:39Z rwatler $
 */
public class HeaderAggregatorImpl implements PageAggregator
{
    protected final static Log log = LogFactory.getLog( HeaderAggregatorImpl.class );
    protected final static String EOL = "\r\n";   // html eol

    private PortletFactory factory;
    private PortletWindowAccessor windowAccessor;
    private HeaderResourceFactory headerResourceFactory;
    
    private boolean isDesktop;
    
    private Map headerConfiguration;
    private Map headerResourceRegistry;
    private Map headerDynamicConfigurationDefault;
    private Map headerNamedResourcesDefault;
    private Map headerNamedResourcesAddedFragmentsDefault;
    
    /** base portal URL to override default URL server info from servlet */
    private BasePortalURL baseUrlAccess = null;
    
    
    public HeaderAggregatorImpl( PortletFactory factory,
                                 PortletWindowAccessor windowAccessor,
                                 HeaderResourceFactory headerResourceFactory,
                                 boolean isDesktop,
                                 Map headerConfiguration,
                                 Map headerResourceRegistry )
    {
        this( factory, windowAccessor, headerResourceFactory, isDesktop, headerConfiguration, headerResourceRegistry, null );
    }
    
    public HeaderAggregatorImpl( PortletFactory factory,
                                 PortletWindowAccessor windowAccessor,
                                 HeaderResourceFactory headerResourceFactory,
                                 boolean isDesktop,
                                 Map headerConfiguration,
                                 Map headerResourceRegistry,
                                 BasePortalURL baseUrlAccess )
    {
        this.factory = factory;
        this.windowAccessor = windowAccessor;
        this.headerResourceFactory = headerResourceFactory;
        this.isDesktop = isDesktop;
        this.baseUrlAccess = baseUrlAccess;
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
                    Object headerNameObj = (String)headerOrderListIter.next();
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
        return false;
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
            if ( headerTypeId < 0 )
            {
                log.error( "HeaderAggregatorImpl.registerAndOrderNamedHeaderResource() ignoring specification of unknown header section type; header-section-name=" + headerName + " header-section-type=" + headerType );
            }
            
            if ( ( headerTypeId >= 0 ) || ( headerReqFlag != null && headerReqFlag.length() > 0 ) )
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
            String registryContentVal = (String)this.headerResourceRegistry.get( headerFragmentName );
            registryContent[0] = registryContentVal;
            if ( registryContentVal != null )
            {
                this.headerResourceRegistry.remove( headerFragmentName );
            }
            return true;
        }
        registryContent[0] = null;
        return false;
    }
    
    protected String makeJavascriptStatement( String statement, String indent, boolean addEOL )
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
    
    
    /**
     * Read dojo header configuration settings and compile dojo header resource defaults
     */
    protected void initializeDojoHeaderConfigurationDefaults( Map dojoConfigMap, HashMap namedResourcesDefault, HashMap namedResourcesAddedFragmentsDefault, HashMap headerDynamicConfigurationDefault )
    {
        if ( dojoConfigMap != null && dojoConfigMap.size() > 0 )
        {
            String[] registryContent = new String[] { null };
            
            // add dojo.enable and dojo.path to dynamic configuration
            String dojoEnableName = "dojo.enable";
            Object dojoEnableObj = dojoConfigMap.get( dojoEnableName );
            String dojoEnable = ( ( dojoEnableObj == null ) ? (String)null : dojoEnableObj.toString() );
            if ( dojoEnable == null || ! dojoEnable.equals( "true" ) )
            {
                dojoEnable = "false";
            }
            headerDynamicConfigurationDefault.put( dojoEnableName, dojoEnable );
            String dojoPath = (String)dojoConfigMap.get( "dojo.path" );
            if ( dojoPath == null || dojoPath.length() == 0 )
            {
                dojoPath = "/javascript/dojo/";
            }
            headerDynamicConfigurationDefault.put( "dojo.path", dojoPath );
            
            // dojo parameters - djConfig parameters
            String dojoParamDebug = (String)dojoConfigMap.get( "dojo.parameter.isDebug" );
            String dojoParamDebugAtAllCosts = (String)dojoConfigMap.get( "dojo.parameter.debugAtAllCosts" );
            String dojoParams = (String)dojoConfigMap.get( "dojo.parameters" );
            if ( dojoParamDebug != null || dojoParamDebugAtAllCosts != null || dojoParams != null )
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
                if ( canAddHeaderNamedResourceFragment( "dojo.parameters", namedResourcesAddedFragmentsDefault, registryContent ) )
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
            List dojoRequiresCore = (List)dojoConfigMap.get( "dojo.requires.core" );
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
                                String dojoReqFromRegistry = makeJavascriptStatement( registryContent[0], "    ", true );
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
            
            // dojo module definition
            List dojoModules = (List)dojoConfigMap.get( "dojo.modules" );
            if ( dojoModules != null && dojoModules.size() > 0 )
            {
                StringBuffer dojoModulesContent = new StringBuffer();
                boolean addedContent = false;
                Iterator dojoModulesIter = dojoModules.iterator();
                while ( dojoModulesIter.hasNext() )
                {
                    String dojoModule = (String)dojoModulesIter.next();
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
                            dojoModuleContent = makeJavascriptStatement( dojoModuleContent, "    ", true );
                            if ( dojoModuleContent.length() > 0 )
                            {
                                dojoModulesContent.append( dojoModuleContent );
                                addedContent = true;
                            }
                        }
                    }
                }
                if ( addedContent )
                {
                    namedResourcesDefault.put( HeaderResource.HEADER_SECTION_DOJO_MODULES_PATH, dojoModulesContent.toString() );
                    registerAndOrderNamedHeaderResource( HeaderResource.HEADER_SECTION_DOJO_MODULES_PATH, null, dojoEnableName, headerDynamicConfigurationDefault );
                }
            }
            
            // dojo widget module definition
            List dojoModulesWidget = (List)dojoConfigMap.get( "dojo.modules.widget" );
            if ( dojoModulesWidget != null && dojoModulesWidget.size() > 0 )
            {
                StringBuffer dojoModulesWidgetContent = new StringBuffer();
                boolean addedContent = false;
                Iterator dojoModulesWidgetIter = dojoModulesWidget.iterator();
                while ( dojoModulesWidgetIter.hasNext() )
                {
                    String dojoModuleWidget = (String)dojoModulesWidgetIter.next();
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
                            dojoModuleContent = makeJavascriptStatement( dojoModuleContent, "    ", true );
                            if ( dojoModuleContent.length() > 0 )
                            {
                                dojoModulesWidgetContent.append( dojoModuleContent );
                                addedContent = true;
                            }
                        }
                    }
                }
                if ( addedContent )
                {
                    namedResourcesDefault.put( HeaderResource.HEADER_SECTION_DOJO_MODULES_NAMESPACE, dojoModulesWidgetContent.toString() );
                    // registerAndOrderNamedHeaderResource called below
                }
            }
            
            // dojo requires - module libraries (from add-on modules)
            List dojoRequiresModules = (List)dojoConfigMap.get( "dojo.requires.modules" );
            if ( dojoRequiresModules != null && dojoRequiresModules.size() > 0 )
            {
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
                                String dojoReqFromRegistry = makeJavascriptStatement( registryContent[0], "    ", true );
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
                namedResourcesDefault.put( HeaderResource.HEADER_SECTION_DOJO_REQUIRES_MODULES, dojoRequiresContent.toString() );
                registerAndOrderNamedHeaderResource( HeaderResource.HEADER_SECTION_DOJO_REQUIRES_MODULES, null, dojoEnableName, headerDynamicConfigurationDefault );
            }
            
            // dojo writeincludes - for automatically added members to djConfig (eg. djConfig.baseScriptUri="...")
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
            
        }   // if ( dojoConfigMap != null && dojoConfigMap.size() > 0 )
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

        ContentFragment root = page.getRootContentFragment();

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
        
        // handle maximized state
        boolean atLeastOneHasHeaderPhase = false;
        NavigationalState nav = context.getPortalURL().getNavigationalState();
        PortletWindow window = nav.getMaximizedWindow();
        if ( null != window )
        {
            ContentFragment maxedContentFragment = page.getContentFragmentById( window.getId().toString() );
            if ( maxedContentFragment != null )
            {
                atLeastOneHasHeaderPhase = renderHeaderFragment( context, maxedContentFragment );
            }
        }
        else
        {
            atLeastOneHasHeaderPhase = aggregateAndRender( root, context, page );
        }
        
        if ( atLeastOneHasHeaderPhase )
        {
            
        }
    }

    protected boolean aggregateAndRender( ContentFragment fragment, RequestContext context, ContentPage page )
            throws FailedToRenderFragmentException
    {
        boolean atLeastOneHasHeaderPhase = false;
        boolean hasHeaderPhase = false;
        if ( fragment.getContentFragments() != null && fragment.getContentFragments().size() > 0 )
        {
            Iterator children = fragment.getContentFragments().iterator();
            while (children.hasNext())
            {
                ContentFragment child = (ContentFragment) children.next();
                if ( ! "hidden".equals( fragment.getState() ) )
                {
                    hasHeaderPhase = aggregateAndRender( child, context, page );
                    if ( hasHeaderPhase )
                    {
                        atLeastOneHasHeaderPhase = true;
                    }
                }
            }
        }
        hasHeaderPhase = renderHeaderFragment( context, fragment );
        if ( hasHeaderPhase )
        {
            atLeastOneHasHeaderPhase = true;
        }
        return atLeastOneHasHeaderPhase;
    }
    
    protected boolean renderHeaderFragment( RequestContext context, ContentFragment fragment )
    {
        try
        {
            if ( fragment.getType().equals( ContentFragment.LAYOUT ) )
            {
                return false;
            }
            
            PortletWindow portletWindow = getPortletWindowAccessor().getPortletWindow( fragment );
            PortletDefinition pd = portletWindow.getPortletEntity().getPortletDefinition();
            String portletApplicationContextPath = pd.getPortletApplicationDefinition().getWebApplicationDefinition().getContextRoot();
            Portlet portlet = getPortletFactory().getPortletInstance( context.getConfig().getServletContext().getContext( portletApplicationContextPath ), pd ).getRealPortlet();            
            if ( portlet instanceof SupportsHeaderPhase )
            {
                log.debug( "renderHeaderFragment: " + pd.getName() + " supports header phase" );
                
                HeaderResource hr = getHeaderResourceFactory().getHeaderResource( context, this.baseUrlAccess, isDesktop(), getHeaderConfiguration() );
                PortletHeaderRequest headerRequest = new PortletHeaderRequestImpl( context, portletWindow, portletApplicationContextPath );
                PortletHeaderResponse headerResponse = new PortletHeaderResponseImpl( context, hr, isDesktop(), getHeaderConfiguration(), getHeaderResourceRegistry() );
                ((SupportsHeaderPhase)portlet).doHeader( headerRequest, headerResponse );
                return true;
            }
        }
        catch ( Exception e )
        {
            log.error( "renderHeaderFragment failed", e );
        }
        return false;
    }
    
    protected PortletFactory getPortletFactory()
    {
        return this.factory;
    }
    protected PortletWindowAccessor getPortletWindowAccessor()
    {
        return this.windowAccessor;
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
}
