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

import org.apache.jetspeed.prefs.PropertyManager
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl
import org.apache.jetspeed.prefs.PreferencesProvider
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl

import java.io.File



container = new ChildAwareContainer(parent)
container.registerComponentImplementation(PropertyManager, PropertyManagerImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
container.registerComponentImplementation(PreferencesProvider, PreferencesProviderImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed"), new ConstantParameter("org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl")} )	
	
	

return container