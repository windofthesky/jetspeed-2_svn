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
package org.apache.jetspeed.om.portlet;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.xml.namespace.QName;

/**
 * 
 * PortletDefinitionComposite
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PortletDefinition extends org.apache.pluto.container.om.portlet.PortletDefinition, Serializable
{
    final String CLONE_PARENT_INIT_PARAM = "cloneParentPortlet";
    PortletApplication getApplication();
    InitParam getInitParam(String paramName);
    List<InitParam> getInitParams();
    InitParam addInitParam(String paramName);
    
    PortletInfo getPortletInfo();

    Preferences getPortletPreferences();
    Preferences getDescriptorPreferences();
    Preference addDescriptorPreference(String name);
    List<EventDefinitionReference> getSupportedProcessingEvents();
    EventDefinitionReference addSupportedProcessingEvent(QName qname);
    EventDefinitionReference addSupportedProcessingEvent(String name);
    
    List<EventDefinitionReference> getSupportedPublishingEvents();
    EventDefinitionReference addSupportedPublishingEvent(QName qname);
    EventDefinitionReference addSupportedPublishingEvent(String name);
    
    SecurityRoleRef getSecurityRoleRef(String roleName);
    List<SecurityRoleRef> getSecurityRoleRefs();
    SecurityRoleRef addSecurityRoleRef(String roleName);

    Supports getSupports(String mimeType);
    List<Supports> getSupports();
    Supports addSupports(String mimeType);

    Description getDescription(Locale locale);
    List<Description> getDescriptions();
    Description addDescription(String lang);

    DisplayName getDisplayName(Locale locale);
    List<DisplayName> getDisplayNames();
    DisplayName addDisplayName(String lang);
    GenericMetadata getMetadata();

    ContainerRuntimeOption getContainerRuntimeOption(String name);
    List<ContainerRuntimeOption> getContainerRuntimeOptions();
    ContainerRuntimeOption addContainerRuntimeOption(String name);
    
    Language getLanguage(Locale locale);
    List<Language> getLanguages();
    Language addLanguage(Locale locale);
    
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

    String getPreferenceValidatorClassname();
    void setPreferenceValidatorClassname(String classname);

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
    
    /**
     * Check if this instance its persistent identity has changed
     * @param other
     * @return true only if the internal identities are the same
     */
    boolean isSameIdentity(PortletDefinition other);

    /**
     * Determine if this portlet defintion is a clon
     */
    boolean isClone();

    /**
      * returns the parent portlet definition name from which this
      * portlet clone was cloned from. If not defined, returns null
     *
      * @return the parent application name or null if not a clone
      */
     String getCloneParent();
}
 
