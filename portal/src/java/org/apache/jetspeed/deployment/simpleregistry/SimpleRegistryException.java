/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.simpleregistry;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>
 * SimpleRegistryException
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class SimpleRegistryException extends JetspeedException
{

    /**
     * 
     */
    public SimpleRegistryException()
    {
        super();        
    }

    /**
     * @param message
     */
    public SimpleRegistryException(String message)
    {
        super(message);        
    }

    /**
     * @param nested
     */
    public SimpleRegistryException(Throwable nested)
    {
        super(nested);        
    }

    /**
     * @param msg
     * @param nested
     */
    public SimpleRegistryException(String msg, Throwable nested)
    {
        super(msg, nested);        
    }

}
