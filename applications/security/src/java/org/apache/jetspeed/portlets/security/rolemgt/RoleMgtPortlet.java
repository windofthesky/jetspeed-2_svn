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

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.faces.context.FacesContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.BasePrincipal;
import org.apache.myfaces.custom.tree.DefaultMutableTreeNode;
import org.apache.myfaces.custom.tree.model.DefaultTreeModel;
import org.apache.myfaces.custom.tree.model.TreeModel;
import org.apache.portals.bridges.myfaces.FacesPortlet;

/**
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class RoleMgtPortlet extends FacesPortlet
{

    private static final Log log = LogFactory.getLog(RoleMgtPortlet.class);

    /** Role tree table binding variable. */
    private static final String ROLE_TREE_TABLE = "roleTreeTable";
    
    /** The role tree model. */
    TreeModel roleTreeModel = new DefaultTreeModel();

    /**
     * @see javax.portlet.Portlet#init(javax.portlet.PortletConfig)
     */
    public void init(PortletConfig config) throws PortletException
    {
        super.init(config);

        Preferences prefs = Preferences.userRoot().node(
                (BasePrincipal.PREFS_ROLE_ROOT).substring(0, (BasePrincipal.PREFS_ROLE_ROOT).length() - 1));

        roleTreeModel = buildTreeModel(prefs);
    }
   
    /**
     * @see org.apache.portals.bridges.myfaces.FacesPortlet#preProcessFaces(javax.faces.context.FacesContext)
     */
    protected void preProcessFaces(FacesContext context)
    {
        context.getExternalContext().getSessionMap().put(ROLE_TREE_TABLE, new RoleTreeTable(roleTreeModel));
    }
    
    /**
     * <p>
     * Build the tree model.
     * </p>
     * 
     * @param prefs The preferences.
     * @return The tree model.
     */
    private TreeModel buildTreeModel(Preferences prefs)
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new RoleTreeItem(prefs.absolutePath(), prefs.name()));
        processPreferences(prefs, root);

        return new DefaultTreeModel(root);
    }

    /**
     * <p>
     * Recursively processes the preferences to build the role tree model.
     * </p>
     * 
     * @param prefs The preferences.
     * @param parent The parent to add the role item to.
     */
    protected void processPreferences(Preferences prefs, DefaultMutableTreeNode parent)
    {
        try
        {
            String[] names = prefs.childrenNames();
            for (int i = 0; i < names.length; i++)
            {
                Preferences childPrefs = prefs.node(names[i]);
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(new RoleTreeItem(childPrefs.absolutePath(), names[i]));
                parent.insert(child);
                processPreferences(childPrefs, child);
            }
        }
        catch (BackingStoreException bse)
        {
            log.warn("can't find children of " + prefs.absolutePath(), bse);
        }
    }

}