package org.apache.cornerstone.framework.api.persistence.connection;

import org.apache.cornerstone.framework.api.core.BaseException;

public class ConnectionException extends BaseException
{
    public static final String REVISION = "$Revision$";
    
	/**
	 * @param msg
	 */
	public ConnectionException(String msg)
	{
		super(msg);
	}

	/**
	 * @param cause
	 */
	public ConnectionException(Throwable cause)
	{
		super(cause);
	}

    /**
	 * @param msg
	 * @param cause
	 */
	public ConnectionException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}