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
package org.apache.jetspeed.om.common.portlet;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionCtrl;

/**
 * 
 * PortletDefinitionComposite
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PortletDefinitionComposite extends PortletDefinition, PortletDefinitionCtrl, Serializable
{
    GenericMetadata getMetadata();
    
    void setMetadata(GenericMetadata metadata);
    
    void addLanguage(Language lang);

    void addContentType(ContentType cType);

    void setLanguageSet(LanguageSet languages);

    /**
     * The PreferenceSet is a collection user-defineable preferences
     * that this portlet can use to process its logic.
     * 
     * @param preferences
     */
    void setPreferenceSet(PreferenceSet preferences);

    void setInitParameterSet(ParameterSet parameters);

    void setContentTypeSet(ContentTypeSet contentTypes);

    void setInitSecurityRoleRefSet(SecurityRoleRefSet securityRefs);
    
    /**
     * Convenience method for directly adding init parameters
     * to this <code>PortletDefinition.</code>.  This has the
     * same affect as 
     * <code>((ParameterSetCtrl)PortletDefinition.getInitParamaterSet()).add()</code>
     * @param name Name of parameter to set
     * @param value new value of said parameter
     * @return ParameterComposite newly created parameter
     */
    ParameterComposite addInitParameter(String name, String value);

    /**
     * Same as <code>setInitParameter(name, title) plus allows a
     * description to inlcuded.
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#addInitParameter(java.lang.String, java.lang.String)
     * @param name Name of parameter to set
     * @param value new value of the parameter
     * @param DescriptionSet containing locale-specific descriptions of the parameter
     * @return ParameterComposite newly created parameter
     */
    ParameterComposite addInitParameter(String name, String value, DescriptionSet description);

    /**
     * Same as <code>setInitParameter(name, title) plus allows you 
     * to define one initial localized desription.
     * 
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#addInitParameter(java.lang.String, java.lang.String)
     * @param name Name of parameter to set
     * @param value new value of the parameter
     * @param description A description for this parameter
     * @param locale The locale the description
     * @return ParameterComposite newly created parameter
     */
    ParameterComposite addInitParameter(String name, String value, String description, Locale locale);

    /**
     * Setter for setting expiration cache time for this portlet     
     */
    void setExpirationCache(String cache);

    void setPortletApplicationDefinition(PortletApplicationDefinition pad);

    PreferenceComposite addPreference(String name, String[] values);

    void addPreference(Preference preference);

    void setPortletIdentifier(String portletIndentifier);

    String getPortletIdentifier();

    /**
     * A portlet's unique name is a string formed by the combination of a portlet's
     * unique within it's parent application plus the parent application's
     * unique name within the portlet container using ":" as a delimiter. 
     * <br/>
     * <strong>FORMAT: </strong> <i>application name</i>:<i>portlet name</i>
     * <br/>
     * <strong>EXAMPLE: </strong> com.myapp.portletApp1:weather-portlet
     * 
     
     * @return Name that uniquely indetifies this portlet within the container.  If
     * either the name of the portlet is <code>null</code> or this portlet has not
     * yet been assigned to an portlet application, <code>null</code> is returned.
     */
    String getUniqueName();

    /**
     * Returns localized text of this PortletDefinitions display name.
     * 
     * @param locale Locale to get the display name for
     * @return Localized text string of the display name or <code>null</code>
     * if no DisplayName exists for this locale
     */
    String getDisplayNameText(Locale locale);

    /**
     * Returns localized text of this PortletDefinitions description.
     * 
     * @param locale Locale to get the description for
     * @return Localized text string of the display name or <code>null</code>
     * if no Description exists for this locale
     */
    String getDescriptionText(Locale locale);

    void addDescription(Locale locale, String description);

    void addDisplayName(Locale locale, String displayName);

    /**
     * 
     * <p>
     * addDisplayName
     * </p>
     * 
     * @param displayName
     *
     */
    void addDisplayName(DisplayName displayName);

    String getPreferenceValidatorClassname();

    void setPreferenceValidatorClassname(String classname);

    /**
     * 
     * <p>
     * addSecurityRoleRef
     * </p>
     * 
     * Adds the <code>securityRef</code> to the existing
     * set of SecurityRoleRefs of this PortletDefinition
     * 
     * @param securityRef SecurityRoleRef to add.
     *
     */
    void addSecurityRoleRef(SecurityRoleRef securityRef);

}
