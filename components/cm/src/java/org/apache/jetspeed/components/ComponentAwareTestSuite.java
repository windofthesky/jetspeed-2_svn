/*
 * Created on Feb 23, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;
import java.io.InputStreamReader;
import java.io.Reader;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * @author Sweaver
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ComponentAwareTestSuite extends TestSuite
{
    private MutablePicoContainer container;
    private String script;
    private ComponentManager cm;


    /**
     *  
     */
    public ComponentAwareTestSuite()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public ComponentAwareTestSuite(Class arg0, String arg1)
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public ComponentAwareTestSuite(Class arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public ComponentAwareTestSuite(String arg0)
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    protected void buildConainer(String script) throws ClassNotFoundException
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        Reader scriptReader = new InputStreamReader(cl.getResourceAsStream(script));
        cm = new ComponentManager(scriptReader, ComponentManager.GROOVY);
        ObjectReference containerRef = new SimpleReference();
        cm.getContainerBuilder().buildContainer(containerRef, null, "TEST_REGISTRY");
        container = (MutablePicoContainer) containerRef.get();
    }
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.Test#run(junit.framework.TestResult)
     */
    public void run(TestResult arg0)
    {
        try
        {
            buildConainer(script);
            super.run(arg0);
            if (container != null)
            {
                container.stop();
            }
        }
        catch (Exception e)
        {
            if (container != null)
            {
                container.stop();
            }
            
            e.printStackTrace();
            throw new RuntimeException(e.toString());            
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestSuite#runTest(junit.framework.Test,
     *      junit.framework.TestResult)
     */
    public void runTest(Test arg0, TestResult arg1)
    {
        // TODO Auto-generated method stub
        if (arg0 instanceof AbstractComponentAwareTestCase)
        {
            ((AbstractComponentAwareTestCase) arg0).setContainer(container);
            ((AbstractComponentAwareTestCase) arg0).setComponentManager(cm);
        }
        super.runTest(arg0, arg1);
    }

    /**
     * @return Returns the script.
     */
    public String getScript()
    {
        return script;
    }

    /**
     * @param script
     *            The script to set.
     */
    public void setScript(String script)
    {
        this.script = script;
    }
}
