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

package org.apache.cornerstone.framework.init;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.cornerstone.framework.api.factory.IFactory;
import org.apache.cornerstone.framework.api.registry.IRegistry;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

public class Cornerstone
{
    public static final String REVISION = "$Revision$";

    public static final String CORNERSTONE_RUNTIME_HOME = "CORNERSTONE_RUNTIME_HOME";
    public static final String DEFAULT_CORNERSTONE_RUNTIME_HOME = "./";
    public static final String BOOTSTRAP_CONFIG_FILE_NAME = "bootstrap.properties";

    /**
     * Initializes the Cornerstone Framework.
     * Gets Cornerstone runtime home directory from system property <code>CORNERSTONE_RUNTIME_HOME</code>.
     * If it doesn't exist, the default value of "./" (current directory) is used.
     * @throws InitException
     */
    public static void init() throws InitException
    {
    	String runtimeHomeDir = System.getProperty(
    		CORNERSTONE_RUNTIME_HOME,
            DEFAULT_CORNERSTONE_RUNTIME_HOME
        );
        init(runtimeHomeDir);
    }

    /**
     * Initialized the Cornerstone Framework with the runtime home directory passed in.
     * @param runtimeHomeDir Cornerstone Framework runtime home directoy, where bootstrap
     * configuration files and registry are to be found.
     * @throws InitException
     */
    public static void init(String runtimeHomeDir) throws InitException
    {
    	_RuntimeHomeDir = runtimeHomeDir;
    	if (_Logger.isInfoEnabled())
    		_Logger.info(CORNERSTONE_RUNTIME_HOME + "='" + _RuntimeHomeDir + "'");

        readBootStrapProperties();
    }

    public static String getRuntimeHome()
    {
        return _RuntimeHomeDir;
    }

    /**
     * This class acts as a mini implemenation manager for bootstrap classes
     * which are defined in Cornerstone.properties and can be overridden in
     * bootstrap.properties.
     * @param iface Interface of implementation to create
     * @return Implementation of the interface
     */
    public static Object getImplementation(Class implementationInterface)
	{
    	Object implementation = _ImplementationMap.get(implementationInterface);
    	if (implementation == null)
    	{
    		Boolean implementationLoaded = (Boolean) _ImplementationLoadedMap.get(implementationInterface);
    		if (implementationLoaded == null)
    		{
    			implementation = loadImplementation(implementationInterface);
    		}
    	}
    	return implementation;
    }

    public static IRegistry getRegistry()
	{
    	String registryFactoryClassName = getImplementationFactoryClassName(IRegistry.class);
    	if (registryFactoryClassName != null)
        {
	        try
			{
				IFactory factory = (IFactory) Util.createInstance(registryFactoryClassName);
				IRegistry registry = (IRegistry) factory.createInstance(_RuntimeHomeDir);
				_ImplementationMap.put(IRegistry.class, registry);
		    	_ImplementationLoadedMap.put(IRegistry.class, Boolean.TRUE);
				return registry;
			}
			catch (Exception e)
			{
	    		_Logger.error("failed to create registry; factoryClassName=" + registryFactoryClassName);
	            return null;
			}
        }
    	else
    	{
    		_Logger.error("failed to create registry; factoryClassName=" + registryFactoryClassName);
    		return null;
    	}
    }

    protected static void readBootStrapProperties() throws InitException
    {
    	try
		{
    		// this class' own properties provide the basic values
    		Properties cornerstoneProperties = readResourceProperties(Cornerstone.class);

    		// the bootstrap properties file (if any) provides overrides for the basic values
    		String bootStrapFilePath = _RuntimeHomeDir + File.separator + BOOTSTRAP_CONFIG_FILE_NAME;
    		Properties bootStrapProperties = readFileProperties(bootStrapFilePath);

    		_BootStrapProperties = cornerstoneProperties;
    		_BootStrapProperties.putAll(bootStrapProperties);
    	}
    	catch(IOException ioe)
		{
    		throw new InitException(ioe);
    	}
    }

