import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.apache.jetspeed.components.persistence.store.ojb.OJBTypeIntializer
import org.apache.jetspeed.components.persistence.store.ojb.otm.OTMStoreImpl
import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.apache.jetspeed.components.util.system.FSSystemResourceUtilImpl
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorComponentAdapter


container = new DefaultPersistenceStoreContainer(15000, 10000)

FSSystemResourceUtilImpl resourceUtil = new FSSystemResourceUtilImpl("../../portal/src/webapp")

OJBTypeIntializer ojbBootstrap = new OJBTypeIntializer(resourceUtil, "WEB-INF/conf/ojb", "OJB.properties", null)

container.registerComponentInstance(ojbBootstrap)

// Parameter[] storeParams = new Parameter[] {new ConstantParameter("jetspeed")}


Class OTMStoreClass = Class.forName("org.apache.jetspeed.components.persistence.store.ojb.otm.OTMStoreImpl")
ComponentAdapter ca = new ConstructorComponentAdapter("jetspeed", OTMStoreClass, new Parameter[] {new ConstantParameter("jetspeed")})

container.registerComponent(ca)


return container