/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.components.portletpreferences;

import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.pluto.container.PortletPreference;
import org.apache.pluto.container.PortletPreferencesService;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Set;


/**
 * 
 * @version $Id$
 *
 */
public interface PortletPreferencesProvider extends PortletPreferencesService
{
    /**
     * Initializes the preferences node by executing configured preloads.
     * @throws Exception
     */
    void preload() throws Exception;
    
    /**
     * Preload all preferences for the given portlet application name into the preferences cache
     * @param portletApplicationName
     */
    public void preloadApplicationPreferences(String portletApplicationName);
    
    /**
     * Preload all user preferences for all users into the preferences cache. Be careful 
     * with this method as it can chew up a lot of memory. Ensure the cache is configured
     * to be large enough to hold all preferences
     */
    public void preloadUserPreferences();
    
    /**
     * Store the default preferences by descriptor preferences for a given portlet definition
     * @param pd
     */
    public void storeDefaults(PortletDefinition pd, Preferences prefs);
    
    /**
     * Store the default preferences by input preference for a given portlet definition
     * @param pd
     */
    public void storeDefaults(PortletDefinition pd, Preference preference);
    
    /**
     * Store the default preferences for all portlets in the given portlet application
     * @param app
     */
    public void storeDefaults(PortletApplication app);
    
    /**
     * Remove all default preferences for a given portlet definition
     * @param pd
     */
    public void removeDefaults(PortletDefinition pd);
    
    /**
     * Remove default preferences by preference name for a given portlet definition
     * @param pd
     */
    public void removeDefaults(PortletDefinition pd, String preferenceName);
    
    /**
     * Remove all default preferences for all portlet definitions in a given portlet application
     * @param app
     */
    public void removeDefaults(PortletApplication app);
    
    /**
     * Retrieve the default preferences for a given portlet definition
     * @param pd
     * @return the default preferences map for a given portlet definition
     */
    public Map<String, PortletPreference> retrieveDefaultPreferences(PortletDefinition pd);

    /**
     * Retrieve the user preferences for the window and user parameters
     * 
     * @param window
     * @param userName
     * @return
     */
    public Map<String, PortletPreference> retrieveUserPreferences(PortletWindow window, String userName);

    /**
     * Retrieve entity (window) default preferences for a given window 
     * 
     * @param window
     * @return
     */
    public Map<String, PortletPreference> retrieveEntityPreferences(PortletWindow window);

    /**
     * Store user preferences contained in the map parameter for a given user and window
     *  
     * @param map
     * @param window
     * @param userName
     * @throws PreferencesException 
     */
    public void storeUserPreferences(Map<String, PortletPreference> map, PortletWindow window, String userName)
    throws PreferencesException;

    /**
     * Store entity preferences contained in the map parameter for a given window
     * 
     * @param map
     * @param window
     * @throws PreferencesException
     */
    public void storeEntityPreferences(Map<String, PortletPreference> map, PortletWindow window)
        throws PreferencesException;
    
    /**
     * Retrieve entity ids (window) given portlet definition
     * 
     * @param portletdefinition
     */
    public Set<String> getPortletWindowIds(PortletDefinition portletdefinition);

    /**
     * Retrieve user names for given entity(window) id and portlet definition
     * @param portletdefinition
     * @param windowId
     */    
    public Set<String> getUserNames(PortletDefinition portletdefinition, String windowId);
    
    
    /**
     * Retrieve entity preference names for given entity(window) id ,portlet definition and user name
     * @param portletdefinition
     * @param windowId
     */
    public Map<String,PortletPreference> getUserPreferences(PortletDefinition portletdefinition, String windowId, String userName);
    
    /**
     * Store portlet definition entity preferences contained in the map parameter
     * for a given window and username 
     * @param portletdefinition
     * @param windowId
     * @param userName
     * @param map
     */
    public void storePortletPreference(PortletDefinition portletdefinition, String windowId, String userName, Map<String,PortletPreference> map);

    /**
     * Store portlet entity preferences contained in the map parameter for a given
     * window and username 
     * @param appName
     * @param portletName
     * @param windowId
     * @param userName
     * @param map
     */
    public void storePortletPreference(String appName, String portletName, String windowId, String userName, Map<String,PortletPreference> map);

    /**
     * Remove all user preferences for a given username
     *
     * @since 2.3.0
     * @param user the name of the user to delete all preferences for
     */
    public void removeUserPreferences(String user);

    public void sessionCreatedEvent(HttpSession session);
    public void sessionDestroyedEvent(HttpSession session);

}
