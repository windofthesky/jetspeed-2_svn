package org.apache.cornerstone.framework.demo.jmx;

public class LogServiceMetric
{
	public static final String REVISION = "$Revision$";

	public LogServiceMetric(LogService logService)
	{
		_logService = logService;
	}

	public int getThresholdLevel()
	{
		return _logService.getThresholdLevel();
	}

	public void setThresholdLevel(int level)
	{
		_logService.setThresholdLevel(level);
	}

	public int getLogCount()
	{
		return _logService.getLogCount();
	}

	// get jmx name (optional to override system default behavior)
	public String getName()
	{
		return "logServiceMetric";
	}

	protected LogService _logService;
}