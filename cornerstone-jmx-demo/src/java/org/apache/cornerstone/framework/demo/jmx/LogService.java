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