import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.jetspeed.components.jndi.JNDIComponent
import org.apache.jetspeed.components.jndi.TyrexJNDIComponent
import org.apache.jetspeed.components.datasource.BoundDBCPDatasourceComponent
import org.apache.jetspeed.components.datasource.DatasourceComponent
import org.hsqldb.jdbcDriver
import org.apache.commons.pool.impl.GenericObjectPool
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter
import java.io.File
import org.apache.jetspeed.components.MultiStartPicoContainerWrapper

// WARNING!!!!!!
// DO NOT use {Class}.class as it appears to be broken in Groovy
// You end getting a Class instance of the type java.lang.Class
// instead of the requested type!  This causes AssignabilityExceptions
// in pico.



container = new DefaultPicoContainer(parent)

// This JNDI component helps us publish the datasource
Class jndiClass = JNDIComponent
JNDIComponent jndiImpl = new TyrexJNDIComponent()
container.registerComponentInstance(jndiClass, jndiImpl)

// Create a datasource based on the HSQL server we just created
String url = System.getProperty("org.apache.jetspeed.database.url")
String driver = System.getProperty("org.apache.jetspeed.database.driver")
String user = System.getProperty("org.apache.jetspeed.database.user")
String password = System.getProperty("org.apache.jetspeed.database.password")

if(url != null)
{
	container.registerComponentInstance(DatasourceComponent, new BoundDBCPDatasourceComponent(user, password, driver, url, 20, 5000, GenericObjectPool.WHEN_EXHAUSTED_GROW, true, "jetspeed", jndiImpl))
}



return new MultiStartPicoContainerWrapper(container)