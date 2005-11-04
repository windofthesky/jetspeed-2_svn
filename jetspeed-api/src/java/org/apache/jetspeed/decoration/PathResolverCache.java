package org.apache.jetspeed.decoration;

public interface PathResolverCache
{
    void addPath(String key, String path);
    
    String getPath(String key);
    
    String removePath(String key);
    
    void clear();
    
}
