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

package org.apache.jetspeed.services.registry.impl;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.om.registry.Registry;
import org.apache.jetspeed.om.registry.RegistryEntry;
import org.apache.jetspeed.om.registry.RegistryException;
import org.apache.jetspeed.om.registry.base.BaseRegistry;
import org.apache.jetspeed.om.registry.base.LocalRegistry;
import org.apache.jetspeed.services.registry.RegistryService;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * <p>This is an implementation of the <code>RegistryService</code>
 * based on the Castor XML serialization mechanisms</p>
 * <p>This registry aggregates multiple RegistryFragment to store the regsistry
 * entries</p>
 *
 * <p>This service expects the following properties to be set for correct operation:
 * <dl>
 *    <dt>directory</dt><dd>The directory where the Registry will look for
 *    fragment files</dd>
 *    <dt>extension</dt><dd>The extension used for identifying the registry fragment
 *    files. Default .xreg</dd>
 *    <dt>mapping</dt><dd>the Castor object mapping file path</dd>
 *    <dt>registries</dt><dd>a comma separated list of registry names to load
 *     from this file</dd>
 *    <dt>refreshRate</dt><dd>Optional. The manager will check every
 *     refreshRate seconds if the config has changed and if true will refresh
 *     all the registries. A value of 0 or negative will disable the
 *     automatic refresh operation. Default: 300 (5 minutes)</dd>
 *    <dt>verbose</dt><dd>Optional. Control the amount of debug output. The bigger
 *    the more output, you've been warned ! Default: 0</dd>
 * </dl>
 * </p>
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @version $Id$
 */
