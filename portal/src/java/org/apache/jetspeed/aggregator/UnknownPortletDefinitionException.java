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
 * UnknownPortletDefinitionException
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class UnknownPortletDefinitionException extends JetspeedException
{


    /**
     * 
     */
    public UnknownPortletDefinitionException()
    {
        super();
        
    }

    /**
     * @param message
     */
    public UnknownPortletDefinitionException(String message)
    {
        super(message);
        
    }

    /**
     * @param nested
     */
    public UnknownPortletDefinitionException(Throwable nested)
    {
        super(nested);
        
    }

    /**
     * @param msg
     * @param nested
     */
    public UnknownPortletDefinitionException(String msg, Throwable nested)
    {
        super(msg, nested);
        
    }

}
