import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.components.SimpleComponent

// create the root container
container = new DefaultPicoContainer()

//
// Simple Component assembly
//
container.registerComponentInstance("simple", new SimpleComponent("simple"))

return container
