/*
 * Created on Apr 21, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.picocontainer.Parameter;
import org.picocontainer.defaults.ComponentParameter;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class TestChildAwareContainer extends TestCase
{
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestChildAwareContainer.class);
    }
    
    public void testFindingNestedComponentFromParent()
    {
        ChildAwareContainer root = new ChildAwareContainer();
        
        ChildAwareContainer sub1 = new ChildAwareContainer(root);
        
        sub1.registerComponentInstance("sub1Component1", new MockComponent(1,"sub1Component1"));
        
        root.start();
        
        assertNotNull("Could not locate child container's component.", root.getComponentInstance("sub1Component1"));
    }
    
    public void testFindingNestedComponentsFromMultipleChildren()
    {
        ChildAwareContainer root = new ChildAwareContainer();
        
        ChildAwareContainer sub1 = new ChildAwareContainer(root);
        
        ChildAwareContainer sub2 = new ChildAwareContainer(root);
        
        sub1.registerComponentInstance("sub1Component1", new MockComponent(1,"sub1Component1"));
        
        sub2.registerComponentInstance("sub2Component1", new MockComponent(1,"sub2Component1"));
        
    
        root.start();
        
        assertNull(root.getComponentInstance("noSuchComponent"));
        
        assertNotNull("Could not locate child 1 container's component.", root.getComponentInstance("sub1Component1"));
        
        assertNotNull("Could not locate child 2 container's component.", root.getComponentInstance("sub2Component1"));
        
        assertTrue(sub1.getComponentInstance("sub1Component1") == root.getComponentInstance("sub1Component1") );        
    }
    
    public void testFindingNestedComponentAsDependency()
    {
        ChildAwareContainer root = new ChildAwareContainer();
        
        ChildAwareContainer sub1 = new ChildAwareContainer(root);
        
        ChildAwareContainer sub2 = new ChildAwareContainer(root);
        
        sub1.registerComponentInstance("sub1Component1", new MockComponent(1,"sub1Component1"));
        
        sub2.registerComponentImplementation("sub2Component1", MockDependentComponent.class, new Parameter[] {new ComponentParameter("sub1Component1")} );
        
        try
        {
            root.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("unable to start root container: "+e.toString());            
        }
        MockDependentComponent sub2Component1 = (MockDependentComponent) root.getComponentInstance("sub2Component1");
        
        assertNotNull("Could not locate child container's component.", sub2Component1);
        
        assertNotNull("Dependency not set for MockDependent object.", sub2Component1.getDependency());
        
        assertTrue(sub2Component1.getDependency() == sub1.getComponentInstance("sub1Component1"));
    }
    
    
    public void testReallyCrazyNesting()
    {
        ChildAwareContainer root = new ChildAwareContainer();
        
        ChildAwareContainer sub1 = new ChildAwareContainer(root);
        
        ChildAwareContainer sub2 = new ChildAwareContainer(root);
        
        ChildAwareContainer sub2sub3 = new ChildAwareContainer(sub2);
        
        sub1.registerComponentInstance("sub1Component1", new MockComponent(1,"sub1Component1"));
        
        sub2.registerComponentImplementation("sub2Component1", MockDependentComponent.class, new Parameter[] {new ComponentParameter("sub1Component1")} );
        
        sub2sub3.registerComponentImplementation("sub2sub3Component1", MockDependentComponent.class, new Parameter[] {new ComponentParameter("sub1Component1")} );
        
        try
        {
            root.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("unable to start root container: "+e.toString());            
        }
        MockDependentComponent sub2Component1 = (MockDependentComponent) root.getComponentInstance("sub2Component1");
        MockDependentComponent sub2sub3Component1 = (MockDependentComponent) root.getComponentInstance("sub2sub3Component1");
        
        assertNotNull("Could not locate child container's component.", sub2Component1);
        
        assertNotNull("Could not locate child container's component.", sub2sub3Component1);
        
        assertNotNull("Dependency not set for MockDependent object.", sub2Component1.getDependency());
        
        assertNotNull("Dependency not set for MockDependent object.", sub2sub3Component1.getDependency());
        
        assertTrue(sub2Component1.getDependency() == sub1.getComponentInstance("sub1Component1"));
    }
    
    
    
    
}
