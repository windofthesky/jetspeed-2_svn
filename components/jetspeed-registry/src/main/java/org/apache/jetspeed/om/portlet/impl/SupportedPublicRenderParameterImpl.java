package org.apache.jetspeed.om.portlet.impl;

import org.apache.jetspeed.om.portlet.SupportedPublicRenderParameter;


public class SupportedPublicRenderParameterImpl implements SupportedPublicRenderParameter
{
    private String name;

    public SupportedPublicRenderParameterImpl()
    {}
    
    public SupportedPublicRenderParameterImpl(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }

    public boolean equals(Object qname)
    {
        return (this.toString().equals(qname.toString()));
    }
    
    public String toString()
    {
        return name;
    }
    
}
