/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.om.page;

import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.layout.PageLayoutComponent;
import org.apache.jetspeed.om.portlet.GenericMetadata;

/**
 * ContentPage is a read-only version of the {@link org.apache.jetspeed.om.page.Page}
 * object for use in rendering. <code>Page</code> objects are persistent, single-instance 
 * metadata objects that should not be used to hold per-request content. ContentPage
 * solves this by providing a parallel interface that can be used for rendering
 * requested content associated with the current user-request. 
 * 
 * @author weaver@apache.org
 *
 */
public interface ContentPage
{
    /************** SecuredResource **************/

    /**
     * Check security access to page.
     *
     * @param actions list to be checked against in CSV string form
     * @throws SecurityException
     */
    void checkAccess(String actions) throws SecurityException;
    
    /************** BaseElement **************/
    
    /**
     * Returns the unique Id of this element. This id is guaranteed to be unique
     * from the complete portal and is suitable to be used as a unique key.
     *
     * @return the unique id of this element.
     */
    String getId();

    /**
     * Returns the title in the default Locale
     *
     * @return the page title
     */
    String getTitle();

    /**
     * Returns the short title in the default Locale
     *
     * @return the page short title
     */
    String getShortTitle();
    
    /************** Node **************/
    
    /**
     * Return page metadata.
     *
     * @return page metadata
     */
    GenericMetadata getMetadata();

    /**
     * Returns the name of this page.
     *
     * @return page name
     */
    String getName();
    
    /**
     * Returns the path of this page.
     * 
     * @return page path
     */
    String getPath();

    /**
     * Returns the short title for the specified locale.
     *
     * @param locale
     * @return localized title of this Node.
     */
    String getShortTitle(Locale locale);
    
    /**
     * Returns the title for the specified locale.
     *
     * @param locale
     * @return localized title of this Node.
     */
    String getTitle(Locale locale);

    /**
     * Returns URL of page.
     * 
     * @return page URL
     */
    String getUrl();
    
    /**
     * Whether or not this Node should be hidden in terms of the view.
     *   
     * @return hidden flag
     */
    boolean isHidden();

    /************** BasePageElement **************/

    /**
     * Returns the name of the default skin that applies to this
     * page.
     *
     * @return the page default skin name
     */
    String getSkin();

    /**
     * Returns the name of the default decorator that applies in this page
     * to fragments of the specified type
     *
     * @param fragmentType the type of fragment considered
     * @return the decorator name for the selected type
     */
    String getDefaultDecorator(String fragmentType);

    /**
     * Provides access to a per-request safe ContentFragment.
     * ContentFragments add the additional ability to temporarily
     * store rendered content of the current request along with
     * original.
     * 
     * @return root ContentFragment fragment.
     */
    ContentFragment getRootFragment();

    /**
     * Returns a ContentFragment represented by the fragment id argument.
     * @param id unique id of the ContentFragment we want to retrieve.
     * @return
     */
    ContentFragment getFragmentById(String id);

    /**
     * Returns a list of ContentFragment fragments represented by the name argument.
     * @param name name of the ContentFragments we want to retrieve.
     * @return list of ContentFragment
     */
    List getFragmentsByName(String name);
    
    /************** BaseConcretePageElement **************/

    /**
     * Returns the name of the default decorator as set here or
     * in parent folders that applies in this page to fragments
     * of the specified type.
     *
     * @param fragmentType the type of fragment considered
     * @return the decorator name for the selected type
     */
    String getEffectiveDefaultDecorator(String fragmentType);

    /************** ContentPage **************/

    /**
     * Access underlying concrete persistent page or null
     * if page is transient or constructed dynamically.
     * 
     * @return persistent page or null
     */
    Page getPage();
    
    /**
     * Returns the PageLayoutComponent that generated this ContentPage
     * 
     * @return PageLayoutComponent instance.
     */
    PageLayoutComponent getPageLayoutComponent();

    /**
     * Override page default decorator.
     *  
     * @param decoratorName decorator name
     * @param fragmentType decorator fragment type
     */
    void overrideDefaultDecorator(String decoratorName, String fragmentType);
    
    /************** PageLayoutComponent Operations **************/

    /**
     * Add fragment to page at specified row column layout position.
     * 
     * @param fragment externally constructed fragment to add
     * @param row fragment row position
     * @param column fragment column position
     */
    ContentFragment addFragmentAtRowColumn(ContentFragment fragment, int row, int column);                        
    
    /**
     * Add portlet to page returning associated content fragment.
     * 
     * @param type portlet type
     * @param name portlet name
     * @return new content fragment added to page
     */
    ContentFragment addPortlet(String type, String name);

    /**
     * Decrement position of folder in parent folder document order.
     */
    void decrementFolderInDocumentOrder();

    /**
     * Decrement position of page in folder document order.
     */
    void decrementInDocumentOrder();

    /**
     * Compute fragment nesting level of fragment by id. Fragment
     * level returned is 0 if root fragment, 1 if in root fragment
     * collection, etc. 
     * 
     * @param fragmentId target fragment id
     * @return fragment nesting level or -1 if not found
     */
    int getFragmentNestingLevel(String fragmentId);
    
    /**
     * Increment position of folder in parent folder document order.
     */
    void incrementFolderInDocumentOrder();

    /**
     * Increment position of page in folder document order.
     */
    void incrementInDocumentOrder();
    
    /**
     * Move fragment from current parent layout fragment to another
     * layout fragment in the same page.
     *
     * @param fragmentId fragment id of fragment to move
     * @param fromFragmentId fragment id of current parent layout fragment
     * @param toFragmentId fragment id of new parent layout fragment
     */
    void moveFragment(String fragmentId, String fromFragmentId, String toFragmentId);

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
     * @param folderName unique new folder name, (also used as
     *                   default page title)
     * @param folderTitle new folder title or null
     * @param folderShortTitle new folder short title or null
     * @param defaultPageLayoutName root level layout fragment name
     *                              for default page
     */
    void newSiblingFolder(String folderName, String folderTitle, String folderShortTitle, String defaultPageLayoutName);
    
    /**
     * Create new sibling page with specified configuration and add
     * new page at end of folder document order list. Both title and
     * short title parameters default to page name if not specified.
     * The layout fragment name is cloned from this content page if
     * not specified. Default decorators are cloned from this content
     * page.
     * 
     * @param pageName unique new page name
     * @param layoutName root level layout fragment name or null 
     * @param pageTitle new page title or null
     * @param pageShortTitle new page short title or null
     */
    void newSiblingPage(String pageName, String layoutName, String pageTitle, String pageShortTitle);
    
    /**
     * Remove fragment from page by id.
     * 
     * @param fragmentId id of fragment to remove
     * @return flag indicating removed
     */
    void removeFragment(String fragmentId);
    
    /**
     * Remove page and remove from folder document order list.
     */
    void remove();
    
    /**
     * Remove folder and remove from parent folder document order list.
     */
    void removeFolder();
    
    /**
     * Update page default decorator.
     *  
     * @param decoratorName decorator name
     * @param fragmentType decorator fragment type
     */
    void updateDefaultDecorator(String decoratorName, String fragmentType);
    
    /**
     * Update folder titles.
     * 
     * @param title folder title
     * @param shortTitle folder short title
     */
    void updateFolderTitles(String title, String shortTitle);

    /**
     * Update page titles.
     * 
     * @param title page title
     * @param shortTitle page short title
     */
    void updateTitles(String title, String shortTitle);
}
