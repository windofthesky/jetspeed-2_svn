import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.locator.JetspeedTemplateLocator
import org.apache.jetspeed.components.ComponentAssemblyTestCase
import org.apache.jetspeed.idgenerator.JetspeedIdGenerator
import org.apache.jetspeed.page.impl.CastorXmlPageManager
import org.apache.jetspeed.Jetspeed

applicationRoot = Jetspeed.getRealPath("/")

// create the root container
container = new DefaultPicoContainer()

//
// Template Locator component assembly
//
roots = [ applicationRoot + "/WEB-INF/templates" ]
container.registerComponentInstance("TemplateLocator", new JetspeedTemplateLocator(roots))

//
// ID Generator
//
Long counterStart = 65536
peidPrefix = "P-"
peidSuffix = ""
container.registerComponentInstance("IdGenerator", new JetspeedIdGenerator(counterStart, peidPrefix, peidSuffix))

//
// Page Manager
//
root = applicationRoot + "/WEB-INF/pages"
// TODO: move this into a class loader resource
mapping = applicationRoot + "/WEB-INF/conf/page-mapping.xml"
// TODO: modelclasses, extension, scanrate, cachesize
container.registerComponentInstance("CastorXmlPageManager", 
                                     new CastorXmlPageManager(idgenerator, mapping, root))

return container
