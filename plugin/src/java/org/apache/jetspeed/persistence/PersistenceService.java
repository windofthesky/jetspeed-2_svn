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

import org.apache.jetspeed.cps.CommonService;
import org.apache.jetspeed.services.plugin.PluginConfiguration;
import org.apache.jetspeed.services.plugin.PluginInitializationException;


/**
 * 
 * PersistenceService
 * 
 * Generic persistence service that uses a plug in architecture to support
 * persistence operations.  It serves as a common gateway to retreive
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface PersistenceService extends CommonService
{
    String SERVICE_NAME = "PersistenceService";

    PersistencePlugin createPersistencePlugin(PluginConfiguration conf) throws PluginInitializationException;

    /**
     * 
     * @return PersistencePlugin named as the default.  This is specified
     * in the service configuration "services.PersistenceService.default.plugin"
     */
    PersistencePlugin getDefaultPersistencePlugin();

    /**
     * You can define multiple <code>PersistencePlugin</code> classes
     * to be available through the PersistenceService.  This is done by specifing
     * the class within the <code>PersistenceService</code> configuration:
     * <br/>
     * <code>
     *   services.PersistenceService.plugin.define=myplugin
     *   services.PersistenceService.plugin.myplugin.classname=MyPersistencePlugin.class</code>
     * <br/>
     * MyPersistencePlugin.class must implement the <code>PersistencePlugin</code> interface
     * Optional initialization parameters can be passed <code>PersistencePlugin.init()</code>
     * method in the form of:
     * <code>services.PersistenceService.plugin.myplugin.someproperty=somevalue</code> 
     * 
     * @param name The name of the <code>PerisistencePlugin</code> to retreive.
     * @return PersistencePlugin associated to the <code>name</code> argument.
     */
    PersistencePlugin getPersistencePlugin(String name);

}
