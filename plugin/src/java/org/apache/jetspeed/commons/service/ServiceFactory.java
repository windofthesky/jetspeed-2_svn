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
package org.apache.jetspeed.commons.service;

import java.io.InputStream;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.fulcrum.BaseServiceBroker;

import org.apache.fulcrum.ServiceManager;

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

            setApplicationRoot("./");
            setConfiguration(conf);

            // Initialize the service manager. Services
            // that have its 'earlyInit' property set to
            // a value of 'true' will be started when
            // the service manager is initialized.
            init();

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
