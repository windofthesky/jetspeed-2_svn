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
package org.apache.jetspeed.services.plugin;


/**
 * 
 * PluginConfiguration
 * 
 * Configuration interface used with configuring <code>PersistencePlugins</code>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PluginConfiguration
{
    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String desc);

    String getClassName();

    void setClassName(String name);

    String getProperty(String name);

    String getProperty(String name, String defaultValue);

    void setProperty(String name, String value);

    boolean isDefault();

    void setDefault(boolean bool);
    
    Object getFactory();
    
    void setFactory(Object factory);

    /**
     * 
     * @return PathResolver implementation that will return absolute pathes
     * usable with URLs and URLClassLaoders.
     */
    PathResolver getPathResolver();

    /**
     * 
     * @param pathResolver PathResolver implementation that will format
     * absolute pathes usable with <code>URLs</code> and ><code>URLClassLaoders</code>.
     */
    void setPathResolver(PathResolver pathResolver);

}
