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

import java.io.Serializable;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import org.apache.myfaces.custom.tabbedpane.HtmlPanelTab;
import org.apache.myfaces.custom.tabbedpane.HtmlPanelTabbedPane;
import org.apache.myfaces.custom.tabbedpane.TabChangeEvent;
import org.apache.myfaces.custom.tabbedpane.TabChangeListener;

/**
 * <p>
 * Controls the state of the role management tabs.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class PanelTabStateListener implements Serializable, TabChangeListener
{
    /** The panel tab state bean. */
    public static final String PANEL_TAB_STATE = "roleMgtPanelTabState";
    
    /** Edit role id. */
    private static final String EDIT_ROLE_TAB_ID = "editRoleTab";

    /** Role management panel tab state value binding. */
    private static final String ROLE_MGT_PANEL_TAB_STATE = "roleMgtPanelTabState";

    /** Whether to render edit role. */
    private boolean renderEditRole = false;

    /** Whether to render view roles. */
    private boolean renderViewRoles = true;

    /**
     * @return Returns the renderEditRole.
     */
    public boolean isRenderEditRole()
    {
        return renderEditRole;
    }

    /**
     * @param renderEditRole The renderEditRole to set.
     */
    public void setRenderEditRole(boolean renderEditRole)
    {
        this.renderEditRole = renderEditRole;
    }

    /**
     * @return Returns the renderViewRoles.
     */
    public boolean isRenderViewRoles()
    {
        return renderViewRoles;
    }

    /**
     * @param renderViewRoles The renderViewRoles to set.
     */
    public void setRenderViewRoles(boolean renderViewRoles)
    {
        this.renderViewRoles = renderViewRoles;
    }

    /**
     * <p>
     * Render edit role.
     * </p>
     */
    public void renderEditRole()
    {
        this.renderEditRole = true;
        this.renderViewRoles = false;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ROLE_MGT_PANEL_TAB_STATE, this);
    }

    /**
     * <p>
     * Render view roles.
     * </p>
     */
    public void renderViewRoles()
    {
        this.renderEditRole = false;
        this.renderViewRoles = true;
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(ROLE_MGT_PANEL_TAB_STATE, this);
    }

    /**
     * @see org.apache.myfaces.custom.tabbedpane.TabChangeListener#processTabChange(org.apache.myfaces.custom.tabbedpane.TabChangeEvent)
     */
    public void processTabChange(TabChangeEvent tabChangeEvent) throws AbortProcessingException
    {
        HtmlPanelTabbedPane tabbedPane = (HtmlPanelTabbedPane) tabChangeEvent.getComponent();
        List children = tabbedPane.getChildren();
        for (int i = 0, len = children.size(); i < len; i++)
        {
            UIComponent child = (UIComponent) children.get(i);
            if (child instanceof HtmlPanelTab)
            {
                if (child.getId().equals(EDIT_ROLE_TAB_ID) && child.isRendered())
                {
                    renderViewRoles();
                }
            }
        }
    }
}