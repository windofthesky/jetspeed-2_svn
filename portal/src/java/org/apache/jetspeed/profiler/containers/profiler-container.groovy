import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorComponentAdapter
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.defaults.ComponentParameter
import org.hsqldb.jdbcDriver
import org.apache.jetspeed.components.jndi.JNDIComponent
import org.apache.jetspeed.components.jndi.TyrexJNDIComponent
import org.apache.jetspeed.components.datasource.BoundDBCPDatasourceComponent
import org.apache.jetspeed.components.datasource.DatasourceComponent
import org.apache.commons.pool.impl.GenericObjectPool
import org.apache.jetspeed.components.persistence.store.ojb.OJBTypeIntializer
import org.apache.jetspeed.components.persistence.store.ojb.otm.OTMStoreImpl
import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
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

// This JNDI component helps us publish the datasource
Class jndiClass = JNDIComponent
JNDIComponent jndiImpl = new TyrexJNDIComponent()
container.registerComponentInstance(jndiClass, jndiImpl)

Class dsClass = DatasourceComponent
String url = System.getProperty("org.apache.jetspeed.database.url")
String driver = System.getProperty("org.apache.jetspeed.database.driver")
String user = System.getProperty("org.apache.jetspeed.database.user")
String password = System.getProperty("org.apache.jetspeed.database.password")

if(url != null)
{
    container.registerComponentInstance(dsClass, new BoundDBCPDatasourceComponent(user, password, driver, url, 20, 5000, GenericObjectPool.WHEN_EXHAUSTED_GROW, true, "jetspeed", jndiImpl))
}

//
// Persistence
PersistenceContainer pContainer = new DefaultPersistenceStoreContainer(300000, 10000)
Class pContainerClass = Class.forName("org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer")

// Parameter[] storeParams = new Parameter[] {new ConstantParameter("jetspeed")}


Class OTMStoreClass = Class.forName("org.apache.jetspeed.components.persistence.store.ojb.otm.OTMStoreImpl")
ComponentAdapter ca = new ConstructorComponentAdapter("jetspeed", OTMStoreClass, new Parameter[] {new ConstantParameter("jetspeed")})

pContainer.registerComponent(ca)

container.registerComponentInstance(pContainerClass, pContainer);

//
// Page Manager
//
root = applicationRoot + "/testdata/pages"
Long scanRate = 120
cacheSize = 100
fileCache = new FileCache(scanRate, cacheSize)
pageManager = new CastorXmlPageManager(idgenerator, fileCache, root)
container.registerComponentInstance(PageManager, pageManager)

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

container.registerComponentInstance(Profiler, new JetspeedProfiler(pContainer, pageManager, props))

return container