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
package org.apache.cornerstone.framework.demo.bo.factory;

import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.demo.bo.api.IA;
import org.apache.cornerstone.framework.demo.bo.api.IB;
import org.apache.cornerstone.framework.demo.bo.factory.api.IAFactory;
import org.apache.cornerstone.framework.factory.BaseFactory;

public class AFactory extends BaseFactory implements IAFactory
{
	public static final String REVISION = "$Revision$";

	public static final String CONFIG_A_INSTANCE_CLASS_NAME = Constant.PROPERTY_DOT + "a" + Constant.DOT + Constant.INSTANCE_CLASS_NAME;
	public static final String CONFIG_B_INSTANCE_CLASS_NAME = Constant.PROPERTY_DOT + "b" + Constant.DOT + Constant.INSTANCE_CLASS_NAME;

	public static final String CONFIG_PARAMS = CONFIG_A_INSTANCE_CLASS_NAME + "," + CONFIG_B_INSTANCE_CLASS_NAME;

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.factory.IFactory#createInstance()
	 */
	public Object createInstance() throws CreationException
	{
		try
		{
			String aClassName = getConfigProperty(CONFIG_A_INSTANCE_CLASS_NAME);
			IA a = (IA) Class.forName(aClassName).newInstance();

			String bClassName = getConfigProperty(CONFIG_B_INSTANCE_CLASS_NAME);
			IB b = (IB) Class.forName(bClassName).newInstance();

			a.setB(b);

			return a;
		}
		catch (Exception e)
		{
			throw new CreationException(e);
		}
	}
}