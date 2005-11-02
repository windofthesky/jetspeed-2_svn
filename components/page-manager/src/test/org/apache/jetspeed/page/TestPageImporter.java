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

import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.rdbms.ojb.ConnectionRepositoryEntry;
import org.apache.jetspeed.components.test.AbstractSpringTestCase;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;


/**
 * DatabasePageManager
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */

public class TestPageImporter extends AbstractSpringTestCase
{
    private PageManager dbManager;
    private PageManager castorManager;    
    
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestPageImporter.class.getName() });
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();        
        dbManager = (PageManager)ctx.getBean("dbPageManager");
        castorManager = (PageManager)ctx.getBean("castorPageManager");
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestPageImporter.class);
    }
    
    protected String[] getConfigurations()
    {
        return new String[]
        { "import-page-manager.xml", "transaction.xml" };
    }

    protected String[] getBootConfigurations()
    {
        return new String[]
        { "test-repository-datasource-spring.xml" };
    }
     
    public void testImporter()
    throws Exception
    {
        System.out.println("Importer Test");
        assertNotNull("db manager is null", dbManager);
        assertNotNull("castor manager is null", castorManager);

        // create root folder
        Folder fsRoot = castorManager.getFolder("/");
        Folder root = importFolder(fsRoot);                        
        
        // NOTE: this will delete EVERYTHING
        // dbManager.removeFolder(root);
        
        System.out.println("Importer Test Completed.");
        System.out.println(folderCount + " folders imported.");        
        System.out.println(pageCount + " pages imported.");        
    }
    
    private int folderCount = 0;
    private int pageCount = 0;
    
    private Folder importFolder(Folder srcFolder)
    throws Exception
    {
        System.out.println("copying folder " + srcFolder.getPath());
        Folder dstFolder = dbManager.copyFolder(srcFolder, srcFolder.getPath());
        dbManager.updateFolder(dstFolder);
        folderCount++;
        
        Iterator pages = srcFolder.getPages().iterator();
        while (pages.hasNext())
        {
            Page srcPage = (Page)pages.next();
            System.out.println("copying page " + srcPage.getPath());
            Page dstPage = dbManager.copyPage(srcPage, srcPage.getPath());
            dbManager.updatePage(dstPage);
            pageCount++;
        }
        
        Iterator folders = srcFolder.getFolders().iterator();
        while (folders.hasNext())
        {
            Folder folder = (Folder)folders.next();
            importFolder(folder);
        }
        
        return dstFolder;
    }
}
