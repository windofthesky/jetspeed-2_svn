/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetspeed.om.folder;

import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.PageSet;
import org.apache.jetspeed.page.PageNotFoundException;

/**
 * Folder
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public interface Folder extends SecuredResource, ChildNode
{
    /**
     * Gets the unique name of this desktop
     * 
     * @return The unique name of the desktop
     */
    String getName();
    
    /**
     * Sets the unique name of this desktop
     * 
     * @param name The name of the desktop 
     */
    void setName(String name);
    
    String getDefaultPage();
    
    void setDefaultPage(String defaultPage);
    
    String getDefaultTheme();
    
    void setDefaultTheme(String defaultTheme);
    
    FolderSet getFolders();
    
    void setFolders(FolderSet folders);
    
    /**
     * 
     * <p>
     * getPages
     * </p>
     *
     * @return PageSet of all the Pages referenced by this Folder.
     * @throws PageNotFoundException if any of the Pages referenced by this Folder
     * could not be found.
     */
    PageSet getPages() throws PageNotFoundException;
    
    void setPages(PageSet pages);
    
  
    /**
     * 
     * <p>
     * getPage
     * </p>
     *
     * @param name
.     * @throws PageNotFoundException if the Page requested could not be found.
     */
    Page getPage(String name) throws PageNotFoundException;
}
