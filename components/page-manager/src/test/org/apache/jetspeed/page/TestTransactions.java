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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.test.AbstractSpringTestCase;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;

/**
 * Test Transactions
 * 
 * @author <a href="rwatler@apache.org">Randy Watler</a>
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id: $
 *          
 */
public class TestTransactions extends  /*AbstractSpringTestCase*/ DatasourceEnabledSpringTestCase implements PageManagerTestShared
{
    protected PageManager pageManager;

    protected String somePortletId;
    
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestTransactions.class.getName() });
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();
        pageManager = (PageManager)ctx.getBean("pageManager");
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestTransactions.class);
    }
    
    protected String[] getConfigurations()
    {
        return new String[]
        { "tx-page-manager.xml", "transaction.xml", "interceptors.xml" }; 
    }

    public void testTx() throws Exception
    {
        if (pageManager.folderExists("/"))
        {
            pageManager.removeFolder(pageManager.getFolder("/"));
        }
        Folder root = pageManager.newFolder("/");
        pageManager.updateFolder(root);
        Page[] pages = new Page[3];
        pages[0] = pageManager.newPage("/test1.psml");
        pages[1] = pageManager.newPage("/test2.psml");
        pages[2] = pageManager.newPage("/test3.psml");
        try
        {
            pageManager.addPages(pages);
        }
        catch (Exception e)
        {
            System.out.println("Exception adding pages" + e);
            e.printStackTrace();
            
        }
        assertFalse("page 1 found", pageManager.pageExists("/test1.psml"));
        assertFalse("page 2 found", pageManager.pageExists("/test2.psml"));
        assertFalse("page 3 found", pageManager.pageExists("/test3.psml"));
    }
}
