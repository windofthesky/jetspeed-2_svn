/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.page;

// Java imports
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.PortalComponentAssemblyTestCase;
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Property;

/**
 * TestPageXmlPersistence
 *
 * @author <a href="raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class TestCastorXmlPageManager extends PortalComponentAssemblyTestCase
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
        return new TestSuite(TestCastorXmlPageManager.class);
        //ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestCastorXmlPageManager.class);
        // suite.setScript("org/apache/jetspeed/page/impl/registry.container.groovy");
        // return suite ;      
    }

    public void testNewPage()
    {
        PageManager pm = (PageManager)componentManager.getComponent("CastorXmlPageManager");
        assertNotNull("castor xml manager is null", pm);            
        Page testpage = pm.newPage();
        assertNotNull(testpage);
        assertNotNull(testpage.getId());
        assertNotNull(testpage.getRootFragment());
        assertNotNull(testpage.getRootFragment().getId());
    }

    public void testNewFragment()
    {
        PageManager pm = (PageManager)componentManager.getComponent("CastorXmlPageManager");
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
        PageManager pm = (PageManager)componentManager.getComponent("CastorXmlPageManager");
        assertNotNull("castor xml manager is null", pm);            
        Page testpage = pm.getPage("test001");
        assertNotNull(testpage);
        assertTrue(testpage.getId().equals("test001"));
        assertTrue(testpage.getTitle().equals("Test Page"));
        assertTrue(testpage.getAcl().equals("owner-only"));
        assertTrue(testpage.getDefaultSkin().equals("test-skin"));
        assertTrue(testpage.getDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(testpage.getDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));

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
        PageManager pm = (PageManager)componentManager.getComponent("CastorXmlPageManager");
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
        PageManager pm = (PageManager)componentManager.getComponent("CastorXmlPageManager");
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
        PageManager pm = (PageManager)componentManager.getComponent("CastorXmlPageManager");
        assertNotNull("castor xml manager is null", pm);            
        List pages = pm.listPages();
        assertTrue(pages.size() == 3);
        assertTrue(pages.contains(this.testId));
        assertTrue(pages.contains("test001"));
    }

    public void testRemovePage() throws Exception
    {
        PageManager pm = (PageManager)componentManager.getComponent("CastorXmlPageManager");
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
