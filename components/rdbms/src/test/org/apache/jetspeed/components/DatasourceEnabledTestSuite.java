/**
 * Created on Feb 16, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.components;

import java.io.InputStreamReader;
import java.io.Reader;

import javax.naming.NamingException;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.datasource.DatasourceComponent;
import org.apache.jetspeed.components.jndi.JNDIComponent;
import org.picocontainer.MutablePicoContainer;

/**
 * <p>
 * DatasourceEnabledTestSuite
 * </p>
 * <p>
 * Use this Test Suite when you need to have access to the 
 * portal/test/db/hsql/Registry database via the "java:comp/env/jdbc/jetspeed"
 * JNDI datasource.
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class DatasourceEnabledTestSuite extends TestSuite
{
    
    public static final String DEFAULT_CONTAINER = "org/apache/jetspeed/containers/rdbms.container.groovy";
    
    private ComponentManager cm;

    private String script;

    /**
     * 
     */
    public DatasourceEnabledTestSuite()
    {
        super();
        script = DEFAULT_CONTAINER;		
    }

    /**
     * @param arg0
     * @param arg1
     */
    public DatasourceEnabledTestSuite(Class arg0, String arg1)
    {
        super(arg0, arg1);
        script = DEFAULT_CONTAINER;
		
    }

    /**
     * @param arg0
     */
    public DatasourceEnabledTestSuite(Class arg0)
    {
        super(arg0);
        script = DEFAULT_CONTAINER;
		
    }   
    
    

    /**
     * @param arg0
     */
    public DatasourceEnabledTestSuite(String arg0)
    {
        super(arg0);
        script = DEFAULT_CONTAINER;
		
    }
    
    public DatasourceEnabledTestSuite(Class arg0, String arg1, String containerScript)
    {
        super(arg0, arg1);
        script = containerScript;
		
    }


    protected void initDatasource()throws Exception
    {
        System.out.println("========================= DatasourceEnabledTestSuite start RDBMS container ");
        Reader composition = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(
        script));
        cm = new ComponentManager(composition, ComponentManager.GROOVY);
       	MutablePicoContainer container = cm.getRootContainer();	
        
		JNDIComponent jndi = (JNDIComponent) container.getComponentInstanceOfType(JNDIComponent.class);
		DatasourceComponent dsc = (DatasourceComponent) container.getComponentInstanceOfType(DatasourceComponent.class);
		try
        {
            jndi.bindObject("comp/env/jdbc/jetspeed", dsc.getDatasource());
        }
        catch (NamingException e)
        {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * @see junit.framework.Test#run(junit.framework.TestResult)
     */
    public void run(TestResult arg0)
    {
        try
        {
            initDatasource();            
            super.run(arg0);
            System.out.println("========================= DatasourceEnabledTestSuite stopping RDBMS container ");            
            cm.getRootContainer().stop();
           // Thread.sleep(2000);
        }
        catch(Throwable e)
        {
            cm.getRootContainer().stop();
            e.printStackTrace();
            throw new IllegalStateException("Unable to start RDBMS container: "+e.toString());
        }
  
    }

    /* (non-Javadoc)
     * @see junit.framework.TestSuite#runTest(junit.framework.Test, junit.framework.TestResult)
     */
    public void runTest(Test arg0, TestResult arg1)
    {
        System.out.println("========================= DatasourceEnabledTestSuite running test "+arg0);
        
        if(arg0 instanceof AbstractComponentAwareTestCase)
        {
           ((AbstractComponentAwareTestCase)arg0).setComponentManager(cm); 
        }
        super.runTest(arg0, arg1);
    }

}
