/*
 * Created on May 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.util;

import junit.framework.TestCase;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.jetspeed.components.datasource.BoundDBCPDatasourceComponent;
import org.apache.jetspeed.components.jndi.JNDIComponent;
import org.apache.jetspeed.components.jndi.TyrexJNDIComponent;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class DatasourceTestCase extends TestCase
{

    protected BoundDBCPDatasourceComponent datasourceComponent;
    /**
     * 
     */
    public DatasourceTestCase()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public DatasourceTestCase( String arg0 )
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }


    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {        
        super.setUp();
        JNDIComponent jndi = new TyrexJNDIComponent();
        String url = System.getProperty("org.apache.jetspeed.database.url");
        String driver = System.getProperty("org.apache.jetspeed.database.driver");
        String user = System.getProperty("org.apache.jetspeed.database.user");
        String password = System.getProperty("org.apache.jetspeed.database.password");
        datasourceComponent = new BoundDBCPDatasourceComponent(user, password, driver, url, 20, 5000, GenericObjectPool.WHEN_EXHAUSTED_GROW,  true, "jetspeed", jndi );
        datasourceComponent.start();
        
    }
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        if(datasourceComponent != null)
        {
            datasourceComponent.stop();
        }
        super.tearDown();
    }
}
