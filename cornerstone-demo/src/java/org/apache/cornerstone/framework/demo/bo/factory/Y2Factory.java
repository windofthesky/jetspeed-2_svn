package org.apache.cornerstone.framework.demo.bo.factory;

import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.factory.BaseFactory;
import org.apache.cornerstone.framework.demo.bo.Y2;

public class Y2Factory extends BaseFactory
{
    public static final String REVISION = "$Revision$";

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.factory.IFactory#createInstance()
	 */
	public Object createInstance() throws CreationException
	{
		return new Y2();
	}
}