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

import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.ComponentAdapter
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.defaults.ComponentParameter
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.apache.jetspeed.page.PageManager
import org.apache.jetspeed.page.impl.CastorXmlPageManager
import org.apache.jetspeed.cache.file.FileCache
import org.apache.jetspeed.profiler.Profiler
import org.apache.jetspeed.profiler.impl.JetspeedProfiler
import org.apache.jetspeed.components.ComponentAssemblyTestCase

import java.io.File
import java.util.Properties

applicationRoot = ComponentAssemblyTestCase.getApplicationRoot("portal", "test")

// create the root container
container = new DefaultPicoContainer()

//
// Profiler
//
props = new Properties()
props.put("persistenceStore", "jetspeed")
props.put("defaultRule", "j1")
props.put("anonymousUser", "anon")
props.put("locator.impl", "org.apache.jetspeed.profiler.impl.JetspeedProfileLocator")
props.put("principalRule.impl", "org.apache.jetspeed.profiler.rules.impl.PrincipalRuleImpl")
props.put("profilingRule.impl", "org.apache.jetspeed.profiler.rules.impl.AbstractProfilingRule")

// container.registerComponentInstance(Profiler, new JetspeedProfiler(container.get, pageManager, props))
container.registerComponentImplementation(Profiler, JetspeedProfiler, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter(pageManager), new ConstantParameter(props)} )

return container