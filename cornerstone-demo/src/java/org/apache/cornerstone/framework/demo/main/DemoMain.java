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
package org.apache.cornerstone.framework.demo.main;

import org.apache.log4j.PropertyConfigurator;
import org.apache.cornerstone.framework.api.context.IContext;
import org.apache.cornerstone.framework.api.implementation.IImplementationManager;
import org.apache.cornerstone.framework.api.service.IService;
import org.apache.cornerstone.framework.api.service.IServiceManager;
import org.apache.cornerstone.framework.bean.visitor.BeanPrinter;
import org.apache.cornerstone.framework.context.BaseContext;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.cornerstone.framework.demo.bo.api.IA;
import org.apache.cornerstone.framework.demo.bo.api.IX;
import org.apache.cornerstone.framework.demo.bo.factory.api.IAFactory;
import org.apache.cornerstone.framework.demo.bo.factory.api.IXFactory;
import org.apache.cornerstone.framework.demo.service.DateService;

public class DemoMain
{
    public static final String REVISION = "$Revision$";

    public static void main(String[] args) throws Exception
    {
        // init log4j
        String log4jConfigFilePath = System.getProperty("log4j.configuration", "log4j.properties");
        PropertyConfigurator.configure(log4jConfigFilePath);

        // init Cornerstone
        Cornerstone.init();

        // ------------------------------------------------------------------------
        // Demo of calling services of the same class with different configurations

        // ServiceManager looks into
        // ${CORNERSTONE_RUNTIME_HOME}/registry/implementation/cornerstone.demo.service/cornerstone.demo.getDate.reg.properties
        // for the definition of this service
        String serviceName = "cornerstone.demo.getDate";
        IServiceManager serviceManager = (IServiceManager) Cornerstone.getImplementation(IServiceManager.class);
        IService service = serviceManager.createServiceByName(serviceName);

		// call passing no values in context
		// service will use its defaults
        IContext context = new BaseContext();
        String dateString = (String) service.invoke(context);
		printDate(serviceName, dateString, context);

		// call passing value of one of invoke_direct_inputs
        context = new BaseContext();
        context.setValue(DateService.INPUT_TIME_ZONE, "GMT-0800");	// San Jose, CA
        dateString = (String) service.invoke(context);
		printDate(serviceName, dateString, context);

		// call passing all values of invoke_direct_inputs
        context = new BaseContext();
		context.setValue(DateService.INPUT_TIME_ZONE, "GMT+0800");	// Beijing, China
		context.setValue(DateService.INPUT_DATE_FORMAT, DateService.DATE_FORMAT_SHORT);
		dateString = (String) service.invoke(context);
		printDate(serviceName, dateString, context);

		// call another instance of DateService which has different configurations
        // ${CORNERSTONE_RUNTIME_HOME}/registry/implementation/cornerstone.demo.service/cornerstone.demo.getDate2.reg.properties
		serviceName = "cornerstone.demo.getDate2";
		service = serviceManager.createServiceByName(serviceName);
		context = new BaseContext();
		dateString = (String) service.invoke(context);
		printDate(serviceName, dateString, context);

		// call yet another instance of DateService which has different configurations
        // ${CORNERSTONE_RUNTIME_HOME}/registry/implementation/cornerstone.demo.service/cornerstone.demo.getDate3.reg.properties
		serviceName = "cornerstone.demo.getDate3";
		service = serviceManager.createServiceByName(serviceName);
		context = new BaseContext();
		dateString = (String) service.invoke(context);
		printDate(serviceName, dateString, context);

        // --------------------------------------
        // Demo of calling services in a sequence

        // ${CORNERSTONE_RUNTIME_HOME}/registry/implementation/cornerstone.demo.service/cornerstone.demo.getDate1x1.reg.properties
        // Notice getDate10? has overwriting _.invokeDirect.inputs and _.invokeDirect.output and
        // "spread" the inputs and outputs around different names; otherwise the 3 getDate10?
        // services will share the same inputs and output, whihc is not desirable.  This
        // "spreading" is unnecessary if the services in the sequence are of different classes.
		serviceName = "cornerstone.demo.getDate1x1";     // name of service controller
        service = serviceManager.createServiceByName(serviceName);
        context = new BaseContext();
        context.setValue("tz102", "GMT-0800");
        context.setValue("tz103", "GMT+0800");
        context.setValue("df103", DateService.DATE_FORMAT_SHORT);
        // s1 will use defaults for both dateFormat and timeZone
        // s2 will use "tz102" passed in and default for dateFormat
        // s3 will use "tz103" and "df103" passed in
        String lastDateString = (String) service.invoke(context);
        String date101 = (String) context.getValue("date101");
        System.out.println("date101: '" + date101 + "'");
        String date102 = (String) context.getValue("date102");
        System.out.println("date102: '" + date102 + "'");
        String date103 = (String) context.getValue("date103");
        System.out.println("date103: '" + date103 + "'");

        // ----------------------------------------------------------------------------
        // Demo of indirect creation of implementations of an interface in various ways

        // get the single implementation of an factory interface
        // ${CORNERSTONE_RUNTIME_HOME}/registry/implementation/...IAFactory/.reg.properties
        IImplementationManager implementationManager = (IImplementationManager) Cornerstone.getImplementation(IImplementationManager.class);
		IAFactory aFactory = (IAFactory) implementationManager.createImplementation(IAFactory.class);
		IA a = (IA) aFactory.createInstance();
		String aPrintString = BeanPrinter.getPrintString(a);
		System.out.println("a=" + aPrintString);

        // get the "a1_viaInstanceClassName" implementation variant of interface IA
        // This variant defines how an instance should be created by using "instance.className".
        // ${CORNERSTONE_RUNTIME_HOME}/registry/implementation/...IA/a1_viaInstanceClassName.reg.properties
        IA a1_viaInstanceClassName = (IA) implementationManager.createImplementation(IA.class, "a1_viaInstanceClassName");
        String a1_viaInstanceClassNamePrintString = BeanPrinter.getPrintString(a1_viaInstanceClassName);
        System.out.println("a1_viaInstanceClassName=" + a1_viaInstanceClassNamePrintString);

        // get the "a1_viaFactoryClassName" implementation variant of interface IA
        // This variant defines how an instance should be created by using "factory.className".
        // ${CORNERSTONE_RUNTIME_HOME}/registry/implementation/...IA/a1_viaFactoryClassName.reg.properties
        IA a1_viaFactoryClassName = (IA) implementationManager.createImplementation(IA.class, "a1_viaFactoryClassName");
        String a1_viaFactoryClassNamePrintString = BeanPrinter.getPrintString(a1_viaFactoryClassName);
        System.out.println("a1_viaFactoryClassName=" + a1_viaFactoryClassNamePrintString);

        // get the "a1_viaParentName" implementation variant of interface IA
        // This variant doesn't specify either instance.className or factory.className but gets that
        // from its parent (another implementation for the same interface) specified by "parent.name".
        // ${CORNERSTONE_RUNTIME_HOME}/registry/implementation/...IA/a1_viaParentName.reg.properties
        IA a1_viaParentName = (IA) implementationManager.createImplementation(IA.class, "a1_viaParentName");
        String a1_viaParentNamePrintString = BeanPrinter.getPrintString(a1_viaParentName);
        System.out.println("a1_viaParentName=" + a1_viaParentNamePrintString);

        // ----------------------
        // Demo of an IoC Factory

        // First notice the demo.bo.api and demo.bo packages are completely independent of any framework

        // get the implementation variant "x1y1" of factory interface IXFactory and create an instance
        // the instance of X1 will be associated with an instance of Y1
        // ${CORNERSTONE_RUNTIME_HOME}/registry/implementation/...IXFactory/x1y1.reg.properties
        IXFactory xFactory = (IXFactory) implementationManager.createImplementation(IXFactory.class, "x1y1");
        IX x1y1 = (IX) xFactory.createInstance();
        String x1y1PrintString = BeanPrinter.getPrintString(x1y1);
        System.out.println("x1y1=" + x1y1PrintString);

        // get the implementation variant "x1y2" of factory interface IXFactory and create an instance
        // the instance of X1 will be associated with an instance of Y2
        // ${CORNERSTONE_RUNTIME_HOME}/registry/implementation/...IXFactory/x1y2.reg.properties
        xFactory = (IXFactory) implementationManager.createImplementation(IXFactory.class, "x1y2");
        IX x1y2 = (IX) xFactory.createInstance();
        String x1y2PrintString = BeanPrinter.getPrintString(x1y2);
        System.out.println("x1y2=" + x1y2PrintString);

        // get the implementation variant "x1y3" of factory interface IXFactory and create an instance
        // the instance of X1 will be associated with an instance of Y3
        // ${CORNERSTONE_RUNTIME_HOME}/registry/implementation/...IXFactory/x1y3.reg.properties
        xFactory = (IXFactory) implementationManager.createImplementation(IXFactory.class, "x1y3");
        IX x1y3 = (IX) xFactory.createInstance();
        String x1y3PrintString = BeanPrinter.getPrintString(x1y3);
        System.out.println("x1y3=" + x1y3PrintString);
    }

    protected static void printDate(String serviceName, String dateString, IContext context)
    {
    	String timeZoneName = DateService.INPUT_TIME_ZONE;
    	String timeZoneValue = (String) context.getValue(timeZoneName);
    	String dateFormatName = DateService.INPUT_DATE_FORMAT;
    	String dateFormatValue = (String) context.getValue(dateFormatName);

    	System.out.println(
    		serviceName +
    		" (" + timeZoneName + "=" + timeZoneValue + ", " +
    		dateFormatName + "=" + dateFormatValue + "):\n" +
    		"    " + dateString
    	);
    }
}