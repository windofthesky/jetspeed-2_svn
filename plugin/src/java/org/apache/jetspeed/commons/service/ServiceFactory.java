/**
 * Created on Jul 7, 2003
 *
 * 
 * @author
 */
package org.apache.jetspeed.commons.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.fulcrum.BaseServiceBroker;
import org.apache.fulcrum.InitializationException;
import org.apache.fulcrum.ServiceManager;
import org.apache.fulcrum.TurbineServices;

/**
 * <p>
 * ServiceFactory
 * </p>
 * <p>
 * The service factory can be used if the system in which the
 * peristence service is used has no running fulcrum impplementation.
 * </p>
 * <p>
 * </p>
 * 
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ServiceFactory extends BaseServiceBroker implements ServiceManager
{
    private static ServiceFactory instance;

    protected ServiceFactory() throws ServiceInitializationException
    {
        super();
        try
        {

            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream propsPath = cl.getResourceAsStream("org/apache/jetspeed/services/plugin/configuration/service.properties");
            PropertiesConfiguration conf = new PropertiesConfiguration();
            conf.load(propsPath);

            instance.setConfiguration(conf);

            // Initialize the service manager. Services
            // that have its 'earlyInit' property set to
            // a value of 'true' will be started when
            // the service manager is initialized.
            instance.init();

        }
        catch (Exception e)
        {
            throw new ServiceInitializationException("Unable to initialize service factory.", e);
        }

    }

    public static ServiceFactory getInstance()
    {
        if (instance == null)
        {
            instance = new ServiceFactory();
        }

        return instance;
    }    

}
