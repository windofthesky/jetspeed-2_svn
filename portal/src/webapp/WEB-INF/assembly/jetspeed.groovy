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
import org.picocontainer.defaults.CachingComponentAdapter
import org.picocontainer.Parameter
import org.picocontainer.defaults.ComponentParameter
import org.apache.jetspeed.components.adapters.InterceptorAdapter
import org.apache.jetspeed.components.adapters.ThreadLocalDelegationStrategy
import org.apache.jetspeed.locator.JetspeedTemplateLocator
import org.apache.jetspeed.components.ComponentAssemblyTestCase
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator
import org.apache.jetspeed.page.PageManager
import org.apache.jetspeed.page.impl.CastorXmlPageManager
import org.apache.jetspeed.Jetspeed
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.ojb.pb.PBStore
import org.apache.jetspeed.components.util.system.FSSystemResourceUtilImpl
import org.apache.jetspeed.container.window.PortletWindowAccessor
import org.apache.jetspeed.container.window.impl.PortletWindowAccessorImpl
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent

import org.apache.jetspeed.cache.file.FileCache
import org.apache.jetspeed.profiler.Profiler
import org.apache.jetspeed.profiler.impl.JetspeedProfiler
import org.apache.jetspeed.capabilities.Capabilities
import org.apache.jetspeed.capabilities.impl.JetspeedCapabilities

import org.apache.jetspeed.aggregator.PageAggregator
import org.apache.jetspeed.aggregator.impl.PageAggregatorImpl
import org.apache.jetspeed.aggregator.PortletAggregator
import org.apache.jetspeed.aggregator.impl.PortletAggregatorImpl
import org.apache.jetspeed.aggregator.PortletRenderer
import org.apache.jetspeed.aggregator.impl.PortletRendererImpl
import org.apache.jetspeed.request.RequestContextComponent
import org.apache.jetspeed.request.JetspeedRequestContextComponent
import org.apache.jetspeed.container.session.NavigationalStateComponent
import org.apache.jetspeed.container.session.impl.JetspeedNavigationalStateComponent
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl
import org.apache.jetspeed.security.impl.SecurityProviderImpl

// JNDI and Datasource Implementations
import org.apache.jetspeed.components.jndi.JNDIComponent
import org.apache.jetspeed.components.jndi.TyrexJNDIComponent
import org.apache.jetspeed.components.datasource.DatasourceComponent
import org.apache.jetspeed.components.datasource.BoundDBCPDatasourceComponent

// Commons Pooling 
import org.apache.commons.pool.impl.GenericObjectPool

// Persistence Store
import org.apache.jetspeed.components.persistence.store.ojb.pb.PBStore
import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer

// Portlet Registry and Entity
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponentImpl
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponentImpl

// Preferences
import org.apache.jetspeed.prefs.PropertyManager
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl
import org.apache.jetspeed.prefs.PreferencesProvider
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl

// Security
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

//User Info
import org.apache.jetspeed.security.UserManager
import org.apache.jetspeed.userinfo.UserInfoManager
import org.apache.jetspeed.userinfo.impl.UserInfoManagerImpl

// Portlet Container
import org.apache.pluto.PortletContainer
import org.apache.pluto.PortletContainerImpl
import org.apache.jetspeed.container.PortletContainerWrapper
import org.apache.jetspeed.container.JetspeedPortletContainerWrapper

       
/* **********************************************************
 *  U T I L L I T Y   C L O S U R E S                                                     *
 * ******************************************************** */
 
 // This creates a single component instance per thread
 makeThreadLocalAdapter = 
{ 
  key, clazz, parameters | return new CachingComponentAdapter(
                  new InterceptorAdapter( 
                   new ConstructorComponentAdapter(key, clazz, parameters),
                   ThreadLocalDelegationStrategy
                   )
                 )
}

// Shorthand for creating a ConstantParameter
cstParam = { key | return  new ConstantParameter(key) }

// Shorthand for creating a ComponentParameter
cmpParam = { key | return  new ComponentParameter(key) }

// Closure to perform easy building of Parameter[]
doParams = 
{
    paramList |  paramArray = new Parameter[paramList.size()]
                       i = 0
                       for(param in paramList)
                      {
                         paramArray[i]   =  param                    
                         i++
                      }
                      return paramArray
}
                   
 
 /* ******************************************************* */

ClassLoader cl = Thread.currentThread().getContextClassLoader()


applicationRoot = Jetspeed.getRealPath("/")

//
// Resource Location Utility
//
FSSystemResourceUtilImpl resourceUtil = new FSSystemResourceUtilImpl(applicationRoot)

// create the root container
container = new DefaultPicoContainer()

/* **********************************************************
 *  Template Locators                                                                      *
 * ******************************************************** */
