import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
//import org.apache.jetspeed.components.persistence.store.ojb.OJBTypeIntializer
//import org.apache.jetspeed.components.persistence.store.ojb.otm.OTMStoreImpl
import org.apache.jetspeed.components.persistence.store.ojb.pb.PBStore
import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.apache.jetspeed.components.util.system.FSSystemResourceUtilImpl
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorComponentAdapter

container = new DefaultPersistenceStoreContainer(15000, 10000)

// Class OTMStoreClass = Class.forName("org.apache.jetspeed.components.persistence.store.ojb.otm.OTMStoreImpl")
ComponentAdapter ca = new ConstructorComponentAdapter("jetspeed", PBStore, new Parameter[] {new ConstantParameter("jetspeed")})

container.registerComponent(ca)


return container