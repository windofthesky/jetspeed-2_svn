/**
 * Created on Jul 8, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.services.plugin;


/**
 * IPlugin
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface Plugin
{
    /**
     * Performs plugin-specific initialization.
     * @param configuration <code>org.apache.commons.configuration.Configuration</code>
     * specific to this plugin's initalization.
     * @throws PluginInitializationException when there is a problem initializing the plugin.
     */
    void init(PluginConfiguration configuration) throws PluginInitializationException;

}
