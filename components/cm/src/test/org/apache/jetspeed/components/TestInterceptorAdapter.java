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
package org.apache.jetspeed.components;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.adapters.InterceptorAdapter;
import org.apache.jetspeed.components.adapters.StandardDelegationStrategy;
import org.apache.jetspeed.components.adapters.ThreadLocalDelegationStrategy;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ConstructorComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.defaults.Swappable;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 *  
 */
public class TestInterceptorAdapter extends TestCase
{

    protected static final String SINGLETON_KEY = "singleton";
    static TestSuite suite;

    /**
     *  
     */
    public TestInterceptorAdapter()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public TestInterceptorAdapter( String arg0 )
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        suite = new TestSuite(TestInterceptorAdapter.class);
        return suite;
    }

    public void testStandardDelegation() throws Exception
    {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        MockComponent c1 = new BaseMockComponent(1, "c1");
        MockComponent c2 = new BaseMockComponent(2, "c2");

        pico.registerComponent(new InterceptorAdapter(
                new InstanceComponentAdapter(MockComponent.class, c1),
                StandardDelegationStrategy.class));

        MockComponent testComponent = (MockComponent) pico
                .getComponentInstance(MockComponent.class);
        assertNotNull(testComponent);

        // Should not be the actual instance, but a dynamic proxy
        assertFalse(testComponent instanceof BaseMockComponent);
        
        assertTrue(testComponent.getValue1() == 1
                && testComponent.getValue2().equals("c1"));

        // Are we Swappable???
        assertTrue(testComponent instanceof Swappable);

        // Now test our hot swapping
        ((Swappable) testComponent).__hotSwap(c2);

        assertTrue(testComponent.getValue1() == 2
                && testComponent.getValue2().equals("c2"));
    }

    public void testThreadLocalDelegation() throws Exception
    {
        Thread.currentThread().setName("Thread 1");
        DefaultPicoContainer pico = new DefaultPicoContainer();

        ConstructorComponentAdapter cca = new ConstructorComponentAdapter(
                MockComponent.class, BaseMockComponent.class, new Parameter[]{
                        new ConstantParameter(new Integer(1)),
                        new ConstantParameter("c1")});

        InterceptorAdapter ia = new InterceptorAdapter(cca,
                ThreadLocalDelegationStrategy.class);

        CachingComponentAdapter cacheAdptr = new CachingComponentAdapter(ia);

        pico.registerComponent(cacheAdptr);
        pico.start();

        //Make sure that caching is working as expected
        assertTrue(pico.getComponentInstance(MockComponent.class) == pico
                .getComponentInstance(MockComponent.class));
        MockComponent comp = (MockComponent) pico
                .getComponentInstance(MockComponent.class);
        assertEquals("Thread 1", comp.getThreadName());
        ThreadTest th1 = new ThreadTest(pico);
        th1.setName("Thread 2");
        ThreadTest th2 = new ThreadTest(pico);
        th2.setName("Thread 3");
        th1.start();
        th2.start();

        // try to artificially increase the instance count
        for (int i = 0; i < 5; i++)
        {
            pico.getComponentInstance(MockComponent.class);
        }

        assertEquals(3, BaseMockComponent.instanceCount);

    }

    public void testDependencyWithThreadLocalAndHotSwapping() throws Exception
    {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        
        pico.registerComponentImplementation(SINGLETON_KEY, MockDependentComponent.class,  new Parameter[]{new ComponentParameter(MockComponent.class)});
        
        ConstructorComponentAdapter cca = new ConstructorComponentAdapter(MockComponent.class, BaseMockComponent.class, new Parameter[] {new ConstantParameter(new Integer(1)), new ConstantParameter("c1")});
        
        pico.registerComponent(new InterceptorAdapter(cca, ThreadLocalDelegationStrategy.class));
        pico.start();
        
        ThreadTest2 tt = new ThreadTest2(pico);
        
        tt.start();
        
        MockDependentComponent mdc = (MockDependentComponent) pico.getComponentInstance(SINGLETON_KEY);
        assertNotNull(mdc);
        MockComponent testMc = mdc.getMockComponent();
        assertNotNull(testMc);
        assertEquals(testMc.getValue2(), "c1");
        
        tt.doVerify(SINGLETON_KEY, "c1", testMc);
        
        synchronized(tt)
        {
            tt.wait();
        }
        
        assertFalse(tt.failed());
        
        //Now change this thread without affecting the other thread
        MockComponent c2 = new BaseMockComponent(2, "c2");
        ((Swappable) testMc).__hotSwap(c2);
        
        assertEquals("c2", mdc.getMockComponent().getValue2());
        // verify that the value has not changed in the other thread
        tt.doVerify(SINGLETON_KEY, "c1", testMc);
        
        synchronized(tt)
        {
            tt.wait();
        }
        
        assertFalse(tt.failed());
        
        tt.shutdown();
        
    }

    class ThreadTest extends Thread
    {
        private PicoContainer pico;

        ThreadTest( PicoContainer pico )
        {
            this.pico = pico;
        }

        public void run()
        {
            // super.run();

            MockComponent mc = (MockComponent) pico
                    .getComponentInstance(MockComponent.class);
            assertEquals(Thread.currentThread().getName(), mc.getThreadName());
            // try to artificially increase the instance count
            for (int i = 0; i < 5; i++)
            {
                pico.getComponentInstance(MockComponent.class);
            }

        }
    }

    class ThreadTest2 extends Thread
    {
        private PicoContainer pico;
        private boolean fieldVerify;
        private String fieldValue;
        private Object fieldKey;
        private boolean fieldRunning;
        private boolean failed=false;
        private Object otherThreadsInstance;

        ThreadTest2( PicoContainer pico )
        {
            this.pico = pico;
        }

        public void run()
        {
            // super.run();
            fieldRunning = true;
            while (fieldRunning)
            {
                if (fieldVerify)
                {
                    fieldVerify = false;
                    MockDependentComponent mdc = (MockDependentComponent) pico
                            .getComponentInstance(fieldKey);
                    try
                    {
                        assertNotNull(mdc);
                        assertNotNull(mdc.getMockComponent());
                        assertEquals(mdc.getMockComponent().getValue2(), fieldValue);
                        if(otherThreadsInstance != null)
                        {
                            // Verifies that component is the same in both Threads.
                            assertTrue(mdc.getMockComponent() == otherThreadsInstance);
                        }
                    }
                    catch (Error e)
                    {
                         System.err.println(e.getMessage());
                         failed = true;
                    }
                    
                    synchronized(this)
                    {
                        notifyAll();
                    }
                }
            }

        }

        void doVerify( Object key, String value, Object otherThreadsInstance )
        {
            fieldKey = key;            
            fieldValue = value;
            this.otherThreadsInstance = otherThreadsInstance;
            fieldVerify = true;
        }       
       
        
        boolean failed()
        {
            return failed;
        }
        
        void shutdown()
        {
            fieldRunning = false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {

        super.tearDown();
    }
}