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

import java.util.Map;

import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageTemplate;

/**
 * Page layout component interface.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public interface PageLayoutComponent
{
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
     * Construct a new content page hierarchy from PSML page, page
     * templates, and fragment definitions.
     * 
     * @param page PSML page to construct content page from
     * @param pageTemplate PSML page template to merge into content page
     * @param fragmentDefinitions PSML fragment definitions referenced
     *                            by page and/or page template
     * @return new content page
     */
    ContentPage newContentPage(Page page, PageTemplate pageTemplate, Map fragmentDefinitions);
    
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
     * Update fragment portlet decorator.
     *  
     * @param contentFragment content fragment context
     * @param decoratorName portlet decorator name
     */
    void updateDecorator(ContentFragment contentFragment, String decoratorName);

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
     * Update fragment layout position.
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
     * Update preferences with new preferences set, accepting
     * Map of strings, string arrays, FragmentPreference or
     * PortletPreference. Existing preferences are removed and
     * replaced with the specified preferences.
     * 
     * @param contentFragment content fragment context
     * @param preferences map of new preferences set.
     */
    void updatePreferences(ContentFragment contentFragment, Map preferences);

    /**
     * Update fragment row and column layout position.
     * 
     * @param contentFragment content fragment context
     * @param row fragment row position
     * @param column fragment column position
     */
    void updateRowColumn(ContentFragment contentFragment, int row, int column);
    
    /**
     * Update fragment portlet state and/or mode.
     * 
     * @param contentFragment content fragment context
     * @param portletState fragment portlet state or null
     * @param portletMode fragment portlet mode or null
     */
    void updateStateMode(ContentFragment contentFragment, String portletState, String portletMode);

    /**
     * Update page titles.
     * 
     * @param contentPage content page context
     * @param title page title
     * @param shortTitle page short title
     */
    void updateTitles(ContentPage contentPage, String title, String shortTitle);
    
    /**
     * Returns the root fragment which is not locked and not merged from a page template
     * 
     * @param contentPage content page context
     * @return
     */
    ContentFragment getUnlockedRootFragment(ContentPage contentPage);
    
}
