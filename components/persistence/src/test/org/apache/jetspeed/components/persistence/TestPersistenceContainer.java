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
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreEventListener;
import org.apache.jetspeed.components.persistence.store.ojb.pb.PBStore;
import org.apache.jetspeed.components.util.DatasourceTestCase;
import org.jmock.Mock;
import org.jmock.core.matcher.InvokeOnceMatcher;

/**
 * <p>
 * TestPersistenceContainer
 * </p>@
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $ $
 *  
 */
public class TestPersistenceContainer extends DatasourceTestCase
{
    
    private PersistenceStore store; 

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPersistenceContainer.class);
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
          
        }
    }
    
    public void testEvents() throws Exception
    {
        Mock mockListener = new Mock(PersistenceStoreEventListener.class);
        mockListener.expects(new InvokeOnceMatcher()).method("afterMakePersistent").isVoid();
        mockListener.expects(new InvokeOnceMatcher()).method("beforeMakePersistent").isVoid();
        mockListener.expects(new InvokeOnceMatcher()).method("afterDeletePersistent").isVoid();
        mockListener.expects(new InvokeOnceMatcher()).method("beforeDeletePersistent").isVoid();
        PersistenceStoreEventListener listener =(PersistenceStoreEventListener) mockListener.proxy();
        store.addEventListener(listener);
        
        
        store.getTransaction().begin();
        A a = new A();
        a.setName("a1");
        store.makePersistent(a);
        store.getTransaction().commit();
        
        store.getTransaction().begin();
        store.deletePersistent(a);
        store.getTransaction().commit();
        
              
        mockListener.verify();
        
        mockListener.reset();
        
        mockListener.expects(new InvokeOnceMatcher()).method("afterMakePersistent").isVoid();
        mockListener.expects(new InvokeOnceMatcher()).method("beforeMakePersistent").isVoid();
        mockListener.expects(new InvokeOnceMatcher()).method("afterDeletePersistent").isVoid();
        mockListener.expects(new InvokeOnceMatcher()).method("beforeDeletePersistent").isVoid();
        
        PersistenceStore store2 = new PBStore("jetspeed");
        store2.getTransaction().begin();
        A a2 = new A();
 
        a.setName("a2");
        store2.makePersistent(a2);
        store2.getTransaction().commit();
        
        store2.getTransaction().begin();
        store2.deletePersistent(a2);
        store2.getTransaction().commit();
        
        mockListener.verify();
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        store = new PBStore("jetspeed");        
    }
    /**
     * <p>
     * tearDown
     * </p>
     *
     * @see junit.framework.TestCase#tearDown()
     * @throws Exception
     */
    protected void tearDown() throws Exception
    {
        Filter af = store.newFilter();
        store.getTransaction().begin();
        store.deleteAll(store.newQuery(A.class, af));
        store.getTransaction().commit();

        super.tearDown();
    }
}
