import org.picocontainer.defaults.DefaultPicoContainer
import org.picocontainer.Parameter
import org.picocontainer.defaults.ConstantParameter
import org.picocontainer.defaults.ComponentParameter
import org.picocontainer.defaults.ConstructorComponentAdapter

import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
import org.apache.jetspeed.components.util.system.FSSystemResourceUtilImpl

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

// Prior to this, you will need to have an Implementation
// of org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer
// registered.
if(parent != null)
{
	container = new DefaultPicoContainer(parent)
	parent.registerComponentImplementation(PropertyManager, PropertyManagerImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
	ComponentAdapter ca = new ConstructorComponentAdapter(PreferencesProvider, PreferencesProviderImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed"), new ConstantParameter("org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl")})
	parent.registerComponent(ca)
}
else
{
	container = new DefaultPicoContainer()
    container.registerComponentImplementation(PropertyManager, PropertyManagerImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed")} )
	ComponentAdapter ca = new ConstructorComponentAdapter(PreferencesProvider, PreferencesProviderImpl, new Parameter[] {new ComponentParameter(PersistenceStoreContainer), new ConstantParameter("jetspeed"), new ConstantParameter("org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl")})
	container.registerComponent(ca)
}	
	
// This will be an empty container if "parent" was not null
return container