roots = [ applicationRoot + "WEB-INF/templates" ]
container.registerComponentInstance("TemplateLocator", new JetspeedTemplateLocator(roots, applicationRoot))

decorationRoots = [ applicationRoot + "WEB-INF/decorations" ]
container.registerComponentInstance("DecorationLocator", new JetspeedTemplateLocator(decorationRoots, applicationRoot))

/* **********************************************************
 *  ID Generator                                                                               *
 * ******************************************************** */
Long counterStart = 65536
peidPrefix = "P-"
peidSuffix = ""
idgenerator = new JetspeedIdGenerator(counterStart, peidPrefix, peidSuffix)
container.registerComponentInstance("IdGenerator", idgenerator)

/* **********************************************************
 *  Page Manager                                                                             *
 * ******************************************************** */
root = applicationRoot + "/WEB-INF/pages"
Long scanRate = 120
cacheSize = 100
fileCache = new FileCache(scanRate, cacheSize)
pageManager = new CastorXmlPageManager(idgenerator, fileCache, root)
container.registerComponentInstance(PageManager, pageManager)

/* **********************************************************
 *  JNDI and Pooled Datasource                                                        *
 * ******************************************************** */
 
if(Boolean.getBoolean("portal.use.internal.jndi"))
{
   container.registerComponentInstance(JNDIComponent, new TyrexJNDIComponent()) 

   url = System.getProperty("org.apache.jetspeed.database.url")  
   driver = System.getProperty("org.apache.jetspeed.database.driver")
   user = System.getProperty("org.apache.jetspeed.database.user")
   password = System.getProperty("org.apache.jetspeed.database.password")

   container.registerComponentInstance(DatasourceComponent, 
              new BoundDBCPDatasourceComponent(
                    user, 
                    password, 
                    driver, 
                    url, 
                    20, 
                    5000, 
                    GenericObjectPool.WHEN_EXHAUSTED_GROW, 
                    true, 
                    "jetspeed", 
                    container.getComponentInstance(JNDIComponent))
               )
}

/* **********************************************************
 *  Persistence Store Container                                                        *
 * ******************************************************** */
pContainer = new DefaultPersistenceStoreContainer(15000, 10000)
container.registerComponentInstance(PersistenceStoreContainer, pContainer)
pContainer.registerComponent( new ConstructorComponentAdapter(
											  "jetspeed", 
                                              PBStore, 
                                              doParams([cstParam("jetspeed")])
                                        )
)



/* **********************************************************
 * Persistence Store: as a thread safe per thread component           *
 * (will replace PersistenceStoreContainer above)                            *
 * ******************************************************** */

container.registerComponent(makeThreadLocalAdapter("jetspeed", PBStore.class,  new Parameter[]{new ConstantParameter("jetspeed")}))


/* **********************************************************
 *  Porlet Registry                                                                            *
 * ******************************************************** */
 container.registerComponentImplementation(
                            PortletRegistryComponent, 
                            PortletRegistryComponentImpl, 
                            doParams([cmpParam(PersistenceStoreContainer), cstParam("jetspeed")])
)

/* **********************************************************
 *  Portlet Entity                                                                                *
 * ******************************************************** */
container.registerComponentImplementation(
                           PortletEntityAccessComponent, 
                           PortletEntityAccessComponentImpl,
                           doParams([cmpParam(PersistenceStoreContainer),  cstParam("jetspeed")])
)

/* **********************************************************
 *  Profiler                                                                                        *
 * ******************************************************** */
container.registerComponentImplementation(
                       Profiler, 
                       JetspeedProfiler, 
                       doParams([cmpParam(PersistenceStoreContainer), cmpParam(PageManager)])
)

/* **********************************************************
 *  Capabilities                                                                                 *
 * ******************************************************** */
container.registerComponentImplementation(
                      Capabilities, 
                      JetspeedCapabilities, 
                      doParams([cmpParam(PersistenceStoreContainer)])
)

/* **********************************************************
 *  Preferences  & Properites (java.util.prefs implemnetation)           *
 * ******************************************************** */
container.registerComponentImplementation(
	                      PropertyManager, 
	                      PropertyManagerImpl, 
	                      doParams([cmpParam(PersistenceStoreContainer), cstParam("jetspeed")])
)
	
container.registerComponentImplementation(
                       PreferencesProvider, 
                       PreferencesProviderImpl, 
                       doParams([cmpParam(PersistenceStoreContainer),
                                        cstParam("jetspeed"), 
                                        cstParam("org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl")]
                                       )
)	


/* **********************************************************
 *  Security                                                                                      *
 * ******************************************************** */
