/*
 * Created on Feb 22, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

import java.io.FileInputStream;
import java.util.Properties;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4jFactory;
import org.apache.log4j.PropertyConfigurator;

import org.picocontainer.MutablePicoContainer;

import junit.framework.TestCase;

/**
 * @author Sweaver
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public abstract class AbstractComponentAwareTestCase extends TestCase
{
    private ComponentManager ncm;
    private MutablePicoContainer container;
    
    /**
     * @param arg0
     */
    public AbstractComponentAwareTestCase(String arg0)
    {
        super(arg0);
    }
    /**
     * @return Returns the ncm.
     */
    public ComponentManager getComponentManager()
    {
        return ncm;
    }

    /**
     * @param ncm The ncm to set.
     */
    public void setComponentManager(ComponentManager ncm)
    {
        this.ncm = ncm;
    }



    /**
     * @return Returns the container.
     */
    public MutablePicoContainer getContainer()
    {
        return container;
    }

    /**
     * @param container The container to set.
     */
    public void setContainer(MutablePicoContainer container)
    {
        this.container = container;
    }

    public static final String LOG4J_CONFIG_FILE = "log4j.file";
	// TODO: make this relative, move it into script
    public static final String LOG4J_CONFIG_FILE_DEFAULT = "src/webapp/WEB-INF/conf/Log4j.properties";
    
    protected void setUp() throws Exception
    {
        super.setUp();
        
        System.out.println("MAIN --------------");
        String log4jFile = LOG4J_CONFIG_FILE_DEFAULT;
        Properties p = new Properties();
        try
        {
            p.load(new FileInputStream(log4jFile));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(p);
        
        System.getProperties().setProperty(LogFactory.class.getName(), Log4jFactory.class.getName());
        
    }
    

}
