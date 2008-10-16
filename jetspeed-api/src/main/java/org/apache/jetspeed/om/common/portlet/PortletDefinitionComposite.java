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
package org.apache.jetspeed.om.common.portlet;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.pluto.om.portlet.DescriptionSet;
import org.apache.pluto.om.portlet.Language;
import org.apache.pluto.om.portlet.LanguageSet;
import org.apache.pluto.om.portlet.ParameterSet;
import org.apache.pluto.om.portlet.Preference;
import org.apache.pluto.om.portlet.PreferenceSet;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.portlet.DisplayName;
import org.apache.pluto.om.portlet.DisplayNameSet;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.SecurityRoleRef;
import org.apache.pluto.om.portlet.SecurityRoleRefSet;

/**
 * 
 * PortletDefinitionComposite
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PortletDefinitionComposite extends PortletDefinition, Serializable
{
    GenericMetadata getMetadata();
    
    void setMetadata(GenericMetadata metadata);
    
    void addLanguage(Language lang);
    
    void addLanguage(String title, String shortTitle, String keywords, Locale locale);

    void addContentType(ContentType cType);
    
    void addContentType(String contentType, Collection modes);

    void setLanguageSet(LanguageSet languages);
    
    String getResourceBundle();
    
    Collection getSupportedLocales();

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

    /**
     * @obsolete use #getPreferenceSet.add(String, String[])
     */
    PreferenceComposite addPreference(String name, String[] values);

    /**
     * @obsolete use #getPreferenceSet.add(String, String[])
     */
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
    
    DescriptionSet getDescriptionSet();

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
    
    DisplayNameSet getDisplayNameSet();

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
    
    SecurityRoleRef addSecurityRoleRef(String roleName, String roleLink);

    /**
     * <p>
     * Get the Jetspeed Security Constraint reference for this portlet.
     * This security constraint name references a Jetspeed-specific Security Constraint.
     * Security Constraints are not Java Security Permissions, but a 
     * Jetspeed specific way of securing portlets, also known as PSML constraints.
     * See the <i>page.security</i> file for examples of defining security constraint definitions.
     * If a Jetspeed Security Constraint is not defined for a portlet, the constraint 
     * applied will then fallback to the constraint defined for the portlet application.
     * If the portlet application does not define a constraint, then no security constraints
     * will be applied to this portlet. Security constraints for a portlet are normally
     * checking during the render process of a portlet.
     * </p>
     * 
     * @return The name of the Security Definition applied to this portlet, defined in 
     *                  the Jetspeed Security Constraints 
     */    
    String getJetspeedSecurityConstraint();
    
    /**
     * <p>
     * Set the Jetspeed Security Constraint reference for this portlet.
     * This security constraint name references a Jetspeed-specific Security Constraint.
     * Security Constraints are not Java Security Permissions, but a 
     * Jetspeed specific way of securing portlets, also known as PSML constraints.
     * See the <i>page.security</i> file for examples of defining security constraint definitions.
     * If the portlet application does not define a constraint, then no security constraints
     * will be applied to this portlet. Security constraints for a portlet are normally
     * checking during the render process of a portlet.
     * </p>
     * 
     * @param constraint The name of the Security Definition defined in 
     *                  the Jetspeed Security Constraints 
     */
    void setJetspeedSecurityConstraint(String constraint);
    
    /**
     * <p>
     * Persistence callback to allow a PortletDefinition instance to persist children
     * objects (like portlet preferences) <em>within</em> the same transaction.
     * </p>
     * <p>
     * This method must be called <em>always</em> from the #store() method. Using a callback from
     * the persistence manager might not be reliable when the PortletDefinition <em>itself</em>
     * isn't changed but children might.
     * </p>
     * <p>
     * Notably condition when this might happen is the Pluto 1.0.1 preferences handling calling
     * the portletDefinition store() method
     * */
    void storeChildren();
}
