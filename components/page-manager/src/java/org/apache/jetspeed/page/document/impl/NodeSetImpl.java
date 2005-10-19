/*
 * Copyright 2005 The Apache Software Foundation.
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
package org.apache.jetspeed.page.document.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeSet;

/**
 * NodeSetImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class NodeSetImpl implements NodeSet
{
    private List nodes;
    private Map nodesByName;

    public NodeSetImpl()
    {
    }
    public NodeSetImpl(List nodes)
    {
        this.nodes = new ArrayList(nodes.size());
        this.nodesByName = new HashMap((nodes.size() / 2) + 1);
        Iterator addIter = nodes.iterator();
        while (addIter.hasNext())
        {
            Node node = (Node)addIter.next();
            this.nodes.add(node);
            if (!this.nodesByName.containsKey(node.getName()))
            {
                this.nodesByName.put(node.getName(), node);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#add(org.apache.jetspeed.page.document.Node)
     */
    public void add(Node node)
    {
        if (nodes != null)
        {
            nodes = new ArrayList(8);
            nodesByName = new HashMap(5);
        }
        nodes.add(node);
        if (!nodesByName.containsKey(node.getName()))
        {
            nodesByName.put(node.getName(), node);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#get(java.lang.String)
     */
    public Node get(String name)
    {
        if (nodesByName != null)
        {
            return (Node)nodesByName.get(name);
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#iterator()
     */
    public Iterator iterator()
    {
        if (nodes != null)
        {
            return nodes.iterator();
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#subset(java.lang.String)
     */
    public NodeSet subset(String type)
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#inclusiveSubset(java.lang.String)
     */
    public NodeSet inclusiveSubset(String regex)
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#exclusiveSubset(java.lang.String)
     */
    public NodeSet exclusiveSubset(String regex)
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#size()
     */
    public int size()
    {
        if (nodes != null)
        {
            return nodes.size();
        }
        return 0;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#contains(org.apache.jetspeed.page.document.Node)
     */
    public boolean contains(Node node)
    {
        if (nodes != null)
        {
            return nodes.contains(node);
        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#isEmpty()
     */
    public boolean isEmpty()
    {
        if (nodes != null)
        {
            return nodes.isEmpty();
        }
        return true;
    }
}
