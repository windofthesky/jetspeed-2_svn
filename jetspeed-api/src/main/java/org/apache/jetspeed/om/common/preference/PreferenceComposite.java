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

package org.apache.jetspeed.om.common.preference;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceCtrl;

/**
 * 
 * PreferenceComposite
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: PreferenceComposite.java,v 1.2 2004/06/18 20:46:21 weaver Exp $
 *
 */
public interface PreferenceComposite extends PreferenceCtrl, Preference, Serializable
{
	String DEFAULT_PREFERENCE = "org.apache.pluto.om.common.Preference.default";
	String USER_PREFERENCE = "org.apache.pluto.om.common.Preference.default.user";
	
    void addDescription(Locale locale, String Description);

    Description getDescription(Locale locale);    

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
    List<String> getValuesList();

    Iterator getDescriptions();

}
