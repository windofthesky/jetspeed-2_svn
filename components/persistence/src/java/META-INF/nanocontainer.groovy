import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.apache.jetspeed.components.persistence.store.ojb.pb.PBStore
import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.picocontainer.ComponentAdapter
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter


container = new DefaultPersistenceStoreContainer(15000, 10000)
if(parent != null) 
{
 container.setParent(parent);
 // make sure that this container is also registered as a component
 // by its interface
 parent.registerComponentInstance(PersistenceStoreContainer, container);
}

ComponentAdapter ca = new ConstructorInjectionComponentAdapter("jetspeed", PBStore, new Parameter[] {new ConstantParameter("jetspeed")})

container.registerComponent(ca)


return container