import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator
import org.apache.jetspeed.page.impl.CastorXmlPageManager
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
// Page Manager
//
println("app root = " + applicationRoot)
root = applicationRoot + "/testdata/pages"
mapping = applicationRoot + "/../src/webapp/WEB-INF/conf/page-mapping.xml"
container.registerComponentInstance("CastorXmlPageManager", 
                                     new CastorXmlPageManager(idgenerator, mapping, root))

return container
