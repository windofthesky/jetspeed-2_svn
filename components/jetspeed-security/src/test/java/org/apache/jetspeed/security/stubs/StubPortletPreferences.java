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
package org.apache.jetspeed.security.stubs;

import org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider;
import org.apache.jetspeed.components.portletpreferences.PreferencesException;
import org.apache.jetspeed.container.PortletWindow;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.pluto.container.PortletContainerException;
import org.apache.pluto.container.PortletPreference;

import javax.portlet.PortletRequest;
import javax.portlet.PreferencesValidator;
import javax.portlet.ValidatorException;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Set;

public class StubPortletPreferences implements PortletPreferencesProvider {
    @Override
    public void preload() throws Exception {

    }

    @Override
    public void preloadApplicationPreferences(String portletApplicationName) {

    }

    @Override
    public void preloadUserPreferences() {

    }

    @Override
    public void storeDefaults(PortletDefinition pd, Preferences prefs) {

    }

    @Override
    public void storeDefaults(PortletDefinition pd, Preference preference) {

    }

    @Override
    public void storeDefaults(PortletApplication app) {

    }

    @Override
    public void removeDefaults(PortletDefinition pd) {

    }

    @Override
    public void removeDefaults(PortletDefinition pd, String preferenceName) {

    }

    @Override
    public void removeDefaults(PortletApplication app) {

    }

    @Override
    public Map<String, PortletPreference> retrieveDefaultPreferences(PortletDefinition pd) {
        return null;
    }

    @Override
    public Map<String, PortletPreference> retrieveUserPreferences(PortletWindow window, String userName) {
        return null;
    }

    @Override
    public Map<String, PortletPreference> retrieveEntityPreferences(PortletWindow window) {
        return null;
    }

    @Override
    public void storeUserPreferences(Map<String, PortletPreference> map, PortletWindow window, String userName) throws PreferencesException {

    }

    @Override
    public void storeEntityPreferences(Map<String, PortletPreference> map, PortletWindow window) throws PreferencesException {

    }

    @Override
    public Set<String> getPortletWindowIds(PortletDefinition portletdefinition) {
        return null;
    }

    @Override
    public Set<String> getUserNames(PortletDefinition portletdefinition, String windowId) {
        return null;
    }

    @Override
    public Map<String, PortletPreference> getUserPreferences(PortletDefinition portletdefinition, String windowId, String userName) {
        return null;
    }

    @Override
    public void storePortletPreference(PortletDefinition portletdefinition, String windowId, String userName, Map<String, PortletPreference> map) {

    }

    @Override
    public void storePortletPreference(String appName, String portletName, String windowId, String userName, Map<String, PortletPreference> map) {

    }

    @Override
    public void removeUserPreferences(String user) {

    }

    @Override
    public Map<String, PortletPreference> getDefaultPreferences(org.apache.pluto.container.PortletWindow portletWindow, PortletRequest portletRequest) throws PortletContainerException {
        return null;
    }

    @Override
    public Map<String, PortletPreference> getStoredPreferences(org.apache.pluto.container.PortletWindow portletWindow, PortletRequest portletRequest) throws PortletContainerException {
        return null;
    }

    @Override
    public void store(org.apache.pluto.container.PortletWindow portletWindow, PortletRequest portletRequest, Map<String, PortletPreference> map) throws PortletContainerException {

    }

    @Override
    public PreferencesValidator getPreferencesValidator(org.apache.pluto.container.om.portlet.PortletDefinition portletDefinition) throws ValidatorException {
        return null;
    }

    @Override
    public void sessionDestroyedEvent(HttpSession session) {

    }

    @Override
    public void sessionCreatedEvent(HttpSession session) {

    }
}
