package org.apache.portals.bridges.struts.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.digester.Digester;

public class RenderContextAttributes extends AbstractConfigComponent
{
    private static class AttributeValue implements Serializable
    {
        private String  name;
        private Object  value;
        
        public AttributeValue(String name, Object value)
        {
            super();
            this.name = name;
            this.value = value;
        }
        
        public String getName()
        {
            return name;
        }
        
        public Object getValue()
        {
            return value;
        }
    }
    
    public static class Attribute
    {
        private String value;
        private boolean prefix;
        private boolean keep;

        public Attribute()
        {        
        }
        
        public boolean isKeep()
        {
            return keep;
        }
        
        public void setKeep(boolean keep)
        {
            this.keep = keep;
        }
        
        public boolean isPrefix()
        {
            return prefix;
        }

        public String getValue()
        {
            return value;
        }
        
        public void setName(String value)
        {
            this.value = value;
            this.prefix = false;
        }
        
        public void setPrefix(String value)
        {
            this.value = value;
            this.prefix = true;
        }
    }
    
    private String name = this.getClass().getName();
    private Attribute[] namedAttributes;
    private Attribute[] prefixAttributes;
    private ArrayList namedAttributesList;
    private ArrayList prefixAttributesList;
    
    public RenderContextAttributes()
    {
        namedAttributesList = new ArrayList();
        prefixAttributesList = new ArrayList();
    }
    
    private Attribute[] createArray(List attributes)
    {
        Attribute[] array = null;
        if ( attributes != null && attributes.size() > 0 )
        {
            array = new Attribute[attributes.size()];
            for ( int i = 0; i < array.length; i++ )
            {
                array[i] = (Attribute)attributes.get(i);
            }
        }
        return array;
    }
    
    public void addAttribute(Attribute attribute)
    {
        checkLoaded();
        
        if (attribute.isPrefix())
        {
            prefixAttributesList.add(attribute);
        }
        else
        {
            namedAttributesList.add(attribute);            
        }
    }
    
    public void setName(String name)
    {
        checkLoaded();
        this.name = name;
    }
    
    public void configure(Digester digester)
    {
        digester.addRule("config/render-context", new SetParentRule(this));
        digester.addSetProperties("config/render-context");
        digester.addObjectCreate("config/render-context/attribute", Attribute.class);
        digester.addSetProperties("config/render-context/attribute");
        digester.addSetNext("config/render-context/attribute", "addAttribute");
        digester.addCallMethod("config/render-context", "afterLoad");
        
    }
    
    public void afterLoad()
    {
        super.afterLoad();

        namedAttributes = createArray(namedAttributesList);
        prefixAttributes = createArray(prefixAttributesList);
        
        namedAttributesList = null;
        prefixAttributesList = null;
    }
    
    public void saveAttributes(HttpServletRequest request)
    {
        ArrayList keepAttributes = new ArrayList();
        ArrayList tempAttributes = new ArrayList();
        ArrayList savedNames = new ArrayList();
        if ( namedAttributes != null )
        {
            for ( int i = 0; i < namedAttributes.length; i++ )
            {
                Object value = request.getAttribute(namedAttributes[i].getValue());
                if ( value != null )
                {
                    AttributeValue attributeValue = new AttributeValue(namedAttributes[i].getValue(), value);
                    savedNames.add(attributeValue.getName());
                    if ( namedAttributes[i].isKeep() )
                    {
                        keepAttributes.add(attributeValue);
                    }
                    else
                    {
                        tempAttributes.add(attributeValue);
                    }                    
                }
            }
        }
        if ( prefixAttributes != null )
        {
            Enumeration names = request.getAttributeNames();
            while ( names.hasMoreElements() )
            {
                String name = (String)names.nextElement();
                for ( int i = 0; i < prefixAttributes.length; i++ )
                {
                    if (!savedNames.contains(name) && name.startsWith(prefixAttributes[i].getValue()))
                    {
                        AttributeValue attributeValue = new AttributeValue(name, request.getAttribute(name));
                        savedNames.add(name);
                        if (prefixAttributes[i].isKeep())
                        {
                            keepAttributes.add(attributeValue);
                        }
                        else
                        {
                            tempAttributes.add(attributeValue);
                        }                    
                    }
                }
            }
        }
        if (keepAttributes.size() > 0)
        {
            if (tempAttributes.size() > 0)
            {
                keepAttributes.add(null); // indicating subsequent attributeValues are temporarily
                keepAttributes.addAll(tempAttributes);
            }
            request.getSession().setAttribute(name,keepAttributes);
        }
        else if (tempAttributes.size() > 0)
        {
            tempAttributes.add(0,null); // indicating subsequent attributeValues are temporarily
            request.getSession().setAttribute(name,tempAttributes);
        }
    }
    
    public void clearAttributes(HttpSession session)
    {
        session.removeAttribute(name);
    }
    
    public void restoreAttributes(HttpServletRequest request)
    {
        ArrayList attributes = (ArrayList)request.getSession().getAttribute(name);
        if ( attributes != null )
        {
            for ( int size = attributes.size(), i = size - 1 ; i > -1; i-- )
            {
                AttributeValue attributeValue = (AttributeValue)attributes.get(i);
                if ( attributeValue == null )
                {
                    if ( i == 0 )
                    {
                        request.getSession().removeAttribute(name);
                    }
                    else
                    {
                        // remove this and previously retrieved attributeValues as being temporarily
                        while (size > i )
                        {
                            attributes.remove(--size);
                        }
                    }
                }
                else
                {
                    request.setAttribute(attributeValue.getName(), attributeValue.getValue());
                }
            }
        }
    }
}
