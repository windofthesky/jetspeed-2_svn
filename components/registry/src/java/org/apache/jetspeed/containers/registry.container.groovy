import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.defaults.ComponentParameter

import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.apache.jetspeed.components.util.system.FSSystemResourceUtilImpl

import org.apache.jetspeed.components.portletregistry.PortletRegistryComponentImpl
import org.apache.jetspeed.components.portletregistry.PortletRegistryComponent
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponentImpl

import java.io.File

/**
 * This is the standard assembly for a Regsitry
 * component.  We want the Registry component to be exposed
 * at as high the container hierarchy as possibly so, if a
 * parent container is provided, we will regsiter to the parent
 * and use it as the container for the Regsitry.
 */

// Prior to this, you will need to have an Implementation
// of org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
// registered.
if(parent != null)
{
	container = new DefaultPicoContainer(parent)
	parent.registerComponentImplementation(PortletRegistryComponent, PortletRegistryComponentImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
    parent.registerComponentImplementation(PortletEntityAccessComponent, PortletEntityAccessComponentImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
}
else
{
    container = new DefaultPicoContainer()
    container.registerComponentImplementation(PortletRegistryComponent, PortletRegistryComponentImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
    container.registerComponentImplementation(PortletEntityAccessComponent, PortletEntityAccessComponentImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
}


// This will be an empty container if "parent" was not null
return container
