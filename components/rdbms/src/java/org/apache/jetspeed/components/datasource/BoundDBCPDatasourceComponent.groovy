import org.apache.jetspeed.components.datasource.BoundDBCPDatasourceComponent
import org.apache.jetspeed.components.datasource.DatasourceComponent
import org.apache.jetspeed.components.jndi.JNDIComponent
import org.apache.commons.pool.impl.GenericObjectPool
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.ComponentAdapter

String url = System.getProperty("org.apache.jetspeed.database.url")
String driver = System.getProperty("org.apache.jetspeed.database.driver")
String user = System.getProperty("org.apache.jetspeed.database.user")
String password = System.getProperty("org.apache.jetspeed.database.password")

JNDIComponent jndiImpl = picoContainer.getComponentInstance(JNDIComponent)

return new BoundDBCPDatasourceComponent(user, password, driver, url, 20, 5000, GenericObjectPool.WHEN_EXHAUSTED_GROW, true, "jetspeed", jndiImpl)