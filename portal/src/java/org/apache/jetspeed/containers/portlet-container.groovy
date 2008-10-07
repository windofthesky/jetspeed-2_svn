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
 
import org.apache.pluto.PortletContainer
import org.apache.pluto.PortletContainerImpl
import org.apache.jetspeed.container.PortletContainerWrapper
import org.apache.jetspeed.container.JetspeedPortletContainerWrapper

// create the Pluto container
PortletContainer pluto = new PortletContainerImpl()

if(parent != null)
{
    container = new DefaultPicoContainer(parent)
    // wrapper Pluto
    parent.registerComponentImplementation(PortletContainer, 
                                          JetspeedPortletContainerWrapper,
                                          new Parameter[] {new ConstantParameter(pluto)} )

}
else
{
    container = new DefaultPicoContainer()
    // wrapper Pluto
    container.registerComponentImplementation(PortletContainer, 
                                          JetspeedPortletContainerWrapper,
                                          new Parameter[] {new ConstantParameter(pluto)} )

}



return container

