package org.apache.cornerstone.framework.demo.bo;

import org.apache.cornerstone.framework.demo.bo.api.IY;

public class Y1 implements IY
{
    public static final String REVISION = "$Revision$";

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IY#getQ()
	 */
	public int getQ()
	{
		return _q;
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IY#setQ(int)
	 */
	public void setQ(int q)
	{
		_q = q;
	}

	protected int _q = 1000;
}