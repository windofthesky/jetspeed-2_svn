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
package org.apache.jetspeed.profiler;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.document.NodeSet;

/**
 * <p>The ProfiledPageContext is used to capture the page and navigation
 * elements associated with a profiled page. This information is intended
 * to reflect the entire context of the displayed page.</p> 
 * 
 * @author <a href="mailto:rwatler@finali.com">Randy Watler</a>
 * @version $Id$
 */
public interface ProfiledPageContext
{
    /**
     * Initialize this page context.
     *
     * @param profiler The profiler initializing this page context.
     * @param locator The profile locator used to create this page context.
     */
    void init(Profiler profiler, ProfileLocator locator);

    /**
     * Return ProfileLocator used to generate this page context.
     *  
     * @return The ProfileLocator instance.
     */
    ProfileLocator getLocator();

    /**
     * Return profiled Page.
     *  
     * @return The Page instance.
     */
    Page getPage();

    /**
     * Set profiled Page.
     *  
     * @param page The Page instance.
     */
    void setPage(Page page);

    /**
     * Return profiled page Folder used for navigation. This is the
     * folder relative to the profiled page. The returned value may
     * or may not be equivalent to getPage().getParent().
     *  
     * @return The Folder instance.
     */
    Folder getFolder();

    /**
     * Set profiled page Folder.
     *  
     * @param folder The Folder instance.
     */
    void setFolder(Folder folder);

    /**
     * Return profiled sibling Pages used for tab navigation. This is
     * the list of defined pages relative to the profiled page. The
     * returned value may or may not be equivalent to
     * getPage().getParent().getPages() or getFolder().getPages().
     *  
     * @return The sibling Page set.
     */
    NodeSet getSiblingPages();

    /**
     * Set profiled sibling Pages.
     *  
     * @param siblingPages The sibling Page set.
     */
    void setSiblingPages(NodeSet pages);

    /**
     * Return profiled parent Folder used for link navigation. This is
     * the parent folder of the folder relative to the profiled page.
     * The returned value may or may not be equivalent to
     * getPage().getFolder().getParent() or getFolder().getParent().
     *  
     * @return The parent Folder instance.
     */
    Folder getParentFolder();

    /**
     * Set profiled parent Folder.
     *  
     * @param folder The parent Folder instance.
     */
    void setParentFolder(Folder folder);

    /**
     * Return profiled sibling Folders used for link navigation. This is
     * the list of defined folders relative to the profiled page. The
     * returned value may or may not be equivalent to
     * getPage().getParent().getFolders() or getFolder().getFolders().
     *  
     * @return The sibling Folder set.
     */
    NodeSet getSiblingFolders();

    /**
     * Set profiled sibling Folders.
     *  
     * @param folders The sibling Folder set.
     */
    void setSiblingFolders(NodeSet folders);

    /**
     * Return profiled root Links used for menu navigation. This is the
     * list of defined links related to the profiled page. The returned
     * value may or may not be equivalent to the result of accessing 
     * getLinks() from getPage(), getFolder(), or getParentFolder().
     *  
     * @return The root Link set.
     */
    NodeSet getRootLinks();

    /**
     * Set profiled root Links.
     *  
     * @param links The root Link set.
     */
    void setRootLinks(NodeSet links);
}
