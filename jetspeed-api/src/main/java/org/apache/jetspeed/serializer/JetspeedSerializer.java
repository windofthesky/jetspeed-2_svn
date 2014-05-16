/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.util.List;
import java.util.Map;

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
    short ERROR_DECODING = -1;

    /** the passwords are in clear text */
    short NO_DECODING = 0;

    /**
     * the passwords are encoded and the provider is the same as the data
     * source, but is a 1-way algorithm
     */
    short PASSTHRU_REQUIRED = 1;

    /**
     * the passwords are encoded and the provider is the same as the data source
     * and we have a 2-way algorithm
     */
    short DECODING_SUPPORTED = 2;

    /** the passwords are encoded and the current provider is DIFFERENT.... */
    short INVALID_PASSWORDS = 3;

    /** export/import instructions */

    String KEY_PROCESS_USERS = "process_users";
    String KEY_PROCESS_CAPABILITIES = "process_capabilities";
    String KEY_PROCESS_PROFILER = "process_profiler";
    String KEY_PROCESS_PERMISSIONS = "process_permissions";
    String KEY_PROCESS_USER_PREFERENCES = "process_user_preferences";
    String KEY_PROCESS_PORTAL_PREFERENCES = "process_portal_preferences";
    String KEY_PROCESS_USER_TEMPLATES = "process_user_templates";
    String KEY_PROCESS_SSO = "process_sso";
    String KEY_LOGGER = "logger";
    String KEY_OVERWRITE_EXISTING = "overwrite_existing";
    String KEY_BACKUP_BEFORE_PROCESS = "backup_before_process";
    String KEY_EXPORT_INDENTATION = "export_indentation";
    
    /**<p> the main tag in the XML file */
    String DEFAULT_TAG_SNAPSHOT_NAME = "Snapshot"; 
    String [] TAG_SNAPSHOT_NAMES = new String[] {DEFAULT_TAG_SNAPSHOT_NAME, "SecondaryData"}; 
    
    List<JetspeedComponentSerializer> getSerializers();
    Map<String, Object> getDefaultSettings();
    
    /**
     * Main routine to export the set of data elements and write them to the
     * named XML file.
     * 
     * @param name
     *            of the snapshot
     * @param exportFileName
     */
    void exportData(String name, String exportFileName) throws SerializerException;

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
    void exportData(String name, String exportFileName, Map<String,Object> settings) throws SerializerException;

    /**
     * Main routine to import the set of data elements and write them to the
     * current environment.
     * 
     * @param importFileName
     * @return
     */
    void importData(String importFileName) throws SerializerException;
    
    /**
     * Main routine to import the set of data elements and write them to the
     * current environment.
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
    void importData(String importFileName, Map<String,Object> settings) throws SerializerException;
    
    void deleteData() throws SerializerException;
    void deleteData(Map<String,Object> settings) throws SerializerException;
}
