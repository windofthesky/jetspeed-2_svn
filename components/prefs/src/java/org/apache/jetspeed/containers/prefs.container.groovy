import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.defaults.ConstantParameter
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
import org.apache.jetspeed.components.persistence.store.ojb.pb.PBStore
import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.apache.jetspeed.components.util.system.FSSystemResourceUtilImpl

import org.apache.jetspeed.prefs.PropertyManager
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl
import org.apache.jetspeed.prefs.PreferencesProvider
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl

import java.io.File

// WARNING!!!!!!
// DO NOT use {Class}.class as it appears to be broken in Groovy
// You end getting a Class instance of the type java.lang.Class
// instead of the requested type!  This causes AssignabilityExceptions
// in pico.  You need to use Class.forName() instead.



// create the root container
container = new DefaultPicoContainer()


// This JNDI component helps us publish the datasource
Class jndiClass = JNDIComponent
JNDIComponent jndiImpl = new TyrexJNDIComponent()
container.registerComponentInstance(jndiClass, jndiImpl)

String url = System.getProperty("org.apache.jetspeed.database.url")
String driver = System.getProperty("org.apache.jetspeed.database.driver")
String user = System.getProperty("org.apache.jetspeed.database.user")
String password = System.getProperty("org.apache.jetspeed.database.password")

if(url != null)
{
	container.registerComponentInstance(DatasourceComponent, new BoundDBCPDatasourceComponent(user, password, driver, url, 20, 5000, GenericObjectPool.WHEN_EXHAUSTED_GROW, true, "jetspeed", jndiImpl))
}

//
// Persistence
PersistenceStoreContainer pContainer = new DefaultPersistenceStoreContainer(300000, 10000)

ComponentAdapter ca = new ConstructorComponentAdapter("jetspeed", PBStore, new Parameter[] {new ConstantParameter("jetspeed")})

pContainer.registerComponent(ca)

container.registerComponentInstance(PersistenceStoreContainer, pContainer);

container.registerComponentInstance(PreferencesProvider, new PreferencesProviderImpl(pContainer, "jetspeed", "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl"))

//
// Property Manager
//
container.registerComponentImplementation(PropertyManager, PropertyManagerImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} );

return container