    protected static Properties readFileProperties(String path) throws IOException
	{
    	Properties properties = new Properties();
		try
		{
			InputStream is = new FileInputStream(path);
			properties.load(is);
			return properties;
		}
		catch (FileNotFoundException fnfe)
		{
			return properties;
		}
    }

    protected static Properties readResourceProperties(Class c) throws IOException
	{
    	String className = c.getName();
    	String classNameTail = className.substring(className.lastIndexOf('.') + 1);
    	String classPropertiesFileName = classNameTail + Constant.FILE_EXTENSION_PROPERTIES;
    	Properties properties = new Properties();
    	try
		{
    		InputStream is = c.getResourceAsStream(classPropertiesFileName);
    		properties.load(is);
    		return properties;
    	}
    	catch (FileNotFoundException fnfe)
		{
    		return properties;
    	}
    }

    protected static String getImplementationInstanceClassName(Class implementationInterface)
	{
    	String implementationClassNameConfigName = Constant.IMPLEMENTATION + Constant.SLASH + implementationInterface.getName() + Constant.SLASH + Constant.INSTANCE_CLASS_NAME;
    	String implementationClassName = _BootStrapProperties.getProperty(implementationClassNameConfigName);
    	return implementationClassName;
	}

    protected static String getImplementationFactoryClassName(Class implementationInterface)
	{
    	String implementationFactoryClassNameConfigName = Constant.IMPLEMENTATION + Constant.SLASH + implementationInterface.getName() + Constant.SLASH + Constant.FACTORY_CLASS_NAME;
    	String implementationFactoryClassName = _BootStrapProperties.getProperty(implementationFactoryClassNameConfigName);
    	return implementationFactoryClassName;
	}

    protected static Object loadImplementation(Class implementationInterface)
    {
    	String implementationClassName = getImplementationInstanceClassName(implementationInterface);
    	if (implementationClassName != null)
        {
    		// instance.className provided
	        try
			{
	        	_Logger.info("loadImplementation(): implementationClassName=" + implementationClassName);
				Object implementation = Util.createInstance(implementationClassName);
				_ImplementationMap.put(implementationInterface, implementation);
		    	_ImplementationLoadedMap.put(implementationInterface, Boolean.TRUE);
				return implementation;
			}
			catch (Exception e)
			{
	            _Logger.error("failed to create implementation of " + implementationInterface.getName(), e);
	            return null;
			}
        }

    	String implementationFactoryClassName = getImplementationFactoryClassName(implementationInterface);
    	if (implementationFactoryClassName != null)
        {
    		// factory.className provided
	        try
			{
	        	_Logger.info("loadImplementation(): implementationFactoryClassName=" + implementationFactoryClassName);
				IFactory factory = (IFactory) Util.createInstance(implementationFactoryClassName);
				Object implementation = factory.createInstance();
				_ImplementationMap.put(implementationInterface, implementation);
		    	_ImplementationLoadedMap.put(implementationInterface, Boolean.TRUE);
				return implementation;
			}
			catch (Exception e)
			{
	            _Logger.error("failed to create implementation of " + implementationInterface.getName(), e);
	            return null;
			}
        }

        _Logger.error("no instance or factory class name specified for " + implementationInterface.getName());
    	return null;
    }

    private static Logger _Logger = Logger.getLogger(Cornerstone.class);
    protected static String _RuntimeHomeDir = DEFAULT_CORNERSTONE_RUNTIME_HOME;
    protected static Properties _BootStrapProperties;
    protected static Map _ImplementationMap = new HashMap();
    protected static Map _ImplementationLoadedMap = new HashMap();

    // auto initialize so that even if .init() is not called, Cornerstone functions in a default way
    static
	{
    	try
		{
			Cornerstone.init();
		}
		catch (InitException ie)
		{
			_Logger.info("auto-init failed", ie);
		}
    }
}