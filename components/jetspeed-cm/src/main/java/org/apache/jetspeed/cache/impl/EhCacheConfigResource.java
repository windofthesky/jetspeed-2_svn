/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.cache.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.InitializingBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.components.util.ConfigurationProperties;

/**
 * EhCacheConfigResource
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class EhCacheConfigResource extends AbstractResource implements InitializingBean
{
    protected static Logger log = LoggerFactory.getLogger(EhCacheConfigResource.class);
    
    // Constants
    
    public static final String EHCACHE_CONFIG_RESOURCE_PROP_NAME = "org.apache.jetspeed.ehcache.config.resource";
    public static final String EHCACHE_CONFIG_RESOURCE_DEFAULT = "ehcache.xml";
    public static final String EHCACHE_CONFIG_RESOURCE_DISTRIBUTED_CACHE = "distributed-ehcache.xml";

    public static final String EHCACHE_GROUP_ADDRESS_PROP_NAME = "org.apache.jetspeed.ehcache.group.address";
    public static final String EHCACHE_GROUP_ADDRESS_DEFAULT = "230.0.0.1";
    public static final String EHCACHE_GROUP_PORT_PROP_NAME = "org.apache.jetspeed.ehcache.group.port";
    public static final String EHCACHE_GROUP_PORT_DEFAULT = "4446";
    public static final String EHCACHE_GROUP_TTL_PROP_NAME = "org.apache.jetspeed.ehcache.group.ttl";
    public static final String EHCACHE_GROUP_TTL_DEFAULT = "1";
    public static final String EHCACHE_GROUP_TTL_TEST_DEFAULT = "0";
    public static final String EHCACHE_HOSTNAME_PROP_NAME = "org.apache.jetspeed.ehcache.hostname";
    public static final String EHCACHE_HOSTNAME_DEFAULT = "";
    public static final String EHCACHE_HOSTNAME_TEST_DEFAULT = "localhost";
    public static final String EHCACHE_PORT_PROP_NAME = "org.apache.jetspeed.ehcache.port";
    public static final String EHCACHE_PORT_DEFAULT = "40001";

    public static final String EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_LEGACY_PROP_NAME = "db.page.manager.cache.size";
    public static final String EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_PROP_NAME = "org.apache.jetspeed.ehcache.pagemanager.maxelements";
    public static final String EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_DEFAULT = "128";
    public static final String EHCACHE_PAGE_MANAGER_ELEMENT_TTL_LEGACY_PROP_NAME = "db.page.manager.cache.expire";
    public static final String EHCACHE_PAGE_MANAGER_ELEMENT_TTL_PROP_NAME = "org.apache.jetspeed.ehcache.pagemanager.element.ttl";
    public static final String EHCACHE_PAGE_MANAGER_ELEMENT_TTL_DEFAULT = "150";
    public static final String EHCACHE_PAGE_MANAGER_MAX_FILES_LEGACY_PROP_NAME = "page.file.cache.size";
    public static final String EHCACHE_PAGE_MANAGER_MAX_FILES_PROP_NAME = "org.apache.jetspeed.ehcache.pagemanager.maxfiles";
    public static final String EHCACHE_PAGE_MANAGER_MAX_FILES_DEFAULT = "1000";

    // Singleton implementation
    
    private static EhCacheConfigResource instance;
    
    public static EhCacheConfigResource getInstance(final String defaultConfigResource, final boolean test)
    {
        // construct and return a default instance
        if ( instance == null)
        {
            instance = new EhCacheConfigResource();
            instance.setDefaultConfigResource(defaultConfigResource);
            instance.setTest(test);
            instance.afterPropertiesSet();
        }
        return instance;
    }
    
    // Members

    private ConfigurationProperties configuration;
    private String defaultConfigResource;
    private boolean test;
    private String defaultGroupAddress;
    private String defaultGroupPort;
    private String defaultGroupTTL;
    private String defaultHostname;
    private String defaultPort;
    private String defaultPageManagerMaxElements;
    private String defaultPageManagerElementTTL;
    private String defaultPageManagerMaxFiles;
    
    private ClassPathResource classPathResource;
    
    // InitializingBean implementation
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
    {
        // copy specified configuration settings
        if (configuration != null)
        {
            if (configuration.getString(EHCACHE_CONFIG_RESOURCE_PROP_NAME) != null)
            {
                defaultConfigResource = configuration.getString(EHCACHE_CONFIG_RESOURCE_PROP_NAME);
            }
            if (configuration.getString(EHCACHE_GROUP_ADDRESS_PROP_NAME) != null)
            {
                defaultGroupAddress = configuration.getString(EHCACHE_GROUP_ADDRESS_PROP_NAME);
            }
            if (configuration.getString(EHCACHE_GROUP_PORT_PROP_NAME) != null)
            {
                defaultGroupPort = configuration.getString(EHCACHE_GROUP_PORT_PROP_NAME);
            }
            if (configuration.getString(EHCACHE_GROUP_TTL_PROP_NAME) != null)
            {
                defaultGroupTTL = configuration.getString(EHCACHE_GROUP_TTL_PROP_NAME);
            }
            if (configuration.getString(EHCACHE_HOSTNAME_PROP_NAME) != null)
            {
                defaultHostname = configuration.getString(EHCACHE_HOSTNAME_PROP_NAME);
            }
            if (configuration.getString(EHCACHE_PORT_PROP_NAME) != null)
            {
                defaultPort = configuration.getString(EHCACHE_PORT_PROP_NAME);
            }
            if (configuration.getString(EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_PROP_NAME) != null)
            {
                defaultPageManagerMaxElements = configuration.getString(EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_PROP_NAME);
            }
            if (configuration.getString(EHCACHE_PAGE_MANAGER_ELEMENT_TTL_PROP_NAME) != null)
            {
                defaultPageManagerElementTTL = configuration.getString(EHCACHE_PAGE_MANAGER_ELEMENT_TTL_PROP_NAME);
            }
            if (configuration.getString(EHCACHE_PAGE_MANAGER_MAX_FILES_PROP_NAME) != null)
            {
                defaultPageManagerMaxFiles = configuration.getString(EHCACHE_PAGE_MANAGER_MAX_FILES_PROP_NAME);
            }
        }
        
        // set system properties used in global cache configuration
        if (System.getProperty(EHCACHE_CONFIG_RESOURCE_PROP_NAME) == null)
        {
            System.setProperty(EHCACHE_CONFIG_RESOURCE_PROP_NAME, ((defaultConfigResource != null) ? defaultConfigResource : EHCACHE_CONFIG_RESOURCE_DEFAULT));
        }
        if (System.getProperty(EHCACHE_GROUP_ADDRESS_PROP_NAME) == null)
        {
            System.setProperty(EHCACHE_GROUP_ADDRESS_PROP_NAME, ((defaultGroupAddress != null) ? defaultGroupAddress : EHCACHE_GROUP_ADDRESS_DEFAULT));
        }
        if (System.getProperty(EHCACHE_GROUP_PORT_PROP_NAME) == null)
        {
            System.setProperty(EHCACHE_GROUP_PORT_PROP_NAME, ((defaultGroupPort != null) ? defaultGroupPort : EHCACHE_GROUP_PORT_DEFAULT));
        }
        if (System.getProperty(EHCACHE_GROUP_TTL_PROP_NAME) == null)
        {
            System.setProperty(EHCACHE_GROUP_TTL_PROP_NAME, ((defaultGroupTTL != null) ? defaultGroupTTL : (test ? EHCACHE_GROUP_TTL_TEST_DEFAULT : EHCACHE_GROUP_TTL_DEFAULT)));
        }
        if (System.getProperty(EHCACHE_HOSTNAME_PROP_NAME) == null)
        {
            System.setProperty(EHCACHE_HOSTNAME_PROP_NAME, ((defaultHostname != null) ? defaultHostname : (test ? EHCACHE_HOSTNAME_TEST_DEFAULT : EHCACHE_HOSTNAME_DEFAULT)));
        }
        if (System.getProperty(EHCACHE_PORT_PROP_NAME) == null)
        {
            System.setProperty(EHCACHE_PORT_PROP_NAME, ((defaultPort != null) ? defaultPort : EHCACHE_PORT_DEFAULT));
        }
        
        // set system properties used in page manager cache configuration
        if (System.getProperty(EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_PROP_NAME) == null)
        {
            String pageManagerMaxElements = ((defaultPageManagerMaxElements != null) ? defaultPageManagerMaxElements : System.getProperty(EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_LEGACY_PROP_NAME, EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_DEFAULT));
            if ((pageManagerMaxElements != null) && (Integer.parseInt(pageManagerMaxElements) < 0))
            {
                pageManagerMaxElements = EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_DEFAULT;
            }
            System.setProperty(EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_PROP_NAME, pageManagerMaxElements);
        }
        if (System.getProperty(EHCACHE_PAGE_MANAGER_ELEMENT_TTL_PROP_NAME) == null)
        {
            String pageManagerElementTTL = ((defaultPageManagerElementTTL != null) ? defaultPageManagerElementTTL : System.getProperty(EHCACHE_PAGE_MANAGER_ELEMENT_TTL_LEGACY_PROP_NAME, EHCACHE_PAGE_MANAGER_ELEMENT_TTL_DEFAULT));
            if ((pageManagerElementTTL != null) && (Integer.parseInt(pageManagerElementTTL) < 0))
            {
                pageManagerElementTTL = EHCACHE_PAGE_MANAGER_ELEMENT_TTL_DEFAULT;
            }
            System.setProperty(EHCACHE_PAGE_MANAGER_ELEMENT_TTL_PROP_NAME, pageManagerElementTTL);
        }
        if (System.getProperty(EHCACHE_PAGE_MANAGER_MAX_FILES_PROP_NAME) == null)
        {
            System.setProperty(EHCACHE_PAGE_MANAGER_MAX_FILES_PROP_NAME, ((defaultPageManagerMaxFiles != null) ? defaultPageManagerMaxFiles : System.getProperty(EHCACHE_PAGE_MANAGER_MAX_FILES_LEGACY_PROP_NAME, EHCACHE_PAGE_MANAGER_MAX_FILES_DEFAULT)));
        }

        // setup delegate ClassPathResource
        final String configResource = System.getProperty(EHCACHE_CONFIG_RESOURCE_PROP_NAME);
        log.info("Configured with resource: "+configResource);
        classPathResource = new ClassPathResource(configResource);
    }
    
    // AbstractResource implementation

    /* (non-Javadoc)
     * @see org.springframework.core.io.AbstractResource#createRelative(java.lang.String)
     */
    public Resource createRelative(String relativePath) throws IOException
    {
        // delegate to ClassPathResource
        return classPathResource.createRelative(relativePath);
    }

    /* (non-Javadoc)
     * @see org.springframework.core.io.AbstractResource#getFile()
     */
    public File getFile() throws IOException
    {
        // delegate to ClassPathResource
        return classPathResource.getFile();
    }

    /* (non-Javadoc)
     * @see org.springframework.core.io.AbstractResource#getFilename()
     */
    public String getFilename() throws IllegalStateException
    {
        // delegate to ClassPathResource
        return classPathResource.getFilename();
    }

    /* (non-Javadoc)
     * @see org.springframework.core.io.AbstractResource#getURL()
     */
    public URL getURL() throws IOException
    {
        // delegate to ClassPathResource
        return classPathResource.getURL();
    }

    /* (non-Javadoc)
     * @see org.springframework.core.io.Resource#getDescription()
     */
    public String getDescription()
    {
        // delegate to ClassPathResource
        return classPathResource.getDescription();
    }

    /* (non-Javadoc)
     * @see org.springframework.core.io.InputStreamSource#getInputStream()
     */
    public InputStream getInputStream() throws IOException
    {
        // delegate to ClassPathResource
        return classPathResource.getInputStream();
    }
    
    // Data access
        
    /**
     * @param configuration the configuration to set
     */
    public void setConfiguration(ConfigurationProperties configuration)
    {
        this.configuration = configuration;
    }

    /**
     * @param defaultConfigResource the defaultConfigResource to set
     */
    public void setDefaultConfigResource(String defaultConfigResource)
    {
        this.defaultConfigResource = defaultConfigResource;
    }

    /**
     * @param test the test to set
     */
    public void setTest(boolean test)
    {
        this.test = test;
    }

    /**
     * @param defaultGroupAddress the defaultGroupAddress to set
     */
    public void setDefaultGroupAddress(String defaultGroupAddress)
    {
        this.defaultGroupAddress = defaultGroupAddress;
    }

    /**
     * @param defaultGroupPort the defaultGroupPort to set
     */
    public void setDefaultGroupPort(String defaultGroupPort)
    {
        this.defaultGroupPort = defaultGroupPort;
    }

    /**
     * @param defaultGroupTTL the defaultGroupTTL to set
     */
    public void setDefaultGroupTTL(String defaultGroupTTL)
    {
        this.defaultGroupTTL = defaultGroupTTL;
    }

    /**
     * @param defaultHostname the defaultHostname to set
     */
    public void setDefaultHostname(String defaultHostname)
    {
        this.defaultHostname = defaultHostname;
    }

    /**
     * @param defaultPort the defaultPort to set
     */
    public void setDefaultPort(String defaultPort)
    {
        this.defaultPort = defaultPort;
    }

    /**
     * @param defaultPageManagerMaxElements the defaultPageManagerMaxElements to set
     */
    public void setDefaultPageManagerMaxElements(String defaultPageManagerMaxElements)
    {
        this.defaultPageManagerMaxElements = defaultPageManagerMaxElements;
    }

    /**
     * @param defaultPageManagerElementTTL the defaultPageManagerElementTTL to set
     */
    public void setDefaultPageManagerElementTTL(String defaultPageManagerElementTTL)
    {
        this.defaultPageManagerElementTTL = defaultPageManagerElementTTL;
    }

    /**
     * @param defaultPageManagerMaxFiles the defaultPageManagerMaxFiles to set
     */
    public void setDefaultPageManagerMaxFiles(String defaultPageManagerMaxFiles)
    {
        this.defaultPageManagerMaxFiles = defaultPageManagerMaxFiles;
    }
}
