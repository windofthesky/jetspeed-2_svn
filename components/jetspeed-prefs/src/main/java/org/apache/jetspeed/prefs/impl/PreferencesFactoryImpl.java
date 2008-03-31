/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.Observer;
import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

import org.apache.jetspeed.prefs.PreferencesException;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.util.PreferencesRootWrapper;

/**
 * <p>{@link java.util.prefs.PreferencesFactory} implementation to
 * return {@link PreferencesImpl}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class PreferencesFactoryImpl implements PreferencesFactory
{
    private Preferences userRootWrapper;
    private Preferences systemRootWrapper;
    private PreferencesImpl userRoot;
    private PreferencesImpl systemRoot;
    private PreferencesProvider preferencesProvider;
    
    /**
     * Java Preferences invoked constructor
     */
    public PreferencesFactoryImpl()
    {
        userRootWrapper = new PreferencesRootWrapper();
        systemRootWrapper = new PreferencesRootWrapper();
    }

    /**
     * Spring invoked constructor with a dummy parameter to distinguish it from the default constructor invoked by the Java Preferences
     * @param dummy
     */
    public PreferencesFactoryImpl(int dummy)
    {
        System.setProperty("java.util.prefs.PreferencesFactory", getClass().getName());
    }
    
    /**
     * @see java.util.prefs.PreferencesFactory#systemRoot()
     */
    public Preferences systemRoot()
    {
      return  systemRootWrapper;
    }

    /**
     * @see java.util.prefs.PreferencesFactory#userRoot()
     */
    public Preferences userRoot()
    {
        return  userRootWrapper;
    }
    
    /**
     * <p>
     * Initializes the factory.
     * </p>
     * 
     * @throws Exception
     */
    public void init() throws Exception
    {        
        try
        {   
            // Wrap the PreferencesProvider to provide a single instance to be stored in the Preferences nodes
            // which can be disposed at once for all
            PreferencesProviderWrapper ppw = new PreferencesProviderWrapper(preferencesProvider);
            preferencesProvider = null;
            userRoot = new PreferencesImpl(null, ppw, "", PreferencesImpl.USER_NODE_TYPE);
            systemRoot = new PreferencesImpl(null, ppw, "", PreferencesImpl.SYSTEM_NODE_TYPE);
            // set/update the Java Preferences userRoot and systeRoot PreferencesRootWrapper instances
            ((Observer)Preferences.userRoot()).update(null, userRoot);
            ((Observer)Preferences.systemRoot()).update(null, systemRoot);
        }
        catch(Throwable e)
        {
            throw new PreferencesException("Failed to initialize prefs api.  "+e.getMessage(), e);
        }
    }
    
    public void dispose()
    {
        ((Observer)Preferences.userRoot()).update(null, null);
        ((Observer)Preferences.systemRoot()).update(null, null);
        userRoot.disposeNode();
        systemRoot.disposeNode();
        userRoot.ppw.dispose();
        userRoot = null;
        systemRoot = null;
    }
    
    /**
     * <p>
     * Set the preferences provider.
     * </p>
     * 
     * @param preferencesProvider The {@link PreferencesProvider}
     */
    public void setPrefsProvider(PreferencesProvider preferencesProvider)
    {
        this.preferencesProvider = preferencesProvider;
    }
}
