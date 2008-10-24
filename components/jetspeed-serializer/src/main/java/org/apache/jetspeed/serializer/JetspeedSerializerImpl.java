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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.serializer.objects.JSApplication;
import org.apache.jetspeed.serializer.objects.JSApplications;
import org.apache.jetspeed.serializer.objects.JSCapabilities;
import org.apache.jetspeed.serializer.objects.JSCapability;
import org.apache.jetspeed.serializer.objects.JSClient;
import org.apache.jetspeed.serializer.objects.JSClientCapabilities;
import org.apache.jetspeed.serializer.objects.JSClientMimeTypes;
import org.apache.jetspeed.serializer.objects.JSClients;
import org.apache.jetspeed.serializer.objects.JSEntities;
import org.apache.jetspeed.serializer.objects.JSEntity;
import org.apache.jetspeed.serializer.objects.JSEntityPreference;
import org.apache.jetspeed.serializer.objects.JSEntityPreferences;
import org.apache.jetspeed.serializer.objects.JSGroup;
import org.apache.jetspeed.serializer.objects.JSGroups;
import org.apache.jetspeed.serializer.objects.JSMediaType;
import org.apache.jetspeed.serializer.objects.JSMediaTypes;
import org.apache.jetspeed.serializer.objects.JSMimeType;
import org.apache.jetspeed.serializer.objects.JSMimeTypes;
import org.apache.jetspeed.serializer.objects.JSNVPElement;
import org.apache.jetspeed.serializer.objects.JSNVPElements;
import org.apache.jetspeed.serializer.objects.JSPWAttributes;
import org.apache.jetspeed.serializer.objects.JSPermission;
import org.apache.jetspeed.serializer.objects.JSPermissions;
import org.apache.jetspeed.serializer.objects.JSPortlet;
import org.apache.jetspeed.serializer.objects.JSPortlets;
import org.apache.jetspeed.serializer.objects.JSPrincipalAssociation;
import org.apache.jetspeed.serializer.objects.JSPrincipalAssociations;
import org.apache.jetspeed.serializer.objects.JSPrincipalRule;
import org.apache.jetspeed.serializer.objects.JSPrincipalRules;
import org.apache.jetspeed.serializer.objects.JSPrincipals;
import org.apache.jetspeed.serializer.objects.JSProfilingRule;
import org.apache.jetspeed.serializer.objects.JSProfilingRules;
import org.apache.jetspeed.serializer.objects.JSRole;
import org.apache.jetspeed.serializer.objects.JSRoles;
import org.apache.jetspeed.serializer.objects.JSRuleCriterion;
import org.apache.jetspeed.serializer.objects.JSRuleCriterions;
import org.apache.jetspeed.serializer.objects.JSSecurityAttributes;
import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.jetspeed.serializer.objects.JSUser;
import org.apache.jetspeed.serializer.objects.JSUserAttributes;
import org.apache.jetspeed.serializer.objects.JSUserGroups;
import org.apache.jetspeed.serializer.objects.JSUserRoles;
import org.apache.jetspeed.serializer.objects.JSUserUsers;
import org.apache.jetspeed.serializer.objects.JSUsers;

import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;

/**
 * Main JetspeedSerializer implementation delegating the real serializing to JetspeedComponentSerializer instances
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedSerializerImpl implements JetspeedSerializer
{
    private static final Log log = LogFactory.getLog(JetspeedSerializerImpl.class);

    private List serializers;
    private XMLBinding binding;
    private Map defaultSettings;

    public JetspeedSerializerImpl(List serializers, Map defaultSettings)
    {
        this.serializers = Collections.unmodifiableList(serializers);
        this.defaultSettings = defaultSettings != null ? Collections.unmodifiableMap(defaultSettings) : Collections.EMPTY_MAP;
        binding = new XMLBinding();
        setupAliases(binding);
    }
    
    public List getSerializers()
    {
        return serializers;
    }
    
    public Map getDefaultSettings()
    {
        return defaultSettings;
    }

    public void importData(String filename) throws SerializerException
    {
        importData(filename, null);
    }
    
    public void importData(String filename, Map settings) throws SerializerException
    {
        Map processSettings = getProcessSettings(settings);
        JSSnapshot snapshot = readSnapshot(filename);

        if (snapshot == null)
            throw new SerializerException(SerializerException.FILE_PROCESSING_ERROR.create(new String[] { filename,
                    "Snapshot is NULL" }));

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
    
    public void exportData(String name, String filename, Map settings) throws SerializerException
    {
        Map processSettings = getProcessSettings(settings);
        JSSnapshot snapshot = new JSSnapshot(name);
        snapshot.setDateCreated(new Date(new java.util.Date().getTime()).toString());
        snapshot.setSavedVersion(snapshot.getSoftwareVersion());
        snapshot.setSavedSubversion(snapshot.getSoftwareSubVersion());

        for (int i = 0, size = serializers.size(); i < size; i++)
        {
            ((JetspeedComponentSerializer) serializers.get(i)).processExport(snapshot, processSettings);
        }
        
        writeSnapshot(snapshot, filename, binding, processSettings);
    }
    
    public void deleteData() throws SerializerException
    {
        deleteData(null);
    }
    
    public void deleteData(Map settings) throws SerializerException
    {
        Map processSettings = getProcessSettings(settings);
        for (int i = 0, size = serializers.size(); i < size; i++)
        {
            ((JetspeedComponentSerializer) serializers.get(i)).deleteData(processSettings);
        }
    }

    protected Map getProcessSettings(Map settings)
    {
        Map processSettings = new HashMap(defaultSettings);
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
        binding.setAlias(JSEntityPreference.class, "Principal");
        binding.setAlias(JSEntityPreferences.class, "Settings");

        binding.setAlias(String.class, "String");
        binding.setAlias(Integer.class, "int");

        binding.setClassAttribute(null);
    }

    protected JSSnapshot readSnapshot(String importFileName) throws SerializerException
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
            snap = (JSSnapshot) reader.read(TAG_SNAPSHOT, JSSnapshot.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
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

    protected void writeSnapshot(JSSnapshot snapshot, String filename, XMLBinding binding, Map settings) throws SerializerException
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
            writer.write(snapshot, TAG_SNAPSHOT, JSSnapshot.class);

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
    protected XMLObjectWriter openWriter(String filename, Map settings) throws SerializerException
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
    protected static boolean isSettingSet(Map settings, String key)
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