import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator
import org.apache.jetspeed.page.impl.CastorXmlPageManager
import org.apache.jetspeed.components.ComponentAssemblyTestCase
import org.apache.jetspeed.cache.file.FileCache

// create the root container
container = new DefaultPicoContainer()

applicationRoot = ComponentAssemblyTestCase.getApplicationRoot("portal", "test")

//
// ID Generator
//
idgenerator = new JetspeedIdGenerator()
container.registerComponentInstance("IdGenerator", idgenerator)

//
// Page Manager
//
Long scanRate = 120
cacheSize = 100
fileCache = new FileCache(scanRate, cacheSize)
root = applicationRoot + "/testdata/pages"
container.registerComponentInstance("CastorXmlPageManager", 
                                     new CastorXmlPageManager(idgenerator, fileCache, root))

return container
