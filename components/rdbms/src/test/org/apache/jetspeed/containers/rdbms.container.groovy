import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.jetspeed.components.hsql.HSQLServerComponent
import org.apache.jetspeed.components.jndi.JNDIComponent
import org.apache.jetspeed.components.jndi.TyrexJNDIComponent
import org.apache.jetspeed.components.datasource.DBCPDatasourceComponent
import org.apache.jetspeed.components.datasource.DatasourceComponent
import org.hsqldb.jdbcDriver
import org.apache.commons.pool.impl.GenericObjectPool
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorComponentAdapter

// WARNING!!!!!!
// DO NOT use {Class}.class as it appears to be broken in Groovy
// You end getting a Class instance of the type java.lang.Class
// instead of the requested type!  This causes AssignabilityExceptions
// in pico.



container = new DefaultPicoContainer()

// This is the HSQL engine that holds the test registry
container.registerComponentInstance(new HSQLServerComponent(9001, "sa","","../../portal/test/db/hsql/Registry",false, true))

// This JNDI component helps us publish the datasource
Class jndiClass = Class.forName("org.apache.jetspeed.components.jndi.JNDIComponent")
Class tyrexJndiClass = Class.forName("org.apache.jetspeed.components.jndi.TyrexJNDIComponent")
container.registerComponentImplementation(jndiClass, tyrexJndiClass)

// Create a datasource based on the HSQL server we just created
Class dsClass = Class.forName("org.apache.jetspeed.components.datasource.DatasourceComponent")
container.registerComponentInstance(dsClass, new DBCPDatasourceComponent("sa","", "org.hsqldb.jdbcDriver", "jdbc:hsqldb:hsql://127.0.0.1", 5, 5000, GenericObjectPool.WHEN_EXHAUSTED_GROW, true))



return container