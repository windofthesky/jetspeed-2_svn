/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.page.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.file.FileCacheEntry;
import org.apache.jetspeed.cache.file.FileCacheEventListener;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderMetaData;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.folder.impl.FolderImpl;
import org.apache.jetspeed.om.page.Document;

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

    private String documentRoot;
    private File documentRootDir;
    private DocumentHandler metadataDocHandler;
    private DocumentHandlerFactory handlerFactory;

    private final static Log log = LogFactory.getLog(FileSystemFolderHandler.class);

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
    public FileSystemFolderHandler( String documentRoot, DocumentHandlerFactory handlerFactory, FileCache fileCache )
            throws FileNotFoundException, UnsupportedDocumentTypeException
    {
        super();
        this.documentRoot = documentRoot;
        this.documentRootDir = new File(documentRoot);
        verifyPath(documentRootDir);
        this.handlerFactory = handlerFactory;
        this.metadataDocHandler = handlerFactory.getDocumentHandler(FolderMetaData.DOCUMENT_TYPE);
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
        FolderMetaData metadata = null;
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
        

        if (fromCache)
        {
            folder = (Folder) fileCache.getDocument(path);
        }

        if (folder == null)
        {
            try
            {
                if (path.endsWith("/"))
                {
                    Object obj = metadataDocHandler.getDocument(path + FolderMetaData.DOCUMENT_TYPE);
                    metadata = (FolderMetaData) obj;
                }
                else
                {
                    Object obj =(FolderMetaData) metadataDocHandler.getDocument(path + "/"
                            + FolderMetaData.DOCUMENT_TYPE);
                    metadata = (FolderMetaData) obj;
                }
                folder = new FolderImpl(path, metadata, handlerFactory, this);
            }
            catch (DocumentNotFoundException e)
            {
                folder = new FolderImpl(path, handlerFactory, this);
            }

            if (!path.equals("/") && path.indexOf('/') > -1)
            {
                folder.setParent(getFolder(path.substring(0, path.lastIndexOf('/'))));
            }
            else if (!path.equals("/") && path.indexOf('/') < 0)
            {
                folder.setParent(getFolder("/"));
            }
        }

        if (fromCache)
        {
            addToCache(path, folder);
        }

        return folder;
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
                if (path.endsWith("/"))
                {
                    folders.add(getFolder(path + children[i]));
                }
                else
                {
                    folders.add(getFolder(path + "/" + children[i]));
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
        String[] relativeNames = null;
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
        if ((path == null) || ! path.startsWith("/"))
        {
            throw new InvalidFolderException( "Invalid path specified " + path );
        }

        // traverse folders and parse path from root,
        // accumualting matches in node set
        Folder folder = getFolder("/");
        NodeSetImpl matched = new NodeSetImpl(null);
        getNodes(folder,path,regexp,matched);

        // return matched nodes filtered by document type
        if (documentType != null)
            return matched.subset(documentType);
        return matched;
    }

    private void getNodes(Folder folder, String path, boolean regexp, NodeSet matched)
        throws FolderNotFoundException, InvalidFolderException, NodeException
    {
        // test for trivial folder match
        if (path.equals("/"))
        {
            matched.add(folder);
            return;
        }

        // remove leading separator
        if (path.startsWith("/"))
            path = path.substring(1);

        // parse path for folder path match
        int separatorIndex = path.indexOf("/");
        if (separatorIndex != -1)
        {
            // match folder name
            String folderName = path.substring(0,separatorIndex);
            String folderPath = (folder.getPath().endsWith("/") ? folder.getPath() : folder.getPath() + "/") + folderName;
            NodeSet matchedFolders = null;
            if (regexp)
            {
                // get regexp matched folders
                matchedFolders = folder.getFolders().inclusiveSubset(folderPath);
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
            Iterator matchedFoldersIter = matchedFolders.iterator();
            while (matchedFoldersIter.hasNext())
            {
                Folder matchedFolder = (Folder) matchedFoldersIter.next();
                getNodes(matchedFolder, path, regexp, matched);
            }
            return;
        }

        // match node name
        String nodeName = path;
        String nodePath = (folder.getPath().endsWith("/") ? folder.getPath() : folder.getPath() + "/") + nodeName;
        if (regexp)
        {
            // get regexp matched nodes
            Iterator addIter = folder.getAllNodes().inclusiveSubset(nodePath).iterator();
            while (addIter.hasNext())
                matched.add((Node) addIter.next());
        }
        else
        {
            // get single matched node
            Iterator findIter = folder.getAllNodes().iterator();
            while (findIter.hasNext())
            {
                Node addNode = (Node) findIter.next();
                if (addNode.getPath().equals(nodePath))
                {
                    matched.add(addNode);
                    break;
                }
            }
        }
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
        if (entry.getDocument() instanceof Folder )
        {
            Folder folder = (Folder) entry.getDocument();            
            entry.setDocument(getFolder(folder.getPath(), false));
            if(folder.getParent() != null)
            {
                FileCacheEntry parentEntry = (FileCacheEntry)fileCache.get(folder.getParent().getPath());
                refresh(parentEntry);                
            }
        }
        else if(entry.getDocument() instanceof Document)
        {
            Document doc = (Document) entry.getDocument();
            if(doc.getType().equals(FolderMetaData.DOCUMENT_TYPE))
            {
                FileCacheEntry folderEntry = fileCache.get(doc.getParent().getPath());
                refresh(folderEntry);
            }
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

}
