package org.apache.cornerstone.framework.demo.bo;

import org.apache.cornerstone.framework.demo.bo.api.IB;

public class B1 implements IB
{
	public static final String REVISION = "$Revision$";

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IB#getQ()
	 */
	public int getQ()
	{
		return _q;
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IB#setQ(java.lang.String)
	 */
	public void setQ(int q)
	{
		_q = q;
	}

	protected int _q = 100;
}