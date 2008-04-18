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

package org.apache.jetspeed.components.util;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

/**
 * <p>
 * ConfigurationProperties is a Spring wrapper for Commons PropertiesConfiguration
 * allowing loading multiple properties files with overriding behavior as well as
 * predefining local/inline properties to be loaded first.
 * </p>
 * <p>
 * Multiple properties files are loaded in sequence of definition and <em>copied</em>
 * over each other, instead of being added/appended as Commons Configuration does by default
 * when multiple definitions of the same property key is loaded.
 * </p>
 * @version $Id$
 *
 */
public class ConfigurationProperties extends PropertiesConfiguration implements InitializingBean
{
    private Resource[] locations;
    private Properties[] localProperties;

    public void setProperties(Properties properties) {
        this.localProperties = new Properties[] {properties};
    }

    public void setLocation(Resource location) {
        this.locations = new Resource[] {location};
    }

    public void setLocations(Resource[] locations) {
        this.locations = locations;
    }

    public void afterPropertiesSet() throws Exception
    {
        if (localProperties != null)
        {
            for (Properties props : localProperties)
            {
                for (Map.Entry entry : props.entrySet())
                {
                    setProperty((String)entry.getKey(), entry.getValue());
                }
            }
        }
        
        if (this.locations != null)
        {
            for (int i = 0; i < this.locations.length; i++)
            {
                Resource location = this.locations[i];
                InputStream is = null;
                try 
                {
                    is = location.getInputStream();
                    PropertiesConfiguration config = this;
                    if (i > 0)
                    {
                        config = new PropertiesConfiguration();
                    }
                    config.load(is);
                    if (i > 0)
                    {
                        ConfigurationUtils.copy(config,this);
                        config.clear();
                    }
                }
                finally 
                {
                    if (is != null) 
                    {
                        is.close();
                    }
                }
            }
        }
    }
}
