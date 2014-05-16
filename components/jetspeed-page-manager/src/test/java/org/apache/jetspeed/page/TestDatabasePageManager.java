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
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
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
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.FragmentProperty;
import org.apache.jetspeed.om.page.FragmentReference;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageFragment;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.PrincipalsSet;

import javax.security.auth.Subject;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * TestPageXmlPersistence
 * 
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 *          
 */
public class TestDatabasePageManager extends DatasourceEnabledSpringTestCase implements PageManagerTestShared
{
    private static final String deepFolderPath = "/__subsite-rootx/_user/userx/_role/rolex/_group/groupx/_mediatype/xhtml/_language/en/_country/us/_custom/customx";
    private static final String deepPagePath = deepFolderPath + "/default-page.psml";       

    private static class PageManagerEventListenerImpl implements PageManagerEventListener
    {
        int newNodeCount;
        int updatedNodeCount;
        int removedNodeCount;

        /* (non-Javadoc)
         * @see org.apache.jetspeed.page.PageManagerEventListener#newNode(org.apache.jetspeed.page.document.Node)
         */
        public void newNode(Node node)
        {
            newNodeCount++;
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.page.PageManagerEventListener#newNode(org.apache.jetspeed.page.document.Node)
         */
        public void updatedNode(Node node)
        {
            updatedNodeCount++;
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.page.PageManagerEventListener#newNode(org.apache.jetspeed.page.document.Node)
         */
        public void removedNode(Node node)
        {
            removedNodeCount++;
        }

        /* (non-Javadoc)
         * @see org.apache.jetspeed.page.PageManagerEventListener#reapNodes(long)
         */
        public void reapNodes(long interval)
        {
        }
    }

    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestDatabasePageManager.class.getName() });
    }
    
    
    public static Test suite()
    {
        return createFixturedTestSuite(TestDatabasePageManager.class, "firstTestSetup", null);
    }
    
    protected String[] getConfigurations()
    {
        return new String[]{"database-page-manager.xml", "transaction.xml"};
    }

    public void firstTestSetup() throws Exception
    {
        System.out.println("Running firstTestSetup");
        try
        {
            PageManager pageManager = scm.lookupComponent("pageManager");

            Folder removeRootFolder = pageManager.getFolder("/");
            pageManager.removeFolder(removeRootFolder);
        }
        catch (FolderNotFoundException e)
        {
        }
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
        doTestCreates();
        doTestGets();
        doTestUpdates();
        doTestRemoves();
    }

    public void doTestCreates() throws Exception
    {
        final PageManager pageManager = scm.lookupComponent("pageManager");
        pageManager.reset();
        PageManagerEventListenerImpl pmel = new PageManagerEventListenerImpl();
        pageManager.addListener(pmel);

        // test document and folder creation
        Folder folder = pageManager.newFolder("/");
        assertEquals("Top", folder.getTitle());
        folder.setTitle("Root Folder");
        folder.setDefaultDecorator("jetspeed", Fragment.LAYOUT);
        folder.setDefaultDecorator("gray-gradient", Fragment.PORTLET);
        folder.setSkin("skin-1");
        folder.setDefaultPage("default-page.psml");
        folder.setShortTitle("Root");
        GenericMetadata metadata = folder.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "[fr] Root Folder");
        SecurityConstraints folderConstraints = pageManager.newSecurityConstraints();
        folderConstraints.setOwner("admin");
        List<SecurityConstraint> inlineFolderConstraints = new ArrayList<SecurityConstraint>(2);
        SecurityConstraint folderConstraint = pageManager.newFolderSecurityConstraint();
        folderConstraint.setUsers(Shared.makeListFromCSV("user,admin"));
        folderConstraint.setRoles(Shared.makeListFromCSV("manager"));
        folderConstraint.setGroups(Shared.makeListFromCSV("*"));
        folderConstraint.setPermissions(Shared.makeListFromCSV("view,edit"));
        inlineFolderConstraints.add(folderConstraint);
        folderConstraint = folder.newSecurityConstraint();
        folderConstraint.setPermissions(Shared.makeListFromCSV("edit"));
        inlineFolderConstraints.add(folderConstraint);
        folderConstraints.setSecurityConstraints(inlineFolderConstraints);
        List<String> folderConstraintsRefs = new ArrayList<String>(2);
        folderConstraintsRefs.add("public-view");
        folderConstraintsRefs.add("public-edit");
        folderConstraints.setSecurityConstraintsRefs(folderConstraintsRefs);
        folder.setSecurityConstraints(folderConstraints);
        List<String> documentOrder = new ArrayList<String>(2);
        documentOrder.add("some-other-page.psml");
        documentOrder.add("default-page.psml");
        folder.setDocumentOrder(documentOrder);
        MenuDefinition newMenu = folder.newMenuDefinition();
        newMenu.setName("folder-menu");
        newMenu.setTitle("The Test Folder Menu");
        newMenu.setShortTitle("Folder Menu");
        newMenu.setProfile("group-fallback");
        metadata = newMenu.getMetadata();
        metadata.addField(Locale.FRENCH, "short-title", "[fr] Folder Menu");
        metadata.addField(Locale.FRENCH, "title", "[fr] The Test Folder Menu");
        MenuSeparatorDefinition newSeparator = folder.newMenuSeparatorDefinition();
        newSeparator.setText("-- Folder Menu --");
        newSeparator.setTitle("Rollover: Folder Menu");
        newSeparator.setSkin("header");
        metadata = newSeparator.getMetadata();
        metadata.addField(Locale.FRENCH, "text", "-- [fr] Folder Menu --");
        metadata.addField(Locale.FRENCH, "title", "[fr] Rollover: Folder Menu");
        newMenu.getMenuElements().add(newSeparator);
        MenuOptionsDefinition newOptions0 = folder.newMenuOptionsDefinition();
        newOptions0.setOptions("/*.psml");
        newOptions0.setRegexp(true);
        newOptions0.setSkin("flash");
        newMenu.getMenuElements().add(newOptions0);
        MenuOptionsDefinition newOptions1 = folder.newMenuOptionsDefinition();
        newOptions1.setOptions("/folder0");
        newOptions1.setProfile("role-fallback");
        newOptions1.setOrder("/folder*");
        newOptions1.setDepth(1);
        newOptions1.setPaths(true);
        newMenu.getMenuElements().add(newOptions1);
        MenuDefinition newNestedMenu = folder.newMenuDefinition();
        newNestedMenu.setOptions("/*/");
        newNestedMenu.setRegexp(true);
        newNestedMenu.setDepth(2);
        newNestedMenu.setOrder("/x*/,/y*/,/z*/");
        newNestedMenu.setSkin("bold");
        newMenu.getMenuElements().add(newNestedMenu);
        MenuExcludeDefinition newExcludeMenu = folder.newMenuExcludeDefinition();
        newExcludeMenu.setName("exclude-menu");
        newMenu.getMenuElements().add(newExcludeMenu);
        MenuIncludeDefinition newIncludeMenu = folder.newMenuIncludeDefinition();
        newIncludeMenu.setName("include-menu");
        newIncludeMenu.setNest(true);
        newMenu.getMenuElements().add(newIncludeMenu);
        folder.getMenuDefinitions().add(newMenu);
        newMenu = folder.newMenuDefinition();
        newMenu.setName("folder-breadcrumb-menu");
        newMenu.setSkin("bread-crumbs");
        newMenu.setOptions("./");
        newMenu.setPaths(true);
        folder.getMenuDefinitions().add(newMenu);
        pageManager.updateFolder(folder);
        
        assertNull(folder.getParent());

        Page page = pageManager.newPage("/default-page.psml");
        assertEquals("Default Page", page.getTitle());
        page.setTitle("Default Page");
        page.setVersion("6.89");
        page.setDefaultDecorator("tigris", Fragment.LAYOUT);
        page.setDefaultDecorator("blue-gradient", Fragment.PORTLET);
        page.setSkin("skin-1");
        page.setShortTitle("Default");
        metadata = page.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "[fr] Default Page");
        metadata.addField(Locale.JAPANESE, "title", "[ja] Default Page");
        SecurityConstraints pageConstraints = page.newSecurityConstraints();
        pageConstraints.setOwner("user");
        List<SecurityConstraint> inlinePageConstraints = new ArrayList<SecurityConstraint>(1);
        SecurityConstraint pageConstraint = page.newSecurityConstraint();
        pageConstraint.setUsers(Shared.makeListFromCSV("jetspeed"));
        pageConstraint.setPermissions(Shared.makeListFromCSV("edit"));
        inlinePageConstraints.add(pageConstraint);
        pageConstraints.setSecurityConstraints(inlinePageConstraints);
        List<String> pageConstraintsRefs = new ArrayList<String>(1);
        pageConstraintsRefs.add("manager-edit");
        pageConstraints.setSecurityConstraintsRefs(pageConstraintsRefs);
        page.setSecurityConstraints(pageConstraints);
        List<MenuDefinition> pageMenus = new ArrayList<MenuDefinition>();
        newMenu = page.newMenuDefinition();
        newMenu.setName("page-menu-1");
        newMenu.setTitle("The Test Page Menu");
        metadata = newMenu.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "[fr] The Test Page Menu");
        newSeparator = page.newMenuSeparatorDefinition();
        newSeparator.setText("-- Page Menu --");
        List<MenuDefinitionElement> menuElements = new ArrayList<MenuDefinitionElement>();
        menuElements.add(newSeparator);
        newOptions0 = page.newMenuOptionsDefinition();
        newOptions0.setOptions("/*.psml");
        menuElements.add(newOptions0);
        newNestedMenu = page.newMenuDefinition();
        newNestedMenu.setOptions("/*/");
        menuElements.add(newNestedMenu);
        newExcludeMenu = page.newMenuExcludeDefinition();
        newExcludeMenu.setName("exclude-menu");
        menuElements.add(newExcludeMenu);
        newIncludeMenu = page.newMenuIncludeDefinition();
        newIncludeMenu.setName("include-menu");
        menuElements.add(newIncludeMenu);
        newMenu.setMenuElements(menuElements);
        pageMenus.add(newMenu);
        newMenu = page.newMenuDefinition();
        newMenu.setName("page-menu-2");
        newMenu.setOptions("./");
        pageMenus.add(newMenu);
        page.setMenuDefinitions(pageMenus);

        BaseFragmentElement rootFragmentElement = page.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        Fragment root = (Fragment)rootFragmentElement;
        root.setDecorator("blue-gradient");
        root.setName("jetspeed-layouts::VelocityTwoColumns");
        root.setShortTitle("Root");
        root.setTitle("Root Fragment");
        root.setState("Normal");
        root.setLayoutSizes("50%,50%");
        FragmentProperty property1 = pageManager.newFragmentProperty();
        property1.setName("custom-prop1");
        property1.setValue("custom-prop-value1");
        root.getProperties().add(property1);
        FragmentProperty property2 = pageManager.newFragmentProperty();
        property2.setName("custom-prop2");
        property2.setValue("custom-prop-value2");
        root.getProperties().add(property2);
        root.setProperty("custom-0", null, null, "custom-value-0");
        root.setProperty("custom-1", null, null, "custom-value-1");
        root.setProperty("custom-2", null, null, "custom-value-2");
        root.setProperty("custom-3", null, null, "custom-value-3");
        
        Fragment portlet = pageManager.newPortletFragment();
        portlet.setName("security::LoginPortlet");
        portlet.setShortTitle("Portlet");
        portlet.setTitle("Portlet Fragment");
        portlet.setState("Normal");
        portlet.setLayoutRow(88);
        portlet.setLayoutColumn(99);
        portlet.setLayoutX(12.34F);
        portlet.setLayoutY(23.45F);
        portlet.setLayoutZ(34.56F);
        portlet.setLayoutWidth(45.67F);
        portlet.setLayoutHeight(56.78F);
        List<FragmentPreference> preferences = new ArrayList<FragmentPreference>(2);
        FragmentPreference preference = pageManager.newFragmentPreference();
        preference.setName("pref0");
        preference.setReadOnly(true);
        List<String> preferenceValues = new ArrayList<String>(2);
        preferenceValues.add("pref0-value0");
        preferenceValues.add("pref0-value1");
        preference.setValueList(preferenceValues);
        preferences.add(preference);
        preference = pageManager.newFragmentPreference();
        preference.setName("pref1");
        preferenceValues = new ArrayList<String>(1);
        preferenceValues.add("pref1-value");
        preference.setValueList(preferenceValues);
        preferences.add(preference);
        portlet.setPreferences(preferences);
        root.getFragments().add(portlet);
        portlet = pageManager.newPortletFragment();
        portlet.setName("some-app::SomePortlet");
        portlet.setShortTitle("Some Portlet");
        portlet.setTitle("Some Portlet Fragment");
        portlet.setState("Normal");
        portlet.setLayoutRow(22);
        portlet.setLayoutColumn(11);
        portlet.setLayoutX(11.11F);
        portlet.setLayoutY(22.22F);
        portlet.setLayoutZ(33.33F);
        portlet.setLayoutWidth(44.44F);
        portlet.setLayoutHeight(55.55F);
        SecurityConstraints fragmentConstraints = portlet.newSecurityConstraints();
        fragmentConstraints.setOwner("user");
        portlet.setSecurityConstraints(fragmentConstraints);
        root.getFragments().add(portlet);
        FragmentReference fragmentReference = pageManager.newFragmentReference();
        fragmentReference.setRefId("fragment-definition");
        root.getFragments().add(fragmentReference);

        pageManager.updatePage(page);

        assertNotNull(page.getParent());
        assertEquals(page.getParent().getId(), folder.getId());
        assertNotNull(folder.getPages());
        assertEquals(1, folder.getPages().size());
        assertNotNull(pageManager.getPages(folder));
        assertEquals(1, pageManager.getPages(folder).size());
        
        final Fragment userRootFragment = (Fragment)page.getRootFragment();
        Exception userException = (Exception)JSSubject.doAsPrivileged(constructUserSubject(), new PrivilegedAction()
        {
            public Object run()
            {
                try
                {
                    if (FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
                    {
                        userRootFragment.setProperty("custom-1", FragmentProperty.ROLE_PROPERTY_SCOPE, "role", "custom-value-role-1");
                        userRootFragment.setProperty("custom-2", FragmentProperty.ROLE_PROPERTY_SCOPE, "role", "custom-value-role-2");
                        userRootFragment.setProperty("custom-2", FragmentProperty.GROUP_PROPERTY_SCOPE, "group", "custom-value-group-2");
                        userRootFragment.setProperty("custom-3", FragmentProperty.ROLE_PROPERTY_SCOPE, "role", "custom-value-role-3");
                        userRootFragment.setProperty("custom-3", FragmentProperty.GROUP_PROPERTY_SCOPE, "group", "custom-value-group-3");
                    }
                    userRootFragment.setProperty("custom-3", FragmentProperty.USER_PROPERTY_SCOPE, "user", "custom-value-user-3");

                    if (FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
                    {
                        pageManager.updateFragmentProperties(userRootFragment, PageManager.ROLE_PROPERTY_SCOPE);
                        pageManager.updateFragmentProperties(userRootFragment, PageManager.GROUP_PROPERTY_SCOPE);
                    }
                    pageManager.updateFragmentProperties(userRootFragment, PageManager.USER_PROPERTY_SCOPE);
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

        page = pageManager.newPage("/another-page.psml");
        assertEquals("Another Page", page.getTitle());
        page.setTitle("Another Page");
        pageManager.updatePage(page);
        assertNotNull(page.getParent());
        assertEquals(page.getParent().getId(), folder.getId());
        page = pageManager.newPage("/some-other-page.psml");
        assertEquals("Some Other Page", page.getTitle());
        page.setTitle("Some Other Page");
        pageManager.updatePage(page);
        assertNotNull(page.getParent());
        assertEquals(page.getParent().getId(), folder.getId());
        assertEquals(3, folder.getPages().size());
        assertEquals(3, pageManager.getPages(folder).size());

        Link link = pageManager.newLink("/default.link");
        assertEquals("Default", link.getTitle());
        link.setTitle("Default Link");
        link.setVersion("1.23");
        link.setShortTitle("Default");
        link.setTarget("top");
        link.setUrl("http://www.default.org/");
        metadata = link.getMetadata();
        metadata.addField(Locale.FRENCH, "title", "[fr] Default Link");
        metadata.addField(Locale.GERMAN, "title", "[de] Default Link");
        SecurityConstraints linkConstraints = link.newSecurityConstraints();
        linkConstraints.setOwner("user");
        List<SecurityConstraint> inlineLinkConstraints = new ArrayList<SecurityConstraint>(1);
        SecurityConstraint linkConstraint = link.newSecurityConstraint();
        linkConstraint.setUsers(Shared.makeListFromCSV("jetspeed"));
        linkConstraint.setPermissions(Shared.makeListFromCSV("edit"));
        inlineLinkConstraints.add(linkConstraint);
        linkConstraints.setSecurityConstraints(inlineLinkConstraints);
        List<String> linkConstraintsRefs = new ArrayList<String>(1);
        linkConstraintsRefs.add("manager-edit");
        linkConstraints.setSecurityConstraintsRefs(linkConstraintsRefs);
        link.setSecurityConstraints(linkConstraints);

        pageManager.updateLink(link);

        assertNotNull(link.getParent());
        assertEquals(link.getParent().getId(), folder.getId());
        assertNotNull(folder.getLinks());
        assertEquals(1, folder.getLinks().size());
        assertNotNull(pageManager.getLinks(folder));
        assertEquals(1, pageManager.getLinks(folder).size());

        PageSecurity pageSecurity = pageManager.newPageSecurity();
        List<SecurityConstraintsDef> constraintsDefs = new ArrayList<SecurityConstraintsDef>(2);
        SecurityConstraintsDef constraintsDef = pageManager.newSecurityConstraintsDef();
        constraintsDef.setName("public-view");
        List<SecurityConstraint> defConstraints = new ArrayList<SecurityConstraint>(1);
        SecurityConstraint defConstraint = pageSecurity.newSecurityConstraint();
        defConstraint.setUsers(Shared.makeListFromCSV("*"));
        defConstraint.setPermissions(Shared.makeListFromCSV("view"));
        defConstraints.add(defConstraint);
        constraintsDef.setSecurityConstraints(defConstraints);
        constraintsDefs.add(constraintsDef);
        constraintsDef = pageSecurity.newSecurityConstraintsDef();
        constraintsDef.setName("admin-all");
        defConstraints = new ArrayList<SecurityConstraint>(2);
        defConstraint = pageSecurity.newSecurityConstraint();
        defConstraint.setRoles(Shared.makeListFromCSV("admin"));
        defConstraint.setPermissions(Shared.makeListFromCSV("view,edit"));
        defConstraints.add(defConstraint);
        defConstraint = pageSecurity.newSecurityConstraint();
        defConstraint.setRoles(Shared.makeListFromCSV("nobody"));
        defConstraints.add(defConstraint);
        constraintsDef.setSecurityConstraints(defConstraints);
        constraintsDefs.add(constraintsDef);
        pageSecurity.setSecurityConstraintsDefs(constraintsDefs);
        List<String> globalConstraintsRefs = new ArrayList<String>(2);
        globalConstraintsRefs.add("admin-all");
        globalConstraintsRefs.add("public-view");
        pageSecurity.setGlobalSecurityConstraintsRefs(globalConstraintsRefs);

        pageManager.updatePageSecurity(pageSecurity);

        assertNotNull(pageSecurity.getParent());
        assertEquals(pageSecurity.getParent().getId(), folder.getId());
        assertNotNull(folder.getPageSecurity());

        PageTemplate pageTemplate = pageManager.newPageTemplate("/page-template.tpsml");
        pageTemplate.setTitle("Created Page Template");
        metadata = pageTemplate.getMetadata();
        metadata.addField(null, "description", "Page Template Description");
        rootFragmentElement = pageTemplate.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        root.setName("jetspeed-layouts::VelocityTwoColumns");
        portlet = pageManager.newFragment();
        portlet.setType(Fragment.PORTLET);
        portlet.setName("templates::MenusTemplatePortlet");
        portlet.setLayoutRow(1);
        portlet.setLayoutColumn(1);
        root.getFragments().add(portlet);
        fragmentReference = pageManager.newFragmentReference();
        fragmentReference.setRefId("fragment-definition");
        root.getFragments().add(fragmentReference);
        PageFragment pageFragment = pageManager.newPageFragment();
        root.getFragments().add(pageFragment);        
        SecurityConstraints constraints = pageTemplate.newSecurityConstraints();
        constraints.setOwner("admin");
        pageTemplate.setSecurityConstraints(constraints);

        pageManager.updatePageTemplate(pageTemplate);

        DynamicPage dynamicPage = pageManager.newDynamicPage("/dynamic-page.dpsml");
        dynamicPage.setContentType("default");
        assertTrue(dynamicPage.isInheritable());
        dynamicPage.setInheritable(false);        
        dynamicPage.setTitle("Created Dynamic Page");
        rootFragmentElement = dynamicPage.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        root.setName("jetspeed-layouts::VelocityTwoColumns");
        portlet = pageManager.newFragment();
        portlet.setType(Fragment.PORTLET);
        portlet.setName("content-app::ContentPortlet");
        root.getFragments().add(portlet);        
        fragmentReference = pageManager.newFragmentReference();
        fragmentReference.setRefId("fragment-definition");
        root.getFragments().add(fragmentReference);        

        pageManager.updateDynamicPage(dynamicPage);

        FragmentDefinition fragmentDefinition = pageManager.newFragmentDefinition("/fragment-definition.fpsml");
        fragmentDefinition.setTitle("Created Fragment Definition");
        rootFragmentElement = fragmentDefinition.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        root.setName("security::LoginPortlet");
        root.setId("fragment-definition");

        pageManager.updateFragmentDefinition(fragmentDefinition);

        // test duplicate creates
        try
        {
            Folder dupFolder = pageManager.newFolder("/");
            pageManager.updateFolder(dupFolder);
            assertTrue("Duplicate Folder / CREATED", false);
        }
        catch (FolderNotUpdatedException e)
        {
        }
        try
        {
            Page dupPage = pageManager.newPage("/default-page.psml");
            pageManager.updatePage(dupPage);
            assertTrue("Duplicate Page / CREATED", false);
        }
        catch (PageNotUpdatedException e)
        {
        }
        try
        {
            PageTemplate dupPageTemplate = pageManager.newPageTemplate("/page-template.tpsml");
            pageManager.updatePageTemplate(dupPageTemplate);
            assertTrue("Duplicate PageTemplate / CREATED", false);
        }
        catch (PageNotUpdatedException e)
        {
        }
        try
        {
            DynamicPage dupDynamicPage = pageManager.newDynamicPage("/dynamic-page.dpsml");
            pageManager.updateDynamicPage(dupDynamicPage);
            assertTrue("Duplicate DynamicPage / CREATED", false);
        }
        catch (PageNotUpdatedException e)
        {
        }
        try
        {
            FragmentDefinition dupFragmentDefinition = pageManager.newFragmentDefinition("/fragment-definition.fpsml");
            pageManager.updateFragmentDefinition(dupFragmentDefinition);
            assertTrue("Duplicate FragmentDefinition / CREATED", false);
        }
        catch (PageNotUpdatedException e)
        {
        }
        try
        {
            Link dupLink = pageManager.newLink("/default.link");
            pageManager.updateLink(dupLink);
            assertTrue("Duplicate Link / CREATED", false);
        }
        catch (FailedToUpdateDocumentException e)
        {
        }
        try
        {
            PageSecurity dupPageSecurity = pageManager.newPageSecurity();
            pageManager.updatePageSecurity(dupPageSecurity);
            assertTrue("Duplicate PageSecurity / CREATED", false);
        }
        catch (FailedToUpdateDocumentException e)
        {
        }

        // test folder/page creation with attributes on deep path
        Folder deepFolder = null;
        int pathIndex = deepFolderPath.indexOf('/', 1);
        while ((pathIndex != -1) && (pathIndex <= deepFolderPath.length()))
        {
            deepFolder = pageManager.newFolder(deepFolderPath.substring(0, pathIndex));
            pageManager.updateFolder(deepFolder);
            assertNotNull(deepFolder.getParent());
            assertNotNull(((Folder)deepFolder.getParent()).getFolders());
            assertEquals(1, ((Folder)deepFolder.getParent()).getFolders().size());
            assertNotNull(pageManager.getFolders((Folder)deepFolder.getParent()));
            assertEquals(1, pageManager.getFolders((Folder)deepFolder.getParent()).size());
            if (pathIndex < deepFolderPath.length())
            {
                pathIndex = deepFolderPath.indexOf('/', pathIndex+1);
                if (pathIndex == -1)
                {
                    pathIndex = deepFolderPath.length();
                }
            }
            else
            {
                pathIndex = -1;
            }
        }
        Page deepPage = pageManager.newPage(deepPagePath);
        pageManager.updatePage(deepPage);
        assertNotNull(deepPage.getParent());
        assertEquals(deepPage.getParent().getId(), deepFolder.getId());

        // test folder nodesets
        assertNotNull(folder.getFolders());
        assertEquals(1, folder.getFolders().size());
        assertNotNull(pageManager.getFolders(folder));
        assertEquals(1, pageManager.getFolders(folder).size());
        assertNotNull(folder.getAll());
        assertEquals(9, folder.getAll().size());
        assertNotNull(pageManager.getAll(folder));
        assertEquals(9, pageManager.getAll(folder).size());
        Iterator<Node> all = folder.getAll().iterator();
        assertEquals("some-other-page.psml", ((Node)all.next()).getName());
        assertEquals("default-page.psml", ((Node)all.next()).getName());
        assertEquals("__subsite-rootx", ((Node)all.next()).getName());
        assertEquals("another-page.psml", ((Node)all.next()).getName());
        assertEquals("default.link", ((Node)all.next()).getName());
        assertEquals("dynamic-page.dpsml", ((Node)all.next()).getName());
        assertEquals("fragment-definition.fpsml", ((Node)all.next()).getName());
        assertEquals("page-template.tpsml", ((Node)all.next()).getName());
        assertEquals("page.security", ((Node)all.next()).getName());
        assertNotNull(folder.getAll().subset(Page.DOCUMENT_TYPE));
        assertEquals(3, folder.getAll().subset(Page.DOCUMENT_TYPE).size());
        assertNotNull(folder.getAll().subset(PageTemplate.DOCUMENT_TYPE));
        assertEquals(1, folder.getAll().subset(PageTemplate.DOCUMENT_TYPE).size());
        assertNotNull(folder.getAll().inclusiveSubset(".*other.*"));
        assertEquals(2, folder.getAll().inclusiveSubset(".*other.*").size());
        assertNotNull(folder.getAll().inclusiveSubset("nomatch"));
        assertEquals(0, folder.getAll().inclusiveSubset("nomatch").size());
        assertNotNull(folder.getAll().exclusiveSubset(".*-page.psml"));
        assertEquals(6, folder.getAll().exclusiveSubset(".*-page.psml").size());
        
        // verify listener functionality and operation counts
        assertEquals(25, pmel.newNodeCount);
        assertEquals(0, pmel.updatedNodeCount);
        assertEquals(0, pmel.removedNodeCount);
        pageManager.removeListener(pmel);
    }

    public void doTestGets() throws Exception
    {
        PageManager pageManager = scm.lookupComponent("pageManager");
        pageManager.reset();
        PageManagerEventListenerImpl pmel = new PageManagerEventListenerImpl();
        pageManager.addListener(pmel);
        
        // read documents and folders from persisted store
        try
        {
            PageSecurity check = pageManager.getPageSecurity();
            assertEquals("/page.security", check.getPath());
            assertEquals("page.security", check.getName());
            assertEquals("/page.security", check.getUrl());
            assertNotNull(check.getSecurityConstraintsDefs());
            assertEquals(2, check.getSecurityConstraintsDefs().size());
            assertEquals("admin-all", check.getSecurityConstraintsDefs().get(0).getName());
            assertNotNull(check.getSecurityConstraintsDefs().get(0).getSecurityConstraints());
            assertEquals(2, check.getSecurityConstraintsDefs().get(0).getSecurityConstraints().size());
            assertEquals("view,edit", Shared.makeCSVFromList(check.getSecurityConstraintsDefs().get(0).getSecurityConstraints().get(0).getPermissions()));
            assertEquals("public-view", check.getSecurityConstraintsDefs().get(1).getName());
            assertNotNull(check.getSecurityConstraintsDefs().get(1).getSecurityConstraints());
            assertEquals(1, check.getSecurityConstraintsDefs().get(1).getSecurityConstraints().size());
            assertEquals("*", Shared.makeCSVFromList(check.getSecurityConstraintsDefs().get(1).getSecurityConstraints().get(0).getUsers()));
            assertEquals("view", Shared.makeCSVFromList(check.getSecurityConstraintsDefs().get(1).getSecurityConstraints().get(0).getPermissions()));

            assertNotNull(check.getGlobalSecurityConstraintsRefs());
            assertEquals(2, check.getGlobalSecurityConstraintsRefs().size());
            assertEquals("admin-all", check.getGlobalSecurityConstraintsRefs().get(0));
            assertEquals("public-view", check.getGlobalSecurityConstraintsRefs().get(1));
            assertNotNull(check.getParent());
        }
        catch (DocumentNotFoundException e)
        {
            assertTrue("PageSecurity NOT FOUND", false);
        }
        try
        {
            Link check = pageManager.getLink("/default.link");
            assertEquals("/default.link", check.getPath());
            assertEquals("default.link", check.getName());
            assertEquals("Default Link", check.getTitle());
            assertEquals("1.23", check.getVersion());            
            assertEquals("Default", check.getShortTitle());
            assertEquals("top", check.getTarget());
            assertEquals("http://www.default.org/", check.getUrl());
            assertNotNull(check.getMetadata());
            assertEquals("[fr] Default Link", check.getTitle(Locale.FRENCH));
            assertEquals("[de] Default Link", check.getTitle(Locale.GERMAN));
            assertNotNull(check.getSecurityConstraints());
            assertEquals("user", check.getSecurityConstraints().getOwner());
            assertNotNull(check.getSecurityConstraints().getSecurityConstraintsRefs());
            assertEquals(1, check.getSecurityConstraints().getSecurityConstraintsRefs().size());
            assertEquals("manager-edit", (String)check.getSecurityConstraints().getSecurityConstraintsRefs().get(0));
            assertNotNull(check.getSecurityConstraints().getSecurityConstraints());
            assertEquals(1, check.getSecurityConstraints().getSecurityConstraints().size());
            assertEquals("jetspeed", Shared.makeCSVFromList(check.getSecurityConstraints().getSecurityConstraints().get(0).getUsers()));
            assertNotNull(check.getParent());
        }
        catch (PageNotFoundException e)
        {
            assertTrue("Link /default.link NOT FOUND", false);
        }
        try
        {
            FragmentDefinition check = pageManager.getFragmentDefinition("/fragment-definition.fpsml");
            assertNotNull(check);
            assertEquals("Created Fragment Definition", check.getTitle());
            assertNotNull(check.getRootFragment());
            BaseFragmentElement rootFragmentElement = check.getRootFragment();
            assertTrue(rootFragmentElement instanceof Fragment);
            Fragment root = (Fragment)rootFragmentElement;
            assertEquals("fragment-definition", root.getId());
            assertEquals("security::LoginPortlet", root.getName());
            assertEquals(Fragment.PORTLET, root.getType());
            assertTrue(root.getFragments().isEmpty());
        }
        catch (PageNotFoundException e)
        {
            assertTrue("FragmentDefinition /fragment-definition NOT FOUND", false);
        }
        try
        {
            DynamicPage check = pageManager.getDynamicPage("/dynamic-page.dpsml");
            assertNotNull(check);
            assertEquals("default", check.getContentType());
            assertFalse(check.isInheritable());
            assertEquals("Created Dynamic Page", check.getTitle());
            assertNotNull(check.getRootFragment());
            BaseFragmentElement rootFragmentElement = check.getRootFragment();
            assertTrue(rootFragmentElement instanceof Fragment);
            Fragment root = (Fragment)rootFragmentElement;
            assertEquals("jetspeed-layouts::VelocityTwoColumns", root.getName());
            assertEquals(Fragment.LAYOUT, root.getType());
            assertEquals(2, root.getFragments().size());
            BaseFragmentElement checkElement0 = root.getFragments().get(0);
            assertTrue(checkElement0 instanceof Fragment);
            Fragment check0 = (Fragment)checkElement0;
            assertEquals("content-app::ContentPortlet", check0.getName());
            assertEquals(Fragment.PORTLET, check0.getType());
            BaseFragmentElement checkElement1 = root.getFragments().get(1);
            assertTrue(checkElement1 instanceof FragmentReference);
            FragmentReference check1 = (FragmentReference)checkElement1;
            assertEquals("fragment-definition", check1.getRefId());
        }
        catch (PageNotFoundException e)
        {
            assertTrue("DynamicPage /dynamic-page.dpsml NOT FOUND", false);
        }
        try
        {
            PageTemplate check = pageManager.getPageTemplate("/page-template.tpsml");
            assertNotNull(check);
            assertEquals("Created Page Template", check.getTitle());
            assertNotNull(check.getRootFragment());
            BaseFragmentElement rootFragmentElement = check.getRootFragment();
            assertTrue(rootFragmentElement instanceof Fragment);
            Fragment root = (Fragment)rootFragmentElement;
            assertEquals("jetspeed-layouts::VelocityTwoColumns", root.getName());
            assertEquals(Fragment.LAYOUT, root.getType());
            assertEquals(3, root.getFragments().size());
            BaseFragmentElement checkElement0 = root.getFragments().get(0);
            assertTrue(checkElement0 instanceof Fragment);
            Fragment check0 = (Fragment)checkElement0;
            assertEquals("templates::MenusTemplatePortlet", check0.getName());
            assertEquals(Fragment.PORTLET, check0.getType());
            assertEquals(1, check0.getLayoutRow());
            BaseFragmentElement checkElement1 = root.getFragments().get(1);
            assertTrue(checkElement1 instanceof FragmentReference);
            FragmentReference check1 = (FragmentReference)checkElement1;
            assertEquals("fragment-definition", check1.getRefId());
            BaseFragmentElement checkElement2 = root.getFragments().get(2);
            assertTrue(checkElement2 instanceof PageFragment);
        }
        catch (PageNotFoundException e)
        {
            assertTrue("PageTemplate /page-template.tpsml NOT FOUND", false);
        }
        try
        {
            Page check = pageManager.getPage("/default-page.psml");
            assertEquals("/default-page.psml", check.getPath());
            assertEquals("default-page.psml", check.getName());
            assertEquals("/default-page.psml", check.getUrl());
            assertEquals("Default Page", check.getTitle());
            assertEquals("6.89", check.getVersion());            
            assertEquals("tigris", check.getEffectiveDefaultDecorator(Fragment.LAYOUT));
            assertEquals("tigris", check.getDefaultDecorator(Fragment.LAYOUT));
            assertEquals("blue-gradient", check.getDefaultDecorator(Fragment.PORTLET));
            assertEquals("skin-1", check.getSkin());
            assertEquals("Default", check.getShortTitle());
            assertNotNull(check.getMetadata());
            assertEquals("[fr] Default Page", check.getTitle(Locale.FRENCH));
            assertEquals("[ja] Default Page", check.getTitle(Locale.JAPANESE));
            assertNotNull(check.getSecurityConstraints());
            assertEquals("user", check.getSecurityConstraints().getOwner());
            assertNotNull(check.getSecurityConstraints().getSecurityConstraintsRefs());
            assertEquals(1, check.getSecurityConstraints().getSecurityConstraintsRefs().size());
            assertEquals("manager-edit", (String)check.getSecurityConstraints().getSecurityConstraintsRefs().get(0));
            assertNotNull(check.getSecurityConstraints().getSecurityConstraints());
            assertEquals(1, check.getSecurityConstraints().getSecurityConstraints().size());
            assertEquals("jetspeed", Shared.makeCSVFromList(check.getSecurityConstraints().getSecurityConstraints().get(0).getUsers()));
            assertNotNull(check.getMenuDefinitions());
            assertEquals(2, check.getMenuDefinitions().size());
            MenuDefinition checkMenu = check.getMenuDefinitions().get(0);
            assertEquals("page-menu-1", checkMenu.getName());
            assertEquals("The Test Page Menu", checkMenu.getTitle());
            assertEquals("[fr] The Test Page Menu", checkMenu.getTitle(Locale.FRENCH));
            assertNotNull(checkMenu.getMenuElements());
            assertEquals(5,checkMenu.getMenuElements().size());
            assertTrue(checkMenu.getMenuElements().get(0) instanceof MenuSeparatorDefinition);
            assertEquals("-- Page Menu --", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getText());
            assertTrue(checkMenu.getMenuElements().get(1) instanceof MenuOptionsDefinition);
            assertEquals("/*.psml", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(1)).getOptions());
            assertTrue(checkMenu.getMenuElements().get(2) instanceof MenuDefinition);
            assertEquals("/*/", ((MenuDefinition)checkMenu.getMenuElements().get(2)).getOptions());
            assertNotNull(((MenuDefinition)checkMenu.getMenuElements().get(2)).getMenuElements());
            assertTrue(((MenuDefinition)checkMenu.getMenuElements().get(2)).getMenuElements().isEmpty());
            assertTrue(checkMenu.getMenuElements().get(3) instanceof MenuExcludeDefinition);
            assertEquals("exclude-menu", ((MenuExcludeDefinition)checkMenu.getMenuElements().get(3)).getName());
            assertTrue(checkMenu.getMenuElements().get(4) instanceof MenuIncludeDefinition);
            assertEquals("include-menu", ((MenuIncludeDefinition)checkMenu.getMenuElements().get(4)).getName());
            checkMenu = check.getMenuDefinitions().get(1);
            assertEquals("page-menu-2", checkMenu.getName());
            assertNotNull(checkMenu.getMenuElements());
            assertTrue(checkMenu.getMenuElements().isEmpty());
            assertNotNull(check.getRootFragment());
            assertEquals("blue-gradient", check.getRootFragment().getDecorator());
            BaseFragmentElement checkRootFragmentElement = check.getRootFragment();
            assertTrue(checkRootFragmentElement instanceof Fragment);
            Fragment checkRootFragment = (Fragment)checkRootFragmentElement;
            assertEquals("jetspeed-layouts::VelocityTwoColumns", checkRootFragment.getName());
            assertEquals("Root", check.getRootFragment().getShortTitle());
            assertEquals("Root Fragment", check.getRootFragment().getTitle());
            assertEquals("Normal", check.getRootFragment().getState());
            assertEquals("50%,50%", check.getRootFragment().getLayoutSizes());
            assertNotNull(check.getRootFragment().getProperties());
            assertEquals("custom-prop-value1", check.getRootFragment().getProperty("custom-prop1"));
            assertEquals("custom-value-0", checkRootFragment.getProperty("custom-0"));
            assertEquals("custom-value-1", checkRootFragment.getProperty("custom-1"));
            assertEquals("custom-value-2", checkRootFragment.getProperty("custom-2"));
            assertEquals("custom-value-3", checkRootFragment.getProperty("custom-3"));
            final Fragment checkUserFragment = checkRootFragment;
            Exception userException = (Exception)JSSubject.doAsPrivileged(constructUserSubject(), new PrivilegedAction()
            {
                public Object run()
                {
                    try
                    {
                        List<FragmentProperty> properties = checkUserFragment.getProperties();
                        assertNotNull(properties);
                        assertEquals((FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED ? 12 : 7), properties.size());
                        assertEquals("50%,50%", checkUserFragment.getProperty(Fragment.SIZES_PROPERTY_NAME));
                        assertEquals("custom-value-0", checkUserFragment.getProperty("custom-0"));
                        assertNull(checkUserFragment.getProperty("custom-0", Fragment.USER_PROPERTY_SCOPE, null));
                        assertNull(checkUserFragment.getProperty("custom-0", Fragment.USER_PROPERTY_SCOPE, "user"));
                        if (FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
                        {
                            assertEquals("custom-value-role-1", checkUserFragment.getProperty("custom-1"));
                            assertNotNull(checkUserFragment.getProperty("custom-1", Fragment.ROLE_PROPERTY_SCOPE, "role"));
                        }
                        else
                        {
                            assertEquals("custom-value-1", checkUserFragment.getProperty("custom-1"));                        
                        }
                        assertNull(checkUserFragment.getProperty("custom-1", Fragment.USER_PROPERTY_SCOPE, "user"));
                        if (FragmentProperty.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED)
                        {
                            assertEquals("custom-value-group-2", checkUserFragment.getProperty("custom-2"));
                            assertNotNull(checkUserFragment.getProperty("custom-2", Fragment.GROUP_PROPERTY_SCOPE, "group"));
                        }
                        else
                        {
                            assertEquals("custom-value-2", checkUserFragment.getProperty("custom-2"));                        
                        }
                        assertNull(checkUserFragment.getProperty("custom-2", Fragment.USER_PROPERTY_SCOPE, "user"));
                        assertEquals("custom-value-user-3", checkUserFragment.getProperty("custom-3"));
                        assertNotNull(checkUserFragment.getProperty("custom-3", Fragment.USER_PROPERTY_SCOPE, null));
                        assertNotNull(checkUserFragment.getProperty("custom-3", Fragment.USER_PROPERTY_SCOPE, "user"));
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
            assertNotNull(checkRootFragment.getFragments());
            assertEquals(3, checkRootFragment.getFragments().size());
            BaseFragmentElement checkElement0 = checkRootFragment.getFragments().get(0);
            assertTrue(checkElement0 instanceof Fragment);
            Fragment check0 = (Fragment)checkElement0;
            assertEquals("security::LoginPortlet", check0.getName());
            assertEquals("Portlet", check0.getShortTitle());
            assertEquals("Portlet Fragment", check0.getTitle());
            assertEquals("Normal", check0.getState());
            assertEquals(88, check0.getLayoutRow());
            assertEquals(88, check0.getIntProperty(Fragment.ROW_PROPERTY_NAME));
            assertEquals(99, check0.getLayoutColumn());
            assertNotNull(check0.getProperty(Fragment.X_PROPERTY_NAME));
            assertTrue(check0.getProperty(Fragment.X_PROPERTY_NAME).startsWith("12.3"));
            assertTrue((check0.getLayoutX() > 12.0F) && (check0.getLayoutX() < 13.0F));
            assertTrue((check0.getFloatProperty(Fragment.X_PROPERTY_NAME) > 12.0F) &&
                       (check0.getFloatProperty(Fragment.X_PROPERTY_NAME) < 13.0F));
            assertTrue((check0.getLayoutY() > 23.0F) && (check0.getLayoutY() < 24.0F));
            assertTrue((check0.getLayoutZ() > 34.0F) && (check0.getLayoutZ() < 35.0F));
            assertTrue((check0.getLayoutWidth() > 45.0F) && (check0.getLayoutWidth() < 46.0F));
            assertTrue((check0.getLayoutHeight() > 56.0F) && (check0.getLayoutWidth() < 57.0F));
            assertNotNull(check0.getPreferences());
            assertEquals(2, check0.getPreferences().size());
            assertEquals("pref0", check0.getPreferences().get(0).getName());
            assertTrue(check0.getPreferences().get(0).isReadOnly());
            assertNotNull(check0.getPreferences().get(0).getValueList());
            assertEquals(2, check0.getPreferences().get(0).getValueList().size());
            assertEquals("pref0-value0", check0.getPreferences().get(0).getValueList().get(0));
            assertEquals("pref0-value1", check0.getPreferences().get(0).getValueList().get(1));
            assertEquals("pref1", check0.getPreferences().get(1).getName());
            assertFalse(check0.getPreferences().get(1).isReadOnly());
            assertNotNull(check0.getPreferences().get(1).getValueList());
            assertEquals(1, check0.getPreferences().get(1).getValueList().size());
            assertEquals("pref1-value", check0.getPreferences().get(1).getValueList().get(0));
            BaseFragmentElement checkElement1 = checkRootFragment.getFragments().get(1);
            assertTrue(checkElement1 instanceof Fragment);
            Fragment check1 = (Fragment)checkElement1;
            assertEquals("some-app::SomePortlet", check1.getName());
            assertEquals("Some Portlet", check1.getShortTitle());
            assertEquals("Some Portlet Fragment", check1.getTitle());
            assertEquals("Normal", check1.getState());
            assertEquals(22, check1.getLayoutRow());
            assertEquals(11, check1.getLayoutColumn());
            assertTrue((check1.getLayoutX() > 11.0F) && (check1.getLayoutX() < 12.0F));
            assertTrue((check1.getLayoutY() > 22.0F) && (check1.getLayoutY() < 23.0F));
            assertTrue((check1.getLayoutZ() > 33.0F) && (check1.getLayoutZ() < 34.0F));
            assertTrue((check1.getLayoutWidth() > 44.0F) && (check1.getLayoutWidth() < 45.0F));
            assertTrue((check1.getLayoutHeight() > 55.0F) && (check1.getLayoutWidth() < 56.0F));
            assertNotNull(check1.getSecurityConstraints());
            assertEquals("user", check1.getSecurityConstraints().getOwner());
            assertNotNull(check.getFragmentById(check0.getId()));
            assertNotNull(check.getFragmentsByName("some-app::SomePortlet"));
            assertEquals(1, check.getFragmentsByName("some-app::SomePortlet").size());
            assertNotNull(check.getParent());
            BaseFragmentElement checkElement2 = checkRootFragment.getFragments().get(2);
            assertTrue(checkElement2 instanceof FragmentReference);
            FragmentReference checkFragmentReference = (FragmentReference)checkElement2;
            assertTrue(checkFragmentReference.getRefId().equals("fragment-definition"));
            assertNotNull(check.getFragmentsByInterface(null));
            assertEquals(4, check.getFragmentsByInterface(null).size());
            assertNotNull(check.getFragmentsByInterface(FragmentReference.class));
            assertEquals(1, check.getFragmentsByInterface(FragmentReference.class).size());
        }
        catch (PageNotFoundException e)
        {
            assertTrue("Page /default-page.psml NOT FOUND", false);
        }
        try
        {
            Folder check = pageManager.getFolder("/");
            assertEquals("/", check.getPath());
            assertEquals("/", check.getName());
            assertEquals("/", check.getUrl());
            assertEquals("Root Folder", check.getTitle());
            assertEquals("jetspeed", check.getEffectiveDefaultDecorator(Fragment.LAYOUT));
            assertEquals("jetspeed", check.getDefaultDecorator(Fragment.LAYOUT));
            assertEquals("gray-gradient", check.getDefaultDecorator(Fragment.PORTLET));
            assertEquals("skin-1", check.getSkin());
            assertEquals("default-page.psml", check.getDefaultPage());
            assertEquals("Root", check.getShortTitle());
            assertNotNull(check.getMetadata());
            assertEquals("[fr] Root Folder", check.getTitle(Locale.FRENCH));
            assertNotNull(check.getSecurityConstraints());
            assertEquals("admin", check.getSecurityConstraints().getOwner());
            assertNotNull(check.getSecurityConstraints().getSecurityConstraintsRefs());
            assertEquals(2, check.getSecurityConstraints().getSecurityConstraintsRefs().size());
            assertEquals("public-edit", check.getSecurityConstraints().getSecurityConstraintsRefs().get(1));
            assertNotNull(check.getSecurityConstraints().getSecurityConstraints());
            assertEquals(2, check.getSecurityConstraints().getSecurityConstraints().size());
            assertEquals("user,admin", Shared.makeCSVFromList(check.getSecurityConstraints().getSecurityConstraints().get(0).getUsers()));
            assertEquals("manager", Shared.makeCSVFromList(check.getSecurityConstraints().getSecurityConstraints().get(0).getRoles()));
            assertEquals("*", Shared.makeCSVFromList(check.getSecurityConstraints().getSecurityConstraints().get(0).getGroups()));
            assertEquals("edit", Shared.makeCSVFromList(check.getSecurityConstraints().getSecurityConstraints().get(1).getPermissions()));
            assertNotNull(check.getDocumentOrder());
            assertEquals(2, check.getDocumentOrder().size());
            assertEquals("some-other-page.psml", check.getDocumentOrder().get(0));
            assertEquals("default-page.psml", check.getDocumentOrder().get(1));
            assertNull(check.getParent());
            assertNotNull(check.getPageSecurity());
            assertNotNull(check.getPages());
            assertEquals(3, check.getPages().size());
            assertNotNull(check.getLinks());
            assertEquals(1, check.getLinks().size());
            assertNotNull(check.getFolders());
            assertEquals(1, check.getFolders().size());
            assertNotNull(check.getAll());
            assertEquals(9, check.getAll().size());
            Iterator<Node> all = check.getAll().iterator();
            assertEquals("some-other-page.psml", ((Node)all.next()).getName());
            assertEquals("default-page.psml", ((Node)all.next()).getName());
            assertEquals("__subsite-rootx", ((Node)all.next()).getName());
            assertEquals("another-page.psml", ((Node)all.next()).getName());
            assertEquals("default.link", ((Node)all.next()).getName());
            assertEquals("dynamic-page.dpsml", ((Node)all.next()).getName());
            assertEquals("fragment-definition.fpsml", ((Node)all.next()).getName());
            assertEquals("page-template.tpsml", ((Node)all.next()).getName());
            assertEquals("page.security", ((Node)all.next()).getName());
            assertNotNull(check.getMenuDefinitions());
            assertEquals(2, check.getMenuDefinitions().size());
            MenuDefinition checkMenu = check.getMenuDefinitions().get(0);
            assertEquals("folder-breadcrumb-menu", checkMenu.getName());
            assertEquals("bread-crumbs", checkMenu.getSkin());
            assertEquals("./", checkMenu.getOptions());
            assertTrue(checkMenu.isPaths());
            assertNotNull(checkMenu.getMenuElements());
            assertTrue(checkMenu.getMenuElements().isEmpty());
            checkMenu = check.getMenuDefinitions().get(1);
            assertEquals("folder-menu", checkMenu.getName());
            assertEquals("The Test Folder Menu", checkMenu.getTitle());
            assertEquals("Folder Menu", checkMenu.getShortTitle());
            assertEquals("group-fallback", checkMenu.getProfile());
            assertEquals("[fr] Folder Menu", checkMenu.getShortTitle(Locale.FRENCH));
            assertEquals("[fr] The Test Folder Menu", checkMenu.getTitle(Locale.FRENCH));
            assertNotNull(checkMenu.getMenuElements());
            assertEquals(6,checkMenu.getMenuElements().size());
            assertTrue(checkMenu.getMenuElements().get(0) instanceof MenuSeparatorDefinition);
            assertEquals("-- Folder Menu --", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getText());
            assertEquals("Rollover: Folder Menu", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getTitle());
            assertEquals("header", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getSkin());
            assertEquals("-- [fr] Folder Menu --", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getText(Locale.FRENCH));
            assertEquals("[fr] Rollover: Folder Menu", ((MenuSeparatorDefinition)checkMenu.getMenuElements().get(0)).getTitle(Locale.FRENCH));
            assertTrue(checkMenu.getMenuElements().get(1) instanceof MenuOptionsDefinition);
            assertEquals("/*.psml", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(1)).getOptions());
            assertTrue(((MenuOptionsDefinition)checkMenu.getMenuElements().get(1)).isRegexp());
            assertEquals("flash", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(1)).getSkin());
            assertTrue(checkMenu.getMenuElements().get(2) instanceof MenuOptionsDefinition);
            assertEquals("/folder0", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(2)).getOptions());
            assertEquals("role-fallback", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(2)).getProfile());
            assertEquals("/folder*", ((MenuOptionsDefinition)checkMenu.getMenuElements().get(2)).getOrder());
            assertEquals(1, ((MenuOptionsDefinition)checkMenu.getMenuElements().get(2)).getDepth());
            assertTrue(((MenuOptionsDefinition)checkMenu.getMenuElements().get(2)).isPaths());
            assertTrue(checkMenu.getMenuElements().get(3) instanceof MenuDefinition);
            assertEquals("/*/", ((MenuDefinition)checkMenu.getMenuElements().get(3)).getOptions());
            assertTrue(((MenuDefinition)checkMenu.getMenuElements().get(3)).isRegexp());
            assertEquals(2, ((MenuDefinition)checkMenu.getMenuElements().get(3)).getDepth());
            assertEquals("/x*/,/y*/,/z*/", ((MenuDefinition)checkMenu.getMenuElements().get(3)).getOrder());
            assertEquals("bold", ((MenuDefinition)checkMenu.getMenuElements().get(3)).getSkin());
            assertNotNull(((MenuDefinition)checkMenu.getMenuElements().get(3)).getMenuElements());
            assertTrue(((MenuDefinition)checkMenu.getMenuElements().get(3)).getMenuElements().isEmpty());
            assertTrue(checkMenu.getMenuElements().get(4) instanceof MenuExcludeDefinition);
            assertEquals("exclude-menu", ((MenuExcludeDefinition)checkMenu.getMenuElements().get(4)).getName());
            assertTrue(checkMenu.getMenuElements().get(5) instanceof MenuIncludeDefinition);
            assertEquals("include-menu", ((MenuIncludeDefinition)checkMenu.getMenuElements().get(5)).getName());
            assertTrue(((MenuIncludeDefinition)checkMenu.getMenuElements().get(5)).isNest());
        }
        catch (FolderNotFoundException e)
        {
            assertTrue("Folder / NOT FOUND", false);
        }
        try
        {
            Page check = pageManager.getPage("/another-page.psml");
            assertEquals("/another-page.psml", check.getPath());
            assertEquals("another-page.psml", check.getName());
            assertEquals("Another Page", check.getTitle());
            assertEquals("jetspeed", check.getEffectiveDefaultDecorator(Fragment.LAYOUT));
            assertEquals("gray-gradient", check.getEffectiveDefaultDecorator(Fragment.PORTLET));
            assertNull(check.getDefaultDecorator(Fragment.LAYOUT));
            assertNull(check.getDefaultDecorator(Fragment.PORTLET));
        }
        catch (PageNotFoundException e)
        {
            assertTrue("Page /default-page.psml NOT FOUND", false);
        }

        try
        {
            Page check = pageManager.getPage(deepPagePath);
            assertEquals(deepPagePath, check.getPath());
            assertNotNull(check.getParent());
        }
        catch (PageNotFoundException e)
        {
            assertTrue("Page " + deepPagePath + " NOT FOUND", false);
        }
        try
        {
            Folder check = pageManager.getFolder(deepFolderPath);
            assertEquals(deepFolderPath, check.getPath());
            assertNotNull(check.getParent());
        }
        catch (FolderNotFoundException e)
        {
            assertTrue("Folder " + deepFolderPath + " NOT FOUND", false);
        }
        
        // verify listener functionality and operation counts
        assertEquals(0, pmel.newNodeCount);
        assertEquals(0, pmel.updatedNodeCount);
        assertEquals(0, pmel.removedNodeCount);
        pageManager.removeListener(pmel);
    }

    public void doTestUpdates() throws Exception
    {
        PageManager pageManager = scm.lookupComponent("pageManager");
        pageManager.reset();
        PageManagerEventListenerImpl pmel = new PageManagerEventListenerImpl();
        pageManager.addListener(pmel);
        
        // update documents and folders in persisted store
        PageSecurity pageSecurity = pageManager.getPageSecurity();
        assertEquals("/page.security", pageSecurity.getPath());
        pageSecurity.getGlobalSecurityConstraintsRefs().add("UPDATED");
        pageManager.updatePageSecurity(pageSecurity);
        Page page = pageManager.getPage("/default-page.psml");
        assertEquals("/default-page.psml", page.getPath());
        page.setTitle("UPDATED");
        FragmentProperty removeProperty = null;
        for (FragmentProperty property : page.getRootFragment().getProperties())
        {
            if (property.getName().equals("custom-prop1"))
            {
                removeProperty = property;
            }
        }
        page.getRootFragment().getProperties().remove(removeProperty);
        FragmentProperty property = pageManager.newFragmentProperty();
        property.setName("UPDATED");
        property.setValue("UPDATED");
        page.getRootFragment().getProperties().add(property);
        BaseFragmentElement rootFragmentElement = page.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        Fragment root = (Fragment)rootFragmentElement;
        assertNotNull(root.getFragments());
        assertEquals(3, root.getFragments().size());
        String removeId = ((BaseFragmentElement)root.getFragments().get(1)).getId();
        assertNotNull(page.removeFragmentById(removeId));
        SecurityConstraint pageConstraint = page.newSecurityConstraint();
        pageConstraint.setUsers(Shared.makeListFromCSV("UPDATED"));
        page.getSecurityConstraints().getSecurityConstraints().add(0, pageConstraint);
        pageManager.updatePage(page);

        PageTemplate pageTemplate = pageManager.getPageTemplate("/page-template.tpsml");
        assertEquals("/page-template.tpsml", pageTemplate.getPath());
        pageTemplate.setTitle("UPDATED");
        rootFragmentElement = pageTemplate.getRootFragment();
        assertTrue(rootFragmentElement instanceof Fragment);
        root = (Fragment)rootFragmentElement;
        assertTrue(root.getFragments().size() == 3);
        BaseFragmentElement removeElement0 = (BaseFragmentElement)root.getFragments().get(0);
        assertTrue(removeElement0 instanceof Fragment);
        pageTemplate.removeFragmentById(removeElement0.getId());
        pageManager.updatePageTemplate(pageTemplate);

        DynamicPage dynamicPage = pageManager.getDynamicPage("/dynamic-page.dpsml");
        assertEquals("/dynamic-page.dpsml", dynamicPage.getPath());
        dynamicPage.setTitle("UPDATED");
        dynamicPage.setContentType("UPDATED");
        dynamicPage.setInheritable(true);
        pageManager.updateDynamicPage(dynamicPage);

        FragmentDefinition fragmentDefinition = pageManager.getFragmentDefinition("/fragment-definition.fpsml");
        assertEquals("/fragment-definition.fpsml", fragmentDefinition.getPath());
        fragmentDefinition.setTitle("UPDATED");
        pageManager.updateFragmentDefinition(fragmentDefinition);

        Link link = pageManager.getLink("/default.link");
        assertEquals("/default.link", link.getPath());
        link.setTitle("UPDATED");
        link.getSecurityConstraints().setOwner("UPDATED");
        pageManager.updateLink(link);

        Folder folder = pageManager.getFolder("/");
        assertEquals("/", folder.getPath());
        folder.setTitle("UPDATED");
        folder.getDocumentOrder().remove("some-other-page.psml");
        folder.getDocumentOrder().add("UPDATED");
        folder.getDocumentOrder().add("some-other-page.psml");
        MenuDefinition updateMenu = (MenuDefinition)folder.getMenuDefinitions().get(1);
        updateMenu.setName("UPDATED");
        updateMenu.setTitle("UPDATED");
        updateMenu.getMetadata().addField(Locale.JAPANESE, "short-title", "[ja] UPDATED");
        ((MenuOptionsDefinition)updateMenu.getMenuElements().get(2)).setProfile("UPDATED");
        pageManager.updateFolder(folder);
        assertNotNull(folder.getAll());
        assertEquals(9, folder.getAll().size());
        Iterator<Node> all = folder.getAll().iterator();
        assertEquals("default-page.psml", ((Node)all.next()).getName());
        assertEquals("some-other-page.psml", ((Node)all.next()).getName());

        folder.setTitle("FOLDER-UPDATED-DEEP");
        page.setTitle("FOLDER-UPDATED-DEEP");
        link.setTitle("FOLDER-UPDATED-DEEP");
        Folder deepFolder = pageManager.getFolder(deepFolderPath);
        deepFolder.setTitle("FOLDER-UPDATED-DEEP");
        Page deepPage = pageManager.getPage(deepPagePath);
        deepPage.setTitle("FOLDER-UPDATED-DEEP");
        pageManager.updateFolder(folder, true);

        // verify listener functionality and operation counts
        assertEquals(0, pmel.newNodeCount);
        assertEquals(32, pmel.updatedNodeCount);
        assertEquals(0, pmel.removedNodeCount);
        pageManager.removeListener(pmel);
    }

    public void doTestRemoves() throws Exception
    {
        PageManager pageManager = scm.lookupComponent("pageManager");
        pageManager.reset();
        PageManagerEventListenerImpl pmel = new PageManagerEventListenerImpl();
        pageManager.addListener(pmel);
        
        // remove root folder
        try
        {
            Folder remove = pageManager.getFolder("/");
            assertEquals("/", remove.getPath());
            pageManager.removeFolder(remove);
        }
        catch (FolderNotFoundException e)
        {
            assertTrue("Folder / NOT FOUND", false);
        }
        
        // reset page manager cache
        pageManager.reset();
        
        // verify root folder deep removal
        try
        {
            pageManager.getFolder("/");
            assertTrue("Folder / FOUND", false);
        }
        catch (FolderNotFoundException e)
        {
        }
        try
        {
            pageManager.getPageSecurity();
            assertTrue("PageSecurity FOUND", false);
        }
        catch (DocumentNotFoundException e)
        {
        }
        try
        {
            pageManager.getLink("/default.link");
            assertTrue("Link /default.link FOUND", false);
        }
        catch (DocumentNotFoundException e)
        {
        }
        try
        {
            pageManager.getPage("/default-page.psml");
            assertTrue("Page /default-page.psml FOUND", false);
        }
        catch (PageNotFoundException e)
        {
        }
        try
        {
            pageManager.getPageTemplate("/page-template.tpsml");
            assertTrue("Page /page-template.tpsml FOUND", false);
        }
        catch (PageNotFoundException e)
        {
        }
        try
        {
            pageManager.getDynamicPage("/dynamic-page.dpsml");
            assertTrue("Page /dynamic-page.dpsml FOUND", false);
        }
        catch (PageNotFoundException e)
        {
        }
        try
        {
            pageManager.getFragmentDefinition("/fragment-definition.fpsml");
            assertTrue("Page /fragment-definition.fpsml FOUND", false);
        }
        catch (PageNotFoundException e)
        {
        }
        try
        {
            pageManager.getFolder("/");
            assertTrue("Folder / FOUND", false);
        }
        catch (FolderNotFoundException e)
        {
        }
        try
        {
            pageManager.getFolder(deepFolderPath);
            assertTrue("Folder " + deepFolderPath + " FOUND", false);
        }
        catch (FolderNotFoundException e)
        {
        }
        try
        {
            pageManager.getPage(deepPagePath);
            assertTrue("Page " + deepPagePath + " FOUND", false);
        }
        catch (PageNotFoundException e)
        {
        }
        // verify listener functionality and operation counts
        assertEquals(0, pmel.newNodeCount);
        assertEquals(0, pmel.updatedNodeCount);
        assertEquals(25, pmel.removedNodeCount);
        pageManager.removeListener(pmel);
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
