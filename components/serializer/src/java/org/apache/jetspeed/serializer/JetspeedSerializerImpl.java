/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.serializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.security.auth.Subject;

import javolution.xml.XMLBinding;
import javolution.xml.XMLObjectReader;
import javolution.xml.XMLObjectWriter;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.capabilities.Capability;
import org.apache.jetspeed.capabilities.Client;
import org.apache.jetspeed.capabilities.MediaType;
import org.apache.jetspeed.capabilities.MimeType;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.apache.jetspeed.profiler.Profiler;
import org.apache.jetspeed.profiler.rules.PrincipalRule;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.security.BasePrincipal;
import org.apache.jetspeed.security.Group;
import org.apache.jetspeed.security.GroupManager;
import org.apache.jetspeed.security.PasswordCredential;
import org.apache.jetspeed.security.PermissionManager;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.User;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.om.InternalPermission;
import org.apache.jetspeed.security.om.InternalPrincipal;
import org.apache.jetspeed.security.spi.PasswordCredentialProvider;
import org.apache.jetspeed.serializer.objects.JSCapabilities;
import org.apache.jetspeed.serializer.objects.JSCapability;
import org.apache.jetspeed.serializer.objects.JSClient;
import org.apache.jetspeed.serializer.objects.JSClientCapabilities;
import org.apache.jetspeed.serializer.objects.JSClientMimeTypes;
import org.apache.jetspeed.serializer.objects.JSClients;
import org.apache.jetspeed.serializer.objects.JSGroup;
import org.apache.jetspeed.serializer.objects.JSGroups;
import org.apache.jetspeed.serializer.objects.JSMediaType;
import org.apache.jetspeed.serializer.objects.JSMediaTypes;
import org.apache.jetspeed.serializer.objects.JSMimeType;
import org.apache.jetspeed.serializer.objects.JSMimeTypes;
import org.apache.jetspeed.serializer.objects.JSNameValuePairs;
import org.apache.jetspeed.serializer.objects.JSPermission;
import org.apache.jetspeed.serializer.objects.JSPermissions;
import org.apache.jetspeed.serializer.objects.JSPrincipalRule;
import org.apache.jetspeed.serializer.objects.JSPrincipalRules;
import org.apache.jetspeed.serializer.objects.JSProfilingRule;
import org.apache.jetspeed.serializer.objects.JSProfilingRules;
import org.apache.jetspeed.serializer.objects.JSRole;
import org.apache.jetspeed.serializer.objects.JSRoles;
import org.apache.jetspeed.serializer.objects.JSRuleCriterion;
import org.apache.jetspeed.serializer.objects.JSRuleCriterions;
import org.apache.jetspeed.serializer.objects.JSSnapshot;
import org.apache.jetspeed.serializer.objects.JSUser;
import org.apache.jetspeed.serializer.objects.JSUserAttributes;
import org.apache.jetspeed.serializer.objects.JSUserGroups;
import org.apache.jetspeed.serializer.objects.JSUserRoles;
import org.apache.jetspeed.serializer.objects.JSUserUsers;
import org.apache.jetspeed.serializer.objects.JSUsers;

