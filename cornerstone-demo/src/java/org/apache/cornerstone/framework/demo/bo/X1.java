package org.apache.cornerstone.framework.demo.bo;

import org.apache.cornerstone.framework.demo.bo.api.IX;
import org.apache.cornerstone.framework.demo.bo.api.IY;

public class X1 implements IX
{
    public static final String REVISION = "$Revision$";

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IX#getP()
	 */
	public String getP()
	{
		return _p;
	}

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IX#getY()
	 */
	public IY getY()
	{
		return _y;
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IX#setP(java.lang.String)
	 */
	public void setP(String p)
	{
		_p = p;
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IX#setY(org.apache.cornerstone.framework.demo.bo.api.IY)
	 */
	public void setY(IY y)
	{
		_y = y;
	}

    protected String _p;
    protected IY _y;
}