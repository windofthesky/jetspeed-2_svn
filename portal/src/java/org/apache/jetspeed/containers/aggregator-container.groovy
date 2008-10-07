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

import org.apache.jetspeed.aggregator.PageAggregator
import org.apache.jetspeed.aggregator.impl.PageAggregatorImpl
import org.apache.jetspeed.aggregator.PortletAggregator
import org.apache.jetspeed.aggregator.impl.PortletAggregatorImpl
import org.apache.jetspeed.aggregator.PortletRenderer


// sequential = 0, parallel = 1
strategy = 1

if(parent != null)
{
    container = new DefaultPicoContainer(parent)
    parent.registerComponentImplementation(PageAggregator, 
                                           PageAggregatorImpl,
                              new Parameter[] {new ComponentParameter(PortletRenderer), 
                                               new ConstantParameter(strategy)} )                           	    
    parent.registerComponentImplementation(PortletAggregator, 
                                           PortletAggregatorImpl,
                              new Parameter[] {new ComponentParameter(PortletRenderer)})
                                          
}
else
{
    container = new DefaultPicoContainer()                           	    
    container.registerComponentImplementation(PageAggregator, 
                                           PageAggregatorImpl,
                              new Parameter[] {new ComponentParameter(PortletRenderer), 
                                               new ConstantParameter(strategy)} )                           	    
    container.registerComponentImplementation(PortletAggregator, 
                                           PortletAggregatorImpl,
                              new Parameter[] {new ComponentParameter(PortletRenderer)})
                            
}

return container


