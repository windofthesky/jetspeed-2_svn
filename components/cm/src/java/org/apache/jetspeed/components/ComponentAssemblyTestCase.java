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

import java.io.File;

import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

import junit.framework.TestCase;

/**
 * ComponentAssemblyTestCase
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public abstract class ComponentAssemblyTestCase extends TestCase
{
    public ComponentAssemblyTestCase(String name) 
    {
        super( name );
    }
    
    public String getAssemblyScriptType()
    {
        return ".groovy";
    }
    
    public String getTestName()
    {
        String className = this.getClass().getName();
        int ix = className.lastIndexOf(".");
        if (ix > -1)
        {
            className = className.substring(ix + 1);
        }
        return className;        
    }
    
    public abstract String getBaseProject();

    public String getRelativePath()
    {
        return "test";
    }
        
    public String getApplicationRoot()
    {
        return getApplicationRoot(getBaseProject(), getRelativePath());        
    }
    
    public static String getApplicationRoot(String baseProject, String relativePath)
    {
        String applicationRoot = relativePath;
        File testPath = new File(applicationRoot);
        if (!testPath.exists())
        {
            testPath = new File( baseProject + File.separator + applicationRoot);
            if (testPath.exists())
            {
                applicationRoot = testPath.getAbsolutePath();
            }
        }
        return applicationRoot;
    }
    
    protected ComponentManager componentManager = null;
    
    public void setUp()
    throws Exception
    {
        String applicationRoot = getApplicationRoot(getBaseProject(), getRelativePath());
        File containerAssembler = new File(applicationRoot + "/assembly/" + getTestName() + getAssemblyScriptType());
        assertTrue(containerAssembler.exists());
        componentManager = new  ComponentManager(containerAssembler);
        ObjectReference rootContainerRef = new SimpleReference();       
                            
        componentManager.getContainerBuilder().buildContainer(rootContainerRef, null, "TEST_SCOPE");
        
        assertNotNull(rootContainerRef.get());
            
    }
    
    
}
