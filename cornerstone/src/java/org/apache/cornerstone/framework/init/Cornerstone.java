package org.apache.cornerstone.framework.init;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.apache.cornerstone.framework.api.action.IActionManager;
import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.api.factory.IFactory;
import org.apache.cornerstone.framework.api.implementation.IImplementationManager;
import org.apache.cornerstone.framework.api.registry.IRegistry;
import org.apache.cornerstone.framework.api.service.IServiceManager;
import org.apache.cornerstone.framework.api.singleton.ISingletonManager;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

public class Cornerstone
{
    public static final String REVISION = "$Revision$";

    public static final String CORNERSTONE_RUNTIME_HOME = "CORNERSTONE_RUNTIME_HOME";
    public static final String DEFAULT_CORNERSTONE_RUNTIME_HOME = "./";
    public static final String BOOTSTRAP_CONFIG_FILE_NAME = "bootstrap.properties";

    public static final String CONFIG_SINGLETON_MANAGER_INSTANCE_CLASS_NAME = Constant.IMPLEMENTATION + Constant.SLASH + ISingletonManager.class.getName() + Constant.SLASH + Constant.INSTANCE_CLASS_NAME;
    public static final String CONFIG_REGISTRY_FACTORY_CLASS_NAME = Constant.IMPLEMENTATION + Constant.SLASH + IRegistry.class.getName() + Constant.SLASH + Constant.FACTORY_CLASS_NAME;
    public static final String CONFIG_IMPLEMENTATION_MANAGER_INSTANCE_CLASS_NAME = Constant.IMPLEMENTATION + Constant.SLASH + IImplementationManager.class.getName() + Constant.SLASH + Constant.INSTANCE_CLASS_NAME;
    public static final String CONFIG_SERVICE_MANAGER_INSTANCE_CLASS_NAME = Constant.IMPLEMENTATION + Constant.SLASH + IServiceManager.class.getName() + Constant.SLASH + Constant.INSTANCE_CLASS_NAME;
    public static final String CONFIG_ACTION_MANAGER_INSTANCE_CLASS_NAME = Constant.IMPLEMENTATION + Constant.SLASH + IActionManager.class.getName() + Constant.SLASH + Constant.INSTANCE_CLASS_NAME;

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
        initSingletonManager();
        initRegistry();
        initImplementationManager();
        initServiceManager();
        initActionManager();
    }

    public static String getRuntimeHome()
    {
        return _RuntimeHomeDir;
    }

    public static ISingletonManager getSingletonManager()
    {
    	return _singletonManager;
    }

    public static IRegistry getRegistry()
    {
    	return _Registry;
    }

    public static IImplementationManager getImplementationManager()
    {
    	return _implementationManager;
    }

    public static IServiceManager getServiceManager()
    {
    	return _serviceManager;
    }

    public static IActionManager getActionManager()
    {
    	return _actionManager;
    }

    protected static void readBootStrapProperties() throws InitException
    {
        String bootStrapFilePath = _RuntimeHomeDir + File.separator + BOOTSTRAP_CONFIG_FILE_NAME;
        FileInputStream fis;
        try
        {
            fis = new FileInputStream(bootStrapFilePath);
            _BootStrapProperties = new Properties();
            _BootStrapProperties.load(fis);
        }
        catch (Exception e)
        {
            throw new InitException(e);
        }
    }

    protected static void initSingletonManager() throws InitException
    {
        String singletonManagerClassName = _BootStrapProperties.getProperty(CONFIG_SINGLETON_MANAGER_INSTANCE_CLASS_NAME);
        if (singletonManagerClassName == null)
        {
            String bootStrapFilePath = _RuntimeHomeDir + File.pathSeparator + BOOTSTRAP_CONFIG_FILE_NAME;
            throw new InitException("'" + CONFIG_SINGLETON_MANAGER_INSTANCE_CLASS_NAME + "' undefined in '" + bootStrapFilePath + "'");
        }
        try
		{
			_singletonManager = (ISingletonManager) Util.createInstance(singletonManagerClassName);
		}
		catch (Exception e)
		{
            throw new InitException("failed to create singleton manager", e);
		}
    }

    protected static void initRegistry() throws InitException
    {
    	String registryFactoryClassName = _BootStrapProperties.getProperty(CONFIG_REGISTRY_FACTORY_CLASS_NAME);
        if (registryFactoryClassName == null)
        {
            String bootStrapFilePath = _RuntimeHomeDir + File.pathSeparator + BOOTSTRAP_CONFIG_FILE_NAME;
            throw new InitException("'" + CONFIG_REGISTRY_FACTORY_CLASS_NAME + "' undefined in '" + bootStrapFilePath + "'");
        }
        IFactory registryFactory = (IFactory) getSingletonManager().getSingleton(registryFactoryClassName);
        try
		{
			_Registry = (IRegistry) registryFactory.createInstance(_RuntimeHomeDir);
		}
		catch (CreationException ce)
		{
			throw new InitException("failed to create registry instance", ce.getCause());
		}
    }

    protected static void initImplementationManager() throws InitException
    {
        String implementationManagerClassName = _BootStrapProperties.getProperty(CONFIG_IMPLEMENTATION_MANAGER_INSTANCE_CLASS_NAME);
        if (implementationManagerClassName == null)
        {
            String bootStrapFilePath = _RuntimeHomeDir + File.pathSeparator + BOOTSTRAP_CONFIG_FILE_NAME;
            throw new InitException("'" + CONFIG_IMPLEMENTATION_MANAGER_INSTANCE_CLASS_NAME + "' undefined in '" + bootStrapFilePath + "'");
        }
        _implementationManager = (IImplementationManager) getSingletonManager().getSingleton(implementationManagerClassName);
    }

    protected static void initServiceManager() throws InitException
    {
        String serviceManagerClassName = _BootStrapProperties.getProperty(CONFIG_SERVICE_MANAGER_INSTANCE_CLASS_NAME);
        if (serviceManagerClassName == null)
        {
            String bootStrapFilePath = _RuntimeHomeDir + File.pathSeparator + BOOTSTRAP_CONFIG_FILE_NAME;
            throw new InitException("'" + CONFIG_SERVICE_MANAGER_INSTANCE_CLASS_NAME + "' undefined in '" + bootStrapFilePath + "'");
        }
        _serviceManager = (IServiceManager) getSingletonManager().getSingleton(serviceManagerClassName);
    }

    protected static void initActionManager() throws InitException
    {
        String actionManagerClassName = _BootStrapProperties.getProperty(CONFIG_ACTION_MANAGER_INSTANCE_CLASS_NAME);
        if (actionManagerClassName == null)
        {
            String bootStrapFilePath = _RuntimeHomeDir + File.pathSeparator + BOOTSTRAP_CONFIG_FILE_NAME;
            throw new InitException("'" + CONFIG_ACTION_MANAGER_INSTANCE_CLASS_NAME + "' undefined in '" + bootStrapFilePath + "'");
        }
        _actionManager = (IActionManager) getSingletonManager().getSingleton(actionManagerClassName);
    }

    private static Logger _Logger = Logger.getLogger(Cornerstone.class);
    protected static String _RuntimeHomeDir = DEFAULT_CORNERSTONE_RUNTIME_HOME;
    protected static Properties _BootStrapProperties;

    protected static ISingletonManager _singletonManager;
    protected static IRegistry _Registry;
    protected static IImplementationManager _implementationManager;
    protected static IServiceManager _serviceManager;
    protected static IActionManager _actionManager;
}