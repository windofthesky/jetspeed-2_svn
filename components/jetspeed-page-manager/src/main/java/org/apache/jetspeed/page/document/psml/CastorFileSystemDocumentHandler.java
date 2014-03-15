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

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.file.FileCacheEntry;
import org.apache.jetspeed.cache.file.FileCacheEventListener;
import org.apache.jetspeed.idgenerator.IdGenerator;
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
import org.castor.mapping.BindingType;
import org.castor.mapping.MappingUnmarshaller;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.mapping.MappingLoader;
import org.exolab.castor.xml.ClassDescriptorResolver;
import org.exolab.castor.xml.ClassDescriptorResolverFactory;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.SAX2EventProducer;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLClassDescriptorResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

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
    private final static Logger log = LoggerFactory.getLogger(CastorFileSystemDocumentHandler.class);

    private final static String PSML_DOCUMENT_ENCODING = "UTF-8";

    protected IdGenerator generator;
    protected String documentType;
    protected Class expectedReturnType;
    protected String documentRoot;
    protected File documentRootDir;
    protected FileCache fileCache;
    
    private OutputFormat format;
    private final XMLReader xmlReader;
    private DocumentHandlerFactory handlerFactory;
    private ClassDescriptorResolver classDescriptorResolver;

    /**
     * 
     * @param generator
     *            id generator for unmarshalled documents
     * @param mappingFile
     *            Castor mapping file. THe mapping file must be in the class
     *            path
     * @param documentType
     * @param expectedReturnType
     * @throws FileNotFoundException
     */
    public CastorFileSystemDocumentHandler( IdGenerator generator, String mappingFile, String documentType, Class expectedReturnType,
            String documentRoot, FileCache fileCache ) throws FileNotFoundException,SAXException,ParserConfigurationException, MappingException
    {
        super();
        this.generator = generator;
        this.documentType = documentType;
        this.expectedReturnType = expectedReturnType;
        this.documentRoot = documentRoot;
        this.documentRootDir = new File(documentRoot);
        verifyPath(documentRootDir);
        this.fileCache = fileCache;
        this.fileCache.addListener(this);
        this.format = new OutputFormat("    ", true, PSML_DOCUMENT_ENCODING);
        this.format.setXHTML(true);
        this.format.setExpandEmptyElements(false);

        String javaVersion = System.getProperty("java.version");
        if ((javaVersion.startsWith("1.3.") || javaVersion.startsWith("1.4.")) && (System.getProperty("org.xml.sax.driver") == null))
        {
            System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
            log.info("SAX driver configured: "+System.getProperty("org.xml.sax.driver"));
        }        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        
        xmlReader = parser.getXMLReader();
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", false);
        
        /*
         * Create ClassDescripterResolver for better performance. 
         * Mentioned as 'best practice' on the Castor website.
         */
        createCastorClassDescriptorResolver(mappingFile);
    }
    
    public CastorFileSystemDocumentHandler( IdGenerator generator, String mappingFile, String documentType, String expectedReturnType,
            String documentRoot, FileCache fileCache ) throws FileNotFoundException, ClassNotFoundException,SAXException,ParserConfigurationException, MappingException
    {
        this(generator, mappingFile, documentType, Class.forName(expectedReturnType), documentRoot, fileCache);
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
    
    public void updateDocument( Document document ) throws FailedToUpdateDocumentException
    {
    	updateDocument(document, false);
    }
    
    /**
     * <p>
     * updateDocument
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.DocumentHandler#updateDocument(org.apache.jetspeed.om.page.Document)
     * @param document
     * @param systemUpdate 
     */
    protected void updateDocument( Document document, boolean systemUpdate) throws FailedToUpdateDocumentException
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
        String absolutePath = "";
        Writer writer = null;        
        try
        {
            // JS2-903: move try up to ensure no backdoors to disabling security        
            if (systemUpdate)
            {
            	// on system update: temporarily turn off security
                documentImpl.setPermissionsEnabled(false);
                documentImpl.setConstraintsEnabled(false);
            } 
            else 
            {
                try
                {
                    // JS2-903: fragments are getting stripped out on write if the current user does not have edit access to write to the file
                    document.checkAccess(JetspeedActions.EDIT);
                }
                catch (SecurityException se)
                {
                    throw new FailedToUpdateDocumentException("Insufficient Access: no edit access, cannot write.");
                }
                documentImpl.setPermissionsEnabled(false);
                documentImpl.setConstraintsEnabled(false);            
            }
            documentImpl.marshalling();            
            // marshal page to disk
            String fileName = path;        
            if (!fileName.endsWith(this.documentType))
            {
                fileName = path + this.documentType;
            }
            File f = new File(this.documentRootDir, fileName);
            absolutePath = f.getAbsolutePath();            
            // marshal: use SAX II handler to filter document XML for
            // page and folder menu definition menu elements ordered
            // polymorphic collection to strip artifical <menu-element>
            // and <fragment-element> tags enabling Castor XML binding;
            // see JETSPEED-INF/castor/page-mapping.xml
            writer = new OutputStreamWriter(new FileOutputStream(f), PSML_DOCUMENT_ENCODING);
            XMLWriter xmlWriter = new XMLWriter(writer, this.format);
            final ContentHandler handler = xmlWriter;
            
            Marshaller marshaller = new Marshaller(new ContentHandler()
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
                    
					public void endElement(String uri, String localName, String qName) throws SAXException {
                        // track menu depth
                        if (qName.equals("menu"))
                        {
                            menuDepth--;
                        }

                        // filter menu-element nodes within menu definition and fragment-element nodes
                        if (((menuDepth == 0) || !qName.equals("menu-element")) && !qName.equals("fragment-element"))
                        {
                            handler.endElement(uri, localName, qName);
                        }
					}

					public void endPrefixMapping(String prefix) throws SAXException {
					}

					public void skippedEntity(String name) throws SAXException {
						handler.skippedEntity(name);
					}

					public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                        // filter menu-element nodes within menu definition and fragment-element nodes
                        if (((menuDepth == 0) || !qName.equals("menu-element")) && !qName.equals("fragment-element"))
                        {
                            handler.startElement(uri,localName, qName, atts);
                        }

                        // track menu depth
                        if (qName.equals("menu"))
                        {
                            menuDepth++;
                        }
					}

					public void startPrefixMapping(String prefix, String uri) throws SAXException {
					}
                });
            marshaller.setResolver((XMLClassDescriptorResolver) classDescriptorResolver);
            
            marshaller.setValidation(false); // results in better performance
            marshaller.marshal(document);
        }
        catch (MarshalException e)
        {
            log.error("Could not marshal the file " + absolutePath, e);
            throw new FailedToUpdateDocumentException(e);
        }
        catch (ValidationException e)
        {
            log.error("Document " + absolutePath + " is not valid", e);
            throw new FailedToUpdateDocumentException(e);
        }
        catch (IOException e)
        {
            log.error("Could not save the file " + absolutePath, e);
            throw new FailedToUpdateDocumentException(e);
        }
        catch (Exception e)
        {
            log.error("Error while saving  " + absolutePath, e);
            throw new FailedToUpdateDocumentException(e);
        }
        finally
        {
        	// restore permissions / constraints
        	documentImpl.setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
            documentImpl.setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
        	try
            {
        	    if (writer != null)
        	        writer.close();
            }
            catch (IOException e)
            {
            }
        }

    }

    protected void createCastorClassDescriptorResolver(String mappingFile) throws MappingException
    {
    	Mapping mapping=null;
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
        this.classDescriptorResolver =
 		   ClassDescriptorResolverFactory.createClassDescriptorResolver(BindingType.XML);
 		MappingUnmarshaller mappingUnmarshaller = new MappingUnmarshaller();
 		MappingLoader mappingLoader = mappingUnmarshaller.getMappingLoader(mapping, BindingType.XML);
 		classDescriptorResolver.setMappingLoader(mappingLoader);
    }

    protected Object unmarshallDocument(Class<?> clazz, String path, String extension ) throws DocumentNotFoundException,
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
            // unmarshal: use SAX II parser to read document XML, filtering
            // for page and folder menu definition menu elements ordered
            // polymorphic collection to insert artifical <menu-element>
            // and <fragment-element> tags enabling Castor XML binding;
            // see JETSPEED-INF/castor/page-mapping.xml
            
            final InputSource readerInput = new InputSource(new InputStreamReader(new FileInputStream(f), PSML_DOCUMENT_ENCODING));
            Unmarshaller unmarshaller = new Unmarshaller();
            unmarshaller.setResolver((XMLClassDescriptorResolver) classDescriptorResolver);            
            unmarshaller.setValidation(false); // results in better performance
            
            synchronized (xmlReader)
            {
                document = (Document) unmarshaller.unmarshal(new SAX2EventProducer()
                    {
                        public void setContentHandler(final ContentHandler handler)
                        {
                        	xmlReader.setContentHandler(new ContentHandler()
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

    								public void endElement(String uri, String localName, String qName) throws SAXException
                                    {
                                        // always include all elements
                                        handler.endElement(uri,localName,qName);

                                        // track menu depth and insert menu-element nodes
                                        // to encapsulate menu elements to support collection
                                        // polymorphism in Castor
                                        if (qName.equals("menu"))
                                        {
                                            menuDepth--;
                                            if (menuDepth > 0)
                                            {
                                                handler.endElement(null,null,"menu-element");
                                            }
                                        }
                                        else if ((menuDepth > 0) &&
                                                 (qName.equals("options") || qName.equals("separator") ||
                                                  qName.equals("include") || qName.equals("exclude")))
                                        {
                                            handler.endElement(null,null,"menu-element");
                                        }
                                        // insert fragment-element nodes to encapsulate
                                        // fragment elements to support collection
                                        // polymorphism in Castor
                                        if (qName.equals("fragment") || qName.equals("fragment-reference") || qName.equals("page-fragment"))
                                        {
                                            handler.endElement(null,null,"fragment-element");
                                        }
                                    }

    								public void endPrefixMapping(String prefix) throws SAXException
                                    {
    								}

    								public void skippedEntity(String name) throws SAXException
                                    {
    									handler.skippedEntity(name);
    								}

    								public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
                                    {
                                        // track menu depth and insert menu-element nodes
                                        // to encapsulate menu elements to support collection
                                        // polymorphism in Castor    									
    									if (qName.equals("menu"))
                                        {
                                            if (menuDepth > 0)
                                            {
                                                handler.startElement(null,null,"menu-element", null);
                                            }
                                            menuDepth++;
                                        }
                                        else if ((menuDepth > 0) &&
                                                 (qName.equals("options") || qName.equals("separator") ||
                                                  qName.equals("include") || qName.equals("exclude")))
                                        {
                                            handler.startElement(null,null,"menu-element", null);
                                        }
                                        // insert fragment-element nodes to encapsulate
                                        // fragment elements to support collection
                                        // polymorphism in Castor
                                        if (qName.equals("fragment") || qName.equals("fragment-reference") || qName.equals("page-fragment"))
                                        {
                                            handler.startElement(null,null,"fragment-element",null);
                                        }

                                        // always include all elements
                                        handler.startElement(null,null, qName, atts);
    								}

    								public void startPrefixMapping(String prefix, String uri) throws SAXException
                                    {
    								}
                                });
                        }
                        public void start() throws SAXException
                        {
                            try
                            {
                            	xmlReader.parse(readerInput);
                            }
                            catch (IOException ioe)
                            {
                                throw new SAXException(ioe);
                            }
                        }
                    });
            }
            
            document.setPath(path);
            AbstractBaseElement documentImpl = (AbstractBaseElement)document;
            documentImpl.setHandlerFactory(handlerFactory);
            documentImpl.setPermissionsEnabled(handlerFactory.getPermissionsEnabled());
            documentImpl.setConstraintsEnabled(handlerFactory.getConstraintsEnabled());
            boolean dirty = documentImpl.unmarshalled(generator);
            if (dirty || document.isDirty()){
                updateDocument(document, true);
                document.setDirty(false);
            }
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
        catch (ValidationException e)
        {
        	log.error("Document " + f.getAbsolutePath() + " is not valid", e);
            throw new DocumentNotFoundException("Document " + f.getAbsolutePath() + " is not valid", e);
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
     * @param fromCache
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
            Node parent = ((AbstractNode)document).getParent(false);
            if (parent instanceof FolderImpl)
            {
                Document freshDoc = getDocument(document.getPath(), false);
                freshDoc.setParent(parent);
                ((FolderImpl)parent).getAllNodes().add(freshDoc);
                freshDoc.setPath(document.getPath());
                entry.setDocument(freshDoc);
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.DocumentHandler#shutdown()
     */
    public void shutdown()
    {
        // disconnect cache listener
        fileCache.removeListener(this);
    }    
}
