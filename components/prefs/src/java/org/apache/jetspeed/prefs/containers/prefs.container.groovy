import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.defaults.ComponentParameter
import org.picocontainer.defaults.ConstructorComponentAdapter

import org.apache.jetspeed.components.persistence.store.ojb.pb.PBStore
import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer

import org.apache.jetspeed.prefs.PropertyManager
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl
import org.apache.jetspeed.prefs.PreferencesProvider
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl

import java.io.File

/**
 * This is the standard assembly for a Preferences
 * component.  We want the Preferences component to be exposed
 * at as high the container hierarchy as possibly so, if a
 * parent container is provided, we will regsiter to the parent
 * and use it as the container for the Preferences.
 */

pContainer = new DefaultPersistenceStoreContainer(15000, 10000)
if (parent != null) 
{
	pContainer.setParent(parent);
}

ComponentAdapter ca = new ConstructorComponentAdapter("jetspeed", PBStore, new Parameter[] {new ConstantParameter("jetspeed")})
pContainer.registerComponent(ca)

if(parent != null)
{
	container = new DefaultPicoContainer(parent)
	parent.registerComponentInstance(PersistenceStoreContainer, pContainer);
	parent.registerComponentImplementation(PropertyManager, PropertyManagerImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
	//parent.registerComponentImplementation(PreferencesProvider, PreferencesProviderImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed"), new ConstantParameter("org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl")})
	parent.registerComponentInstance(PreferencesProvider, new PreferencesProviderImpl(pContainer, "jetspeed", "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl"))	
}
else
{
	container = new DefaultPicoContainer()
	container.registerComponentInstance(PersistenceStoreContainer, pContainer);
    container.registerComponentImplementation(PropertyManager, PropertyManagerImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
	//container.registerComponentImplementation(PreferencesProvider, PreferencesProviderImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed"), new ConstantParameter("org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl")})
	container.registerComponentInstance(PreferencesProvider, new PreferencesProviderImpl(pContainer, "jetspeed", "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl"))
}	
	
// This will be an empty container if "parent" was not null
return container