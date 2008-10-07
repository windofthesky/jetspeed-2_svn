/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.provider.local.LocalFileSystem;


/**
 * @author Scott Weaver
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ComponentPackage
{
    private FileObject applicationRoot;
    protected Configuration config;
    protected Configuration parentConfig;
    protected ClassLoader packageClassLoader;

    public ComponentPackage(FileObject applicationRoot, Configuration parentConfig) throws FileSystemException, IOException
    {
        this.applicationRoot = applicationRoot;
        FileObject configObj = getComponentPkg(applicationRoot);
                
        if(parentConfig != null)
        {
            config = new PropertiesConfiguration(parentConfig);
            this.parentConfig = parentConfig;
        }
        else
        {
            config = new PropertiesConfiguration();
            this.parentConfig = config;
        }
        
        ((PropertiesConfiguration)config).load(configObj.getContent().getInputStream());
        
    }
    
    public String getPackageId()
    {
        return config.getString("package.id");
    }
    
    public String[] getExportedPackages()
    {
        return config.getStringArray("export.package");
    }
    
    public String[] getExportedJars()
    {
        return config.getStringArray("export.jar");
    }
    
    public Iterator getAllComponentInformation() throws IOException
    {
         String[] componentNames = config.getStringArray("components");
         ArrayList infos = new ArrayList(componentNames.length);
         for(int i=0; i<componentNames.length; i++)
         {
             PropertiesConfiguration infoConf = new PropertiesConfiguration(parentConfig); 
             //infoConf.
             Configuration infoSubset = config.subset("component."+componentNames[i]);
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
    
    public Collection getUrlsToLoad() throws IOException, MalformedURLException, URISyntaxException
    {
        List depJars =  getJarDependecies(applicationRoot);
        List allUrls = new ArrayList(depJars.size() + 1);
        Iterator depItr = depJars.iterator();
        while(depItr.hasNext())
        {
            FileObject dep = (FileObject)depItr.next();
            URL url = dep.getURL();
            allUrls.add(url);
        }
        
        if(applicationRoot.getFileSystem() instanceof LocalFileSystem)
        {
           String file = applicationRoot.getURL().toExternalForm();
           allUrls.add(new URI(file+"/").toURL());
        }
        else
        {    
           allUrls.add(applicationRoot.getURL());
        }
        
        return allUrls;        
    }
    
    public Collection getFileObjectsToLoad() throws FileSystemException
    {
        ArrayList foList = new ArrayList();
        foList.addAll(getJarDependecies(applicationRoot));
        foList.add(applicationRoot);
        return foList;
        
    }
    
    protected FileObject getComponentPkg( FileObject applicationFolder ) throws FileSystemException
    {
        final FileObject metaInf = applicationFolder.getChild("META-INF");
        if (metaInf == null)
        {
            throw new FileSystemException("Missing META-INF folder in " + applicationFolder.getName().getPath());
        }
        final FileObject[] nanocontainerScripts = metaInf.findFiles(new FileSelector()
        {
            public boolean includeFile( FileSelectInfo fileSelectInfo ) throws Exception
            {
                return fileSelectInfo.getFile().getName().getBaseName().equals("component.pkg");
            }

            public boolean traverseDescendents( FileSelectInfo fileSelectInfo ) throws Exception
            {
                return true;
            }
        });
        if (nanocontainerScripts == null || nanocontainerScripts.length < 1)
        {
            throw new FileSystemException("No deployment packge descriptor, component.pkg, in " + applicationFolder.getName().getPath()
                    + "/META-INF");
        }
        return nanocontainerScripts[0];
    }
    
    protected List getJarDependecies( FileObject applicationFolder ) throws FileSystemException
    {
        final FileObject libDir = applicationFolder.getChild("lib");
        final ArrayList jars = new ArrayList();
        //log.info("Checking for container Jar dependencies...");
        if (libDir != null && libDir.getType().equals(FileType.FOLDER))
        {
            final FileObject[] libFiles = libDir.getChildren();
            for (int i = 0; i < libFiles.length; i++)
            {
                if (libFiles[i].getName().getExtension().equals("jar"))
                {
                    //log.info("Getting Jar dependency, "+libFiles[i].getName()+", for container");
                    jars.add(libFiles[i]);
                }
            }
        }
        
        return jars;
    }
    
    
    /**
     * @return Returns the packageClassLoader.
     */
    public ClassLoader getPackageClassLoader()
    {
        return packageClassLoader;
    }
    /**
     * @param packageClassLoader The packageClassLoader to set.
     */
    public void setPackageClassLoader( ClassLoader packageClassLoader )
    {
        this.packageClassLoader = packageClassLoader;
    }
}
