import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator
import org.apache.jetspeed.page.impl.DatabasePageManager
import org.apache.jetspeed.components.ComponentAssemblyTestCase

// create the root container
container = new DefaultPicoContainer()

applicationRoot = ComponentAssemblyTestCase.getApplicationRoot("portal", "test")

//
// ID Generator
//
idgenerator = new JetspeedIdGenerator()
container.registerComponentInstance("IdGenerator", idgenerator)


//
// Persistence Store Service
//
// TODO: get persistence store service

//
// Page Manager
//
container.registerComponentInstance("DatabasePageManager", 
                                     new DatabasePageManager(null, idgenerator))

return container
