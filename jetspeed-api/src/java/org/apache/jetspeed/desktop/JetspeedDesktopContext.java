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
package org.apache.jetspeed.desktop;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.jetspeed.headerresource.HeaderResource;

/**
 * Jetspeed Desktop 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: JetspeedDesktopContext.java $
 */
public interface JetspeedDesktopContext
{
    String DESKTOP_ATTRIBUTE = "jetspeedDesktop";
    
    String RESOURCES_DIRECTORY_NAME = "resources";
    
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
     * Gets the desktop-theme name
     * 
     * @return
     */
    public String getDesktopTheme();

    /**
     * Gets an absolute resource url to the desktop-theme directory
     * 
     * @return
     */
    public String getDesktopThemeRootUrl();

    /**
     * Gets an absolute resource url to a desktop-theme resource
     * 
     * @param relativePath
     * @return
     */
    public String getDesktopThemeResourceUrl( String relativePath );
    
    /**
     * Gets an relative resource url to a desktop-theme resource
     * 
     * @param relativePath
     * @return
     */
    public String getDesktopThemeResource( String relativePath );
    
    public ResourceBundle getResourceBundle(Locale locale);
    
    public HeaderResource getHeaderResource();
}
