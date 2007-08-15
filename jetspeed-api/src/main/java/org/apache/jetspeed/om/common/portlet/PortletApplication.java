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

import java.util.Collection;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;


/**
 * PortletApplication
 *
 * Extends the <code>org.apache.pluto.om.portlet.PortletApplicationDefinition</code>
 * interface adding methods for those attributes that do not have them
 * so as to make manipulating the portlet OM easier.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface PortletApplication extends PortletApplicationDefinition
{
    /**
     * Returns the metadata from the extended jetspeed-portlet.xml
     * 
     * @return Jetspeed specific metadata
     */
    public GenericMetadata getMetadata();

    /**
     * Gets the name of the Portlet Application.
     * 
     * @return Name of the application
     */
    String getName();
    
    /**
     * @return
     */
    Collection getPortletDefinitions();

    /**
     * Finds a portlet by portlet name, searching this portlet application's collection.
     * 
     * @param name The portlet name.
     * @return A Portlet Definition
     */
    PortletDefinition getPortletDefinitionByName(String name);
    
    /**
     * <p>Gets the collection of user attribute refs associated
     * with this portlet application.</p>
     */
    Collection getUserAttributeRefs();

    /**
     * <p>Gets the collection of user attributes associated
     * with this portlet application.</p>
     */
    Collection getUserAttributes();

    String getApplicationIdentifier();

    /**
     * @return
     */
    String getDescription();

    /**
     * Gets the Portlet Application type.
     * Valid values are:
     * <p>
     *      {@link MutablePortletApplication#WEBAPP} - A standard web application, stored in the web application
     *               server's web application space.
     * <p>
     *      {@link MutablePortletApplication#LOCAL} - A local portlet application stored within Jetspeed's web application.
     * <p>
     * @return The type of portlet application.
     */
    int getApplicationType();
    
    /**
     * Gets a collection of all Jetspeed Services allowed for this application.
     * 
     * @see org.apache.jetspeed.om.common.JetspeedServiceReference
     * @return The collection of services of type <code>JetspeedServiceReference</code>.
     */
    Collection getJetspeedServices();
    
    Collection getCustomPortletModes();        
    Collection getCustomWindowStates();
    
    PortletMode getMappedPortletMode(PortletMode mode);
    WindowState getMappedWindowState(WindowState state);
    PortletMode getCustomPortletMode(PortletMode mode);
    WindowState getCustomWindowState(WindowState state);
        
    Collection getSupportedPortletModes();
    Collection getSupportedWindowStates();
    
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
     * Returns true if the portlet application is a layout application
     * Layouts are not "general" portlets, but instead used to group together
     * other layouts and portlet fragments
     * 
     * @return true when this app is a Jetspeed layout application
     */
    boolean isLayoutApplication();
    
}
