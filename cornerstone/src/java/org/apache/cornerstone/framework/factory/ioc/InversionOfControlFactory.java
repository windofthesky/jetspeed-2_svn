package org.apache.cornerstone.framework.factory.ioc;

import java.beans.PropertyDescriptor;
import java.util.Enumeration;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.api.factory.IFactory;
import org.apache.cornerstone.framework.api.implementation.ImplementationException;
import org.apache.cornerstone.framework.bean.helper.BeanHelper;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.factory.BaseFactory;
import org.apache.cornerstone.framework.init.Cornerstone;

/**
 * 
 * Sample config: MyFactory.properties
 * product.instance.className=MyA1
 * product.property.b.instance.className=MyB2
 * product.property.c.factory.className=MyC1Factory 
 * product.property.x.parent.name=myObj1            ### will look under property's interface type
 * product.property.y.factory.parent.name=yFactory  ### will look under cornerstone.factory
 */

public class InversionOfControlFactory extends BaseFactory
{
    public static final String REVISION = "$Revision$";

    public static final String VALUE = "value";

    public static final String CONFIG_PRODUCT_INSTANCE_CLASS_NAME = Constant.PRODUCT_DOT + Constant.INSTANCE_CLASS_NAME;
    public static final String CONFIG_PRODUCT_PROPERTY_DOT = Constant.PRODUCT_DOT + Constant.PROPERTY_DOT;

    /* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.factory.IFactory#createInstance()
	 */
	public Object createInstance() throws CreationException
	{
        // create product instance
        String productInstanceClassName = _config.getProperty(CONFIG_PRODUCT_INSTANCE_CLASS_NAME);
        if (productInstanceClassName == null)
            throw new CreationException("config '" + CONFIG_PRODUCT_INSTANCE_CLASS_NAME + "' missing");
        Object product = createInstanceByClassName(productInstanceClassName);

        // set properties
        for (Enumeration e = _config.keys(); e.hasMoreElements();)
        {
        	String configName = (String) e.nextElement();
            if (configName.startsWith(CONFIG_PRODUCT_PROPERTY_DOT))
            {
                String propertyConfigName = configName.substring(CONFIG_PRODUCT_PROPERTY_DOT.length());
                int dot = propertyConfigName.indexOf(Constant.DOT);
                String propertyName = propertyConfigName.substring(0, dot);
                String instanceSpecName = propertyConfigName.substring(dot + 1);
                String instanceSpecValue = _config.getProperty(configName);
                Object propertyValue = createProperty(product, propertyName, instanceSpecName, instanceSpecValue);
                BeanHelper.getSingleton().setProperty(product, propertyName, propertyValue);
            }
        }

        return product;
	}

    protected Object createProperty(
        Object product,
        String propertyName,
        String instanceSpecName,
        String instanceSpecValue
    )
        throws CreationException
    {
    	if (Constant.INSTANCE_CLASS_NAME.equals(instanceSpecName))
        {
            Object propertyValue = createInstanceByClassName(instanceSpecValue);
            return propertyValue;
        }
        else if (Constant.FACTORY_CLASS_NAME.equals(instanceSpecName))
        {
            Object propertyValue = createInstanceByFactoryClassName(instanceSpecValue);
            return propertyValue;
        }
        else if (Constant.PARENT_NAME.equals(instanceSpecName))
        {
            Object propertyValue = createInstanceByParentName(product, propertyName, instanceSpecValue);
            return propertyValue;
        }
        else if (VALUE.equals(instanceSpecName))
        {
        	return instanceSpecValue;
        }
        else
        {
        	throw new CreationException(
        		"instanceSpecName '" + instanceSpecName + "' of property '" +
                propertyName + "' not understood;" +
        		"allowed: '" + Constant.INSTANCE_CLASS_NAME +
        		"' and '" + Constant.FACTORY_CLASS_NAME +
        		"' and '" + Constant.PARENT_NAME +
        		"'"
            );
        }
    }

    protected Object createInstanceByClassName(String className) throws CreationException
    {
        try
        {
            Object instance = Class.forName(className).newInstance();
            return instance;
        }
        catch (Exception e)
        {
            throw new CreationException("failed to create instance of class '" + className + "'", e);
        }
    }

    protected Object createInstanceByFactoryClassName(String factoryClassName) throws CreationException
    {
    	IFactory factory = (IFactory) Cornerstone.getSingletonManager().getSingleton(factoryClassName);
        if (factory == null)
            throw new CreationException("singleton of class '" + factoryClassName + "' not found");
        Object product = factory.createInstance();
        return product;
    }

    protected Object createInstanceByParentName(Object product, String propertyName, String parentName) throws CreationException
    {
        try
        {
            PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(product, propertyName);
            Class interfaceType = pd.getPropertyType();
            if (!interfaceType.isInterface())
                throw new CreationException("property '" + propertyName + "' of class '" + product.getClass().getName() + "' should be an interface type");
            Object propertyValue = Cornerstone.getImplementationManager().createImplementation(interfaceType, parentName);
            return propertyValue;
        }
        catch (ImplementationException ie)
        {
            throw new CreationException(ie.getCause());
        }
        catch (Exception e)
        {
            throw new CreationException(e);
        }
    }
}