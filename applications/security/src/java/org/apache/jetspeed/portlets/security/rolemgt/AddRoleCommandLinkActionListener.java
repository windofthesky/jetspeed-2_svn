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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import javax.faces.el.VariableResolver;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.apache.myfaces.custom.tabbedpane.HtmlPanelTabbedPane;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class AddRoleCommandLinkActionListener implements ActionListener
{
    /** The role action controller bean. */
    private final static String ADD_ROLE_TO_PARAM = "addRoleToParam";

    public void processAction(ActionEvent event) throws AbortProcessingException
    {
        UIComponent component = event.getComponent();
        HtmlPanelTabbedPane tabbedPane = RoleMgtRenderUtil.findTabbedPane(component);

        // Load add role pane.
        tabbedPane.setSelectedIndex(1);

        // Set the current role action.
        FacesContext facesContext = FacesContext.getCurrentInstance();
        VariableResolver vr = facesContext.getApplication().getVariableResolver();

        RoleActionForm roleAction = (RoleActionForm) vr.resolveVariable(facesContext, RoleActionForm.ROLE_ACTION_FORM);

        String fullPath = (String) facesContext.getExternalContext().getRequestParameterMap().get(ADD_ROLE_TO_PARAM);
        Preferences prefs = Preferences.userRoot().node(fullPath);

        roleAction.setParentRole(new RoleTreeItem(fullPath, prefs.name()));

        //if (component.getId().equals("addButton") ||
        //    component.getId().equals("href1"))
        //{
        //    form.add();
        //}
        //else
        //{
        //    form.subtract();
        //}
    }
}