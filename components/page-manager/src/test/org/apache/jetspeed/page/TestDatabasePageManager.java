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

import java.util.Locale;

import org.apache.jetspeed.components.test.AbstractSpringTestCase;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.Fragment;
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
        // createTestData();
    }

    protected void tearDown() throws Exception
    {
        //dropTestData();
        super.tearDown();
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

    public void xtestPages() throws Exception
    {
        boolean pageNotFound = false;
        try
        {
            Page page = pageManager.getPage("/notfound.psml");
        }
        catch (PageNotFoundException e)
        {
            pageNotFound = true;
        }
        assertTrue("should have got a page not found error", pageNotFound);
        
        try
        {
            Page page = pageManager.getPage("/default-page.psml");
        }
        catch (PageNotFoundException e)
        {
            fail("should have found root default page");                    
        }
    }
    
    public void xtestFolders() throws Exception
    {
        try
        {
            Folder folder = pageManager.getFolder("/");
        }
        catch (FolderNotFoundException e)
        {
            fail("should have found root folder");                    
        }
    }
        
    public void testCreates()
    {
        try
        {
            // test basic folder/page/fragment creation
            Folder folder = pageManager.newFolder("/");
            folder.setTitle("Root Folder");
            folder.setDefaultPage("default-page.psml");
            folder.setDefaultTheme("Blue Theme");
            folder.setShortTitle("Root");
            GenericMetadata metadata = folder.getMetadata();
            metadata.addField(Locale.FRENCH, "title", "[fr] Root Folder");
            pageManager.updateFolder(folder);
            
            assertNull(folder.getParent());

            Page page = pageManager.newPage("/default-page.psml");
            page.setTitle("Default Page");
            page.setDefaultDecorator("tigris", Fragment.LAYOUT);
            page.setDefaultDecorator("blue-gradient", Fragment.PORTLET);
            page.setDefaultSkin("skin-1");
            page.setShortTitle("Default");
            metadata = page.getMetadata();
            metadata.addField(Locale.FRENCH, "title", "[fr] Default Page");
            metadata.addField(Locale.JAPANESE, "title", "[ja] Default Page");

            Fragment root = page.getRootFragment();
            root.setDecorator("blue-gradient");
            root.setName("jetspeed-layouts::VelocityTwoColumns");
            root.setShortTitle("Root");
            root.setTitle("Root Fragment");
            root.setState("Normal");
            root.setLayoutSizes("50%,50%");
            root.getProperties().put("custom-prop1", "custom-prop-value1");
            root.getProperties().put("custom-prop2", "custom-prop-value2");
            
            Fragment portlet = pageManager.newPortletFragment();
            portlet.setName("security::LoginPortlet");
            portlet.setShortTitle("Portlet");
            portlet.setTitle("Portlet Fragment");
            portlet.setState("Normal");
            portlet.setLayoutRow(88);
            portlet.setLayoutColumn(99);
            root.getFragments().add(portlet);

            pageManager.updatePage(page);

            assertNotNull(page.getParent());
            assertEquals(page.getParent().getId(), folder.getId());

            try
            {
                Page check = pageManager.getPage("/default-page.psml");
                assertEquals("/default-page.psml", check.getPath());
            }
            catch (PageNotFoundException e)
            {
                assertTrue("Page /default-page.psml NOT FOUND", false);
            }
            try
            {
                Folder checkFolder = pageManager.getFolder("/");
                assertEquals("/", checkFolder.getPath());
            }
            catch (FolderNotFoundException e)
            {
                assertTrue("Folder / NOT FOUND", false);
            }
            try
            {
                Folder checkFolder = pageManager.newFolder("/");
                pageManager.updateFolder(checkFolder);
                assertTrue("Duplicate Folder / CREATED", false);
            }
            catch (FolderNotUpdatedException e)
            {
                assertTrue("Duplicate Folder / NOT CREATED", true);
            }

            pageManager.removeFolder(folder);

            // test folder/page creation with attributes
            String testAttributesPath = "/__custom/subsiteX/_user/userX/_role/roleX/_group/groupX/_mediatype/xhtml/_language/en/_country/us/_custom/customX";
            String verifyTestAttributesPath = "/__subsite-root/subsitex/_user/userx/_role/rolex/_group/groupx/_mediatype/xhtml/_language/en/_country/us/_custom/customx";
            folder = pageManager.newFolder(testAttributesPath);
            pageManager.updateFolder(folder);
            assertNull(folder.getParent());

            page = pageManager.newPage(testAttributesPath + "/default-page.psml");
            pageManager.updatePage(page);

            assertNotNull(page.getParent());
            assertEquals(page.getParent().getId(), folder.getId());

            try
            {
                Page check = pageManager.getPage(testAttributesPath + "/default-page.psml");
                assertEquals(verifyTestAttributesPath + "/default-page.psml", check.getPath());
            }
            catch (PageNotFoundException e)
            {
                assertTrue("Page " + testAttributesPath + "/default-page.psml NOT FOUND", false);
            }
            try
            {
                Folder checkFolder = pageManager.getFolder(testAttributesPath);
                assertEquals(verifyTestAttributesPath, checkFolder.getPath());
            }
            catch (FolderNotFoundException e)
            {
                assertTrue("Folder " + testAttributesPath + " NOT FOUND", false);
            }
            try
            {
                Folder checkFolder = pageManager.newFolder(testAttributesPath);
                pageManager.updateFolder(checkFolder);
                assertTrue("Duplicate Folder " + testAttributesPath + " CREATED", false);
            }
            catch (FolderNotUpdatedException e)
            {
                assertTrue("Duplicate Folder " + testAttributesPath + " NOT CREATED", true);
            }

            pageManager.removeFolder(folder);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.out);
            fail("could not create test data: "+e);
        }
    }
    
    private void dropTestData()
    {
        try
        {
            Folder folder = pageManager.getFolder("/");
            assertNotNull("folder should be found", folder);
            pageManager.removeFolder(folder);
        }
        catch (Exception e)
        {
            fail("could not remove test data: "+e);
        }
    }
}
