/**
 * Created on Nov 26, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.entity;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>
 * PortletEntityNotDeletedException
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PortletEntityNotDeletedException extends JetspeedException
{

    /**
     * 
     */
    public PortletEntityNotDeletedException()
    {
        super();        
    }

    /**
     * @param message
     */
    public PortletEntityNotDeletedException(String message)
    {
        super(message);        
    }

    /**
     * @param nested
     */
    public PortletEntityNotDeletedException(Throwable nested)
    {
        super(nested);        
    }

    /**
     * @param msg
     * @param nested
     */
    public PortletEntityNotDeletedException(String msg, Throwable nested)
    {
        super(msg, nested);        
    }

}
