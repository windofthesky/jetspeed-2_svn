/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 *  
 */
public class ContainerDeployerTestSuite extends TestSuite
{

    private MutablePicoContainer container;
    private String[] localPackages;

    /**
     *  
     */
    public ContainerDeployerTestSuite()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public ContainerDeployerTestSuite( Class arg0 )
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    
    public ContainerDeployerTestSuite( Class arg0, String[] localPackages )
    {
        super(arg0);
        this.localPackages = localPackages;
    }

    /**
     * @param arg0
     * @param arg1
     */
    public ContainerDeployerTestSuite( Class arg0, String arg1 )
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub
    }
    
    public ContainerDeployerTestSuite( Class arg0, String arg1, String[] localPackages )
    {
        super(arg0, arg1);
        this.localPackages = localPackages;
    }

    /**
     * @param arg0
     */
    public ContainerDeployerTestSuite( String arg0 )
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.Test#run(junit.framework.TestResult)
     */
    public void run( TestResult arg0 )
    {
        try
        {

            buildContainer();
            super.run(arg0);
            if (container != null)
            {
                container.stop();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (container != null)
            {
                container.stop();
            }

            
            
            throw new RuntimeException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestSuite#runTest(junit.framework.Test,
     *      junit.framework.TestResult)
     */
    public void runTest( Test arg0, TestResult arg1 )
    {
        // TODO Auto-generated method stub
        try
        {
            if (arg0 instanceof AbstractComponentAwareTestCase)
            {
                ((AbstractComponentAwareTestCase) arg0).setContainer(container);            
            }
            super.runTest(arg0, arg1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
           // throw e;
        }
       
    }

    protected void buildContainer() throws Exception
    {
        try
        {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();

            File  testClasses = new File("./target/test-classes/");        
            if (!testClasses.exists())
            {
                throw new FileNotFoundException("Could not locate dir "
                        + testClasses.getAbsolutePath());
            }

            URLClassLoader testClassLoader = new URLClassLoader(
                    new URL[]{testClasses.toURL()}, cl);

            String applicationRoot = System.getProperty(
                    "org.apache.jetspeed.application_root", "./");
            Configuration sysConfig = new PropertiesConfiguration();
            sysConfig.setProperty("app.root", applicationRoot);

//            ContainerManager manager = new ClassLoaderDeploymentContainerManager(
//                    "test-container", "./target/deploy",
//                    new String[]{"./target/classes/"}, testClassLoader,
//                    sysConfig);
//            Thread.currentThread().setContextClassLoader(
//                    manager.getContainerClassLoader());
            ContainerManager manager = new LocalDeploymentContainerManager(sysConfig);

            MutablePicoContainer container = new DefaultPicoContainer();
            manager.assembleContainer(container).start();
            this.container = container;
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

    }
}