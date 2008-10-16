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
import java.util.Locale;

import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.pluto.om.portlet.Language;
import org.apache.pluto.om.portlet.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;

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

    Language createLanguage( Locale locale, String title, String shortTitle, String description, Collection keywords )
    throws RegistryException;

    Collection getAllPortletDefinitions();

    /**
     * Retreives a PortletApplication by it's id.
     * @param id 
     * @return
     */
    PortletApplication getPortletApplicationById( String id );

    /**
     * Retreives a PortletApplication by it's unique name.  We use
     * PortletApplicationComposite interface which extends the PortletApplication
     * and adds additional functionallity to it.
     * @param id 
     * @return PortletApplicationComposite
     */
    PortletApplication getPortletApplication( String name );

    /**
     * Locates a portlet application using it's unique <code>identifier</code> 
     * field.
     * @param identifier Unique id for this portlet application
     * @return portlet application matching this unique id.
     */
    PortletApplication getPortletApplicationByIdentifier( String identifier );

    Collection getPortletApplications();

    /**
     * Locates a portlet using it's unique <code>identifier</code> 
     * field.
     * <br/>
     * This method automatically calls {@link getStoreableInstance(PortletDefinitionComposite portlet)}
     * on the returned <code>PortletEntityInstance</code>
     * @param identifier Unique id for this portlet
     * @return Portlet matching this unique id.
     * @throws java.lang.IllegalStateException If <code>PortletDefinitionComposite != null</code> AND
     *  <code>PortletDefinitionComposite.getPortletApplicationDefinition() ==  null</code>.
     * The reason for this is that every PortletDefinition is required to
     * have a parent PortletApplicationDefinition
     */
    PortletDefinitionComposite getPortletDefinitionByIdentifier( String identifier );
    
    
    /**
     * Locates the portlet defintion by its unique <code>ObjectID</code>.
     * The ObjectID is generated internally by the portal when the portlet
     * definition is first registered and has no connection to the information
     * stored within the <code>portlet.xml</code>.
     * @param id
     * @return PortletDefinitionComposite
     */
    PortletDefinitionComposite getPortletDefinition(ObjectID id);
    

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
    PortletDefinitionComposite getPortletDefinitionByUniqueName( String name );

    /**
     * Checks whether or not a portlet application with this identity has all ready
     * been registered to the container.
     * @param appIdentity portlet application indetity to check for.
     * @return boolean <code>true</code> if a portlet application with this identity
     * is alreay registered, <code>false</code> if it has not.
     */
    boolean portletApplicationExists( String appIentity );
    
    /**
     * 
     * <p>
     * namedPortletApplicationExists
     * </p>
     *
     * @param appName
     * @return
     */
    boolean namedPortletApplicationExists( String appName );

    /**
     * Checks whether or not a portlet with this identity has all ready
     * been registered to the container.
     * @param portletIndentity portlet indetity to check for.
     * @return boolean <code>true</code> if a portlet with this identity
     * is alreay registered, <code>false</code> if it has not.
     */
    boolean portletDefinitionExists( String portletIndentity );

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
    void registerPortletApplication( PortletApplicationDefinition newApp ) throws RegistryException;

    void removeApplication( PortletApplicationDefinition app ) throws RegistryException;

    /**
     * Makes any changes to the <code>PortletApplicationDefinition</code>
     * persistent.
     * @param app
     */
    void updatePortletApplication( PortletApplicationDefinition app ) throws RegistryException;
    
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
    
    void removeRegistryEventListner(RegistryEventListener listener);
}
