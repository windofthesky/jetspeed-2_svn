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
package org.apache.jetspeed.portlet;

import javax.portlet.PortletPreferences;

public interface PortletHeaderRequest
{    
    /**
     * Returns the web context path for the portal  
     * @return the portal context path, for example "/jetspeed"
     */
    String getPortalContextPath();
    
    /**
     * Returns the portlet applicatoin context path 
     * 
     * @return
     */
    String getPortletApplicationContextPath();
    
    /**
     * Get the portlet preferences
     */
    PortletPreferences getPreferences();
    
    /**
     * Get the init parameter by name
     */
    String getInitParameter( String name );
}
