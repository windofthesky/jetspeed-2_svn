/*
 * Created on May 24, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.persistence.store.util;

import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.ojb.pb.PBStore;
import org.apache.jetspeed.components.util.DatasourceTestCase;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class PersistenceSupportedTestCase extends DatasourceTestCase
{
    
    protected PersistenceStore persistenceStore;

    /**
     * 
     */
    public PersistenceSupportedTestCase()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public PersistenceSupportedTestCase( String arg0 )
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
        persistenceStore = new PBStore("jetspeed");
    }
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        persistenceStore.close();        
        persistenceStore = null;
        super.tearDown();
    }
}
