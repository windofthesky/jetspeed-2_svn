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

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.UserAttribute;
import org.apache.jetspeed.om.common.UserAttributeRef;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.apache.pluto.om.servlet.WebApplicationDefinition;

/**
 * MutablePortletApplication
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
/**
 *
 * MutablePortletApplication
 *
 * Extends the <code>org.apache.pluto.om.portlet.PortletApplicationDefinition</code>
 * interface adding mutator methods for those attributes that do not have them
 * so as to make manipulating the portlet OM easier.
 * It has additional methods to make it easier to use within Jetspeed.
 *
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface MutablePortletApplication extends PortletApplicationDefinition, Serializable
{
    public GenericMetadata getMetadata();
    
    public void setMetadata(GenericMetadata metadata);
    
    void setWebApplicationDefinition(WebApplicationDefinition wad);

    void setName(String name);

    String getName();

    void addPortletDefinition(PortletDefinition pd);

    Collection getPortletDefinitions();

    PortletDefinition getPortletDefinitionByName(String name);

    void setPortletDefinitionList(PortletDefinitionList portlets);

    /**
     * <p>Adds a user attribute to the user attribute set.</p>
     * @param userAttribute The user attribute.
     */
    void addUserAttribute(UserAttribute userAttribute);

    /**
     * <p>Sets a user-attribute-ref to the collection of user attribute refs associated
     * with this portlet application.</p>
     */
    void setUserAttributeRefs(Collection userAttributeRefs);
    
    /**
     * <p>Gets the collection of user attribute refs associated
     * with this portlet application.</p>
     */
    Collection getUserAttributeRefs();

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
    
    /**
     * <p>Gets the collection of user attributes associated
     * with this portlet application.</p>
     */
    Collection getUserAttributes();

    void setApplicationIdentifier(String applicationIndentifier);

    String getApplicationIdentifier();

    /**
     * @return
     */
    String getDescription();

    /**
     * @param string
     */
    void setDescription(String string);

    /**
     * @param objectID
     */
    void setId(String objectID);

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
     * Marks this application as a standard web application,
     * stored in the web application server's web application space.
     */
    public static final int WEBAPP = 0;

    /**
     * Marks this application as a LOCAL portlet application,
     * stored in Jetspeed managed portlet application space.
     */
    public static final int LOCAL = 1;
}
