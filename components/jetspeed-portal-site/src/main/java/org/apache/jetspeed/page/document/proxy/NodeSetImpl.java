/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.page.document.proxy;

import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class implements generic NodeSet ordered lists
 * used with proxied instances of PSML Folders to create a
 * logical view of site content.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class NodeSetImpl implements NodeSet
{
    /**
     * nodes - list of proxy nodes
     */
    private List<Node> nodes;

    /**
     * NodeSetImpl - construct immutable proxy Node NodeSet list
     *
     * @param nodes list of proxy Nodes
     */
    public NodeSetImpl(List<Node> nodes)
    {
        this.nodes = nodes;
    }

    /**
     * get - return proxy Node by name or path
     *
     * @param name node name
     * @return Node proxy
     */
    public Node get(String name)
    {
        // search nodes for matching name or path
        for (Node node : this)
        {
            if (node.getName().equals(name) || node.getPath().equals(name))
            {
                return node;
            }
        }
        return null;
    }

    /**
     * iterator - return iterator over ordered list
     *
     * @return proxy NodeSet list iterator
     */
    public Iterator<Node> iterator()
    {
        return nodes.iterator();
    }
    
    /**
     * subset - construct new NodeSet containing Node proxies
     *          of the specified type
     *
     * @param type node type 
     * @return proxy NodeSet list
     */
    public NodeSet subset(String type)
    {
        // search for matching nodes
        List<Node> subsetNodes = null;
        for (Node node : this)
        {
            if (node.getType().equals(type))
            {
                if (subsetNodes == null)
                {
                    subsetNodes = new ArrayList<Node>(nodes.size());
                }
                subsetNodes.add(node);
            }
        }

        // wrap matching nodes in new NodeSet
        if (subsetNodes != null)
            return new NodeSetImpl(subsetNodes);
        return null;
    }

    /**
     * inclusiveSubset - construct new NodeSet containing Node
     *                   proxies whose name or path matches
     *                   the specified regex pattern
     *
     * @param regex proxy Node name/path match pattern 
     * @return proxy NodeSet list
     */
    public NodeSet inclusiveSubset(String regex)
    {
        // search for matching nodes
        List<Node> subsetNodes = null;
        Pattern pattern = Pattern.compile(regex);
        for (Node node : this)
        {
            if (pattern.matcher(node.getName()).matches() || pattern.matcher(node.getPath()).matches())
            {
                if (subsetNodes == null)
                {
                    subsetNodes = new ArrayList<Node>(nodes.size());
                }
                subsetNodes.add(node);
            }
        }

        // wrap matching nodes in new NodeSet
        if (subsetNodes != null)
            return new NodeSetImpl(subsetNodes);
        return null;
    }
    
    /**
     * exclusiveSubset - construct new NodeSet containing Node
     *                   proxies whose name or path does not match
     *                   the specified regex pattern
     *
     * @param regex proxy Node name/path match pattern 
     * @return proxy NodeSet list
     */
    public NodeSet exclusiveSubset(String regex)
    {
        // search for matching nodes
        List<Node> subsetNodes = null;
        Pattern pattern = Pattern.compile(regex);
        for (Node node : this)
        {
            if (!pattern.matcher(node.getName()).matches() && !pattern.matcher(node.getPath()).matches())
            {
                if (subsetNodes == null)
                {
                    subsetNodes = new ArrayList<Node>(nodes.size());
                }
                subsetNodes.add(node);
            }
        }

        // wrap matching nodes in new NodeSet
        if (subsetNodes != null)
            return new NodeSetImpl(subsetNodes);
        return null;
    }

    /**
     * size - return size of NodeSet list
     *
     * @return size of list
     */
    public int size()
    {
        return nodes.size();
    }

    /**
     * contains - test named Node proxy for existance in NodeSet list
     *
     * @param node proxy Node
     * @return Node proxy
     */
    public boolean contains(Node node)
    {
        return nodes.contains(node);
    }

    /**
     * isEmpty - returns flag indicationg whether NodeSet list is
     *           empty or not
     *
     * @return empty flag
     */
    public boolean isEmpty()
    {
        return nodes.isEmpty();
    }

    /**
     * add - adds specified proxyNode to the ordered NodeSet list
     *
     * @param node proxy Node
     */
    public void add(Node node)
    {
        // not implementd for immutable proxy lists
        throw new RuntimeException("NodeSet list is immutable from proxy.");
    }
}
