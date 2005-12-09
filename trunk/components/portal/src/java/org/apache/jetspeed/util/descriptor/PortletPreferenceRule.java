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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.digester.Rule;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.preference.impl.PrefsPreference;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.xml.sax.Attributes;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class PortletPreferenceRule extends Rule
{
    

    protected PortletDefinitionComposite portlet;
    
    protected String name;
    protected boolean readOnly;
    protected List values; 
 
    /**
     * <p>
     * begin
     * </p>
     *
     * @see org.apache.commons.digester.Rule#begin(java.lang.String, java.lang.String, org.xml.sax.Attributes)
     * @param arg0
     * @param arg1
     * @param arg2
     * @throws java.lang.Exception
     */
    public void begin( String arg0, String arg1, Attributes arg2 ) throws Exception
    {
        Object peeked = digester.peek();
        portlet = (PortletDefinitionComposite) peeked;
        portlet.setPortletApplicationDefinition((PortletApplicationDefinition) digester.getRoot());
        
        // reset properties to default values
        // as the same instance of this rule can be used multiple times
        values = new ArrayList();        
        readOnly = false;
        
        TempValueObject temp = new TempValueObject();
        digester.push(temp);
    }
    /**
     * <p>
     * end
     * </p>
     *
     * @see org.apache.commons.digester.Rule#end(java.lang.String, java.lang.String)
     * @param arg0
     * @param arg1
     * @throws java.lang.Exception
     */
    public void end( String arg0, String arg1 ) throws Exception
    {       
        PrefsPreference pref = new PrefsPreference(portlet, name);
        pref.setValues(values);
        pref.setReadOnly(readOnly);
        digester.pop();
    }
    
    public class TempValueObject
    {
        public void setName(String name)
        {
            PortletPreferenceRule.this.name = name;
        }
        
        public void setReadOnly(boolean readOnly)
        {
            PortletPreferenceRule.this.readOnly = readOnly;
        }
        
        public void addValue(String value)
        {
            PortletPreferenceRule.this.values.add(value);
        }
    }
  
    /**
     * <p>
     * finish
     * </p>
     *
     * @see org.apache.commons.digester.Rule#finish()
     * @throws java.lang.Exception
     */
    public void finish() throws Exception
    {
        if(values != null)
        {
            values.clear();
        }
        super.finish();
    }
}
