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
package org.apache.jetspeed.serializer;

import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;
import org.apache.jetspeed.serializer.objects.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main JetspeedSerializer implementation delegating the real serializing to JetspeedComponentSerializer instances
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedSerializerImpl implements JetspeedSerializer
{
    private static final Logger log = LoggerFactory.getLogger(JetspeedSerializerImpl.class);

    private List<JetspeedComponentSerializer> serializers;
    private XMLBinding binding;
    private Map<String,Object> defaultSettings;

    public JetspeedSerializerImpl(List<JetspeedComponentSerializer> serializers, Map<String,Object> defaultSettings)
    {
        this.serializers = Collections.unmodifiableList(serializers);
        this.defaultSettings = defaultSettings != null ? Collections.unmodifiableMap(defaultSettings) : Collections.EMPTY_MAP;
        binding = new XMLBinding();
        setupAliases(binding);
    }
    
    public List<JetspeedComponentSerializer> getSerializers()
    {
        return serializers;
    }
    
    public Map<String,Object> getDefaultSettings()
    {
        return defaultSettings;
    }

    public void importData(String filename) throws SerializerException
    {
        importData(filename, null);
    }
    
    public void importData(String filename, Map<String,Object> settings) throws SerializerException
    {
        Map<String,Object> processSettings = getProcessSettings(settings);
        JSSnapshot snapshot = null;
        SerializerException snapshotException = null;
        for (int i = 0; ((i < TAG_SNAPSHOT_NAMES.length) && (snapshot == null)); i++)
        {
            String snapshotTagName = TAG_SNAPSHOT_NAMES[i];
            try
            {
                snapshot = readSnapshot(filename, snapshotTagName);
            }
            catch (SerializerException se)
            {
                if (snapshotException == null)
                {
                    snapshotException = se;
                }
            }
        }
        if (snapshotException != null)
        {
            throw snapshotException;
        }
        else if (snapshot == null)
        {
            throw new SerializerException(SerializerException.FILE_PROCESSING_ERROR.create(new String[] { filename, "Snapshot is NULL" }));
        }

        if (!(snapshot.checkVersion()))
            throw new SerializerException(SerializerException.INCOMPETIBLE_VERSION.create(new String[] { filename,
                    String.valueOf(snapshot.getSoftwareVersion()), String.valueOf(snapshot.getSavedSubversion()) }));
        for (int i = 0, size = serializers.size(); i < size; i++)
        {
            ((JetspeedComponentSerializer) serializers.get(i)).processImport(snapshot, processSettings);
        }
    }

    public void exportData(String name, String filename) throws SerializerException
    {
        exportData(name, filename, null);
    }
    
    public void exportData(String name, String filename, Map<String,Object> settings) throws SerializerException
    {
        Map<String,Object> processSettings = getProcessSettings(settings);
        JSSnapshot snapshot = new JSSnapshot(name);
        snapshot.setDateCreated(new Date(new java.util.Date().getTime()).toString());
        snapshot.setSavedVersion(snapshot.getSoftwareVersion());
        snapshot.setSavedSubversion(snapshot.getSoftwareSubVersion());

        for (int i = 0, size = serializers.size(); i < size; i++)
        {
            serializers.get(i).processExport(snapshot, processSettings);
        }
        
        writeSnapshot(snapshot, filename, binding, processSettings);
    }
    
    public void deleteData() throws SerializerException
    {
        deleteData(null);
    }
    
    public void deleteData(Map<String,Object> settings) throws SerializerException
    {
        Map<String,Object> processSettings = getProcessSettings(settings);
        for (int i = 0, size = serializers.size(); i < size; i++)
        {
            serializers.get(i).deleteData(processSettings);
        }
    }

    protected Map<String,Object> getProcessSettings(Map<String,Object> settings)
    {
        Map<String,Object> processSettings = new HashMap(defaultSettings);
        processSettings.put(KEY_LOGGER, log);
        if ( settings != null )
        {
            processSettings.putAll(settings);
        }
        return processSettings;
    }

    protected void setupAliases(XMLBinding binding)
    {
        binding.setAlias(JSPrincipals.class, "Principals");
        binding.setAlias(JSPrincipalAssociations.class, "PrincipalAssociations");
        binding.setAlias(JSPrincipalAssociation.class, "PrincipalAssociation");
        binding.setAlias(JSRole.class, "Role");
        binding.setAlias(JSRoles.class, "Roles");
        binding.setAlias(JSGroup.class, "Group");
        binding.setAlias(JSGroups.class, "Groups");
        binding.setAlias(JSUser.class, "User");
        binding.setAlias(JSUsers.class, "Users");
        binding.setAlias(JSSecurityAttributes.class, "SecurityAttributes");
        binding.setAlias(JSUserAttributes.class, "userinfo");
        binding.setAlias(JSNVPElements.class, "preferences");
        binding.setAlias(JSNVPElement.class, "SecurityAttribute");
        binding.setAlias(JSNVPElement.class, "preference");
        binding.setAlias(JSSnapshot.class, "snapshot");
        binding.setAlias(JSUserRoles.class, "roles");
        binding.setAlias(JSUserGroups.class, "groups");
        binding.setAlias(JSClient.class, "Client");
        binding.setAlias(JSClients.class, "Clients");
        binding.setAlias(JSClientCapabilities.class, "capabilities");
        binding.setAlias(JSClientMimeTypes.class, "mimeTypes");
        binding.setAlias(JSMimeTypes.class, "MimeTypes");
        binding.setAlias(JSMimeType.class, "MimeType");
        binding.setAlias(JSCapabilities.class, "Capabilities");
        binding.setAlias(JSCapability.class, "Capability");
        binding.setAlias(JSMediaTypes.class, "MediaTypes");
        binding.setAlias(JSMediaType.class, "MediaType");
        binding.setAlias(JSUserUsers.class, "users");

        binding.setAlias(JSPermissions.class, "Permissions");
        binding.setAlias(JSPermission.class, "Permission");
        binding.setAlias(JSProfilingRules.class, "ProfilingRules");
        binding.setAlias(JSProfilingRule.class, "ProfilingRule");
        binding.setAlias(JSRuleCriterions.class, "Criteria");
        binding.setAlias(JSRuleCriterion.class, "Criterion");

        binding.setAlias(JSPrincipalRule.class, "Rule");
        binding.setAlias(JSPrincipalRules.class, "Rules");

        binding.setAlias(JSPWAttributes.class, "credentials");

        binding.setAlias(JSApplication.class, "PortletApplication");
        binding.setAlias(JSApplications.class, "PortletApplications");
        binding.setAlias(JSPortlet.class, "Portlet");
        binding.setAlias(JSPortlets.class, "Portlets");
        binding.setAlias(JSEntity.class, "Entity");
        binding.setAlias(JSEntities.class, "Entities");
        binding.setAlias(JSEntityPreferenceCompat.class, "Principal");        
        binding.setAlias(JSEntityPreference.class, "EntityPreference");
        binding.setAlias(JSEntityPreferences.class, "EntityPreferences");
        binding.setAlias(JSEntityPreferences.class, "Settings");
        binding.setAlias(JSSecurityDomains.class, "SecurityDomains");
        binding.setAlias(JSSecurityDomain.class, "SecurityDomain");

        binding.setAlias(JSSSOSite.class, "Site");
        binding.setAlias(JSSSOSiteRemoteUser.class, "RemoteUser");
        binding.setAlias(JSSSOSiteRemoteUsers.class, "RemoteUsers");
        binding.setAlias(JSSSOSites.class, "SSOSites");

        binding.setAlias(String.class, "String");
        binding.setAlias(Integer.class, "int");

        binding.setClassAttribute(null);
    }

    protected JSSnapshot readSnapshot(String importFileName, String snapshotTagName) throws SerializerException
    {
        XMLObjectReader reader = null;
        JSSnapshot snap = null;
        try
        {
        	File exists = new File(importFileName);
        	if (exists.exists())
        		reader = XMLObjectReader.newInstance(new FileInputStream(importFileName));
        	else
        		reader = XMLObjectReader.newInstance(this.getClass().getClassLoader().getResourceAsStream(importFileName));
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.FILE_READER_ERROR.create(new String[] { importFileName,
                    e.getMessage() }));
        }
        try
        {
            if (this.binding != null)
                reader.setBinding(this.binding);
            snap = (JSSnapshot) reader.read(snapshotTagName, JSSnapshot.class);
        }
        catch (Exception e)
        {
            new SerializerException(SerializerException.FILE_PROCESSING_ERROR.create(new String[] { importFileName,
                    e.getMessage() }));
        }
        finally
        {
            /** ensure the reader is closed */
            try
            {
                log.debug("*********closing up reader ********");
                reader.close();
            }
            catch (Exception e1)
            {
                log.debug("Error in closing reader " + e1.getMessage());
                /**
                 * don't do anything with this exception - never let the bubble
                 * out of the finally block
                 */
                return null;
            }
        }
        return snap;
    }

    protected void writeSnapshot(JSSnapshot snapshot, String filename, XMLBinding binding, Map<String,Object> settings) throws SerializerException
    {
        XMLObjectWriter writer = openWriter(filename, settings);
        writer.setBinding(binding);
        if (settings != null)
        {
            Object o = settings.get(JetspeedSerializer.KEY_EXPORT_INDENTATION);
            if (o != null && o instanceof String)
            {
                writer.setIndentation((String)o);
            }
        }

        try
        {
            log.debug("*********Writing data*********");
            writer.write(snapshot, DEFAULT_TAG_SNAPSHOT_NAME, JSSnapshot.class);

        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.FILE_PROCESSING_ERROR.create(new String[] { filename,
                    e.getMessage() }));
        }
        finally
        {
            /** ensure the writer is closed */
            try
            {
                log.debug("*********closing up********");
                writer.close();
            }
            catch (Exception e)
            {
                log.error("Error in closing writer " + e.getMessage());
                /**
                 * don't do anything with this exception - never let the bubble
                 * out of the finally block
                 */
            }
        }
    }

    /**
     * create or open a given file for writing
     */
    protected XMLObjectWriter openWriter(String filename, Map<String,Object> settings) throws SerializerException
    {
        File f;

        try
        {
            f = new File(filename);
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.FILE_PROCESSING_ERROR.create(new String[] { filename,
                    e.getMessage() }));
        }
        boolean exists = f.exists();

        if (exists)
        {
            if (!(isSettingSet(settings, JetspeedSerializer.KEY_OVERWRITE_EXISTING)))
                throw new SerializerException(SerializerException.FILE_ALREADY_EXISTS.create(filename));
            if (isSettingSet(settings, JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS))
            {
                String backName = createUniqueBackupFilename(f.getParentFile(), f.getName());
                if (backName == null)
                    throw new SerializerException(SerializerException.FILE_BACKUP_FAILED.create(filename));
                File ftemp = new File(backName);
                f.renameTo(ftemp);
            }
        }
        try
        {
            XMLObjectWriter writer = XMLObjectWriter.newInstance(new FileOutputStream(filename));
            return writer;
        }
        catch (Exception e)
        {
            throw new SerializerException(SerializerException.FILE_WRITER_ERROR.create(new String[] { filename,
                    e.getMessage() }));
        }
    }

    /**
     * returns if the key for a particular setting is true. False if the key
     * doesn't exist.
     * 
     * @param key
     * @return
     */
    protected static boolean isSettingSet(Map<String,Object> settings, String key)
    {
        if (settings != null)
        {
            Object o = settings.get(key);
            if (o != null && o instanceof Boolean)
            {
                return ((Boolean) o).booleanValue();
            }
        }
        return false;
    }

    /**
     * Helper routine to create a unique filename for a backup of an existing
     * filename....not intended to be rocket science...
     * 
     * @param name
     * @return
     */
    protected static String createUniqueBackupFilename(File folder, String name)
    {
        File f = new File(folder, name + ".bak");
        int counter = 0;
        if (!(f.exists()))
        {
            return f.getAbsolutePath();
        }
        while (counter < 100)
        {
            f = new File(folder, name + ".bak" + counter);
            if (!(f).exists())
            {
                return f.getAbsolutePath();
            }
            counter++;
        }
        return null;
    }
}
