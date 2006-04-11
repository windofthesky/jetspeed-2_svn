package org.apache.jetspeed.container;

import javax.portlet.Portlet;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import org.apache.pluto.om.portlet.PortletDefinition;

public class PortletRequestContext
{
    private static ThreadLocal context = new ThreadLocal();
    
    private PortletDefinition pd;
    private Portlet portlet;
    private PortletRequest request;
    private PortletResponse response;
    
    public static PortletRequestContext getContext()
    {
        return (PortletRequestContext)context.get();
    }
    
    public static void createContext(PortletDefinition pd, Portlet portlet, PortletRequest request, PortletResponse response)
    {
        context.set(new PortletRequestContext(pd, portlet, request, response));
    }
    
    public static void clearContext()
    {        
        context.set(null);
    }

    private PortletRequestContext(PortletDefinition pd, Portlet portlet, PortletRequest request, PortletResponse response)
    {
        this.pd = pd;
        this.portlet = portlet;
        this.request = request;
        this.response = response;
    }

    public PortletDefinition getPortletDefinition()
    {
        return pd;
    }

    public Portlet getPortlet()
    {
        return portlet;
    }

    public PortletRequest getRequest()
    {
        return request;
    }

    public PortletResponse getResponse()
    {
        return response;
    }
}
