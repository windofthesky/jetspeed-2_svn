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

import net.sf.ehcache.Cache;
import org.apache.jetspeed.components.util.ConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

    public static final String EHCACHE_JSPM_MAX_ELEMENTS_PROP_NAME = "org.apache.jetspeed.ehcache.jspm.maxelements";
    public static final String EHCACHE_JSPM_MAX_ELEMENTS_DEFAULT = "128";
    public static final String EHCACHE_JSPM_ELEMENT_TTL_PROP_NAME = "org.apache.jetspeed.ehcache.jspm.element.ttl";
    public static final String EHCACHE_JSPM_ELEMENT_TTL_DEFAULT = "150";

    // Class Members
    
    private static Map overrideSystemProperties;
    
    // Singleton implementation
    
    private static EhCacheConfigResource instance;
    
    public static synchronized EhCacheConfigResource getInstance(String defaultConfigResource, boolean test)
    {
        // construct and return a default or new instance
        if (test || (instance == null))
        {
            EhCacheConfigResource newInstance = new EhCacheConfigResource();
            newInstance.setDefaultConfigResource(defaultConfigResource);
            newInstance.setTest(test);
            newInstance.afterPropertiesSet();
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
    private String defaultJSPMMaxElements;
    private String defaultJSPMElementTTL;

    private ClassPathResource classPathResource;
    
    // InitializingBean implementation
    
    /* (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet()
    {
        synchronized (EhCacheConfigResource.class)
        {
            // set global EhCache properties configuration
            //
            // Use "classic" LRU implementation based on java's LinkedHashMap
            // with accessOrder mode enabled. Do not remove this setting casually
            // without verifying proper cache operation under load. EhCache
            // supplied LRU and LFU modes in 1.7.2 do not fare as well under load
            // and result in objects not remaining in the cache for long enough
            // intervals, especially after being forcibly expired out of the cache.
            System.setProperty(Cache.NET_SF_EHCACHE_USE_CLASSIC_LRU, "true");
            
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
                if (configuration.getString(EHCACHE_JSPM_MAX_ELEMENTS_PROP_NAME) != null)
                {
                    defaultJSPMMaxElements = configuration.getString(EHCACHE_JSPM_MAX_ELEMENTS_PROP_NAME);
                }
                if (configuration.getString(EHCACHE_JSPM_ELEMENT_TTL_PROP_NAME) != null)
                {
                    defaultJSPMElementTTL = configuration.getString(EHCACHE_JSPM_ELEMENT_TTL_PROP_NAME);
                }
            }

            // save override system properties
            if (overrideSystemProperties == null)
            {
                overrideSystemProperties = new HashMap();
                String overrideConfigResource = System.getProperty(EHCACHE_CONFIG_RESOURCE_PROP_NAME);
                if (overrideConfigResource != null)
                {
                    overrideSystemProperties.put(EHCACHE_CONFIG_RESOURCE_PROP_NAME, overrideConfigResource);
                }
                String overrideGroupAddress = System.getProperty(EHCACHE_GROUP_ADDRESS_PROP_NAME);
                if (overrideGroupAddress != null)
                {
                    overrideSystemProperties.put(EHCACHE_GROUP_ADDRESS_PROP_NAME, overrideGroupAddress);
                }
                String overrideGroupPort = System.getProperty(EHCACHE_GROUP_PORT_PROP_NAME);
                if (overrideGroupPort != null)
                {
                    overrideSystemProperties.put(EHCACHE_GROUP_PORT_PROP_NAME, overrideGroupPort);
                }
                String overrideGroupTTL = System.getProperty(EHCACHE_GROUP_TTL_PROP_NAME);
                if (overrideGroupTTL != null)
                {
                    overrideSystemProperties.put(EHCACHE_GROUP_TTL_PROP_NAME, overrideGroupTTL);
                }
                String overrideHostname = System.getProperty(EHCACHE_HOSTNAME_PROP_NAME);
                if (overrideHostname != null)
                {
                    overrideSystemProperties.put(EHCACHE_HOSTNAME_PROP_NAME, overrideHostname);
                }
                String overridePort = System.getProperty(EHCACHE_PORT_PROP_NAME);
                if (overridePort != null)
                {
                    overrideSystemProperties.put(EHCACHE_PORT_PROP_NAME, overridePort);
                }
                String overridePageManagerMaxElements = System.getProperty(EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_PROP_NAME);
                if (overridePageManagerMaxElements != null)
                {
                    overrideSystemProperties.put(EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_PROP_NAME, overridePageManagerMaxElements);
                }
                String overridePageManagerElementTTL = System.getProperty(EHCACHE_PAGE_MANAGER_ELEMENT_TTL_PROP_NAME);
                if (overridePageManagerElementTTL != null)
                {
                    overrideSystemProperties.put(EHCACHE_PAGE_MANAGER_ELEMENT_TTL_PROP_NAME, overridePageManagerElementTTL);
                }
                String overridePageManagerMaxFiles = System.getProperty(EHCACHE_PAGE_MANAGER_MAX_FILES_PROP_NAME);
                if (overridePageManagerMaxFiles != null)
                {
                    overrideSystemProperties.put(EHCACHE_PAGE_MANAGER_MAX_FILES_PROP_NAME, overridePageManagerMaxFiles);
                }
                String overrideJSPMMaxElements = System.getProperty(EHCACHE_JSPM_MAX_ELEMENTS_PROP_NAME);
                if (overrideJSPMMaxElements != null)
                {
                    overrideSystemProperties.put(EHCACHE_JSPM_MAX_ELEMENTS_PROP_NAME, overrideJSPMMaxElements);
                }
                String overrideJSPMElementTTL = System.getProperty(EHCACHE_JSPM_ELEMENT_TTL_PROP_NAME);
                if (overrideJSPMElementTTL != null)
                {
                    overrideSystemProperties.put(EHCACHE_JSPM_ELEMENT_TTL_PROP_NAME, overrideJSPMElementTTL);
                }
            }
            
            // set system properties used in global cache configuration
            String setConfigResource = (String)overrideSystemProperties.get(EHCACHE_CONFIG_RESOURCE_PROP_NAME);
            if (setConfigResource == null)
            {
                setConfigResource = ((defaultConfigResource != null) ? defaultConfigResource : EHCACHE_CONFIG_RESOURCE_DEFAULT);
            }
            System.setProperty(EHCACHE_CONFIG_RESOURCE_PROP_NAME, setConfigResource);
            String setGroupAddress = (String)overrideSystemProperties.get(EHCACHE_GROUP_ADDRESS_PROP_NAME);
            if (setGroupAddress == null)
            {
                setGroupAddress = ((defaultGroupAddress != null) ? defaultGroupAddress : EHCACHE_GROUP_ADDRESS_DEFAULT);
            }
            System.setProperty(EHCACHE_GROUP_ADDRESS_PROP_NAME, setGroupAddress);
            String setGroupPort = (String)overrideSystemProperties.get(EHCACHE_GROUP_PORT_PROP_NAME);
            if (setGroupPort == null)
            {
                setGroupPort = ((defaultGroupPort != null) ? defaultGroupPort : EHCACHE_GROUP_PORT_DEFAULT);
            }
            System.setProperty(EHCACHE_GROUP_PORT_PROP_NAME, setGroupPort);
            String setGroupTTL = (String)overrideSystemProperties.get(EHCACHE_GROUP_TTL_PROP_NAME);
            if (setGroupTTL == null)
            {
                setGroupTTL = ((defaultGroupTTL != null) ? defaultGroupTTL : (test ? EHCACHE_GROUP_TTL_TEST_DEFAULT : EHCACHE_GROUP_TTL_DEFAULT));
            }
            System.setProperty(EHCACHE_GROUP_TTL_PROP_NAME, setGroupTTL);
            String setHostname = (String)overrideSystemProperties.get(EHCACHE_HOSTNAME_PROP_NAME);
            if (setHostname == null)
            {
                setHostname = ((defaultHostname != null) ? defaultHostname : (test ? EHCACHE_HOSTNAME_TEST_DEFAULT : EHCACHE_HOSTNAME_DEFAULT));
            }
            System.setProperty(EHCACHE_HOSTNAME_PROP_NAME, setHostname);
            String setPort = (String)overrideSystemProperties.get(EHCACHE_PORT_PROP_NAME);
            if (setPort == null)
            {
                setPort = ((defaultPort != null) ? defaultPort : EHCACHE_PORT_DEFAULT);
            }
            System.setProperty(EHCACHE_PORT_PROP_NAME, setPort);

            // set system properties used in page manager cache configuration
            String setPageManagerMaxElements = (String)overrideSystemProperties.get(EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_PROP_NAME);
            if (setPageManagerMaxElements == null)
            {
                setPageManagerMaxElements = ((defaultPageManagerMaxElements != null) ? defaultPageManagerMaxElements : System.getProperty(EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_LEGACY_PROP_NAME, EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_DEFAULT));
                if ((setPageManagerMaxElements != null) && (Integer.parseInt(setPageManagerMaxElements) < 0))
                {
                    setPageManagerMaxElements = EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_DEFAULT;
                }
            }
            System.setProperty(EHCACHE_PAGE_MANAGER_MAX_ELEMENTS_PROP_NAME, setPageManagerMaxElements);
            String setPageManagerElementTTL = (String)overrideSystemProperties.get(EHCACHE_PAGE_MANAGER_ELEMENT_TTL_PROP_NAME);
            if (setPageManagerElementTTL == null)
            {
                setPageManagerElementTTL = ((defaultPageManagerElementTTL != null) ? defaultPageManagerElementTTL : System.getProperty(EHCACHE_PAGE_MANAGER_ELEMENT_TTL_LEGACY_PROP_NAME, EHCACHE_PAGE_MANAGER_ELEMENT_TTL_DEFAULT));
                if ((setPageManagerElementTTL != null) && (Integer.parseInt(setPageManagerElementTTL) < 0))
                {
                    setPageManagerElementTTL = EHCACHE_PAGE_MANAGER_ELEMENT_TTL_DEFAULT;
                }
            }
            System.setProperty(EHCACHE_PAGE_MANAGER_ELEMENT_TTL_PROP_NAME, setPageManagerElementTTL);
            String setPageManagerMaxFiles = (String)overrideSystemProperties.get(EHCACHE_PAGE_MANAGER_MAX_FILES_PROP_NAME);
            if (setPageManagerMaxFiles == null)
            {
                setPageManagerMaxFiles = ((defaultPageManagerMaxFiles != null) ? defaultPageManagerMaxFiles : System.getProperty(EHCACHE_PAGE_MANAGER_MAX_FILES_LEGACY_PROP_NAME, EHCACHE_PAGE_MANAGER_MAX_FILES_DEFAULT));
            }
            System.setProperty(EHCACHE_PAGE_MANAGER_MAX_FILES_PROP_NAME, setPageManagerMaxFiles);

            // set system properties used in jetspeed security persistence manager cache configuration
            String setJSPMMaxElements = (String)overrideSystemProperties.get(EHCACHE_JSPM_MAX_ELEMENTS_PROP_NAME);
            if (setJSPMMaxElements == null)
            {
                setJSPMMaxElements = ((defaultJSPMMaxElements != null) ? defaultJSPMMaxElements : EHCACHE_JSPM_MAX_ELEMENTS_DEFAULT);
                if ((setJSPMMaxElements != null) && (Integer.parseInt(setJSPMMaxElements) < 0))
                {
                    setJSPMMaxElements = EHCACHE_JSPM_MAX_ELEMENTS_DEFAULT;
                }
            }
            System.setProperty(EHCACHE_JSPM_MAX_ELEMENTS_PROP_NAME, setJSPMMaxElements);
            String setJSPMElementTTL = (String)overrideSystemProperties.get(EHCACHE_JSPM_ELEMENT_TTL_PROP_NAME);
            if (setJSPMElementTTL == null)
            {
                setJSPMElementTTL = ((defaultJSPMElementTTL != null) ? defaultJSPMElementTTL : EHCACHE_JSPM_ELEMENT_TTL_DEFAULT);
                if ((setJSPMElementTTL != null) && (Integer.parseInt(setJSPMElementTTL) < 0))
                {
                    setJSPMElementTTL = EHCACHE_JSPM_ELEMENT_TTL_DEFAULT;
                }
            }
            System.setProperty(EHCACHE_JSPM_ELEMENT_TTL_PROP_NAME, setJSPMElementTTL);

            // setup delegate ClassPathResource
            String configResource = System.getProperty(EHCACHE_CONFIG_RESOURCE_PROP_NAME);
            log.info("Configured with resource: "+configResource);
            classPathResource = new ClassPathResource(configResource);

            // save global instance
            instance = this;
        }
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

    /**
     * @param defaultJSPMMaxElements the defaultPageManagerMaxElements to set
     */
    public void setDefaultJSPMMaxElements(String defaultJSPMMaxElements)
    {
        this.defaultJSPMMaxElements = defaultJSPMMaxElements;
    }

    /**
     * @param defaultJSPMElementTTL the defaultJSPMElementTTL to set
     */
    public void setDefaultJSPMElementTTL(String defaultJSPMElementTTL)
    {
        this.defaultJSPMElementTTL = defaultJSPMElementTTL;
    }
}
