import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.locator.JetspeedTemplateLocator
import org.apache.jetspeed.locator.JetspeedTemplateDescriptor
import org.apache.jetspeed.locator.JetspeedLocatorDescriptor
import org.apache.jetspeed.components.ComponentAssemblyTestCase

applicationRoot = ComponentAssemblyTestCase.getApplicationRoot("components/jetspeed", "test")

// create the root container
container = new DefaultPicoContainer()

//
// Template Locator component assembly 
//

roots = [ applicationRoot + "/WEB-INF/templates" ]
omClasses = [ org.apache.jetspeed.locator.JetspeedTemplateDescriptor,
              org.apache.jetspeed.locator.JetspeedLocatorDescriptor ]
defaultType = "email"
container.registerComponentInstance("TemplateLocator", 
                                    new JetspeedTemplateLocator(roots, omClasses, defaultType, applicationRoot))

return container
