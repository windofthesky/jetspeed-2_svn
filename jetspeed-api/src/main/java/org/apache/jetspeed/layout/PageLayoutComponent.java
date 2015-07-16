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

import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.FragmentDefinition;
import org.apache.jetspeed.om.page.PageTemplate;

import java.util.Map;

/**
 * Page layout component interface.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public interface PageLayoutComponent
{
    static final String CONTENT_FRAGMENT_ID_SEPARATOR = "__"; // javascript identifier and css selector safe separator
    
    /**
     * user standard property scope
     */
    String USER_PROPERTY_SCOPE = ContentFragment.USER_PROPERTY_SCOPE;

    /**
     * group standard property scope
     */
    String GROUP_PROPERTY_SCOPE = ContentFragment.GROUP_PROPERTY_SCOPE;

    /**
     * role standard property scope
     */
    String ROLE_PROPERTY_SCOPE = ContentFragment.ROLE_PROPERTY_SCOPE;

    /**
     * global standard property scope
     */
    String GLOBAL_PROPERTY_SCOPE = ContentFragment.GLOBAL_PROPERTY_SCOPE;

    /**
     * group and role standard property scopes enabled flag
     */
    boolean GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED = ContentFragment.GROUP_AND_ROLE_PROPERTY_SCOPES_ENABLED;

    /**
     * Add fragment to page at specified row column layout position
     * returning associated content fragment. Fragment is always added to
     * the page root layout fragment.
     * 
     * @param contentPage content page context
     * @param contentFragment externally constructed fragment to add
     * @param row fragment row position
     * @param column fragment column position
     * @return content fragment added to page
     */
    ContentFragment addFragmentAtRowColumn(ContentPage contentPage, ContentFragment contentFragment, int row, int column);                        
    
    /**
     * Add fragment reference to fragment with specified row and
     * column returning associated content fragment.
     * 
     * @param contentFragment content fragment context
     * @param id fragment definition id
     * @param row fragment row position
     * @param column fragment column position
     * @return new content fragment added to fragment
     */
    ContentFragment addFragmentReference(ContentFragment contentFragment, String id, int row, int column);

    /**
     * Add fragment reference to fragment returning associated
     * content fragment.
     * 
     * @param contentFragment content fragment context
     * @param id fragment definition id
     * @return new content fragment added to fragment
     */
    ContentFragment addFragmentReference(ContentFragment contentFragment, String id);
    
    /**
     * Add fragment reference to page returning associated content
     * fragment.
     * 
     * @param contentPage content page context
     * @param id fragment definition id
     * @return new content fragment added to page
     */
    ContentFragment addFragmentReference(ContentPage contentPage, String id);

    /**
     * Add portlet to fragment with specified row and column returning
     * associated content fragment.
     * 
     * @param contentFragment content fragment context
     * @param type portlet type
     * @param name portlet name
     * @param row fragment row position
     * @param column fragment column position
     * @return new content fragment added to fragment
     */
    ContentFragment addPortlet(ContentFragment contentFragment, String type, String name, int row, int column);

    /**
     * Add portlet to fragment returning associated content fragment.
     * 
     * @param contentFragment content fragment context
     * @param type portlet type
     * @param name portlet name
     * @return new content fragment added to fragment
     */
    ContentFragment addPortlet(ContentFragment contentFragment, String type, String name);
    
    /**
     * Add portlet to page returning associated content fragment.
     * 
     * @param contentPage content page context
     * @param type portlet type
     * @param name portlet name
     * @return new content fragment added to page
     */
    ContentFragment addPortlet(ContentPage contentPage, String type, String name);

    /**
     * Decrement position of folder in parent folder document order.
     * 
     * @param contentPage content page context
     */
    void decrementFolderInDocumentOrder(ContentPage contentPage);

    /**
     * Decrement position of page in folder document order.
     * 
     * @param contentPage content page context
     */
    void decrementInDocumentOrder(ContentPage contentPage);

    /**
     * Increment position of folder in parent folder document order.
     * 
     * @param contentPage content page context
     */
    void incrementFolderInDocumentOrder(ContentPage contentPage);

    /**
     * Increment position of page in folder document order.
     * 
     * @param contentPage content page context
     */
    void incrementInDocumentOrder(ContentPage contentPage);

    /**
     * Move fragment in page to another layout fragment in the same page.
     *
     * @param contentPage content page context
     * @param fragmentId fragment id of fragment to move
     * @param toFragmentId fragment id of new parent layout fragment
     */
    void moveFragment(ContentPage contentPage, String fragmentId, String toFragmentId);

    /**
     * Move fragment from current parent layout fragment to another
     * layout fragment in the same page.
     *
     * @param contentPage content page context
     * @param fragmentId fragment id of fragment to move
     * @param fromFragmentId fragment id of current parent layout fragment or
     *                       or null if fragment to be found anywhere in page
     * @param toFragmentId fragment id of new parent layout fragment
     */
    void moveFragment(ContentPage contentPage, String fragmentId, String fromFragmentId, String toFragmentId);

    /**
     * Construct a new content page hierarchy from PSML page or template,
     * page templates, and fragment definitions.
     * 
     * @param pageOrTemplate PSML page or template to construct content page from
     * @param pageTemplate PSML page template to merge into content page
     * @param fragmentDefinitions PSML fragment definitions referenced
     *                            by page and/or page template
     * @return new content page
     */
    ContentPage newContentPage(BaseFragmentsElement pageOrTemplate, PageTemplate pageTemplate, Map<String, FragmentDefinition> fragmentDefinitions);
    
    /**
     * Create a new sibling folder with specified configuration and
     * new default page. Also, adds folder to end of page folder
     * document order list. The default page is added to the new
     * folder document order list. Both title and short title
     * parameters default to page name if not specified. The layout
     * fragment name for the default page is cloned from this content
     * page if not specified. Default decorators are cloned from this
     * content page.
     *  
     * @param contentPage content page context
     * @param folderName unique new folder name, (also used as
     *                   default page title)
     * @param folderTitle new folder title or null
     * @param folderShortTitle new folder short title or null
     * @param defaultPageLayoutName root level layout fragment name
     *                              for default page
     */
    void newSiblingFolder(ContentPage contentPage, String folderName, String folderTitle, String folderShortTitle, String defaultPageLayoutName);
    
    /**
     * Create new sibling page with specified configuration and add
     * new page at end of folder document order list. Both title and
     * short title parameters default to page name if not specified.
     * The layout fragment name is cloned from this content page if
     * not specified. Default decorators are cloned from this content
     * page.
     * 
     * @param contentPage content page context
     * @param pageName unique new page name
     * @param layoutName root level layout fragment name or null 
     * @param pageTitle new page title or null
     * @param pageShortTitle new page short title or null
     */
    void newSiblingPage(ContentPage contentPage, String pageName, String layoutName, String pageTitle, String pageShortTitle);
    
    /**
     * Create new sibling dynamic page with specified configuration. 
     * Both title and short title parameters default to page name if
     * not specified. The layout fragment name is cloned from this
     * content page if not specified. Default decorators are cloned
     * from this content page.
     * 
     * @param contentPage content page context
     * @param pageName unique new dynamic page name
     * @param contentType dynamic page content type
     * @param layoutName root level layout fragment name or null 
     * @param pageTitle new page title or null
     * @param pageShortTitle new page short title or null
     */
    void newSiblingDynamicPage(ContentPage contentPage, String pageName, String contentType, String layoutName, String pageTitle, String pageShortTitle);
    
    /**
     * Create new sibling page template with specified configuration.
     * The layout fragment name is cloned from this content page if
     * not specified. Default decorators are cloned from this content
     * page.
     * 
     * @param contentPage content page context
     * @param templateName unique new page template name
     * @param layoutName root level layout fragment name or null 
     * @param templateTitle new page title or null
     * @param templateShortTitle new page short title or null
     */
    void newSiblingPageTemplate(ContentPage contentPage, String templateName, String layoutName, String templateTitle, String templateShortTitle);
    
    /**
     * Create new sibling fragment definition with specified
     * configuration.
     * 
     * @param contentPage content page context
     * @param definitionName unique new fragment definition name
     * @param defId unique new fragment definition id or null
     * @param portletName root level portlet fragment name or null 
     * @param definitionTitle new page title or null
     * @param definitionShortTitle new page short title or null
     */
    void newSiblingFragmentDefinition(ContentPage contentPage, String definitionName, String defId, String portletName, String definitionTitle, String definitionShortTitle);
    
    /**
     * Remove fragment from page by id.
     * 
     * @param contentPage content page context
     * @param fragmentId id of fragment to remove
     * @return flag indicating removed
     */
    void removeFragment(ContentPage contentPage, String fragmentId);
    
    /**
     * Remove page and remove from folder document order list.
     * 
     * @param contentPage content page context
     */
    void remove(ContentPage contentPage);
    
    /**
     * Remove folder and remove from parent folder document order list.
     *
     * @param contentPage content page context
     */
    void removeFolder(ContentPage contentPage);
    
    /**
     * Update dynamic page content type and inheritable flag.
     *  
     * @param contentPage content page context
     * @param contentType dynamic page content type
     * @param contentType dynamic page content type
     */
    void updateContent(ContentPage contentPage, String contentType, Boolean inheritable);

    /**
     * Update global fragment portlet decorator.
     *  
     * @param contentFragment content fragment context
     * @param decoratorName portlet decorator name
     */
    void updateDecorator(ContentFragment contentFragment, String decoratorName);

    /**
     * Update fragment portlet decorator.
     *  
     * @param contentFragment content fragment context
     * @param decoratorName portlet decorator name
     * @param scope property scope
     * @param scopeValue property scope value
     */
    void updateDecorator(ContentFragment contentFragment, String decoratorName, String scope, String scopeValue);

    /**
     * Update page default decorator.
     *  
     * @param contentPage content page context
     * @param decoratorName decorator name
     * @param fragmentType decorator fragment type
     */
    void updateDefaultDecorator(ContentPage contentPage, String decoratorName, String fragmentType);
    
    /**
     * Update folder titles.
     * 
     * @param contentPage content page context
     * @param title folder title
     * @param shortTitle folder short title
     */
    void updateFolderTitles(ContentPage contentPage, String title, String shortTitle);

    /**
     * Update fragment name.
     * 
     * @param contentFragment content fragment context
     * @param name fragment name
     */
    void updateName(ContentFragment contentFragment, String name);

    /**
     * Update global fragment layout position.
     * 
     * @param contentFragment content fragment context
     * @param x fragment X coordinate or -1
     * @param y fragment Y coordinate or -1
     * @param z fragment Z level or -1
     * @param width fragment portlet width or -1
     * @param height fragment portlet height or -1
     */
    void updatePosition(ContentFragment contentFragment, float x, float y, float z, float width, float height);
    
    /**
     * Update fragment layout position.
     * 
     * @param contentFragment content fragment context
     * @param x fragment X coordinate or -1
     * @param y fragment Y coordinate or -1
     * @param z fragment Z level or -1
     * @param width fragment portlet width or -1
     * @param height fragment portlet height or -1
     * @param scope properties scope
     * @param scopeValue properties scope value
     */
    void updatePosition(ContentFragment contentFragment, float x, float y, float z, float width, float height, String scope, String scopeValue);
    
    /**
     * Update preferences with new preferences set, accepting
     * Map of strings, string arrays, FragmentPreference or
     * PortletPreference. Existing preferences are removed and
     * replaced with the specified preferences.
     *
     * preferences values are normally {@link org.apache.pluto.container.PortletPreference} but can also be String, String[], or
     *      {@link org.apache.jetspeed.om.preference.FragmentPreference}
     *
     * @param contentFragment content fragment context
     * @param preferences map of new preferences set.
     */
    void updatePreferences(ContentFragment contentFragment, Map<String,?> preferences);

    /**
     * Update global fragment property.
     * 
     * @param contentFragment content fragment context
     * @param propName fragment property name
     * @param propValue fragment property value
     */
    void updateProperty(ContentFragment contentFragment, String propName, String propValue);
    
    /**
     * Update fragment property.
     * 
     * @param contentFragment content fragment context
     * @param propName fragment property name
     * @param propValue fragment property value
     * @param scope property scope 
     * @param scopeValue property scope value
     */
    void updateProperty(ContentFragment contentFragment, String propName, String propValue, String scope, String scopeValue);
    
    /**
     * Update fragment reference reference id.
     * 
     * @param contentFragment content fragment context
     * @param refId referenced fragment definition id
     */
    void updateRefId(ContentFragment contentFragment, String refId);
    
    /**
     * Update global fragment row and column layout position.
     * 
     * @param contentFragment content fragment context
     * @param row fragment row position
     * @param column fragment column position
     */
    void updateRowColumn(ContentFragment contentFragment, int row, int column);
    
    /**
     * Update fragment row and column layout position.
     * 
     * @param contentFragment content fragment context
     * @param row fragment row position
     * @param column fragment column position
     * @param scope properties scope
     * @param scopeValue properties scope value
     */
    void updateRowColumn(ContentFragment contentFragment, int row, int column, String scope, String scopeValue);

    /**
     * Update fragment security constraints.
     * 
     * @param contentFragment content fragment context
     * @param constraints security constraints
     */
    void updateSecurityConstraints(ContentFragment contentFragment, SecurityConstraints constraints);
    
    /**
     * Update global fragment portlet state and/or mode.
     * 
     * @param contentFragment content fragment context
     * @param portletState fragment portlet state or null
     * @param portletMode fragment portlet mode or null
     */
    void updateStateMode(ContentFragment contentFragment, String portletState, String portletMode);

    /**
     * Update fragment portlet state and/or mode.
     * 
     * @param contentFragment content fragment context
     * @param portletState fragment portlet state or null
     * @param portletMode fragment portlet mode or null
     * @param scope properties scope
     * @param scopeValue properties scope value
     */
    void updateStateMode(ContentFragment contentFragment, String portletState, String portletMode, String scope, String scopeValue);

    /**
     * Update page titles.
     * 
     * @param contentPage content page context
     * @param title page title
     * @param shortTitle page short title
     */
    void updateTitles(ContentPage contentPage, String title, String shortTitle);

    /**
     * Reorder portlet columns locations over maxColumns
     *
     * @param targetFragment
     * @param maxColumns
     */
    void reorderColumns(ContentFragment targetFragment, int maxColumns);

}
