/**
 * Created on Jul 10, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.services.plugin;

/**
 * PluginConfigurationException
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PluginConfigurationException extends PluginInitializationException
{

    /**
     * 
     */
    public PluginConfigurationException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public PluginConfigurationException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param nested
     */
    public PluginConfigurationException(Throwable nested)
    {
        super(nested);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msg
     * @param nested
     */
    public PluginConfigurationException(String msg, Throwable nested)
    {
        super(msg, nested);
        // TODO Auto-generated constructor stub
    }

}
