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
package org.apache.jetspeed.layout.impl;

import java.util.Map;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.PortletDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PageMetadataAccess used to extract layout data from
 * Fragment and ContentFragment hierarchies.
 *
 * @author <a>David Gurney</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class PortletPlacementMetadataAccess
{
    private static Logger log = LoggerFactory.getLogger( PortletPlacementMetadataAccess.class );

    /**
     * Access layout fragment hierarchy metadata.
     *  
     * @param layoutFragment layout fragment root
     * @param registry registry component
     * @param fragSizes returned sizes metadata
     * @param suppressErrorLogging logging flag
     * @return column count metadata
     */
    public static int getColumnCountAndSizes( Object layoutFragment, PortletRegistry registry, Map fragSizes )
    {
        return getColumnCountAndSizes( layoutFragment, registry, fragSizes, false );
    }
    
    /**
     * Access layout fragment hierarchy metadata.
     *  
     * @param layoutFragment layout fragment root
     * @param registry registry component
     * @param fragSizes returned sizes metadata
     * @param suppressErrorLogging logging flag
     * @return column count metadata
     */
    public static int getColumnCountAndSizes( Object layoutFragment, PortletRegistry registry, Map fragSizes, boolean suppressErrorLogging )
    {
        if ( ! ( ( layoutFragment instanceof ContentFragment ) || ( layoutFragment instanceof Fragment ) ) )
        {
            throw new NullPointerException( "getColumnCountAndSizes cannot accept a null or non Fragment argument" );
        }
        if ( registry == null )
        {
            throw new NullPointerException( "getColumnCountAndSizes cannot accept a null PortletRegistry argument" );
        }
        
        int columnCount = -1;
        if ( ! "layout".equals( getType( layoutFragment ) ) )
        {
            if ( ! suppressErrorLogging )
            {
                log.error( "getColumnCountAndSizes not a layout fragment - " + getId( layoutFragment ) + " type=" + getType( layoutFragment ) );
            }
        }
        else
        {   // get layout fragment sizes
            String sizesVal = getProperty( layoutFragment, "sizes" );
            String layoutName = getName( layoutFragment );
            layoutName = ( (layoutName != null && layoutName.length() > 0) ? layoutName : (String)null );
            PortletDefinition portletDef = null;
            if ( sizesVal == null || sizesVal.length() == 0 )
            {
                if ( layoutName != null )
                {
                    // logic below is copied from org.apache.jetspeed.portlets.MultiColumnPortlet
                    portletDef = registry.getPortletDefinitionByUniqueName( layoutName, true );
                    if ( portletDef != null )
                    {
                        InitParam sizesParam = portletDef.getInitParam( "sizes" );
                        sizesVal = ( sizesParam == null ) ? null : sizesParam.getParamValue();
                    }
                }
            }
            if ( sizesVal != null && sizesVal.length() > 0 )
            {
                if ( fragSizes != null )
                {
                    fragSizes.put( getId( layoutFragment ), sizesVal );
                }
                    
                int sepPos = -1, startPos = 0, sizesLen = sizesVal.length();
                columnCount = 0;
                do
                {
                    sepPos = sizesVal.indexOf( ',', startPos );
                    if ( sepPos != -1 )
                    {
                        if ( sepPos > startPos )
                        {
                            columnCount++;
                        }
                        startPos = sepPos +1;
                    }
                    else if ( startPos < sizesLen )
                    {
                        columnCount++;
                    }
                }
                while ( startPos < sizesLen && sepPos != -1 );
                
                if ( ! suppressErrorLogging && columnCount <= 0 )
                {
                    log.error( "getColumnCountAndSizes invalid columnCount - " + getId( layoutFragment ) + " / " + layoutName + " count=" + columnCount + " sizes=" + sizesVal );
                }
            }
            else if ( portletDef == null || portletDef.getInitParams().isEmpty() )
            {
                if ( ! suppressErrorLogging )
                {
                    if ( layoutName == null )
                    {
                        log.error( "getColumnCountAndSizes null sizes, null layoutName - " + getId( layoutFragment ) );
                    }
                    else if ( portletDef == null )
                    {
                        log.error( "getColumnCountAndSizes null sizes, null PortletDefinition - " + getId( layoutFragment ) + " / " + layoutName );
                    }
                    else
                    {
                        log.error( "getColumnCountAndSizes null sizes, null ParameterSet - " + getId( layoutFragment ) + " / " + layoutName );
                    }
                }
            }
            else
            {
                InitParam colsParam = portletDef.getInitParam( "columns" );
                String colsParamVal = ( colsParam == null ) ? null : colsParam.getParamValue();
                if ( colsParamVal != null && colsParamVal.length() > 0 )
                {
                    try
                    {
                        columnCount = Integer.parseInt( colsParamVal );
                    }
                    catch ( NumberFormatException ex )
                    {
                    }
                    if ( columnCount < 1 )
                    {
                        columnCount = 2;
                    }
                    switch ( columnCount )
                    {
                        case 1: sizesVal = "100%"; break;
                        case 2: sizesVal = "50%,50%"; break;
                        case 3: sizesVal = "34%,33%,33%"; break;
                        case 4: sizesVal = "25%,25%,25%,25%"; break;
                        default: 
                        {
                            sizesVal = "50%,50%";
                            columnCount = 2;
                            break;
                        }
                    }
                    if ( fragSizes != null )
                    {
                        fragSizes.put( getId( layoutFragment ), sizesVal );
                    }
                    // log.info( "getColumnCountAndSizes " + getId( layoutFragment ) + " count=" + columnCount + " defaulted-sizes=" + sizesVal );
                }
                else
                {
                    if ( ! suppressErrorLogging )
                    {
                        log.error( "getColumnCountAndSizes null sizes, columns not defined in ParameterSet - " + getId( layoutFragment ) + " / " + layoutName );
                    }
                }
            }
        }
        return columnCount;
    }
    
    /**
     * Get layout fragment type.
     * 
     * @param f fragment
     * @return fragment type
     */
    private static String getType( Object f )
    {
        return ( ( f instanceof ContentFragment ) ? ((ContentFragment) f).getType() : ((Fragment) f).getType() );
    }

    /**
     * Get layout fragment id.
     * 
     * @param f fragment
     * @return fragment id
     */
    private static String getId( Object f )
    {
        return ( ( f instanceof ContentFragment ) ? ((ContentFragment) f).getId() : ((Fragment) f).getId() );
    }

    /**
     * Get layout fragment name.
     * 
     * @param f fragment
     * @return fragment name
     */
    private static String getName( Object f )
    {
        return ( ( f instanceof ContentFragment ) ? ((ContentFragment) f).getName() : ((Fragment) f).getName() );
    }

    /**
     * Get layout fragment property value.
     * 
     * @param f fragment
     * @param propertyName fragment property name
     * @return property value
     */
    private static String getProperty( Object f, String propertyName )
    {
        return ( ( f instanceof ContentFragment ) ? ((ContentFragment) f).getProperty( propertyName ) : ((Fragment) f).getProperty( propertyName ) );
    }
}
