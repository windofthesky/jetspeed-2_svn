import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.jetspeed.cps.template.TemplateLocatorComponentImpl
import org.apache.jetspeed.components.ComponentAssemblyTestCase

applicationRoot = ComponentAssemblyTestCase.getApplicationRoot("cps", "test")

// create the root container
container = new DefaultPicoContainer()

//
// Template Locator component assembly
//
roots = [ applicationRoot + "/WEB-INF/templates" ]
omClasses = [ org.apache.jetspeed.cps.template.TemplateImpl,
              org.apache.jetspeed.cps.template.TemplateLocatorImpl ]
defaultType = "email"
container.registerComponentInstance("templateLocator", 
                                    new TemplateLocatorComponentImpl(roots, omClasses, defaultType))

return container
