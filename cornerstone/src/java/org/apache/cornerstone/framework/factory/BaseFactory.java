/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.cornerstone.framework.factory;

import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.api.factory.IFactory;
import org.apache.cornerstone.framework.core.BaseObject;

public abstract class BaseFactory extends BaseObject implements IFactory
{
	public static final String REVISION = "$Revision$";

	/**
	 * Creates an instance.  Main factory method to virtualize default
	 * constructor.
	 * @return new instance created.
	*/
	public abstract Object createInstance() throws CreationException;

	/**
	 * Creates an instance given a parameter.
	 * @param Object parameter for creation.
	 * @return instance created by calling createInstance().
	*/
	public Object createInstance(Object param) throws CreationException
	{
		return createInstance();
	}
}