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

package org.apache.jetspeed.om.common.preference;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Locale;

import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceCtrl;

/**
 * 
 * PreferenceComposite
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PreferenceComposite extends PreferenceCtrl, Preference, Serializable
{
	String DEFAULT_PREFERENCE = "org.apache.pluto.om.common.Preference.default";
	String USER_PREFERENCE = "org.apache.pluto.om.common.Preference.default.user";
	
    void addDescription(Locale locale, String Description);

    Description getDescription(Locale locale);    

    /**
     * @throws java.lang.ArrayIndexOutofBounds if index is outside the constraints
     * @param index
     * @return The String value at the specified index or <code>null</code>
     * if no values are present.
     */
    String getValueAt(int index);

    /**
     * 
     * <p>
     * setValueAt
     * </p>
     * Sets the current Preference's value at <code>index</code>
     * to the specified <code>value</code> 
     * 
     * @param index Index hows value will be set.
     * @param value Value to set
     *
     */
    void setValueAt(int index, String value);

    /**
     * 
     * <p>
     * addValue
     * </p>
     * Adds a new value to this Preference.
     * @param value Vale to add to the preference
     *
     */
    void addValue(String value);

    /**
     * 
     * @return
     */
    String[] getValueArray();

    /**
     * 
     * <p>
     * setValues
     * </p>
     * 
     * Replaces the current set of values of this preference
     * with this one. 
     * 
     * @param stringValues 
     *
     */
    void setValues(String[] stringValues);

    /**
     * @return
     */
    String getType();
    /**
     * @param string
     */
    void setType(String string);
    
    String[] cloneValues();
    
    Iterator getDescriptions();

}
