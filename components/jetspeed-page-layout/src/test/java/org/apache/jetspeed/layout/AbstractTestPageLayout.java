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
package org.apache.jetspeed.layout;

import org.apache.jetspeed.components.test.AbstractSpringTestCase;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.security.JSSubject;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PrincipalsSet;
import org.apache.jetspeed.security.SecurityAttributeType;
import org.apache.jetspeed.security.SecurityAttributeTypes;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.impl.TransientJetspeedPrincipal;

import javax.security.auth.Subject;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * AbstractTestPageLayout
 * <P>
 * Intended to contain the common tests for both CastoXmlPageManager-based test and DatabasePageManager-based test. 
 * </P>
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class AbstractTestPageLayout extends AbstractSpringTestCase
{
    /**
     * PageManager component
     */
    private PageManager pageManager;

    /**
     * PageLayout component
     */
    private PageLayoutComponent pageLayout;

    /**
     * Test user subject
     */
    private Subject userSubject;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception
    {
        // setup components, subjects, and cleanup from previous run
        super.setUp();
        pageManager = scm.lookupComponent("pageManager");
        pageLayout = scm.lookupComponent("pageLayout");
        Set principals = new PrincipalsSet();
        principals.add(new TestUser("user"));
        userSubject = new Subject(true, principals, new HashSet(), new HashSet());
        cleanupTest();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {        
        // cleanup from test run and teardown
        //cleanupTest();
        pageManager = null;
        pageLayout = null;
        userSubject = null;
        super.tearDown();
    }
    
    /**
     * Cleanup test artifacts.
     */
    protected void cleanupTest() throws Exception
    {
        // cleanup test artifacts
        if (pageManager.pageExists("/newpage.psml"))
        {
            pageManager.removePage(pageManager.getPage("/newpage.psml"));
        }
        if (pageManager.dynamicPageExists("/newcontentpage.dpsml"))
        {
            pageManager.removeDynamicPage(pageManager.getDynamicPage("/newcontentpage.dpsml"));
        }
        if (pageManager.pageTemplateExists("/newtemplate.tpsml"))
        {
            pageManager.removePageTemplate(pageManager.getPageTemplate("/newtemplate.tpsml"));
        }
        if (pageManager.fragmentDefinitionExists("/newdefinition0.fpsml"))
        {
            pageManager.removeFragmentDefinition(pageManager.getFragmentDefinition("/newdefinition0.fpsml"));
        }
        if (pageManager.fragmentDefinitionExists("/newdefinition1.fpsml"))
        {
            pageManager.removeFragmentDefinition(pageManager.getFragmentDefinition("/newdefinition1.fpsml"));
        }        
    }

    /**
     * Test PageManager test configuration
     *
     * @throws Exception
     */
    public void testPageManagerSetup() throws Exception
    {
        // validate test setup
        assertNotNull(pageManager);
        Folder folder = pageManager.getFolder("/");
        assertNotNull(folder);
        Page page = pageManager.getPage("/page.psml");
        assertNotNull(page);
        DynamicPage docPage = pageManager.getDynamicPage("/docpage.dpsml");        
        assertNotNull(docPage);
        DynamicPage contentPage = pageManager.getDynamicPage("/contentpage.dpsml");        
        assertNotNull(contentPage);
        FragmentDefinition definition0 = pageManager.getFragmentDefinition("/definition0.fpsml");        
        assertNotNull(definition0);
        FragmentDefinition definition1 = pageManager.getFragmentDefinition("/definition1.fpsml");        
        assertNotNull(definition1);
        PageTemplate template = pageManager.getPageTemplate("/template.tpsml");        
        assertNotNull(template);
    }

    /**
     * Test PageLayout component
     *
     * @throws Exception
     */
    public void testPageLayoutComponent() throws Exception
    {
        assertNotNull(pageManager);
        assertNotNull(pageLayout);

        // load definitions and templates
        Map fragmentDefinitions = new HashMap();
        FragmentDefinition definition0 = pageManager.getFragmentDefinition("/definition0.fpsml");        
        assertNotNull(definition0);
        assertEquals("fake-fragment-definition-0", definition0.getDefId());
        fragmentDefinitions.put(definition0.getDefId(), definition0);
        FragmentDefinition definition1 = pageManager.getFragmentDefinition("/definition1.fpsml");        
        assertNotNull(definition1);
        fragmentDefinitions.put(definition1.getDefId(), definition1);
        assertEquals("fake-fragment-definition-1", definition1.getDefId());
        PageTemplate template = pageManager.getPageTemplate("/template.tpsml");        
        assertNotNull(template);

        // create and validate content page for concrete page
        Page page = pageManager.getPage("/page.psml");
        assertNotNull(page);
        ContentPage pageContentPage = pageLayout.newContentPage(page, template, fragmentDefinitions);
        assertNotNull(pageContentPage);
        assertEquals("/page.psml", pageContentPage.getTitle());
        ContentFragment pageContentFragment0 = pageContentPage.getFragmentByFragmentId("fake-template");
        assertNotNull(pageContentFragment0);
        assertEquals("fake-template", pageContentFragment0.getId());
        ContentFragment pageContentFragment1 = pageContentPage.getFragmentByFragmentId("fake-fragment-definition-0");
        assertNotNull(pageContentFragment1);
        assertEquals("fake-template__fake-fragment-reference__fake-fragment-definition-0", pageContentFragment1.getId());
        assertEquals("fake-fragment-definition-0", pageContentFragment1.getRefId());
        ContentFragment pageContentFragment2 = pageContentPage.getFragmentByFragmentId("fake");
        assertNotNull(pageContentFragment2);
        assertEquals("fake-template__fake-page-fragment__fake", pageContentFragment2.getId());
        ContentFragment pageContentFragment3 = pageContentPage.getFragmentByFragmentId("fake-fragment-definition-1");
        assertNotNull(pageContentFragment3);
        assertEquals("fake-template__fake-page-fragment__fake__fake-fragment-reference__fake-fragment-definition-1", pageContentFragment3.getId());
        assertEquals("fake-fragment-definition-1", pageContentFragment3.getRefId());
        ContentFragment pageContentFragment4 = pageContentPage.getFragmentByFragmentId("fake-portlet");
        assertNotNull(pageContentFragment4);
        assertEquals("fake-template__fake-page-fragment__fake__fake-portlet", pageContentFragment4.getId());
        
        // create and validate content pages for concrete dynamic pages
        DynamicPage docPage = pageManager.getDynamicPage("/docpage.dpsml");        
        assertNotNull(docPage);
        ContentPage docPageContentPage = pageLayout.newContentPage(docPage, template, fragmentDefinitions);
        assertNotNull(docPageContentPage);
        assertEquals("/docpage.dpsml", docPageContentPage.getTitle());
        assertEquals("doc-type", docPageContentPage.getContentType());
        assertTrue(docPageContentPage.isInheritable());
        pageContentFragment0 = docPageContentPage.getFragmentByFragmentId("fake-template");
        assertNotNull(pageContentFragment0);
        assertEquals("fake-template", pageContentFragment0.getId());
        pageContentFragment1 = docPageContentPage.getFragmentByFragmentId("fake-fragment-definition-0");
        assertNotNull(pageContentFragment1);
        assertEquals("fake-template__fake-fragment-reference__fake-fragment-definition-0", pageContentFragment1.getId());
        assertEquals("fake-fragment-definition-0", pageContentFragment1.getRefId());
        pageContentFragment2 = docPageContentPage.getFragmentByFragmentId("fake");
        assertNotNull(pageContentFragment2);
        assertEquals("fake-template__fake-page-fragment__fake", pageContentFragment2.getId());
        DynamicPage contentPage = pageManager.getDynamicPage("/contentpage.dpsml");        
        assertNotNull(contentPage);
        ContentPage contentPageContentPage = pageLayout.newContentPage(contentPage, template, fragmentDefinitions);
        assertNotNull(contentPageContentPage);
        assertEquals("/contentpage.dpsml", contentPageContentPage.getTitle());
        assertEquals("*", contentPageContentPage.getContentType());
        assertTrue(contentPageContentPage.isInheritable());
        pageContentFragment0 = contentPageContentPage.getFragmentByFragmentId("fake-template");
        assertNotNull(pageContentFragment0);
        assertEquals("fake-template", pageContentFragment0.getId());
        pageContentFragment1 = contentPageContentPage.getFragmentByFragmentId("fake-fragment-definition-0");
        assertNotNull(pageContentFragment1);
        assertEquals("fake-template__fake-fragment-reference__fake-fragment-definition-0", pageContentFragment1.getId());
        assertEquals("fake-fragment-definition-0", pageContentFragment1.getRefId());
        pageContentFragment2 = contentPageContentPage.getFragmentByFragmentId("fake");
        assertNotNull(pageContentFragment2);
        assertEquals("fake-template__fake-page-fragment__fake", pageContentFragment2.getId());

        // create and validate content page for page template
        ContentPage templateContentPage = pageLayout.newContentPage(template, template, fragmentDefinitions);        
        assertNotNull(templateContentPage);
        assertEquals("/template.tpsml", templateContentPage.getTitle());
        ContentFragment templateContentFragment0 = templateContentPage.getFragmentByFragmentId("fake-template", true);
        assertNotNull(templateContentFragment0);
        assertEquals("fake-template__fake-page-fragment__fake-template", templateContentFragment0.getId());
        ContentFragment templateContentFragment1 = templateContentPage.getFragmentByFragmentId("fake-page-fragment", true);
        assertNotNull(templateContentFragment1);
        assertEquals("fake-template__fake-page-fragment__fake-template__fake-page-fragment", templateContentFragment1.getId());
        ContentFragment templateContentFragment2 = templateContentPage.getFragmentByFragmentId("fake-fragment-definition-0", true);
        assertNotNull(templateContentFragment2);
        assertEquals("fake-template__fake-page-fragment__fake-template__fake-fragment-reference__fake-fragment-definition-0", templateContentFragment2.getId());
        assertEquals("fake-fragment-definition-0", templateContentFragment2.getRefId());

        // create and validate content page for page template
        ContentPage fragmentDefinitionContentPage = pageLayout.newContentPage(definition0, template, fragmentDefinitions);        
        assertNotNull(fragmentDefinitionContentPage);
        assertEquals("/definition0.fpsml", fragmentDefinitionContentPage.getTitle());
        ContentFragment fragmentDefinitionContentFragment0 = fragmentDefinitionContentPage.getFragmentByFragmentId("fake-fragment-definition-0");
        assertNotNull(fragmentDefinitionContentFragment0);
        assertEquals("fake-template__fake-page-fragment__fake-fragment-definition-0", fragmentDefinitionContentFragment0.getId());
        
        // create new PSML pages, content pages, templates, and fragment definitions
        fragmentDefinitionContentPage.newSiblingFragmentDefinition("newdefinition1", null, "new-fake-fragment-definition-1", "/newdefinition1.fpsml", "/newdefinition1.fpsml");
        fragmentDefinitionContentPage.newSiblingFragmentDefinition("newdefinition0", "new-fake-fragment-definition-0", "new-fake-fragment-definition-0", "/newdefinition0.fpsml", "/newdefinition0.fpsml");
        templateContentPage.newSiblingPageTemplate("newtemplate", "new-fake-template", "/newtemplate.tpsml", "/newtemplate.tpsml");
        contentPageContentPage.newSiblingDynamicPage("newcontentpage", "*", "new-fake", "/newcontentpage.dpsml", "/newcontentpage.dpsml");
        pageContentPage.newSiblingPage("newpage", "new-fake", "/newpage.psml", "/newpage.psml");        
        Map newFragmentDefinitions = new HashMap();
        FragmentDefinition newDefinition1 = pageManager.getFragmentDefinition("/newdefinition1.fpsml");
        assertNotNull(newDefinition1);
        String newDefinition1Id = newDefinition1.getRootFragment().getId();
        newFragmentDefinitions.put(newDefinition1Id, newDefinition1);
        FragmentDefinition newDefinition0 = pageManager.getFragmentDefinition("/newdefinition0.fpsml");
        assertNotNull(newDefinition0);
        assertEquals("new-fake-fragment-definition-0", newDefinition0.getRootFragment().getId());
        newFragmentDefinitions.put("new-fake-fragment-definition-0", newDefinition0);
        PageTemplate newTemplate = pageManager.getPageTemplate("/newtemplate.tpsml");
        assertNotNull(newTemplate);
        DynamicPage newContentPage = pageManager.getDynamicPage("/newcontentpage.dpsml");
        assertNotNull(newContentPage);
        assertEquals("*", newContentPage.getContentType());
        assertTrue(newContentPage.isInheritable());
        Page newPage = pageManager.getPage("/newpage.psml");
        assertNotNull(newPage);

        // create and validate new content pages
        ContentPage newFragmentDefinition1ContentPage = pageLayout.newContentPage(newDefinition1, newTemplate, newFragmentDefinitions);
        assertNotNull(newFragmentDefinition1ContentPage);
        List contentFragments = newFragmentDefinition1ContentPage.getFragmentsByName("new-fake-fragment-definition-1");
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        assertEquals(newDefinition1Id, newDefinition1.getDefId());        
        ContentPage newFragmentDefinition0ContentPage = pageLayout.newContentPage(newDefinition0, newTemplate, newFragmentDefinitions);
        assertNotNull(newFragmentDefinition0ContentPage);
        contentFragments = newFragmentDefinition0ContentPage.getFragmentsByName("new-fake-fragment-definition-0");
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        assertEquals("new-fake-fragment-definition-0", ((ContentFragment)contentFragments.get(0)).getFragmentId());
        assertEquals("new-fake-fragment-definition-0", newDefinition0.getDefId());        
        ContentPage newTemplateContentPage = pageLayout.newContentPage(newTemplate, newTemplate, newFragmentDefinitions);
        assertNotNull(newTemplateContentPage);
        newTemplateContentPage.addFragmentReference("new-fake-fragment-definition-0");
        newTemplateContentPage = pageLayout.newContentPage(newTemplate, newTemplate, newFragmentDefinitions);        
        ContentPage newContentPageContentPage = pageLayout.newContentPage(newContentPage, newTemplate, newFragmentDefinitions);
        assertNotNull(newContentPageContentPage);
        ContentPage newPageContentPage = pageLayout.newContentPage(newPage, newTemplate, newFragmentDefinitions);
        assertNotNull(newPageContentPage);
        contentFragments = newPageContentPage.getFragmentsByName("new-fake");
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        ContentFragment newPageRootFragment = (ContentFragment)contentFragments.get(0);
        pageLayout.addFragmentReference(newPageRootFragment, newDefinition1Id, 0, 0);
        pageLayout.addPortlet(newPageRootFragment, Fragment.PORTLET, "new-fake-portlet", 1, 0);
        pageLayout.addFragmentReference(newPageRootFragment, "nonexistent-reference-id", 2, 0);
        newPageContentPage = pageLayout.newContentPage(newPage, newTemplate, newFragmentDefinitions);
        assertNotNull(newPageContentPage);
        
        // validate new PSML pages, content pages, templates, and fragment definitions
        assertEquals("/newpage.psml", newPageContentPage.getTitle());
        contentFragments = newPageContentPage.getFragmentsByName("new-fake-template");
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        contentFragments = newPageContentPage.getFragmentsByName("new-fake-fragment-definition-0");
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        assertEquals("new-fake-fragment-definition-0", ((ContentFragment)contentFragments.get(0)).getRefId());
        contentFragments = newPageContentPage.getFragmentsByName("new-fake");
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        contentFragments = ((ContentFragment)contentFragments.get(0)).getFragments();
        assertNotNull(contentFragments);
        assertEquals(3, contentFragments.size());
        ContentFragment newPageReferenceContentFragment = null;
        Iterator contentFragmentsIter = contentFragments.iterator();
        while (contentFragmentsIter.hasNext())
        {
            ContentFragment contentFragment = (ContentFragment)contentFragmentsIter.next();
            if (contentFragment.getType().equals(ContentFragment.PORTLET) && contentFragment.getName().equals("new-fake-portlet"))
            {
            }
            else if (contentFragment.getType().equals(ContentFragment.PORTLET) && contentFragment.getName().equals("new-fake-fragment-definition-1"))
            {
                assertEquals(newDefinition1Id, contentFragment.getRefId());
            }
            else if (contentFragment.getType().equals(ContentFragment.REFERENCE))
            {
                assertEquals("nonexistent-reference-id", contentFragment.getRefId());
                newPageReferenceContentFragment = contentFragment;
            }
            else
            {
                fail("Unexpected content fragment: "+contentFragment.getType()+"/"+contentFragment.getName());
            }
        }
        assertEquals("/newcontentpage.dpsml", newContentPageContentPage.getTitle());
        assertEquals("*", newContentPageContentPage.getContentType());
        assertTrue(newContentPageContentPage.isInheritable());
        contentFragments = newContentPageContentPage.getFragmentsByName("new-fake-template");
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        contentFragments = newContentPageContentPage.getFragmentsByName("new-fake-fragment-definition-0");
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        assertEquals("new-fake-fragment-definition-0", ((ContentFragment)contentFragments.get(0)).getRefId());
        contentFragments = newContentPageContentPage.getFragmentsByName("new-fake");
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        assertEquals("/newtemplate.tpsml", newTemplateContentPage.getTitle());
        contentFragments = newTemplateContentPage.getFragmentsByName("new-fake-template", true);
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        contentFragments = ((ContentFragment)contentFragments.get(0)).getFragments();
        assertNotNull(contentFragments);
        assertEquals(2, contentFragments.size());
        contentFragmentsIter = contentFragments.iterator();
        while (contentFragmentsIter.hasNext())
        {
            ContentFragment contentFragment = (ContentFragment)contentFragmentsIter.next();
            if (contentFragment.getType().equals(ContentFragment.PAGE))
            {
            }
            else if (contentFragment.getType().equals(ContentFragment.PORTLET) && contentFragment.getName().equals("new-fake-fragment-definition-0"))
            {
                assertEquals("new-fake-fragment-definition-0", contentFragment.getRefId());
            }
            else
            {
                fail("Unexpected content fragment: "+contentFragment.getType()+"/"+contentFragment.getName());
            }
        }
        assertEquals("/newdefinition0.fpsml", newFragmentDefinition0ContentPage.getTitle());
        contentFragments = newFragmentDefinition0ContentPage.getFragmentsByName("new-fake-fragment-definition-0");
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        assertEquals("new-fake-fragment-definition-0", ((ContentFragment)contentFragments.get(0)).getFragmentId());        
        assertEquals("new-fake-fragment-definition-0", newFragmentDefinition0ContentPage.getDefId());        
        assertEquals("/newdefinition1.fpsml", newFragmentDefinition1ContentPage.getTitle());
        contentFragments = newFragmentDefinition1ContentPage.getFragmentsByName("new-fake-fragment-definition-1");
        assertNotNull(contentFragments);
        assertEquals(1, contentFragments.size());
        assertEquals(newDefinition1Id, ((ContentFragment)contentFragments.get(0)).getFragmentId());        
        assertEquals(newDefinition1Id, newFragmentDefinition1ContentPage.getDefId());        

        // update pages via content pages and fragments
        newFragmentDefinition1ContentPage.updateTitles(null, "new-fragment-definition-1-updated");
        ContentFragment updateContentFragment = (ContentFragment)(newFragmentDefinition1ContentPage.getFragmentsByName("new-fake-fragment-definition-1").get(0));
        updateContentFragment.updateProperty("global-prop", "global-fragment-definition");
        updateContentFragment.updateProperty("prop", "global-fragment-definition");
        updateContentFragment.updateProperty("prop", "user-fragment-definition", ContentFragment.USER_PROPERTY_SCOPE, "user");
        updateContentFragment.updateProperty("user-prop", "user-fragment-definition", ContentFragment.USER_PROPERTY_SCOPE, "user");
        updateContentFragment.updateProperty("global-fragment-definition-prop", "global-fragment-definition");
        updateContentFragment.updateProperty("fragment-definition-prop", "global-fragment-definition");
        updateContentFragment.updateProperty("fragment-definition-prop", "user-fragment-definition", ContentFragment.USER_PROPERTY_SCOPE, "user");
        updateContentFragment.updateProperty("user-fragment-definition-prop", "user-fragment-definition", ContentFragment.USER_PROPERTY_SCOPE, "user");
        newTemplateContentPage.updateTitles(null, "new-page-template-updated");
        updateContentFragment = (ContentFragment)(newTemplateContentPage.getFragmentsByName("new-fake-template", true).get(0));
        updateContentFragment.updateProperty("global-prop", "global-template");
        updateContentFragment.updateProperty("prop", "global-template");
        updateContentFragment.updateProperty("prop", "user-template", ContentFragment.USER_PROPERTY_SCOPE, "user");
        updateContentFragment.updateProperty("user-prop", "user-template", ContentFragment.USER_PROPERTY_SCOPE, "user");        
        updateContentFragment = (ContentFragment)(newTemplateContentPage.getFragmentsByName("new-fake-fragment-definition-0", true).get(0));
        updateContentFragment.updateProperty("global-prop", "global-template");
        updateContentFragment.updateProperty("prop", "global-template");
        updateContentFragment.updateProperty("prop", "user-template", ContentFragment.USER_PROPERTY_SCOPE, "user");
        updateContentFragment.updateProperty("user-prop", "user-template", ContentFragment.USER_PROPERTY_SCOPE, "user");
        newPageContentPage.updateTitles(null, "new-page-updated");
        updateContentFragment = newPageContentPage.getRootFragment();
        updateContentFragment.updateProperty("user-prop", "user-page", ContentFragment.USER_PROPERTY_SCOPE, "user");        
        updateContentFragment = (ContentFragment)(newPageContentPage.getFragmentsByName("new-fake").get(0));
        updateContentFragment.updateProperty("global-prop", "global-page");
        updateContentFragment.updateProperty("prop", "global-page");
        updateContentFragment.updateProperty("prop", "user-page", ContentFragment.USER_PROPERTY_SCOPE, "user");
        updateContentFragment.updateProperty("user-prop", "user-page", ContentFragment.USER_PROPERTY_SCOPE, "user");
        updateContentFragment = (ContentFragment)(newPageContentPage.getFragmentsByName("new-fake-fragment-definition-0").get(0));
        updateContentFragment.updateProperty("user-prop", "user-page", ContentFragment.USER_PROPERTY_SCOPE, "user");        
        updateContentFragment = (ContentFragment)(newPageContentPage.getFragmentsByName("new-fake-fragment-definition-1").get(0));
        updateContentFragment.updateProperty("global-prop", "global-page");
        updateContentFragment.updateProperty("prop", "global-page");
        updateContentFragment.updateProperty("prop", "user-page", ContentFragment.USER_PROPERTY_SCOPE, "user");
        updateContentFragment.updateProperty("user-prop", "user-page", ContentFragment.USER_PROPERTY_SCOPE, "user");
        newPageReferenceContentFragment.updateRefId("fake-fragment-definition-0");
        newContentPageContentPage.updateTitles(null, "new-content-page-updated");
        newContentPageContentPage.updateContent("new-doc-type", false);
        
        // continue test case as user
        Exception exception = (Exception)JSSubject.doAsPrivileged(userSubject, new PrivilegedAction()
        {
            public Object run()
            {
                try
                {
                    // reload PSML pages, content pages, templates, and fragment definitions
                    Map newFragmentDefinitions = new HashMap();
                    FragmentDefinition definition0 = pageManager.getFragmentDefinition("/definition0.fpsml");
                    assertNotNull(definition0);
                    newFragmentDefinitions.put(definition0.getDefId(), definition0);
                    FragmentDefinition newDefinition1 = pageManager.getFragmentDefinition("/newdefinition1.fpsml");
                    assertNotNull(newDefinition1);
                    newFragmentDefinitions.put(newDefinition1.getDefId(), newDefinition1);
                    FragmentDefinition newDefinition0 = pageManager.getFragmentDefinition("/newdefinition0.fpsml");
                    assertNotNull(newDefinition0);
                    newFragmentDefinitions.put(newDefinition0.getDefId(), newDefinition0);
                    PageTemplate newTemplate = pageManager.getPageTemplate("/newtemplate.tpsml");
                    assertNotNull(newTemplate);
                    Page newPage = pageManager.getPage("/newpage.psml");
                    assertNotNull(newPage);
                    DynamicPage newContentPage = pageManager.getDynamicPage("/newcontentpage.dpsml");
                    assertNotNull(newContentPage);
                    
                    // verify content page and fragment updates
                    ContentPage newFragmentDefinition1ContentPage = pageLayout.newContentPage(newDefinition1, newTemplate, newFragmentDefinitions);
                    assertNotNull(newFragmentDefinition1ContentPage);
                    assertEquals("new-fragment-definition-1-updated", newFragmentDefinition1ContentPage.getShortTitle());
                    ContentFragment updatedContentFragment = (ContentFragment)(newFragmentDefinition1ContentPage.getFragmentsByName("new-fake-fragment-definition-1").get(0));
                    assertEquals("global-fragment-definition", updatedContentFragment.getProperty("global-prop"));
                    assertEquals("user-fragment-definition", updatedContentFragment.getProperty("prop"));
                    assertEquals("user-fragment-definition", updatedContentFragment.getProperty("user-prop"));
                    assertEquals("global-fragment-definition", updatedContentFragment.getProperty("global-fragment-definition-prop"));
                    assertEquals("user-fragment-definition", updatedContentFragment.getProperty("fragment-definition-prop"));
                    assertEquals("user-fragment-definition", updatedContentFragment.getProperty("user-fragment-definition-prop"));
                    ContentPage newFragmentDefinition0ContentPage = pageLayout.newContentPage(newDefinition0, newTemplate, newFragmentDefinitions);
                    assertNotNull(newFragmentDefinition0ContentPage);
                    ContentPage newTemplateContentPage = pageLayout.newContentPage(newTemplate, newTemplate, newFragmentDefinitions);
                    assertNotNull(newTemplateContentPage);
                    assertEquals("new-page-template-updated", newTemplateContentPage.getShortTitle());
                    updatedContentFragment = newTemplateContentPage.getRootFragment();
                    assertEquals("global-template", updatedContentFragment.getProperty("global-prop"));
                    assertEquals("user-template", updatedContentFragment.getProperty("prop"));
                    assertEquals("user-page", updatedContentFragment.getProperty("user-prop"));
                    updatedContentFragment = (ContentFragment)(newTemplateContentPage.getFragmentsByName("new-fake-template", true).get(0));
                    assertEquals("global-template", updatedContentFragment.getProperty("global-prop"));
                    assertEquals("user-template", updatedContentFragment.getProperty("prop"));
                    assertEquals("user-page", updatedContentFragment.getProperty("user-prop"));
                    updatedContentFragment = (ContentFragment)(newTemplateContentPage.getFragmentsByName("new-fake-fragment-definition-0", true).get(0));
                    assertEquals("global-template", updatedContentFragment.getProperty("global-prop"));
                    assertEquals("user-template", updatedContentFragment.getProperty("prop"));
                    assertEquals("user-page", updatedContentFragment.getProperty("user-prop"));
                    ContentPage newPageContentPage = pageLayout.newContentPage(newPage, newTemplate, newFragmentDefinitions);
                    assertNotNull(newPageContentPage);
                    assertEquals("new-page-updated", newPageContentPage.getShortTitle());
                    updatedContentFragment = newPageContentPage.getRootFragment();
                    assertEquals("global-template", updatedContentFragment.getProperty("global-prop"));
                    assertEquals("user-template", updatedContentFragment.getProperty("prop"));
                    assertEquals("user-page", updatedContentFragment.getProperty("user-prop"));
                    updatedContentFragment = (ContentFragment)(newPageContentPage.getFragmentsByName("new-fake").get(0));
                    assertEquals("global-page", updatedContentFragment.getProperty("global-prop"));
                    assertEquals("user-page", updatedContentFragment.getProperty("prop"));
                    assertEquals("user-page", updatedContentFragment.getProperty("user-prop"));
                    updatedContentFragment = (ContentFragment)(newPageContentPage.getFragmentsByName("new-fake-fragment-definition-0").get(0));
                    assertEquals("global-template", updatedContentFragment.getProperty("global-prop"));
                    assertEquals("user-template", updatedContentFragment.getProperty("prop"));
                    assertEquals("user-page", updatedContentFragment.getProperty("user-prop"));
                    updatedContentFragment = (ContentFragment)(newPageContentPage.getFragmentsByName("new-fake-fragment-definition-1").get(0));
                    assertEquals("global-page", updatedContentFragment.getProperty("global-prop"));
                    assertEquals("user-page", updatedContentFragment.getProperty("prop"));
                    assertEquals("user-page", updatedContentFragment.getProperty("user-prop"));
                    assertEquals("global-fragment-definition", updatedContentFragment.getProperty("global-fragment-definition-prop"));
                    assertEquals("user-fragment-definition", updatedContentFragment.getProperty("fragment-definition-prop"));
                    assertEquals("user-fragment-definition", updatedContentFragment.getProperty("user-fragment-definition-prop"));
                    List contentFragments = newPageContentPage.getFragmentsByName("new-fake");
                    assertNotNull(contentFragments);
                    assertEquals(1, contentFragments.size());
                    contentFragments = ((ContentFragment)contentFragments.get(0)).getFragments();
                    assertNotNull(contentFragments);
                    assertEquals(3, contentFragments.size());
                    Iterator contentFragmentsIter = contentFragments.iterator();
                    while (contentFragmentsIter.hasNext())
                    {
                        ContentFragment contentFragment = (ContentFragment)contentFragmentsIter.next();
                        if (contentFragment.getType().equals(ContentFragment.PORTLET) && contentFragment.getName().equals("new-fake-portlet"))
                        {
                        }
                        else if (contentFragment.getType().equals(ContentFragment.PORTLET) && contentFragment.getName().equals("new-fake-fragment-definition-1"))
                        {
                            assertEquals(newDefinition1.getDefId(), contentFragment.getRefId());
                        }
                        else if (contentFragment.getType().equals(ContentFragment.PORTLET) && contentFragment.getName().equals("fake-fragment-definition-0"))
                        {
                            assertEquals("fake-fragment-definition-0", contentFragment.getRefId());
                        }
                        else
                        {
                            fail("Unexpected content fragment: "+contentFragment.getType()+"/"+contentFragment.getName());
                        }
                    }
                    ContentPage newContentPageContentPage = pageLayout.newContentPage(newContentPage, newTemplate, newFragmentDefinitions);
                    assertNotNull(newContentPageContentPage);
                    assertEquals("new-content-page-updated", newContentPageContentPage.getShortTitle());
                    assertEquals("new-doc-type", newContentPageContentPage.getContentType());
                    assertFalse(newContentPageContentPage.isInheritable());

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
        if (exception != null)
        {
            throw exception;
        }
    }
    
    public void testUpdateSecurityConstraints() throws Exception {
        assertNotNull(pageManager);
        assertNotNull(pageLayout);

        Page page = pageManager.getPage("/page.psml");
        assertNotNull(page);
        ContentPage pageContentPage = pageLayout.newContentPage(page, pageManager.getPageTemplate("/template.tpsml"), Collections.<String, FragmentDefinition>emptyMap());
        assertNotNull(pageContentPage);
        ContentFragment contentFrag = pageContentPage.getFragmentByFragmentId("fake-portlet");
        assertNotNull(contentFrag);
        
        pageLayout.updateSecurityConstraints(contentFrag, null);
        assertNull(contentFrag.getSecurityConstraints());
    }

    public static abstract class AbstractTestPrincipal extends TransientJetspeedPrincipal
    {
        private static final SecurityAttributeTypes attributeTypes = new SecurityAttributeTypes()
        {

            public Map<String, SecurityAttributeType> getAttributeTypeMap()
            {
                return Collections.emptyMap();
            }

            public Map<String, SecurityAttributeType> getAttributeTypeMap(String category)
            {
                return Collections.emptyMap();
            }

            public boolean isExtendable()
            {
                return false;
            }

            public boolean isReadOnly()
            {
                return true;
            }
        };
        
        private JetspeedPrincipalType type;
        
        private static final long serialVersionUID = 1L;
        

        public AbstractTestPrincipal(final String type, String name)
        {
            super(type, name);
            this.type = new JetspeedPrincipalType()
            {               
                public SecurityAttributeTypes getAttributeTypes()
                {
                    return attributeTypes;
                }

                public String getClassName()
                {
                    return null;
                }

                public String getName()
                {
                    return type;
                }

                public Class<JetspeedPrincipal> getPrincipalClass()
                {
                    return null;
                }
            };
        }

        public synchronized JetspeedPrincipalType getType()
        {
            return type;
        }
    }
    
    public static class TestUser extends AbstractTestPrincipal implements User
    {
        private static final long serialVersionUID = 1L;

        public TestUser(String name)
        {
            super(JetspeedPrincipalType.USER, name);
        }
    }
}
