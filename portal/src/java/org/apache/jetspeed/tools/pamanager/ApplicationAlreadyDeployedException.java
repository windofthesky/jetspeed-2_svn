/*
 * Created on Feb 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.tools.pamanager;

/**
 * <p>
 * ApplicationAlreadyDeployedException
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ApplicationAlreadyDeployedException extends DeploymentException
{

    /**
     * 
     */
    public ApplicationAlreadyDeployedException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param message
     */
    public ApplicationAlreadyDeployedException( String message )
    {
        super(message);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param nested
     */
    public ApplicationAlreadyDeployedException( Throwable nested )
    {
        super(nested);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msg
     * @param nested
     */
    public ApplicationAlreadyDeployedException( String msg, Throwable nested )
    {
        super(msg, nested);
        // TODO Auto-generated constructor stub
    }

}
