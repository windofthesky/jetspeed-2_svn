package org.apache.cornerstone.framework.implementation;

import java.util.*;
import org.apache.cornerstone.framework.api.config.IConfigurable;
import org.apache.cornerstone.framework.api.factory.IFactory;
import org.apache.cornerstone.framework.api.implementation.IImplementationManager;
import org.apache.cornerstone.framework.api.implementation.ImplementationException;
import org.apache.cornerstone.framework.api.registry.IRegistry;
import org.apache.cornerstone.framework.api.registry.IRegistryEntry;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.cornerstone.framework.init.Cornerstone;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

public class BaseImplementationManager extends BaseObject implements IImplementationManager
{
	public static final String REVISION = "$Revision$";

	public static final String REGISTRY_DOMAIN_NAME = Constant.IMPLEMENTATION;
    public static final String META = "_";
    public static final String META_DOT = META + ".";
    public static final String IS_SINGLETON = "isSingleton";    // per virtual class singleton

	public static final String CONFIG_META_INSTANCE_CLASS_NAME = META_DOT + Constant.INSTANCE_CLASS_NAME;
    public static final String CONFIG_META_FACTORY_CLASS_NAME = META_DOT + Constant.FACTORY_CLASS_NAME;
    public static final String CONFIG_META_PARENT_NAME = META_DOT + Constant.PARENT_NAME;
    public static final String CONFIG_INSTANCE_IS_SINGLETON = Constant.INSTANCE + Constant.DOT + IS_SINGLETON;

	public static BaseImplementationManager getSingleton()
	{
		return _Singleton;
	}

    /* (non-Javadoc)
     * @see org.apache.cornerstone.framework.api.core.IObject#init()
     */
    public void init()
    {
        super.init();
        _implementationMap = new HashMap();
        _registry = Cornerstone.getRegistry();
    }

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.implementation.IImplementationManager#getImplementation(java.lang.String)
	 */
	public Object createImplementation(String interfaceName) throws ImplementationException
	{
		return createImplementationInternal(interfaceName, "");
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.implementation.IImplementationManager#getImplementation(java.lang.Class)
	 */
	public Object createImplementation(Class interfaceClass) throws ImplementationException
	{
		return createImplementation(interfaceClass.getName());
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.implementation.IImplementationManager#getImplementation(java.lang.String, java.lang.String)
	 */
	public Object createImplementation(String interfaceName, String variantName) throws ImplementationException
	{
		return createImplementationInternal(interfaceName, variantName);
	}

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.implementation.IImplementationManager#getImplementation(java.lang.Class, java.lang.String)
	 */
	public Object createImplementation(Class interfaceClass, String variantName) throws ImplementationException
	{
		return createImplementation(interfaceClass.getName(), variantName);
	}

    protected Object createImplementationInternal(String interfaceName, String variantName) throws ImplementationException
    {
        IRegistryEntry entry = _registry.getEntry(REGISTRY_DOMAIN_NAME, interfaceName, variantName);
        if (entry == null)
        {
            String message = "registry entry '" + REGISTRY_DOMAIN_NAME + Constant.SLASH + interfaceName + Constant.SLASH + variantName + "' undefined";
            _Logger.error(message);
            throw new ImplementationException(message);
        }

        Properties entryProperties = entry.getProperties();
        Properties metaProperties = Util.getPropertiesOfPrefix(entryProperties, META_DOT);

        Object implementation;
        String configImplementationIsSingleton = metaProperties.getProperty(CONFIG_INSTANCE_IS_SINGLETON);
        boolean implementationIsSingleton = new Boolean(configImplementationIsSingleton).booleanValue();
        if (implementationIsSingleton)
        {
            String key = getImplemenationMapKey(interfaceName, variantName);
            implementation = _implementationMap.get(key);
            if (implementation == null)
            {
                implementation = createImplementationInternalAccordingToMetaProperties(interfaceName, variantName, metaProperties, entryProperties);
                _implementationMap.put(key, implementation);    // TODO: lock
            }
            else
            {
            	if (_Logger.isDebugEnabled()) _Logger.debug("singleton of '" +key + "' already created");
            }
        }
        else
        {
        	implementation = createImplementationInternalAccordingToMetaProperties(interfaceName, variantName, metaProperties, entryProperties);
        }

        return implementation;
    }

    protected Object createImplementationInternalAccordingToMetaProperties(
        String interfaceName,
        String variantName,
        Properties metaProperties,
        Properties instanceConfig
    )
        throws ImplementationException
    {
        String instanceClassName = metaProperties.getProperty(Constant.INSTANCE_CLASS_NAME);
        if (instanceClassName != null)
        {
            // "_.instance.className" is specified, create a new instance using default constructor
            try
            {
            	Class implementationClass = Class.forName(instanceClassName);
                Object implementation = implementationClass.newInstance();
                overwriteConfig(implementation, instanceConfig);
                return implementation;
            }
            catch (Exception e)
            {
                throw new ImplementationException("failed to create instance of class '" + instanceClassName + "'", e);
            }
        }

        String factoryClassName = metaProperties.getProperty(Constant.FACTORY_CLASS_NAME);
        if (factoryClassName != null)
        {
            // "_.factory.className" is specified, create a new instance using factory
            try
            {
                IFactory factory = (IFactory) Cornerstone.getSingletonManager().getSingleton(factoryClassName);
                Object implementation = factory.createInstance();
                overwriteConfig(implementation, instanceConfig);
                return implementation;
            }
            catch (Exception e)
            {
                throw new ImplementationException("failed to create instance with factory class '" + factoryClassName + "'", e);
            }
        }

        String parentName = metaProperties.getProperty(Constant.PARENT_NAME);
        if (parentName != null)
        {
            // "_.parent.name" is specified, create a new instance using parent
            try
            {
                Object implementation = createImplementation(interfaceName, parentName);
                overwriteConfig(implementation, instanceConfig);
                return implementation;
            }
            catch (Exception e)
            {
                throw new ImplementationException("failed to create instance using parent '" + parentName + "'", e);
            }
        }

        throw new ImplementationException(
            "registry entry '" + REGISTRY_DOMAIN_NAME + Constant.SLASH + interfaceName + Constant.SLASH + variantName +
        	"' has no '" + CONFIG_META_INSTANCE_CLASS_NAME +
        	"' or '" + CONFIG_META_FACTORY_CLASS_NAME +
        	"' or '" + CONFIG_META_PARENT_NAME +
        	"' defined"
        );
    }

    /**
	 * @param interfaceName
	 * @param variantName
	 * @return
	 */
	protected String getImplemenationMapKey(String interfaceName, String variantName)
	{
		return interfaceName + Constant.SLASH + variantName;
	}

	protected void overwriteConfig(Object object, Properties config)
    {
    	if (object instanceof IConfigurable)
        {
    		IConfigurable configurable = (IConfigurable) object;
            configurable.overwriteConfig(config);
        }
    }

    private static Logger _Logger = Logger.getLogger(BaseImplementationManager.class);
    private static BaseImplementationManager _Singleton = new BaseImplementationManager();
    protected Map _implementationMap;
	protected IRegistry _registry = Cornerstone.getRegistry();
}