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

package org.apache.jetspeed.page.impl;

//standard java stuff
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.file.FileCacheEntry;
import org.apache.jetspeed.cache.file.FileCacheEventListener;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.impl.FolderImpl;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageNotFoundException;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.mapping.MappingException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.picocontainer.Startable;
import org.xml.sax.InputSource;

/**
 * This service is responsible for loading and saving PSML pages serialized to
 * disk
 * 
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta </a>
 * @author <a href="mailto:weaver@apache.org">Scott T Weaver</a>
 * @version $Id$
 */
public class CastorXmlPageManager extends AbstractPageManager implements FileCacheEventListener, PageManager, Startable
{
    private final static Log log = LogFactory.getLog(CastorXmlPageManager.class);

    // configuration keys
    protected final static String CONFIG_ROOT = "root";
    protected final static String CONFIG_EXT = "ext";
    protected final static String CONFIG_SCAN_RATE = "scanRate";
    protected final static String CONFIG_CACHE_SIZE = "cacheSize";

    // default configuration values

    // the root psml resource directory
    protected String root;
    // base store directory
    protected File rootDir = null;
    // file extension
    protected String ext = PAGE_SUFFIX;

    /** The pages loaded by this manager */
    protected FileCache pages = null;

    /** the output format for pretty printing when saving registries */
    protected OutputFormat format = null;

    // castor mapping
    protected String mapFileResource = "META-INF/page-mapping.xml";

    /** the Castor mapping file name */
    protected Mapping mapping = null;

    public CastorXmlPageManager( IdGenerator generator, FileCache fileCache, String root ) throws FileNotFoundException
    {
        super(generator);
        this.rootDir = new File(root);
        verifyPath(rootDir);
        this.pages = fileCache;
    }

    public CastorXmlPageManager( IdGenerator generator, FileCache fileCache, String root, List modelClasses )
            throws FileNotFoundException
    {
        super(generator, modelClasses);
        this.rootDir = new File(root);
        verifyPath(rootDir);
        this.pages = fileCache;
    }

    public CastorXmlPageManager(IdGenerator generator,
                                FileCache fileCache, 
                                String root,                                        
                                List modelClasses,
                                String extension) throws FileNotFoundException 
                                       
    {
        this(generator, fileCache, root, modelClasses);
        this.ext = extension;
    }

    public void start()
    {

        //If it is still missing, try to create it
        if (!rootDir.exists())
        {
            try
            {
                rootDir.mkdirs();
            }
            catch (Exception e)
            {
            }
        }

        // create the serializer output format
        this.format = new OutputFormat();
        format.setIndenting(true);
        format.setIndent(4);

        // psml castor mapping file
        loadMapping();

        pages.addListener(this);
        pages.startFileScanner();

    }

    public void stop()
    {
        pages.stopFileScanner();
    }

    /**
     * 
     * <p>
     * getPage
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getPage(org.apache.jetspeed.profiler.ProfileLocator)
     * @param locator
     * @return @throws
     *         PageNotFoundException
     */
    public Page getPage( ProfileLocator locator ) throws PageNotFoundException
    {
        return getPage(locator.getValue("page"));
    }

