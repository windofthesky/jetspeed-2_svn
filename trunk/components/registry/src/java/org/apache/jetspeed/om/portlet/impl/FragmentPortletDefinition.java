/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.om.portlet.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.preference.impl.FragmentPortletPreferenceSet;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.DisplayNameSet;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.servlet.ServletDefinition;

/**
 * Per-request wrapper for a PortletDefinition that allows for
 * the supplementaton of psml-based portlet Preferences. 
 * The Preferences are transparently accessed as default Preferences in
 * the exact same way default Preferences that are provided via the portelt.xml
 * are. 
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class FragmentPortletDefinition implements PortletDefinitionComposite
{
    private final PortletDefinitionComposite portletDefinition;
    private final Fragment fragment;
    
    public FragmentPortletDefinition(PortletDefinitionComposite portletDefinition, Fragment fragment)
    {
        this.portletDefinition = portletDefinition;
        this.fragment = fragment;
    }

    public void addContentType(ContentType cType)
    {
        portletDefinition.addContentType(cType);
    }

    public void addContentType(String contentType, Collection modes)
    {
        portletDefinition.addContentType(contentType, modes);
    }

    public void addDescription(Locale locale, String description)
    {
        portletDefinition.addDescription(locale, description);
    }

    public void addDisplayName(DisplayName displayName)
    {
        portletDefinition.addDisplayName(displayName);
    }

    public void addDisplayName(Locale locale, String displayName)
    {
        portletDefinition.addDisplayName(locale, displayName);
    }

    public ParameterComposite addInitParameter(String name, String value, DescriptionSet description)
    {
        return portletDefinition.addInitParameter(name, value, description);
    }

    public ParameterComposite addInitParameter(String name, String value, String description, Locale locale)
    {
        return portletDefinition.addInitParameter(name, value, description, locale);
    }

    public ParameterComposite addInitParameter(String name, String value)
    {
        return portletDefinition.addInitParameter(name, value);
    }

    public void addLanguage(Language lang)
    {
        portletDefinition.addLanguage(lang);
    }

    public void addLanguage(String title, String shortTitle, String keywords, Locale locale)
    {
        portletDefinition.addLanguage(title, shortTitle, keywords, locale);
    }

    public void addPreference(Preference preference)
    {
        portletDefinition.addPreference(preference);
    }

    public PreferenceComposite addPreference(String name, String[] values)
    {
        return portletDefinition.addPreference(name, values);
    }

    public void addSecurityRoleRef(SecurityRoleRef securityRef)
    {
        portletDefinition.addSecurityRoleRef(securityRef);
    }

    public SecurityRoleRef addSecurityRoleRef(String roleName, String roleLink)
    {
        return portletDefinition.addSecurityRoleRef(roleName, roleLink);
    }

    public String getClassName()
    {
        return portletDefinition.getClassName();
    }

    public ContentTypeSet getContentTypeSet()
    {
        return portletDefinition.getContentTypeSet();
    }

    public Description getDescription(Locale arg0)
    {
        return portletDefinition.getDescription(arg0);
    }

    public DescriptionSet getDescriptionSet()
    {
        return portletDefinition.getDescriptionSet();
    }

    public String getDescriptionText(Locale locale)
    {
        return portletDefinition.getDescriptionText(locale);
    }

    public DisplayName getDisplayName(Locale arg0)
    {
        return portletDefinition.getDisplayName(arg0);
    }

    public DisplayNameSet getDisplayNameSet()
    {
        return portletDefinition.getDisplayNameSet();
    }

    public String getDisplayNameText(Locale locale)
    {
        return portletDefinition.getDisplayNameText(locale);
    }

    public String getExpirationCache()
    {
        return portletDefinition.getExpirationCache();
    }

    public ObjectID getId()
    {
        return portletDefinition.getId();
    }

    public ParameterSet getInitParameterSet()
    {
        return portletDefinition.getInitParameterSet();
    }

    public SecurityRoleRefSet getInitSecurityRoleRefSet()
    {
        return portletDefinition.getInitSecurityRoleRefSet();
    }

    public LanguageSet getLanguageSet()
    {
        return portletDefinition.getLanguageSet();
    }

    public GenericMetadata getMetadata()
    {
        return portletDefinition.getMetadata();
    }

    public String getName()
    {
        return portletDefinition.getName();
    }

    public PortletApplicationDefinition getPortletApplicationDefinition()
    {
        return portletDefinition.getPortletApplicationDefinition();
    }

    public ClassLoader getPortletClassLoader()
    {
        return portletDefinition.getPortletClassLoader();
    }

    public String getPortletIdentifier()
    {
        return portletDefinition.getPortletIdentifier();
    }

    public PreferenceSet getPreferenceSet()
    {
        return new FragmentPortletPreferenceSet((PreferenceSetComposite) portletDefinition.getPreferenceSet(), fragment);
    }

    public String getPreferenceValidatorClassname()
    {
        return portletDefinition.getPreferenceValidatorClassname();
    }

    public String getResourceBundle()
    {
        return portletDefinition.getResourceBundle();
    }

    public ServletDefinition getServletDefinition()
    {
        return portletDefinition.getServletDefinition();
    }

    public Collection getSupportedLocales()
    {
        return portletDefinition.getSupportedLocales();
    }

    public String getUniqueName()
    {
        return portletDefinition.getUniqueName();
    }

    public void setClassName(String arg0)
    {
        portletDefinition.setClassName(arg0);
    }

    public void setContentTypeSet(ContentTypeSet contentTypes)
    {
        portletDefinition.setContentTypeSet(contentTypes);
    }

    public void setDescriptions(DescriptionSet arg0)
    {
        portletDefinition.setDescriptions(arg0);
    }

    public void setDisplayNames(DisplayNameSet arg0)
    {
        portletDefinition.setDisplayNames(arg0);
    }

    public void setExpirationCache(String cache)
    {
        portletDefinition.setExpirationCache(cache);
    }

    public void setId(String arg0)
    {
        portletDefinition.setId(arg0);
    }

    public void setInitParameterSet(ParameterSet parameters)
    {
        portletDefinition.setInitParameterSet(parameters);
    }

    public void setInitSecurityRoleRefSet(SecurityRoleRefSet securityRefs)
    {
        portletDefinition.setInitSecurityRoleRefSet(securityRefs);
    }

    public void setLanguageSet(LanguageSet languages)
    {
        portletDefinition.setLanguageSet(languages);
    }

    public void setMetadata(GenericMetadata metadata)
    {
        portletDefinition.setMetadata(metadata);
    }

    public void setName(String arg0)
    {
        portletDefinition.setName(arg0);
    }

    public void setPortletApplicationDefinition(PortletApplicationDefinition pad)
    {
        portletDefinition.setPortletApplicationDefinition(pad);
    }

    public void setPortletClassLoader(ClassLoader arg0)
    {
        portletDefinition.setPortletClassLoader(arg0);
    }

    public void setPortletIdentifier(String portletIndentifier)
    {
        portletDefinition.setPortletIdentifier(portletIndentifier);
    }

    public void setPreferenceSet(PreferenceSet preferences)
    {
        portletDefinition.setPreferenceSet(preferences);
    }

    public void setPreferenceValidatorClassname(String classname)
    {
        portletDefinition.setPreferenceValidatorClassname(classname);
    }

    public void store() throws IOException
    {
        portletDefinition.store();
    }
}
