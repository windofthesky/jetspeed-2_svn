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

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

import org.apache.jetspeed.prefs.PreferencesException;
import org.apache.jetspeed.prefs.PreferencesProvider;

/**
 * <p>{@link java.util.prefs.PreferencesFactory} implementation to
 * return {@link PreferencesImpl}.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PreferencesFactoryImpl implements PreferencesFactory
{
    
    
    protected static PreferencesProvider prefsProvider;

    public PreferencesFactoryImpl()
    {
        super();        
        System.setProperty("java.util.prefs.PreferencesFactory", getClass().getName());
    }
    
     

    /**
     * @see java.util.prefs.PreferencesFactory#systemRoot()
     */
    public Preferences systemRoot()
    {
      return  PreferencesImpl.systemRoot;
    }

    /**
     * @see java.util.prefs.PreferencesFactory#userRoot()
     */
    public Preferences userRoot()
    {
        return  PreferencesImpl.userRoot;
    }
    
    public void init() throws Exception
    {        
        try
        {           
              PreferencesImpl.setPreferencesProvider(prefsProvider);
              PreferencesImpl.systemRoot = new PreferencesImpl(null, "", PreferencesImpl.SYSTEM_NODE_TYPE);
              PreferencesImpl.userRoot =  new PreferencesImpl(null, "", PreferencesImpl.USER_NODE_TYPE);            
        }
        catch(Throwable e)
        {
            throw new PreferencesException("Failed to initialize prefs api.  "+e.toString());
        }
    }
    
    

    public PreferencesProvider getPrefsProvider()
    {
        return prefsProvider;
    }
    
    public void setPrefsProvider( PreferencesProvider prefsProvider )
    {
        this.prefsProvider = prefsProvider;
    }
}
