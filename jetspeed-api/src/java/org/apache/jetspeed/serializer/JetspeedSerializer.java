/**
 * Copyright 2004 The Apache Software Foundation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.serializer;

import java.util.Map;

import org.apache.jetspeed.components.ComponentManager;

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
 * @version $Id: JetspeedSerializer.java 0 2006-10-31 22:51:28Z hjb $
 * 
 */
public interface JetspeedSerializer
{

    /** Password handling */
    /** Error in determening correct password handling */
    public final static short ERROR_DECODING = -1;

    /** the passwords are in clear text */
    public final static short NO_DECODING = 0;

    /**
     * the passwords are encoded and the provider is the same as the data
     * source, but is a 1-way algorithm
     */
    public final static short PASSTHRU_REQUIRED = 1;

    /**
     * the passwords are encoded and the provider is the same as the data source
     * and we have a 2-way algorithm
     */
    public final static short DECODING_SUPPORTED = 2;

    /** the passwords are encoded and the current provider is DIFFERENT.... */
    public final static short INVALID_PASSWORDS = 3;

    /** export/import instructions */

    public final static String KEY_PROCESS_USERS = "process_users".intern();

    public final static String KEY_PROCESS_CAPABILITIES = "process_capabilities"
            .intern();

    public final static String KEY_PROCESS_PROFILER = "process_profiler"
            .intern();

    public final static String KEY_PROCESS_USER_PREFERENCES = "process_user_preferences"
            .intern();

    public final static String KEY_OVERWRITE_EXISTING = "overwrite_existing"
            .intern();

    public final static String KEY_BACKUP_BEFORE_PROCESS = "backup_before_process"
            .intern();

    /**<p> the main tag in the XML file */
    public final static String TAG_SNAPSHOT = "Snapshot"; 
    
    /**
     * hand the serializer an existing component manager to access the
     * environment
     * 
     * @param cm
     */
    public void setComponentManager(ComponentManager cm)
            throws SerializerException;

    /**
     * Create a component manager with the list of primary components (boot),
     * the application components and the root path of the application
     * 
     * @param appRoot
     *            working directory
     * @param bootConfig
     *            boot (primary) file or files (wildcards are allowed)
     * @param appConfig
     *            application (secondary) file or files (wildcards are allowed)
     * @return a newly initiated component manager
     * @throws SerializerException
     */
    public void initializeComponentManager(String appRoot, String[] bootConfig,
            String[] appConfig) throws SerializerException;

    /**
     * Main routine to export the set of data elements and write them to the
     * named XML file. The default behavior of the serializer is that all
     * available data is extracted and the target file gets created or
     * overwritten
     * <p>
     * The caller can adjust the default behavior by passign in a map of flags.
     * Each map entry is keyed by a key Constant and the associated Boolean
     * value, for exammple KEY_PROCESS_USER_PREFERENCES, Boolean.FALSE would
     * cause the serializer to skip user preferences.
     * <p>
     * Note that ProfilingRules require the users . Hence turning off User
     * collection will automatically turn off the Profiling rules
     * 
     * 
     * @param name
     *            of the snapshot
     * @param exportFileName
     * @param settings
     *            optional Map overwriting default export behavior
     */
    public void exportData(String name, String exportFileName, Map settings)
            throws SerializerException;

    /**
     * Main routine to import the set of data elements and write them to the
     * current environment. The default behavior of the serializer is that all
     * available data is read and written to the current environment.
     * <p>
     * Existing entries (like users) etc. will be overwritten with the provided
     * data.
     * <p>
     * The caller can adjust the default behavior by passign in a map of flags.
     * Each map entry is keyed by a key Constant and the associated Boolean
     * value, for exammple KEY_PROCESS_USER_PREFERENCES, Boolean.FALSE would
     * cause the serializer to skip user preferences.
     * <p>
     * Note that settings are valid throughout each invocation. Therefore if a
     * caller wants to preserve current users and only add new entries while at
     * the same time overwrite all profiling rules, exportData has to be invoked
     * twice - once to process only the users with the no-overwrite option and
     * once to process the profiling rules
     * 
     * @param importFileName
     * @param settings
     *            optional Map overwriting default import behavior
     * @return
     */
    public void importData(String importFileName, Map settings)
            throws SerializerException;

    /**
     * Set the default indent for the XML output
     * 
     * @param indent
     */
    public void setDefaultIndent(String indent);

    /**
     * Get the current indent setting for XML files
     * 
     * @return the current indent setting
     */
    public String getDefaultIndent();

    /**
     * reelase the resources etc.
     * 
     */
    public void closeUp();

}
