/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.components;

import java.io.File;
import java.util.ArrayList;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.defaults.SimpleReference;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id: NanoDeployerBasedTestSuite.java,v 1.1.2.2 2004/04/22 20:56:25
 *          weaver Exp $
 *  
 */
public class NanoDeployerBasedTestSuite extends TestSuite
{
    private MutablePicoContainer container;
    private String[] applicationFolders;
    private ComponentManager cm;

    /**
     *  
     */
    public NanoDeployerBasedTestSuite()
    {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     * @param arg1
     */
    public NanoDeployerBasedTestSuite( Class arg0, String arg1 )
    {
        super(arg0, arg1);
        // TODO Auto-generated constructor stub

    }

    /**
     * @param arg0
     */
    public NanoDeployerBasedTestSuite( Class arg0 )
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param arg0
     */
    public NanoDeployerBasedTestSuite( String arg0 )
    {
        super(arg0);
        // TODO Auto-generated constructor stub
    }

    protected void buildContainer() throws Exception
    {
        ArrayList containers = new ArrayList();
        File deploy = new File("./target/deploy");
        if (deploy.exists())
        {
            File[] archives = deploy.listFiles();
            for (int i = 0; i < archives.length; i++)
            {
                containers.add("zip:/" + archives[i].getAbsolutePath());
            }
        }

        containers.add(new File("./target/classes").toURL().toString());

        setApplicationFolders((String[]) containers.toArray(new String[containers.size()]));

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        FileSystemManager fsManager = VFS.getManager();
        // NanoContainerDeployer deployer = new
        // NanoContainerDeployer(fsManager);
        DependencyAwareDeployer deployer = new DependencyAwareDeployer(fsManager);
        MutablePicoContainer parent = new ChildAwareContainer();
        SimpleReference parentRef = new SimpleReference();
        parentRef.set(parent);
        for (int i = 0; i < getApplicationFolders().length; i++)
        {
            container = (MutablePicoContainer) deployer.deploy(fsManager.resolveFile(getApplicationFolders()[i]), cl, parentRef).get();
        }

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
    public void runTest( Test arg0, TestResult arg1 )
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
    public String[] getApplicationFolders()
    {
        return applicationFolders;
    }

    /**
     * @param script
     *            The script to set.
     */
    public void setApplicationFolders( String[] appFolders )
    {
        this.applicationFolders = appFolders;
    }
}