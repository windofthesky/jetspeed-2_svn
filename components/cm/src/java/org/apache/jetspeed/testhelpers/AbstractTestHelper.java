package org.apache.jetspeed.testhelpers;

import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public abstract class AbstractTestHelper implements TestHelper
{
    public static final String BEAN_FACTORY = "bean.factory";
    private final Map context;

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
        // use system properties passed to test via the
        // maven.junit.sysproperties configuration from
        // maven build.properties and/or project.properties
        return System.getProperty(key).toString();
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
