/*
 * Copyright 2004 The Apache Software Foundation.
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

import org.apache.myfaces.custom.tree.DefaultMutableTreeNode;
import org.apache.myfaces.custom.tree.model.DefaultTreeModel;
import org.apache.myfaces.custom.tree.model.TreeModel;

/**
 * <p>
 * Bean holding the tree hierarchy.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class RoleTreeTable implements Serializable
{
    private DefaultTreeModel treeModel;

    /**
     * @param treeModel The treeModel.
     */
    public RoleTreeTable(TreeModel treeModel)
    {
        this.treeModel = (DefaultTreeModel) treeModel;
    }

    /**
     * <p>
     * Default constructor.
     * </p>
     */
    public RoleTreeTable()
    {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new RoleTreeItem("/role/XY", "XY"));
        DefaultMutableTreeNode a = new DefaultMutableTreeNode(new RoleTreeItem("/role/XY/A", "A"));
        root.insert(a);
        DefaultMutableTreeNode b = new DefaultMutableTreeNode(new RoleTreeItem("/role/XY/B", "B"));
        root.insert(b);
        DefaultMutableTreeNode c = new DefaultMutableTreeNode(new RoleTreeItem("/role/XY/C", "C"));
        root.insert(c);

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(new RoleTreeItem("/role/XY/A/a1", "a1"));
        a.insert(node);
        node = new DefaultMutableTreeNode(new RoleTreeItem("/role/XY/A/a2", "a2"));
        a.insert(node);
        node = new DefaultMutableTreeNode(new RoleTreeItem("/role/XY/A/a3", "a3"));
        a.insert(node);
        node = new DefaultMutableTreeNode(new RoleTreeItem("/role/XY/B/b", "b"));
        b.insert(node);

        a = node;
        node = new DefaultMutableTreeNode(new RoleTreeItem("/role/XY/B/b/x1", "x1"));
        a.insert(node);
        node = new DefaultMutableTreeNode(new RoleTreeItem("/role/XY/B/b/x2", "x2"));
        a.insert(node);
        
        this.treeModel = new DefaultTreeModel(root);
    }

    /**
     * @return Returns the treeModel.
     */
    public DefaultTreeModel getTreeModel()
    {
        return treeModel;
    }

    /**
     * @param treeModel The treeModel to set.
     */
    public void setTreeModel(DefaultTreeModel treeModel)
    {
        this.treeModel = treeModel;
    }
}