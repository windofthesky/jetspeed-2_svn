package org.apache.jetspeed.testhelpers;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import junit.framework.TestCase;


public class TestDatasourceHelper extends TestCase
{
    public void testHelper() throws Exception
    {
        Map context = new HashMap();
        DatasourceHelper helper = new DatasourceHelper(context);
        helper.setUp();
        
        DataSource ds = (DataSource) context.get(DatasourceHelper.DATASOURCE_KEY);
        assertNotNull(ds);        
        Connection conn = ds.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
        conn.close();    
        helper.tearDown();
    }
}
