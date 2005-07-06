/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetspeed.util.descriptor;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RuleSetBase;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class PortletPreferenceRuleSet extends RuleSetBase
{

  

    /**
     * <p>
     * addRuleInstances
     * </p>
     *
     * @see org.apache.commons.digester.RuleSet#addRuleInstances(org.apache.commons.digester.Digester)
     * @param arg0
     */
    public void addRuleInstances( Digester digester )
    {
       digester.addRule("portlet-app/portlet/portlet-preferences/preference", new PortletPreferenceRule());
       digester.addBeanPropertySetter("portlet-app/portlet/portlet-preferences/preference/name", "name");
       digester.addCallMethod("portlet-app/portlet/portlet-preferences/preference/value", "addValue", 0);
       digester.addCallMethod(
           "portlet-app/portlet/portlet-preferences/preference/read-only",
           "setReadOnly",
           0,
           new Class[] { Boolean.class });

    }

}
