/*
 * Created on Jul 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.engine;

import java.io.FileInputStream;

import javax.servlet.ServletConfig;

import junit.framework.TestCase;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.components.ComponentManagement;
import org.jmock.Mock;

/**
 * <p>
 * AbstractEngineTest
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public abstract class AbstractEngineTest extends TestCase
{

    /**
     * 
     */
    public AbstractEngineTest()
    {
        super();
    }
    /**
     * @param arg0
     */
    public AbstractEngineTest( String arg0 )
    {
        super(arg0);
    }
    protected Engine engine;
    protected Object[] keysToCheck;
    
    public void testEngine() throws Exception
    {        
        assertNotNull(engine.getComponentManager());
        assertNotNull(engine.getComponentManager().getRootContainer());
        if(keysToCheck != null)
        {
            verifyComponents(keysToCheck);
        }
    }
    protected void setUp() throws Exception
    {        
        super.setUp();
        // need to flag internal JNDI on...
        System.setProperty("portal.use.internal.jndi", "true");
        PropertiesConfiguration config = new  PropertiesConfiguration();
        config.load(new FileInputStream("./src/webapp/WEB-INF/conf/jetspeed.properties")); 
        Mock servletConfigMock = new Mock(ServletConfig.class);
        engine = Jetspeed.createEngine(config, "./src/webapp", (ServletConfig) servletConfigMock.proxy(), getEngineClass());
    
    }
    protected void tearDown() throws Exception
    {
        
        super.tearDown();
    }
    
    protected void verifyComponents( Object[] keys )
    {
        ComponentManagement cm = engine.getComponentManager();
        for(int i=0; i < keys.length; i++)
        {
             assertNotNull("Could not get component insatance "+keys[i], cm.getComponent(keys[i]));
             System.out.println("Load componenet "+cm.getComponent(keys[i]).getClass()+" for key "+keys[i]);
        }
    }
    
    protected abstract Class getEngineClass();
}
