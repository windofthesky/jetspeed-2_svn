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
package org.apache.jetspeed.components.portletregistry;

import java.util.Collection;

import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;

/**
 * <p>
 * PortletRegistryComponentImpl
 * </p>
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public interface PortletRegistry
{
    Collection<PortletDefinition> getAllPortletDefinitions();

    /**
     * Retreives a PortletApplication by it's unique name.  We use
     * PortletApplicationComposite interface which extends the PortletApplication
     * and adds additional functionallity to it.
     * @param id 
     * @return PortletApplicationComposite
     */
    PortletApplication getPortletApplication( String name );

    Collection<PortletApplication> getPortletApplications();

    /**
     * unique name is a string formed by the combination of a portlet's
     * unique within it's parent application plus the parent application's
     * unique name within the portlet container using ":" as a delimiter. 
     * <br/>
     * <strong>FORMAT: </strong> <i>application name</i>::<i>portlet name</i>
     * <br/>
     * <strong>EXAMPLE: </strong> com.myapp.portletApp1::weather-portlet
     * <br/>
     * This methos automatically calls {@link getStoreableInstance(PortletDefinitionComposite portlet)}
     * on the returned <code>PortletEntityInstance</code> 
     * @param name portlets unique name.  
     * @return Portlet that matches the unique name 
     * @throws java.lang.IllegalStateException If <code>PortletDefinitionComposite != null</code> AND
     *  <code>PortletDefinitionComposite.getPortletApplicationDefinition() ==  null</code>.
     * The reason for this is that every PortletDefinition is required to
     * have a parent PortletApplicationDefinition
     * 
     */
    PortletDefinition getPortletDefinitionByUniqueName( String name );

    /**
     * Checks whether or not a portlet application with this name has all ready
     * been registered to the container.
     * @param name portlet application name to check for.
     * @return boolean <code>true</code> if a portlet application with this name
     * is alreay registered, <code>false</code> if it has not.
     */
    boolean portletApplicationExists( String name );
    
    /**
     * Checks whether or not a portlet with this identity has all ready
     * been registered to the PortletApplication.
     * @param portletIndentity portlet indetity to check for.
     * @param app PortletApplication to check .
     * @return boolean <code>true</code> if a portlet with this identity
     * is alreay registered, <code>false</code> if it has not.
     */
    boolean portletDefinitionExists( String portletName, PortletApplication app );

    /**
     * Creates a new <code>PortletApplicationDefinition</code> 
     * within the Portal.          
     * @param newApp
     */
    void registerPortletApplication( PortletApplication newApp ) throws RegistryException;

    void removeApplication( PortletApplication app ) throws RegistryException;

    /**
     * Makes any changes to the <code>PortletApplicationDefinition</code>
     * persistent.
     * @param app
     */
    void updatePortletApplication( PortletApplication app ) throws RegistryException;
    
    /**
     * 
     * <p>
     * savePortletDefinition
     * </p>
     *
     * @param portlet
     * @throws FailedToStorePortletDefinitionException
     */
    void savePortletDefinition(PortletDefinition portlet) throws FailedToStorePortletDefinitionException;
	
    void addRegistryListener(RegistryEventListener listener);
    
    void removeRegistryEventListener(RegistryEventListener listener);
}
