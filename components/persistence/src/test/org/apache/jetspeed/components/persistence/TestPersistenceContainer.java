/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.components.persistence;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

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

    private ComponentManager persistenceCm;
    private ComponentManager rdbmsCm;
    private MutablePicoContainer rdbmsContainer;
    private PersistenceStoreContainer persistenceContainer;
    private DefaultPicoContainer parent;
    private PersistenceStore store;


    /**
     * @param arg0
     */
    public TestPersistenceContainer(String arg0)
    {
        super(arg0, "./src/test/Log4j.properties");
        // TODO Auto-generated constructor stub
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPersistenceContainer.class);
    }

    public void testStartContainer()
    {
        assertNotNull(rdbmsCm);
        assertNotNull(persistenceCm);
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
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Reader rdbmsScript = new InputStreamReader(cl
        .getResourceAsStream("org/apache/jetspeed/containers/rdbms.container.groovy"));
        Reader persistenceScript = new InputStreamReader(cl
        .getResourceAsStream("org/apache/jetspeed/containers/persistence.container.groovy"));
        rdbmsCm = new ComponentManager(rdbmsScript, ComponentManager.GROOVY);
        persistenceCm = new ComponentManager(persistenceScript, ComponentManager.GROOVY);
        ObjectReference parentRef = new SimpleReference();
        ObjectReference rdbmsRef = new SimpleReference();
        ObjectReference persistenceRef = new SimpleReference();
        parentRef.set(parent);
        rdbmsCm.getContainerBuilder().buildContainer(rdbmsRef, parentRef, "TEST_PERSISTENCE");
        persistenceCm.getContainerBuilder().buildContainer(persistenceRef, parentRef, "TEST_PERSISTENCE");
        rdbmsContainer = (MutablePicoContainer) rdbmsRef.get();
        persistenceContainer = (PersistenceStoreContainer) persistenceRef.get();
        store = persistenceContainer.getStoreForThread("jetspeed");
    }

    protected void tearDown() throws Exception
    {
        // parent.stop();

        rdbmsContainer.stop();
        ((MutablePicoContainer) persistenceContainer).stop();
        super.tearDown();
    }
}
