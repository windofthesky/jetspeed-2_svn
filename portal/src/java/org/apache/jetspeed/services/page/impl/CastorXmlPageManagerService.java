/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.jetspeed.services.page.impl;

//standard java stuff
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.file.FileCacheEntry;
import org.apache.jetspeed.cache.file.FileCacheEventListener;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Property;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;
import org.apache.jetspeed.om.page.psml.PropertyImpl;
import org.apache.jetspeed.services.idgenerator.JetspeedIdGenerator;
import org.apache.jetspeed.services.page.PageManagerService;
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
 * This service is responsible for loading and saving PSML pages
 * serialized to disk
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class CastorXmlPageManagerService extends BaseCommonService implements FileCacheEventListener, PageManagerService
{
    // configuration keys
    protected final static String CONFIG_ROOT = "root";
    protected final static String CONFIG_EXT = "ext";
    protected final static String CONFIG_SCAN_RATE = "scanRate";
    protected final static String CONFIG_CACHE_SIZE = "cacheSize";

    // default configuration values
    public final static String DEFAULT_ROOT = "/WEB-INF/pages";
    public final static String DEFAULT_EXT = ".psml";

    // the root psml resource directory
    protected String root;
    // base store directory
    protected File rootDir = null;
    // file extension
    protected String ext;

    /** The pages loaded by this manager */
    protected FileCache pages = null;

    /** the output format for pretty printing when saving registries */
    protected OutputFormat format = null;

    /** the base refresh rate for pages */
    protected long scanRate = 1000 * 60; // every minute

    /** the default cache size */
    protected int cacheSize = 100;

    // castor mapping
    public static final String DEFAULT_MAPPING = "page-mapping.xml";
    protected String mapFile = null;

    /** the Castor mapping file name */
    protected Mapping mapping = null;

    private final static Log log = LogFactory.getLog(CastorXmlPageManagerService.class);

    /**
     * This is the early initialization method called by the
     * Turbine <code>Service</code> framework
     */
    public void init() throws CPSInitializationException
    {
        if (isInitialized())
        {
            return;
        }

        // get the PSML Root Directory
        this.root = getConfiguration().getString(CONFIG_ROOT, DEFAULT_ROOT);
        this.rootDir = new File(root);

        //If the rootDir does not exist, treat it as context relative
        if (!rootDir.exists())
        {
            try
            {
                this.rootDir = new File(Jetspeed.getRealPath(root));
            }
            catch (Exception e)
            {
                // this.rootDir = new File("./webapp" + this.rootDir.toString());
            }
        }
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

        // get default extension
        this.ext = getConfiguration().getString(CONFIG_EXT, DEFAULT_EXT);

        // create the serializer output format
        this.format = new OutputFormat();
        format.setIndenting(true);
        format.setIndent(4);

        // psml castor mapping file
        mapFile = getConfiguration().getString("mapping", DEFAULT_MAPPING);
        mapFile = Jetspeed.getRealPath(mapFile);
        loadMapping();

        this.scanRate = getConfiguration().getLong(CONFIG_SCAN_RATE, this.scanRate);
        this.cacheSize = getConfiguration().getInt(CONFIG_CACHE_SIZE, this.cacheSize);

        pages = new FileCache(this.scanRate, this.cacheSize);
        pages.addListener(this);
        pages.startFileScanner();

        //Mark that we are done
        setInit(true);
    }

    /**
     * This is the shutdown method called by the
     * Turbine <code>Service</code> framework
     */
    public void shutdown()
    {
        pages.stopFileScanner();
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#newPage()
     */
    public Page newPage()
    {
        PageImpl p = new PageImpl();
        p.setId(JetspeedIdGenerator.getNextPeid());
        FragmentImpl f = new FragmentImpl();
        f.setId(JetspeedIdGenerator.getNextPeid());
        f.setType(Fragment.LAYOUT);
        p.setRootFragment(f);

        return p;
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#newFragment()
     */
    public Fragment newFragment()
    {
        FragmentImpl f = new FragmentImpl();
        f.setId(JetspeedIdGenerator.getNextPeid());
        f.setType(Fragment.LAYOUT);

        return f;
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#newProperty()
     */
    public Property newProperty()
    {
        return new PropertyImpl();
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#getPage(java.lang.String)
     */
    public Page getPage(String id)
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
            File f = new File(this.rootDir, id + this.ext);

            if (!f.exists())
            {
                return null;
            }

            FileReader reader = null;

            try
            {
                reader = new FileReader(f);
                Unmarshaller unmarshaller = new Unmarshaller(this.mapping);
                page = (Page) unmarshaller.unmarshal(reader);
            }
            catch (IOException e)
            {
                log.error("Could not load the file " + f.getAbsolutePath(), e);
                page = null;
            }
            catch (MarshalException e)
            {
                log.error("Could not unmarshal the file " + f.getAbsolutePath(), e);
                page = null;
            }
            catch (MappingException e)
            {
                log.error("Could not unmarshal the file " + f.getAbsolutePath(), e);
                page = null;
            }
            catch (ValidationException e)
            {
                log.error("Document " + f.getAbsolutePath() + " is not valid", e);
                page = null;
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

            synchronized (pages)
            {
                // store the document in the hash and reference it to the watcher
                try
                {
                    pages.put(id, page);
                }
                catch (java.io.IOException e)
                {
                    log.error("Error putting document: " + e);
                }
            }
        }

        return page;
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#listPages()
     */
    public List listPages()
    {
        ArrayList results = new ArrayList();
        File[] files = this.rootDir.listFiles(new FilenameFilter()
        {
            public boolean accept(File dir, String file)
            {
                return file.endsWith(CastorXmlPageManagerService.this.ext);
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
    public void registerPage(Page page) throws JetspeedException
    {
        // snaity checks
        if (page == null)
        {
            log.warn("Recieved null page to register");
            return;
        }

        String id = page.getId();

        if (id == null)
        {
            page.setId(JetspeedIdGenerator.getNextPeid());
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
    public void updatePage(Page page) throws JetspeedException
    {
        registerPage(page);
    }

    /**
     * @see org.apache.jetspeed.services.page.PageManagerService#removePage(org.apache.jetspeed.om.page.Page)
     */
    public void removePage(Page page)
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

    protected void loadMapping() throws CPSInitializationException
    {
        // test the mapping file and create the mapping object

        if (mapFile != null)
        {
            File map = new File(mapFile);
            if (log.isDebugEnabled())
            {
                log.debug("Loading psml mapping file " + mapFile);
            }
            if (map.exists() && map.isFile() && map.canRead())
            {
                try
                {
                    mapping = new Mapping();
                    InputSource is = new InputSource(new FileReader(map));
                    is.setSystemId(mapFile);
                    mapping.loadMapping(is);
                }
                catch (Exception e)
                {
                    log.error("Error in psml mapping creation", e);
                    throw new CPSInitializationException("Error in mapping", e);
                }
            }
            else
            {
                throw new CPSInitializationException("PSML Mapping not found or not a file or unreadable: " + mapFile);
            }
        }
    }

    /**
     * Refresh event, called when the entry is being refreshed from file system.
     *
     * @param entry the entry being refreshed.
     */
    public void refresh(FileCacheEntry entry)
    {
        log.debug("Entry is refreshing: " + entry.getFile().getName());
    }

    /**
     * Evict event, called when the entry is being evicted out of the cache
     *
     * @param entry the entry being refreshed.
     */
    public void evict(FileCacheEntry entry)
    {
        log.debug("Entry is evicting: " + entry.getFile().getName());
    }
}