/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
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
    
    protected void buildContainer(String script) throws ClassNotFoundException
    {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream is = cl.getResourceAsStream(script);
        if (is == null)
        {
            throw new ClassNotFoundException("script not found: " + script);
        }
        Reader scriptReader = new InputStreamReader(is);
        
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
            
            buildContainer(script);
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
            
             throw new RuntimeException(e);            
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
