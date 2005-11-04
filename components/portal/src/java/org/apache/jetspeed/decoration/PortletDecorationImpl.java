package org.apache.jetspeed.decoration;

import java.util.Properties;

import org.apache.jetspeed.util.Path;

public class PortletDecorationImpl extends BaseDecoration implements PortletDecoration
{

    public PortletDecorationImpl(Properties config, ResourceValidator validator, Path basePath, PathResolverCache cache) throws InvalidDecorationConfigurationException
    {
        super(config, validator, basePath, cache);    
    }

    public String getTemplate()
    {
        return null;
    }

}
