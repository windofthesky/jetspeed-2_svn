/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.serializer.objects.JSCriterion;
import org.apache.jetspeed.serializer.objects.JSPermission;
import org.apache.jetspeed.serializer.objects.JSProfilingRule;
import org.apache.jetspeed.serializer.objects.JSUser;
import org.apache.jetspeed.serializer.objects.JSNameValuePair;

import java.io.FileInputStream;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


/**
 * Jetspeed Importer
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JetspeedImporter 
{
    //private final static Log log = LogFactory.getLog(JetspeedImporter.class);

    public JetspeedImporter()
    {
    }

    public static void main(String[] args) 
    {
        String fileName = System.getProperty("org.apache.jetspeed.xml.importer.configuration", "xml-import.properties");
        PropertiesConfiguration configuration = new PropertiesConfiguration();
        try
        {
//            configuration.load(fileName);        
//            String [] bootAssemblies = configuration.getStringArray("boot.assemblies");
//            String [] assemblies = configuration.getStringArray("assemblies");
//            ClassPathXmlApplicationContext ctx;            
//            
//            if (bootAssemblies != null)
//            {
//                ApplicationContext bootContext = new ClassPathXmlApplicationContext(bootAssemblies, true);
//                ctx = new ClassPathXmlApplicationContext(assemblies, true, bootContext);
//            }
//            else
//            {
//                ctx = new ClassPathXmlApplicationContext(assemblies, true);
//            }
//            
//            String rootFolder = configuration.getString("root.folder", "/");
        
        
            JetspeedImporter importer = new JetspeedImporter();
            JSImportData data = importer.importData("jetspeed-import.xml");
            data.debug(System.out);
        }
        catch (Exception e)
        {
            System.err.println("Failed to XML import: " + e);
            e.printStackTrace();
        }
        
    }

    public JSImportData importData(String importFileName)
    {
        JSImportData importData = null;
        try
        {
            XStream xstream = new XStream(new DomDriver());
            setupAliases(xstream);            
            //Reader reader = new InputStreamReader(this.getClass().getResourceAsStream(importFileName));
            Reader reader = new InputStreamReader(new FileInputStream(importFileName), "UTF-8");
            
            importData = (JSImportData)xstream.fromXML(reader);
            
            addRoles(importData.getRoles());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return importData;
    }
    
    public void setupAliases(XStream xstream)
    {
        xstream.alias("import-data", JSImportData.class);        
        xstream.alias("role", String.class);
        xstream.alias("group", String.class);
        xstream.alias("permission", JSPermission.class);        
        xstream.alias("profilingRule", JSProfilingRule.class);
        xstream.alias("user", JSUser.class);
        xstream.alias("info", JSNameValuePair.class);
        xstream.alias("criterion", JSCriterion.class);
        xstream.alias("rule", JSNameValuePair.class);
        xstream.alias("info", JSNameValuePair.class);
        //xstream.registerConverter()
    }
    
    public void start() 
    {
//        log.info( "Start Jetspeed Importer");        
    }

    public void stop() 
    {       
 //       log.info( "Stop Jetspeed Importer");               
    }

    public void addRoles(List roles)
    {
        Iterator list = roles.iterator();
        while (list.hasNext())
        {
            String role = (String)list.next();
            
        }
        
    }
    
}
