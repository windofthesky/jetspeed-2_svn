/*
 * Created on Jul 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.container.session;

import org.apache.jetspeed.exception.JetspeedRuntimeException;

/**
 * <p>
 * FailedToCreateNavStateException
 * </p>
 * <p>
 *  Thrown if an attempt to create a {@link NavigationalState} met with an unexpected
 *  failure. 
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class FailedToCreateNavStateException extends JetspeedRuntimeException
{

    /**
     * 
     */
    public FailedToCreateNavStateException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public FailedToCreateNavStateException( String arg0 )
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public FailedToCreateNavStateException( Throwable arg0 )
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public FailedToCreateNavStateException( String arg0, Throwable arg1 )
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}
