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
package org.apache.jetspeed.services.psml;

//standard java stuff
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.cache.file.FileCache;
import org.apache.jetspeed.cache.file.FileCacheEntry;
import org.apache.jetspeed.cache.file.FileCacheEventListener;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.om.profile.BasePSMLDocument;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.services.profiler.Profiler;
import org.apache.jetspeed.util.DirectoryUtils;
import org.apache.jetspeed.util.FileCopy;
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
 * This service is responsible for loading and saving PSML documents.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:sgala@apache.org">Santiago Gala</a>
 * @version $Id$
 */
public class CastorPsmlManagerService extends BaseCommonService 
                                      implements FileCacheEventListener,
                                                 PsmlManagerService
{
    // resource path constants
    protected static final String PATH_GROUP              = "group";
    protected static final String PATH_ROLE               = "role";
    protected static final String PATH_USER               = "user";

    // configuration keys
    protected final static String CONFIG_ROOT             = "root";
    protected final static String CONFIG_EXT              = "ext";
    protected final static String CONFIG_SCAN_RATE        = "scanRate";
    protected final static String CONFIG_CACHE_SIZE       = "cacheSize";

    // default configuration values
    public final static String DEFAULT_ROOT             = "/WEB-INF/psml";
    public final static String DEFAULT_EXT              = ".psml";

    // default resource
    public final static String DEFAULT_RESOURCE         = "default.psml";

    // the root psml resource directory
    protected String root;
    // base store directory
    protected File rootDir = null;
    // file extension
    protected String ext;

    /** The documents loaded by this manager */
    protected FileCache documents = null;

    /** the output format for pretty printing when saving registries */
    protected OutputFormat format = null;

    /** the base refresh rate for documents */
    protected long scanRate = 1000 * 60; // every minute

    /** the default cache size */
    protected int cacheSize = 100;

    /** the import/export consumer service **/
    protected PsmlManagerService consumer = null;
    protected boolean importFlag = false;

    // castor mapping
    public static final String DEFAULT_MAPPING = "${webappRoot}/WEB-INF/conf/psml-mapping.xml";
    protected String mapFile = null;

    /** the Castor mapping file name */
    protected Mapping mapping = null;

    private final static Log log = LogFactory.getLog(CastorPsmlManagerService.class);

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
        this.root = getConfiguration().getString( CONFIG_ROOT, DEFAULT_ROOT );
        this.rootDir = new File(root);

        //If the rootDir does not exist, treat it as context relative
        if ( !rootDir.exists() )
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
        this.ext = getConfiguration().getString( CONFIG_EXT, DEFAULT_EXT );

        // create the serializer output format
        this.format = new OutputFormat();
        format.setIndenting(true);
        format.setIndent(4);

        // psml castor mapping file
        mapFile = getConfiguration().getString("mapping",DEFAULT_MAPPING);
        mapFile = Jetspeed.getRealPath( mapFile );
        loadMapping();

        this.scanRate = getConfiguration().getLong(CONFIG_SCAN_RATE, this.scanRate);
        this.cacheSize= getConfiguration().getInt(CONFIG_CACHE_SIZE, this.cacheSize);

        documents = new FileCache(this.scanRate, this.cacheSize);
        documents.addListener(this);
        documents.startFileScanner();


        //Mark that we are done
        setInit(true);

        // Test
        //testCases();

    }


    /**
     * This is the shutdown method called by the
     * Turbine <code>Service</code> framework
     */
    public void shutdown()
    {
        documents.stopFileScanner();
    }

    /**
     * Returns a PSML document of the given name.
     * For this implementation, the name must be the document
     * URL or absolute filepath
     *
     * @deprecated
     * @param name the name of the document to retrieve
     */
    public PSMLDocument getDocument( String name )
    {
        if (name == null)
        {
            String message = "PSMLManager: Must specify a name";
            log.error( message );
            throw new IllegalArgumentException( message );
        }

        if (log.isDebugEnabled())
        {
            log.debug( "PSMLManager: asked for " + name );
        }

        PSMLDocument doc = null;

        doc = (PSMLDocument)documents.getDocument(name);

        if (doc == null)
        {
            doc = loadDocument(name);

            synchronized (documents)
            {
                // store the document in the hash and reference it to the watcher
                try
                {
                    documents.put(name, doc);
                }
                catch (java.io.IOException e)
                {
                    log.error("Error puttin document: " + e);
                }
            }
        }

        return doc;
    }

    /**
     * Returns a PSML document for the given locator
     *
     * @param locator The locator descriptor of the document to be retrieved.
     */
    public PSMLDocument getDocument( ProfileLocator locator )
    {
        if (locator == null)
        {
            String message = "PSMLManager: Must specify a name";
            log.error( message );
            throw new IllegalArgumentException( message );
        }
        File base = this.rootDir;
        String path = mapLocatorToFile(locator);
        File file = new File(base, path);
        String name = null;

        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            log.error("PSMLManager: unable to resolve file path for "+ file);
        }

        if (log.isDebugEnabled())
        {
            log.debug("PSMLManager: calculated resource:" + path + ". Base: " + base + " File: " + name);
        }

        PSMLDocument doc = null;
        Profile profile = null;

        profile = (Profile)documents.getDocument(name);

        if (profile == null)
        {
            doc = loadDocument(name);
            if (null == doc)
            {
                if (log.isWarnEnabled())
                {
                    log.warn( "PSMLManager: " + name + " not found, returning null document" );
                }
                return null;
            }

            synchronized (documents)
            {
                // store the document in the hash and reference it to the watcher
                Profile newProfile = createProfile(locator);
                newProfile.setDocument(doc);
                try
                {
                    documents.put(name, newProfile);
                }
                catch (IOException e)
                {
                    log.error("Error putting document: " + e);
                }
            }
        }
        else
        {
            doc = profile.getDocument();
        }

        return doc;
    }

    /**
     * Load a PSMLDOcument from disk
     *
     * @param fileOrUrl a String representing either an absolute URL or an
     * absolute filepath
     */
    protected PSMLDocument loadDocument(String fileOrUrl)
    {
        PSMLDocument doc = null;

        if (fileOrUrl!=null)
        {
            if (!fileOrUrl.endsWith(DEFAULT_EXT))
            {
                fileOrUrl = fileOrUrl.concat(DEFAULT_EXT);
            }

            // load the document and add it to the watcher
            // we'll assume the name is the the location of the file

            File f = getFile(fileOrUrl);
            if (null == f)
                return null;

            doc = new BasePSMLDocument();
            doc.setName(fileOrUrl);

            // now that we have a file reference, try to load the serialized PSML
            Portlets portlets = null;
            FileReader reader = null;
            try
            {
                reader = new FileReader(f);

                portlets = load(reader);

                doc.setPortlets(portlets);

            }
            catch (IOException e)
            {
                log.error("PSMLManager: Could not load the file "+f.getAbsolutePath(), e);
                doc = null;
            }
            catch (MarshalException e)
            {
                log.error("PSMLManager: Could not unmarshal the file "+f.getAbsolutePath(), e);
                doc = null;
            }
            catch (MappingException e)
            {
                log.error("PSMLManager: Could not unmarshal the file "+f.getAbsolutePath(), e);
                doc = null;
            }
            catch (ValidationException e)
            {
                log.error("PSMLManager: document "+f.getAbsolutePath()+" is not valid", e);
                doc = null;
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
        }

        return doc;
    }

    /** Store the PSML document on disk, using its locator
     *
     * @param profile the profile locator description.
     * @return true if the operation succeeded
     */
    public boolean store(Profile profile)
    {
        PSMLDocument doc = profile.getDocument();

        File base = this.rootDir;
        String path = mapLocatorToFile(profile);

        File file = new File(base, path);
        String fullpath = null;

        try
        {
            fullpath = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            log.error("PSMLManager: unable to resolve file path for "+ file);
        }

        boolean ok = saveDocument(fullpath, doc);

        // update it in cache
        synchronized (documents)
        {
            try
            {
                documents.put(fullpath, profile);
            }
            catch (IOException e)
            {
                log.error("Error storing document: " + e);
            }
        }

        return ok;
    }

    /** Save the PSML document on disk, using its name as filepath
     * @deprecated
     * @param doc the document to save
     */
    public boolean saveDocument(PSMLDocument doc)
    {
        return saveDocument(doc.getName(), doc);
    }

    /** Save the PSML document on disk to the specififed fileOrUrl
     *
     * @param fileOrUrl a String representing either an absolute URL
     * or an absolute filepath
     * @param doc the document to save
     */
    public boolean saveDocument(String fileOrUrl, PSMLDocument doc)
    {
        boolean success = false;

        if (doc == null) return false;
        File f = getFile(fileOrUrl);
        if (f == null)
        {
            f = new File(fileOrUrl);
        }


        FileWriter writer = null;

        try
        {
            writer = new FileWriter(f);
            save(writer, doc.getPortlets());
            success = true;
        }
        catch (MarshalException e)
        {
            log.error("PSMLManager: Could not marshal the file "+f.getAbsolutePath(), e);
        }
        catch (MappingException e)
        {
            log.error("PSMLManager: Could not marshal the file "+f.getAbsolutePath(), e);
        }
        catch (ValidationException e)
        {
            log.error("PSMLManager: document "+f.getAbsolutePath()+" is not valid", e);
        }
        catch (IOException e)
        {
            log.error("PSMLManager: Could not save the file "+f.getAbsolutePath(), e);
        }
        catch (Exception e)
        {
            log.error("PSMLManager: Error while saving  "+f.getAbsolutePath(), e);
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

        return success;
    }

    /** Deserializes a PSML structure read from the reader using Castor
     *  XML unmarshaller
     *
     * @param reader the reader to load the PSML from
     * @param the loaded portlets structure or null
     */
    protected Portlets load(Reader reader)
        throws IOException, MarshalException, ValidationException, MappingException
    {
        Unmarshaller unmarshaller = new Unmarshaller(this.mapping);
        Portlets portlets = (Portlets)unmarshaller.unmarshal(reader);
        return portlets;
    }

    protected void loadMapping()
        throws CPSInitializationException
    {
        // test the mapping file and create the mapping object

        if (mapFile != null)
        {
            File map = new File(mapFile);
            if (log.isDebugEnabled())
            {
                log.debug("PSMLManager: Loading psml mapping file "+mapFile);
            }
            if (map.exists() && map.isFile() && map.canRead())
            {
                try
                {
                    mapping = new Mapping();
                    InputSource is = new InputSource( new FileReader(map) );
                    is.setSystemId( mapFile );
                    mapping.loadMapping( is );
                }
                catch (Exception e)
                {
                    log.error("PSMLManager: Error in psml mapping creation",e);
                    throw new CPSInitializationException("Error in mapping",e);
                }
            }
            else
            {
                throw new CPSInitializationException("PSML Mapping not found or not a file or unreadable: "+mapFile);
            }
        }
    }

    /** Serializes a PSML structure using the specified writer with Castor
     *  XML marshaller and a Xerces serializer for pretty printing
     *
     * @param writer the writer to use for serialization
     * @param portlets the structure to save
     */
    protected void save(Writer writer, Portlets portlets)
        throws IOException, MarshalException, ValidationException, MappingException
    {
        if (portlets != null)
        {
            Serializer serializer = new XMLSerializer(writer, format);
//            portlets.marshal(serializer.asDocumentHandler());
            Marshaller marshaller = new Marshaller(serializer.asDocumentHandler());
            marshaller.setMapping(this.mapping);
            marshaller.marshal(portlets);
        }
    }

    /** Tests wether the passed argument is an URL string or a file name
     *  and returns the corresponding file object, using diskcache for
     *  remote URLs
     *
     *  @param fileOrUrl the URL string or file path
     *  @return a File object. This file may not exist on disk.
     */
    protected File getFile(String fileOrUrl)
    {
        File f = null;

        f = new File(fileOrUrl);

        if (f.exists())
        {
            return f;
        }
        return null;
    }

    /** Create a new document.
     *
     * @param profile The description and default value for the new document.
     * @return The newly created document;
     */
    public PSMLDocument createDocument( Profile profile )
    {
        File base = this.rootDir;
        String path = mapLocatorToFile((ProfileLocator)profile);

        if (log.isDebugEnabled())
        {
            log.debug("PSMLManager: Create document for profile " + profile +", calculated path: " + path);
        }

        File file = new File(base, path);
        String name = null;

        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            log.error("PSMLManager: unable to resolve file path for "+ file);
        }

        PSMLDocument template = profile.getDocument();
        PSMLDocument doc = new BasePSMLDocument( name, template.getPortlets() );
        try
        {
            String parent = file.getParent();
            File filePath = new File(parent);
            filePath.mkdirs();
            if (template.getName() != null)
            {
                try
                {
                    File source = new File(template.getName());
                    if (source.exists())
                    {
                        FileCopy.copy( template.getName(), name );
                    }
                }
                catch (Exception e)
                {}
            }
            else
            {
                doc.setName(name);
            }
            saveDocument(doc);
        }
        catch (Exception e)
        {
            log.error("PSMLManager: Failed to save document: " , e);
            e.printStackTrace();
        }
        return doc;
    }

    /** Given a ordered list of locators, find the first document matching
     *  a profile locator, starting from the beginning of the list and working
     *  to the end.
     *
     * @param locator The ordered list of profile locators.
     */
    public PSMLDocument getDocument( List locators )
    {
        PSMLDocument doc=null;

        Iterator i = locators.iterator();
        while ((doc==null)&&(i.hasNext()))
        {
            doc=getDocument((ProfileLocator)i.next());
        }

        return doc;
    }

    /** Removes a document.
     *
     * @param locator The description of the profile resource to be removed.
     */
    public void removeDocument( ProfileLocator locator )
    {
        // remove a single document
        String fileName = mapLocatorToFile(locator);

        File base = this.rootDir;
        File file = new File(base, fileName);
        String name = null;

        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            log.error("PSMLManager: unable to resolve file path for "+ file);
        }


        synchronized (documents)
        {
            documents.remove(name);
        }

        file.delete();

    }

    /** Removes all documents for a given user.
     *
     * @param user The user object.
     */
    public void removeUserDocuments( String name )
    {
        ProfileLocator locator = Profiler.createLocator();
        locator.setUser(name);
        StringBuffer buffer = new StringBuffer();
        buffer.append(PATH_USER);
        if (null != name && name.length() > 0)
        {
            buffer.append(File.separator)
                .append(name);
        }
        else
            return; // don't delete the entire user directories

        String path = buffer.toString();
        File base = this.rootDir;
        File file = new File(base, path);

        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            log.error("PSMLManager: unable to resolve file path for "+ file);
        }


        synchronized (documents)
        {
            DirectoryUtils.rmdir(name);
            Iterator it = documents.getIterator();
            while (it.hasNext())
            {
                FileCacheEntry entry = (FileCacheEntry)it.next();
                if (null == entry)
                {
                    continue;
                }
                Profile profile = (Profile)entry.getDocument();
                if (null == profile)
                {
                    continue;
                }
                String pUser = profile.getUser();
                if (null != pUser && pUser.equals(name))
                {
                    documents.remove(profile.getDocument().getName());
                }
            }
        }

    }

    /** Removes all documents for a given role.
     *
     * @param role The role object.
     */
    public void removeRoleDocuments( String role )
    {
        ProfileLocator locator = Profiler.createLocator();
        locator.setRole(role);
        StringBuffer buffer = new StringBuffer();
        buffer.append(PATH_ROLE);
        if (null != role && role.length() > 0)
        {
            buffer.append(File.separator)
                .append(name);
        }
        else
            return; // don't delete the entire role directories

        String path = buffer.toString();
        File base = this.rootDir;
        File file = new File(base, path);
        String name = role;
        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            log.error("PSMLManager: unable to resolve file path for "+ file);
        }


        synchronized (documents)
        {
            DirectoryUtils.rmdir(name);
            Iterator it = documents.getIterator();
            while (it.hasNext())
            {
                FileCacheEntry entry = (FileCacheEntry)it.next();
                if (null == entry)
                {
                    continue;
                }
                Profile profile = (Profile)entry.getDocument();
                if (null == profile)
                {
                    continue;
                }
                String pRole = profile.getRole();
                if (null != pRole && pRole.equals(role))
                {
                    documents.remove(profile.getDocument().getName());
                }
            }
        }
    }

    /** Removes all documents for a given group.
     *
     * @param group The group object.
     */
    public void removeGroupDocuments( String group )
    {
        ProfileLocator locator = Profiler.createLocator();
        locator.setGroup(group);
        StringBuffer buffer = new StringBuffer();
        buffer.append(PATH_GROUP);
        if (null != group && group.length() > 0)
        {
            buffer.append(File.separator)
                .append(group);
        }
        else
            return; // don't delete the entire group directories

        String path = buffer.toString();
        File base = this.rootDir;
        File file = new File(base, path);
        String name = group;
        try
        {
            name = file.getCanonicalPath();
        }
        catch (IOException e)
        {
            log.error("PSMLManager: unable to resolve file path for "+ file);
        }


        synchronized (documents)
        {
            DirectoryUtils.rmdir(name);
            Iterator it = documents.getIterator();
            while (it.hasNext())
            {
                FileCacheEntry entry = (FileCacheEntry)it.next();
                if (null == entry)
                {
                    continue;
                }
                Profile profile = (Profile)entry.getDocument();
                if (null == profile)
                {
                    continue;
                }
                String pGroup = profile.getGroup();
                if (null != pGroup && pGroup.equals(group))
                {
                    documents.remove(profile.getDocument().getName());
                }
            }
        }

    }


    /**
     * Maps a ProfileLocator to a file.
     *
     * @param locator The profile locator describing the PSML resource to be found.
     * @return the String path of the file.
     */
    protected String mapLocatorToFile(ProfileLocator locator)
    {
        StringBuffer path = new StringBuffer();

        // move the base dir is either user or role is specified
        String role = locator.getRole();
        String group = locator.getGroup();
        String user = locator.getUser();

        if (user != null)
        {
            path.append(PATH_USER);
            if (user.length() > 0)
            {
                path.append(File.separator)
                    .append(user);
            }
        }
        else if (group != null)
        {
            path.append(PATH_GROUP);
            if (group.length() > 0)
            {
                path.append(File.separator)
                    .append(group);
            }
        }
        else if (null != role)
        {
            path.append(PATH_ROLE);
            if (role.length() > 0)
            {
                path.append(File.separator)
                    .append(role);
            }
        }

        // Media
        if (null != locator.getMediaType())
        {
            path.append(File.separator)
                .append(locator.getMediaType());
        }
        // Language
        if (null != locator.getLanguage())
        {
            path.append(File.separator)
                .append(locator.getLanguage());
        }
        // Country
        if (null != locator.getCountry())
        {
            path.append(File.separator)
                .append(locator.getCountry());
        }
        // Resource Name
        if (null != locator.getName())
        {
            if (!(locator.getName().endsWith(CastorPsmlManagerService.DEFAULT_EXT)))
            {
                path.append(File.separator)
                    .append(locator.getName()).append(CastorPsmlManagerService.DEFAULT_EXT);
            }
            else
            {
                path.append(File.separator)
                    .append(locator.getName());
            }
        }
        else
        {
            path.append(File.separator)
                .append(DEFAULT_RESOURCE);
        }

        return  path.toString();
    }

    protected static int STATE_INIT = 0;
    protected static int STATE_BASE = 1;
    protected static int STATE_NAME = 2;
    protected static int STATE_MEDIA = 3;
    protected static int STATE_LANGUAGE = 4;
    protected static int STATE_COUNTRY = 5;

    /** Query for a collection of profiles given a profile locator criteria.
     *
     * @param locator The profile locator criteria.
     */
    public Iterator query( QueryLocator locator )
    {
        List list = new LinkedList();

        String role = locator.getRole();
        String group = locator.getGroup();
        String user = locator.getUser();

        // search thru anonymous directories?
        int qm = locator.getQueryMode();
        if ((qm & QueryLocator.QUERY_USER) == QueryLocator.QUERY_USER)
        {
            Profile profile = createProfile();
            StringBuffer path = new StringBuffer();
            path.append(PATH_USER);
            int state = STATE_INIT;
            if (null != user)
            {
                profile.setUser( user );
                path.append(File.separator).append(user);
                state = STATE_BASE;
            }
            File base = this.rootDir;
            File file = new File(base, path.toString());
            String absPath = file.getAbsolutePath();
            QueryState qs = new QueryState( QUERY_BY_USER,
                                             profile,
                                             locator,
                                             list,
                                             user,
                                             state);
            subQuery(qs, absPath);
        }
        if ((qm & QueryLocator.QUERY_ROLE) == QueryLocator.QUERY_ROLE)
        {
            Profile profile = createProfile();
            StringBuffer path = new StringBuffer();
            path.append(PATH_ROLE);
            int state = STATE_INIT;
            if (null != role)
            {
                profile.setRole( role );
                path.append(File.separator).append(role);
                state = STATE_BASE;
            }
            File base = this.rootDir;
            File file = new File(base, path.toString());
            String absPath = null;

            try
            {
                absPath = file.getCanonicalPath();
            }
            catch (IOException e)
            {
                log.error("PSMLManager: unable to resolve file path for "+ file);
            }

            QueryState qs = new QueryState( QUERY_BY_ROLE,
                                             profile,
                                             locator,
                                             list,
                                             role,
                                             state);
            subQuery(qs, absPath);
        }
        if ((qm & QueryLocator.QUERY_GROUP) == QueryLocator.QUERY_GROUP)
        {
            Profile profile = createProfile();
            StringBuffer path = new StringBuffer();
            path.append(PATH_GROUP);
            int state = STATE_INIT;
            if (null != group)
            {
                profile.setGroup( group );
                path.append(File.separator).append(group);
                state = STATE_BASE;
            }
            File base = this.rootDir;
            File file = new File(base, path.toString());
            String absPath = null;

            try
            {
                absPath = file.getCanonicalPath();
            }
            catch (IOException e)
            {
                log.error("PSMLManager: unable to resolve file path for "+ file);
            }

            QueryState qs = new QueryState( QUERY_BY_GROUP,
                                             profile,
                                             locator,
                                             list,
                                             group,
                                             state);
            subQuery(qs, absPath);
        }

        return list.iterator();
    }

    /** Create a profile based on import flag.
     *
     */
    protected Profile createProfile()
    {
//    TODO:    if (importFlag)
  //          return new ImportProfile(this, this.consumer);
    //    else
            return Profiler.createProfile();
    }

    protected Profile createProfile(ProfileLocator locator)
    {
// TODO        if (importFlag)
//            return new ImportProfile(this, this.consumer, locator);
  //      else
            return Profiler.createProfile(locator);
    }

    /** Query for a collection of profiles given a profile locator criteria.
     *  This method should be used when importing or exporting profiles between services.
     *
     * @param locator The profile locator criteria.
     * @return The count of profiles exported.
     */
    public int export(PsmlManagerService consumer, QueryLocator locator)
    {
        importFlag = true;
        Iterator profiles = null;
        int count = 0;
        try
        {
            this.consumer = consumer;
            profiles = query(locator);

            while (profiles.hasNext() )
            {
                Profile profile = (Profile)profiles.next();
                //dumpProfile(profile);
                try
                {
                    consumer.createDocument(profile);
                    count++;
                }
                catch (Exception ex)
                {
                    try
                    {
                        consumer.store(profile);
                        count++;
                    }
                    catch (Exception e)
                    {
                        log.error("PSMLManager: Failed to export profiles to DB: " + profile, ex );
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            log.error("PSMLManager: Failed to export profiles to DB: " , e );

        }
        finally
        {
            importFlag = false;
        }
        return count;
    }


    /** Query for a collection of profiles given a profile locator criteria.
     *  To specify 'all' - use '*' in the criteria
     *
     * @param locator The profile locator criteria.
     */
    protected void subQuery(QueryState qs, String path)
    {
        File file = new File(path);
        if (file.isFile())
        {
            try
            {
                String filename = file.getName();

                if (!filename.endsWith(this.ext))
                    return;

                Profile clone = (Profile)qs.profile.clone();
                clone.setName(filename);
                qs.list.add( clone );
            }
            catch (Exception e)
            {
                log.error("PSMLManager: Failed to clone profile: " + path + " : " + e, e);
            }
        }
        else if (file.isDirectory())
        {
            String dirName = file.getName();

            qs.state++;

            // filter out based on name, mediatype, language, country
            if (qs.state == STATE_NAME)
            {
                if (null != qs.name)
                {
                    if (!dirName.equals(qs.name))
                        return;
                }
                try
                {
                    if (QUERY_BY_USER == qs.queryBy)
                    {
                        String user = qs.profile.getUser();
                        if (null == user)
                        {
                            qs.profile.setUser(file.getName());
                            qs.clearName = true;
                        }
                    }
                    else if (QUERY_BY_ROLE == qs.queryBy)
                    {
                        String role = qs.profile.getRole();
                        if (null == role)
                        {
                            qs.profile.setRole(file.getName());
                            qs.clearName = true;
                        }
                    }
                    else if (QUERY_BY_GROUP == qs.queryBy)
                    {
                        String group = qs.profile.getGroup();
                        if (null == group)
                        {
                            qs.profile.setGroup(file.getName());
                            qs.clearName = true;
                        }
                    }
                }
                catch (Exception e)
                {}


            }
            else if (qs.state == STATE_MEDIA)
            {
                String media = qs.locator.getMediaType();
                if (null != media)
                {
                    if (!dirName.equals(media))
                        return;
                    else
                    {
                        qs.profile.setMediaType(dirName);
                    }
                }
                else
                {
                    qs.profile.setMediaType(dirName);
                    qs.clearMedia = true;
                }
            }
            else if (qs.state == STATE_LANGUAGE)
            {
                String language = qs.locator.getLanguage();
                if (null != language)
                {
                    if (!dirName.equals(language))
                        return;
                    else
                    {
                        qs.profile.setLanguage(dirName);
                    }
                }
                else
                {
                    qs.profile.setLanguage(dirName);
                    qs.clearLanguage = true;
                }
            }
            else if (qs.state == STATE_COUNTRY)
            {
                String country = qs.locator.getCountry();
                if (null != country)
                {
                    if (!dirName.equals(country))
                        return;
                    else
                    {
                        qs.profile.setCountry(dirName);
                    }
                }
                else
                {
                    qs.profile.setCountry(dirName);
                    qs.clearCountry = true;
                }
            }

            if (!path.endsWith(File.separator))
                path += File.separator;

            String files[] = file.list();


            // Process all files recursivly
            for(int ix = 0; files != null && ix < files.length; ix++)
            {
                subQuery(qs, path + files[ix]);
            }

            // clear state
            if (qs.state == STATE_NAME && true == qs.clearName)
            {
                if (QUERY_BY_USER == qs.queryBy)
                    qs.profile.setUser(null);
                else if (QUERY_BY_ROLE == qs.queryBy)
                    qs.profile.setRole(null);
                else if (QUERY_BY_GROUP == qs.queryBy)
                    qs.profile.setGroup(null);
                qs.clearName = false;
            }
            else if (qs.state == STATE_MEDIA && true == qs.clearMedia)
            {
                qs.profile.setMediaType(null);
                qs.clearMedia = false;
            }
            else if (qs.state == STATE_LANGUAGE && true == qs.clearLanguage)
            {
                qs.profile.setLanguage(null);
                qs.clearLanguage = false;
            }
            else if (qs.state == STATE_COUNTRY && true == qs.clearCountry)
            {
                qs.profile.setCountry(null);
                qs.clearCountry = false;
            }

            qs.state--;

        }

    }

     static int QUERY_BY_USER = 0;
     static int QUERY_BY_ROLE = 1;
     static int QUERY_BY_GROUP = 2;

    protected class QueryState
    {

        QueryState( int queryBy,
                    Profile profile,
                    ProfileLocator locator,
                    List list,
                    String name,
                    int state)
        {
            this.queryBy = queryBy;
            this.profile = profile;
            this.locator = locator;
            this.list = list;
            this.name = name;
            this.state = state;
        }

        protected int queryBy;
        protected Profile profile;
        protected ProfileLocator locator;
        protected List list;
        protected String name;
        protected int state;

        protected boolean clearName = false;
        protected boolean clearMedia = false;
        protected boolean clearLanguage = false;
        protected boolean clearCountry = false;

    }

    protected void dump( Iterator it )
    {
        System.out.println("===============================================");
        while (it.hasNext() )
        {
            Profile profile = (Profile)it.next();
            dumpProfile(profile);
        }
        System.out.println("===============================================");
    }

    protected void dumpProfile(Profile profile)
    {
        String user = profile.getUser();
        String group = profile.getGroup();
        String role = profile.getRole();
        if (profile.getAnonymous() == true)
            System.out.println("ANON USER");
        System.out.println("RESOURCE = " + profile.getName());
        if (null != user)
            System.out.println("USER = " + user );
        if (null != group)
            System.out.println("GROUP = " + group );
        if (null != role)
            System.out.println("ROLE = " + role );
        System.out.println("MEDIA TYPE = " + profile.getMediaType());
        System.out.println("LANGUAGE = " + profile.getLanguage());
        System.out.println("COUNTRY = " + profile.getCountry());
        PSMLDocument doc = profile.getDocument();
        if (null == doc)
            System.out.println("Document is null");
        else
        {
            if (null == profile.getName())
                System.out.println("profile name is null");
            else
                System.out.println("Doc.name=" + profile.getName());
        }

        System.out.println("----------------------");
    }

    /**
     * Refresh event, called when the entry is being refreshed from file system.
     *
     * @param entry the entry being refreshed.
     */
    public void refresh(FileCacheEntry entry)
    {
        System.out.println("entry is refreshing: " + entry.getFile().getName());
        Profile profile = (Profile) entry.getDocument();
        if (profile != null)
        {
            profile.setDocument(loadDocument(entry.getFile().getName()));
        }
    }

    /**
     * Evict event, called when the entry is being evicted out of the cache
     *
     * @param entry the entry being refreshed.
     */
    public void evict(FileCacheEntry entry)
    {
        System.out.println("entry is evicting: " + entry.getFile().getName());
    }

}

