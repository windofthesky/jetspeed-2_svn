package org.apache.cornerstone.framework.demo.jmx;

import java.io.IOException;

import org.apache.cornerstone.framework.api.jmx.IJMXManager;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.log4j.PropertyConfigurator;

public class Demo1
{
	public static final String REVISION = "$Revision$";

	public static void main(String[] args) throws Exception
	{
		// init log4j
		String log4jConfigFilePath = System.getProperty("log4j.configuration", "log4j.properties");
		PropertyConfigurator.configure(log4jConfigFilePath);

		// create a service instance which can be any Object
		LogService logService = new LogService(LogService.LOG_LEVEL_DEBUG);

		// create an optional service metric object if you don't expose everything of the service via JMX
		LogServiceMetric logServiceMetric = new LogServiceMetric(logService);

		// get hold of JMX manager
		IJMXManager jmxManager = (IJMXManager) Cornerstone.getManager(IJMXManager.class);

		// let logServiceMetric be managed
		jmxManager.manage(logServiceMetric);

		// you can let the service be managed directly too
		jmxManager.manage(logService);

		System.out.println("\nBrowse to http://localhost:9092");
		System.out.println("\nUse Ctrl-C to terminate or it terminates automatically in 1 hour");

		Thread.currentThread().sleep(3600000);	// 1 hour
	}
}
