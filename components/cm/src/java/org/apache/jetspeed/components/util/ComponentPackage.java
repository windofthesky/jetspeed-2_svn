/*
 * Created on Apr 25, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.util;

import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;


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
    
    public String[] getComponentClassNames()
    {
        return config.getStringArray("component");
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
            throw new FileSystemException("No deployment script (nanocontainer.[groovy|bsh|js|py|xml]) in " + applicationFolder.getName().getPath()
                    + "/META-INF");
        }
        return nanocontainerScripts[0];
    }
}
