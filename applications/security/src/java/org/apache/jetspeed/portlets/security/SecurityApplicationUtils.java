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
package org.apache.jetspeed.portlets.security;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.portlets.security.rolemgt.RoleTreeItem;
import org.apache.jetspeed.portlets.security.rolemgt.RoleTreeTable;
import org.apache.jetspeed.security.BasePrincipal;
import org.apache.myfaces.custom.tree.DefaultMutableTreeNode;
import org.apache.myfaces.custom.tree.model.DefaultTreeModel;

/**
 * <p>
 * Utility class for the security application.
 * </p>
 * 
 * @author <a href="dlestrat@apache.org">David Le Strat</a>
 */
public class SecurityApplicationUtils
{
    /** The logger. */
    private static final Log log = LogFactory.getLog(SecurityApplicationUtils.class);
    
    /** Node path delimiter. */
    private static final String PATH_DELIMITER = "/";
   
    /**
     * <p>
     * Build the role tree model.
     * </p>
     * 
     * @return The tree model.
     */
    public static DefaultTreeModel buildRoleTreeModel()
    {
        DefaultTreeModel roleTreeModel = buildTreeModel(BasePrincipal.PREFS_ROLE_ROOT);
        
        return roleTreeModel;
    }
    
    /**
     * <p>
     * Build the tree model.
     * </p>
     * 
     * @param prefs The preferences.
     * @return The tree model.
     */
    private static DefaultTreeModel buildTreeModel(String prefsRoot)
    {
        Preferences prefs = Preferences.userRoot().node(prefsRoot.substring(0, prefsRoot.length() - 1));
        
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
    private static void processPreferences(Preferences prefs, DefaultMutableTreeNode parent)
    {
        try
        {
            String[] names = prefs.childrenNames();
            for (int i = 0; i < names.length; i++)
            {
                Preferences childPrefs = prefs.node(names[i]);
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(new RoleTreeItem(childPrefs.absolutePath(), names[i]));
                System.out.println("++++++ Rebuilding tree - Adding to tree: " + childPrefs.absolutePath());
                parent.insert(child);
                processPreferences(childPrefs, child);
            }
        }
        catch (BackingStoreException bse)
        {
            log.warn("can't find children of " + prefs.absolutePath(), bse);
        }
    }
    
    /**
     * <p>
     * Finds a tree node in the tree model given the nodePath of the node to find.
     * </p>
     * 
     * @param treeModel The tree model.
     * @param nodePath The path of the node to find.
     * @return The {@link DefaultMutableTreeNode}.
     */
    public static DefaultMutableTreeNode findTreeNode(RoleTreeTable treeModel, String nodePath)
    {
        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treeModel.getTreeModel().getRoot();
        int nodePathDepth = getNodePathDepth(nodePath);
        for (int i = 1; i < nodePathDepth; i++)
        {
            if (null != treeNode)
            {
                treeNode = findTreeNodeAtDepth(getNodePathAtDepth(nodePath, i), treeNode.children());
            }
        }
        return treeNode;
    }
    
    /**
     * <p>
     * Finds the current child matching the sub node path where the node path is the path
     * matching the level of the children.
     * </p>
     * 
     * @param nodePath The node path to find the child at.
     * @param children The children at the given level.
     * @return
     */
    protected static DefaultMutableTreeNode findTreeNodeAtDepth(String nodePath, Iterator children)
    {
        DefaultMutableTreeNode nodeAtDepth = null;
        while (children.hasNext())
        {
            DefaultMutableTreeNode currNode = (DefaultMutableTreeNode) children.next();
            RoleTreeItem currItem = (RoleTreeItem) currNode.getUserObject();
            if (currItem.getFullPath().equals(nodePath))
            {
                nodeAtDepth = currNode;
                break;
            }
        }
        return nodeAtDepth;
    }
    
    /**
     * <p>
     * Gets a subset of the node path at the given depth.
     * </p>
     * 
     * @param nodePath The node path.
     * @param depth The depth where depth starts at 0;
     * @return The subset of the node path.
     */
    protected static String getNodePathAtDepth(String nodePath, int depth)
    {
        StringTokenizer tokens = new StringTokenizer(nodePath, PATH_DELIMITER);
        int nodeDepth = tokens.countTokens();
        String nodePathAtDepth = nodePath;
        if (depth < nodeDepth)
        {
            nodePathAtDepth = PATH_DELIMITER;
            for (int i = 0; i <= depth; i ++)
            {
                nodePathAtDepth += tokens.nextToken();
                if ((depth > 0) && (i <= depth - 1))
                {
                    nodePathAtDepth += PATH_DELIMITER;
                }
            }
        }
        return nodePathAtDepth;
    }
    
    /**
     * <p>
     * The depth represented in the node path.
     * </p>
     * 
     * @param nodePath The node path.
     * @return The depth.
     */
    protected static int getNodePathDepth(String nodePath)
    {
        StringTokenizer tokens = new StringTokenizer(nodePath, PATH_DELIMITER);
        return tokens.countTokens();
    }

}
