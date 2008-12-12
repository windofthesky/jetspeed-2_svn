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

import org.apache.jetspeed.headerresource.HeaderResourceFactory;
import org.apache.jetspeed.request.RequestContext;

/**
 * Jetspeed Desktop 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface JetspeedDesktop 
{
    String DESKTOP_ENABLED_REQUEST_ATTRIBUTE = "desktop.enabled";
    
    String DESKTOP_ENCODER_REQUEST_PARAMETER = "encoder";
    String DESKTOP_ENCODER_REQUEST_PARAMETER_VALUE = "desktop";
    
    String DESKTOP_AJAX_REQUEST_PARAMETER = "jsdajax";
    String DESKTOP_REQUEST_NOT_AJAX_PARAMETER = "org.apache.jetspeed.desktop.request.not.ajax";
    
    String DEFAULT_DESKTOP_PIPELINE_PATH = "/desktop";
    String DEFAULT_DESKTOP_ACTION_PIPELINE_PATH = "/action";
    String DEFAULT_DESKTOP_RENDER_PIPELINE_PATH = "/render";
    String DEFAULT_DESKTOP_CONFIGURE_PIPELINE_PATH = "/dtconfigure";

    /**
     * Render a desktop theme.
     * 
     * @param request
     */
    void render(RequestContext request);
    
    /**
     * Indicates whether /desktop is enabled for the current portal request.
     * Located here due to range of jetspeed components which need this information and
     * already have a DecorationFactory reference.
     * 
     * @param requestContext current portal request.
     * 
     * @return true if /desktop is enabled for the current portal request, otherwise false
     */
    boolean isDesktopEnabled( RequestContext requestContext );
    
    /**
     * Retrieve the header resource factory
     * 
     * @return header resource factory
     */
    HeaderResourceFactory getHeaderResourceFactory();
    
    /**
     * Desktop servlet path ( e.g. /desktop )
     * 
     * @return portal base url
     */
    public String getDesktopServletPath();
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl( RequestContext requestContext );
    
    /**
     * Portal base url ( e.g. http://localhost:8080/jetspeed )
     * 
     * @return portal base url
     */
    public String getPortalBaseUrl( RequestContext requestContext, boolean encode );
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( RequestContext requestContext, String relativePath );
    
    /**
     * Portal base url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/javascript/dojo/ )
     * 
     * @return portal base url with relativePath argument appended
     */
    public String getPortalResourceUrl( RequestContext requestContext, String relativePath, boolean encode );
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl( RequestContext requestContext );
    
    /**
     * Portal base servlet url ( e.g. http://localhost:8080/jetspeed/desktop/ )
     * 
     * @return portal base servlet url
     */
    public String getPortalUrl( RequestContext requestContext, boolean encode );
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( RequestContext requestContext, String relativePath );
    
    /**
     * Portal base servlet url with relativePath argument appended ( e.g. http://localhost:8080/jetspeed/desktop/default-page.psml )
     * 
     * @return portal base servlet url with relativePath argument appended
     */
    public String getPortalUrl( RequestContext requestContext, String relativePath, boolean encode );
            
}
