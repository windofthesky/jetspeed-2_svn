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
package org.apache.jetspeed.prefs.util.test;

import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.prefs.PreferencesProvider;

/**
 * <p>
 * AbstractPrefsSupportedTestCase
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class AbstractPrefsSupportedTestCase extends DatasourceEnabledSpringTestCase
{
    protected PreferencesProvider prefsProvider;

    protected String[] getConfigurations()
    {
        return new String[]
        { "prefs.xml", "transaction.xml", "cache.xml" };
    }

    protected String[] getBootConfigurations()
    {
        return new String[]
        { "boot/datasource.xml" };
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        prefsProvider = (PreferencesProvider) ctx.getBean("prefsProvider");
    }
}
