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
package org.apache.jetspeed.page;

import org.apache.jetspeed.components.test.AbstractSpringTestCase;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * TestPageXmlPersistence
 * 
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 *          
 */
public class TestDatabasePageManager extends AbstractSpringTestCase
{
    private PageManager pageManager;
    
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestDatabasePageManager.class.getName() });
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();        
        pageManager = (PageManager)ctx.getBean("pageManager");        
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestDatabasePageManager.class);
    }
    
    protected String[] getConfigurations()
    {
        return new String[]
        { "database-page-manager.xml", "transaction.xml" };
    }

    protected String[] getBootConfigurations()
    {
        return new String[]
        { "test-repository-datasource-spring.xml" };
    }

    public void testPages()
    throws Exception
    {
        System.out.println("************ creating test data");
        createTestData();
        try
        {
            Page page = pageManager.getPage("/notfound.psml");
        }
        catch (PageNotFoundException e)
        {
            System.out.println("Page " + "/notfound.psml");
            return;
        }
        fail("should have got a page not found error");
        
        try
        {
            Folder folder = pageManager.getFolder("/");
        }
        catch (PageNotFoundException e)
        {
            fail("should have found root page");                    
        }
        dropTestData();        
    }
    
//    public void testFolders()
//    throws Exception
//    {
//        Folder folder = pageManager.getFolder("/");
//    }
    
    private void createTestData()
    {
        try
        {
            Folder folder = pageManager.newFolder("/");
            folder.setTitle("Root");
            pageManager.updateFolder(folder);
            
        }
        catch (Exception e)
        {
            fail("could not add root folder");
        }
    }
    
    private void dropTestData()
    {
        try
        {
            Folder root = pageManager.getFolder("/");
            assertNotNull("folder should be found", root);
            pageManager.removeFolder(root);
            
        }
        catch (Exception e)
        {
            fail("could not remove root folder");
        }
        
    }
}