package org.apache.cornerstone.framework.api.implementation;

import org.apache.cornerstone.framework.api.core.BaseException;

public class ImplementationException extends BaseException
{
	public static final String REVISION = "$Revision$";

	/**
	 * @param msg
	 */
	public ImplementationException(String msg)
	{
		super(msg);
	}

	/**
	 * @param cause
	 */
	public ImplementationException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * @param msg
	 * @param cause
	 */
	public ImplementationException(String msg, Throwable cause)
	{
		super(msg, cause);
	}
}