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
package org.apache.jetspeed.page.document.psml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.file.FileCacheEntry;
import org.apache.jetspeed.cache.file.FileCacheEventListener;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.folder.Reset;
import org.apache.jetspeed.om.folder.psml.FolderImpl;
import org.apache.jetspeed.om.folder.psml.FolderMetaDataImpl;
import org.apache.jetspeed.om.page.Document;
import org.apache.jetspeed.page.document.DocumentHandler;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteFolderException;
import org.apache.jetspeed.page.document.FailedToUpdateFolderException;
import org.apache.jetspeed.page.document.FolderHandler;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeSet;
import org.apache.jetspeed.page.document.UnsupportedDocumentTypeException;

/**
 * <p>
 * FileSystemFolderHandler
 * </p>
 * <p>
 * Implementation of <code>FolderHanlder</code> that is based off of the file
 * system.
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class FileSystemFolderHandler implements FolderHandler, FileCacheEventListener
{

    private IdGenerator generator;
    private File documentRootDir;
    private DocumentHandler metadataDocHandler;
    private DocumentHandlerFactory handlerFactory;

    private final static Logger log = LoggerFactory.getLogger(FileSystemFolderHandler.class);

    protected static final FilenameFilter FOLDER_FILTER = new FilenameFilter()
    {

        public boolean accept( File pathname, String fileName )
        {
            return new File(pathname, fileName).isDirectory();
        }

    };
    private FileCache fileCache;

    /**
     * 
     * @param generator
     *            id generator for unmarshalled documents
     * @param documentRoot
     *            directory on file system to use as the root when locating
     *            folders
     * @param handlerFactory
     *            A <code>DocumentHandlerFactory</code>
     * @param fileCache
     *            For caching folder instances
     * @throws FileNotFoundException
     *             if the <code>documentRoot</code> does not exist
     * @throws UnsupportedDocumentTypeException
     *             if no <code>DocumentHnadler</code> could be found that
     *             supports folder metadata (folder.metadata) in the
     *             <code>handlerFactory</code>.
     */
    public FileSystemFolderHandler( IdGenerator generator, String documentRoot, DocumentHandlerFactory handlerFactory, FileCache fileCache )
            throws FileNotFoundException, UnsupportedDocumentTypeException
    {
        super();
        this.generator = generator;
        this.documentRootDir = new File(documentRoot);
        verifyPath(documentRootDir);
        this.handlerFactory = handlerFactory;
        this.metadataDocHandler = handlerFactory.getDocumentHandler(FolderMetaDataImpl.DOCUMENT_TYPE);
        this.fileCache = fileCache;
        this.fileCache.addListener(this);
    }

    /**
     * <p>
     * getFolder
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.FolderHandler#getFolder(java.lang.String)
     * @param path
     * @return @throws
     *         FolderNotFoundException
     * @throws FolderNotFoundException
     * @throws InvalidFolderException
     * @throws NodeException
     * @throws DocumentNotFoundException
     */
    public Folder getFolder( String path ) throws FolderNotFoundException, InvalidFolderException, NodeException
    {

        return getFolder(path, true);
    }

    protected void verifyPath( File path ) throws FileNotFoundException
    {
        if (path == null)
        {
            throw new IllegalArgumentException("Page root cannot be null");
        }

        if (!path.exists())
        {
            throw new FileNotFoundException("Could not locate root pages path " + path.getAbsolutePath());
        }
    }

    /**
     * <p>
     * getFolder
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.FolderHandler#getFolder(java.lang.String,
     *      boolean)
     * @param path
     * @param fromCache
     * @return @throws
     *         DocumentException, FolderNotFoundException
     * @throws InvalidFolderException
     * @throws DocumentNotFoundException
     */
    public Folder getFolder( String path, boolean fromCache ) throws NodeException, FolderNotFoundException, InvalidFolderException
    {
        Folder folder = null;
        File folderFile = new File(documentRootDir, path);
        if(!folderFile.exists())
        {
            throw new FolderNotFoundException(folderFile.getAbsolutePath()+" does not exist.");
        }
        
        if(!folderFile.isDirectory())
        {
            throw new InvalidFolderException(folderFile.getAbsolutePath()+" is not a valid directory.");
        }
        
        // cleanup trailing separators
        if (!path.equals(Folder.PATH_SEPARATOR) && path.endsWith(Folder.PATH_SEPARATOR))
        {
            path = path.substring(0, path.length()-1);
        }

        // check cache
        if (fromCache)
        {
            folder = (Folder) fileCache.getDocument(path);
        }

        // get new folder
        if (folder == null)
        {
            try
            {
                // look for metadata
                FolderMetaDataImpl metadata = (FolderMetaDataImpl) metadataDocHandler.getDocument(path + Folder.PATH_SEPARATOR + FolderMetaDataImpl.DOCUMENT_TYPE);
                folder = new FolderImpl(path, metadata, handlerFactory, this);
            }
            catch (DocumentNotFoundException e)
            {
                // no metadata
                folder = new FolderImpl(path, handlerFactory, this);
            }

            // recursively set parent
            if (!path.equals(Folder.PATH_SEPARATOR))
            {
                String parentPath = path;
                int parentSeparatorIndex = parentPath.lastIndexOf(Folder.PATH_SEPARATOR_CHAR);
                if (parentSeparatorIndex > 0)
                {
                    parentPath = parentPath.substring(0, parentSeparatorIndex);
                }
                else
                {
                    parentPath = Folder.PATH_SEPARATOR;
                }
                folder.setParent(getFolder(parentPath));
            }

            // folder unmarshalled
            ((FolderImpl) folder).unmarshalled(generator);

            // add to cache
            if (fromCache)
            {
                addToCache(path, folder);
            }
        }

        return folder;
    }

    /**
     * <p>
     * updateFolder
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.FolderHandler#updateFolder(org.apache.jetspeed.om.folder.Folder)
     * @param folder
     * @throws FailedToUpdateFolderException
     */
    public void updateFolder(Folder folder) throws FailedToUpdateFolderException
    {
        // sanity checks
        if (folder == null)
        {
            log.warn("Recieved null Folder to update");
            return;
        }
        String path = folder.getPath();
        if (path == null)
        {
            path = folder.getId();
            if (path == null)
            {
                log.warn("Recieved Folder with null path/id to update");
                return;
            }
            folder.setPath(path);
        }

        // setup folder implementation
        FolderImpl folderImpl = (FolderImpl)folder;
        folderImpl.setFolderHandler(this);
        folderImpl.setHandlerFactory(handlerFactory);
        folderImpl.setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
        folderImpl.setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
        folderImpl.marshalling();

        // create underlying folder if it does not exist
        File folderFile = new File(documentRootDir, path);
        if ((folderFile.exists() && !folderFile.isDirectory()) || (!folderFile.exists() && !folderFile.mkdir()))
        {
            throw new FailedToUpdateFolderException(folderFile.getAbsolutePath()+" does not exist and cannot be created.");
        }

        // update metadata
        try
        {
            FolderMetaDataImpl metadata = folderImpl.getFolderMetaData();
            metadata.setPath(path + Folder.PATH_SEPARATOR + FolderMetaDataImpl.DOCUMENT_TYPE);
            metadataDocHandler.updateDocument(metadata);
        }
        catch (Exception e)
        {
            throw new FailedToUpdateFolderException(folderFile.getAbsolutePath()+" failed to update folder.metadata", e);
        }

        // add to cache
        addToCache(path, folder);
    }

    /**
     * <p>
     * removeFolder
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.FolderHandler#removeFolder(org.apache.jetspeed.om.folder.Folder)
     * @param folder
     * @throws FailedToDeleteFolderException
     */
    public void removeFolder(Folder folder) throws FailedToDeleteFolderException
    {
        // sanity checks
        if (folder == null)
        {
            log.warn("Recieved null Folder to remove");
            return;
        }
        String path = folder.getPath();
        if (path == null)
        {
            path = folder.getId();
            if (path == null)
            {
                log.warn("Recieved Folder with null path/id to remove");
                return;
            }
            folder.setPath(path);
        }

        // remove folder nodes
        FolderImpl folderImpl = (FolderImpl)folder;
        try
        {
            // copy all folder nodes to remove
            List<Node> removeNodes = new ArrayList<Node>();
            for (Node node : folderImpl.getAllNodes())
            {
                removeNodes.add(node);
            }
            
            // remove folder nodes
            for (Node node : removeNodes)
            {
                if (node instanceof Folder)
                {
                    // recursively remove folder
                    removeFolder((Folder)node);
                }
                else if (node instanceof Document)
                {
                    // remove folder document
                    try
                    {
                        handlerFactory.getDocumentHandler(node.getType()).removeDocument((Document)node);
                    }
                    catch (Exception e)
                    {
                        File documentFile = new File(this.documentRootDir, node.getPath());
                        throw new FailedToDeleteFolderException(documentFile.getAbsolutePath()+" document cannot be deleted.");
                    }
                }
                ((NodeSetImpl)folderImpl.getAllNodes()).remove(node);
            }
        }
        catch (FailedToDeleteFolderException fdfe)
        {
            throw fdfe;
        }
        catch (Exception e)
        {
            throw new FailedToDeleteFolderException(e.getMessage());
        }

        // remove underlying folder and unknown files
        File folderFile = new File(this.documentRootDir, path);
        File metadataFile = null;
        if ((folderImpl.getFolderMetaData() != null) && (folderImpl.getFolderMetaData().getPath() != null))
        {
            metadataFile = new File(this.documentRootDir, folderImpl.getFolderMetaData().getPath());
        }
        if (folderFile.exists() && folderFile.isDirectory())
        {
            // attempt to clean folder for delete
            String[] contents = folderFile.list();
            for (int i = 0; (i < contents.length); i++)
            {
                File contentFile = new File(folderFile, contents[i]);
                if ((metadataFile == null) || !contentFile.equals(metadataFile))
                {
                    if (!deleteFile(contentFile))
                    {
                        throw new FailedToDeleteFolderException(folderFile.getAbsolutePath()+" unrecognized folder contents cannot be deleted.");
                    }
                }
            }
            // delete folder and metadata
            if ((metadataFile != null) && metadataFile.exists() && !metadataFile.delete())
            {
                throw new FailedToDeleteFolderException(folderFile.getAbsolutePath()+" folder metadata cannot be deleted.");
            }
            // delete folder and all remaining folder contents
            // unless folder is root folder which should be
            // preserved as PSML "mount point"
            if (!path.equals(Folder.PATH_SEPARATOR) && !folderFile.delete())
            {
                throw new FailedToDeleteFolderException(folderFile.getAbsolutePath()+" folder cannot be deleted.");
            }
        }
        else
        {
            throw new FailedToDeleteFolderException(folderFile.getAbsolutePath()+" not found.");
        }

        // remove from cache
        fileCache.remove(path);

        // reset folder
        if (folderImpl.getFolderMetaData() != null)
        {
            folderImpl.getFolderMetaData().setParent(null);
        }
        folderImpl.setParent(null);
        folderImpl.reset();
    }

    private static final boolean deleteFile(File file)
    {
        if (file.isDirectory())
        {
            String[] children = file.list();
            for (int i = 0; (i < children.length); i++)
            {
                if (!deleteFile(new File(file, children[i])))
                {
                    return false;
                }
            }
        }
        return file.delete();
    }

    /**
     * <p>
     * getFolders
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.FolderHandler#getFolders(java.lang.String)
     * @param path
     * @return @throws
     *         FolderNotFoundException
     * @throws FolderNotFoundException
     * @throws InvalidFolderException
     * @throws NodeException
     */
    public NodeSet getFolders( String path ) throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        File parent = new File(documentRootDir, path);
        if (!parent.exists())
        {
            throw new FolderNotFoundException("No folder exists at the path: " + parent.getAbsolutePath());
        }
        else
        {
            String[] children = getChildrenNames(path, FOLDER_FILTER);
            NodeSetImpl folders = new NodeSetImpl(path);
            for (int i = 0; i < children.length; i++)
            {
                if (path.endsWith(Folder.PATH_SEPARATOR))
                {
                    folders.add(getFolder(path + children[i]));
                }
                else
                {
                    folders.add(getFolder(path + Folder.PATH_SEPARATOR + children[i]));
                }
            }
            return folders;
        }
    }

    public class DocumentTypeFilter implements FilenameFilter
    {
        private String documentType;

        public DocumentTypeFilter( String documentType )
        {
            this.documentType = documentType;
        }

        /**
         * <p>
         * accept
         * </p>
         * 
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         * @param dir
         * @param name
         * @return
         */
        public boolean accept( File dir, String name )
        {
            return name.endsWith(documentType);
        }

    }

    /**
     * <p>
     * list
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.FolderHandler#list(java.lang.String)
     * @param documentType
     * @return @throws
     *         FolderNotFoundException
     */
    public String[] list( String folderPath, String documentType ) throws FolderNotFoundException
    {
        return getChildrenNames(folderPath, new DocumentTypeFilter(documentType));
    }

    /**
     * <p>
     * listAll
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.FolderHandler#listAll(java.lang.String)
     * @param folderPath
     * @return @throws
     *         FolderNotFoundException
     */
    public String[] listAll( String folderPath ) throws FolderNotFoundException
    {
        return getChildrenNames(folderPath, null);
    }

    protected String[] getChildrenNames( String path, FilenameFilter filter ) throws FolderNotFoundException
    {
        File parent = new File(documentRootDir, path);
        if (!parent.exists())
        {
            throw new FolderNotFoundException("No folder exists at the path: " + parent.getAbsolutePath());
        }
        else
        {
            if (filter != null)
            {
                return parent.list(filter);
            }
            else
            {
                return parent.list();
            }
        }
    }

    /**
     * <p>
     * getChildNodes
     * </p>
     *
     * @see org.apache.jetspeed.page.document.FolderHandler#getNodes(java.lang.String,boolean,java.lang.String)
     * @param path
     * @param regexp
     * @param documentType
     * @return NodeSet
     * @throws FolderNotFoundException
     * @throws DocumentException
     * @throws InvalidFolderException
     * @throws NodeException
     */
    public NodeSet getNodes(String path, boolean regexp, String documentType)
        throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        // path must be valid absolute path
        if ((path == null) || ! path.startsWith(Folder.PATH_SEPARATOR))
        {
            throw new InvalidFolderException( "Invalid path specified " + path );
        }

        // traverse folders and parse path from root,
        // accumualting matches in node set
        Folder folder = getFolder(Folder.PATH_SEPARATOR);
        NodeSetImpl matched = new NodeSetImpl(null);
        getNodes(folder,path,regexp,matched);

        // return matched nodes filtered by document type
        if (documentType != null)
        {
            return matched.subset(documentType);
        }
        return matched;
    }

    private void getNodes(Folder folder, String path, boolean regexp, NodeSet matched)
        throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        // test for trivial folder match
        if (path.equals(Folder.PATH_SEPARATOR))
        {
            matched.add(folder);
            return;
        }

        // remove leading separator
        if (path.startsWith(Folder.PATH_SEPARATOR))
        {
            path = path.substring(1);
        }

        // parse path for folder path match
        int separatorIndex = path.indexOf(Folder.PATH_SEPARATOR);
        if (separatorIndex != -1)
        {
            // match folder name
            String folderName = path.substring(0,separatorIndex);
            String folderPath = (folder.getPath().endsWith(Folder.PATH_SEPARATOR) ? folder.getPath() : folder.getPath() + Folder.PATH_SEPARATOR) + folderName;
            NodeSet matchedFolders = null;
            if (regexp)
            {
                // get regexp matched folders
                matchedFolders = ((FolderImpl)folder).getFolders(false).inclusiveSubset(folderPath);
            }
            else
            {
                // get single matched folder
                Folder matchedFolder = getFolder(folderPath);
                if (matchedFolder != null)
                {
                    matchedFolders = new NodeSetImpl(folder.getPath());
                    matchedFolders.add(matchedFolder);
                }
            }
            if ((matchedFolders == null) || (matchedFolders.size() == 0))
            {
                throw new FolderNotFoundException("Cannot find folder" + folderName + " in " + folder.getPath());
            }

            // match recursively over matched folders
            path = path.substring(separatorIndex);
            for (Node matchedFolder : matchedFolders)
            {
                getNodes((Folder)matchedFolder, path, regexp, matched);
            }
            return;
        }

        // match node name
        String nodeName = path;
        String nodePath = (folder.getPath().endsWith(Folder.PATH_SEPARATOR) ? folder.getPath() : folder.getPath() + Folder.PATH_SEPARATOR) + nodeName;
        if (regexp)
        {
            // get regexp matched nodes
            for (Node matchedNode : ((FolderImpl)folder).getAllNodes().inclusiveSubset(nodePath))
            {
                matched.add(matchedNode);
            }
        }
        else
        {
            // get single matched node
            for (Node findNode : ((FolderImpl)folder).getAllNodes())
            {
                if (findNode.getPath().equals(nodePath))
                {
                    matched.add(findNode);
                    break;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.FolderHandler#shutdown()
     */
    public void shutdown()
    {
        // disconnect cache listener
        fileCache.removeListener(this);
    }    

    /**
     * <p>
     * addToCache
     * </p>
     * 
     * @param id
     * @param objectToCache
     */
    protected void addToCache( String id, Object objectToCache )
    {
        synchronized (fileCache)
        {
            // store the document in the hash and reference it to the
            // watcher
            try
            {
                fileCache.put(id, objectToCache, this.documentRootDir);

            }
            catch (java.io.IOException e)
            {

                String msg = "Error storing Document in the FileCache: " + e.toString();
                log.error(msg);
                IllegalStateException ise = new IllegalStateException(msg);
                ise.initCause(e);
            }
        }
    }

    /**
     * <p>
     * refresh
     * </p>
     * 
     * @see org.apache.jetspeed.cache.file.FileCacheEventListener#refresh(org.apache.jetspeed.cache.file.FileCacheEntry)
     * @param entry
     * @throws Exception
     */
    public void refresh( FileCacheEntry entry ) throws Exception
    {
        if (entry.getDocument() instanceof Folder)
        {
            Folder folder = (Folder) entry.getDocument();            
            entry.setDocument(getFolder(folder.getPath(), false));
            Node parentNode = ((AbstractNode)folder).getParent(false);
            if (parentNode != null)
            {
                FileCacheEntry parentEntry = fileCache.get(parentNode.getPath());
                refresh(parentEntry);                
            }
        }
        else if (entry.getDocument() instanceof Document)
        {
            Document doc = (Document) entry.getDocument();
            if (doc.getType().equals(FolderMetaDataImpl.DOCUMENT_TYPE))
            {
                Node folderNode = ((AbstractNode)doc).getParent(false);
                if (folderNode != null)
                {                
                    FileCacheEntry folderEntry = fileCache.get(folderNode.getPath());
                    refresh(folderEntry);
                }
            }
        }
        
        if (entry.getDocument() instanceof Reset)
        {
            ((Reset)entry.getDocument()).reset();
        }
    }

    /**
     * <p>
     * evict
     * </p>
     * 
     * @see org.apache.jetspeed.cache.file.FileCacheEventListener#evict(org.apache.jetspeed.cache.file.FileCacheEntry)
     * @param entry
     * @throws Exception
     */
    public void evict( FileCacheEntry entry ) throws Exception
    {

    }

    public boolean isFolder(String path)
    {
        return new File(this.documentRootDir, path).isDirectory();        
    }
}
