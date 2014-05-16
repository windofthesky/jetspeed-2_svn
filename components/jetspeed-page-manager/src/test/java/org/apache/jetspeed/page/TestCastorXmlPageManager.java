/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.MenuDefinition;
import org.apache.jetspeed.om.folder.MenuDefinitionElement;
import org.apache.jetspeed.om.folder.MenuExcludeDefinition;
import org.apache.jetspeed.om.folder.MenuIncludeDefinition;
import org.apache.jetspeed.om.folder.MenuOptionsDefinition;
import org.apache.jetspeed.om.folder.MenuSeparatorDefinition;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.Document;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.om.page.FragmentReference;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageFragment;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.psml.CastorXmlPageManager;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.PrincipalsSet;
import org.apache.jetspeed.test.JetspeedTestCase;

import javax.security.auth.Subject;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * TestCastorXmlPageManager
 * 
 * @author <a href="raphael@apache.org">Rapha\u00ebl Luta</a>
 * @author <a href="rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class TestCastorXmlPageManager extends JetspeedTestCase implements PageManagerTestShared 
{
    protected CastorXmlPageManager pageManager;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        pageManager = Shared.makeCastorXMLPageManager(getBaseDir(), "pages", false, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
        Shared.shutdownCastorXMLPageManager(pageManager);
    }

    /**
     * Defines the testcase name for JUnit.
     * 
     * @param name
     *            the testcase's name.
     */
    public TestCastorXmlPageManager( String name )
    {
        super(name);
    }

    /**
     * Start the tests.
     * 
     * @param args
     *            the arguments. Not used
     */
    public static void main( String args[] )
    {
        junit.awtui.TestRunner.main(new String[]{TestCastorXmlPageManager.class.getName()});
    }

    /**
     * Creates the test suite.
     * 
     * @return a test suite (<code>TestSuite</code>) that includes all
     *         methods starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestCastorXmlPageManager.class);
    }

    /**
     * JDK7 junit legacy adapter: runs all tests in order.
     *
     * By default junit runs the tests in the order defined in the
     * class definition. With JDK7, the class reflection APIs no
     * longer return methods in order of definition. Instead, they
     * are effectively randomized. Test suite/classes that relied
     * on the test execution order now need to be explicitly run
     * in order. 
     */
    public void testAllInOrder() throws Exception {
        doTestNewPage();
        doTestNewFragment();
        doTestNewFolder();
        doTestNewLink();
        doTestGetPage();
        doTestCreatePage();
        doTestCreateFolder();
        doTestCreateLink();
        doTestUpdatePage();
        doTestUpdateFolder();
        doTestUpdateLink();
        doTestGetFolders();
        doTestFolders();
        doTestFolderMetaData();
        doTestDefaultTitles();
        doTestPageMetaData();
        doTestLinks();
        doTestMenuDefinitions();
        doTestRemovePage();
        doTestRemoveFolder();
        doTestRemoveLink();
        doTestClonePage();
        doTestIdGeneration();
    }

    public void doTestNewPage()
    {
        pageManager.reset();
        Page testpage = pageManager.newPage("/test003.psml");
        assertNotNull(testpage);
        assertNotNull(testpage.getId());
        assertNotNull(testpage.getPath());
        assertEquals(testpage.getId(), testpage.getPath());
        assertNotNull(testpage.getRootFragment());
        assertNotNull(testpage.getRootFragment().getId());
        assertTrue(testpage.getRootFragment() instanceof Fragment);
        assertEquals(((Fragment)testpage.getRootFragment()).getType(), Fragment.LAYOUT);

        PageTemplate testpagetemplate = pageManager.newPageTemplate("/test003.tpsml");
        assertNotNull(testpagetemplate);
        assertNotNull(testpagetemplate.getId());
        assertNotNull(testpagetemplate.getPath());
        assertEquals(testpagetemplate.getId(), testpagetemplate.getPath());
        assertNotNull(testpagetemplate.getRootFragment());
        assertNotNull(testpagetemplate.getRootFragment().getId());
        assertTrue(testpagetemplate.getRootFragment() instanceof Fragment);
        assertEquals(((Fragment)testpagetemplate.getRootFragment()).getType(), Fragment.LAYOUT);

        DynamicPage testdynamicpage = pageManager.newDynamicPage("/test003.dpsml");
        assertNotNull(testdynamicpage);
        assertNotNull(testdynamicpage.getId());
        assertNotNull(testdynamicpage.getPath());
        assertEquals(testdynamicpage.getId(), testdynamicpage.getPath());
        assertNotNull(testdynamicpage.getRootFragment());
        assertNotNull(testdynamicpage.getRootFragment().getId());
        assertTrue(testdynamicpage.getRootFragment() instanceof Fragment);
        assertEquals(((Fragment)testdynamicpage.getRootFragment()).getType(), Fragment.LAYOUT);

        FragmentDefinition testfragmentdefinition = pageManager.newFragmentDefinition("/test003.fpsml");
        assertNotNull(testfragmentdefinition);
        assertNotNull(testfragmentdefinition.getId());
        assertNotNull(testfragmentdefinition.getPath());
        assertEquals(testfragmentdefinition.getId(), testfragmentdefinition.getPath());
        assertNotNull(testfragmentdefinition.getRootFragment());
        assertNotNull(testfragmentdefinition.getRootFragment().getId());
        assertTrue(testfragmentdefinition.getRootFragment() instanceof Fragment);
        assertEquals(((Fragment)testfragmentdefinition.getRootFragment()).getType(), Fragment.PORTLET);
    }

    public void doTestNewFragment()
    {
        pageManager.reset();
        Fragment f = pageManager.newFragment();
        assertNotNull(f);
        assertNotNull(f.getId());
        assertTrue(f.getType().equals(Fragment.LAYOUT));
    }

    public void doTestNewFolder()
    {
        pageManager.reset();
        Folder testfolder = pageManager.newFolder("/folder3");
        assertNotNull(testfolder);
        assertNotNull(testfolder.getId());
        assertNotNull(testfolder.getPath());
        assertEquals(testfolder.getId(), testfolder.getPath());
    }

    public void doTestNewLink()
    {
        pageManager.reset();
        Link testlink = pageManager.newLink("/test003.link");
        assertNotNull(testlink);
        assertNotNull(testlink.getId());
        assertNotNull(testlink.getPath());
        assertEquals(testlink.getId(), testlink.getPath());
    }

    public void doTestGetPage() throws Exception
    {
        pageManager.reset();
        Page testpage = pageManager.getPage("/test001.psml");
        assertNotNull(testpage);
        assertTrue(testpage.getId().equals("/test001.psml"));
        assertTrue(testpage.getTitle().equals("Test Page"));
        assertTrue(testpage.getSkin().equals("test-skin"));
        assertTrue(testpage.getEffectiveDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(testpage.getDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(testpage.getDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));
        assertTrue(testpage.getVersion().equals("2.77"));

        GenericMetadata md = testpage.getMetadata();
        Collection<LocalizedField> descriptions = md.getFields("description");
        Collection<LocalizedField> subjects = md.getFields("subject");
        assertEquals(2, descriptions.size());
        assertEquals(1, subjects.size());

        BaseFragmentElement rootFragmentElement = testpage.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        Fragment root = (Fragment)rootFragmentElement;
        assertNotNull(root);
        assertTrue(root.getId().equals("f001"));
        assertTrue(root.getName().equals("TwoColumns"));
        assertTrue(root.getType().equals(Fragment.LAYOUT));
        assertNull(root.getDecorator());

        List<BaseFragmentElement> children = root.getFragments();
        assertNotNull(children);
        assertTrue(children.size() == 4);

        Fragment f = (Fragment) children.get(0);
        assertTrue(f.getId().equals("pe001"));
        assertTrue(f.getName().equals("HelloPortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));

        List<FragmentProperty> properties = f.getProperties();
        assertNotNull(properties);
        assertEquals(11, properties.size());
        assertEquals("0", f.getProperty(Fragment.ROW_PROPERTY_NAME));
        assertEquals(0, f.getIntProperty(Fragment.COLUMN_PROPERTY_NAME));
        assertEquals(0, f.getLayoutRow());
        assertEquals(0, f.getLayoutColumn());
        assertNotNull(f.getProperty(Fragment.X_PROPERTY_NAME));
        assertTrue(f.getProperty(Fragment.X_PROPERTY_NAME).startsWith("11.1"));
        assertTrue((f.getLayoutX() > 11.0F) && (f.getLayoutX() < 12.0F));
        assertTrue((f.getFloatProperty(Fragment.X_PROPERTY_NAME) > 11.0F) &&
                   (f.getFloatProperty(Fragment.X_PROPERTY_NAME) < 12.0F));
        assertTrue((f.getLayoutY() > 22.0F) && (f.getLayoutY() < 23.0F));
        assertTrue((f.getLayoutZ() > 33.0F) && (f.getLayoutZ() < 34.0F));
        assertTrue((f.getLayoutWidth() > 44.0F) && (f.getLayoutWidth() < 45.0F));
        assertTrue((f.getLayoutHeight() > 55.0F) && (f.getLayoutHeight() < 56.0F));
        assertEquals("custom-value-0", f.getProperty("custom-0"));
        assertEquals("custom-value-1", f.getProperty("custom-1"));
        assertEquals("custom-value-2", f.getProperty("custom-2"));
        assertEquals("custom-value-3", f.getProperty("custom-3"));

        final Fragment userFragment = f;
        Exception userException = (Exception)JSSubject.doAsPrivileged(constructUserSubject(), new PrivilegedAction()
        {
            public Object run()
            {
                try
                {
                    assertTrue(userFragment.getId().equals("pe001"));
                    List<FragmentProperty> properties = userFragment.getProperties();
                    assertNotNull(properties);
                    assertEquals((FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED ? 17 : 12), properties.size());
                    assertEquals("0", userFragment.getProperty(Fragment.ROW_PROPERTY_NAME));
                    assertEquals(0, userFragment.getIntProperty(Fragment.COLUMN_PROPERTY_NAME));
                    assertTrue((userFragment.getLayoutHeight() > 55.0F) && (userFragment.getLayoutHeight() < 56.0F));
                    assertEquals("custom-value-0", userFragment.getProperty("custom-0"));
                    assertNull(userFragment.getProperty("custom-0", Fragment.USER_PROPERTY_SCOPE, null));
                    assertNull(userFragment.getProperty("custom-0", Fragment.USER_PROPERTY_SCOPE, "user"));
                    if (FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
                    {
                        assertEquals("custom-value-role-1", userFragment.getProperty("custom-1"));
                        assertNotNull(userFragment.getProperty("custom-1", Fragment.ROLE_PROPERTY_SCOPE, "role"));
                    }
                    else
                    {
                        assertEquals("custom-value-1", userFragment.getProperty("custom-1"));                        
                    }
                    assertNull(userFragment.getProperty("custom-1", Fragment.USER_PROPERTY_SCOPE, "user"));
                    if (FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
                    {
                        assertEquals("custom-value-group-2", userFragment.getProperty("custom-2"));
                        assertNotNull(userFragment.getProperty("custom-2", Fragment.GROUP_PROPERTY_SCOPE, "group"));
                    }
                    else
                    {
                        assertEquals("custom-value-2", userFragment.getProperty("custom-2"));                        
                    }
                    assertNull(userFragment.getProperty("custom-2", Fragment.USER_PROPERTY_SCOPE, "user"));
                    assertEquals("custom-value-user-3", userFragment.getProperty("custom-3"));
                    assertNotNull(userFragment.getProperty("custom-3", Fragment.USER_PROPERTY_SCOPE, null));
                    assertNotNull(userFragment.getProperty("custom-3", Fragment.USER_PROPERTY_SCOPE, "user"));
                    return null;
                }
                catch (Exception e)
                {
                    return e;
                }
                finally
                {
                    JSSubject.clearSubject();
                }
            }
        }, null);
        if (userException != null)
        {
            throw userException;
        }

        List<FragmentPreference> preferences = f.getPreferences();
        assertNotNull(preferences);
        assertTrue(preferences.size() == 2);
        assertEquals("pref0", preferences.get(0).getName());
        assertTrue(preferences.get(0).isReadOnly());
        assertNotNull(preferences.get(0).getValueList());
        assertEquals(2, preferences.get(0).getValueList().size());
        assertEquals("pref0-value0", preferences.get(0).getValueList().get(0));
        assertEquals("pref0-value1", preferences.get(0).getValueList().get(1));
        assertEquals("pref1", preferences.get(1).getName());
        assertFalse(preferences.get(1).isReadOnly());
        assertNotNull(preferences.get(1).getValueList());
        assertEquals(1, preferences.get(1).getValueList().size());
        assertEquals("pref1-value", preferences.get(1).getValueList().get(0));

        f = (Fragment) children.get(1);
        assertTrue(f.getId().equals("pe002"));
        assertTrue(f.getName().equals("JMXPortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));

        properties = f.getProperties();
        assertNotNull(properties);
        assertEquals(2, properties.size());
        assertEquals("0", f.getProperty(Fragment.ROW_PROPERTY_NAME));
        assertEquals(1, f.getIntProperty(Fragment.COLUMN_PROPERTY_NAME));

        BaseFragmentElement bf = testpage.getFragmentById("f002");
        assertTrue(bf instanceof Fragment);
        f = (Fragment)bf;
        assertNotNull(f);
        assertTrue(f.getId().equals("f002"));
        assertTrue(f.getName().equals("Card"));
        assertTrue(f.getType().equals(Fragment.LAYOUT));
        assertTrue(f.getDecorator().equals("Tab"));
        assertNotNull(f.getFragments());
        assertTrue(f.getFragments().size() == 2);

        bf = testpage.getFragmentById("f003");
        assertTrue(bf instanceof FragmentReference);
        FragmentReference fr = (FragmentReference)bf;
        assertTrue(fr.getId().equals("f003"));
        assertTrue(fr.getRefId().equals("test001"));

        List<BaseFragmentElement> fragments = testpage.getFragmentsByName("JMXPortlet");
        assertNotNull(fragments);
        assertEquals(1, fragments.size());
        assertTrue(((Fragment)fragments.get(0)).getId().equals("pe002"));
        assertTrue(((Fragment)fragments.get(0)).getName().equals("JMXPortlet"));
        assertTrue(((Fragment)fragments.get(0)).getType().equals(Fragment.PORTLET));
        List<BaseFragmentElement> fragmentsByInterface = testpage.getFragmentsByInterface(null);
        assertNotNull(fragmentsByInterface);
        assertEquals(7, fragmentsByInterface.size());
        fragmentsByInterface = testpage.getFragmentsByInterface(FragmentReference.class);
        assertNotNull(fragmentsByInterface);
        assertEquals(1, fragmentsByInterface.size());

        PageTemplate testpagetemplate = pageManager.getPageTemplate("/test001.tpsml");
        assertNotNull(testpagetemplate);
        assertTrue(testpagetemplate.getId().equals("/test001.tpsml"));
        assertTrue(testpagetemplate.getTitle().equals("Test Page Template"));
        assertTrue(testpagetemplate.getSkin().equals("test-template-skin"));
        assertTrue(testpagetemplate.getDefaultDecorator(Fragment.LAYOUT).equals("test-template-layout"));
        assertTrue(testpagetemplate.getDefaultDecorator(Fragment.PORTLET).equals("test-template-portlet"));
        assertTrue(testpagetemplate.getVersion().equals("2.77"));    
        md = testpagetemplate.getMetadata();
        descriptions = md.getFields("description");
        assertNotNull(descriptions);
        assertEquals(1, descriptions.size());
        rootFragmentElement = testpagetemplate.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertNotNull(root);
        assertTrue(root.getId().equals("pt-f001"));
        assertTrue(root.getName().equals("TwoColumns"));
        assertTrue(root.getType().equals(Fragment.LAYOUT));
        assertNotNull(root.getFragments());
        assertTrue(root.getFragments().size() == 3);
        f = (Fragment)root.getFragments().get(0);
        assertTrue(f.getId().equals("pt-f002"));
        assertTrue(f.getName().equals("TemplatePortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));
        bf = testpagetemplate.getFragmentById("pt-f003");
        assertTrue(bf instanceof PageFragment);
        PageFragment pf = (PageFragment)bf;
        assertTrue(pf.getId().equals("pt-f003"));
        bf = testpagetemplate.getFragmentById("pt-f004");
        assertTrue(bf instanceof FragmentReference);
        fr = (FragmentReference)bf;
        assertTrue(fr.getId().equals("pt-f004"));
        assertTrue(fr.getRefId().equals("test001"));

        DynamicPage testdynamicpage = pageManager.getDynamicPage("/test001.dpsml");
        assertTrue(testdynamicpage.getId().equals("/test001.dpsml"));
        assertTrue(testdynamicpage.getContentType().equals("default"));        
        assertTrue(testdynamicpage.isInheritable());        
        assertTrue(testdynamicpage.getTitle().equals("Test Dynamic Page"));
        assertTrue(testdynamicpage.getVersion().equals("2.77"));
        rootFragmentElement = testdynamicpage.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertNotNull(root);
        assertTrue(root.getId().equals("dp-f001"));
        assertTrue(root.getName().equals("TwoColumns"));
        assertTrue(root.getType().equals(Fragment.LAYOUT));
        assertNotNull(root.getFragments());
        assertTrue(root.getFragments().size() == 2);
        f = (Fragment)root.getFragments().get(0);
        assertTrue(f.getId().equals("dp-f002"));
        assertTrue(f.getName().equals("HelloPortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));
        bf = testdynamicpage.getFragmentById("dp-f003");
        assertTrue(bf instanceof FragmentReference);
        fr = (FragmentReference)bf;
        assertTrue(fr.getId().equals("dp-f003"));
        assertTrue(fr.getRefId().equals("test001"));
    
        FragmentDefinition testfragmentdefinition = pageManager.getFragmentDefinition("/test001.fpsml");
        assertTrue(testfragmentdefinition.getId().equals("/test001.fpsml"));
        assertTrue(testfragmentdefinition.getTitle().equals("Test Fragment Definition"));
        assertTrue(testfragmentdefinition.getVersion().equals("2.77"));
        rootFragmentElement = testfragmentdefinition.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertNotNull(root);
        assertTrue(root.getId().equals("test001"));
        assertTrue(root.getName().equals("HelloPortlet"));
        assertTrue(root.getType().equals(Fragment.PORTLET));
        assertTrue(root.getFragments().isEmpty());
    }

    public void doTestCreatePage() throws Exception
    {
        pageManager.reset();
        Page page = pageManager.newPage("/test002.psml");
        System.out.println("Retrieved test_id in create " + "/test002.psml");
        page.setSkin("myskin");
        page.setTitle("Created Page");
        GenericMetadata metadata = page.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "Created Page de PSML");
        metadata.addField(Locale.JAPANESE, "title", "Created \u3078\u3088\u3046\u3053\u305d");

        BaseFragmentElement rootFragmentElement = page.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        Fragment root = (Fragment)rootFragmentElement;
        root.setName("TestLayout");
        Fragment f = pageManager.newFragment();
        f.setType(Fragment.PORTLET);
        f.setName("TestPortlet");
        List<FragmentProperty> properties = f.getProperties();
        FragmentProperty fp = pageManager.newFragmentProperty();
        fp.setName(Fragment.ROW_PROPERTY_NAME);
        fp.setValue("0");
        properties.add(fp);
        fp = pageManager.newFragmentProperty();
        fp.setName(Fragment.COLUMN_PROPERTY_NAME);
        fp.setValue("0");
        properties.add(fp);
        f.setLayoutX(100.0F);
        f.setLayoutY(100.0F);
        f.setProperty("custom-0", null, null, "custom-value-0");
        f.setProperty("custom-1", null, null, "custom-value-1");
        f.setProperty("custom-2", null, null, "custom-value-2");
        root.getFragments().add(f);
        FragmentReference fr = pageManager.newFragmentReference();
        fr.setRefId("test002");
        root.getFragments().add(fr);

        SecurityConstraints constraints = page.newSecurityConstraints();
        constraints.setOwner("new-user");
        List<SecurityConstraint> constraintsList = new ArrayList<SecurityConstraint>(1);
        SecurityConstraint constraint = page.newSecurityConstraint();
        constraint.setUsers(Shared.makeListFromCSV("user10,user11"));
        constraint.setRoles(Shared.makeListFromCSV("*"));
        constraint.setPermissions(Shared.makeListFromCSV(JetspeedActions.EDIT + "," + JetspeedActions.VIEW));
        constraintsList.add(constraint);
        constraints.setSecurityConstraints(constraintsList);
        List<String> constraintsRefsList = new ArrayList<String>(1);
        constraintsRefsList.add("public-view");
        constraints.setSecurityConstraintsRefs(constraintsRefsList);
        page.setSecurityConstraints(constraints);

        try
        {
            pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
        
        final Fragment userFragment = (Fragment)((Fragment)page.getRootFragment()).getFragments().get(0);
        Exception userException = (Exception)JSSubject.doAsPrivileged(constructUserSubject(), new PrivilegedAction()
        {
            public Object run()
            {
                try
                {
                    userFragment.setProperty("custom-1", Fragment.USER_PROPERTY_SCOPE, "user", "custom-value-user-1");
                    userFragment.setProperty("custom-2", Fragment.USER_PROPERTY_SCOPE, null, "custom-value-user-2");

                    try
                    {
                        pageManager.updateFragmentProperties(userFragment, PageManager.USER_PROPERTY_SCOPE);
                    }
                    catch (Exception e)
                    {
                        String errmsg = "Exception in page fragment properties update: " + e.toString();
                        e.printStackTrace();
                        System.err.println(errmsg);
                        assertNotNull(errmsg, null);
                    }
                    return null;
                }
                catch (Exception e)
                {
                    return e;
                }
                finally
                {
                    JSSubject.clearSubject();
                }
            }
        }, null);
        if (userException != null)
        {
            throw userException;
        }

        page = pageManager.getPage("/test002.psml");
        assertNotNull(page);
        assertTrue(page.getId().equals("/test002.psml"));
        assertEquals("Created Page", page.getTitle());
        assertEquals("Created Page de PSML", page.getTitle(Locale.FRENCH));
        assertEquals("Created \u3078\u3088\u3046\u3053\u305d", page.getTitle(Locale.JAPANESE));
        assertNotNull(page.getRootFragment());
        rootFragmentElement = page.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertTrue(root.getName().equals("TestLayout"));
        assertTrue(root.getFragments().size() == 2);
        BaseFragmentElement bf = root.getFragments().get(0);
        assertTrue(bf instanceof Fragment);
        f = (Fragment)bf;
        assertNotNull(f.getProperties());
        assertEquals(0, f.getIntProperty(Fragment.ROW_PROPERTY_NAME));
        assertTrue((99.9F < f.getLayoutX()) && (100.1F > f.getLayoutX()));
        assertEquals("custom-value-0", f.getProperty("custom-0"));
        assertEquals("custom-value-1", f.getProperty("custom-1"));
        final Fragment userFragment2 = f;
        Exception userException2 = (Exception)JSSubject.doAsPrivileged(constructUserSubject(), new PrivilegedAction()
        {
            public Object run()
            {
                try
                {
                    assertEquals("custom-value-0", userFragment2.getProperty("custom-0"));
                    assertEquals("custom-value-user-1", userFragment2.getProperty("custom-1"));
                    assertEquals("custom-value-user-2", userFragment2.getProperty("custom-2"));
                    return null;
                }
                catch (Exception e)
                {
                    return e;
                }
                finally
                {
                    JSSubject.clearSubject();
                }
            }
        }, null);
        if (userException2 != null)
        {
            throw userException2;
        }

        PageTemplate pagetemplate = pageManager.newPageTemplate("/test002.tpsml");
        pagetemplate.setTitle("Created Page Template");
        metadata = pagetemplate.getMetadata();
        metadata.addField(null, "description", "Page Template Description");
        rootFragmentElement = pagetemplate.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        root.setName("TestLayout");
        f = pageManager.newFragment();
        f.setType(Fragment.PORTLET);
        f.setName("TestPortlet");
        properties = f.getProperties();
        fp = pageManager.newFragmentProperty();
        fp.setName(Fragment.ROW_PROPERTY_NAME);
        fp.setValue("1");
        properties.add(fp);
        fp = pageManager.newFragmentProperty();
        fp.setName(Fragment.COLUMN_PROPERTY_NAME);
        fp.setValue("1");
        properties.add(fp);
        root.getFragments().add(f);
        fr = pageManager.newFragmentReference();
        fr.setRefId("test002");
        root.getFragments().add(fr);
        PageFragment pf = pageManager.newPageFragment();
        root.getFragments().add(pf);        
        constraints = pagetemplate.newSecurityConstraints();
        constraints.setOwner("new-user");
        pagetemplate.setSecurityConstraints(constraints);
        try
        {
            pageManager.updatePageTemplate(pagetemplate);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page template update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
        pagetemplate = pageManager.getPageTemplate("/test002.tpsml");
        assertNotNull(pagetemplate);
        assertTrue(pagetemplate.getId().equals("/test002.tpsml"));
        assertEquals("Created Page Template", pagetemplate.getTitle());
        assertNotNull(pagetemplate.getRootFragment());
        rootFragmentElement = pagetemplate.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertTrue(root.getName().equals("TestLayout"));
        assertTrue(root.getType().equals(Fragment.LAYOUT));
        assertTrue(root.getFragments().size() == 3);
        bf = root.getFragments().get(0);
        assertTrue(bf instanceof Fragment);
        f = (Fragment)bf;
        assertTrue(f.getName().equals("TestPortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));
        assertNotNull(f.getProperties());
        assertEquals(1, f.getIntProperty(Fragment.ROW_PROPERTY_NAME));
        bf = root.getFragments().get(1);
        assertTrue(bf instanceof FragmentReference);
        fr = (FragmentReference)bf;
        assertTrue(fr.getRefId().equals("test002"));
        bf = root.getFragments().get(2);
        assertTrue(bf instanceof PageFragment);

        DynamicPage dynamicpage = pageManager.newDynamicPage("/test002.dpsml");
        dynamicpage.setContentType("default");        
        dynamicpage.setInheritable(false);        
        dynamicpage.setTitle("Created Dynamic Page");
        rootFragmentElement = dynamicpage.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        root.setName("TestLayout");
        f = pageManager.newFragment();
        f.setType(Fragment.PORTLET);
        f.setName("TestPortlet");
        root.getFragments().add(f);        
        fr = pageManager.newFragmentReference();
        fr.setRefId("test002");
        root.getFragments().add(fr);        
        try
        {
            pageManager.updateDynamicPage(dynamicpage);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in dynamic page update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
        dynamicpage = pageManager.getDynamicPage("/test002.dpsml");
        assertNotNull(dynamicpage);
        assertTrue(dynamicpage.getId().equals("/test002.dpsml"));
        assertEquals("default", dynamicpage.getContentType());
        assertFalse(dynamicpage.isInheritable());
        assertEquals("Created Dynamic Page", dynamicpage.getTitle());
        assertNotNull(dynamicpage.getRootFragment());
        rootFragmentElement = dynamicpage.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertTrue(root.getName().equals("TestLayout"));
        assertTrue(root.getType().equals(Fragment.LAYOUT));
        assertTrue(root.getFragments().size() == 2);
        bf = root.getFragments().get(0);
        assertTrue(bf instanceof Fragment);
        f = (Fragment)bf;
        assertTrue(f.getName().equals("TestPortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));
        bf = root.getFragments().get(1);
        assertTrue(bf instanceof FragmentReference);
        fr = (FragmentReference)bf;
        assertTrue(fr.getRefId().equals("test002"));

        FragmentDefinition fragmentdefinition = pageManager.newFragmentDefinition("/test002.fpsml");
        fragmentdefinition.setTitle("Created Fragment Definition");
        rootFragmentElement = fragmentdefinition.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        root.setName("TestPortlet");
        root.setId("test002");
        try
        {
            pageManager.updateFragmentDefinition(fragmentdefinition);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in fragment definition update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
        fragmentdefinition = pageManager.getFragmentDefinition("/test002.fpsml");
        assertNotNull(fragmentdefinition);
        assertTrue(fragmentdefinition.getId().equals("/test002.fpsml"));
        assertEquals("Created Fragment Definition", fragmentdefinition.getTitle());
        assertNotNull(fragmentdefinition.getRootFragment());
        rootFragmentElement = fragmentdefinition.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertTrue(root.getId().equals("test002"));
        assertTrue(root.getName().equals("TestPortlet"));
        assertTrue(root.getType().equals(Fragment.PORTLET));
        assertTrue(root.getFragments().isEmpty());
    }

    public void doTestCreateFolder() throws Exception
    {
        pageManager.reset();
        Folder folder = pageManager.newFolder("/folder2");
        System.out.println("Retrieved test_id in create " + "/folder2");
        folder.setTitle("Created Folder");
        folder.setSkin("test-skin");
        folder.setDefaultDecorator("test-layout", Fragment.LAYOUT);
        folder.setDefaultDecorator("test-portlet", Fragment.PORTLET);

        try
        {
            pageManager.updateFolder(folder);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in folder update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        folder = pageManager.getFolder("/folder2");
        assertNotNull(folder);
        assertTrue(folder.getId().equals("/folder2"));
        assertTrue(folder.getTitle().equals("Created Folder"));
        assertTrue(folder.getSkin().equals("test-skin"));
        assertTrue(folder.getEffectiveDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(folder.getDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(folder.getDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));
    }

    public void doTestCreateLink() throws Exception
    {
        pageManager.reset();
        Link link = pageManager.newLink("/test002.link");
        System.out.println("Retrieved test_id in create " + "/test002.link");
        link.setTitle("Created Link");
        link.setSkin("test-skin");
        link.setUrl("http://www.created.link.com/");

        try
        {
            pageManager.updateLink(link);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in link update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        link = pageManager.getLink("/test002.link");
        assertNotNull(link);
        assertTrue(link.getId().equals("/test002.link"));
        assertTrue(link.getTitle().equals("Created Link"));
        assertTrue(link.getSkin().equals("test-skin"));
    }

    public void doTestUpdatePage() throws Exception
    {
        pageManager.reset();
        Page page = pageManager.getPage("/test002.psml");
        page.setTitle("Updated Title");
        BaseFragmentElement rootFragmentElement = page.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        Fragment root = (Fragment)rootFragmentElement;
        assertNotNull(root);
        assertNotNull(root.getFragments());
        assertEquals(2, root.getFragments().size());
//        String testId = ((Fragment)root.getFragments().get(0)).getId();
//        assertNotNull(page.removeFragmentById(testId));

        try
        {
            pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        page = pageManager.getPage("/test002.psml");
        assertTrue(page.getTitle().equals("Updated Title"));
        rootFragmentElement = page.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertNotNull(root);
        assertNotNull(root.getFragments());
//        assertEquals(1, root.getFragments().size());

        PageTemplate pagetemplate = pageManager.getPageTemplate("/test002.tpsml");
        pagetemplate.setTitle("Updated Title");
        try
        {
            pageManager.updatePageTemplate(pagetemplate);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page template update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
        pagetemplate = pageManager.getPageTemplate("/test002.tpsml");
        assertNotNull(pagetemplate);
        assertEquals("Updated Title", pagetemplate.getTitle());

        DynamicPage dynamicpage = pageManager.getDynamicPage("/test002.dpsml");
        dynamicpage.setTitle("Updated Title");
        try
        {
            pageManager.updateDynamicPage(dynamicpage);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in dynamic page update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
        dynamicpage = pageManager.getDynamicPage("/test002.dpsml");
        assertNotNull(dynamicpage);
        assertEquals("Updated Title", dynamicpage.getTitle());

        FragmentDefinition fragmentdefinition = pageManager.getFragmentDefinition("/test002.fpsml");
        fragmentdefinition.setTitle("Updated Title");
        try
        {
            pageManager.updateFragmentDefinition(fragmentdefinition);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in fragment definition update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
        fragmentdefinition = pageManager.getFragmentDefinition("/test002.fpsml");
        assertNotNull(fragmentdefinition);
        assertEquals("Updated Title", fragmentdefinition.getTitle());
    }

    public void doTestUpdateFolder() throws Exception
    {
        pageManager.reset();
        Folder folder = pageManager.getFolder("/folder2");
        folder.setTitle("Updated Title");

        try
        {
            pageManager.updateFolder(folder);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in folder update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        folder = pageManager.getFolder("/folder2");
        assertTrue(folder.getTitle().equals("Updated Title"));

        Page page = pageManager.newPage("/folder2/test004.psml");
        page.setTitle("Folder Page");

        try
        {
            pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        assertEquals(1, folder.getPages().size());
        assertEquals(1, pageManager.getPages(folder).size());
        assertNotNull(folder.getPages().get("/folder2/test004.psml"));

        folder.setTitle("Updated Deep Title");
        page.setTitle("Updated Deep Title");

        try
        {
            pageManager.updateFolder(folder, true);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in deep folder update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        folder = pageManager.getFolder("/folder2");
        assertTrue(folder.getTitle().equals("Updated Deep Title"));
        page = pageManager.getPage("/folder2/test004.psml");
        assertTrue(page.getTitle().equals("Updated Deep Title"));
    }

    public void doTestUpdateLink() throws Exception
    {
        pageManager.reset();
        Link link = pageManager.getLink("/test002.link");
        link.setTitle("Updated Title");

        try
        {
            pageManager.updateLink(link);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in link update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        link = pageManager.getLink("/test002.link");
        assertTrue(link.getTitle().equals("Updated Title"));
    }

    public void doTestGetFolders() throws Exception
    {

        pageManager.reset();
        Folder subsites = pageManager.getFolder("/subsites");
        assertNotNull(subsites);

        int count = 0;
        for (Node folderNode : subsites.getFolders())
        {
            Folder folder = (Folder)folderNode;
            System.out.println("folder = " + folder.getName());
            count++;
        }
        assertEquals(4, count);
    }
    
    public void doTestFolders() throws Exception
    {

        pageManager.reset();
        Folder folder1 = pageManager.getFolder("/folder1");
        assertNotNull(folder1);
        assertTrue(folder1.getSkin().equals("test-skin"));
        assertTrue(folder1.getEffectiveDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(folder1.getDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(folder1.getDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));
                
        assertEquals(2, folder1.getFolders().size());
        assertEquals(2, pageManager.getFolders(folder1).size());
        Iterator<Node> childItr = folder1.getFolders().iterator();
        // Test that the folders are naturally orderd
        Folder folder2 = (Folder) childItr.next();
        assertEquals("/folder1/folder2", folder2.getPath());
        assertEquals("folder2", folder2.getName());
        Folder folder3 = (Folder) childItr.next();
        assertEquals("/folder1/folder3", folder3.getPath());
        assertEquals("test001.psml", folder3.getDefaultPage());
        assertEquals(1, folder2.getPages().size());
        assertEquals(2, folder3.getPages().size());
        assertEquals(2, pageManager.getPages(folder3).size());

        // test folder decoration inheritance
        Page page = (Page)folder3.getPages().get("test001.psml");
        assertTrue(page.getEffectiveDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(page.getEffectiveDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));
        
        // Check link order
        assertEquals(6, folder3.getAll().size());
        assertEquals(6, pageManager.getAll(folder3).size());
        Iterator<Node> linkItr = folder3.getAll().iterator();
        assertEquals("Jetspeed2Wiki.link", ((Link)linkItr.next()).getName());
        assertEquals("Jetspeed2.link", ((Link)linkItr.next()).getName());        
        assertEquals("apache_portals.link", ((Link)linkItr.next()).getName());
        assertEquals("apache.link", ((Link)linkItr.next()).getName());
        assertEquals("test001.psml", ((Page)linkItr.next()).getName());
        assertEquals("default-page.psml", ((Page)linkItr.next()).getName());

        //Test FolderSet with both absolute and relative names
        assertNotNull(folder1.getFolders().get("/folder1/folder2"));
        assertNotNull(folder1.getFolders().get("folder2"));
        assertEquals(folder1.getFolders().get("/folder1/folder2"), folder1.getFolders().get("folder2"));

        //Test PageSet with both absolute and relative names
        assertNotNull(folder3.getPages().get("/folder1/folder3/test001.psml"));
        assertNotNull(folder3.getPages().get("test001.psml"));
        assertEquals("test001.psml", folder3.getPages().get("/folder1/folder3/test001.psml").getName());
        
        assertTrue(folder3.isHidden());
        assertTrue(folder3.getPage("default-page.psml").isHidden());
        assertTrue(folder3.getLinks().get("Jetspeed2.link").isHidden());
        assertFalse(folder3.getLinks().get("apache.link").isHidden());
        
        assertNotNull(folder3.getAll().get("Jetspeed2.link"));
        assertNull(folder3.getAll().exclusiveSubset("Jetspeed2\\.link").get("Jetspeed2.link"));
        assertNull(folder3.getAll().inclusiveSubset("apache\\.link").get("Jetspeed2.link"));
        assertNotNull(folder3.getAll().inclusiveSubset("apache\\.link").get("apache.link"));
    }

    public void doTestFolderMetaData() throws Exception
    {
        pageManager.reset();
        Folder folder1French = pageManager.getFolder("/folder1");

        assertEquals("Titre francais pour la chemise 1", folder1French.getTitle(Locale.FRENCH));
        assertEquals("Titre francais pour la chemise 1", folder1French.getTitle(Locale.FRANCE));

        Folder folder1English = pageManager.getFolder("/folder1");

        assertEquals("English Title for Folder 1", folder1English.getTitle(Locale.ENGLISH));

        // check that default works
        Folder folder1German = pageManager.getFolder("/folder1");
       
        assertEquals("Default Title for Folder 1", folder1German.getTitle(Locale.GERMAN));
        
        // Test folder with no metadata assigned
        Folder rootFolder = pageManager.getFolder("/");
        assertEquals(rootFolder.getTitle(), rootFolder.getTitle(Locale.FRENCH));
    }

    public void doTestDefaultTitles() throws Exception
    {
        pageManager.reset();
        Page defaultPage = pageManager.getPage("/folder1/folder2/default-page.psml");
        assertNotNull(defaultPage);
        assertEquals("Default Page", defaultPage.getTitle());

        Folder rootFolder = pageManager.getFolder("/");
        assertEquals("Top", rootFolder.getTitle());
    }

    public void doTestPageMetaData() throws Exception
    {
        pageManager.reset();
        Page page = pageManager.getPage("/default-page.psml");
        assertNotNull(page);
        String frenchTitle = page.getTitle(Locale.FRENCH);
        assertNotNull(frenchTitle);
        assertEquals("Ma Premiere Page de PSML", frenchTitle);
        String japaneseTitle = page.getTitle(Locale.JAPANESE);
        assertNotNull(japaneseTitle);
        assertEquals("Jetspeed 2 \u3078\u3088\u3046\u3053\u305d", japaneseTitle);
        String defaultTitle = page.getTitle(Locale.GERMAN);
        assertNotNull(defaultTitle);
        assertEquals("My First PSML Page", defaultTitle);
    }

    public void doTestLinks() throws Exception
    {
        pageManager.reset();
        Link link = pageManager.getLink("/apache_portals.link");
        assertNotNull(link);
        assertEquals("http://portals.apache.org", link.getUrl());
        assertEquals("Apache Portals Website", link.getTitle());
        assertEquals("Apache Software Foundation [french]", link.getTitle(Locale.FRENCH));
        assertEquals("test-skin", link.getSkin());

        Folder folder = pageManager.getFolder("/");
        assertNotNull(folder);
        assertNotNull(folder.getLinks());
        assertEquals(2,folder.getLinks().size());
        assertEquals(2,pageManager.getLinks(folder).size());
        assertEquals("http://portals.apache.org", ((Document) folder.getLinks().get("/apache_portals.link")).getUrl());
    }

    public void doTestMenuDefinitions() throws Exception
    {
        // test folder resident menu definitions
        pageManager.reset();
        Folder folder = pageManager.getFolder("/");
        assertNotNull(folder);
        List<MenuDefinition> menus = folder.getMenuDefinitions();
        assertNotNull(menus);
        assertEquals(5, menus.size());

        MenuDefinition simpleMenu = menus.get(0);
        assertNotNull(simpleMenu);
        assertEquals("simple", simpleMenu.getName());
        assertNotNull(simpleMenu.getMenuElements());
        assertEquals(1, simpleMenu.getMenuElements().size());
        assertTrue(simpleMenu.getMenuElements().get(0) instanceof MenuOptionsDefinition);
        assertEquals("/test001.psml,/folder1/folder2", ((MenuOptionsDefinition)simpleMenu.getMenuElements().get(0)).getOptions());

        MenuDefinition top2LevelsMenu = menus.get(1);
        assertNotNull(top2LevelsMenu);
        assertEquals("top-2-levels", top2LevelsMenu.getName());
        assertNull(top2LevelsMenu.getMenuElements());
        assertEquals("/", top2LevelsMenu.getOptions());
        assertEquals(2, top2LevelsMenu.getDepth());
        assertEquals("dhtml-pull-down", top2LevelsMenu.getSkin());

        MenuDefinition topRolePagesMenu = menus.get(2);
        assertNotNull(topRolePagesMenu);
        assertEquals("top-role-pages", topRolePagesMenu.getName());
        assertTrue(topRolePagesMenu.isRegexp());
        assertEquals("roles", topRolePagesMenu.getProfile());
        assertEquals("*.psml,*.link", topRolePagesMenu.getOrder());

        MenuDefinition breadCrumbsMenu = menus.get(3);
        assertNotNull(breadCrumbsMenu);
        assertEquals("bread-crumbs", breadCrumbsMenu.getName());
        assertTrue(breadCrumbsMenu.isPaths());

        MenuDefinition topCustomMenu = menus.get(4);
        assertNotNull(topCustomMenu);
        assertEquals("top-custom", topCustomMenu.getName());
        assertEquals("Top Menu", topCustomMenu.getTitle());
        assertEquals("Top", topCustomMenu.getShortTitle());
        assertEquals("Haut", topCustomMenu.getTitle(Locale.FRENCH));
        assertEquals("H", topCustomMenu.getShortTitle(Locale.FRENCH));
        assertNotNull(topCustomMenu.getMenuElements());
        assertEquals(5, topCustomMenu.getMenuElements().size());
        assertTrue(topCustomMenu.getMenuElements().get(0) instanceof MenuOptionsDefinition);
        assertTrue(((MenuOptionsDefinition)topCustomMenu.getMenuElements().get(0)).isRegexp());
        assertEquals("groups", ((MenuOptionsDefinition)topCustomMenu.getMenuElements().get(0)).getProfile());
        assertTrue(topCustomMenu.getMenuElements().get(1) instanceof MenuDefinition);
        assertTrue(topCustomMenu.getMenuElements().get(2) instanceof MenuExcludeDefinition);
        assertEquals("top-role-pages", ((MenuExcludeDefinition)topCustomMenu.getMenuElements().get(2)).getName());
        assertTrue(topCustomMenu.getMenuElements().get(3) instanceof MenuSeparatorDefinition);
        assertEquals("More Top Pages", ((MenuSeparatorDefinition)topCustomMenu.getMenuElements().get(3)).getText());
        assertTrue(topCustomMenu.getMenuElements().get(4) instanceof MenuIncludeDefinition);
        assertEquals("simple", ((MenuIncludeDefinition)topCustomMenu.getMenuElements().get(4)).getName());
        assertTrue(((MenuIncludeDefinition)topCustomMenu.getMenuElements().get(4)).isNest());

        MenuDefinition topCustomNestedMenu = (MenuDefinition)topCustomMenu.getMenuElements().get(1);
        assertEquals("/", topCustomNestedMenu.getOptions());
        assertEquals("page", topCustomNestedMenu.getProfile());
        assertEquals(5, topCustomNestedMenu.getMenuElements().size());
        assertTrue(topCustomNestedMenu.getMenuElements().get(0) instanceof MenuSeparatorDefinition);
        assertEquals("Top Pages", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(0)).getText());
        assertEquals("Ye Olde Top Pages", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(0)).getText(Locale.ENGLISH));
        assertEquals("Select from Top Pages menu...", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(0)).getTitle());
        assertEquals("Haut", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(0)).getTitle(Locale.FRENCH));
        assertTrue(topCustomNestedMenu.getMenuElements().get(1) instanceof MenuOptionsDefinition);
        assertTrue(topCustomNestedMenu.getMenuElements().get(2) instanceof MenuSeparatorDefinition);
        assertEquals("bold", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(2)).getSkin());
        assertEquals("Custom Pages", ((MenuSeparatorDefinition)topCustomNestedMenu.getMenuElements().get(2)).getTitle());
        assertTrue(topCustomNestedMenu.getMenuElements().get(3) instanceof MenuOptionsDefinition);
        assertEquals(1, ((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(3)).getDepth());
        assertEquals("*.psml", ((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(3)).getOrder());
        assertTrue(topCustomNestedMenu.getMenuElements().get(4) instanceof MenuOptionsDefinition);
        assertTrue(((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(4)).isPaths());
        assertEquals("*", ((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(4)).getProfile());
        assertEquals("links", ((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(4)).getSkin());
        assertEquals("@", ((MenuOptionsDefinition)topCustomNestedMenu.getMenuElements().get(4)).getOptions());

        // test page resident menu definitions
        Page page = pageManager.getPage("/test001.psml");
        assertNotNull(page);
        menus = page.getMenuDefinitions();
        assertNotNull(menus);
        assertEquals(1, menus.size());

        simpleMenu = menus.get(0);
        assertNotNull(simpleMenu);
        assertEquals("simple", simpleMenu.getName());
        assertNotNull(simpleMenu.getMenuElements());
        assertEquals(2, simpleMenu.getMenuElements().size());

        // test writing page menu definitions
        page = pageManager.getPage("/test002.psml");
        page.setMenuDefinitions(new ArrayList<MenuDefinition>());
        MenuDefinition newMenu = page.newMenuDefinition();
        newMenu.setName("updated-menu");
        newMenu.setSkin("tabs");
        newMenu.setMenuElements(new ArrayList<MenuDefinitionElement>());
        MenuSeparatorDefinition newSeparator = page.newMenuSeparatorDefinition();
        newSeparator.setText("-- Updated Menu --");
        newMenu.getMenuElements().add(newSeparator);
        MenuOptionsDefinition newOptions0 = page.newMenuOptionsDefinition();
        newOptions0.setOptions("/*.psml");
        newOptions0.setRegexp(true);
        newMenu.getMenuElements().add(newOptions0);
        MenuOptionsDefinition newOptions1 = page.newMenuOptionsDefinition();
        newOptions1.setOptions("/folder0");
        newMenu.getMenuElements().add(newOptions1);
        MenuDefinition newNestedMenu = page.newMenuDefinition();
        newNestedMenu.setOptions("/*/");
        newNestedMenu.setRegexp(true);
        newMenu.getMenuElements().add(newNestedMenu);
        MenuExcludeDefinition newExcludeMenu = page.newMenuExcludeDefinition();
        newExcludeMenu.setName("exclude-menu");
        newMenu.getMenuElements().add(newExcludeMenu);
        MenuIncludeDefinition newIncludeMenu = page.newMenuIncludeDefinition();
        newIncludeMenu.setName("include-menu");
        newIncludeMenu.setNest(true);
        newMenu.getMenuElements().add(newIncludeMenu);
        page.getMenuDefinitions().add(newMenu);
        try
        {
            pageManager.updatePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
        page = pageManager.getPage("/test002.psml");
        assertNotNull(page.getMenuDefinitions());
        assertEquals(1, page.getMenuDefinitions().size());
        assertNotNull(page.getMenuDefinitions().get(0).getMenuElements());
        assertEquals(6, page.getMenuDefinitions().get(0).getMenuElements().size());
        assertTrue(page.getMenuDefinitions().get(0).getMenuElements().get(0) instanceof MenuSeparatorDefinition);
        assertTrue(page.getMenuDefinitions().get(0).getMenuElements().get(1) instanceof MenuOptionsDefinition);
        assertTrue(page.getMenuDefinitions().get(0).getMenuElements().get(2) instanceof MenuOptionsDefinition);
        assertTrue(page.getMenuDefinitions().get(0).getMenuElements().get(3) instanceof MenuDefinition);
        assertTrue(page.getMenuDefinitions().get(0).getMenuElements().get(4) instanceof MenuExcludeDefinition);
        assertTrue(page.getMenuDefinitions().get(0).getMenuElements().get(5) instanceof MenuIncludeDefinition);

        // test writing folder menu definitions
        folder = pageManager.getFolder("/folder2");
        folder.setMenuDefinitions(new ArrayList<MenuDefinition>());
        newMenu = folder.newMenuDefinition();
        newMenu.setName("updated-menu");
        newMenu.setSkin("bread-crumbs");
        newMenu.setOptions("./");
        newMenu.setPaths(true);
        folder.getMenuDefinitions().add(newMenu);
        try
        {
            pageManager.updateFolder(folder);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in folder update: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
        folder = pageManager.getFolder("/folder2");
        assertNotNull(folder.getMenuDefinitions());
        assertEquals(1, folder.getMenuDefinitions().size());
        assertEquals("updated-menu", folder.getMenuDefinitions().get(0).getName());
        assertEquals("bread-crumbs", folder.getMenuDefinitions().get(0).getSkin());
        assertEquals("./", folder.getMenuDefinitions().get(0).getOptions());
    }

    public void doTestRemovePage() throws Exception
    {
/*
        Page page = pageManager.getPage("/test002.psml");
        try
        {
            pageManager.removePage(page);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page remove: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        boolean exceptionFound = false;
        try
        {
            page = pageManager.getPage("/test002.psml");
        }
        catch (DocumentNotFoundException dnfe)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
*/boolean exceptionFound = false;

        pageManager.reset();
        PageTemplate pagetemplate = pageManager.getPageTemplate("/test002.tpsml");
        try
        {
            pageManager.removePageTemplate(pagetemplate);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in page template remove: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        exceptionFound = false;
        try
        {
            pagetemplate = pageManager.getPageTemplate("/test002.tpsml");
        }
        catch (DocumentNotFoundException dnfe)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);

        DynamicPage dynamicpage = pageManager.getDynamicPage("/test002.dpsml");
        try
        {
            pageManager.removeDynamicPage(dynamicpage);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in dynamic page remove: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        exceptionFound = false;
        try
        {
            dynamicpage = pageManager.getDynamicPage("/test002.dpsml");
        }
        catch (DocumentNotFoundException dnfe)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
        
        FragmentDefinition fragmentdefinition = pageManager.getFragmentDefinition("/test002.fpsml");
        try
        {
            pageManager.removeFragmentDefinition(fragmentdefinition);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in fragment definition remove: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        exceptionFound = false;
        try
        {
            fragmentdefinition = pageManager.getFragmentDefinition("/test002.fpsml");
        }
        catch (DocumentNotFoundException dnfe)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
    }

    public void doTestRemoveFolder() throws Exception
    {
        pageManager.reset();
        Folder folder = pageManager.getFolder("/folder2");

        try
        {
            pageManager.removeFolder(folder);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in folder remove: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        boolean exceptionFound = false;
        try
        {
            folder = pageManager.getFolder("/folder2");
        }
        catch (FolderNotFoundException fnfe)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
    }

    public void doTestRemoveLink() throws Exception
    {
        pageManager.reset();
        Link link = pageManager.getLink("/test002.link");

        try
        {
            pageManager.removeLink(link);
        }
        catch (Exception e)
        {
            String errmsg = "Exception in link remove: " + e.toString();
            e.printStackTrace();
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }

        boolean exceptionFound = false;
        try
        {
            link = pageManager.getLink("/test002.link");
        }
        catch (DocumentNotFoundException dnfe)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
    }
    
    public void doTestClonePage() throws Exception
    {
        pageManager.reset();
        Page testpage = pageManager.getPage("/clonetest.psml");
        assertNotNull(testpage);
        Page clone = pageManager.copyPage(testpage, "/cloned.psml");
        assertNotNull(clone);
        
        assertTrue(clone.getId().equals("/cloned.psml"));
        assertTrue(clone.getName().equals("cloned.psml"));
        assertTrue(clone.getTitle().equals("Test Page"));
        assertTrue(clone.getSkin().equals("test-skin"));
        assertTrue(clone.getEffectiveDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(clone.getDefaultDecorator(Fragment.LAYOUT).equals("test-layout"));
        assertTrue(clone.getDefaultDecorator(Fragment.PORTLET).equals("test-portlet"));

        // TODO: Test Meta data
        BaseFragmentElement rootFragmentElement = testpage.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        Fragment root = (Fragment)rootFragmentElement;
        BaseFragmentElement cloneRootFragmentElement = clone.getRootFragment();
        assertTrue(cloneRootFragmentElement instanceof Fragment);
        Fragment cloneRoot = (Fragment)cloneRootFragmentElement;
        
        assertNotNull(cloneRoot);
        assertNotNull(cloneRoot.getId());        
        assertFalse(cloneRoot.getId().equals(root.getId()));
        assertTrue(cloneRoot.getName().equals("TwoColumns"));
        assertTrue(cloneRoot.getType().equals(Fragment.LAYOUT));
        assertNull(cloneRoot.getDecorator());

        List<BaseFragmentElement> children = root.getFragments();
        List<BaseFragmentElement> cloneChildren = cloneRoot.getFragments();
        assertNotNull(cloneChildren);
        assertTrue(cloneChildren.size() == 3);

        Fragment f = (Fragment) children.get(0);
        Fragment cf = (Fragment) cloneChildren.get(0);
        assertNotNull(cf.getId());
        assertFalse(cf.getId().equals(f.getId()));
        assertTrue(cf.getName().equals("HelloPortlet"));
        assertTrue(cf.getType().equals(Fragment.PORTLET));

        List<FragmentProperty> properties = f.getProperties();
        List<FragmentProperty> cloneProperties = cf.getProperties();

        assertNotNull(cloneProperties);
        assertEquals(3, cloneProperties.size());
        assertEquals("0", cf.getProperty(Fragment.ROW_PROPERTY_NAME));
        assertEquals(0, cf.getIntProperty(Fragment.COLUMN_PROPERTY_NAME));
        assertEquals("custom-value", cf.getProperty("custom"));
        assertNull(cf.getProperty("custom", Fragment.USER_PROPERTY_SCOPE, "user"));

        cf = (Fragment) cloneChildren.get(1);
        f = (Fragment) children.get(1);
        assertNotNull(cf.getId());
        assertFalse(cf.getId().equals(f.getId()));
        assertTrue(cf.getName().equals("JMXPortlet"));
        assertTrue(cf.getType().equals(Fragment.PORTLET));

        properties = cf.getProperties();
        assertNotNull(properties);
        assertEquals(2, properties.size());
        assertEquals("0", cf.getProperty(Fragment.ROW_PROPERTY_NAME));
        assertEquals(1, cf.getIntProperty(Fragment.COLUMN_PROPERTY_NAME));

        BaseFragmentElement bf = cloneChildren.get(2);
        assertTrue(bf instanceof Fragment);
        cf = (Fragment)bf;
        String id = cf.getId();
        bf = clone.getFragmentById(id);
        assertTrue(bf instanceof Fragment);
        cf = (Fragment)bf;
        
        assertNotNull(cf);        
        assertNotNull(cf.getId());        
        assertFalse(cf.getId().equals(f.getId()));
        assertTrue(cf.getName().equals("Card"));
        assertTrue(cf.getType().equals(Fragment.LAYOUT));
        assertTrue(cf.getDecorator().equals("Tab"));
        assertNotNull(cf.getFragments());
        assertTrue(cf.getFragments().size() == 2);
        
        // security testing
        SecurityConstraints constraints = clone.getSecurityConstraints();
        assertNotNull(constraints); 
        assertTrue(constraints.getOwner().equals("new-user"));
        List<SecurityConstraint> secs = constraints.getSecurityConstraints();
        assertNotNull(secs);
        assertTrue(secs.size() == 1);
        SecurityConstraint constraint = secs.get(0);
        assertNotNull(constraint);
        assertTrue(constraint.getUsers() != null);
        assertTrue(constraint.getUsers().size() == 2);
        assertTrue(Shared.makeCSVFromList(constraint.getUsers()).equals("user10,user11"));
        assertTrue(constraint.getRoles() != null);
        assertTrue(constraint.getRoles().size() == 1);
        assertTrue(Shared.makeCSVFromList(constraint.getRoles()).equals("*"));
        assertTrue(constraint.getPermissions() != null);
        assertTrue(constraint.getPermissions().size() == 2);
        assertTrue(Shared.makeCSVFromList(constraint.getPermissions()).equals("edit,view"));
        List<String> refs = constraints.getSecurityConstraintsRefs();
        assertNotNull(refs);
        assertTrue(refs.size() == 1);
        String ref = refs.get(0);
        assertNotNull(ref);
        assertTrue(ref.equals("public-view"));
        
        // TODO: menu testing

        PageTemplate testpagetemplate = pageManager.getPageTemplate("/clonetest.tpsml");
        assertNotNull(testpagetemplate);
        PageTemplate clonepagetemplate = pageManager.copyPageTemplate(testpagetemplate, "/cloned.tpsml");
        assertNotNull(clonepagetemplate);
        assertTrue(clonepagetemplate.getId().equals("/cloned.tpsml"));
        assertTrue(clonepagetemplate.getTitle().equals("Clone Test Page Template"));
        assertTrue(clonepagetemplate.getSkin().equals("test-template-skin"));
        assertTrue(clonepagetemplate.getDefaultDecorator(Fragment.LAYOUT).equals("test-template-layout"));
        assertTrue(clonepagetemplate.getDefaultDecorator(Fragment.PORTLET).equals("test-template-portlet"));
        GenericMetadata md = clonepagetemplate.getMetadata();
        Collection<LocalizedField> descriptions = md.getFields("description");
        assertNotNull(descriptions);
        assertEquals(1, descriptions.size());
        rootFragmentElement = clonepagetemplate.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertNotNull(root);
        assertFalse(root.getId().equals("cpt-f001"));
        assertTrue(root.getName().equals("TwoColumns"));
        assertTrue(root.getType().equals(Fragment.LAYOUT));
        assertNotNull(root.getFragments());
        assertTrue(root.getFragments().size() == 3);
        f = (Fragment)root.getFragments().get(0);
        assertFalse(f.getId().equals("cpt-f002"));
        assertTrue(f.getName().equals("TemplatePortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));
        bf = root.getFragments().get(1);
        assertTrue(bf instanceof PageFragment);
        PageFragment pf = (PageFragment)bf;
        assertFalse(pf.getId().equals("cpt-f003"));
        bf = root.getFragments().get(2);
        assertTrue(bf instanceof FragmentReference);
        FragmentReference fr = (FragmentReference)bf;
        assertFalse(fr.getId().equals("cpt-f004"));
        assertTrue(fr.getRefId().equals("ctest001"));

        DynamicPage testdynamicpage = pageManager.getDynamicPage("/clonetest.dpsml");
        assertNotNull(testdynamicpage);
        DynamicPage clonedynamicpage = pageManager.copyDynamicPage(testdynamicpage, "/cloned.dpsml");
        assertNotNull(clonedynamicpage);
        assertTrue(clonedynamicpage.getId().equals("/cloned.dpsml"));
        assertTrue(clonedynamicpage.getContentType().equals("default"));        
        assertTrue(clonedynamicpage.isInheritable());        
        assertTrue(clonedynamicpage.getTitle().equals("Clone Test Dynamic Page"));
        rootFragmentElement = clonedynamicpage.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertNotNull(root);
        assertFalse(root.getId().equals("cdp-f001"));
        assertTrue(root.getName().equals("TwoColumns"));
        assertTrue(root.getType().equals(Fragment.LAYOUT));
        assertNotNull(root.getFragments());
        assertTrue(root.getFragments().size() == 2);
        f = (Fragment)root.getFragments().get(0);
        assertFalse(f.getId().equals("cdp-f002"));
        assertTrue(f.getName().equals("HelloPortlet"));
        assertTrue(f.getType().equals(Fragment.PORTLET));
        bf = root.getFragments().get(1);
        assertTrue(bf instanceof FragmentReference);
        fr = (FragmentReference)bf;
        assertFalse(fr.getId().equals("cdp-f003"));
        assertTrue(fr.getRefId().equals("ctest001"));    

        FragmentDefinition testfragmentdefinition = pageManager.getFragmentDefinition("/clonetest.fpsml");
        assertNotNull(testfragmentdefinition);
        FragmentDefinition clonefragmentdefinition = pageManager.copyFragmentDefinition(testfragmentdefinition, "/cloned.fpsml");
        assertNotNull(clonefragmentdefinition);
        assertTrue(clonefragmentdefinition.getId().equals("/cloned.fpsml"));
        assertTrue(clonefragmentdefinition.getTitle().equals("Clone Test Fragment Definition"));
        rootFragmentElement = clonefragmentdefinition.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertNotNull(root);
        assertFalse(root.getId().equals("ctest001"));
        assertTrue(root.getName().equals("HelloPortlet"));
        assertTrue(root.getType().equals(Fragment.PORTLET));
        assertTrue(root.getFragments().isEmpty());
    }
    
    public Collection<String> collectIds(Folder f) throws Exception {
        Collection<String> result = new ArrayList<String>();
        
        for (Node node : f.getAll())
        {
           if (node instanceof Page){
               Page thisPage = (Page) node;
               if (thisPage.getRootFragment()!=null){
                   result.addAll(collectIds(thisPage.getRootFragment()));    
               }                          
           } else
           if (node instanceof Folder){
               Folder thisFolder = (Folder)node;
               result.addAll(collectIds(thisFolder));
           }            
        }   
        
        return result;
    }
    
    public Collection<String> collectIds(BaseFragmentElement bf){
    	Collection<String> result = new ArrayList<String>();
        
    	result.add(bf.getId());
    	if (bf instanceof Fragment) {
    	    Fragment f = (Fragment)bf;
    	    if (f.getFragments().size() > 0){
                for (BaseFragmentElement child : f.getFragments()) {
    	            result.addAll(collectIds(child));
    	        }
    	    }
    	}
    	return result;
    }
    
    private int countFragments(BaseFragmentElement bf){
        int result = 1;
        if (bf instanceof Fragment) {
            Fragment f = (Fragment)bf;
            for (BaseFragmentElement child : f.getFragments()) {
                result+=countFragments(child);
            }
        }
        
        return result;
    }
    
    private void compareFolders(Folder folder1, Folder folder2) throws Exception {
        for (Node node : folder1.getAll())
        {
           if (node instanceof Page){
               Page thisPage = (Page) node;
               Page otherPage = folder2.getPage(thisPage.getName());
               assertEquals(thisPage.getRootFragment()!=null,otherPage.getRootFragment() != null);
               if (thisPage.getRootFragment() != null){
                   BaseFragmentElement thisRootFragment = thisPage.getRootFragment();
                   BaseFragmentElement otherRootFragment = otherPage.getRootFragment();
                   assertEquals(thisRootFragment.getClass(), otherRootFragment.getClass());
                   if (thisRootFragment instanceof Fragment) {
                       assertEquals(((Fragment)thisRootFragment).getFragments().size(),((Fragment)otherRootFragment).getFragments().size());
                   }
                   assertEquals(countFragments(thisRootFragment),countFragments(otherRootFragment));
               }               
           } else
           if (node instanceof Folder){
               Folder thisFolder = (Folder)node;
               compareFolders(thisFolder, folder2.getFolder(thisFolder.getName())); 
           }
            
        }        
    }
    
    public void doTestIdGeneration() throws Exception{
        pageManager.reset();
        Folder webappIds = pageManager.getFolder("/webapp-ids");
        Folder webappNoIds = pageManager.getFolder("/webapp-no-ids");
        
        compareFolders(webappIds,webappNoIds);

        Collection<String> allIds = collectIds(webappNoIds);
        for (String id : allIds) {
			assertNotNull(id);
			assertEquals(true,id.length() > 0);
            if (CollectionUtils.cardinality(id, allIds) > 1){
                 System.out.println("Fragment with id "+id+" has duplicates");
            }
			assertEquals(1, CollectionUtils.cardinality(id, allIds)); // uniqueness test			
		}        
    }

    private Subject constructUserSubject()
    {
        // setup test subject
        Set<Principal> principals = new PrincipalsSet();
        principals.add(new TestUser("user"));
        principals.add(new TestGroup("group"));
        principals.add(new TestRole("role"));
        return new Subject(true, principals, new HashSet<Principal>(), new HashSet<Principal>());
    }
}
