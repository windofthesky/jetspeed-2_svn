package org.apache.portals.bridges.struts.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.digester.Digester;

public class PortletURLTypes extends AbstractConfigComponent
{
    private static final Comparator portletURLTypeComparator =
        new Comparator()
        {
        	public int compare(Object o1, Object o2)
        	{
        	    PortletURLType a1 = (PortletURLType)o1;
        	    PortletURLType a2 = (PortletURLType)o2;
        	    int lendiff = a1.getPath().length() - a2.getPath().length();
        	    if ( lendiff == 0 )
        	    {
        	        return a1.getPath().compareTo(a2.getPath());
        	    }
        	    else if ( lendiff > 0 )
        	    {
        	        return -1;
        	    }
        	    else
        	    {
        	        return 1;
        	    }
        	}
        };
        
    public static class URLType
    {
        public static final URLType ACTION = new URLType(0,"action");
        public static final URLType RENDER = new URLType(1,"render");
        public static final URLType RESOURCE = new URLType(2,"resource");
        
        private int id;
        private String name;
        
        private URLType(int id, String name)
        {
            this.id = id;
            this.name = name;
        }
        
        public String getName()
        {
            return name;
        }
        
        public boolean equals(URLType type)
        {
            return type != null ? type.id == id : false;
        }
    }
            
    public static class PortletURLType
    {
        private String path;
        private URLType type;

        public PortletURLType(){}
        public String getPath()
        {
            return path;
        }
        
        public void setPath(String path)
        {
            this.path = path;
        }

        public void setType(URLType type)
        {
            this.type = type;
        }

        public URLType getType()
        {
            return type;
        }
        
        public String toString()
        {
            return "PortletURLType: path="+path+", type="+type;
        }
    }
    
    private URLType defaultPortletURLType = URLType.RENDER;
    private PortletURLType[] portletURLTypes = new PortletURLType[0];
    private ArrayList portletURLTypeList;
    
    public PortletURLTypes(){}
    
    public void addActionType(PortletURLType portletURLType)
    {
        checkLoaded();
        portletURLType.setType(URLType.ACTION);
        portletURLTypeList.add(portletURLType);
    }
    
    public void addRenderType(PortletURLType portletURLType)
    {
        checkLoaded();
        portletURLType.setType(URLType.RENDER);
        portletURLTypeList.add(portletURLType);
    }
    
    public void addResourceType(PortletURLType portletURLType)
    {
        checkLoaded();
        portletURLType.setType(URLType.RESOURCE);
        portletURLTypeList.add(portletURLType);
    }
    
    public void setDefault(String value)
    {
        checkLoaded();
        this.defaultPortletURLType = "action".equals(value.toLowerCase()) ? URLType.ACTION : URLType.RENDER;
    }
    
    public void configure(Digester digester)
    {
        portletURLTypeList = new ArrayList();        
        digester.addRule("config/portlet-url-type", new SetParentRule(this));
        digester.addSetProperties("config/portlet-url-type");
        digester.addObjectCreate("config/portlet-url-type/action", PortletURLType.class);
        digester.addSetProperties("config/portlet-url-type/action");
        digester.addSetNext("config/portlet-url-type/action", "addActionType");
        digester.addObjectCreate("config/portlet-url-type/render", PortletURLType.class);
        digester.addSetProperties("config/portlet-url-type/render");
        digester.addSetNext("config/portlet-url-type/render", "addRenderType");
        digester.addObjectCreate("config/portlet-url-type/resource", PortletURLType.class);
        digester.addSetProperties("config/portlet-url-type/resource");
        digester.addSetNext("config/portlet-url-type/resource", "addResourceType");
        digester.addCallMethod("config/portlet-url-type", "afterLoad");
    }
    
    public void afterLoad()
    {
        super.afterLoad();

        if ( portletURLTypeList != null && portletURLTypeList.size() > 0 )
        {
            portletURLTypes = new PortletURLType[portletURLTypeList.size()];
            for ( int i = 0; i < portletURLTypes.length; i++ )
            {
                portletURLTypes[i] = (PortletURLType)portletURLTypeList.get(i);
            }
            if ( portletURLTypes.length > 1 )
            {
                Arrays.sort(portletURLTypes, portletURLTypeComparator);
            }
        }

        portletURLTypeList = null;
    }
    
    public URLType getType(String path)
    {
        URLType type = defaultPortletURLType;
        for (int i = 0; i < portletURLTypes.length; i++ )
        {
            if (path.startsWith(portletURLTypes[i].path))
            {
                type = portletURLTypes[i].getType();
                break;
            }
        }
        return type;
    }
}
