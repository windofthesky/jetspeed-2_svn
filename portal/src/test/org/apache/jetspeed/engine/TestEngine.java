/*
 * Created on May 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.engine;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.script.ScriptedContainerBuilder;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class TestEngine extends TestCase
{

    protected ScriptedContainerBuilder scriptBuilder;
    protected PicoContainer container;
    /**
     * 
     */
    public TestEngine()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public TestEngine( String arg0 )
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestEngine.class);
    }
    
    public void testEngine() throws Exception
    {        
        assertNotNull(container);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {        
        super.setUp();
        PropertiesConfiguration config = new  PropertiesConfiguration();
        config.load("./src/webapp/WEB-INF/conf/jetspeed.properties");
        Jetspeed.createEngine(config, "./src/webapp", null);
        NanoContainer nano  = new NanoContainer(new File("./src/webapp/WEB-INF/assembly/jetspeed.groovy"));
        scriptBuilder = nano.getContainerBuilder();
        ObjectReference containerRef = new SimpleReference();
        scriptBuilder.buildContainer(containerRef, null, "jetspeed");
        container = (PicoContainer) containerRef.get(); 
    }
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        container.stop();
        super.tearDown();
    }
    
    protected void verifyComponents(Object[] keys)
    {
        for(int i=0; i < keys.length; i++)
        {
             assertNotNull("Could not get component insatance "+keys[i], container.getComponentInstance(keys[i]));
        }
    }
}
