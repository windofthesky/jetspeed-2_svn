/**
 * Created on Jul 10, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.services.plugin;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.InitializationException;
import org.jdom.Element;

/**
 * JDOMConfigurationReader
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JDOMConfigurationReaderImpl implements ConfigurationReader
{

	private static final Log log = LogFactory.getLog(JDOMConfigurationReaderImpl.class);

    /**
     * Generates a <code>PluginConfiguration</code> object from a
     * JDom Element.
     * @see org.apache.jetspeed.services.plugin.IConfigurationReader#buildConfiguration(java.lang.Object)
     */
    public PluginConfiguration buildConfiguration(Object rawConfig) throws PluginConfigurationException
    {
		Element xmlConf = (Element) rawConfig;
		PluginConfiguration pluginConf = new BasicPluginConfigurationImpl();

	   String name = xmlConf.getAttributeValue("name");
	   if (name == null)
	   {
		   String message = "Plugins must define a \"name\" attribute.";
		   log.error(message);
		   throw new PluginConfigurationException(message);
	   }
	   pluginConf.setName(name);
	   Element classNameE = xmlConf.getChild("classname");

	   String isDefault = xmlConf.getAttributeValue("default");
	   pluginConf.setDefault(new Boolean(isDefault).booleanValue());

	   pluginConf.setClassName(classNameE.getTextTrim());
	   Iterator propertyIterator = xmlConf.getChildren().iterator();
	   while (propertyIterator.hasNext())
	   {
		   Element prop = (Element) propertyIterator.next();
		   pluginConf.setProperty(prop.getName(), prop.getTextTrim());
	   }

	   return pluginConf;
    }

}
