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
package org.apache.jetspeed.page.document.impl;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.LRUMap;
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
    public static final NodeSetImpl EMPTY_NODE_SET = new NodeSetImpl();

    private static final Map patternCache = new LRUMap(128);

    private Map nodes;
    private Comparator comparator;

    public NodeSetImpl(List nodes, Comparator comparator)
    {
        this.nodes = new TreeMap(comparator);        
        Object[] nodeToCopy = nodes.toArray();
        for (int ix = 0; ix < nodeToCopy.length; ix++)
        {
            Node node = (Node)nodeToCopy[ix];
            if (!this.nodes.containsKey( node.getName()))
            {
                this.nodes.put(node.getName(), node);
            }
        }         
        this.comparator = comparator;
    }

    public NodeSetImpl(List nodes)
    {
        this(nodes, null);
    }

    public NodeSetImpl(Comparator comparator)
    {
        this.comparator = comparator;
    }

    public NodeSetImpl(NodeSet nodeSet)
    {
        this((nodeSet instanceof NodeSetImpl) ? ((NodeSetImpl)nodeSet).comparator : (Comparator)null);
    }

    public NodeSetImpl()
    {
    }

    /**
     * getCachedPattern
     *
     * @param regex pattern
     * @return cached pattern
     */
    private Pattern getCachedPattern(String regex)
    {
        synchronized (patternCache)
        {
            if (patternCache.containsKey(regex))
            {
                return (Pattern)patternCache.get(regex);
            }
            else
            {
                Pattern pattern = Pattern.compile(regex);
                patternCache.put(regex, pattern);
                return pattern;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#add(org.apache.jetspeed.page.document.Node)
     */
    public void add(Node node)
    {
        if (nodes == null)
        {
            nodes = new TreeMap(comparator);
        }
        if (!nodes.containsKey(node.getName()))
        {
            nodes.put(node.getName(), node);
        }
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#get(java.lang.String)
     */
    public Node get(String name)
    {
        if (nodes != null)
        {
            return (Node)nodes.get(name);
        }
        return null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#iterator()
     */
    public Iterator iterator()
    {
        if (nodes == null)
        {
            nodes = new TreeMap(comparator);
        }
        return nodes.values().iterator();
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#subset(java.lang.String)
     */
    public NodeSet subset(String type)
    {
        NodeSetImpl subset = new NodeSetImpl(comparator);
        Iterator nodeItr = iterator();
        while (nodeItr.hasNext())
        {
            Node node = (Node) nodeItr.next();
            if (node.getType().equals(type))
            {
                subset.add(node);
            }
        }
        return subset;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#inclusiveSubset(java.lang.String)
     */
    public NodeSet inclusiveSubset(String regex)
    {
        Pattern pattern = getCachedPattern(regex);
        NodeSetImpl subset = new NodeSetImpl(comparator);
        Iterator nodeItr = iterator();
        while (nodeItr.hasNext())
        {
            Node node = (Node) nodeItr.next();
            if (pattern.matcher(node.getName()).matches())
            {
                subset.add(node);
            }
        }
        return subset;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#exclusiveSubset(java.lang.String)
     */
    public NodeSet exclusiveSubset(String regex)
    {
        Pattern pattern = getCachedPattern(regex);
        NodeSetImpl subset = new NodeSetImpl(comparator);
        Iterator nodeItr = iterator();
        while (nodeItr.hasNext())
        {
            Node node = (Node) nodeItr.next();
            if (!pattern.matcher(node.getName()).matches())
            {
                subset.add(node);
            }
        }
        return subset;
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
            return nodes.containsValue(node);
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
