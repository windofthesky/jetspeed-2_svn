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
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter

import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer

import org.apache.jetspeed.security.SecurityProvider
import org.apache.jetspeed.security.impl.SecurityProviderImpl
import org.apache.jetspeed.security.impl.RdbmsPolicy
import org.apache.jetspeed.security.UserManager
import org.apache.jetspeed.security.impl.UserManagerImpl
import org.apache.jetspeed.security.GroupManager
import org.apache.jetspeed.security.impl.GroupManagerImpl
import org.apache.jetspeed.security.RoleManager
import org.apache.jetspeed.security.impl.RoleManagerImpl
import org.apache.jetspeed.security.PermissionManager
import org.apache.jetspeed.security.impl.PermissionManagerImpl


import java.io.File



container = new ChildAwareContainer()
container.registerComponentImplementation(UserManager, UserManagerImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
container.registerComponentImplementation(GroupManager, GroupManagerImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
container.registerComponentImplementation(RoleManager, RoleManagerImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
container.registerComponentImplementation(PermissionManager, PermissionManagerImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
container.registerComponentImplementation(SecurityProvider, SecurityProviderImpl, new Parameter[] {new ConstantParameter("login.conf"), new ComponentParameter(RdbmsPolicy), new ComponentParameter(UserManager)})
	
	
return container
