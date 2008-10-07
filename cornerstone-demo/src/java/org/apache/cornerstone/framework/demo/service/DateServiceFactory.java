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

package org.apache.cornerstone.framework.demo.service;

import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.service.BaseServiceFactory;

public class DateServiceFactory extends BaseServiceFactory
{
	public static final String REVISION = "$Revision$";

    public static final String CONFIG_DATE_SERVICE_CLASS_NAME = "dateService.className";
    public static final String DEFAULT_DATE_SERVICE_CLASS_NAME = "org.apache.cornerstone.framework.demo.service.DateService";

	// ---------------
	// Factory Pattern

	public Object createInstance() throws CreationException
	{
        // get class name from configuration (DateServiceFactory.properties)
        String dateServiceClassName = getConfigPropertyWithDefault(CONFIG_DATE_SERVICE_CLASS_NAME, DEFAULT_DATE_SERVICE_CLASS_NAME);
		try
		{
			return Class.forName(dateServiceClassName).newInstance();
		}
		catch (Exception e)
		{
			throw new CreationException(e);
		}
	}

	// -----------------
	// Singleton Pattern

	public static DateServiceFactory getSingleton()
	{
		return _Singleton;
	}

	private static DateServiceFactory _Singleton = new DateServiceFactory();
}