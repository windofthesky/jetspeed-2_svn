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

import javax.portlet.PortletMode;
import javax.portlet.WindowState;
import javax.xml.namespace.QName;

import org.apache.pluto.container.om.portlet.PortletApplicationDefinition;


/**
 * PortletApplication
 *
 * Extends the <code>org.apache.pluto.container.om.portlet.PortletApplicationDefinition</code>
 * interface adding methods for those attributes that do not have them
 * so as to make manipulating the portlet OM easier.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface PortletApplication extends PortletApplicationDefinition, Serializable
{
    /**
     * Marks this application as a standard web application,
     * stored in the web application server's web application space.
     */
    int WEBAPP = 0;

    /**
     * Marks this application as a LOCAL portlet application,
     * stored in Jetspeed managed portlet application space.
     */
    int LOCAL = 1;

    /**
     * Gets the Portlet Application type.
     * Valid values are:
     * <p>
     *      {@link PortletApplication#WEBAPP} - A standard web application, stored in the web application
     *               server's web application space.
     * <p>
     *      {@link PortletApplication#LOCAL} - A local portlet application stored within Jetspeed's web application.
     * <p>
     * @return The type of portlet application.
     */
    int getApplicationType();
    void setApplicationType(int type);

    /**
     * The checksum on the portlet XML from the last deployment
     *  
     * @param checksum
     */
    void setChecksum(long checksum);
    
    /**
     * The checksum on the portlet XML from the last deployment
     * 
     * @return
     */
    long getChecksum();
    
    long getRevision();
    
    void setRevision(long revision);
    
    /**
     * <p>
     * Get the Jetspeed Security Constraint reference for this portlet application.
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
     * Set the Jetspeed Security Constraint reference for this portlet application.
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
    
    Description getDescription(Locale locale);
    List<Description> getDescriptions();
    Description addDescription(String lang);

    DisplayName getDisplayName(Locale locale);
    List<DisplayName> getDisplayNames();
    DisplayName addDisplayName(String lang);
    
    List<SecurityRole> getSecurityRoles();
    SecurityRole addSecurityRole(String name);
    
    /**
     * Returns the metadata from the extended jetspeed-portlet.xml
     * The return value cannot be NULL
     * 
     * @return Jetspeed specific metadata
     */
    public GenericMetadata getMetadata();

    PortletDefinition getPortlet(String name);
    PortletDefinition getClone(String name);
    List<PortletDefinition> getPortlets();
    List<PortletDefinition> getClones();
    PortletDefinition addPortlet(String name);
    PortletDefinition addClone(String name);

    List<EventDefinition> getEventDefinitions();
    EventDefinition addEventDefinition(String name);
    EventDefinition addEventDefinition(QName qname);
    
    PublicRenderParameter getPublicRenderParameter(String identifier);
    List<PublicRenderParameter> getPublicRenderParameters();
    PublicRenderParameter addPublicRenderParameter(String name, String identifier);
    PublicRenderParameter addPublicRenderParameter(QName qname, String identifier);
    
    CustomPortletMode getCustomPortletMode(String name);
    PortletMode getCustomPortletMode(PortletMode mode);
    List<CustomPortletMode> getCustomPortletModes();
    CustomPortletMode addCustomPortletMode(String name);
    
    CustomWindowState getCustomWindowState(String name);
    WindowState getCustomWindowState(WindowState state);
    List<CustomWindowState> getCustomWindowStates();
    CustomWindowState addCustomWindowState(String name);
    
    List<PortletMode> getSupportedPortletModes();    
    List<WindowState> getSupportedWindowStates();
    PortletMode getMappedPortletMode(PortletMode mode);
    WindowState getMappedWindowState(WindowState state);
        
    UserAttribute getUserAttribute(String name);
    List<UserAttribute> getUserAttributes();
    UserAttribute addUserAttribute(String name);
    
    UserAttributeRef getUserAttributeRef(String name);
    List<UserAttributeRef> getUserAttributeRefs();
    UserAttributeRef addUserAttributeRef(String name);
    
    List<SecurityConstraint> getSecurityConstraints();
    SecurityConstraint addSecurityConstraint(String transportGuarantee);
    
    Filter getFilter(String filterName);
    List<Filter> getFilters();
    Filter addFilter(String filterName);

    FilterMapping getFilterMapping(String filterName);
    List<FilterMapping> getFilterMappings();
    FilterMapping addFilterMapping(String filterName);

    List<Listener> getListeners();
    Listener addListener(String listenerClass);

    ContainerRuntimeOption getContainerRuntimeOption(String name);
    List<ContainerRuntimeOption> getContainerRuntimeOptions();
    ContainerRuntimeOption addContainerRuntimeOption(String name);
    
    List<JetspeedServiceReference> getJetspeedServices();
    void addJetspeedServiceReference(String name);

    /**
     * Returns true if the portlet application is a layout application
     * Layouts are not "general" portlets, but instead used to group together
     * other layouts and portlet fragments
     * 
     * @return true when this app is a Jetspeed layout application
     */
    boolean isLayoutApplication();    

    /**
     * Returns the context path of local portlet application.
     * If the application is not local, then returns null.
     * @return
     */
    String getLocalContextPath();
    
    /**
     * Sets the context path of local portlet application.
     * @param localContextPath
     */
    void setLocalContextPath(String localContextPath);

}
