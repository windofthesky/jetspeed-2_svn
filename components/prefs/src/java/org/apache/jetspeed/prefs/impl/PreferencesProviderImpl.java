/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.prefs.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.picocontainer.Startable;

/**
 * @author <a href="">David Le Strat</a>
 *
 */
public class PreferencesProviderImpl implements PreferencesProvider, Startable
{
    /** Logger. */
    private static final Log log = LogFactory.getLog(PreferencesProviderImpl.class);

    /** The {@link PreferencesProvider} instance. */
    static PreferencesProvider prefProvider;


    private PersistenceStore persistenceStore;
    
    private boolean enablePropertyManager;
    
   // private List ignoredPathes;

    /**
     * <p>Constructor providing the {@link PersistenceStore} 
     * and store key name and the {@link java.util.prefs.PreferencesFactory}.</p>
     */
    public PreferencesProviderImpl(PersistenceStore persistenceStore, String prefsFactoryImpl,  boolean enablePropertyManager)
    {
        if (log.isDebugEnabled()) log.debug("Constructing PreferencesProviderImpl...");
        this.persistenceStore = persistenceStore;
        System.setProperty("java.util.prefs.PreferencesFactory", prefsFactoryImpl);
        PreferencesProviderImpl.prefProvider = this;
        this.enablePropertyManager = enablePropertyManager;
//        if(ignoredPathes != null)
//        {
//            this.ignoredPathes = Arrays.asList(ignoredPathes);
//        }
//        else
//        {
//            this.ignoredPathes = new ArrayList(0);
//        }
        
 
    }
    
    

   

    /* (non-Javadoc)
     * @see org.apache.jetspeed.prefs.PreferencesProvider#getPersistenceStore()
     */
    public PersistenceStore getPersistenceStore()
    {
        return persistenceStore;
    }



    
    /**
     * <p>
     * isPropertyManagerEnabled
     * </p>
     *
     * @see org.apache.jetspeed.prefs.PreferencesProvider#isPropertyManagerEnabled()
     * @return
     */
    public boolean isPropertyManagerEnabled()
    {      
        return enablePropertyManager;
    }
    /**
     * <p>
     * start
     * </p>
     *
     * @see org.picocontainer.Startable#start()
     * 
     */
    public void start()
    {
        // This will make sure that we are loaded into the vm immediately

    }
    /**
     * <p>
     * stop
     * </p>
     *
     * @see org.picocontainer.Startable#stop()
     * 
     */
    public void stop()
    {
       

    }
}
