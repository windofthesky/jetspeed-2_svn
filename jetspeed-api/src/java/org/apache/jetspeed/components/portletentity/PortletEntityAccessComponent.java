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
package org.apache.jetspeed.components.portletentity;

import java.util.Collection;

import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
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
 * @version $Id$
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
	 * @param principal
	 * @return
	 * @throws PortletEntityNotGeneratedException
	 */
	MutablePortletEntity generateEntityFromFragment(Fragment fragment, String principal) throws PortletEntityNotGeneratedException;
	
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
	MutablePortletEntity generateEntityFromFragment(Fragment fragment) throws PortletEntityNotGeneratedException;
       
	/**
	 * 
	 * <p>
	 * generateEntityKey
	 * </p>
	 *
	 * @param fragment
	 * @param principal
	 * @return
	 */
	ObjectID generateEntityKey(Fragment fragment, String principal);

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
	
	/**
	 * 
	 * <p>
	 * getPortletEntityForFragment
	 * </p>
	 *
	 * @param fragment
	 * @param principal
	 * @return
	 */
	MutablePortletEntity getPortletEntityForFragment(Fragment fragment, String principal);
	
	/**
	 * 
	 * <p>
	 * getPortletEntityForFragment
	 * </p>
	 *
	 * @param fragment
	 * @return
	 */
	MutablePortletEntity getPortletEntityForFragment(Fragment fragment);
    
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

}
