/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.portlets.security.rolemgt;

import java.util.prefs.Preferences;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.PhaseId;

import org.apache.myfaces.custom.tabbedpane.HtmlPanelTabbedPane;

/**
 * @author <a href="dlestrat@apache.org">David Le Strat </a>
 */
public class RoleActionListener implements ActionListener
{
    /** The role management bundle name. */
    private final static String ROLE_MGT_RESOURCES = "org.apache.jetspeed.portlets.security.resources.RoleMgtResources";
    
    /** The invalid selected roles message id. */
    private final static String INVALID_SELECTED_ROLES_MESSAGE_ID = "invalidSelectedRoles";
    
    /** The add role action. */
    private final static String ADD_ROLE_ACTION = "addRoleAction";
    
    /** The edit role action. */
    private final static String EDIT_ROLE_ACTION = "editRoleAction";

    /**
     * @see javax.faces.event.ActionListener#processAction(javax.faces.event.ActionEvent)
     */
    public void processAction(ActionEvent event) throws AbortProcessingException
    {
        if (event.getPhaseId() == PhaseId.INVOKE_APPLICATION)
        {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            UIComponent component = event.getComponent();

            VariableResolver vr = facesContext.getApplication().getVariableResolver();
            RoleActionForm roleActionForm = (RoleActionForm) vr.resolveVariable(facesContext,
                    RoleActionForm.ROLE_ACTION_FORM);
            if (component.getId().equals(ADD_ROLE_ACTION))
            {
                roleActionForm.addRole();
                HtmlPanelTabbedPane tabbedPane = RoleMgtRenderUtil.findTabbedPane(component);
                // Load the view roles pane.
                tabbedPane.setSelectedIndex(0);
            }
            else if (component.getId().equals(EDIT_ROLE_ACTION))
            {
                String[] selectedRoles = roleActionForm.getSelectedRoles();
                
                if ((null == selectedRoles) || (selectedRoles.length == 0) || (selectedRoles.length > 1))
                {
                    facesContext.addMessage(component.getClientId(facesContext),
                            RoleMgtRenderUtil.getMessage(facesContext.getViewRoot().getLocale(),
									   					 FacesMessage.SEVERITY_ERROR,
									   					 INVALID_SELECTED_ROLES_MESSAGE_ID,
									   					 ROLE_MGT_RESOURCES,
									   					 null));
                }
                else
                {
                    Preferences prefs = Preferences.userRoot().node(selectedRoles[0]);
                    roleActionForm.setRolePath(prefs.absolutePath());
                    roleActionForm.setRoleName(prefs.name());
                    
                    PanelTabStateListener panelTabState = (PanelTabStateListener) vr.resolveVariable(facesContext,
                            PanelTabStateListener.PANEL_TAB_STATE);
                    panelTabState.renderEditRole();
                }

            }
        }
    }
}