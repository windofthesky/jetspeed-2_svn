import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.locator.JetspeedTemplateLocator
import org.apache.jetspeed.components.ComponentAssemblyTestCase
import org.apache.jetspeed.Jetspeed

applicationRoot = Jetspeed.getRealPath("/")

// create the root container
container = new DefaultPicoContainer()

//
// Template Locator component assembly JetspeedTemplateLocator.class.toString()
//

roots = [ applicationRoot + "/WEB-INF/templates" ]
container.registerComponentInstance("TemplateLocator", new JetspeedTemplateLocator(roots))

return container
