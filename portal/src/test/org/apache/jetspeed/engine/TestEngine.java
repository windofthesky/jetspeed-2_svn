/*
 * Created on May 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.engine;

import junit.framework.Test;
import junit.framework.TestSuite;


/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class TestEngine extends AbstractEngineTest
{

    /**
     * 
     */
    public TestEngine()
    {
        super();
    }

    /**
     * @param arg0
     */
    public TestEngine( String arg0 )
    {
        super(arg0);
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestEngine.class);
    }

    /**
     * <p>
     * getEngineClass
     * </p>
     *
     * @see org.apache.jetspeed.engine.AbstractEngineTest#getEngineClass()
     * @return
     */
    protected Class getEngineClass()
    {       
        return PicoEngine.class;
    }
}
