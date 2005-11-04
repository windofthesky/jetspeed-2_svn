package org.apache.jetspeed.decoration;

import java.util.Properties;

import org.apache.jetspeed.util.Path;

public class LayoutDecorationImpl extends BaseDecoration implements LayoutDecoration
{

    public LayoutDecorationImpl(Properties config, ResourceValidator validator, Path basePath, PathResolverCache cache) throws InvalidDecorationConfigurationException
    {
        super(config, validator, basePath, cache);
    }

    public String getHeader()
    {
        String headerTemplate = config.getProperty("header", "header.vm");
        return getResource(headerTemplate);
    }

    public String getFooter()
    {
        String footerTemplate = config.getProperty("footer", "footer.vm");
        return getResource(footerTemplate);
    }

}
