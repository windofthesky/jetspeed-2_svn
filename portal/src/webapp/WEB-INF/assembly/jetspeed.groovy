/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorComponentAdapter
import org.picocontainer.Parameter
import org.picocontainer.defaults.ComponentParameter
import org.apache.jetspeed.locator.JetspeedTemplateLocator
import org.apache.jetspeed.components.ComponentAssemblyTestCase
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator
import org.apache.jetspeed.page.PageManager
import org.apache.jetspeed.page.impl.CastorXmlPageManager
import org.apache.jetspeed.Jetspeed
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.apache.jetspeed.components.util.system.FSSystemResourceUtilImpl
import org.apache.jetspeed.container.window.PortletWindowAccessor
import org.apache.jetspeed.container.window.impl.PortletWindowAccessorImpl
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent

import org.apache.jetspeed.cache.file.FileCache
import org.apache.jetspeed.profiler.Profiler
import org.apache.jetspeed.profiler.impl.JetspeedProfiler
import org.apache.jetspeed.capability.Capabilities
import org.apache.jetspeed.capability.impl.JetspeedCapabilities

import org.apache.jetspeed.aggregator.PageAggregator
import org.apache.jetspeed.aggregator.impl.PageAggregatorImpl
import org.apache.jetspeed.aggregator.PortletAggregator
import org.apache.jetspeed.aggregator.impl.PortletAggregatorImpl
import org.apache.jetspeed.aggregator.PortletRenderer
import org.apache.jetspeed.aggregator.impl.PortletRendererImpl
import org.apache.pluto.PortletContainer
import org.apache.jetspeed.request.RequestContextComponent
import org.apache.jetspeed.request.JetspeedRequestContextComponent
import org.apache.jetspeed.container.session.NavigationalStateComponent
import org.apache.jetspeed.container.session.impl.JetspeedNavigationalStateComponent

import org.apache.jetspeed.components.util.NanoQuickAssembler
       
// WARNING!!!!!!
// DO NOT use {Class}.class as it appears to be broken in Groovy
// You end getting a Class instance of the type java.lang.Class
// instead of the requested type!  This causes AssignabilityExceptions
// in pico.  You need to use Class.forName() instead.

ClassLoader cl = Thread.currentThread().getContextClassLoader()


applicationRoot = Jetspeed.getRealPath("/")

//
// Resource Location Utility
//
FSSystemResourceUtilImpl resourceUtil = new FSSystemResourceUtilImpl(applicationRoot)

// create the root container
container = new DefaultPicoContainer()

//
// Template Locator component assembly
//
roots = [ applicationRoot + "WEB-INF/templates" ]
container.registerComponentInstance("TemplateLocator", new JetspeedTemplateLocator(roots, applicationRoot))

decorationRoots = [ applicationRoot + "WEB-INF/decorations" ]
container.registerComponentInstance("DecorationLocator", new JetspeedTemplateLocator(decorationRoots, applicationRoot))

//
// ID Generator
//
Long counterStart = 65536
peidPrefix = "P-"
peidSuffix = ""
idgenerator = new JetspeedIdGenerator(counterStart, peidPrefix, peidSuffix)
container.registerComponentInstance("IdGenerator", idgenerator)

//
// Page Manager
//
root = applicationRoot + "/WEB-INF/pages"
Long scanRate = 120
cacheSize = 100
fileCache = new FileCache(scanRate, cacheSize)
pageManager = new CastorXmlPageManager(idgenerator, fileCache, root)
container.registerComponentInstance(PageManager, pageManager)

// RDBMS Datasource and JNDI registration
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/rdbms.container.groovy", container)

// Persistence Store
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/persistence.container.groovy", container)

// Portlet Registry and Entity Access
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/registry.container.groovy", container)

//
// Profiler
//
// container.registerComponentInstance(Profiler, new JetspeedProfiler(pContainer, pageManager))
container.registerComponentImplementation(Profiler, JetspeedProfiler, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ComponentParameter(PageManager)} )

//
// Capabilities
//
//container.registerComponentInstance(Capabilities, new JetspeedCapabilities(pContainer))
container.registerComponentImplementation(Capabilities, JetspeedCapabilities, new Parameter[] {new ComponentParameter(PersistenceStoreContainer)} )

//
// Navigational State component
//
// navigationKeys: prefix, action, mode, state, renderparam, pid, prev_mode, prev_state, key_delim
navigationKeys = "_,ac,md,st,rp,pid,pm,ps,:"
//navigationKeys = "_,a,m,s,r,i,pm,ps,:"

navStateClass = "org.apache.jetspeed.container.session.impl.PathNavigationalState"
//navStateClass = "org.apache.jetspeed.container.session.impl.SessionNavigationalState"
//urlClass = "org.apache.jetspeed.container.url.impl.SessionPortalURL"
urlClass = "org.apache.jetspeed.container.url.impl.PathPortalURL"
container.registerComponentImplementation(NavigationalStateComponent, JetspeedNavigationalStateComponent,
               new Parameter[] {new ConstantParameter(navStateClass), 
                                new ConstantParameter(urlClass), 
                                new ConstantParameter(navigationKeys)} )


//
// Request Context component
//
requestContextClass = "org.apache.jetspeed.request.JetspeedRequestContext"
container.registerComponentImplementation(RequestContextComponent, JetspeedRequestContextComponent, 
    new Parameter[] {new ComponentParameter(NavigationalStateComponent),
                     new ConstantParameter(requestContextClass)} )

//
// Portlet Window component
//
container.registerComponentImplementation(PortletWindowAccessor, PortletWindowAccessorImpl, 
    new Parameter[] {new ComponentParameter(PortletEntityAccessComponent), new ComponentParameter(PortletRegistryComponent)} )

//
// Portlet Container 
//
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/portlet-container.groovy", container)

// Renderer
container.registerComponentImplementation(PortletRenderer, 
                                          PortletRendererImpl,
                              new Parameter[] {new ComponentParameter(PortletContainer), new ComponentParameter(PortletWindowAccessor)} )

// 
// Aggregator
//
// NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/aggregator-container.groovy", container)
strategy = PageAggregatorImpl.STRATEGY_PARALLEL
container.registerComponentImplementation(PageAggregator, 
                                           PageAggregatorImpl,
                              new Parameter[] {new ComponentParameter(PortletRenderer), 
                                               new ConstantParameter(strategy)} ) 
container.registerComponentImplementation(PortletAggregator, 
                                           PortletAggregatorImpl,
                              new Parameter[] {new ComponentParameter(PortletRenderer)})


return container
