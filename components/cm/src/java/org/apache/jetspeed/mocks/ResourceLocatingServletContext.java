package org.apache.jetspeed.mocks;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ResourceLocatingServletContext extends BaseMockServletContext
{
    private final File rootPath;
    private final Map pathOverrides;
    
    public ResourceLocatingServletContext(File rootPath)
    {   
        this.rootPath = rootPath;
        this.pathOverrides = new HashMap();
    }
    
    public final void addPathOverride(String path, File file)
    {
        pathOverrides.put(path, file);
    }

    public URL getResource(String path) throws MalformedURLException
    {
       if(pathOverrides.containsKey(path))
       {
           return ((File)pathOverrides.get(path)).toURL();
       }
       else
       {
           return new File(rootPath, path).toURL();
       }
    }

    public String getRealPath(String path)
    {
        if(pathOverrides.containsKey(path))
        {
            return ((File)pathOverrides.get(path)).getAbsolutePath();
        }
        else
        {
            return new File(rootPath, path).getAbsolutePath();
        }
    }

    public InputStream getResourceAsStream(String path)
    {
        try
        {
            return getResource(path).openStream();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public Set getResourcePaths(String path)
    {
        File start = new File(rootPath, path);        
        File[] children = start.listFiles();
        HashSet pathes = new HashSet();
        for(int i=0; i < children.length; i++)
        {
            File child = children[i];
            String relativePath = child.getPath().substring(rootPath.getPath().length()).replace('\\','/');
            
            if(child.isDirectory())
            {                
                pathes.add(relativePath+"/");
            }
            else
            {
                pathes.add(relativePath);
            }
        }
        
        Iterator itr = pathOverrides.keySet().iterator();
        while(itr.hasNext())
        {
            pathes.add(itr.next());
        }
        
        return pathes;
    }

}
