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


import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;

/**
 * Folder
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 */
public interface Folder extends Node
{
    String FOLDER_TYPE = "folder";
    
    /**
     * 
     * <p>
     * getDefaultPage
     * </p>
     *
     * @return A String representing the default psml page for this folder
     */
    String getDefaultPage();
    
    /**
     * 
     * <p>
     * setDefaultPage
     * </p>
     *
     * @param defaultPage
     */
    void setDefaultPage(String defaultPage);
    
    /**
     * 
     * <p>
     * getDefaultTheme
     * </p>
     *
     * @return A String representing the default theme for this Folder
     */
    String getDefaultTheme();
    
    /**
     * 
     * <p>
     * setDefaultTheme
     * </p>
     *
     * @param defaultTheme
     */
    void setDefaultTheme(String defaultTheme);
    
    /**
     * 
     * <p>
     * getFolders
     * </p>
     *
     * @return A <code>NodeSet</code> containing all sub-folders directly under
     * this folder.
     * @throws FolderNotFoundException
     * @throws DocumentException
     */
    NodeSet getFolders() throws FolderNotFoundException, DocumentException;
    
    /**
     * 
     * <p>
     * getPages
     * </p>
     *
     * @return NodeSet of all the Pages referenced by this Folder.
     * @throws NodeException
     * @throws PageNotFoundException if any of the Pages referenced by this Folder
     * could not be found.
     */
    NodeSet getPages() throws NodeException;
    
    /**
     * 
     * <p>
     * getPage
     * </p>
     *
     * @param name
.     * @throws PageNotFoundException if the Page requested could not be found.
     * @throws DocumentException
     * @throws NodeException
     */
    Page getPage(String name) throws PageNotFoundException, NodeException;
    
    /**
     * 
     * <p>
     * getLinks
     * </p>
     *
     * @return
     * @throws DocumentException
     * @throws NodeException
     */    
    NodeSet getLinks() throws NodeException;
    
    /**
     * 
     * <p>
     * getAllNodes
     * </p>
     *
     * @return All Nodes immediatley under this Folder.
     * @throws DocumentException
     * @throws FolderNotFoundException
     */
    NodeSet getAllNodes() throws FolderNotFoundException, DocumentException;
}
