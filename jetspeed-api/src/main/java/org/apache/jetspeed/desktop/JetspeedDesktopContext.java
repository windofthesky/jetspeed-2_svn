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
package org.apache.jetspeed.desktop;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.jetspeed.headerresource.HeaderResource;

/**
 * Jetspeed Desktop 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:smilek@apache.org">Steve Milek</a>
 * @version $Id: JetspeedDesktopContext.java $
 */
public interface JetspeedDesktopContext
{
    String DESKTOP_CONTEXT_ATTRIBUTE = "jetspeedDesktop";
    String DESKTOP_REQUEST_CONTEXT_ATTRIBUTE = "JS2RequestContext";
    String DESKTOP_COMPONENT_MANAGER_ATTRIBUTE = "JS2ComponentManager";
    
    String LAYOUT_TEMPLATE_EXTENSION_PROP = "template.extension";
    String LAYOUT_DESKTOP_TEMPLATE_EXTENSION_PROP = "desktop.template.extension";
    
    String LAYOUT_TEMPLATE_ID_PROP = "template.id";
    String LAYOUT_PRINT_TEMPLATE_ID_PROP = "template.print.id";
    String LAYOUT_PORTALUSER_TEMPLATE_ID_PROP = "template.portaluser.id";
    
    String LAYOUT_TEMPLATE_ID_DEFAULT = "desktop";

    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl();
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl( boolean encode );
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( String relativePath );
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( String relativePath, boolean encode );
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl();
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl( boolean encode );
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( String relativePath );
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( String relativePath, boolean encode );

    /**
     * Gets the layout decoration name
     * 
     * @return
     */
    public String getLayoutDecorationName();
    
    /**
     * <p>
     * Get the path to the layout decoration desktop template file.
     * </p>
     * 
     * @return the desktop template file path.
     */
    public String getLayoutTemplatePath();

    /**
     * <p>
     * Get the path to the layout decoration desktop template file.
     * The property name parameter is provided to allow for an alternate
     * property value to be used as the filename (without extension)
     * of the desktop template file.
     * </p>
     * 
     * @return the desktop template file path.
     */
    public String getLayoutTemplatePath( String layoutTemplateIdPropertyName );
    
    /**
     * <p>
     * Returns the base path for the layout decoration.
     * </p>
     * 
     * @return the base path for the layout decoration.
     */
    public String getLayoutBasePath();
    
    /**
     * <p>
     * Returns the base path for the layout decoration
     * with the relativePath argument added.
     * </p>
     * 
     * @param relativePath
     * @return the base path for the layout decoration with the relativePath argument added.
     */
    public String getLayoutBasePath( String relativePath );
    
    /**
     * <p>
     * Returns the base url for the layout decoration.
     * </p>
     * 
     * @return the base url for the layout decoration.
     */
    public String getLayoutBaseUrl();
    
    /**
     * <p>
     * Returns the base url for the layout decoration
     * with the relativePath argument added.
     * </p>
     * 
     * @param relativePath
     * @return the base url for the layout decoration with the relativePath argument added.
     */
    public String getLayoutBaseUrl( String relativePath );
    
    /**
     * @return the layout decoration resource bundle for the given Locale.
     */
    public ResourceBundle getLayoutResourceBundle( Locale locale );
    
    /**
     * @return the HeaderResource component.
     */
    public HeaderResource getHeaderResource();
}
