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

/**
 * Jetspeed Serializer Application
 * 
 * invoke with mandatory 
 * <p>-E filename or -I filename to denote the export or the import file</p>
 * <p>-I filename | directory, if a directory will process all XML files of pattern "*seed.xml"</p>
 * 
 * invoke with (optional) parameters as
 * <p>-p propertyFilename : overwrite the default filename defined in System.getProperty JetSpeed.Serializer.Configuration</p> 
 * <p>-a ApplicationPath : overwrite the default ./ or ApplicationPath property in properties file)</p>
 * <p>-b bootPath : directory to Spring boot files,   overwrite the default assembly/boot/ or bootPath  property in properties file)</p>  
 * <p>-c configPath : directory to Spring config files,   overwrite the default assembly/ or configPath property in properties file)</p>
 * 
 * <p>-O optionstring : overwrite defrault "ALL,REPLACE"</p>
 * <p>optionstring: 
 *      ALL - extract/import all (with exception of PREFERENCES)
 *      USER - extract/import users
 *      CAPABILITIES - extract/import capabilities
 *      PROFILE = extract/import profile settings (for export requires USER) 
 *      PREFS = extract/import  portlet preferences (ignored if any of the above is set)
 *      
 *      NOOVERWRITE = don't overwrite existing file (for export)
 *      BACKUP = backup before process
 * </p>
 * <p>
 * -dc driverClass, for example com.mysql.jdbc.Driver
 * </p>
 * <p>
 * -ds url, ruls according to the driver used, URL needs to point to the correct
 * database
 * </p>
 * <p>
 * -du user, user with create/drop etc. rights on the database
 * </p>
 * <p>
 * -dp password
 * </p>
 * 
 * <p>
 * -l log4j-level, ERROR (default), WARN, INFO 
 * </p>
 * 
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $Id$
 */
public class JetspeedSerializerApplication
{
    // This class needs to be reimplemented after the JetspeedSerializer refactoring is completed
    // and the main part of the new maven-2 build environment is setup.
    // See: JS2-770 & JS2-771
}