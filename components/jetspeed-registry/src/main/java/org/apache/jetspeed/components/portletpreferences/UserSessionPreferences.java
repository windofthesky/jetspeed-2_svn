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
package org.apache.jetspeed.components.portletpreferences;

import org.apache.pluto.container.PortletPreference;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dtaylor on 5/25/15.
 */
public class UserSessionPreferences {

    /**
     * Portlet container requests preferences by window id. Portlet windows can hold 0..n preferences.
     * This map is keyed on window id an associated to the user by putting it in the user session.
     * Individual preferences are keyed by preference name each mapping to a portlet preference
     */
    private Map<String,Map<String,PortletPreference>> windowPreferences = new ConcurrentHashMap<>();

    public Map<String,PortletPreference> getWindowPreferences(String portletWindowId) {
        return windowPreferences.get(portletWindowId);
    }

    public Map<String,PortletPreference> createWindowPreferences(String portletWindowId) {
        Map<String,PortletPreference> preferences = new HashMap<>();
        windowPreferences.put(portletWindowId, preferences);
        return preferences;
    }

    public Map<String,PortletPreference> updateWindowPreferences(String portletWindowId, Map<String,PortletPreference> preferences) {
        windowPreferences.put(portletWindowId, preferences);
        return preferences;
    }


}
