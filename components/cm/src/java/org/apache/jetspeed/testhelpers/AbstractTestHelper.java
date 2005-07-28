package org.apache.jetspeed.testhelpers;

import java.io.File;
import java.util.Map;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public abstract class AbstractTestHelper implements TestHelper
{
    public static final String APP_CONTEXT = "AppContext";
    private final Map context;

    private static final CompositeConfiguration USER_PROPERTIES;
    static
    {
        try
        {
            File userBuildFile = new File(System.getProperty("user.home"), "build.properties");
            Configuration userBuildProps = loadConfiguration(userBuildFile);
            
            File mavenBuildFile = new File("../../build.properties");
            Configuration mavenBuildProps = loadConfiguration(userBuildFile);
            
            File mavenProjectFile = new File("../../project.properties");
            Configuration mavenProjectProps = loadConfiguration(userBuildFile);
            
            USER_PROPERTIES = new CompositeConfiguration();
            USER_PROPERTIES.addConfiguration(userBuildProps);
            USER_PROPERTIES.addConfiguration(mavenBuildProps);
            USER_PROPERTIES.addConfiguration(mavenProjectProps);            
        }
        catch (ConfigurationException e)
        {
            
           throw new IllegalStateException("Unable to load ${USER_HOME}/build.properties");
        }
    }

    private static Configuration loadConfiguration(File propsFile) throws ConfigurationException
    {
        if(propsFile.exists())
        {
            return new PropertiesConfiguration(propsFile);
        }
        else
        {
            return new PropertiesConfiguration();
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
        // use system properties passed to test via the
        // maven.junit.sysproperties configuration from
        // maven build.properties and/or project.properties
        
        String prop = System.getProperty(key);
        if(prop == null)
        {
            return (String) USER_PROPERTIES.getProperty(key);
        }
        else
        {
            return prop;
        }
    }
    
    protected final void addBeanFactory(ConfigurableBeanFactory bf)
    {
        ConfigurableBeanFactory currentBf = (ConfigurableBeanFactory) context.get(APP_CONTEXT);
        if(currentBf != null)
        {
            bf.setParentBeanFactory(currentBf);
            context.put(APP_CONTEXT, new DefaultListableBeanFactory(bf));
        }
        else
        {
            context.put(APP_CONTEXT, bf);
        }
    }

}
