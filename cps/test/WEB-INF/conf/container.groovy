import org.apache.jetspeed.cps.template.TemplateLocatorComponentImpl
import org.apache.jetspeed.cps.template.TemplateLocatorComponent
import org.picocontainer.defaults.DefaultPicoContainer
import org.apache.commons.configuration.PropertiesConfiguration

container = new DefaultPicoContainer()

PropertiesConfiguration locator1Conf = new PropertiesConfiguration();
locator1Conf.setProperty("roots", " WEB-INF/templates")
locator1Conf.setProperty("template.class", "org.apache.jetspeed.cps.template.TemplateImpl")
locator1Conf.setProperty("default.type", "layout")
locator1Conf.setProperty("default.template.name", "columns.vm")
locator1Conf.setProperty("default.extension", "vm")

container.registerComponentInstance("locator", new TemplateLocatorComponentImpl(locator1Conf))




return container