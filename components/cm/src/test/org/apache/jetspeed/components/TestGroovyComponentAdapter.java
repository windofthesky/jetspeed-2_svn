/*
 * Created on Apr 15, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;


import org.apache.jetspeed.components.pico.groovy.GroovyComponentAdapter;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class TestGroovyComponentAdapter extends TestCase
{

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestGroovyComponentAdapter.class);
    }
    
    public void testGroovyAdapter() throws Exception
    {
        MutablePicoContainer container = new DefaultPicoContainer();
        Parameter v1 = new ConstantParameter(new Integer(69));
        Parameter v2 = new ConstantParameter("Some Value");     
        ComponentAdapter adp1 = new GroovyComponentAdapter("multipleObjects", BaseMockComponent.class, new Parameter[] {v1, v2}, false);
        ComponentAdapter adp2 = new GroovyComponentAdapter("singletonObject", BaseMockComponent.class, new Parameter[] {v1, v2}, true);
        
        container.registerComponent(adp1);
        container.registerComponent(adp2);
        container.start();
        
        MockComponent comp1 = (MockComponent) container.getComponentInstance("multipleObjects");
        MockComponent comp2 = (MockComponent) container.getComponentInstance("multipleObjects");
        assertNotNull(comp1);
        assertNotNull(comp2);
        assertTrue(comp1 != comp2);
        beanTest(comp1, 69, "Some Value");
        beanTest(comp2, 69, "Some Value");
        
        MockComponent comp3 = (MockComponent) container.getComponentInstance("singletonObject");
        MockComponent comp4 = (MockComponent) container.getComponentInstance("singletonObject");
        assertNotNull(comp3);
        assertNotNull(comp4);
        assertTrue(comp3 == comp4);
        beanTest(comp3, 69, "Some Value");
        beanTest(comp4, 69, "Some Value");
        
    }
    
    protected void beanTest(MockComponent comp, int v1, String v2)
    {
        assertEquals(comp.getValue1(), 69);
        assertEquals(comp.getValue2(), "Some Value");
        
    }

}
