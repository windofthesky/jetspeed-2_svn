/**
 * Created on May 29, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.services.persistence.impl;

import org.apache.commons.lang.exception.NestableRuntimeException;


/**
 * Thrown when an unexpected error, unrelated to application 
 * logic, happens while using OJB.
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class JetspeedOJBRuntimeException extends NestableRuntimeException
{

    /**
     * 
     */
    public JetspeedOJBRuntimeException()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public JetspeedOJBRuntimeException(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public JetspeedOJBRuntimeException(Throwable arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public JetspeedOJBRuntimeException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

}
