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

package org.apache.cornerstone.framework.demo.jmx;

public class LogService
{
	public static final String REVISION = "$Revision$";

	public static final int LOG_LEVEL_DEBUG = 1000;
	public static final int LOG_LEVEL_ERROR = 2000;
	
	public LogService(int thresholdLevel)
	{
		_thresholdLevel = thresholdLevel;
	}

	public int getThresholdLevel()
	{
		return _thresholdLevel;
	}

	public void setThresholdLevel(int level)
	{
		_thresholdLevel = level;
	}

	public int getLogCount()
	{
		return _logCount;
	}

	public void debug(String message)
	{
		log(LOG_LEVEL_DEBUG, message);
	}

	public void error(String message)
	{
		log(LOG_LEVEL_ERROR, message);
	}

	public void log(int level, String message)
	{
		if (level >= _thresholdLevel)
		{
			System.out.println(message);
			_logCount++;
		}
	}

	protected int _thresholdLevel = LOG_LEVEL_DEBUG;
	protected int _logCount = 0;
}