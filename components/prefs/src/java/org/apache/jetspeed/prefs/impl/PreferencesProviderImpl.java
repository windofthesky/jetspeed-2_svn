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

import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author <a href="">David Le Strat</a>
 *
 */
public class PreferencesProviderImpl implements PreferencesProvider
{
    /** Logger. */
    private static final Log log = LogFactory.getLog(PreferencesProviderImpl.class);

    /** The {@link PreferencesProvider} instance. */
    static PreferencesProvider prefProvider;

    /** The persistence store container. */
    private PersistenceStoreContainer storeContainer;

    /** The store name. */
    private String storeKeyName;

    /**
     * <p>Constructor providing the {@link PersistenceStoreContainer} 
     * and store key name and the {@link java.util.prefs.PreferencesFactory}.</p>
     */
    public PreferencesProviderImpl(PersistenceStoreContainer storeContainer, String storeKeyName, String prefsFactoryImpl)
    {
        if (log.isDebugEnabled()) log.debug("Constructing PreferencesProviderImpl...");
        this.storeContainer = storeContainer;
        this.storeKeyName = storeKeyName;
        System.setProperty("java.util.prefs.PreferencesFactory", prefsFactoryImpl);
        PreferencesProviderImpl.prefProvider = this;

    }

    /**
     * @see org.apache.jetspeed.prefs.PreferencesProvider#getStoreContainer()
     */
    public PersistenceStoreContainer getStoreContainer()
    {
        return this.storeContainer;
    }

    /**
     * @see org.apache.jetspeed.prefs.PreferencesProvider#getStoreKeyName()
     */
    public String getStoreKeyName()
    {
        return this.storeKeyName;
    }

}
