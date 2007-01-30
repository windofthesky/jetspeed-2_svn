/**
 * Created on Jan 16, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.aggregator;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>
 * NoPortletAccessException
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 *
 */
public class PortletAccessDeniedException extends JetspeedException
{


    /**
     * 
     */
    public PortletAccessDeniedException()
    {
        super();
        
    }

    /**
     * @param message
     */
    public PortletAccessDeniedException(String message)
    {
        super(message);
        
    }

    /**
     * @param nested
     */
    public PortletAccessDeniedException(Throwable nested)
    {
        super(nested);
        
    }

    /**
     * @param msg
     * @param nested
     */
    public PortletAccessDeniedException(String msg, Throwable nested)
    {
        super(msg, nested);
        
    }

}
