import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorComponentAdapter
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.defaults.ComponentParameter
import org.apache.jetspeed.locator.JetspeedTemplateLocator
import org.apache.jetspeed.components.ComponentAssemblyTestCase
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator
import org.apache.jetspeed.page.PageManager
import org.apache.jetspeed.page.impl.CastorXmlPageManager
import org.apache.jetspeed.Jetspeed
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.apache.jetspeed.components.util.system.FSSystemResourceUtilImpl


import org.apache.jetspeed.cache.file.FileCache
import org.apache.jetspeed.profiler.Profiler
import org.apache.jetspeed.profiler.impl.JetspeedProfiler
import org.apache.jetspeed.capability.Capabilities
import org.apache.jetspeed.capability.impl.JetspeedCapabilities

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

return container
