import org.picocontainer.defaults.DefaultPicoContainer

import org.apache.jetspeed.components.util.NanoQuickAssembler
import java.io.File

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
// Preferences
//

NanoQuickAssembler.assemble(cl, "org/apache/jetspeed/containers/prefs.container.groovy", container)

//
// Do not forget to get component instance.
//
//container.getComponentInstance(PreferencesProvider);

return container
