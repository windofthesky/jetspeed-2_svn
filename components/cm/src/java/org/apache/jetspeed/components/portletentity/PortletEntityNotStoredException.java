/**
 * Created on Nov 26, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.components.portletentity;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>
 * PortletEntityNotStoredException
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PortletEntityNotStoredException extends JetspeedException
{

    /**
     * 
     */
    public PortletEntityNotStoredException()
    {
        super();        
    }

    /**
     * @param message
     */
    public PortletEntityNotStoredException(String message)
    {
        super(message);        
    }

    /**
     * @param nested
     */
    public PortletEntityNotStoredException(Throwable nested)
    {
        super(nested);        
    }

    /**
     * @param msg
     * @param nested
     */
    public PortletEntityNotStoredException(String msg, Throwable nested)
    {
        super(msg, nested);        
    }

}
