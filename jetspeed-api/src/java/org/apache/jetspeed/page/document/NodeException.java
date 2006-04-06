/*
 * Created on Sep 2, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.page.document;

import org.apache.jetspeed.exception.JetspeedException;

/**
 * <p>
 * NodeException
 * </p>
 * <p>
 * Note that these exceptions are assumed to be "unexpected" and/or
 * fatal; use NodeNotFoundException or other exceptions derived from
 * the base JetspeedException for "informational" exceptions.
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class NodeException extends JetspeedException
{

    /**
     * 
     */
    public NodeException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public NodeException( String message )
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param nested
     */
    public NodeException( Throwable nested )
    {
        super(nested);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msg
     * @param nested
     */
    public NodeException( String msg, Throwable nested )
    {
        super(msg, nested);
        // TODO Auto-generated constructor stub
    }

}
