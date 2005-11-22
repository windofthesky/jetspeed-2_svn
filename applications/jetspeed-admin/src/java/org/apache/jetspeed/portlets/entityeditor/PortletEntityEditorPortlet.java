package org.apache.jetspeed.portlets.entityeditor;

import java.io.IOException;
import java.util.Arrays;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.components.portletentity.PortletEntityAccessComponent;
import org.apache.jetspeed.components.portletentity.PortletEntityNotStoredException;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.portals.bridges.velocity.GenericVelocityPortlet;

public class PortletEntityEditorPortlet extends GenericVelocityPortlet
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

    /* (non-Javadoc)
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#processAction(javax.portlet.ActionRequest, javax.portlet.ActionResponse)
     */
    public void processAction(ActionRequest request, ActionResponse response) throws PortletException, IOException
    {
        String action = request.getParameter("action");
        
        if(action == null)
        {
            throw new PortletException("This editor requires an action parameter");
        }
        else if(action.equals("updateValue"))
        {
            doUpdateValue(request, response);
        }
        else if(action.equals("addValue"))
        {
            doAddValue(request, response);
        }
        else if(action.equals("removeValue"))
        {
            doRemoveValue(request, response);
        }
        else if(action.equals("addPref"))
        {
            doAddPref(request, response);
        }
        else if(action.equals("removePref"))
        {
            doRemovePref(request, response);
        }
        else
        {
            throw new PortletException("'"+action+"' is not a valid editor action.");
        }
    }
    
    protected final void doAddPref(ActionRequest request, ActionResponse response) throws PortletException
    {
        PortletEntity entity = getPortletEntity(request);
        String newName = request.getParameter("newPreferenceName");
        if(newName == null || newName.length() < 1)
        {
            throw new PortletException("You must specify a name for a new preference.");
        }
        
        String[] newValues = request.getParameterValues("newPreferenceValue");
        if(newValues == null || newValues.length == 0)
        {
            throw new PortletException("You must specfiy a value for the new preference "+newName);
        }
        
        PreferenceSetComposite prefSet = (PreferenceSetComposite) entity.getPreferenceSet();
        prefSet.add(newName, Arrays.asList(newValues));
        try
        {
            entityAccess.storePortletEntity(entity);
        }
        catch (PortletEntityNotStoredException e)
        {
            throw new PortletException(e.getMessage(), e);
        }
    }
    
    protected final void doAddValue(ActionRequest request, ActionResponse response) throws PortletException
    {
        PortletEntity entity = getPortletEntity(request);
        String prefString= request.getParameter("selectedPref");
        String newValue = request.getParameter("newPrefValue");
        String prefName = prefString.split("::")[1];
        PreferenceComposite pref = (PreferenceComposite) entity.getPreferenceSet().get(prefName);
        pref.addValue(newValue);
    }
    
    protected final void doRemovePref(ActionRequest request, ActionResponse response) throws PortletException
    {
        PortletEntity entity = getPortletEntity(request);
        String prefString= request.getParameter("selectedPref");
        String prefName = prefString.split("::")[1];
        ((PreferenceSetComposite)entity.getPreferenceSet()).remove(prefName);
        
    }
    
    protected final void doUpdateValue(ActionRequest request, ActionResponse response) throws PortletException
    {
        PortletEntity entity = getPortletEntity(request);
        String prefString= request.getParameter("selectedPref");
        String updatedValue = request.getParameter("selectedPrefValue");
        if(updatedValue.trim().length() == 0)
        {
            throw new PortletException("Preference values cannot be empty.");
        }
        String[] info = prefString.split("::");
        String prefName = info[1];
        int valueIndex = Integer.parseInt(info[2]);
        PreferenceComposite pref = (PreferenceComposite) entity.getPreferenceSet().get(prefName);
        pref.setValueAt(valueIndex, updatedValue);
    }
    
    protected final void doRemoveValue(ActionRequest request, ActionResponse response) throws PortletException
    {
        PortletEntity entity = getPortletEntity(request);
        String prefString= request.getParameter("selectedPref");
        String updatedValue = request.getParameter("selectedPrefValue");
        String[] info = prefString.split("::");
        String prefName = info[1];
        int valueIndex = Integer.parseInt(info[2]);
        PreferenceComposite pref = (PreferenceComposite) entity.getPreferenceSet().get(prefName);
        pref.removeValueAt(valueIndex);
    }

    
    protected final PortletEntity getPortletEntity(ActionRequest request) throws PortletException
    {
        String entityId = request.getParameter("portletEntityId");
        if(entityId == null)
        {
            throw new PortletException("There was no 'entityId' parameter specified in the request.");
        }
        else
        {
           return entityAccess.getPortletEntity(entityId);            
        }
    }
}
