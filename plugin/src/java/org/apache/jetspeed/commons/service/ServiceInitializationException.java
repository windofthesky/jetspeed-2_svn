/**
 * Created on Jul 7, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.commons.service;

import org.apache.commons.lang.exception.NestableRuntimeException;




/**
 * ServiceInitializationException
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ServiceInitializationException extends NestableRuntimeException
{

    /**
     * 
     */
    public ServiceInitializationException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg1
     */
    public ServiceInitializationException(String arg1)
    {
        super(arg1);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg1
     * @param arg2
     */
    public ServiceInitializationException(String arg1, Throwable arg2)
    {
        super(arg1, arg2);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg1
     */
    public ServiceInitializationException(Throwable arg1)
    {
        super(arg1);
        // TODO Auto-generated constructor stub
    }

}
