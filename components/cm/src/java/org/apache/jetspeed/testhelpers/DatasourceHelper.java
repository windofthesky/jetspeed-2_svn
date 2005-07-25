package org.apache.jetspeed.testhelpers;

import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * This helper adds a "datasource" based on the maven
 * build.properties/project.properties database settings
 * passed to the test case, (see AbstractTestHelper).
 * 
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class DatasourceHelper extends AbstractTestHelper
{

    public static final String DATASOURCE_KEY = "datasource";
    private static final String ORG_APACHE_JETSPEED_DATABASE_PASSWORD = "org.apache.jetspeed.database.password";
    private static final String ORG_APACHE_JETSPEED_DATABASE_USER = "org.apache.jetspeed.database.user";
    private static final String ORG_APACHE_JETSPEED_DATABASE_URL = "org.apache.jetspeed.database.url";
    private static final String ORG_APACHE_JETSPEED_DATABASE_DRIVER = "org.apache.jetspeed.database.driver";
    protected BasicDataSource datasource;
    
    public DatasourceHelper(Map context)
    {
        super(context);
        
    }

    public void setUp() throws Exception
    {
        datasource = new BasicDataSource();
        datasource.setDriverClassName(getUserProperty(ORG_APACHE_JETSPEED_DATABASE_DRIVER));
        datasource.setUrl(getUserProperty(ORG_APACHE_JETSPEED_DATABASE_URL));
        datasource.setUsername(getUserProperty(ORG_APACHE_JETSPEED_DATABASE_USER));
        datasource.setPassword(getUserProperty(ORG_APACHE_JETSPEED_DATABASE_PASSWORD));
        getContext().put(DATASOURCE_KEY, datasource);
    }

    public void tearDown() throws Exception
    {
        datasource.close();
    }

   

}
