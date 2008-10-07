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