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

import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent
import org.apache.jetspeed.container.window.PortletWindowAccessor
import org.apache.jetspeed.container.window.impl.PortletWindowAccessorImpl
 

if(parent != null)
{
    container = new DefaultPicoContainer(parent)
    parent.registerComponentImplementation(PortletWindowAccessor, 
                                           PortletWindowAccessorImpl, 
                  new Parameter[] {new ComponentParameter(PortletEntityAccessComponent), 
                                   new ComponentParameter(PortletRegistryComponent)} )
                                          

}
else
{
    container = new DefaultPicoContainer()
    container.registerComponentImplementation(PortletWindowAccessor, 
                                           PortletWindowAccessorImpl, 
                  new Parameter[] {new ComponentParameter(PortletEntityAccessComponent), 
                                   new ComponentParameter(PortletRegistryComponent)} )

}

return container

