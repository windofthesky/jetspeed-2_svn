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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.file.FileCacheEntry;
import org.apache.jetspeed.cache.file.FileCacheEventListener;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Document;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

/**
 * <p>
 * CastorFileSystemDocumentHandler
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class CastorFileSystemDocumentHandler implements DocumentHandler, FileCacheEventListener
{

    private final static Log log = LogFactory.getLog(CastorFileSystemDocumentHandler.class);

    protected String mappingFile;
    protected String documentType;
    protected Class expectedReturnType;
    protected String documentRoot;
    protected File documentRootDir;
    protected FileCache fileCache;
    /** the Castor mapping file name */
    protected Mapping mapping = null;

    private OutputFormat format;

    /**
     * 
     * @param mappingFile
     *            Castor mapping file. THe mapping file must be in the class
     *            path
     * @param documentType
     * @param expectedReturnType
     * @throws FileNotFoundException
     */
    public CastorFileSystemDocumentHandler( String mappingFile, String documentType, Class expectedReturnType,
            String documentRoot, FileCache fileCache ) throws FileNotFoundException
    {
        super();
        this.mappingFile = mappingFile;
        this.documentType = documentType;
        this.expectedReturnType = expectedReturnType;
        this.documentRoot = documentRoot;
        this.documentRootDir = new File(documentRoot);
        verifyPath(documentRootDir);
        this.fileCache = fileCache;
        this.fileCache.addListener(this);
        this.format = new OutputFormat();
        format.setIndenting(true);
        format.setIndent(4);

        loadMapping();
    }
    
    public CastorFileSystemDocumentHandler( String mappingFile, String documentType, String expectedReturnType,
            String documentRoot, FileCache fileCache ) throws FileNotFoundException, ClassNotFoundException
    {
        this(mappingFile, documentType, Class.forName(expectedReturnType), documentRoot, fileCache);
    }

    /**
     * <p>
     * getDocument
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.DocumentHandler#getDocument(java.lang.String)
     * @param name
     * @return @throws
     *         DocumentNotFoundException
     * @throws DocumentException,
     *             DocumentNotFoundException
     */
    public Document getDocument( String name ) throws NodeException, DocumentNotFoundException
    {
        return getDocument(name, true);
    }

    /**
     * <p>
     * updateDocument
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.DocumentHandler#updateDocument(org.apache.jetspeed.om.page.Document)
     * @param document
     */
    public void updateDocument( Document document ) throws FailedToUpdateDocumentException
    {
        //  sanity checks
        if (document == null)
        {
            log.warn("Recieved null Document to update");
            return;
        }

        String id = document.getId();

        // marshal page to disk
        File f = new File(this.documentRootDir, id + this.documentType);
        FileWriter writer = null;

        try
        {
            writer = new FileWriter(f);
            Serializer serializer = new XMLSerializer(writer, this.format);
            Marshaller marshaller = new Marshaller(serializer.asDocumentHandler());
            marshaller.setMapping(this.mapping);
            marshaller.marshal(document);
        }
        catch (MarshalException e)
        {
            log.error("Could not marshal the file " + f.getAbsolutePath(), e);
            throw new FailedToUpdateDocumentException(e);
        }
        catch (MappingException e)
        {
            log.error("Could not marshal the file " + f.getAbsolutePath(), e);
            throw new FailedToUpdateDocumentException(e);
        }
        catch (ValidationException e)
        {
            log.error("Document " + f.getAbsolutePath() + " is not valid", e);
            throw new FailedToUpdateDocumentException(e);
        }
        catch (IOException e)
        {
            log.error("Could not save the file " + f.getAbsolutePath(), e);
            throw new FailedToUpdateDocumentException(e);
        }
        catch (Exception e)
        {
            log.error("Error while saving  " + f.getAbsolutePath(), e);
            throw new FailedToUpdateDocumentException(e);
        }
        finally
        {
            try
            {
                writer.close();
            }
            catch (IOException e)
            {
            }
        }

    }

   

    protected void loadMapping()
    {
        try
        {
            InputStream stream = getClass().getResourceAsStream(mappingFile);

            if (log.isDebugEnabled())
            {
                log.debug("Loading psml mapping file " + mappingFile);
            }

            mapping = new Mapping();

            InputSource is = new InputSource(stream);

            is.setSystemId(mappingFile);
            mapping.loadMapping(is);
        }
        catch (Exception e)
        {
            IllegalStateException ise = new IllegalStateException("Error in psml mapping creation");
            ise.initCause(e);
            throw ise;
        }

    }

    protected Object unmarshallDocument( Class clazz, String path, String extension ) throws NodeNotFoundException,
            DocumentException
    {
        Document document = null;
        File f = null;
        if (path.endsWith(extension))
        {
            f = new File(this.documentRootDir, path);
        }
        else
        {
            f = new File(this.documentRootDir, path + extension);
        }

        if (!f.exists())
        {
            throw new PageNotFoundException("Document not found: " + path);
        }

        FileReader reader = null;

        try
        {
            reader = new FileReader(f);
            Unmarshaller unmarshaller = new Unmarshaller(this.mapping);
            document = (Document) unmarshaller.unmarshal(reader);
            document.setId(path);
            document.setPath(path);
        }
        catch (IOException e)
        {
            throw new PageNotFoundException("Could not load the file " + f.getAbsolutePath(), e);
        }
        catch (MarshalException e)
        {
            throw new PageNotFoundException("Could not unmarshal the file " + f.getAbsolutePath(), e);
        }
        catch (MappingException e)
        {
            throw new PageNotFoundException("Could not unmarshal the file " + f.getAbsolutePath(), e);
        }
        catch (ValidationException e)
        {
            throw new DocumentNotFoundException("Document " + f.getAbsolutePath() + " is not valid", e);
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
            }
        }

        if (document == null)
        {
            throw new DocumentNotFoundException("Document not found: " + path);
        }
        else
        {
            if (!clazz.isAssignableFrom(document.getClass()))
            {
                throw new ClassCastException(document.getClass().getName() + " must implement or extend "
                        + clazz.getName());
            }
            return document;
        }
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
     * removeDocument
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.DocumentHandler#removeDocument(org.apache.jetspeed.om.page.Document)
     * @param document
     * @throws DocumentNotFoundException
     * @throws FailedToDeleteDocumentException
     */
    public void removeDocument( Document document ) throws DocumentNotFoundException, FailedToDeleteDocumentException
    {
        String id = document.getId();

        if (id == null)
        {
            log.warn("Unable to remove page with null Id from disk");
            return;
        }

        File file = new File(this.documentRootDir, id + this.documentType);

        file.delete();

    }

    /**
     * <p>
     * getDocument
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.DocumentHandler#getDocument(java.lang.String,
     *      boolean)
     * @param name
     * @param fromCahe
     *            Whether or not the Document should be pulled from the cache.
     * @return @throws
     *         DocumentNotFoundException
     */
    public Document getDocument( String name, boolean fromCache ) throws NodeException
    {
        Document document = null;
        if (fromCache)
        {
            Object obj = fileCache.getDocument(name);
            document = (Document) obj;
            if (document == null)
            {
                document = (Document) unmarshallDocument(expectedReturnType, name, documentType);
                addToCache(name, document);
            }
        }
        else
        {
            document = (Document) unmarshallDocument(expectedReturnType, name, documentType);
        }

        return document;
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
                log.error("Error putting document: " + e);
                IllegalStateException ise = new IllegalStateException("Error storing Document in the FileCache: "
                        + e.toString());
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
        log.debug("Entry is refreshing: " + entry.getFile().getName());

        if (entry.getDocument() instanceof Document && ((Document) entry.getDocument()).getId().endsWith(documentType))
        {
            Document document = (Document) entry.getDocument();
            Document freshDoc = getDocument(document.getId(), false);
            Node parent = document.getParent();
 
            freshDoc.setParent(parent);
            if(parent instanceof Folder)
            {
                Folder folder = (Folder) parent;
                folder.getAllNodes().add(freshDoc);
            }
            
            freshDoc.setPath(document.getPath());
            entry.setDocument(freshDoc);            
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
        // TODO Auto-generated method stub

    }

    /**
     * <p>
     * getType
     * </p>
     *
     * @see org.apache.jetspeed.page.document.DocumentHandler#getType()
     * @return
     */
    public String getType()
    {
        return documentType;
    }
}