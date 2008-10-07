/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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