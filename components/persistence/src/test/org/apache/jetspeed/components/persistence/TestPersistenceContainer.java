/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.persistence;
import java.net.MalformedURLException;
import java.util.ArrayList;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.NanoDeployerBasedTestSuite;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.picocontainer.MutablePicoContainer;

/**
 * <p>
 * TestPersistenceContainer
 * </p>@
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $ $
 *  
 */
public class TestPersistenceContainer extends AbstractComponentAwareTestCase
{
    
    private MutablePicoContainer rdbmsContainer;
    private PersistenceStoreContainer persistenceContainer;
    private MutablePicoContainer parent;
    private PersistenceStore store;


    /**
     * @param arg0
     */
    public TestPersistenceContainer(String arg0)
    {
        super(arg0, "./src/test/Log4j.properties");
        // TODO Auto-generated constructor stub
    }

    public static Test suite() throws MalformedURLException
    {
       // ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestPersistenceContainer.class);
       //  suite.setScript("org/apache/jetspeed/containers/test.persistence.groovy");

        NanoDeployerBasedTestSuite suite = new NanoDeployerBasedTestSuite(TestPersistenceContainer.class);
        // suite.setScript("org/apache/jetspeed/containers/rdbms.container.groovy");
        
        
        return suite;
    }

    public void testStartContainer()
    {
        assertNotNull(parent);
        assertNotNull(persistenceContainer);
        assertNotNull(store);
    }

    public void test001() throws Exception
    {
        try
        {
            assertNotNull(store);
            store.getTransaction().begin();
            A a = new A();
            a.setName("A1");
            ArrayList bList = new ArrayList(2);
            B b1 = new B();
            b1.setName("B1");
            B b2 = new B();
            b2.setName("B2");
            bList.add(b1);
            bList.add(b2);
            a.setBList(bList);
            store.makePersistent(a);
            store.getTransaction().commit();
            
            //assertNotNull(b1.getA());
            //assertNotNull(b2.getA());
            
            store.getTransaction().begin();
            store.invalidate(b1);
            store.invalidate(b2);
            store.invalidate(a);
            store.getTransaction().commit();
            
            Filter filter = store.newFilter();
            filter.addEqualTo("name", "A1");
            store.getTransaction().begin();
            try
            {
                a = (A) store.getObjectByQuery(store.newQuery(A.class, filter));
                assertNotNull(a);
                assertEquals(2, a.getBList().size());
            }
            finally
            {
                store.getTransaction().commit();
            }
            try
            {
                store.getTransaction().begin();
                Filter b1f = store.newFilter();
                b1f.addEqualTo("name", "B1");
                b1 = (B) store.getObjectByQuery(store.newQuery(B.class, b1f));
                assertNotNull(b1);
                assertNotNull(b1.getA());
            }
            finally
            {
                store.getTransaction().commit();
            }
        }
        finally
        {
            Filter af = store.newFilter();
            store.getTransaction().begin();
            store.deleteAll(store.newQuery(A.class, af));
            store.getTransaction().commit();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        parent = (MutablePicoContainer) getContainer();
        persistenceContainer = (PersistenceStoreContainer) parent.getComponentInstance(PersistenceStoreContainer.class);
        store = persistenceContainer.getStore("jetspeed");
    }
}
