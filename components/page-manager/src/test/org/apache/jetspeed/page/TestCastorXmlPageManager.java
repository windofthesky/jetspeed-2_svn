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

// Java imports
import java.util.Collection;
import java.util.List;

import junit.framework.Test;

import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.NanoDeployerBasedTestSuite;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Property;

/**
 * TestPageXmlPersistence
 *
 * @author <a href="raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class TestCastorXmlPageManager extends AbstractComponentAwareTestCase
{
    private String testId = "test002";

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestCastorXmlPageManager(String name)
    {
        super(name);
    }

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestCastorXmlPageManager.class.getName()});
    }

    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        //return new TestSuite(TestCastorXmlPageManager.class);
        //ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestCastorXmlPageManager.class);
        // suite.setScript("org/apache/jetspeed/page/impl/registry.container.groovy");
        // return suite ;
    	NanoDeployerBasedTestSuite suite = new NanoDeployerBasedTestSuite(TestCastorXmlPageManager.class);
    	return suite;
    }

    public void testNewPage()
    {
        PageManager pm = (PageManager)getContainer().getComponentInstance(PageManager.class);
        assertNotNull("castor xml manager is null", pm);            
        Page testpage = pm.newPage();
        assertNotNull(testpage);
        assertNotNull(testpage.getId());
        assertNotNull(testpage.getRootFragment());
        assertNotNull(testpage.getRootFragment().getId());
    }

    public void testNewFragment()
    {
    	PageManager pm = (PageManager)getContainer().getComponentInstance(PageManager.class);
        assertNotNull("castor xml manager is null", pm);            
        Fragment f = pm.newFragment();
        assertNotNull(f);
        assertNotNull(f.getId());
        assertTrue(f.getType().equals(Fragment.LAYOUT));
    }

    public void testNewProperty()
    {
        // TODO: Fix Property manipulation API, too clumsy right now
    }

    public void testGetPage()
    {
        PageManager pm = (PageManager)getContainer().getComponentInstance(PageManager.class);
        assertNotNull("castor xml manager is null", pm);            
        Page testpage = pm.getPage("test001");
        assertNotNull(testpage);
        assertTrue(testpage.getId().equals("test001"));
        assertTrue(testpage.getTitle().equals("Test Page"));
        assertTrue(testpage.getAcl().equals("owner-only"));
        assertTrue(testpage.getDefaultSkin().equals("test-skin"));
        assertTrue(testpage.getDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(testpage.getDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));
        
        GenericMetadata md = testpage.getMetadata();
        Collection descriptions = md.getFields("description");
        Collection subjects = md.getFields("subject");
        assertEquals(2, descriptions.size());
        assertEquals(1, subjects.size());
        

        Fragment root = testpage.getRootFragment();
        assertNotNull(root);
        assertTrue(root.getId().equals("f001"));
        assertTrue(root.getName().equals("TwoColumns"));
        assertTrue(root.getType().equals(Fragment.LAYOUT));
        assertNull(root.getDecorator());

        List children = root.getFragments();
        assertNotNull(children);
        assertTrue(children.size() == 3);

        Fragment f = (Fragment) children.get(0);
        assertTrue(f.getId().equals("pe001"));
        assertTrue(f.getName().equals("HelloPortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));

        List properties = f.getProperties(root.getName());
        assertNotNull(properties);
        assertTrue(properties.size() == 2);
        assertTrue(((Property) properties.get(0)).getName().equals("row"));
        assertTrue(((Property) properties.get(0)).getValue().equals("0"));
        assertTrue(((Property) properties.get(1)).getName().equals("column"));
        assertTrue(((Property) properties.get(1)).getValue().equals("0"));

        f = (Fragment) children.get(1);
        assertTrue(f.getId().equals("pe002"));
        assertTrue(f.getName().equals("JMXPortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));

        properties = f.getProperties(root.getName());
        assertNotNull(properties);
        assertTrue(properties.size() == 2);
        assertTrue(((Property) properties.get(0)).getName().equals("row"));
        assertTrue(((Property) properties.get(0)).getValue().equals("0"));
        assertTrue(((Property) properties.get(1)).getName().equals("column"));
        assertTrue(((Property) properties.get(1)).getValue().equals("1"));

        f = testpage.getFragmentById("f002");
        assertNotNull(f);
        assertTrue(f.getId().equals("f002"));
        assertTrue(f.getName().equals("Card"));
        assertTrue(f.getType().equals(Fragment.LAYOUT));
        assertTrue(f.getDecorator().equals("Tab"));
        assertNotNull(f.getFragments());
        assertTrue(f.getFragments().size() == 2);
    }

    
    public void testRegisterPage() throws Exception
    {
        PageManager pm = (PageManager)getContainer().getComponentInstance(PageManager.class);
        assertNotNull("castor xml manager is null", pm);            
        Page page = pm.newPage();
        System.out.println("Retrieved test_id in register " + this.testId);
        page.setId(this.testId);
        page.setDefaultSkin("myskin");
        page.setTitle("Registered Page");

        Fragment root = page.getRootFragment();
        root.setName("TestLayout");
        Fragment f = pm.newFragment();
        f.setType(Fragment.PORTLET);
        f.setName("TestPortlet");
        Property p = pm.newProperty();
        p.setLayout("TestLayout");
        p.setName("row");
        p.setValue("0");
        f.addProperty(p);
        p = pm.newProperty();
        p.setLayout("TestLayout");
        p.setName("column");
        p.setValue("0");
        f.addProperty(p);
        root.getFragments().add(f);

        try
        {
            pm.registerPage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page registratio: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        page = pm.getPage(this.testId);
        assertNotNull(page);
        assertTrue(page.getId().equals(this.testId));
        assertTrue(page.getTitle().equals("Registered Page"));
        assertNotNull(page.getRootFragment());
        assertTrue(page.getRootFragment().getName().equals("TestLayout"));
        assertTrue(page.getRootFragment().getFragments().size() == 1);

        f = (Fragment) page.getRootFragment().getFragments().get(0);
        assertNotNull(f.getProperties("TestLayout"));
        assertTrue(((Property) f.getProperties("TestLayout").get(0)).getValue().equals("0"));
    }

    public void testUpdatePage() throws Exception
    {
        PageManager pm = (PageManager)getContainer().getComponentInstance(PageManager.class);
        assertNotNull("castor xml manager is null", pm);            
        Page page = pm.getPage(this.testId);
        page.setTitle("Updated Title");

        try
        {
            pm.updatePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        page = pm.getPage(this.testId);
        assertTrue(page.getTitle().equals("Updated Title"));
    }

    public void testListPages() throws Exception
    {
        PageManager pm = (PageManager)getContainer().getComponentInstance(PageManager.class);
        assertNotNull("castor xml manager is null", pm);            
        List pages = pm.listPages();
        assertTrue(pages.size() == 3);
        assertTrue(pages.contains(this.testId));
        assertTrue(pages.contains("test001"));
    }

    public void testRemovePage() throws Exception
    {
        PageManager pm = (PageManager)getContainer().getComponentInstance(PageManager.class);
        assertNotNull("castor xml manager is null", pm);            
        Page page = pm.getPage(this.testId);

        try
        {
            pm.removePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page remove: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        page = pm.getPage(this.testId);
        assertNull(page);
    }
}
