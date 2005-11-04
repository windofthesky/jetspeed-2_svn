package org.apache.jetspeed.decoration;

public interface Decoration
{
    String getName();
    
    String getResource(String path);
    
    String getStyleSheet();
}
