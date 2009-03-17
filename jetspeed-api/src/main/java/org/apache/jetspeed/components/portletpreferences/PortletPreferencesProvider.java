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

import java.util.Map;

import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.pluto.container.PortletPreference;
import org.apache.pluto.container.PortletPreferencesService;


/**
 * 
 * @version $Id$
 *
 */
public interface PortletPreferencesProvider extends PortletPreferencesService
{
    public void preloadApplicationPreferences(String portletApplicationName);
    public void preloadAllEntities();    
    public void storeDefaults(PortletDefinition pd);
    public void storeDefaults(PortletApplication app);
    public void removeDefaults(PortletDefinition pd);
    public void removeDefaults(PortletApplication app);
    public Map<String, PortletPreference> getDefaultPreferences(PortletDefinition pd);
}
