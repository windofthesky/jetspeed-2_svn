/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed;

import org.apache.jetspeed.components.util.RegistrySupportedTestCase;
import org.apache.jetspeed.prefs.impl.PreferencesProviderImpl;
import org.apache.jetspeed.prefs.impl.PropertyManagerImpl;

/**
 * <p>
 * AbstractPrefsSupportedTestCase
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class AbstractPrefsSupportedTestCase extends RegistrySupportedTestCase
{

    /**
     * 
     */
    public AbstractPrefsSupportedTestCase()
    {
        super();    
    }

    /**
     * @param arg0
     */
    public AbstractPrefsSupportedTestCase( String arg0 )
    {
        super(arg0);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        PropertyManagerImpl pms = new PropertyManagerImpl(persistenceStore);
        PreferencesProviderImpl provider = new PreferencesProviderImpl(persistenceStore, "org.apache.jetspeed.prefs.impl.PreferencesFactoryImpl", false);
    }

}
