package org.apache.cornerstone.framework.init;

import org.apache.cornerstone.framework.api.core.BaseException;

public class InitException extends BaseException
{
	public static final String REVISION = "$Revision$";
    
    /**
     * @param msg
     */
    public InitException(String msg)
    {
        super(msg);
    }

    /**
     * @param cause
     */
    public InitException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param msg
     * @param cause
     */
    public InitException(String msg, Throwable cause)
    {
        super(msg, cause);
    }
}