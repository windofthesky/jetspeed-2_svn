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
        
    public static class PortletURLType
    {
        private String  path;
        private boolean action;

        public PortletURLType(){}
        public String getPath()
        {
            return path;
        }
        
        public void setPath(String path)
        {
            this.path = path;
        }

        public void setAction(boolean action)
        {
            this.action = action;
        }

        public boolean isAction()
        {
            return action;
        }
        
        public String toString()
        {
            return "PortletURLType: path="+path+", action="+action;
        }
    }
    
    private boolean defaultAction;
    private PortletURLType[] portletURLTypes = new PortletURLType[0];
    private ArrayList portletURLTypeList;
    
    public PortletURLTypes(){}
    
    public void addActionType(PortletURLType portletURLType)
    {
        checkLoaded();
        portletURLType.setAction(true);
        portletURLTypeList.add(portletURLType);
    }
    
    public void addRenderType(PortletURLType portletURLType)
    {
        checkLoaded();
        portletURLType.setAction(false);
        portletURLTypeList.add(portletURLType);
    }
    
    public void setDefault(String value)
    {
        checkLoaded();
        this.defaultAction = "action".equals(value.toLowerCase());
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
    
    public boolean isActionURL(String path)
    {
        boolean action = defaultAction;
        for (int i = 0; i < portletURLTypes.length; i++ )
        {
            if (path.startsWith(portletURLTypes[i].path))
            {
                action = portletURLTypes[i].action;
                break;
            }
        }
        return action;
    }
}
