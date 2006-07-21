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
package org.apache.jetspeed.page.document.psml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.file.FileCacheEntry;
import org.apache.jetspeed.cache.file.FileCacheEventListener;
import org.apache.jetspeed.om.folder.psml.FolderImpl;
import org.apache.jetspeed.om.page.Document;
import org.apache.jetspeed.om.page.psml.AbstractBaseElement;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.page.document.DocumentException;
import org.apache.jetspeed.page.document.DocumentHandlerFactory;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.FailedToDeleteDocumentException;
import org.apache.jetspeed.page.document.FailedToUpdateDocumentException;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.EventProducer;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderAdapter;

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
public class CastorFileSystemDocumentHandler implements org.apache.jetspeed.page.document.DocumentHandler, FileCacheEventListener
{
    private final static Log log = LogFactory.getLog(CastorFileSystemDocumentHandler.class);

    private final static String PSML_DOCUMENT_ENCODING = "UTF-8";

    protected String mappingFile;
    protected String documentType;
    protected Class expectedReturnType;
    protected String documentRoot;
    protected File documentRootDir;
    protected FileCache fileCache;
    /** the Castor mapping file name */
    protected Mapping mapping = null;

    private OutputFormat format;

    private DocumentHandlerFactory handlerFactory;

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
        format.setEncoding(PSML_DOCUMENT_ENCODING);

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
        // sanity checks
        if (document == null)
        {
            log.warn("Recieved null Document to update");
            return;
        }
        String path = document.getPath();
        if (path == null)
        {
            path = document.getId();
            if (path == null)
            {
                log.warn("Recieved Document with null path/id to update");
                return;
            }
            document.setPath(path);
        }
        AbstractBaseElement documentImpl = (AbstractBaseElement)document;
        documentImpl.setHandlerFactory(handlerFactory);
        documentImpl.setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
        documentImpl.setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
        documentImpl.marshalling();
        
        // marshal page to disk
        String fileName = path;        
        if (!fileName.endsWith(this.documentType))
        {
            fileName = path + this.documentType;
        }
        File f = new File(this.documentRootDir, fileName);
        Writer writer = null;

