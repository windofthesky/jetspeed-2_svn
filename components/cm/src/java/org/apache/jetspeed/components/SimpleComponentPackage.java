/*
 * Created on May 3, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.jetspeed.components.util.ConfiguredComponentInfo;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class SimpleComponentPackage
{
    protected URL propsUrl;
    protected PropertiesConfiguration config;
    protected Configuration parentConfig;
    protected String componentType;

    public SimpleComponentPackage(URL propsUrl, Configuration parentConfig) throws IOException
    {
        this(propsUrl, parentConfig, "component");        
    }
    
    public SimpleComponentPackage(URL propsUrl, Configuration parentConfig, String componentType) throws IOException
    {
        this.propsUrl = propsUrl;
        this.parentConfig = parentConfig;
        config = new PropertiesConfiguration(parentConfig);
        config.load(propsUrl.openStream());    
        this.componentType = componentType;
    }
    
    public Iterator getAllComponentInformation() throws IOException
    {
         String[] componentNames = config.getStringArray(componentType);
         ArrayList infos = new ArrayList(componentNames.length);
         for(int i=0; i<componentNames.length; i++)
         {
             System.out.println("Retreiving component configuration: "+componentNames[i]);
             PropertiesConfiguration infoConf = new PropertiesConfiguration(parentConfig); 
             Configuration infoSubset = config.subset(componentNames[i]);
             Iterator keys = infoSubset.getKeys();
             while(keys.hasNext())
             {
                 String key = (String) keys.next();
                 infoConf.setProperty(key, infoSubset.getProperty(key) );
             }
             
             infos.add(new ConfiguredComponentInfo(componentNames[i], infoConf));
         }
         
         return infos.iterator();
    }

}
