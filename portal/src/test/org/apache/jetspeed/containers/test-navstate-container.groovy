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
 
import org.apache.jetspeed.request.RequestContextComponent
import org.apache.jetspeed.request.JetspeedRequestContextComponent
import org.apache.jetspeed.container.session.NavigationalStateComponent
import org.apache.jetspeed.container.session.impl.JetspeedNavigationalStateComponent



// def register(container) 


ClassLoader cl = Thread.currentThread().getContextClassLoader()
container = new DefaultPicoContainer()


//
// Navigational State component
//
// navigationKeys: prefix, action, mode, state, renderparam, pid, prev_mode, prev_state, key_delim
// navigationKeys = "_,ac,md,st,rp,pid,pm,ps,:"
navigationKeys = "_,a,m,s,r,i,pm,ps,:"

// navStateClass = "org.apache.jetspeed.container.session.impl.PathNavigationalState"
navStateClass = "org.apache.jetspeed.container.session.impl.SessionNavigationalState"
container.registerComponentImplementation(NavigationalStateComponent, JetspeedNavigationalStateComponent,
               new Parameter[] {new ConstantParameter(navStateClass), new ConstantParameter(navigationKeys)} )

//
// Request Context component
//
requestContextClass = "org.apache.jetspeed.request.JetspeedRequestContext"
container.registerComponentImplementation(RequestContextComponent, JetspeedRequestContextComponent, 
    new Parameter[] {new ComponentParameter(NavigationalStateComponent),
                     new ConstantParameter(requestContextClass)} )

return container


