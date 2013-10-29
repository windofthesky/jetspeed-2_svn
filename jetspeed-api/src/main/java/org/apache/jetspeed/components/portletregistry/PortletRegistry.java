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

import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;

import java.util.Collection;

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
    /**
     * Retrieves all portlet definitions for this deployment
     * including both clones and portlet definitions
     * @return a collection of portlet definitions from all portlet apps
     */
    Collection<PortletDefinition> getAllDefinitions();

    /**
     * Retrieve all portlet definitions system wide
     * The collection does not include clones
     * @return a collection of portlet definitions from all portlet apps
     */
    Collection<PortletDefinition> getAllPortletDefinitions();

    /**
     * Retrieve all clone definitions system wide 
     * @return a collection of portlet definitions from all portlet apps
     */
    Collection<PortletDefinition> getAllCloneDefinitions();

    /**
     * Retrieves a PortletApplication by it's unique name.  We use
     * PortletApplication interface which extends the Pluto PortletApplicationDefinition
     * and adds additional functionallity to it.
     * <br/>
     * This method will always retrieve a fresh instance from the database, bypassing
     * internal cache and therefore should be used when expecting to perform a write operation.
     * @param name 
     * @return PortletApplication
     */
    PortletApplication getPortletApplication( String name );

    /**
     * Retreives a PortletApplication by it's unique name.  We use
     * PortletApplication interface which extends the Pluto PortletApplicationDefinition
     * and adds additional functionallity to it.
     * <br/>
     * This method will optionally try to retrieve a <em>shared</em> instance from an internal
     * cache first. If not found it will retrieve a fresh instance from the database (which
     * will also store the instance in the cache for future access).
     * <br/>
     * Because the returned instance might be shared it should only be used for readonly purposes.
     * @param name
     * @param fromCache when true first try to retrieve a shared instance from cache
     * @return PortletApplication
     */
    PortletApplication getPortletApplication( String name, boolean fromCache );

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
     * This method will always retrieve a fresh instance from the database, bypassing
     * internal cache and therefore should be used when expecting to perform a write operation.
     * @param name portlets unique name.  
     * @return Portlet that matches the unique name 
     */
    PortletDefinition getPortletDefinitionByUniqueName( String name );

    /**
     * unique name is a string formed by the combination of a portlet's
     * unique within it's parent application plus the parent application's
     * unique name within the portlet container using ":" as a delimiter. 
     * <br/>
     * <strong>FORMAT: </strong> <i>application name</i>::<i>portlet name</i>
     * <br/>
     * <strong>EXAMPLE: </strong> com.myapp.portletApp1::weather-portlet
     * <br/>
     * This method will optionally try to retrieve a <em>shared</em> instance from an internal
     * cache first. If not found it will retrieve a fresh instance from the database (which
     * will also store the instance in the cache for future access).
     * <br/>
     * Because the returned instance might be shared it should only be used for readonly purposes.
     * @param name portlets unique name.  
     * @param fromCache when true first try to retrieve a shared instance from cache
     * @return Portlet that matches the unique name 
     */
    PortletDefinition getPortletDefinitionByUniqueName( String name, boolean fromCache );

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
     * @param portletName portlet identity to check for.
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

    /**
     * Create a portlet instance
     *
     * @param source create an instance from this given portlet definition
     * @param newPortletName a unique portlet name
     * @throws FailedToStorePortletDefinitionException
     */
    PortletDefinition clonePortletDefinition(PortletDefinition source, String newPortletName) throws FailedToStorePortletDefinitionException;

    /**
     * Restores all orphaned clones to a re-registered portlet application
     * @param pa
     * @return
     * @throws RegistryException
     */
    public int restoreClones(PortletApplication pa)
            throws RegistryException;


    /**
     * Remove a clone from a given portlet definition
     * 
     * @param clone the portlet definition
     * @throws RegistryException
     */
    void removeClone(PortletDefinition clone)
            throws RegistryException;
    
    /**
     * Remove all clones from a given portlet application
     * 
     * @param pa the portlet application
     * @throws RegistryException
     */
    void removeAllClones(PortletApplication pa)
            throws RegistryException;

}
