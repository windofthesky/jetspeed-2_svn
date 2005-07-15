package org.apache.jetspeed.testhelpers;

import java.io.File;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public abstract class AbstractTestHelper implements TestHelper
{
    public static final String BEAN_FACTORY = "bean.factory";
    private final Map context;

    private static final PropertiesConfiguration USER_PROPERTIES;
    static
    {
        try
        {
            USER_PROPERTIES= new PropertiesConfiguration(new File(System.getProperty("user.home"), "build.properties"));
        }
        catch (ConfigurationException e)
        {
            
           throw new IllegalStateException("Unable to load ${USER_HOME}/build.properties");
        }
    }   

    public AbstractTestHelper(Map context)
    {
        this.context = context;

    }

    public Map getContext()
    {
        return context;
    }

    protected final String getUserProperty(String key)
    {
        return USER_PROPERTIES.getProperty(key).toString();
    }
    
    protected final void addBeanFactory(ConfigurableBeanFactory bf)
    {
        ConfigurableBeanFactory currentBf = (ConfigurableBeanFactory) context.get(BEAN_FACTORY);
        if(currentBf != null)
        {
            bf.setParentBeanFactory(currentBf);
            context.put(BEAN_FACTORY, new DefaultListableBeanFactory(bf));
        }
        else
        {
            context.put(BEAN_FACTORY, bf);
        }
    }

}