public class CastorRegistryService extends BaseService
implements RegistryService, FileRegistry
{
    protected final static Log log =
    LogFactory.getLog(CastorRegistryService.class);
    
    public static final int DEFAULT_VERBOSE = 0;
    public static final int DEFAULT_REFRESH = 300;
    public static final String DEFAULT_EXTENSION = ".xreg";
    public static final String DEFAULT_MAPPING = "${webapp}/WEB-INF/conf/mapping.xml";
    
    /** controls amount of debug output, the bigger the more output will be generated */
    protected int verbose = DEFAULT_VERBOSE;
    
    /** regsitry type keyed list of entries */
    protected Hashtable registries = new Hashtable();
    
    /** The Castor generated RegsitryFragment objects */
    protected Hashtable fragments = new Hashtable();
    
    /** The list of default fragments stores for newly created objects */
    protected Hashtable defaults = new Hashtable();
    
    /** Associates entries with their fragments name for quick lookup */
    protected Hashtable entryIndex = new Hashtable();
    
    /** the Watcher object which monitors the regsitry directory */
    protected RegistryWatcher watcher = null;
    
    /** the Castor mapping file name */
    protected Mapping mapping = null;
    
    /** the output format for pretty printing when saving registries */
    protected OutputFormat format = null;
    
    /** the base regsitry directory */
    protected String directory = null;
    
    /** the extension for registry files */
    protected String extension = null;
    
    public static final String CONFIG_DIRECTORY = "directory";
    
    public static final String CONFIG_MAPPING = "mapping";
    
    public static final String CONFIG_EXTENSION = "extention";
    
    public static final String CONFIG_REFRESH = "refreshRate";
    
    public static final String CONFIG_VERBOSE = "verbose";
    
    /**
     * Returns a Registry object for further manipulation
     *
     * @param regName the name of the registry to fetch
     * @return a Registry object if found by the manager or null
     */
    public Registry get(String regName)
    {
        return (Registry) registries.get(regName);
    }
    
    /**
     *  List all the registry currently available to this service
     *
     * @return an Enumeration of registry names.
     */
    public Enumeration getNames()
    {
        return registries.keys();
    }
    
    /**
     * Creates a new RegistryEntry instance compatible with the current
     * Registry instance implementation
     *
     * @param regName the name of the registry to use
     * @return the newly created RegistryEntry
     */
    public RegistryEntry createEntry(String regName)
    {
        RegistryEntry entry = null;
        Registry registry = (Registry) registries.get(regName);
        
        if (registry != null)
        {
            entry = registry.createEntry();
        }
        
        return entry;
    }
    
    /**
     * Returns a RegistryEntry from the named Registry.
     * This is a convenience wrapper around {@link
     * org.apache.jetspeed.om.registry.Registry#getEntry }
     *
     * @param regName the name of the registry
     * @param entryName the name of the entry to retrieve from the
     *                  registry
     * @return a RegistryEntry object if the key is found or null
     */
    public RegistryEntry getEntry(String regName, String entryName)
    {
        System.out.println("Getting Registry = " + regName + " Entry = " + entryName);
        try
        {
            return ((Registry) registries.get(regName)).getEntry(entryName);
        }
        catch (RegistryException e)
        {
            if (log.isInfoEnabled())
            {
                log.info(
                "RegistryService: Failed to retrieve "
                + entryName
                + " from "
                + regName);
            }
        }
        catch (NullPointerException e)
        {
            log.error(
            "RegistryService: " + regName + " registry is not known ");
            log.error(e);
        }
        
        return null;
    }
    
    /**
     * Add a new RegistryEntry in the named Registry.
     * This is a convenience wrapper around {@link
     * org.apache.jetspeed.om.registry.Registry#addEntry }
     *
     * @param regName the name of the registry
     * @param entry the Registry entry to add
     * @exception Sends a RegistryException if the manager can't add
     *            the provided entry
     */
    public void addEntry(String regName, RegistryEntry entry)
    throws RegistryException
    {
        if (entry == null)
        {
            return;
        }
        System.out.println("Adding entry " + regName);
        BaseRegistry registry = (BaseRegistry) registries.get(regName);
        
        if (registry != null)
        {
            String fragmentName = (String) entryIndex.get(entry.getName());
            
            if (fragmentName == null)
            {
                // either the entry was deleted or it does not exist
                // in both cases, use the default fragment
                fragmentName = (String) defaults.get(regName);
            }
            
            RegistryFragment fragment =
            (RegistryFragment) fragments.get(fragmentName);
            
            //Fragment can be (and sometimes is, but should not be) null
            if (fragment == null)
            {
                fragment = new RegistryFragment();
                fragment.put(regName, new Vector());
                fragments.put(fragmentName, fragment);
            }
            else
            {
                Vector vectRegistry = (Vector) fragment.get(regName);
                if (vectRegistry == null)
                {
                    fragment.put(regName, new Vector());
                }
            }
            
            synchronized (entryIndex)
            {
                if (registry.hasEntry(entry.getName()))
                {
                    fragment.setEntry(regName, entry);
                    registry.setLocalEntry(entry);
                }
                else
                {
                    fragment.addEntry(regName, entry);
                    registry.addLocalEntry(entry);
                }
                
                entryIndex.put(entry.getName(), fragmentName);
                // mark this fragment so that it's persisted next time
                // the registry watcher is running
                fragment.setDirty(true);
            }
        }
    }
    
    /**
     * Deletes a RegistryEntry from the named Registry
     * This is a convenience wrapper around {@link
     * org.apache.jetspeed.om.registry.Registry#removeEntry }
     *
     * @param regName the name of the registry
     * @param entryName the name of the entry to remove
     */
    public void removeEntry(String regName, String entryName)
    {
        if (entryName == null)
        {
            return;
        }
        
        BaseRegistry registry = (BaseRegistry) registries.get(regName);
        
        if (registry != null)
        {
            String fragmentName = (String) entryIndex.get(entryName);
            
            if (fragmentName != null)
            {
                RegistryFragment fragment =
                (RegistryFragment) fragments.get(fragmentName);
                
                synchronized (entryIndex)
                {
                    fragment.removeEntry(regName, entryName);
                    entryIndex.remove(entryName);
                    
                    // mark this fragment so that it's persisted next time
                    // the registry watcher is running
                    fragment.setDirty(true);
                }
            }
            
            // the entry is physically removed, remove the dangling reference
            registry.removeLocalEntry(entryName);
        }
    }
    
    /**
     * This is the early initialization method called by the
     * Turbine <code>Service</code> framework
     */
    public void initConfiguration() throws InitializationException
    {
        String mapFile = null;
        Vector names = new Vector();
        int refreshRate = 0;
        
        // Get confiuration parameters
        directory = getConfiguration().getString( CONFIG_DIRECTORY);
        mapFile = getConfiguration().getString( CONFIG_MAPPING, DEFAULT_MAPPING );
        String extension = getConfiguration().getString( CONFIG_EXTENSION, DEFAULT_EXTENSION );
        refreshRate = getConfiguration().getInt( CONFIG_REFRESH, DEFAULT_REFRESH );
        verbose = getConfiguration().getInt( CONFIG_VERBOSE, DEFAULT_VERBOSE );
        
        // Get real paths
        mapFile = getRealPath(mapFile);
        directory = getRealPath(directory);
        
        // build the map of default fragments, each registry must be associated
        // with at least one fragment
       try
       {
            Configuration defaults = getConfiguration().subset("default");
            Iterator i = defaults.getKeys();
            while (i.hasNext())
            {
                String name = (String) i.next();
                String fragmentFileName = defaults.getString(name);
 
                String absFileName =
                new File(directory, fragmentFileName + extension)
                .getAbsolutePath();
                // add this name in the list of available registries
                names.add(name);
 
                // store the default file mapping
                this.defaults.put(name, absFileName);
            }
        }
        catch (Exception e)
        {
            log.error("RegistryService: Registry init error", e);
            throw new InitializationException("Unable to initialize CastorRegistryService, invalid registries definition");
        }
 ////
        // create the serializer output format
        this.format = new OutputFormat();
        format.setIndenting(true);
        format.setIndent(4);
        
        // test the mapping file and create the mapping object
        
        if (mapFile != null)
        {
            File map = new File(mapFile);
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
                    log.error("RegistryService: Error in mapping creation", e);
                    throw new InitializationException("Error in mapping", e);
                }
            }
            else
            {
                throw new InitializationException(
                "Mapping not found or not a file or unreadable: "
                + mapFile);
            }
        }
        
        // Set directory watcher if directory exists
        File base = new File(directory);
        File[] files = null;
        
        if (base.exists() && base.isDirectory() && base.canRead())
        {
            this.watcher = new RegistryWatcher();
            this.watcher.setSubscriber(this);
            this.watcher.setFilter(new ExtFileFilter(extension));
            if (refreshRate == 0)
            {
                this.watcher.setDone();
            }
            else
            {
                this.watcher.setRefreshRate(refreshRate);
            }
            // changing the base will trigger a synchronous loading of the fragments
            this.watcher.changeBase(base);
        }
        
        //Mark that we are done
        setInit(true);
        
        // load the registries
        Enumeration en = names.elements();
        
        while (en.hasMoreElements())
        {
            String name = (String) en.nextElement();
            Registry registry = (Registry) registries.get(name);
            
            if (registry == null)
            {
                String registryClass = null;
                try
                {
                    registryClass =
                    "org.apache.jetspeed.om.registry.base.Base"
                    + name
                    + "Registry";
                    
                    registry =
                    (Registry) Class.forName(registryClass).newInstance();
                }
                catch (Exception e)
                {
                    if (log.isWarnEnabled())
                    {
                        log.warn(
                        "RegistryService: Class "
                        + registryClass
                        + " not found, reverting to default Registry");
                    }
                    registry = new BaseRegistry();
                }
                
                registries.put(name, registry);
            }
            
            refresh(name);
        }
        
        // Start the directory watcher thread and rely on its refresh process
        // to completely load all registries
        if (this.watcher != null)
        {
            this.watcher.start();
        }
        
        if (log.isDebugEnabled())
        {
            log.debug(
            "RegistryService: early init()....end!, this.getInit()= "
            + isInitialized());
        }
        
    }
    
    public void init() throws InitializationException
    {
        log.info( "Initalizing service");
        
        if (isInitialized())
        {
            return;
        }
        
        initConfiguration();
        
        // initialization done
        setInit(true);
        
        System.out.println("********* " + this.getClass().getName() + " service init'd ****");
        
        if (log.isDebugEnabled())
        {
            log.debug("RegistryService: We are done");
        }
    }
    
    /**
     * This is the shutdown method called by the
     * Turbine <code>Service</code> framework
     */
    public void shutdown()
    {
        this.watcher.setDone();
        
        Iterator i = fragments.keySet().iterator();
        while (i.hasNext())
        {
            saveFragment((String) i.next());
        }
    }
    
    // FileRegistry interface
    
    /** Refresh the state of the registry implementation. Should be called
     *   whenever the underlying fragments are modified
     */
    public void refresh()
    {
        synchronized (watcher)
        {
            Enumeration en = getNames();
            while (en.hasMoreElements())
            {
                refresh((String) en.nextElement());
            }
        }
    }
    
    /**
     * @return a Map of all fragments keyed by file names
     */
    public Map getFragmentMap()
    {
        return (Map) fragments.clone();
    }
    
    /**
     * Load and unmarshal a RegistryFragment from the file
     * @param file the absolute file path storing this fragment
     */
    public void loadFragment(String file)
    {
        try
        {
            DocumentBuilderFactory dbfactory =
            DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dbfactory.newDocumentBuilder();
            
            Document d = builder.parse(new File(file));
            
            Unmarshaller unmarshaller = new Unmarshaller(this.mapping);
            RegistryFragment fragment =
            (RegistryFragment) unmarshaller.unmarshal((Node) d);
            
            //mark this fragment as changed
            fragment.setChanged(true);
            
            // if we get here, we successfully loaded the new fragment
            updateFragment(file, fragment);
            
        }
        catch (Throwable t)
        {
            log.error("RegistryService: Could not unmarshal: " + file, t);
        }
        
    }
    
    /**
     * Read and unmarshal a fragment in memory
     * @param name the name of this fragment
     * @param reader the reader to use for creating this fragment
     * @param persistent whether this fragment should be persisted on disk in
     * the registry
     */
    public void createFragment(String name, Reader reader, boolean persistent)
    {
        String file = null;
        
        try
        {
            file = new File(directory, name + extension).getCanonicalPath();
            
            Unmarshaller unmarshaller = new Unmarshaller(this.mapping);
            RegistryFragment fragment =
            (RegistryFragment) unmarshaller.unmarshal(reader);
            
            fragment.setChanged(true);
            
            updateFragment(file, fragment);
            
            if (persistent)
            {
                saveFragment(file);
            }
        }
        catch (Throwable t)
        {
            log.error("RegistryService: Could not create fragment: " + file, t);
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (Exception e)
            {
                log.error(e); // At least log the exception.
            }
        }
    }
    
    /**
     * Marshal and save a RegistryFragment to disk
     * @param file the absolute file path storing this fragment
     */
    public void saveFragment(String file)
    {
        OutputStreamWriter writer = null;
        String encoding = new String("UTF-8");
        RegistryFragment fragment = (RegistryFragment) fragments.get(file);
        
        if (fragment != null)
        {
            try
            {
                writer =
                new OutputStreamWriter(
                new FileOutputStream(file),
                encoding);
                format.setEncoding(encoding);
                Serializer serializer = new XMLSerializer(writer, format);
                Marshaller marshaller =
                new Marshaller(serializer.asDocumentHandler());
                marshaller.setMapping(this.mapping);
                marshaller.marshal(fragment);
            }
            catch (Throwable t)
            {
                log.error("RegistryService: Could not marshal: " + file, t);
            }
            finally
            {
                try
                {
                    writer.close();
                }
                catch (Exception e)
                {
                    log.error(e); // At least log the exception.
                }
            }
        }
    }
    
    /**
     * Remove a fragment from storage
     * @param file the absolute file path storing this fragment
     */
    public void removeFragment(String file)
    {
        RegistryFragment fragment = (RegistryFragment) fragments.get(file);
        
        if (fragment != null)
        {
            synchronized (entryIndex)
            {
                // clear the entry index
                Iterator i = entryIndex.keySet().iterator();
                while (i.hasNext())
                {
                    if (file.equals(entryIndex.get(i.next())))
                    {
                        i.remove();
                    }
                }
                
                // make sure the keys & entries are freed for this fragment
                // only the entries not replaced by the next registry refresh will
                // stay in memory
                fragment.clear();
                // remove the actual fragment from memory
                fragments.remove(file);
            }
        }
    }
    
    // Implementation specific methods
    
    /**
     * Updates a fragment in storage and the associated entryIndex
     */
    protected void updateFragment(String name, RegistryFragment fragment)
    {
        synchronized (entryIndex)
        {
            // remove the old keys
            Iterator i = entryIndex.keySet().iterator();
            while (i.hasNext())
            {
                if (name.equals(entryIndex.get(i.next())))
                {
                    i.remove();
                }
            }
            
            // store the new fragment
            fragments.put(name, fragment);
            
            // recreate the index entries (only this fragment)
            
            Enumeration enum = fragment.keys();
            while (enum.hasMoreElements())
            {
                String strReg = (String) enum.nextElement();
                Vector v = fragment.getEntries(strReg);
                
                for (int counter = 0; counter < v.size(); counter++)
                {
                    RegistryEntry str = (RegistryEntry) v.elementAt(counter);
                    entryIndex.put(str.getName(), name);
                }
            }
        }
    }
    
    /**
     * Scan all the registry fragments for new entries relevant to
     * this registry and update its definition.
     *
     * @param regName the name of the Registry to refresh
     */
    protected void refresh(String regName)
    {
        
        if (log.isDebugEnabled())
        {
            log.debug("RegistryService: Updating the " + regName + " registry");
        }
        
        int count = 0;
        int counDeleted = 0;
        LocalRegistry registry = (LocalRegistry) get(regName);
        
        Vector toDelete = new Vector();
        Iterator i = registry.listEntryNames();
        
        while (i.hasNext())
        {
            toDelete.add(i.next());
        }
        
        if (registry == null)
        {
            log.error("RegistryService: Null " + name + " registry in refresh");
            return;
        }
        
        // for each fragment...
        Enumeration en = fragments.keys();
        while (en.hasMoreElements())
        {
            String location = (String) en.nextElement();
            RegistryFragment fragment =
            (RegistryFragment) fragments.get(location);
            int fragCount = 0;
            
            if (!fragment.hasChanged())
            {
                if ((verbose > 2) && log.isDebugEnabled())
                {
                    log.debug("RegistryService: Skipping fragment " + location);
                }
                
                //remove this fragment entries from the delete list
                Vector entries = fragment.getEntries(regName);
                i = entries.iterator();
                while (i.hasNext())
                {
                    toDelete.remove(((RegistryEntry) i.next()).getName());
                }
                
                continue;
            }
            
            //the fragment has some changes, iterate over its entries...
            
            Vector entries = fragment.getEntries(regName);
            
            //... if it has entries related to this regsistry,
            if (entries != null)
            {
                // for all these entries
                Enumeration en2 = entries.elements();
                while (en2.hasMoreElements())
                {
                    RegistryEntry entry = (RegistryEntry) en2.nextElement();
                    // update or add the entry in the registry
                    try
                    {
                        if (registry.hasEntry(entry.getName()))
                        {
                            if (registry
                            .getEntry(entry.getName())
                            .equals(entry))
                            {
                                if ((verbose > 2) && log.isDebugEnabled())
                                {
                                    log.debug(
                                    "RegistryService: No changes to entry "
                                    + entry.getName());
                                }
                            }
                            else
                            {
                                if ((verbose > 1) && log.isDebugEnabled())
                                {
                                    log.debug(
                                    "RegistryService: Updating entry "
                                    + entry.getName()
                                    + " of class "
                                    + entry.getClass()
                                    + " to registry "
                                    + name);
                                }
                                
                                registry.setLocalEntry(entry);
                                // Initialize the entry index
                                this.entryIndex.put(entry.getName(), location);
                                ++fragCount;
                            }
                        }
                        else
                        {
                            registry.addLocalEntry(entry);
                            // Initialize the entry index
                            this.entryIndex.put(entry.getName(), location);
                            ++fragCount;
                            
                            if ((verbose > 1) && log.isDebugEnabled())
                            {
                                log.debug(
                                "RegistryService: Adding entry "
                                + entry.getName()
                                + " of class "
                                + entry.getClass()
                                + " to registry "
                                + name);
                            }
                        }
                    }
                    catch (RegistryException e)
                    {
                        log.error(
                        "RegistryService: RegistryException while adding "
                        + entry.getName()
                        + "from "
                        + location,
                        e);
                    }
                    
                    //remove this entry from the delete list
                    toDelete.remove(entry.getName());
                }
            }
            
            count += fragCount;
        }
        
        //now delete the entries not found in any fragment
        i = toDelete.iterator();
        while (i.hasNext())
        {
            String entryName = (String) i.next();
            
            if ((verbose > 1) && log.isDebugEnabled())
            {
                log.debug("RegistryService: removing entry " + entryName);
            }
            
            registry.removeLocalEntry(entryName);
        }
        
        if ((verbose > 1) && log.isDebugEnabled())
        {
            log.debug(
            "RegistryService: Merged "
            + count
            + " entries and deleted "
            + toDelete.size()
            + " in "
            + name);
        }
    }
    
    /** FileFilter implementing a file extension based filter */
    class ExtFileFilter implements FileFilter
    {
        private String extension = null;
        
        ExtFileFilter(String extension)
        {
            this.extension = extension;
        }
        
        public boolean accept(File f)
        {
            return f.toString().endsWith(extension);
        }
    }
    
}
