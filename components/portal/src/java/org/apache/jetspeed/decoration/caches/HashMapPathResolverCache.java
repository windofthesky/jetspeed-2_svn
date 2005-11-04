package org.apache.jetspeed.decoration.caches;

import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.decoration.PathResolverCache;


public class HashMapPathResolverCache implements PathResolverCache
{
    protected Map cache;
    
    public HashMapPathResolverCache()
    {
        this.cache = new HashMap();
    }

    public void addPath(String key, String path)
    {
        cache.put(key, path);
    }

    public String getPath(String key)
    {
        return (String) cache.get(key);
    }

    public String removePath(String key)
    {
        return (String) cache.remove(key);
    }

    public void clear()
    {
        cache.clear();        
    }

}
