package org.apache.jetspeed.om.portlet.impl;

import org.apache.jetspeed.om.portlet.FilterLifecycle;



public class FilterLifecycleImpl implements FilterLifecycle
{
    private String name;

    public FilterLifecycleImpl()
    {}
    
    public FilterLifecycleImpl(String name)
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
