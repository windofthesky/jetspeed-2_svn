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

package org.apache.jetspeed.persistence;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.services.plugin.AbstractPluginFactory;
import org.apache.jetspeed.services.plugin.Plugin;
import org.apache.jetspeed.services.plugin.PluginConfiguration;
import org.apache.jetspeed.services.plugin.PluginInitializationException;

/**
 * 
 * JetspeedPersistenceService
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class JetspeedPersistenceService extends AbstractPluginFactory implements PersistenceService
{
    private static final Log log = LogFactory.getLog(JetspeedPersistenceService.class);
    public static final String PERSISTENCE_TYPE="persistence"; 

    /**
     * @see org.apache.jetspeed.services.persistence.PersistenceService#createPersistencePlugin(org.apache.jetspeed.services.plugin.PluginConfiguration)
     */
    public PersistencePlugin createPersistencePlugin(PluginConfiguration conf) throws PluginInitializationException
    {        
        return (PersistencePlugin) createPersistencePlugin(conf);
    }

    /**
     * @see org.apache.jetspeed.services.persistence.PersistenceService#getDefaultPersistencePlugin()
     */
    public PersistencePlugin getDefaultPersistencePlugin()
    {        
        return (PersistencePlugin) getDefaultPlugin(PERSISTENCE_TYPE);
    }

    /**
     * @see org.apache.jetspeed.services.persistence.PersistenceService#getPersistencePlugin(java.lang.String)
     */
    public PersistencePlugin getPersistencePlugin(String name)
    {        
        return (PersistencePlugin) getPlugin(PERSISTENCE_TYPE, name);
    }

    /**
     * @see org.apache.jetspeed.services.plugin.AbstractPluginService#getType(org.apache.jetspeed.services.plugin.IPlugin)
     */
    protected String getType(Plugin plugin)
    {       
        return PERSISTENCE_TYPE;
    }

}