    /**
     * 
     * <p>
     * getPage
     * </p>
     * 
     * @see org.apache.jetspeed.page.PageManager#getPage(java.lang.String)
     * @param id
     * @return @throws
     *         PageNotFoundException
     * @throws IllegalStateException
     *             if the page could be inserted into the FileCache.
     */
    public Page getPage( String id ) throws PageNotFoundException
    {
        if (id == null)
        {
            String message = "PageManager: Must specify an id";
            log.error(message);
            throw new IllegalArgumentException(message);
        }

        if (log.isDebugEnabled())
        {
            log.debug("Asked for PageID=" + id);
        }

        Page page = null;

        page = (Page) pages.getDocument(id);

        if (page == null)
        {
            File f = null;
            if (id.endsWith(this.ext))
            {
                f = new File(this.rootDir, id);
            }
            else
            {
                f = new File(this.rootDir, id + this.ext);
            }

            if (!f.exists())
            {
                throw new PageNotFoundException("Jetspeed PSML page not found: " + id);
            }

            FileReader reader = null;

            try
            {
                reader = new FileReader(f);
                Unmarshaller unmarshaller = new Unmarshaller(this.mapping);
                page = (Page) unmarshaller.unmarshal(reader);
                page.setId(id);

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
                throw new PageNotFoundException("Document " + f.getAbsolutePath() + " is not valid", e);
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

            if (page == null)
            {
                throw new PageNotFoundException("Page not found: " + id);
            }

            synchronized (pages)
            {
                // store the document in the hash and reference it to the
                // watcher
                try
                {
                    pages.put(id, page);
                    int lastSlash = id.indexOf("/");
                    if (lastSlash > -1)
                    {
                        page.setParent(getFolder(id.substring(0, lastSlash)));
                    }
                    else
                    {
                        page.setParent(getFolder("/"));
                    }
                }
                catch (java.io.IOException e)
                {
                    log.error("Error putting document: " + e);
                    IllegalStateException ise = new IllegalStateException("Error storing Page in the FileCache: "
                            + e.toString());
                    ise.initCause(e);
                }
            }
        }

        return page;
    }

    public Folder getFolder( String folderPath ) throws IOException
    {
        Folder folder = (Folder) pages.getDocument(folderPath);

        if (folder == null)
        {
            File f = new File(this.rootDir, folderPath);

            if (f.exists())
            {
                folder = new FolderImpl(f, folderPath, this );
                pages.put(folderPath, folder);

            }
        }
        return folder;
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#listPages()
     */
    public List listPages()
    {
        ArrayList results = new ArrayList();
        File[] files = this.rootDir.listFiles(new FilenameFilter()
        {
            public boolean accept( File dir, String file )
            {
                return file.endsWith(CastorXmlPageManager.this.ext);
            }
        });

        for (int i = 0; i < files.length; i++)
        {
            String id = files[i].getName().substring(0, files[i].getName().length() - this.ext.length());
            results.add(id);
        }

        return results;
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#registerPage(org.apache.jetspeed.om.page.Page)
     */
    public void registerPage( Page page ) throws JetspeedException
    {
        // sanity checks
        if (page == null)
        {
            log.warn("Recieved null page to register");
            return;
        }

        String id = page.getId();

        if (id == null)
        {
            page.setId(generator.getNextPeid());
            id = page.getId();
            log.warn("Page with no Id, created new Id : " + id);
        }

        // marshal page to disk
        File f = new File(this.rootDir, id + this.ext);
        FileWriter writer = null;

        try
        {
            writer = new FileWriter(f);
            Serializer serializer = new XMLSerializer(writer, this.format);
            Marshaller marshaller = new Marshaller(serializer.asDocumentHandler());
            marshaller.setMapping(this.mapping);
            marshaller.marshal(page);
        }
        catch (MarshalException e)
        {
            log.error("Could not marshal the file " + f.getAbsolutePath(), e);
            throw new JetspeedException(e);
        }
        catch (MappingException e)
        {
            log.error("Could not marshal the file " + f.getAbsolutePath(), e);
            throw new JetspeedException(e);
        }
        catch (ValidationException e)
        {
            log.error("Document " + f.getAbsolutePath() + " is not valid", e);
            throw new JetspeedException(e);
        }
        catch (IOException e)
        {
            log.error("Could not save the file " + f.getAbsolutePath(), e);
            throw new JetspeedException(e);
        }
        catch (Exception e)
        {
            log.error("Error while saving  " + f.getAbsolutePath(), e);
            throw new JetspeedException(e);
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

        // update it in cache
        synchronized (pages)
        {
            try
            {
                pages.put(id, page);
            }
            catch (IOException e)
            {
                log.error("Error storing document: " + e);
            }
        }
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#updatePage(org.apache.jetspeed.om.page.Page)
     */
    public void updatePage( Page page ) throws JetspeedException
    {
        registerPage(page);
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage( Page page )
    {
        String id = page.getId();

        if (id == null)
        {
            log.warn("Unable to remove page with null Id from disk");
            return;
        }

        File file = new File(this.rootDir, id + this.ext);

        synchronized (pages)
        {
            pages.remove(id);
        }

        file.delete();

    }

    protected void loadMapping()
    {
        try
        {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(mapFileResource);

            if (log.isDebugEnabled())
            {
                log.debug("Loading psml mapping file " + mapFileResource);
            }

            mapping = new Mapping();

            InputSource is = new InputSource(stream);
            is.setSystemId(mapFileResource);

            mapping.loadMapping(is);
        }
        catch (Exception e)
        {
            log.error("Error in psml mapping creation", e);
        }

    }

    /**
     * Refresh event, called when the entry is being refreshed from file system.
     * 
     * @param entry
     *            the entry being refreshed.
     */
    public void refresh( FileCacheEntry entry )
    {
        log.debug("Entry is refreshing: " + entry.getFile().getName());
    }

    /**
     * Evict event, called when the entry is being evicted out of the cache
     * 
     * @param entry
     *            the entry being refreshed.
     */
    public void evict( FileCacheEntry entry )
    {
        log.debug("Entry is evicting: " + entry.getFile().getName());
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
}