        try
        {
            // marshal: use SAX I handler to filter document XML for
            // page and folder menu definition menu elements ordered
            // polymorphic collection to strip artifical <menu-element>
            // tags enabling Castor XML binding; see JETSPEED-INF/castor/page-mapping.xml
            writer = new OutputStreamWriter(new FileOutputStream(f), PSML_DOCUMENT_ENCODING);
            Serializer serializer = new XMLSerializer(writer, this.format);
            final DocumentHandler handler = serializer.asDocumentHandler();
            Marshaller marshaller = new Marshaller(new DocumentHandler()
                {
                    private int menuDepth = 0;

                    public void characters(char[] ch, int start, int length) throws SAXException
                    {
                        handler.characters(ch, start, length);
                    }

                    public void endDocument() throws SAXException
                    {
                        handler.endDocument();
                    }
                    
                    public void endElement(String name) throws SAXException
                    {
                        // track menu depth
                        if (name.equals("menu"))
                        {
                            menuDepth--;
                        }

                        // filter menu-element noded within menu definition
                        if ((menuDepth == 0) || !name.equals("menu-element"))
                        {
                            handler.endElement(name);
                        }
                    }
                    
                    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
                    {
                        handler.ignorableWhitespace(ch, start, length);
                    }
                    
                    public void processingInstruction(String target, String data) throws SAXException
                    {
                        handler.processingInstruction(target, data);
                    }
                    
                    public void setDocumentLocator(Locator locator)
                    {
                        handler.setDocumentLocator(locator);
                    }
                    
                    public void startDocument() throws SAXException
                    {
                        handler.startDocument();
                    }
                    
                    public void startElement(String name, AttributeList atts) throws SAXException
                    {
                        // filter menu-element noded within menu definition
                        if ((menuDepth == 0) || !name.equals("menu-element"))
                        {
                            handler.startElement(name, atts);
                        }

                        // track menu depth
                        if (name.equals("menu"))
                        {
                            menuDepth++;
                        }
                    }
                });
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

    protected Object unmarshallDocument( Class clazz, String path, String extension ) throws DocumentNotFoundException,
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

        try
        {
            // unmarshal: use SAX I parser to read document XML, filtering
            // for page and folder menu definition menu elements ordered
            // polymorphic collection to insert artifical <menu-element>
            // tags enabling Castor XML binding; see JETSPEED-INF/castor/page-mapping.xml
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            final XMLReaderAdapter readerAdapter = new XMLReaderAdapter(reader);
            final InputSource readerInput = new InputSource(new InputStreamReader(new FileInputStream(f), PSML_DOCUMENT_ENCODING));
            Unmarshaller unmarshaller = new Unmarshaller(this.mapping);
            document = (Document) unmarshaller.unmarshal(new EventProducer()
                {
                    public void setDocumentHandler(final DocumentHandler handler)
                    {
                        readerAdapter.setDocumentHandler(new DocumentHandler()
                            {
                                private int menuDepth = 0;

                                public void characters(char[] ch, int start, int length) throws SAXException
                                {
                                    handler.characters(ch, start, length);
                                }

                                public void endDocument() throws SAXException
                                {
                                    handler.endDocument();
                                }

                                public void endElement(String name) throws SAXException
                                {
                                    // always include all elements
                                    handler.endElement(name);

                                    // track menu depth and insert menu-element nodes
                                    // to encapsulate menu elements to support collection
                                    // polymorphism in Castor
                                    if (name.equals("menu"))
                                    {
                                        menuDepth--;
                                        if (menuDepth > 0)
                                        {
                                            handler.endElement("menu-element");
                                        }
                                    }
                                    else if ((menuDepth > 0) &&
                                             (name.equals("options") || name.equals("separator") ||
                                              name.equals("include") || name.equals("exclude")))
                                    {
                                        handler.endElement("menu-element");
                                    }
                                }

                                public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
                                {
                                    handler.ignorableWhitespace(ch, start, length);
                                }

                                public void processingInstruction(String target, String data) throws SAXException
                                {
                                    handler.processingInstruction(target, data);
                                }

                                public void setDocumentLocator(Locator locator)
                                {
                                    handler.setDocumentLocator(locator);
                                }

                                public void startDocument() throws SAXException
                                {
                                    handler.startDocument();
                                }

                                public void startElement(String name, AttributeList atts) throws SAXException
                                {
                                    // track menu depth and insert menu-element nodes
                                    // to encapsulate menu elements to support collection
                                    // polymorphism in Castor
                                    if (name.equals("menu"))
                                    {
                                        if (menuDepth > 0)
                                        {
                                            handler.startElement("menu-element", null);
                                        }
                                        menuDepth++;
                                    }
                                    else if ((menuDepth > 0) &&
                                             (name.equals("options") || name.equals("separator") ||
                                              name.equals("include") || name.equals("exclude")))
                                    {
                                        handler.startElement("menu-element", null);
                                    }

                                    // always include all elements
                                    handler.startElement(name, atts);
                                }
                            });
                    }
                    public void start() throws SAXException
                    {
                        try
                        {
                            readerAdapter.parse(readerInput);
                        }
                        catch (IOException ioe)
                        {
                            throw new SAXException(ioe);
                        }
                    }
                });

            document.setPath(path);
            AbstractBaseElement documentImpl = (AbstractBaseElement)document;
            documentImpl.setHandlerFactory(handlerFactory);
            documentImpl.setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
            documentImpl.setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
            documentImpl.unmarshalled();
        }
        catch (IOException e)
        {
        	log.error("Could not load the file " + f.getAbsolutePath(), e);
            throw new PageNotFoundException("Could not load the file " + f.getAbsolutePath(), e);
        }
        catch (MarshalException e)
        {
        	log.error("Could not unmarshal the file " + f.getAbsolutePath(), e);
            throw new PageNotFoundException("Could not unmarshal the file " + f.getAbsolutePath(), e);
        }
        catch (MappingException e)
        {
        	log.error("Could not unmarshal the file " + f.getAbsolutePath(), e);
            throw new PageNotFoundException("Could not unmarshal the file " + f.getAbsolutePath(), e);
        }
        catch (ValidationException e)
        {
        	log.error("Document " + f.getAbsolutePath() + " is not valid", e);
            throw new DocumentNotFoundException("Document " + f.getAbsolutePath() + " is not valid", e);
        }
        catch (SAXException e)
        {
        	log.error("Could not unmarshal the file " + f.getAbsolutePath(), e);
            throw new PageNotFoundException("Could not unmarshal the file " + f.getAbsolutePath(), e);
        }
        catch (ParserConfigurationException e)
        {
        	log.error("Could not unmarshal the file " + f.getAbsolutePath(), e);
            throw new PageNotFoundException("Could not unmarshal the file " + f.getAbsolutePath(), e);
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
        // sanity checks
        if (document == null)
        {
            log.warn("Recieved null Document to remove");
            return;
        }
        String path = document.getPath();
        if (path == null)
        {
            path = document.getId();
            if (path == null)
            {
                log.warn("Recieved Document with null path/id to remove");
                return;
            }
        }

        // remove page from disk
        String fileName = path;        
        if (!fileName.endsWith(this.documentType))
        {
            fileName = path + this.documentType;
        }
        File file = new File(this.documentRootDir, fileName);
        if (!file.delete())
        {
            throw new FailedToDeleteDocumentException(file.getAbsolutePath()+" document cannot be deleted.");
        }

        // remove from cache
        fileCache.remove(path);

        // reset document
        AbstractNode documentImpl = (AbstractNode)document;
        documentImpl.setParent(null);
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
    public Document getDocument( String name, boolean fromCache ) throws DocumentNotFoundException, NodeException
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
     * @param path
     * @param objectToCache
     */
    protected void addToCache( String path, Object objectToCache )
    {
        synchronized (fileCache)
        {
            // store the document in the hash and reference it to the
            // watcher
            try
            {
                fileCache.put(path, objectToCache, this.documentRootDir);

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

        if (entry.getDocument() instanceof Document && ((Document) entry.getDocument()).getPath().endsWith(documentType))
        {
            Document document = (Document) entry.getDocument();
            Document freshDoc = getDocument(document.getPath(), false);
            Node parent = ((AbstractNode)document).getParent(false);
 
            freshDoc.setParent(parent);
            if(parent instanceof FolderImpl)
            {
                FolderImpl folder = (FolderImpl) parent;
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

    /**
     * <p>
     * getHandlerFactory
     * </p>
     *
     * @see org.apache.jetspeed.page.document.DocumentHandler#getHandlerFactory()
     * @return
     */
    public DocumentHandlerFactory getHandlerFactory()
    {
        return handlerFactory;
    }

    /**
     * <p>
     * setHandlerFactory
     * </p>
     *
     * @see org.apache.jetspeed.page.document.DocumentHandler#setHandlerFactory(org.apache.jetspeed.page.document.DocumentHandlerFactory)
     * @param factory
     */
    public void setHandlerFactory(DocumentHandlerFactory factory)
    {
        this.handlerFactory = factory;
    }

}
