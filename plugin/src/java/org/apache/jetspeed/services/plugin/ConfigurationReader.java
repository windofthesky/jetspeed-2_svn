/**
 * Created on Jul 10, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.services.plugin;

/**
 * ConfigurationReader
 * 
 * Reads an individual plugin's configuration from a piece of raw
 * data.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface ConfigurationReader
{
	PluginConfiguration buildConfiguration(Object rawConfig) throws PluginConfigurationException;
}
