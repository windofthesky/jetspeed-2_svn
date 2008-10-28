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

import java.util.Collection;

import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;

public interface PortletPreferencesProvider
{
    void init() throws Exception;
    public PreferenceSetComposite getPreferenceSet(PortletDefinitionComposite pd);
    public PreferenceSetComposite getPreferenceSet(MutablePortletEntity pe);
    public PreferenceSetComposite getPreferenceSet(MutablePortletEntity pe, String userName);
    public Collection<String> getUserNames(MutablePortletEntity pe);
    public void savePreferenceSet(PortletDefinitionComposite pd, PreferenceSetComposite preferenceSet);
    public void savePreferenceSet(MutablePortletEntity pe, PreferenceSetComposite preferenceSet);
    public void savePreferenceSet(MutablePortletEntity pe, String userName, PreferenceSetComposite preferenceSet);        
}