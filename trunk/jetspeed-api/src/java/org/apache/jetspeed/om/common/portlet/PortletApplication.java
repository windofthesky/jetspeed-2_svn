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

import java.util.Collection;

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
    
}
