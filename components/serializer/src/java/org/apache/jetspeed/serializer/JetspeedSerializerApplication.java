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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * Jetspeed Serializer Application
 * 
 * invoke with mandatory 
 * <p>-E filename or -I filename to denote the export or the import file
 *   
 * invoke with (optional) parameters as
 * <p>-p propertyFilename : overwrite the default filename defined in System.getProperty JetSpeed.Serializer.Configuration 
 * <p>-a ApplicationPath : overwrite the default ./ or ApplicationPath property in properties file)
 * <p>-b bootPath : directory to Spring boot files,   overwrite the default assembly/boot/ or bootPath  property in properties file)  
 * <p>-c configPath : directory to Spring config files,   overwrite the default assembly/ or configPath property in properties file)
 * 
 * <p>-o optionstring : overwrite defrault "ALL,REPLACE"
 * <p>optionstring: 
 *      ALL - extract/import all 
 *      USER - extract/import users
 *      USERPREFS - extract/import user preferences (for export requires USER)
 *      CAPABILITIES - extract/import capabilities
 *      PROFILE = extract/import profile settings (for export requires USER) 
 *      NOOVERWRITE = don't overwrite existing file (for export)
 *      BACKUP = backup before process
 *       
 * The overall XML file needs to indicate whether passwords used in credentials
 * are plain text or whether they are encoded. The export algoritm can determine -
 * prior to reading users - which encode/decode scheme was used and if <none> or
 * <implements PasswordEncodingService> then we store plain passwords (Note that
 * that alone requires the resulting XML to be encoded!!!!!)
 * 
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
public class JetspeedSerializerApplication
{
    public static final String JNDI_DS_NAME = "jetspeed";    
    
