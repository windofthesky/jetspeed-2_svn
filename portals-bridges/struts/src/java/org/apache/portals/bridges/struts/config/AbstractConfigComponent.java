package org.apache.portals.bridges.struts.config;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

public abstract class AbstractConfigComponent
{
    protected static class SetParentRule extends Rule
    {
        private Object parent;
        
        public SetParentRule(Object parent)
        {
            this.parent = parent;
        }
        
        public void begin( String arg0, String arg1, Attributes arg2 ) throws Exception
        {
            digester.push(parent);
        }        
    }
    
    private boolean loaded = false;
    
    public AbstractConfigComponent()
    {
    }
    
    protected void checkLoaded()
    {
        if (loaded)
            throw new IllegalStateException("Already loaded");
    }
    
    public abstract void configure(Digester digester);
    
    public void afterLoad()
    {
        checkLoaded();
        loaded = true;
    }
}
