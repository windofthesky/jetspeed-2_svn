/*
 * Created on Jul 27, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.aggregator;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>
 * FailedToRenderFragmentException
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class FailedToRenderFragmentException extends JetspeedException
{

    /**
     * 
     */
    public FailedToRenderFragmentException()
    {
        super();
    }

    /**
     * @param message
     */
    public FailedToRenderFragmentException( String message )
    {
        super(message);
    }

    /**
     * @param nested
     */
    public FailedToRenderFragmentException( Throwable nested )
    {
        super(nested);
    }

    /**
     * @param msg
     * @param nested
     */
    public FailedToRenderFragmentException( String msg, Throwable nested )
    {
        super(msg, nested);
    }

}
