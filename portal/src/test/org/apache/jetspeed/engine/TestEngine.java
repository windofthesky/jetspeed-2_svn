/*
 * Created on May 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.engine;

import java.io.FileInputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.picocontainer.PicoContainer;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class TestEngine extends TestCase
{

    protected Engine engine;

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
        assertNotNull(engine.getComponentManager());
        assertNotNull(engine.getComponentManager().getRootContainer());
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {        
        super.setUp();
        // need to flag internal JNDI on...
        System.setProperty("portal.use.internal.jndi", "true");
        PropertiesConfiguration config = new  PropertiesConfiguration();
        config.load(new FileInputStream("./src/webapp/WEB-INF/conf/jetspeed.properties")); 
        engine = Jetspeed.createEngine(config, "./src/webapp", null);
       
    }
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        
        super.tearDown();
    }
    
    protected void verifyComponents(Object[] keys)
    {
        PicoContainer container = engine.getComponentManager().getRootContainer();
        for(int i=0; i < keys.length; i++)
        {
             assertNotNull("Could not get component insatance "+keys[i], container.getComponentInstance(keys[i]));
        }
    }
}
