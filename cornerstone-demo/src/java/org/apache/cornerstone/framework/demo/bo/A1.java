package org.apache.cornerstone.framework.demo.bo;

import org.apache.cornerstone.framework.demo.bo.api.IA;
import org.apache.cornerstone.framework.demo.bo.api.IB;

public class A1 implements IA
{
	public static final String REVISION = "$Revision$";

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IA#getP()
	 */
	public String getP()
	{
		return _p;
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IA#setP(java.lang.String)
	 */
	public void setP(String p)
	{
		_p = p;
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IA#getB()
	 */
	public IB getB()
	{
		return _b;
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.demo.bo.api.IA#setB(org.apache.cornerstone.framework.demo.bo.api.IB)
	 */
	public void setB(IB b)
	{
		_b = b;
	}

	protected String _p = "p";
	protected IB _b;
}