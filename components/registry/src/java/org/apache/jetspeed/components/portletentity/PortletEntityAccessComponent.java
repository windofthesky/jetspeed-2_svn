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
	StoreablePortletEntityDelegate getPortletEntity(ObjectID id);


	StoreablePortletEntityDelegate getPortletEntity(PortletDefinition portletDefinition, String portletName);
       

	StoreablePortletEntityDelegate newPortletEntityInstance(PortletDefinition portletDefinition);


	void removePortletEntity(PortletEntity portletEntity) throws PortletEntityNotDeletedException;


	void storePortletEntity(PortletEntity portletEntity) throws PortletEntityNotStoredException;


}