container.registerComponentImplementation(
                     UserManager, 
                     UserManagerImpl, 
                     doParams([cmpParam(PersistenceStoreContainer), cstParam("jetspeed")])
)

container.registerComponentImplementation(
                    GroupManager, 
                    GroupManagerImpl, 
                    doParams([cmpParam(PersistenceStoreContainer), cstParam("jetspeed")])
)

container.registerComponentImplementation(
                      RoleManager, 
                      RoleManagerImpl,
                      doParams([cmpParam(PersistenceStoreContainer), cstParam("jetspeed")])
)

container.registerComponentImplementation(
                      PermissionManager, 
                      PermissionManagerImpl, 
                      doParams([cmpParam(PersistenceStoreContainer), cstParam("jetspeed")])
)

container.registerComponentImplementation(
                      RdbmsPolicy, 
                      RdbmsPolicy, 
                      doParams([cmpParam(PermissionManager)])
)

container.registerComponentImplementation(
                      SecurityProvider, 
                      SecurityProviderImpl, 
                      doParams([cstParam("login.conf"), cmpParam(RdbmsPolicy), cmpParam(UserManager)])
)


// Instantiate the Preferences provider.
container.getComponentInstanceOfType(PreferencesProviderImpl)

// Instantiate the Security provider.
container.getComponentInstanceOfType(SecurityProviderImpl)

/* **********************************************************
 *  User Info                                                                                     *
 * ******************************************************** */
container.registerComponentImplementation(
                      UserInfoManager, 
                      UserInfoManagerImpl, 
                      doParams([cmpParam(UserManager), cmpParam(PortletRegistryComponent)])
)

/* **********************************************************
 *   Navigational State component                                                    *
 * ******************************************************** */
// navigationKeys: prefix, action, mode, state, renderparam, pid, prev_mode, prev_state, key_delim
// navigationKeys = "_,ac,md,st,rp,pid,pm,ps,:"
// navigationKeys = "_,a,m,s,r,i,pm,ps,:"

container.registerComponentInstance("navigationKeys", "_,a,m,s,r,i,pm,ps,:")

// navStateClass = "org.apache.jetspeed.container.session.impl.PathNavigationalState"
// navStateClass = "org.apache.jetspeed.container.session.impl.SessionNavigationalState"

container.registerComponentInstance("navStateClass", "org.apache.jetspeed.container.session.impl.SessionNavigationalState")

// urlClass = "org.apache.jetspeed.container.url.impl.SessionPortalURL"
// urlClass = "org.apache.jetspeed.container.url.impl.PathPortalURL"

container.registerComponentInstance("urlClass", "org.apache.jetspeed.container.url.impl.SessionPortalURL")

container.registerComponentImplementation(
                     NavigationalStateComponent, 
                     JetspeedNavigationalStateComponent,
                     doParams([cmpParam("navStateClass"), cmpParam("urlClass"), cmpParam("navigationKeys")])
)

/* **********************************************************
 *  Request Context component                                                        *
 * ******************************************************** */
requestContextClass = "org.apache.jetspeed.request.JetspeedRequestContext"
container.registerComponentImplementation(
                      RequestContextComponent, 
                      JetspeedRequestContextComponent, 
                      doParams([
                                       cmpParam(NavigationalStateComponent), 
                                       cstParam(requestContextClass), 
                                       cmpParam(UserInfoManager)
                                     ])
)


/* **********************************************************
 *  Portlet Window component                                                          *
 * ******************************************************** */
container.registerComponentImplementation(
                      PortletWindowAccessor, 
                      PortletWindowAccessorImpl, 
                      doParams([cmpParam(PortletEntityAccessComponent), cmpParam(PortletRegistryComponent)])
)

/* **********************************************************
 *  Portlet Container                                                                         *
 * ******************************************************** */
container.registerComponentInstance("Pluto", new PortletContainerImpl())

container.registerComponentImplementation(
                      PortletContainer, 
                      JetspeedPortletContainerWrapper,
                      doParams([cmpParam("Pluto")])
)

/* **********************************************************
 *  Portlet Container                                                                         *
 * ******************************************************** */
container.registerComponentImplementation(
                      PortletRenderer, 
                      PortletRendererImpl,
                      doParams([cmpParam(PortletContainer), cmpParam(PortletWindowAccessor)])
)

/* **********************************************************
 *  Aggregation                                                                                *
 * ******************************************************** */
container.registerComponentImplementation(
                      PageAggregator, 
                      PageAggregatorImpl,
                      doParams([cmpParam(PortletRenderer), cstParam(PageAggregatorImpl.STRATEGY_SEQUENTIAL)])
)
 
container.registerComponentImplementation(
                      PortletAggregator, 
                      PortletAggregatorImpl,
                      doParams([cmpParam(PortletRenderer)])
)

return container
