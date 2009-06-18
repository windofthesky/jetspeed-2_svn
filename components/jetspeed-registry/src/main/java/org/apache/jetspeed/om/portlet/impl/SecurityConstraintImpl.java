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

package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.FilterLifecycle;
import org.apache.jetspeed.om.portlet.SecuredPortlet;
import org.apache.jetspeed.om.portlet.SecurityConstraint;
import org.apache.jetspeed.om.portlet.UserDataConstraint;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.ojb.CollectionUtils;

/**
 * @version $Id$
 *
 */
public class SecurityConstraintImpl implements SecurityConstraint, Serializable
{
    protected UserDataConstraintImpl userDataConstraints;
    protected List<SecuredPortlet> portletNames;
    protected List<DisplayName> displayNames;
    
    public DisplayName getDisplayName(Locale locale)
    {
        return (DisplayName)JetspeedLocale.getBestLocalizedObject(getDisplayNames(), locale);
    }
    
    @SuppressWarnings("unchecked")
    public List<DisplayName> getDisplayNames()
    {
        if (displayNames == null)
        {
            displayNames = CollectionUtils.createList();
        }
        return displayNames;
    }
    
    public DisplayName addDisplayName(String lang)
    {
        DisplayNameImpl d = new DisplayNameImpl(this, lang);
        for (DisplayName dn : getDisplayNames())
        {
            if (dn.getLocale().equals(d.getLocale()))
            {
                throw new IllegalArgumentException("DisplayName for language: "+d.getLocale()+" already defined");
            }
        }
        displayNames.add(d);
        return d;
    }

    @SuppressWarnings("unchecked")
    public List<String> getPortletNames()
    {
        if (portletNames == null)
        {
            portletNames = CollectionUtils.createList();
        }
        List<String> result = new ArrayList<String>();
        for (SecuredPortlet sp : portletNames)
        {
            result.add(sp.toString());
        }
        return result;
    }
    
    @SuppressWarnings("unchecked")
    public void addPortletName(String portletName)
    {
        if (portletNames == null)
        {
            portletNames = CollectionUtils.createList();
        }
        for (SecuredPortlet sp : portletNames)
        {
            if (sp.equals(portletName))
            {
                throw new IllegalArgumentException("Support for security constraint portlet name with identifier: " + portletName + " already defined");
            }
        }
        portletNames.add(new SecuredPortletImpl(portletName));        
    }

    public UserDataConstraint getUserDataConstraint()
    {
        if (userDataConstraints == null)
        {
            userDataConstraints = new UserDataConstraintImpl();
        }
        return userDataConstraints;
    }
    
}
