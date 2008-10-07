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
package org.apache.jetspeed.components.portletentity;

import java.util.Collection;

import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * <p>
 * PortletEntityAccessComponent
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: PortletEntityAccessComponent.java,v 1.8 2005/04/29 13:59:46 weaver Exp $
 *
 */
public interface PortletEntityAccessComponent
{
    /**
     * 
     * <p>
     * getPortletEntity
     * </p>
     *
     * @param id
     * @return
     */
	MutablePortletEntity getPortletEntity(ObjectID id);
    
    MutablePortletEntity getPortletEntity(String id);
    
	/**
	 * 
	 * <p>
	 * generateEntityFromFragment
	 * </p>
	 *
	 * @param fragment
	 * @return
	 * @throws PortletEntityNotGeneratedException
	 */
	MutablePortletEntity generateEntityFromFragment(ContentFragment fragment) throws PortletEntityNotGeneratedException;
       
	/**
	 * 
	 * <p>
	 * generateEntityKey
	 * </p>
	 *
	 * @param fragment
	 * @return
	 */
	ObjectID generateEntityKey(Fragment fragment);

	/**
	 * 
	 * <p>
	 * newPortletEntityInstance
	 * </p>
	 *
	 * @param portletDefinition
	 * @return
	 */
	MutablePortletEntity newPortletEntityInstance(PortletDefinition portletDefinition);
    MutablePortletEntity newPortletEntityInstance(PortletDefinition portletDefinition, String id);
	
	/**
	 * 
	 * <p>
	 * getPortletEntityForFragment
	 * </p>
	 *
	 * @param fragment
	 * @return
	 * @throws PortletEntityNotStoredException 
	 */
	MutablePortletEntity getPortletEntityForFragment(ContentFragment fragment) throws PortletEntityNotStoredException;
    
	/**
	 * 
	 * <p>
	 * removePortletEntity
	 * </p>
	 *
	 * @param portletEntity
	 * @throws PortletEntityNotDeletedException
	 */
	void removePortletEntity(PortletEntity portletEntity) throws PortletEntityNotDeletedException;
	
	/**
	 * 
	 * <p>
	 * removeFromCache
	 * </p>
	 * Removes a PortletEntity from the cache.
	 * @param entity
	 */
	void removeFromCache(PortletEntity entity);

    /**
     * <p>
     * updatePortletEntity
     * </p>
     *
     * Updates portlet definition associated with the portlet
     * entity to match the fragment configuration 
     *
     * @param portletEntity
	 * @param fragment
	 * @throws PortletEntityNotStoredException 
     */
    void updatePortletEntity(PortletEntity portletEntity, ContentFragment fragment) throws PortletEntityNotStoredException;

    /**
     * 
     * <p>
     * storePortletEntity
     * </p>
     *
     * @param portletEntity
     * @throws PortletEntityNotStoredException
     */
	void storePortletEntity(PortletEntity portletEntity) throws PortletEntityNotStoredException;
	
	/**
	 * 
	 * <p>
	 * getPortletEntities
	 * </p>
	 *
	 * @param portletDefinition
	 * @return
	 */
	Collection getPortletEntities(PortletDefinition portletDefinition);
    
    Collection getPortletEntities( String portletUniqueName );
	
	/**
	 * 
	 * <p>
	 * removePortletEntities
	 * </p>
	 *
	 * @param portletDefinition
	 * @throws PortletEntityNotDeletedException
	 */
	void removePortletEntities(PortletDefinition portletDefinition) throws PortletEntityNotDeletedException;
	
    /** 
     *  All preferences were shared. With JS2-449, preferences are now
     *  stored 'per user'. The username is stored in the preferences FULL_PATH
     *  To turn on mergeSharedPreferences configure this property to true 
     *  in your Spring configuration. This will NOT turn off per user prefs, 
     *  but instead merge with them, where user prefs override.
     */   
    boolean isMergeSharedPreferences();
}
