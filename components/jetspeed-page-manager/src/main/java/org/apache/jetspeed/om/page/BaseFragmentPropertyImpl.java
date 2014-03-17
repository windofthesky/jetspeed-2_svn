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
package org.apache.jetspeed.om.page;

import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.SubjectHelper;
import org.apache.jetspeed.security.User;

import javax.security.auth.Subject;
import java.security.AccessController;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * BaseFragmentPropertyImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class BaseFragmentPropertyImpl implements FragmentProperty
{
    /**
     * Lookup fragment property in list based on current user.
     * 
     * @param propName property name
     * @param properties fragment properties list
     * @param userValue returned user value
     * @param groupValue returned group value
     * @param roleValue returned role value
     * @param globalValue returned global value
     * @return value found flag
     */
    public static boolean getFragmentProperty(String propName, List<FragmentProperty> properties, String [] userValue, String [] groupValue, String [] roleValue, String [] globalValue)
    {
        boolean valueFound = false;
        
        // iterate through properties list and merge with current
        // principals to determine most specific property value
        Set<Principal> principals = null;
        Principal userPrincipal = null;
        boolean skipPropertyScopes = false;
        Iterator<FragmentProperty> propertiesIter = properties.iterator();
        while ((userValue[0] == null) && propertiesIter.hasNext())
        {
            FragmentProperty fragmentProperty = propertiesIter.next();
            if (fragmentProperty.getName().equals(propName))
            {
                String fragmentPropertyScope = fragmentProperty.getScope();
                if (fragmentPropertyScope != null)
                {
                    if (!skipPropertyScopes)
                    {
                        // get principals
                        if (principals == null)
                        {
                            // get current request context subject for principals
                            Subject subject = JSSubject.getSubject(AccessController.getContext());
                            if (subject != null)
                            {
                                if (GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
                                {
                                    principals = subject.getPrincipals();
                                }
                                else
                                {
                                    userPrincipal = SubjectHelper.getBestPrincipal(subject, User.class);
                                }
                            }
                            else
                            {
                                skipPropertyScopes = true;
                            }
                        }
                        String fragmentPropertyScopeValue = fragmentProperty.getScopeValue();
                        if (userPrincipal != null)
                        {
                            // match user property scope and scope value with user principal
                            if (fragmentPropertyScope.equals(USER_PROPERTY_SCOPE) && userPrincipal.getName().equals(fragmentPropertyScopeValue))
                            {
                                userValue[0] = fragmentProperty.getValue();
                                valueFound = true;
                            }
                        }
                        else if (principals != null)
                        {
                            // match property scope and scope value with most specific
                            // principal without a value
                            Iterator<Principal> principalsIter = principals.iterator();
                            while ((userValue[0] == null) && principalsIter.hasNext())
                            {
                                Principal principal = principalsIter.next();
                                if (principal.getName().equals(fragmentPropertyScopeValue))
                                {
                                    if (fragmentPropertyScope.equals(USER_PROPERTY_SCOPE) && (principal instanceof User))
                                    {
                                        userValue[0] = fragmentProperty.getValue();
                                        valueFound = true;
                                    }
                                    else if (groupValue[0] == null)
                                    {
                                        if (fragmentPropertyScope.equals(GROUP_PROPERTY_SCOPE) && (principal instanceof Group))
                                        {
                                            groupValue[0] = fragmentProperty.getValue();
                                            valueFound = true;
                                        }
                                        else if (roleValue[0] == null)
                                        {
                                            if (fragmentPropertyScope.equals(ROLE_PROPERTY_SCOPE) && (principal instanceof Role))
                                            {
                                                roleValue[0] = fragmentProperty.getValue();
                                                valueFound = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else if ((groupValue[0] == null) && (roleValue[0] == null) && (globalValue[0] == null))
                {
                    globalValue[0] = fragmentProperty.getValue();
                    valueFound = true;
                }
            }
        }
        
        return valueFound;
    }

    /**
     * Filter fragment properties in list based on current user.
     * 
     * @param properties unfiltered fragment properties list
     * @return filtered fragment properties
     */
    public static List<FragmentProperty> filterFragmentProperties(List<FragmentProperty> properties)
    {
        List<FragmentProperty> filteredProperties = new ArrayList<FragmentProperty>();
        
        // iterate through properties list and merge with current
        // principals to determine most specific property value
        Set<Principal> principals = null;
        Principal userPrincipal = null;
        boolean skipPropertyScopes = false;
        for (FragmentProperty fragmentProperty : properties)
        {
            String fragmentPropertyScope = fragmentProperty.getScope();
            if (fragmentPropertyScope != null)
            {
                if (!skipPropertyScopes)
                {
                    // get principals
                    if (principals == null)
                    {
                        // get current request context subject for principals
                        Subject subject = JSSubject.getSubject(AccessController.getContext());
                        if (subject != null)
                        {
                            if (GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
                            {
                                principals = subject.getPrincipals();
                            }
                            else
                            {
                                userPrincipal = SubjectHelper.getBestPrincipal(subject, User.class);
                            }
                        }
                        else
                        {
                            skipPropertyScopes = true;
                        }
                    }
                    String fragmentPropertyScopeValue = fragmentProperty.getScopeValue();
                    if (userPrincipal != null)
                    {
                        // match user property scope and scope value with user principal
                        if (fragmentPropertyScope.equals(USER_PROPERTY_SCOPE) && userPrincipal.getName().equals(fragmentPropertyScopeValue))
                        {
                            filteredProperties.add(fragmentProperty);
                        }
                    }
                    else if (principals != null)
                    {
                        // match property scope and scope value with most specific
                        // principal without a value
                        for (Principal principal : principals)
                        {
                            if (principal.getName().equals(fragmentPropertyScopeValue) &&
                                ((fragmentPropertyScope.equals(USER_PROPERTY_SCOPE) && (principal instanceof User)) ||
                                 (fragmentPropertyScope.equals(GROUP_PROPERTY_SCOPE) && (principal instanceof Group)) ||
                                 (fragmentPropertyScope.equals(ROLE_PROPERTY_SCOPE) && (principal instanceof Role))))
                            {
                                filteredProperties.add(fragmentProperty);
                            }
                        }
                    }
                }
            }
            else
            {
                filteredProperties.add(fragmentProperty);
            }
        }
        
        return filteredProperties;
    }

    /**
     * Find fragment property by name, scope, and scope value.
     * 
     * @param propName property name
     * @param propScope property scope
     * @param propScopeValue property scope value
     * @param properties fragment properties list
     * @return fragment property
     */
    public static FragmentProperty findFragmentProperty(String propName, String propScope, String propScopeValue, List<FragmentProperty> properties)
    {
        // iterate through properties to find specified scoped property
        FragmentProperty fragmentProperty = null;
        for (FragmentProperty findFragmentProperty : properties)
        {
            if (findFragmentProperty.getName().equals(propName))
            {
                String findFragmentPropertyScope = findFragmentProperty.getScope();
                if ((propScope == null) && (findFragmentPropertyScope == null))
                {
                    return findFragmentProperty;
                }
                else if ((findFragmentPropertyScope != null) && findFragmentPropertyScope.equals(propScope))
                {
                    String findFragmentPropertyScopeValue = findFragmentProperty.getScopeValue();
                    if ((findFragmentPropertyScopeValue != null) && findFragmentPropertyScopeValue.equals(propScopeValue))
                    {
                        return findFragmentProperty;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * Lookup current user scope value used to default new fragment properties.
     * 
     * @return current user principal name
     */
    public static String getCurrentUserScopeValue()
    {
        // lookup current user principal using subject
        Subject subject = JSSubject.getSubject(AccessController.getContext());
        if (subject != null)
        {
            Principal userPrincipal = SubjectHelper.getBestPrincipal(subject, User.class);
            if (userPrincipal != null)
            {
                return userPrincipal.getName();
            }
        }
        return null;
    }
}