    public static void main(String[] args)
    {
        String propertyFileName = null;
        
        String fileName = null; // XML filename - mandatory on command line
        
        String applicationPath = null; // configuration.getProperties("applicationPath");
        String bootConfigFiles = null; // configuration.getProperties("bootConfigFiles");
        String configFiles = null; // configuration.getProperties("configFiles");

        String name = null;
        
        String options = null;
        
        PropertiesConfiguration configuration = null;
        
        String defaultIndent = null;

        boolean doImport = false;
        boolean doExport = false;
 
        if (args == null)
            throw new IllegalArgumentException("Either import or export have to be defined (-I or -E follwoed by the filename");

        
        // Parse all the command-line arguments
        for(int n = 0; n < args.length; n++) 
        {
          if (args[n].equals("-p")) propertyFileName = args[++n];
          else if (args[n].equals("-a")) applicationPath = args[++n];
          else if (args[n].equals("-b")) bootConfigFiles = args[++n];
          else if (args[n].equals("-c")) configFiles = args[++n];
          else if (args[n].equals("-E")) { doExport = true; fileName = args[++n];}
          else if (args[n].equals("-I")) { doImport = true; fileName = args[++n];}
          else if (args[n].equals("-N")) name = args[++n];
          else if (args[n].equals("-O")) options = args[++n];
          else throw new IllegalArgumentException("Unknown argument: " + args[n]);
        }

        
        /** The only required argument is the filename for either export or import*/
        if ((!doImport) && (!doExport))
          throw new IllegalArgumentException("Either import or export have to be defined (-I or -E follwoed by the filename");

        /** But not both*/
        if ((doImport) && (doExport))
            throw new IllegalArgumentException("Only one - either import or export - can be requested");

        if (name == null) name = fileName;
        
        /** get system property definition */
        if (propertyFileName == null)
            propertyFileName = System.getProperty(
                "org.apache.jetspeed.xml.importer.configuration",
                null);
 
        if (propertyFileName != null)
        {    
            try
            {
                configuration = new PropertiesConfiguration(propertyFileName);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                System.exit(1);
            }
            if (configuration != null)
            {
                /** only read what was not defined on the command line */
            
                if (applicationPath == null) applicationPath = configuration.getString("applicationPath");
                if (bootConfigFiles == null) applicationPath = configuration.getString("bootConfigFiles");
                if (configFiles == null) applicationPath = configuration.getString("configFiles");
                if (options == null) applicationPath = configuration.getString("options");
                if (defaultIndent == null) applicationPath = configuration.getString("defaultIndent");
                
            }
        }

        // if we still miss some settings, use hardoced defaults
        if (applicationPath == null) applicationPath = "./";
        if (bootConfigFiles == null) bootConfigFiles = "assembly/boot/*.xml";
        if (configFiles == null) configFiles = "assembly/*.xml";

        bootConfigFiles = bootConfigFiles + "*.xml";
        configFiles = configFiles + "*.xml";
     
        // ok - we are ready to rumble....
        
        /** create the instruction map */
        
        Map settings = null;
        if (options != null)
        {
            settings = new HashMap();
            settings.put(JetspeedSerializer.KEY_PROCESS_USERS, Boolean.FALSE);
            settings.put(JetspeedSerializer.KEY_PROCESS_CAPABILITIES, Boolean.FALSE);
            settings.put(JetspeedSerializer.KEY_PROCESS_PROFILER, Boolean.FALSE);
            settings.put(JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES, Boolean.FALSE);
            settings.put(JetspeedSerializer.KEY_OVERWRITE_EXISTING, Boolean.TRUE);
            settings.put(JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS, Boolean.FALSE);            
            String[] optionSet = getTokens(options);
            
            
            for (int i = 0; i < optionSet.length; i++)
            {
                String o = optionSet[i];
                if (o.equalsIgnoreCase("all"))
                {
                    settings.put(JetspeedSerializer.KEY_PROCESS_USERS, Boolean.TRUE);
                    settings.put(JetspeedSerializer.KEY_PROCESS_CAPABILITIES, Boolean.TRUE);
                    settings.put(JetspeedSerializer.KEY_PROCESS_PROFILER, Boolean.TRUE);
                    settings.put(JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES, Boolean.TRUE);
                }
                else
                if (o.equalsIgnoreCase("user"))
                    settings.put(JetspeedSerializer.KEY_PROCESS_USERS, Boolean.TRUE);
                else 
                    if (o.equalsIgnoreCase("USERPREFS"))
                        settings.put(JetspeedSerializer.KEY_PROCESS_USER_PREFERENCES, Boolean.TRUE);
                    else 
                        if (o.equalsIgnoreCase("CAPABILITIES"))
                            settings.put(JetspeedSerializer.KEY_PROCESS_CAPABILITIES, Boolean.TRUE);
                        else 
                            if (o.equalsIgnoreCase("PROFILE"))
                                settings.put(JetspeedSerializer.KEY_PROCESS_PROFILER, Boolean.TRUE);
                            else 
                                if (o.equalsIgnoreCase("NOOVERWRITE"))
                                    settings.put(JetspeedSerializer.KEY_OVERWRITE_EXISTING, Boolean.FALSE);
                                else 
                                    if (o.equalsIgnoreCase("BACKUP"))
                                        settings.put(JetspeedSerializer.KEY_BACKUP_BEFORE_PROCESS, Boolean.TRUE);
                
            }
        }
        JetspeedSerializer serializer = null;
        try
        {
            if (applicationPath == null) applicationPath = "./";
            if (bootConfigFiles == null) bootConfigFiles = "assembly/boot/*.xml";
            if (configFiles == null) configFiles = configuration.getString("assembly/*.xml");

            serializer = new JetspeedSerializerImpl(applicationPath,getTokens(bootConfigFiles),getTokens(configFiles));
            if (doExport)
                serializer.exportData(name, fileName, settings);
            else
                serializer.importData(fileName, settings);
        } catch (Exception e)
        {
            System.err.println("Failed to process XML " + (doExport?"export":"import")+ ":" + e);
            e.printStackTrace();
        }
        finally
        {
            if (serializer != null)
                serializer.closeUp();
            System.out.println("DONE performing " + (doExport?"export":"import")+ " with " + fileName);
        }

    }
        
        
        private static  String[] getTokens(String _line)
        {
            if ((_line == null) || (_line.length() == 0))
                return null;
            
            StringTokenizer st = new StringTokenizer(_line, ",");
            ArrayList list = new ArrayList();

            while (st.hasMoreTokens())
                list.add(st.nextToken());
            String[] s = new String[list.size()];
            for (int i=0; i<list.size(); i++)
                s[i] = (String)list.get(i);
            return s;
        }

}