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
package org.apache.jetspeed.page.document;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;

/**
 * <p>
 * FolderHandler
 * </p>
 * <p>
 *  
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface FolderHandler
{
    /**
     * 
     * <p>
     * getFolder
     * </p>
     * <p>
     *  Locates a folder given using the <code>path</code> argument.  This should behave
     *  as <code>getFolder("folder/subfolder, true);</code>
     * </p>
     *
     * @param path fully-quallified path to a folder
     * @return Folder represented by the <code>path</code> argument.  Never returns <code>null</code>
     * @throws DocumentException if there was an error processing the request.
     * @throws InvalidFolderException
     * @throws NodeException
     * @throws DocumentNotFoundException If there is no folder at the <code>path</code> specified.
     */
    Folder getFolder(String path) throws FolderNotFoundException, InvalidFolderException, NodeException;
    
    /**
     * 
     * <p>
     * updateFolder
     * </p>
     * <p>
     *  Updates the folder specified with the <code>folder</code> argument.
     * </p>
     *
     * @param folder folder to update
     */
    void updateFolder(Folder folder) throws FailedToUpdateFolderException;
    
    /**
     * 
     * <p>
     * removeFolder
     * </p>
     * <p>
     *  Removes the folder specified with the <code>folder</code> argument.
     * </p>
     *
     * @param folder folder to update
     */
    void removeFolder(Folder folder) throws FailedToDeleteFolderException;
    
    /**
     * 
     * <p>
     * getFolder
     * </p>
     * <p>
     *  Locates a folder given using the <code>path</code> argument.  
     * </p>
     *
     * @param path fully-quallified path to a folder
     * @param fromCache whether or not to check the cache first before checking the underlying folder
     * repository.
     * @return Folder represented by the <code>path</code> argument.  Never returns <code>null</code>
     * @throws DocumentException if there was an error processing the request.
     * @throws InvalidFolderException
     * @throws NodeException
     * @throws DocumentNotFoundException If there is no folder at the <code>path</code> specified.
     */
    Folder getFolder(String path, boolean fromCache) throws FolderNotFoundException, InvalidFolderException, NodeException;
    
    /**
     * 
     * <p>
     * getFolders
     * </p>
     *
     * @param path Path from which to locate child folders
     * @return NodeSet of sub-folders located under the <code>path</code> argument.
     * @throws FolderNotFoundException if folder under the <code>path</code> does not actually
     * exist
     * @throws DocumentException if an error is encountered reading the folders.
     * @throws InvalidFolderException
     * @throws NodeException
     */
    NodeSet getFolders( String path ) throws FolderNotFoundException, InvalidFolderException, NodeException;
    
    /**
     * 
     * <p>
     * list
     * </p>
     * <p>
     *  generates a list of document names, relative to the <code>folderPath</code> argument
     * of the type indicated by the <code>documentType</code> argument.
     * </p>
     * @param folderPath folder path to search under
     * @param documentType document type to filter on.
     * @return a <code>String[]</code> of child document names relative to the <code>folderPath</code>
     * argument and matching the <code>documentType</code> argument.
     * @throws FolderNotFoundException if the <code>folderPath</code> does not exsit.
     */
    String[] list(String folderPath, String documentType) throws FolderNotFoundException;
    
    String[] listAll(String folderPath) throws FolderNotFoundException;

    /**
     * <p>
     * getNodes
     * </p>
     * <p>
     * Returns a set of nodes relative to the <code>folder</code> argument of the type
     * indicated by the <code>documentType</code> argument. The <code>folder</code> argument
     * may include regular expressions if indicated by the <code>regex</code> argument. The
     * returned set is unordered.
     * </p>
     *
     * @param path Path from which to locate documents
     * @param regexp Flag indicating whether regexp should be expanded in path
     * @param documentType document type to filter on.
     * @return NodeSet of documents and folders located under the <code>path</code> argument.
     * @throws FolderNotFoundException if folder under the <code>path</code> does not actually exist.
     * @throws DocumentException if an error is encountered reading the folders.
     * @throws InvalidFolderException
     * @throws NodeException
     */
    NodeSet getNodes(String path, boolean regexp, String documentType) throws FolderNotFoundException, InvalidFolderException, NodeException;
    
    /**
     * Returns true if the path is a folder
     * 
     * @param path
     * @return
     */
    boolean isFolder(String path);
    
    
   /**
     * shutdown - gracefully shutdown handler and disconnect
     * from other singleton components, (e.g. shared caches) 
     */
    public void shutdown();
}
