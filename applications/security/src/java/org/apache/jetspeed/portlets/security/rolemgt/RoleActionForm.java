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
import java.util.Collection;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.portlets.security.SecurityApplicationResources;
import org.apache.jetspeed.portlets.security.SecurityApplicationUtils;
import org.apache.jetspeed.security.Role;
import org.apache.jetspeed.security.RoleManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.myfaces.custom.tree.TreeNode;
import org.apache.myfaces.custom.tree.model.DefaultTreeModel;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class RoleActionForm implements Serializable
{
    
    /** The logger. */
    private static final Log log = LogFactory.getLog(RoleActionForm.class);
    
    /** The role action form bean. */
    public final static String ROLE_ACTION_FORM = "roleActionForm";
    
    /** The default parent role path. */
    private final static String DEFAULT_PARENT_ROLE_PATH = "/role";
    
    /** The default parent role name. */
    private final static String DEFAULT_PARENT_ROLE_NAME = "role";

    /** The parent role. */
    private RoleTreeItem parentRole;

    /** The selected roles. */
    private String[] selectedRoles;

    /** The new role name. */
    private String roleName;

    /** The new role path. */
    private String rolePath;

    /**
     * <p>
     * Default Constructor.
     * </p>
     */
    public RoleActionForm()
    {
        this.parentRole = new RoleTreeItem(DEFAULT_PARENT_ROLE_PATH, DEFAULT_PARENT_ROLE_NAME);
    }

    /**
     * @return Returns the parentRole.
     */
    public RoleTreeItem getParentRole()
    {
        return parentRole;
    }

    /**
     * @param parentRole The parentRole to set.
     */
    public void setParentRole(RoleTreeItem parentRole)
    {
        this.parentRole = parentRole;
    }

    /**
     * @return Returns the selectedRoles.
     */
    public String[] getSelectedRoles()
    {
        return selectedRoles;
    }

    /**
     * @param selectedRoles The selectedRoles to set.
     */
    public void setSelectedRoles(String[] selectedRoles)
    {
        this.selectedRoles = selectedRoles;
    }

    /**
     * @return Returns the roleName.
     */
    public String getRoleName()
    {
        return roleName;
    }

    /**
     * @param roleName The roleName to set.
     */
    public void setRoleName(String roleName)
    {
        this.roleName = roleName;
    }

    /**
     * @return Returns the rolePath.
     */
    public String getRolePath()
    {
        return rolePath;
    }

    /**
     * @param rolePath The rolePath to set.
     */
    public void setRolePath(String rolePath)
    {
        this.rolePath = rolePath;
    }

    /**
     * <p>
     * Add a new role.
     * </p>
     */
    public void addRole()
    {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map appMap = (Map) externalContext.getApplicationMap();
        RoleManager roleMgr = (RoleManager) appMap.get(SecurityApplicationResources.CPS_ROLE_MANAGER_COMPONENT);  
        try
        {
            String roleToAddPath = getRoleName();
            // If the role to add is relative to a selected parent.
            if (!getParentRole().getRoleName().equals(DEFAULT_PARENT_ROLE_NAME))
            {
                roleToAddPath = getParentRole().getRoleName() + "." + roleToAddPath;
            }
            if (log.isDebugEnabled())
            {
                log.debug("Adding role: " + roleToAddPath);
            }
            // Add role.
            roleMgr.addRole(roleToAddPath);
            // Get the newly added role.
            Role addedRole = roleMgr.getRole(roleToAddPath);
            // Resolve the tree table.
            FacesContext facesContext = FacesContext.getCurrentInstance();
            VariableResolver vr = facesContext.getApplication().getVariableResolver();
            RoleTreeTable roleTree = (RoleTreeTable) vr.resolveVariable(facesContext, RoleTreeTable.ROLE_TREE_TABLE);
            // Get the listeners registered.
            Collection listeners = roleTree.getTreeModel().getTreeModelListeners();
            // TODO We could be more sophisticated and not rebuild the old tree.  For now this will do.
            roleTree.setTreeModel(SecurityApplicationUtils.buildRoleTreeModel());
            // Get the new tree model.
            DefaultTreeModel treeModel = roleTree.getTreeModel();
            // Add the old listeners back...
            treeModel.getTreeModelListeners().addAll(listeners);
            // Get the index of the new node.
            TreeNode parentNode = SecurityApplicationUtils.findTreeNode(roleTree, getParentRole().getFullPath());
            TreeNode childNode = SecurityApplicationUtils.findTreeNode(roleTree, addedRole.getPreferences().absolutePath());
            int [] childIndices = {treeModel.getIndexOfChild(parentNode, childNode)};
            // Send the node inserted event.
            treeModel.nodesWereInserted(parentNode, childIndices);
        }
        catch (SecurityException se)
        {
            log.error("Error adding role " + getRoleName() + ". " + se);
        }
    }   

}