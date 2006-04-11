package org.apache.jetspeed;

import java.util.Map;

import javax.portlet.PortalContext;
import javax.servlet.ServletConfig;

import org.apache.pluto.factory.PortalContextFactory;

public class PortalContextFactoryImpl implements PortalContextFactory
{
    public PortalContext getPortalContext()
    {
        return Jetspeed.getContext();
    }

    public void init(ServletConfig config, Map properties) throws Exception
    {
    }

    public void destroy() throws Exception
    {
    }
}
