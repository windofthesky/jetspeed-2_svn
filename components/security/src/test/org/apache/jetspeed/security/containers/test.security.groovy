import org.picocontainer.defaults.DefaultPicoContainer

import java.io.File

import org.apache.jetspeed.components.util.NanoQuickAssembler
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl
import org.apache.jetspeed.security.impl.SecurityProviderImpl

// WARNING!!!!!!
// DO NOT use {Class}.class as it appears to be broken in Groovy
// You end getting a Class instance of the type java.lang.Class
// instead of the requested type!  This causes AssignabilityExceptions
// in pico.  You need to use Class.forName() instead.



// create the root container
container = new DefaultPicoContainer()

ClassLoader cl = Thread.currentThread().getContextClassLoader()

NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/rdbms.container.groovy", container)

NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/persistence.container.groovy", container)

//
// Preferences.
//
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/prefs/containers/prefs.container.groovy", container)

//
// Security
//
NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/security/containers/security.container.groovy", container)

// Instantiate the Preferences provider.
container.getComponentInstanceOfType(PreferencesProviderImpl)

// Instantiate the Security provider.
container.getComponentInstanceOfType(SecurityProviderImpl)


return container
