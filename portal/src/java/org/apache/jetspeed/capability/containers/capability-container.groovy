import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorComponentAdapter
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.defaults.ComponentParameter
import org.hsqldb.jdbcDriver
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

import org.apache.jetspeed.capability.Capabilities
import org.apache.jetspeed.capability.impl.JetspeedCapabilities

import java.io.File
import java.util.Properties

// create the root container
container = new DefaultPicoContainer()


// This is the HSQL engine that holds the test registry
if(new File("./test/db/hsql").exists())
{
   container.registerComponentInstance(new HSQLServerComponent(9001, "sa","","./test/db/hsql/Registry",false, true))
}
else
{
   container.registerComponentInstance(new HSQLServerComponent(9001, "sa","","./portal/test/db/hsql/Registry",false, true))
}

// This JNDI component helps us publish the datasource
Class jndiClass = Class.forName("org.apache.jetspeed.components.jndi.JNDIComponent")
Class tyrexJndiClass = Class.forName("org.apache.jetspeed.components.jndi.TyrexJNDIComponent")
container.registerComponentImplementation(jndiClass, tyrexJndiClass)

// Create a datasource based on the HSQL server we just created
Class dsClass = Class.forName("org.apache.jetspeed.components.datasource.DatasourceComponent")
container.registerComponentInstance(dsClass, new DBCPDatasourceComponent("sa","", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://127.0.0.1", 20, 5000, GenericObjectPool.WHEN_EXHAUSTED_GROW, true))

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
// Capabilities
//
container.registerComponentInstance(Capabilities, new JetspeedCapabilities(pContainer))

return container