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

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.namespace.QName;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.EventDefinitionReference;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.PortletInfo;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.portlet.SecurityRoleRef;
import org.apache.jetspeed.om.portlet.Supports;

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
public class FragmentPortletDefinition implements PortletDefinition
{
    private final PortletDefinition portletDefinition;
    private final Fragment fragment;
    
    public FragmentPortletDefinition(PortletDefinition portletDefinition, Fragment fragment)
    {
        this.portletDefinition = portletDefinition;
        this.fragment = fragment;
    }

    public ContainerRuntimeOption addContainerRuntimeOption(String name)
    {
        return portletDefinition.addContainerRuntimeOption(name);
    }

    public Description addDescription(String lang)
    {
        return portletDefinition.addDescription(lang);
    }

    public DisplayName addDisplayName(String lang)
    {
        return portletDefinition.addDisplayName(lang);
    }

    public InitParam addInitParam(String paramName)
    {
        return portletDefinition.addInitParam(paramName);
    }

    public Language addLanguage(Locale locale)
    {
        return portletDefinition.addLanguage(locale);
    }

    public SecurityRoleRef addSecurityRoleRef(String roleName)
    {
        return portletDefinition.addSecurityRoleRef(roleName);
    }

    public void addSupportedLocale(String lang)
    {
        portletDefinition.addSupportedLocale(lang);
    }

    public EventDefinitionReference addSupportedProcessingEvent(QName qname)
    {
        return portletDefinition.addSupportedProcessingEvent(qname);
    }

    public EventDefinitionReference addSupportedProcessingEvent(String name)
    {
        return portletDefinition.addSupportedProcessingEvent(name);
    }

    public void addSupportedPublicRenderParameter(String identifier)
    {
        portletDefinition.addSupportedPublicRenderParameter(identifier);
    }

    public EventDefinitionReference addSupportedPublishingEvent(QName qname)
    {
        return portletDefinition.addSupportedPublishingEvent(qname);
    }

    public EventDefinitionReference addSupportedPublishingEvent(String name)
    {
        return portletDefinition.addSupportedPublishingEvent(name);
    }

    public Supports addSupports(String mimeType)
    {
        return portletDefinition.addSupports(mimeType);
    }

    public PortletApplication getApplication()
    {
        return portletDefinition.getApplication();
    }

    public String getCacheScope()
    {
        return portletDefinition.getCacheScope();
    }

    public ContainerRuntimeOption getContainerRuntimeOption(String name)
    {
        return portletDefinition.getContainerRuntimeOption(name);
    }

    public List<ContainerRuntimeOption> getContainerRuntimeOptions()
    {
        return portletDefinition.getContainerRuntimeOptions();
    }

    public Description getDescription(Locale locale)
    {
        return portletDefinition.getDescription(locale);
    }

    public List<Description> getDescriptions()
    {
        return portletDefinition.getDescriptions();
    }

    public String getDescriptionText(Locale locale)
    {
        return portletDefinition.getDescriptionText(locale);
    }

    public DisplayName getDisplayName(Locale locale)
    {
        return portletDefinition.getDisplayName(locale);
    }

    public List<DisplayName> getDisplayNames()
    {
        return portletDefinition.getDisplayNames();
    }

    public String getDisplayNameText(Locale locale)
    {
        return portletDefinition.getDisplayNameText(locale);
    }

    public int getExpirationCache()
    {
        return portletDefinition.getExpirationCache();
    }

    public InitParam getInitParam(String paramName)
    {
        return portletDefinition.getInitParam(paramName);
    }

    public List<InitParam> getInitParams()
    {
        return portletDefinition.getInitParams();
    }

    public String getJetspeedSecurityConstraint()
    {
        return portletDefinition.getJetspeedSecurityConstraint();
    }

    public Language getLanguage(Locale locale)
    {
        return portletDefinition.getLanguage(locale);
    }

    public List<Language> getLanguages()
    {
        return portletDefinition.getLanguages();
    }

    public GenericMetadata getMetadata()
    {
        return portletDefinition.getMetadata();
    }

    public String getPortletClass()
    {
        return portletDefinition.getPortletClass();
    }

    public PortletInfo getPortletInfo()
    {
        return portletDefinition.getPortletInfo();
    }

    public String getPortletName()
    {
        return portletDefinition.getPortletName();
    }

    public Preferences getPortletPreferences()
    {
//        return new FragmentPortletPreferenceSet((PreferenceSetComposite) portletDefinition.getPreferenceSet(), fragment);
    }

    public String getPreferenceValidatorClassname()
    {
        return portletDefinition.getPreferenceValidatorClassname();
    }

    public String getResourceBundle()
    {
        return portletDefinition.getResourceBundle();
    }

    public ResourceBundle getResourceBundle(Locale locale)
    {
        return portletDefinition.getResourceBundle(locale);
    }

    public SecurityRoleRef getSecurityRoleRef(String roleName)
    {
        return portletDefinition.getSecurityRoleRef(roleName);
    }

    public List<SecurityRoleRef> getSecurityRoleRefs()
    {
        return portletDefinition.getSecurityRoleRefs();
    }

    public List<String> getSupportedLocales()
    {
        return portletDefinition.getSupportedLocales();
    }

    public List<EventDefinitionReference> getSupportedProcessingEvents()
    {
        return portletDefinition.getSupportedProcessingEvents();
    }

    public List<String> getSupportedPublicRenderParameters()
    {
        return portletDefinition.getSupportedPublicRenderParameters();
    }

    public List<EventDefinitionReference> getSupportedPublishingEvents()
    {
        return portletDefinition.getSupportedPublishingEvents();
    }

    public List<Supports> getSupports()
    {
        return portletDefinition.getSupports();
    }

    public Supports getSupports(String mimeType)
    {
        return portletDefinition.getSupports(mimeType);
    }

    public String getUniqueName()
    {
        return portletDefinition.getUniqueName();
    }

    public boolean isSameIdentity(PortletDefinition other)
    {
        return portletDefinition.isSameIdentity(other);
    }

    public void setCacheScope(String cacheScope)
    {
        portletDefinition.setCacheScope(cacheScope);
    }

    public void setExpirationCache(int expirationCache)
    {
        portletDefinition.setExpirationCache(expirationCache);
    }

    public void setJetspeedSecurityConstraint(String constraint)
    {
        portletDefinition.setJetspeedSecurityConstraint(constraint);
    }

    public void setPortletClass(String portletClass)
    {
        portletDefinition.setPortletClass(portletClass);
    }

    public void setPreferenceValidatorClassname(String classname)
    {
        portletDefinition.setPreferenceValidatorClassname(classname);
    }

    public void setResourceBundle(String resourceBundle)
    {
        portletDefinition.setResourceBundle(resourceBundle);
    }

    public void storeChildren()
    {
        portletDefinition.storeChildren();
    }

    
}
