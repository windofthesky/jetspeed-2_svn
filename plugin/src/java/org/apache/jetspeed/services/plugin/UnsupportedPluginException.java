/**
 * Created on Jul 9, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.services.plugin;

/**
 * UnsupportedPluginException
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class UnsupportedPluginException extends PluginInitializationException
{

    /**
     * 
     */
    public UnsupportedPluginException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public UnsupportedPluginException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param nested
     */
    public UnsupportedPluginException(Throwable nested)
    {
        super(nested);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msg
     * @param nested
     */
    public UnsupportedPluginException(String msg, Throwable nested)
    {
        super(msg, nested);
        // TODO Auto-generated constructor stub
    }

}