/**
 * Jetspeed Serializer
 * <p>
 * The Serializer is capable of reading and writing the current content of the
 * Jetspeed environment to and from XML files. The component can be used from a
 * standalone java application for seeding a new database or from a running
 * portal as an administrative backup/restore function.
 * <p>
 * The XML file needs to indicate whether passwords used in credentials
 * are plain text or whether they are encoded. The import algoritm can determine -
 * prior to reading users - which encode/decode scheme was used and if <none> or
 * <implements PasswordEncodingService> then we store plain passwords (Note that
 * that alone requires the resulting XML to be encoded!!!!!)
 * 
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
public class JetspeedSerializerImpl implements JetspeedSerializer
{

    /** Logger */
    private static final Log log = LogFactory.getLog(JetspeedSerializer.class);

    private ComponentManager cm = null;

    private HashMap roleMap = new HashMap();

    private HashMap groupMap = new HashMap();

    private HashMap userMap = new HashMap();

    private HashMap mimeMap = new HashMap();

    private HashMap mediaMap = new HashMap();

    private HashMap capabilityMap = new HashMap();

    private HashMap clientMap = new HashMap();

    private HashMap permissionMap = new HashMap();

    private HashMap rulesMap = new HashMap();

    int refCouter = 0;

    /** the main wrapper class for an XML file */
    private JSSnapshot snapshot;

    /** processing flags */
    /** export/import instructions */

    private HashMap processSettings = new HashMap();

    private boolean initialized = false;

    /** current indent for XML files - defaults to tab */
    private String currentIndent = null;

    public JetspeedSerializerImpl()
    {
    }

    /**
     * hand over existing component manager
     * 
     * @param cm
     */
    public JetspeedSerializerImpl(ComponentManager cm)
    {
        this.setComponentManager(cm);
        this.initialized = true;
    }

    /**
     * This constructor takes the application root, the search path for the boot
     * component configuration files and the search path for the application
     * component configuration files.
     * <p>
     * For example: new JetspeedSerializerImpl("./", "assembly/boot/*.xml",
     * "assembly/*.xml") will establish the current directory as the root,
     * process all xml files in the assembly/boot directory before processing
     * all xml files in the assembly directory itself.
     * 
     * @param appRoot
     *            working directory
     * @param bootConfig
     *            boot (primary) file or files (wildcards are allowed)
     * @param appConfig
     *            application (secondary) file or files (wildcards are allowed)
     */
    public JetspeedSerializerImpl(String appRoot, String[] bootConfig,
            String[] appConfig) throws SerializerException
    {
        this.initializeComponentManager(appRoot, bootConfig, appConfig);
        this.initialized = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#nitializeComponentManager(String,String[],String[])
     */
    public void initializeComponentManager(String appRoot, String[] bootConfig,
            String[] appConfig) throws SerializerException
    {
        if (this.initialized)
            throw new SerializerException(
                    SerializerException.COMPONENT_MANAGER_EXISTS.create(""));
        SpringComponentManager cm = new SpringComponentManager(bootConfig,
                appConfig, appRoot);
        cm.start();
        Configuration properties = (Configuration) new PropertiesConfiguration();
        properties.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY,
                appRoot);
        this.setComponentManager(cm);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#setComponentManager(ComponentManager)
     */
    public void setComponentManager(ComponentManager cm)
    {
        this.cm = cm;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#closeUp()
     */
    public void closeUp()
    {
        if (cm != null) cm.stop();
        cm = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#setDefaultIndent(String)
     */
    public void setDefaultIndent(String indent)
    {
        this.currentIndent = indent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#getDefaultIndent()
     */
    public String getDefaultIndent()
    {
        return this.currentIndent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#importData(String,
     *      Map)
     */
    public void importData(String importFileName, Map settings)
            throws SerializerException
    {
        /** pre-processing homework... */
        XMLBinding binding = new XMLBinding();
        setupAliases(binding);
        checkSettings(settings);

        this.snapshot = readFile(importFileName, binding);

        if (this.snapshot == null)
            throw new SerializerException(
                    SerializerException.FILE_PROCESSING_ERROR
                            .create(new String[]
                            { importFileName, "Snapshot is NULL"}));

        if (!(this.snapshot.checkVersion()))
            throw new SerializerException(
                    SerializerException.INCOMPETIBLE_VERSION
                            .create(new String[]
                            {
                                    importFileName,
                                    String.valueOf(this.snapshot
                                            .getSoftwareVersion()),
                                    String.valueOf(this.snapshot
                                            .getSavedSubversion())}));

        /** ok, now we have a valid snapshot and can start processing it */

        // TODO: HJB complete rules/users etc. for read/write
        // TODO: HJB rebuild objkect references from string lists
        
        
        TEST_ONLY(binding, importFileName);

        // TODO: HJB build simple j2 types (like Mime) first, if excluded read current ones
        // TODO: HJB Reuse lookup tables with environment objects
        // TODO: HJB Make sure to clean lookup tables before next run
        
        

        /** ensure we can work undisturbed */
        synchronized (cm)
        {
            logMe("*********Reading data*********");
            this.processImport();
        }
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.serializer.JetspeedSerializer#exportData(String,String,Map)
     */
    public void exportData(String name, String exportFileName, Map settings)
            throws SerializerException
    {
        /** pre-processing homework... */
        XMLBinding binding = new XMLBinding();
        setupAliases(binding);
        checkSettings(settings);

        /** ensure we can work undisturbed */
        synchronized (cm)
        {
            /** get the snapshot construct */
            this.processExport(name, binding);
            XMLObjectWriter writer = openWriter(exportFileName);
            writer.setBinding(binding);

            if (this.getDefaultIndent() != null)
                writer.setIndentation(this.getDefaultIndent());

            try
            {
                logMe("*********Writing data*********");
                writer.write(this.snapshot, JetspeedSerializer.TAG_SNAPSHOT,
                        JSSnapshot.class);

            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.FILE_PROCESSING_ERROR
                                .create(new String[]
                                { exportFileName, e.getMessage()}));
            } finally
            {
                /** ensure the writer is closed */
                try
                {
                    logMe("*********closing up********");
                    writer.close();
                } catch (Exception e)
                {
                    logMe("Error in closing writer " + e.getMessage());
                    /**
                     * don't do anything with this exception - never let the
                     * bubble out of the finally block
                     */
                }
            }
        }
        return;
    }

    /**
     * create a backup of the current environment in case the import fails
     * 
     */
    private void doBackupOfCurrent(String importFileName, Map currentSettings)
    {
        // TODO: HJB create backup of current content
    }

    /**
     * read a snapshot and return the reconstructed class tree
     * 
     * @param importFileName
     * @throws SerializerException
     */

    private JSSnapshot readFile(String importFileName, XMLBinding binding)
            throws SerializerException
    {
        XMLObjectReader reader = null;
        JSSnapshot snap = null;
        try
        {
            reader = XMLObjectReader.newInstance(new FileInputStream(
                    importFileName));
        } catch (Exception e)
        {
            throw new SerializerException(SerializerException.FILE_READER_ERROR
                    .create(new String[]
                    { importFileName, e.getMessage()}));
        }
        try
        {
            if (binding != null) reader.setBinding(binding);
            snap = (JSSnapshot) reader.read(JetspeedSerializer.TAG_SNAPSHOT,
                    JSSnapshot.class);

        } catch (Exception e)
        {
            e.printStackTrace();
            new SerializerException(SerializerException.FILE_PROCESSING_ERROR
                    .create(new String[]
                    { importFileName, e.getMessage()}));
        } finally
        {
            /** ensure the reader is closed */
            try
            {
                logMe("*********closing up reader ********");
                reader.close();
            } catch (Exception e1)
            {
                logMe("Error in closing reader " + e1.getMessage());
                /**
                 * don't do anything with this exception - never let the bubble
                 * out of the finally block
                 */
                return null;
            }
        }
        return snap;
    }

    /**
     * create or open a given file for writing
     */
    private XMLObjectWriter openWriter(String filename)
            throws SerializerException
    {
        File f;

        try
        {
            f = new File(filename);
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.FILE_PROCESSING_ERROR
                            .create(new String[]
                            { filename, e.getMessage()}));
        }
        boolean exists = f.exists();

        if (exists)
        {
            if (!(this.getSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING)))
                throw new SerializerException(
                        SerializerException.FILE_ALREADY_EXISTS
                                .create(filename));
            if (this.getSetting(JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS))
            {
                String backName = createUniqueBackupFilename(f.getName());
                if (backName == null)
                    throw new SerializerException(
                            SerializerException.FILE_BACKUP_FAILED
                                    .create(filename));
                File ftemp = new File(backName);
                f.renameTo(ftemp);
            }
        }
        try
        {
            XMLObjectWriter writer = XMLObjectWriter
                    .newInstance(new FileOutputStream(filename));
            return writer;
        } catch (Exception e)
        {
            throw new SerializerException(SerializerException.FILE_WRITER_ERROR
                    .create(new String[]
                    { filename, e.getMessage()}));
        }
    }

    /**
     * returns the key for a particular process setting. False if the key
     * doesn't exist.
     * 
     * @param key
     * @return
     */
    public boolean getSetting(String key)
    {
        Object o = processSettings.get(key);
        if ((o == null) || (!(o instanceof Boolean))) return false;
        return ((Boolean) o).booleanValue();
    }

    /**
     * set a process setting for a given key
     * 
     * @param key
     *            instruction to set
     * @param value
     *            true or false
     */
    private void setSetting(String key, boolean value)
    {
        processSettings.put(key, (value ? Boolean.TRUE : Boolean.FALSE));
    }

    /**
     * reset instruction flags to default settings (all true)
     * 
     */
    private void resetSettings()
    {
        setSetting(JetspeedSerializer.KEY_PROCESS_USERS, true);
        setSetting(JetspeedSerializer.KEY_PROCESS_CAPABILITIES, true);
        setSetting(JetspeedSerializer.KEY_PROCESS_PROFILER, true);
        setSetting(JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES, true);
        setSetting(JetspeedSerializer.KEY_OVERWRITE_EXISTING, true);
        setSetting(JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS, true);
    }

    /**
     * set instruction flags to new settings
     * 
     * @param settings
     */
    private void checkSettings(Map settings)
    {
        /** ensure we don't have settings from a previous run */
        resetSettings();
        /** process the new isntructionSet */
        if ((settings == null) || (settings.size() == 0)) return;
        Iterator _it = settings.keySet().iterator();
        while (_it.hasNext())
        {
            try
            {
                String key = (String) _it.next();
                Object o = settings.get(key);
                if ((o != null) && (o instanceof Boolean))
                    setSetting(key, ((Boolean) o).booleanValue());
            } catch (Exception e)
            {
                log.error("checkSettings", e);
            }
        }
    }

    /**
     * On import, get the basic SnapShot data
     * 
     */
    private void getSnapshotData()
    {
        logMe("date created : " + snapshot.getDateCreated());
        logMe("software Version : " + snapshot.getSavedVersion());
        logMe("software SUbVersion : " + snapshot.getSavedSubversion());
    }

    /**
     * On export, set the basic SnapShot data
     * 
     */
    private void setSnapshotData()
    {
        Date d = new Date();
        snapshot.setDateCreated(d.toString());
        snapshot.setSavedVersion(JSSnapshot.softwareVersion);
        snapshot.setSavedSubversion(JSSnapshot.softwareSubVersion);
    }

    private void importCapabilitiesInfrastructure()
    {
        System.out.println("importCapabilitiesInfrastructure - processing");
    }

    private void importUsers()
    {
        System.out.println("importUsers - processing");

    }

    private void importProfiler()
    {
        System.out.println("importProfiler - processing");

    }

    /**
     * The workhorse for importing data
     * 
     * @param binding
     *            established XML binding
     * @return
     * @throws SerializerException
     */
    private void processImport() throws SerializerException
    {
        this.logMe("*********reinstalling data*********");

        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_CAPABILITIES))
        {
            logMe("creating clients, mediatypes and mimetypes");
            importCapabilitiesInfrastructure();
        } else
            logMe("capabilities skipped");

        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_USERS))
        {
            logMe("creating users/roles/groups");
            importUsers();
        } else
            logMe("users skipped");

        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_PROFILER))
        {
            logMe("collecting permissions, profiling rules etc.");
            importProfiler();
        } else
            logMe("permissions, rules etc. skipped ");

    }

    /**
     * The workhorse for exporting data
     * 
     * @param binding
     *            established XML binding
     * @return
     * @throws SerializerException
     */
    private void processExport(String name, XMLBinding binding)
            throws SerializerException
    {
        this.logMe("*********collecting data*********");
        /** first create the snapshot file */

        this.snapshot = new JSSnapshot(name);

        setSnapshotData();

        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_CAPABILITIES))
        {
            logMe("collecting clients, mediatypes and mimetypes");
            exportCapabilitiesInfrastructure();
        } else
            logMe("capabilities skipped");

        if (this.getSetting(JetspeedSerializer.KEY_PROCESS_USERS))
        {
            logMe("collecting users");
            exportUsers();

            /** permissions require users - hence inside this scope */
            if (this.getSetting(JetspeedSerializer.KEY_PROCESS_PROFILER))
            {
                logMe("collecting permissions, profiling rules etc.");
                this.getProfilingRules();
            } else
                logMe(" profiling rules etc. skipped");

        } else
            logMe("users skipped");

    }

    /**
     * Setup the binding for the different classes, mapping each extracted class
     * to a unique tag name in the XML
     * 
     * @param binding
     */
    private void setupAliases(XMLBinding binding)
    {
        binding.setAlias(JSRole.class, "role");
        binding.setAlias(JSRoles.class, "all_roles");
        binding.setAlias(JSGroup.class, "group");
        binding.setAlias(JSGroups.class, "all_groups");
        binding.setAlias(JSUser.class, "user");
        binding.setAlias(JSUsers.class, "all_users");
        binding.setAlias(JSNameValuePairs.class, "preferences");
        binding.setAlias(JSUserAttributes.class, "userinfo");
        binding.setAlias(JSSnapshot.class, "snapshot");
        binding.setAlias(JSUserRoles.class, "roles");
        binding.setAlias(JSUserGroups.class, "groups");
        binding.setAlias(JSClient.class, "client");
        binding.setAlias(JSClients.class, "all_clients");
        binding.setAlias(JSClientCapabilities.class, "capabilities");
        binding.setAlias(JSClientMimeTypes.class, "mimeTypes");
        binding.setAlias(JSMimeTypes.class, "all_mimeTypes");
        binding.setAlias(JSMimeType.class, "all_mimeType");
        binding.setAlias(JSCapabilities.class, "all_capabilities");
        binding.setAlias(JSCapability.class, "capability");
        binding.setAlias(JSMediaTypes.class, "all_mediaTypes");
        binding.setAlias(JSMediaType.class, "mediaType");
        binding.setAlias(JSUserUsers.class, "users");

        binding.setAlias(JSPermissions.class, "permissions");
        binding.setAlias(JSPermission.class, "permission");
        binding.setAlias(JSProfilingRules.class, "profiling_rules");
        binding.setAlias(JSProfilingRule.class, "profiling_rule");
        binding.setAlias(JSRuleCriterions.class, "criteria");
        binding.setAlias(JSRuleCriterion.class, "criterion");

        binding.setAlias(JSPrincipalRule.class, "rule");
        binding.setAlias(JSPrincipalRules.class, "user_rules");

        binding.setAlias(String.class, "String");
        binding.setAlias(Integer.class, "int");

        binding.setClassAttribute("type");

    }

    private Object getObjectBehindPrinicpal(Map map, BasePrincipal _principal)
    {
        return getObjectBehindPath(map, _principal.getFullPath());
    }

    private Object getObjectBehindPath(Map map, String _fullPath)
    {
        return map.get(_fullPath);
    }

    /** ROLES ----------- */
    private JSRole createJSRole(Role role)
    {
        JSRole _role = new JSRole();
        _role.setName(role.getPrincipal().getName());
        return _role;
    }

    private void exportRoles() throws SerializerException
    {
        RoleManager roleManager = (RoleManager) cm
                .getComponent("org.apache.jetspeed.security.RoleManager");
        if (roleManager == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.security.RoleManager"));

        Iterator list = null;
        try
        {
            list = roleManager.getRoles("");
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.GET_EXISTING_OBJECTS
                            .create(new String[]
                            { "Role", e.getMessage()}));
        }
        int count = 0;
        while (list.hasNext())
        {
            try
            {
                Role role = (Role) list.next();
                JSRole _tempRole = (JSRole) getObjectBehindPrinicpal(roleMap,
                        (BasePrincipal) (role.getPrincipal()));
                if (_tempRole == null)
                {
                    _tempRole = createJSRole(role);
                    roleMap.put(_tempRole.getName(), _tempRole);
                    snapshot.getRoles().add(_tempRole);
                }

            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "Role", e.getMessage()}));
            }
        }
        return;
    }

    /** Groups -------------------------- */
    /**
     * create a wrapper JSGroup object
     */
    private JSGroup createJSGroup(Group group)
    {
        JSGroup _group = new JSGroup();
        _group.setName(group.getPrincipal().getName());
        return _group;
    }

    /**
     * extract the groups from the current environment
     * 
     * @throws SerializerException
     */
    private void exportGroups() throws SerializerException
    {
        GroupManager groupManager = (GroupManager) cm
                .getComponent("org.apache.jetspeed.security.GroupManager");
        if (groupManager == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.security.GroupManager"));
        Iterator list = null;
        try
        {
            list = groupManager.getGroups("");
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.GET_EXISTING_OBJECTS
                            .create(new String[]
                            { "Group", e.getMessage()}));
        }
        int count = 0;
        while (list.hasNext())
        {

            try
            {
                Group group = (Group) list.next();
                JSGroup _tempGroup = (JSGroup) getObjectBehindPrinicpal(
                        groupMap, (BasePrincipal) (group.getPrincipal()));
                if (_tempGroup == null)
                {
                    _tempGroup = createJSGroup(group);
                    groupMap.put(_tempGroup.getName(), _tempGroup);
                    snapshot.getGroups().add(_tempGroup);
                }

            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "Group", e.getMessage()}));
            }
        }
        return;
    }

    /**
     * Add the credentials to the JSUser object.
     * <p>
     * If the credential provided is a PasswordCredential, userid and password
     * are extracted and set explcitely
     * 
     * @param isPublic
     *            public or private credential
     * @param newUser
     *            the JS user object reference
     * @param credential
     *            the credential object
     */

    private void addJSUserCredentials(boolean isPublic, JSUser newUser,
            Object credential)
    {
        if (credential == null) return;
        if (credential instanceof PasswordCredential)
        {
            PasswordCredential pw = (PasswordCredential) credential;
            newUser.setUserCredential(pw.getUserName(), pw.getPassword());
            return;
        } else if (isPublic)
            newUser.addPublicCredential(credential);
        else
            newUser.addPrivateCredential(credential);
    }

    /**
     * create a new JSUser object
     * 
     * @param user
     * @return a new JSUser object
     */
    private JSUser createJSUser(User user)
    {
        JSUser _newUser = new JSUser();

        Subject subject = user.getSubject();
        // get the user principal
        Set principals = subject.getPrincipals();
        Iterator list = principals.iterator();
        while (list.hasNext())
        {
            BasePrincipal principal = (BasePrincipal) list.next();
            String path = principal.getFullPath();
            if (path.startsWith("/role/"))
            {
                JSRole _tempRole = (JSRole) this.getObjectBehindPath(roleMap,
                        principal.getName());
                if (_tempRole != null)
                {
                    _newUser.addRole(_tempRole);
                }

            } else
            {
                if (path.startsWith("/group/"))
                {
                    JSGroup _tempGroup = (JSGroup) this.getObjectBehindPath(
                            groupMap, principal.getName());
                    if (_tempGroup != null)
                    {
                        _newUser.addGroup(_tempGroup);
                    }

                } else if (path.startsWith("/user/"))
                    _newUser.setPrincipal(principal);

            }

        }
        // System.out.println("User Public Credentials");
        Set credentials = subject.getPublicCredentials();
        list = credentials.iterator();
        while (list.hasNext())
        {
            Object credential = list.next();
            addJSUserCredentials(true, _newUser, credential);
        }
        // System.out.println("User Private Credentials");
        credentials = subject.getPrivateCredentials();
        list = credentials.iterator();
        while (list.hasNext())
        {
            Object credential = list.next();
            addJSUserCredentials(false, _newUser, credential);
        }

        Preferences preferences = user.getPreferences();
        _newUser.setPreferences(preferences);
        preferences = user.getUserAttributes();
        _newUser.setUserInfo(preferences);
        return _newUser;
    }

    private String getCurrentSecurityProvider()
    {
        PasswordCredentialProvider provider = (PasswordCredentialProvider) cm
                .getComponent("org.apache.jetspeed.security.spi.PasswordCredentialProvider");
        if (provider == null) return "";
        return provider.getEncoder().getClass().getName();
    }

    /**
     * Collect all the roles, groups and users from the current environment.
     * Include the current SecurityProvider to understand, whether the password
     * collected can be used upon import
     * 
     * @throws SerializerException
     */

    private void exportUsers() throws SerializerException
    {
        /** set the security provider info in the snapshot file */
        this.snapshot.setEncryption(getCurrentSecurityProvider());
        /** get the roles */
        exportRoles();
        /** get the groups */
        exportGroups();

        /** users */
        UserManager userManager = (UserManager) cm
                .getComponent("org.apache.jetspeed.security.UserManager");
        if (userManager == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.security.UserManager"));
        Iterator list = null;
        try
        {
            list = userManager.getUsers("");
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.GET_EXISTING_OBJECTS
                            .create(new String[]
                            { "User", e.getMessage()}));
        }
        int count = 0;
        while (list.hasNext())
        {

            try
            {
                User _user = (User) list.next();
                JSUser _tempUser = createJSUser(_user);
                userMap.put(_tempUser.getName(), _tempUser);
                snapshot.getUsers().add(_tempUser);
            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "User", e.getMessage()}));
            }

        }
        return;

    }

    /**
     * extract the capabilities and save in snapshot file
     * 
     * @param caps
     *            capability-container
     * @throws SerializerException
     */
    private void exportCapabilites(Capabilities caps)
            throws SerializerException
    {

        Iterator list = caps.getCapabilities();

        while (list.hasNext())
        {
            try
            {
                Capability _cp = (Capability) list.next();
                JSCapability _jsC = new JSCapability(_cp);
                this.capabilityMap.put(_jsC.getName(), _jsC);
                this.snapshot.getCapabilities().add(_jsC);
            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "C", e.getMessage()}));
            }
        }
        return;

    }

    /**
     * exstract the mime types anmd save in snapshot file
     * 
     * @param caps
     *            capability container
     * @throws SerializerException
     */
    private void exportMimeTypes(Capabilities caps) throws SerializerException
    {

        Iterator list = caps.getMimeTypes();

        while (list.hasNext())
        {
            try
            {
                MimeType _mt = (MimeType) list.next();
                JSMimeType _jsM = new JSMimeType(_mt);
                this.mimeMap.put(_jsM.getName(), _jsM);
                this.snapshot.getMimeTypes().add(_jsM);
            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "MimeType", e.getMessage()}));
            }
        }
        return;

    }

    /**
     * create a JS CLient
     * 
     * @param c
     *            the existing Client object
     * @return
     * @throws SerializerException
     */

    private JSClient createJSClient(Client c) throws SerializerException
    {
        try
        {
            JSClient jsC = new JSClient(c);
            // find the mimeTypes
            Iterator _itM = c.getMimetypes().iterator();
            while (_itM.hasNext())
            {
                MimeType _m = (MimeType) _itM.next();
                JSMimeType _mt = (JSMimeType) mimeMap.get(_m.getName());
                if (_mt != null) jsC.getMimeTypes().add(_mt);
            }
            // find the capabilities
            Iterator _itC = c.getCapabilities().iterator();
            while (_itC.hasNext())
            {
                Capability _c = (Capability) _itC.next();
                JSCapability _ct = (JSCapability) capabilityMap.get(_c
                        .getName());
                if (_ct != null) jsC.getCapabilities().add(_ct);
            }

            return jsC;
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                            .create(new String[]
                            { "Client", e.getMessage()}));
        }

    }

    /**
     * extract the current clients and save in the snapshot file
     * 
     * @param list
     * @param caps
     * @return
     * @throws SerializerException
     */
    private void exportClients(Capabilities caps) throws SerializerException
    {

        /** first the the mime types */
        exportMimeTypes(caps);

        /** second get the capabilities */
        this.exportCapabilites(caps);

        /** now get the clients */
        Iterator _it = caps.getClients();
        while (_it.hasNext())
        {
            Client c = (Client) _it.next();
            JSClient jsC = createJSClient(c);
            if (jsC == null)
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "Client", "createClient returned NULL"}));
            this.clientMap.put(jsC.getName(), jsC);
            this.snapshot.getClients().add(jsC);
        }
        return;
    }

    /**
     * extract the media types and save in snapshot file
     * 
     * @param caps
     *            capabilit container
     * @throws SerializerException
     */
    private void exportMediaTypes(Capabilities caps) throws SerializerException
    {
        Iterator list = caps.getMediaTypes();

        int count = 0;

        while (list.hasNext())
        {
            try
            {
                MediaType _mt = (MediaType) list.next();
                JSMediaType _jsM = new JSMediaType(_mt);
                // find the mimeTypes
                Iterator _itM = _mt.getMimetypes().iterator();
                while (_itM.hasNext())
                {
                    MimeType _m = (MimeType) _itM.next();
                    JSMimeType _mttype = (JSMimeType) mimeMap.get(_m.getName());
                    if (_mttype != null) _jsM.getMimeTypes().add(_mttype);
                }
                // find the capabilities
                Iterator _itC = _mt.getCapabilities().iterator();
                while (_itC.hasNext())
                {
                    Capability _c = (Capability) _itC.next();
                    JSCapability _ct = (JSCapability) capabilityMap.get(_c
                            .getName());
                    if (_ct != null) _jsM.getCapabilities().add(_ct);
                }
                this.mediaMap.put(_jsM.getName(), _jsM);
                this.snapshot.getMediaTypes().add(_jsM);
            } catch (Exception e)
            {
                // do whatever
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "MediaType", e.getMessage()}));
            }
        }
        return;
    }

    /**
     * Extracts all capability related objects (clients, mimetypes and
     * mediatypes) and their relationships
     * 
     * @return
     */

    private void exportCapabilitiesInfrastructure() throws SerializerException
    {
        Capabilities caps = (Capabilities) cm
                .getComponent("org.apache.jetspeed.capabilities.Capabilities");
        if (caps == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.capabilities.Capabilities"));

        /**
         * get the clients (which in turn will get the mime types and
         * capailities)
         */

        exportClients(caps);
        // get the mediatTypes, too

        exportMediaTypes(caps);

    }

    /**
     * extract all permissions from the current environment
     * 
     * @throws SerializerException
     */
    private void getPermissions() throws SerializerException
    {
        Object o = null;
        PermissionManager pm = (PermissionManager) cm
                .getComponent("org.apache.jetspeed.security.PermissionManager");
        if (pm == null)
            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.security.PermissionManager"));

        Iterator list = null;
        try
        {
            list = pm.getPermissions().iterator();
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.GET_EXISTING_OBJECTS
                            .create(new String[]
                            { "Permissions", e.getMessage()}));
        }

        while (list.hasNext())
        {
            try
            {
                JSPermission _js = new JSPermission();

                InternalPermission p = (InternalPermission) list.next();
                _js.setResource(p.getName());
                _js.setActions(p.getActions());
                _js.setId(p.getPermissionId());
                _js.setType(_js.getTypeForClass(p.getClassname()));

                Iterator list2 = p.getPrincipals().iterator();
                while (list2.hasNext())
                {
                    o = list2.next();
                    InternalPrincipal principal = (InternalPrincipal) o;
                    String path = principal.getFullPath();
                    if (path.startsWith("/role/"))
                    {
                        JSRole _tempRole = (JSRole) this.getObjectBehindPath(
                                roleMap, removeFromString(path, "/role/"));
                        if (_tempRole != null)
                        {
                            _js.addRole(_tempRole);
                        }

                    } else
                    {
                        if (path.startsWith("/group/"))
                        {
                            JSGroup _tempGroup = (JSGroup) this
                                    .getObjectBehindPath(groupMap,
                                            removeFromString(path, "/group/"));
                            if (_tempGroup != null)
                            {
                                _js.addGroup(_tempGroup);
                            }

                        } else
                        {
                            if (path.startsWith("/user/"))
                            {
                                JSUser _tempUser = (JSUser) this
                                        .getObjectBehindPath(
                                                userMap,
                                                removeFromString(path, "/user/"));
                                if (_tempUser != null)
                                {
                                    _js.addUser(_tempUser);
                                }

                            }

                        }

                    }
                    this.permissionMap.put(_js.getType(), _js);
                    this.snapshot.getPermissions().add(_js);
                }

            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "Permissions", e.getMessage()}));
            }
        }
        return;

    }

    /**
     * Create the Profiling Rule Wrapper
     * 
     * @param p
     * @return
     */
    private JSProfilingRule createProfilingRule(ProfilingRule p)
    {
        JSProfilingRule rule = new JSProfilingRule();
        rule.setClassName(p.getClassname());
        rule.setDescription(p.getTitle());
        rule.setId(p.getId());

        Collection col = p.getRuleCriteria();
        Iterator keys = col.iterator();
        while (keys.hasNext())
        {
            RuleCriterion rc = (RuleCriterion) keys.next();
            rule.getCriterions().add(new JSRuleCriterion(rc));
        }
        return rule;

    }

    /**
     * read the permissions and then the profiling rules.
     * <p>
     * after that update the cross reference with the users
     * 
     * @throws SerializerException
     */
    private void getProfilingRules() throws SerializerException
    {
        getPermissions();
        Object o = null;
        Profiler pm = (Profiler) cm
                .getComponent("org.apache.jetspeed.profiler.Profiler");
        if (pm == null)

            throw new SerializerException(
                    SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
                            .create("org.apache.jetspeed.profiler.Profiler"));
        Iterator list = null;
        try
        {
            list = pm.getRules().iterator();
        } catch (Exception e)
        {
            throw new SerializerException(
                    SerializerException.GET_EXISTING_OBJECTS
                            .create(new String[]
                            { "ProfilingRules", e.getMessage()}));
        }
        int count = 0;

        while (list.hasNext())
        {
            try
            {
                ProfilingRule p = (ProfilingRule) list.next();
                if (!(this.rulesMap.containsKey(p.getId())))
                {
                    JSProfilingRule rule = createProfilingRule(p);
                    rulesMap.put(rule.getId(), rule);
                    snapshot.getRules().add(rule);

                }
            } catch (Exception e)
            {
                throw new SerializerException(
                        SerializerException.CREATE_SERIALIZED_OBJECT_FAILED
                                .create(new String[]
                                { "ProfilingRules", e.getMessage()}));
            }
        }

        // determine the defualt rule
        snapshot.setDefaultRule(pm.getDefaultRule().getId());

        // get Rules for each user

        Iterator _itUsers = this.userMap.values().iterator();
        while (_itUsers.hasNext())
        {
            JSUser _user = (JSUser) _itUsers.next();
            Principal principal = _user.getPrincipal();
            if (principal != null)
            {
                Collection col = pm.getRulesForPrincipal(principal);
                Iterator _itCol = col.iterator();
                while (_itCol.hasNext())
                {
                    PrincipalRule p1 = (PrincipalRule) _itCol.next();
                    JSPrincipalRule pr = new JSPrincipalRule(p1
                            .getLocatorName(), p1.getProfilingRule().getId());
                    _user.getRules().add(pr);
                }
            }
        }

        return;

    }

    /**
     * Establish whether incoming passwords are "clear" text or whether they are
     * to be decoded. That however depends on whether the passwords were encoded
     * with the current active provider or not.
     * 
     * @return
     */
    public int compareCurrentSecurityProvider(JSSnapshot file)
    {
        String _fileEncryption = file.getEncryption();
        if ((_fileEncryption == null) || (_fileEncryption.length() == 0))
            return NO_DECODING; // passwords are in clear text

        PasswordCredentialProvider provider = (PasswordCredentialProvider) cm
                .getComponent("org.apache.jetspeed.security.spi.PasswordCredentialProvider");
        if (provider == null)
        {
            System.err
                    .println("Error!!! PasswordCredentialProvider not available");
            return ERROR_DECODING;
        }

        boolean encodingSupported = (provider.getEncoder() instanceof org.apache.jetspeed.security.PasswordEncodingService);
        if (provider.getEncoder().getClass().getName().equals(_fileEncryption))
            return (encodingSupported ? DECODING_SUPPORTED : PASSTHRU_REQUIRED);
        else
            return INVALID_PASSWORDS;
    }

    /**
     * ++++++++++++++++++++++++++++++HELPERS
     * +++++++++++++++++++++++++++++++++++++++++++++
     */

    /**
     * remove a given sequence from the beginning of a string
     */
    private String removeFromString(String base, String excess)
    {
        return base.replaceFirst(excess, "").trim();
    }

    /**
     * 
     * just a Simple helper to make code more readable
     * 
     * @param text
     */
    private void logMe(String text)
    {
        if (log.isDebugEnabled()) log.debug(text);
    }

    /**
     * Helper routine to create a unique filename for a backup of an existing
     * filename....not intended to be rocket science...
     * 
     * @param name
     * @return
     */
    private String createUniqueBackupFilename(String name)
    {
        String newName = name + ".bak";

        File f = new File(newName);
        int counter = 0;
        if (!(f.exists())) return newName;
        while (counter < 100)
        {
            String newName1 = newName + counter;
            if (!(new File(newName1).exists())) return newName1;
            counter++;
        }
        return null;
    }

    /**
     * TEST ONLY
     */
    private void TEST_ONLY(XMLBinding binding, String filename)
            throws SerializerException
    {
        XMLObjectWriter writer = null;
        logMe("*********open test writer********");
        String fn = filename + ".testcopy.xml";
        try
        {
            writer = XMLObjectWriter.newInstance(new FileOutputStream(fn));
            writer.setBinding(binding);
            logMe("*********Writing test data*********");
            writer.write(this.snapshot, JetspeedSerializer.TAG_SNAPSHOT,
                    JSSnapshot.class);

        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            /** ensure the writer is closed */
            try
            {
                logMe("*********closing test writer********");
                writer.close();
            } catch (Exception e)
            {
                logMe("Error in closing test writer " + e.getMessage());
                /**
                 * don't do anything with this exception - never let the bubble
                 * out of the finally block
                 */
            }
        }

    }

    /**
     * TEMP ONLY - IGNORE
     */
    private void getGroups1() throws SerializerException
    {
        /**
         * GroupManager groupManager = (GroupManager) cm
         * .getComponent("org.apache.jetspeed.security.GroupManager"); throw new
         * SerializerException(
         * SerializerException.COMPONENTMANAGER_DOES_NOT_EXIST
         * .create("org.apache.jetspeed.security.GroupManager"));
         * 
         * throw new
         * SerializerException(SerializerException.GET_EXISTING_OBJECTS
         * .create("Group", e.getMessage())); throw new SerializerException(
         * SerializerException.CREATE_SERIALIZED_OBJECT_FAILED.create( "Group",
         * e.getMessage()));
         */
    }

}
