import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.apache.jetspeed.components.persistence.store.ojb.pb.PBStore
import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter


container = new DefaultPersistenceStoreContainer(parent, 15000, 10000)

ComponentAdapter ca = new ConstructorInjectionComponentAdapter("jetspeed", PBStore, new Parameter[] {new ConstantParameter("jetspeed")})

container.registerComponent(ca)

if(parent != null)
{
	parent.registerComponentInstance(PersistenceStoreContainer, container)
}


return container