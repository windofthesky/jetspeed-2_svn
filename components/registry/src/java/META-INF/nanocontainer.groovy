/* ========================================================================
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================================
 */

import org.apache.jetspeed.components.ChildAwareContainer
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.defaults.ComponentParameter

import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.apache.jetspeed.components.util.system.FSSystemResourceUtilImpl

import org.apache.jetspeed.components.portletregistry.PortletRegistryComponentImpl
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponentImpl

import java.io.File


container = new ChildAwareContainer(parent)
container.registerComponentImplementation(PortletRegistryComponent, PortletRegistryComponentImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
container.registerComponentImplementation(PortletEntityAccessComponent, PortletEntityAccessComponentImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )



return container
