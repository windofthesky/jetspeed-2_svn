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
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.defaults.ComponentParameter
import org.picocontainer.Parameter
import org.apache.jetspeed.components.util.NanoQuickAssembler
 
import org.apache.pluto.PortletContainer
import org.apache.jetspeed.aggregator.PortletRenderer
import org.apache.jetspeed.aggregator.impl.PortletRendererImpl
import org.apache.jetspeed.container.window.PortletWindowAccessor
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent

// def register(container) 


ClassLoader cl = Thread.currentThread().getContextClassLoader()
container = new DefaultPicoContainer()

// RDBMS Datasource and JNDI registration
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/rdbms.container.groovy", container)

// Persistence Store
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/persistence.container.groovy", container)

// Portlet Registry and Entity Access
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/registry.container.groovy", container)

//WindowAccessor
// Portlet Window component
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/window-accessor-container.groovy", container)

// Portlet Container 
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/portlet-container.groovy", container)

container.registerComponentImplementation(PortletRenderer, 
                                          PortletRendererImpl,
                              new Parameter[] {new ComponentParameter(PortletContainer), new ComponentParameter(PortletWindowAccessor)} )

return container


