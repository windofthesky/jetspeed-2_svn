import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator

// create the root container
container = new DefaultPicoContainer()

//
// ID Generator
//
Long counterStart = 65536
peidPrefix = "P-"
peidSuffix = ""
container.registerComponentInstance("IdGenerator", new JetspeedIdGenerator(counterStart, peidPrefix, peidSuffix))

return container

