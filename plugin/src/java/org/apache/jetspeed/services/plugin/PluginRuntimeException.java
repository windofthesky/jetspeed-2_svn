/**
 * Created on Jan 29, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.services.plugin;

import org.apache.commons.lang.exception.NestableRuntimeException;

/**
 * <p>
 * PluginRuntimeException
 * </p>

 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PluginRuntimeException extends NestableRuntimeException
{

    /**
     * 
     */
    public PluginRuntimeException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public PluginRuntimeException(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public PluginRuntimeException(Throwable arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public PluginRuntimeException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}
