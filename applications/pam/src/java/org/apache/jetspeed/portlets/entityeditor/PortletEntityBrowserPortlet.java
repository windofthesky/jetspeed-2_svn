package org.apache.jetspeed.portlets.entityeditor;

import java.io.IOException;
import java.util.Collection;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;
import org.apache.velocity.context.Context;

public class PortletEntityBrowserPortlet extends GenericVelocityPortlet
{

    private PortletEntityAccessComponent entityAccess;
    private PortletRegistry registry;
    

     /* (non-Javadoc)
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);
        PortletContext context = getPortletContext();
        entityAccess = (PortletEntityAccessComponent)context.getAttribute(CommonPortletServices.CPS_ENTITY_ACCESS_COMPONENT);
        registry = (PortletRegistry)context.getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
    }

    public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        Collection portletApps = registry.getPortletApplications();
        Context context = getContext(request);
        context.put("portletApps", portletApps);
        context.put("entityAccess", entityAccess);
        context.put("portletContext", getPortletContext());
        super.doView(request, response);
    }
    
    protected final void doCreate(ActionRequest request, ActionResponse response) throws PortletException
    {
        try
        {
            PortletDefinition pd = getPortletDefintion(request);
            String newId = request.getParameter("newEntityId");
            PortletEntity entity = null;
            
            if(newId != null)
            {
                entity = entityAccess.newPortletEntityInstance(pd, newId);
            }
            else
            {
                entity = entityAccess.newPortletEntityInstance(pd);
            }
            
            entityAccess.storePortletEntity(entity);
        }
        catch (PortletEntityNotStoredException e)
        {
            throw new PortletException(e.getMessage(), e);
        }
        catch (PortletException e)
        {
            throw new PortletException(e.getMessage(), e);
        }
    }
    
    protected final PortletDefinition getPortletDefintion(ActionRequest request) throws PortletException
    {
        String portletUniqueName = request.getParameter("selectedPortlet");
        if(portletUniqueName == null)
        {
            throw new PortletException("There was no 'portletUniqueName' parameter specified in the request.");
        }
        else
        {
           return registry.getPortletDefinitionByUniqueName(portletUniqueName);            
        }
    }
    
    public void processAction(ActionRequest request, ActionResponse actionResponse) throws PortletException, IOException
    {
        String action = request.getParameter("action");
        
        if(action == null)
        {
            throw new PortletException("Requires that action either be 'edit' or 'create'");
        }
        else if(action.equals("create"))
        {
            doCreate(request, actionResponse);
        }
        else
        {
            throw new PortletException("Requires that action to be 'create'");
        }
    }
    
    

}
