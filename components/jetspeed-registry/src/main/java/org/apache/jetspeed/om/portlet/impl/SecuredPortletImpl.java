package org.apache.jetspeed.om.portlet.impl;

import org.apache.jetspeed.om.portlet.SecuredPortlet;



public class SecuredPortletImpl implements SecuredPortlet
{
    private String name;

    public SecuredPortletImpl()
    {}
    
    public SecuredPortletImpl(String name)
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
