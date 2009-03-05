package org.apache.jetspeed.om.portlet.impl;

import org.apache.jetspeed.om.portlet.FilteredPortlet;



public class FilteredPortletImpl implements FilteredPortlet
{
    private String name;

    public FilteredPortletImpl()
    {}
    
    public FilteredPortletImpl(String name)
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
