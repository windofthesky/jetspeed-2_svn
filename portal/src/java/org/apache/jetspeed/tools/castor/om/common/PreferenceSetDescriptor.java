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
package org.apache.jetspeed.tools.castor.om.common;

import java.util.Collection;

import org.apache.jetspeed.om.preference.impl.PreferenceSetImpl;

/**
 * Used to help Castor in mapping XML preferences to Java objects 
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PreferenceSetDescriptor extends PreferenceSetImpl
{
    private String preferencesValidator;
    
    public Collection getCastorPreferences()
    {
        return this.getInnerCollection();
    }
        
    public String getPreferenceValidatorClassname()
    {
        return preferencesValidator;
    }

    public void SetPreferenceValidatorClassname(String preferencesValidator)
    {
        this.preferencesValidator = preferencesValidator;
    }
    
    
}
