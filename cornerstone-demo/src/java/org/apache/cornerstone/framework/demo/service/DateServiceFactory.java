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