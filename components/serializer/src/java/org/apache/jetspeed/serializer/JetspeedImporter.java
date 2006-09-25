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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.capabilities.Capabilities;
import org.apache.jetspeed.capabilities.Client;
import org.apache.jetspeed.capabilities.MimeType;
import org.apache.jetspeed.capabilities.MediaType;
import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.SpringComponentManager;
import org.apache.jetspeed.engine.JetspeedEngineConstants;
import org.apache.jetspeed.serializer.objects.JSCriterion;
import org.apache.jetspeed.serializer.objects.JSNameValuePair;
import org.apache.jetspeed.serializer.objects.JSPermission;
import org.apache.jetspeed.serializer.objects.JSProfilingRule;
import org.apache.jetspeed.serializer.objects.JSUser;

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
            JetspeedImporter importer = new JetspeedImporter();
//            JSImportData data = importer.importData("jetspeed-import.xml");            
//            data.debug(System.out);
            importer.exportData("");
        }
        catch (Exception e)
        {
            System.err.println("Failed to XML import: " + e);
            e.printStackTrace();
        }
        
    }

    public JSImportData exportData(String exportFileName)
    {
        JSImportData importData = null;
        try
        {
            XStream xstream = new XStream(new DomDriver());
            setupAliases(xstream);            
            String applicationRoot = "./";
            Configuration properties = (Configuration) new PropertiesConfiguration();
            properties.setProperty(JetspeedEngineConstants.APPLICATION_ROOT_KEY, applicationRoot);
            ComponentManager cm = initializeComponentManager(applicationRoot);            
            exportCapabilities(cm, xstream);
            cm.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return importData;
    }
    
    public void exportCapabilities(ComponentManager cm, XStream xstream)
    {
        Capabilities caps = (Capabilities)cm.getComponent("capabilities");
//        Iterator clients = caps.getClients();
//        while (clients.hasNext())
//        {
//            Client client = (Client)clients.next();
//            System.out.println(client.getName());            
//        }
//        Iterator mimeTypes = caps.getMimeTypes();
//        while (mimeTypes.hasNext())
//        {
//            MimeType mimeType = (MimeType)mimeTypes.next();
//            System.out.println(mimeType.getName());
//        }
        Iterator mediaTypes = caps.getMediaTypes();
        while (mediaTypes.hasNext())
        {
            MediaType mediaType = (MediaType)mediaTypes.next();
            //System.out.println(mediaType.getName());
            String xml = xstream.toXML(mediaType);
            System.out.println(xml);
        }        
    }
    
    
    protected ComponentManager initializeComponentManager(String appRoot) throws IOException
    {
        String[] bootConfigs = new String[] {"test/assembly/boot/*.xml"};
        String[] appConfigs =  new String[] {"test/assembly/*.xml"};
        SpringComponentManager cm = new SpringComponentManager(bootConfigs, appConfigs, appRoot);
        cm.start();
        return cm;        
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
