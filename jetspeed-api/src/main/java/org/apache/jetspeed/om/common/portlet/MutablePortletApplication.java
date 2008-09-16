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

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.JetspeedServiceReference;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.UserAttributeRef;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.apache.pluto.om.servlet.WebApplicationDefinition;

/**
 * MutablePortletApplication
 *
 * Extends the <code>org.apache.pluto.om.portlet.PortletApplicationDefinition</code>
 * interface adding mutator methods for those attributes that do not have them
 * so as to make manipulating the portlet OM easier.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface MutablePortletApplication extends PortletApplication, Serializable
{
    /**
     * Sets the metadata from the extended jetspeed-portlet.xml
     * 
     * @param metadata
     */
    public void setMetadata(GenericMetadata metadata);
    
    /**
     * Associates the web application definition with this portlet application defintion.
     * 
     * @param wad
     */
    void setWebApplicationDefinition(WebApplicationDefinition wad);

    /**
     * @param name
     */
    void setName(String name);

    /**
     * @param pd
     */
    void addPortletDefinition(PortletDefinition pd);

    /**
     * @param portlets
     */
    void setPortletDefinitionList(PortletDefinitionList portlets);

    /**
     * <p>Adds a user attribute to the user attribute set.</p>
     * @param userAttribute The user attribute.
     */
    void addUserAttribute(UserAttribute userAttribute);
    
    /**
     * <p>Adds a user attribute to the user attribute set.</p>
     * @param userAttribute The user attribute.
     */
    void addUserAttribute(String name, String description);

    /**
     * <p>Sets a user-attribute-ref to the collection of user attribute refs associated
     * with this portlet application.</p>
     */
    void setUserAttributeRefs(Collection userAttributeRefs);
    
    /**
     * <p>Adds a user attribute ref to the user attribute ref set.</p>
     * @param userAttributeRef The user attribute ref.
     */
    void addUserAttributeRef(UserAttributeRef userAttributeRef);

    /**
     * <p>Sets a user-attribute to the collection of user attributes associated
     * with this portlet application.</p>
     */
    void setUserAttributes(Collection userAttributes);
    
    void setApplicationIdentifier(String applicationIndentifier);

    /**
     * @param string
     */
    void setDescription(String string);

    /**
     *
     * @param version
     */
    void setVersion(String version);

    /**
     * Sets the Portlet Application type.
     * Valid values are:
     * <p>
     *      {@link MutablePortletApplication#WEBAPP} - A standard web application, stored in the web application
     *               server's web application space.
     * <p>
     *      {@link MutablePortletApplication#LOCAL} - A local portlet application stored within Jetspeed's web application.
     * <p>
     * @param type The type of portlet application.
     */
    void setApplicationType(int type);

    /**
     * Marks this application as a standard web application,
     * stored in the web application server's web application space.
     */
    public static final int WEBAPP = 0;

    /**
     * Marks this application as a LOCAL portlet application,
     * stored in Jetspeed managed portlet application space.
     */
    public static final int LOCAL = 1;

    /**
     * Marks this application as a INTERNAL portlet application,
     * stored in Jetspeed managed portlet application space.
     */
    public static final int INTERNAL = 2;
    
    /**
     * Adds a Jetspeed component service to the collection of component services allowed for this application.
     * 
     * @param service The component service being added.
     */
    void addJetspeedService(JetspeedServiceReference service);
    
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
    
}
