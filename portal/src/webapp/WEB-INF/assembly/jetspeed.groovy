import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorComponentAdapter
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.defaults.ComponentParameter
import org.hsqldb.jdbcDriver
import org.apache.jetspeed.locator.JetspeedTemplateLocator
import org.apache.jetspeed.components.ComponentAssemblyTestCase
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator
import org.apache.jetspeed.page.impl.CastorXmlPageManager
import org.apache.jetspeed.Jetspeed
import org.apache.jetspeed.components.hsql.HSQLServerComponent
import org.apache.jetspeed.components.hsql.HSQLServerComponent
import org.apache.jetspeed.components.jndi.JNDIComponent
import org.apache.jetspeed.components.jndi.TyrexJNDIComponent
import org.apache.jetspeed.components.datasource.DBCPDatasourceComponent
import org.apache.jetspeed.components.datasource.DatasourceComponent
import org.apache.commons.pool.impl.GenericObjectPool
import org.apache.jetspeed.components.persistence.store.ojb.OJBTypeIntializer
import org.apache.jetspeed.components.persistence.store.ojb.otm.OTMStoreImpl
import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.apache.jetspeed.components.util.system.FSSystemResourceUtilImpl

import org.apache.jetspeed.components.portletregsitry.PortletRegistryComponentImpl
import org.apache.jetspeed.components.portletregsitry.PortletRegistryComponent


// WARNING!!!!!!
// DO NOT use {Class}.class as it appears to be broken in Groovy
// You end getting a Class instance of the type java.lang.Class
// instead of the requested type!  This causes AssignabilityExceptions
// in pico.  You need to use Class.forName() instead.



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
roots = [ applicationRoot + "/WEB-INF/templates" ]
container.registerComponentInstance("TemplateLocator", new JetspeedTemplateLocator(roots))

//
// ID Generator
//
Long counterStart = 65536
peidPrefix = "P-"
peidSuffix = ""
container.registerComponentInstance("IdGenerator", new JetspeedIdGenerator(counterStart, peidPrefix, peidSuffix))

//
// Page Manager
//
root = applicationRoot + "/WEB-INF/pages"
// TODO: move this into a class loader resource
mapping = applicationRoot + "/WEB-INF/conf/page-mapping.xml"
// TODO: modelclasses, extension, scanrate, cachesize
container.registerComponentInstance("CastorXmlPageManager", 
                                     new CastorXmlPageManager(idgenerator, mapping, root))

//
// HSQL Server 
//                

container.registerComponentInstance(new HSQLServerComponent(9001, "sa","",applicationRoot+"WEB-INF/db/hsql/Registry",false, true))                     


// This JNDI component helps us publish the datasource
Class jndiClass = Class.forName("org.apache.jetspeed.components.jndi.JNDIComponent")
Class tyrexJndiClass = Class.forName("org.apache.jetspeed.components.jndi.TyrexJNDIComponent")
container.registerComponentImplementation(jndiClass, tyrexJndiClass)

// Create a datasource based on the HSQL server we just created
Class dsClass = Class.forName("org.apache.jetspeed.components.datasource.DatasourceComponent")
container.registerComponentInstance(dsClass, new DBCPDatasourceComponent("sa","", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://127.0.0.1", 5, 5000, GenericObjectPool.WHEN_EXHAUSTED_GROW, true))


//
// Persistence component
//

PersistenceContainer pContainer = new DefaultPersistenceStoreContainer(15000, 10000)

OJBTypeIntializer ojbBootstrap = new OJBTypeIntializer(resourceUtil, "WEB-INF/conf/ojb", "OJB.properties", null)

pContainer.registerComponentInstance(ojbBootstrap)

Class OTMStoreClass = Class.forName("org.apache.jetspeed.components.persistence.store.ojb.otm.OTMStoreImpl")
ComponentAdapter ca = new ConstructorComponentAdapter("jetspeed", OTMStoreClass, new Parameter[] {new ConstantParameter("jetspeed")})

pContainer.registerComponent(ca)

Class pContainerClass = Class.forName("org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer")

container.registerComponentInstance(pContainerClass, pContainer);

//
// Portlet Registry
//

Class registryClass = Class.forName("org.apache.jetspeed.components.portletregsitry.PortletRegistryComponent")
Class registryImplClass = Class.forName("org.apache.jetspeed.components.portletregsitry.PortletRegistryComponentImpl")
// Parameter[] regParams = new Parameter[] {new ComponentParameter(pContainerClass), new ConstantParameter("jetspeed")}
container.registerComponentImplementation(registryClass, registryImplClass, new Parameter[] {new ComponentParameter(pContainerClass), new ConstantParameter("jetspeed")} );

return container
