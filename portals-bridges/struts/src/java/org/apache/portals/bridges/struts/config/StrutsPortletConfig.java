package org.apache.portals.bridges.struts.config;

import java.io.InputStream;

import javax.portlet.PortletContext;
import javax.portlet.PortletException;

import org.apache.commons.digester.Digester;

public class StrutsPortletConfig
{
    private RenderContextAttributes renderContextAttributes;
    private PortletURLTypes portletURLTypes;
    
    public void loadConfig(PortletContext portletContext,String config) throws PortletException
    {        
        renderContextAttributes = new RenderContextAttributes();
        portletURLTypes = new PortletURLTypes();
        
        InputStream input = portletContext.getResourceAsStream(config);
        if (input == null)
        {
            return;
        }
        
        Digester digester = new Digester();
        digester.setClassLoader(Thread.currentThread().getContextClassLoader());

        renderContextAttributes.configure(digester);
        portletURLTypes.configure(digester);
        
        try
        {
            digester.parse(input);            
        }
        catch (Exception e)
        {
            throw new PortletException("Error loading StrutsPortlet config " + config + ": " + e.getMessage(), e);
        }
        finally
        {
            try
            {
                input.close();
            }
            catch (Exception e)
            {
            }
        }
    }
    
    public RenderContextAttributes getRenderContextAttributes()
    {
        return renderContextAttributes;
    }

    public PortletURLTypes getPortletURLTypes()
    {
        return portletURLTypes;
    }
}
