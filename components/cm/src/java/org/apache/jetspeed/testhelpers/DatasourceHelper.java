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
package org.apache.jetspeed.testhelpers;

import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * This helper adds a "datasource" based on the maven build.properties/project.properties database settings passed to
 * the test case, (see AbstractTestHelper).
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class DatasourceHelper extends AbstractTestHelper
{

    public static final String DATASOURCE_KEY = "datasource";

    private static final String ORG_APACHE_JETSPEED_TEST_DATABASE_PASSWORD = "org.apache.jetspeed.test.database.password";

    private static final String ORG_APACHE_JETSPEED_TEST_DATABASE_USER = "org.apache.jetspeed.test.database.user";

    private static final String ORG_APACHE_JETSPEED_TEST_DATABASE_URL = "org.apache.jetspeed.test.database.url";

    private static final String ORG_APACHE_JETSPEED_TEST_DATABASE_DRIVER = "org.apache.jetspeed.test.database.driver";

    protected BasicDataSource datasource;

    public DatasourceHelper(Map context)
    {
        super(context);

    }

    public void setUp() throws Exception
    {
        datasource = new BasicDataSource();
        datasource.setDriverClassName(getUserProperty(ORG_APACHE_JETSPEED_TEST_DATABASE_DRIVER));
        datasource.setUrl(getUserProperty(ORG_APACHE_JETSPEED_TEST_DATABASE_URL));
        datasource.setUsername(getUserProperty(ORG_APACHE_JETSPEED_TEST_DATABASE_USER));
        datasource.setPassword(getUserProperty(ORG_APACHE_JETSPEED_TEST_DATABASE_PASSWORD));
        getContext().put(DATASOURCE_KEY, datasource);
    }

    public void tearDown() throws Exception
    {
        datasource.close();
    }

}
