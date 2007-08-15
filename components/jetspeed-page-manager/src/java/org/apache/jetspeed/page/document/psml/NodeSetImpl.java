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
package org.apache.jetspeed.page.document.psml;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeSet;

/**
 * <p>
 * PageSetImpl
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class NodeSetImpl implements NodeSet
{
    private Map nodes;
    private Map subsets;
    private String resolveToPath;
    private Comparator comparator;
    protected static final Map patternCache = new HashMap();

    public NodeSetImpl( String resolveToPath )
    {
        this.resolveToPath = resolveToPath;
        nodes = new TreeMap();
        subsets = new HashMap();
    }

    /**
     * 
     * @param resolveToPath
     * @param comparator
     */
    public NodeSetImpl( String resolveToPath, Comparator comparator )
    {
        this.resolveToPath = resolveToPath;
        nodes = new TreeMap(comparator);
        this.comparator = comparator;
        subsets = new HashMap();
    }

    /**
     * 
     * <p>
     * get
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.NodeSet#get(java.lang.String)
     * @param name
     * @return
     */
    public Node get( String name )
    {

        if (nodes.containsKey(name))
        {
            return (Node) nodes.get(name);
        }
        else if (resolveToPath != null)
        {
            if (resolveToPath.endsWith(Node.PATH_SEPARATOR))
            {
                return (Node) nodes.get(resolveToPath + name);
            }
            else
            {
                return (Node) nodes.get(resolveToPath + Node.PATH_SEPARATOR + name);
            }
        }

        return null;
    }

    /**
     * 
     * <p>
     * add
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.NodeSet#add(org.apache.jetspeed.page.document.Node)
     * @param document
     */
    public void add( Node node )
    {
        String path = node.getPath();
        nodes.put(path, node);
        if (subsets.containsKey(node.getType()))
        {
            ((NodeSet) subsets.get(node.getType())).add(node);
        }
    }

    /**
     * <p>
     * size
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.NodeSet#size()
     * @return
     */
    public int size()
    {
        return nodes.size();
    }

    /**
     * 
     * <p>
     * iterator
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.NodeSet#iterator()
     * @return
     */
    public Iterator iterator()
    {
        return nodes.values().iterator();
    }

    /**
     * <p>
     * subset
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.NodeSet#subset(java.lang.String)
     * @param type
     * @return
     */
    public NodeSet subset( String type )
    {
        NodeSet subset = (NodeSet) subsets.get(type);
        if (subset == null)
        {
            subset = new NodeSetImpl(resolveToPath, comparator);
            subsets.put(type, subset);

            Iterator nodeItr = nodes.values().iterator();
            while (nodeItr.hasNext())
            {
                Node node = (Node) nodeItr.next();
                if (node.getType().equals(type))
                {
                    subset.add(node);
                }
            }
        }

        return subset;
    }

    /**
     * <p>
     * exclusiveSubset
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.NodeSet#exclusiveSubset(java.lang.String)
     * @param regex
     * @return
     */
    public NodeSet exclusiveSubset( String regex )
    {
        Iterator allNodes = nodes.entrySet().iterator();
        NodeSetImpl subset = new NodeSetImpl(resolveToPath, comparator);
        final Pattern pattern = getPattern(regex);
        while (allNodes.hasNext())
        {
            Map.Entry entry = (Map.Entry) allNodes.next();
            Node node = (Node) entry.getValue();
            String key = (String) entry.getKey();
            if (!matches(pattern, key) && !matches(pattern, node.getName()))
            {
                subset.add(node);
            }
        }
        
        return subset;
    }

    /**
     * <p>
     * inclusiveSubset
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.NodeSet#inclusiveSubset(java.lang.String)
     * @param regex
     * @return
     */
    public NodeSet inclusiveSubset( String regex )
    {
        Iterator allNodes = nodes.entrySet().iterator();
        NodeSetImpl subset = new NodeSetImpl(resolveToPath, comparator);
        final Pattern pattern = getPattern(regex);
        while (allNodes.hasNext())
        {
            Map.Entry entry = (Map.Entry) allNodes.next();
            String key = (String) entry.getKey();
            Node node = (Node) entry.getValue();
            if (matches(pattern, key) || matches(pattern, node.getName()))
            {
                subset.add(node);
            }
        }
        
        return subset;
    }
    
    /**
     * 
     * <p>
     * getComparator
     * </p>
     * 
     * @return comparator used to order nodes
     */
    public Comparator getComparator()
    {
        return comparator;
    }

    /**
     * 
     * <p>
     * matches
     * </p>
     *
     * @param pattern
     * @param value
     * @return
     */
    protected final boolean matches(Pattern pattern, String value)
    {
        return pattern.matcher(value).matches();
    }
    
    /**
     * 
     * <p>
     * getPattern
     * </p>
     *
     * @param regex
     * @return
     */
    protected final Pattern getPattern(String regex)
    {        
        if(patternCache.containsKey(regex))
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

    /**
     * <p>
     * contains
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.NodeSet#contains()
     * @return
     */
    public boolean contains( Node node )
    {
        return nodes.values().contains(node);
    }

    /**
     * <p>
     * isEmpty
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.NodeSet#isEmpty()
     * @return
     */
    public boolean isEmpty()
    {
        return nodes.isEmpty();
    }

    /**
     * <p>
     * remove
     * </p>
     * 
     * @param node to remove
     * @return removed node
     */
    public Node remove(Node node)
    {
        String path = node.getPath();
        if (nodes.get(path) == node)
        {
            nodes.remove(path);
            if (subsets.containsKey(node.getType()))
            {
                ((NodeSetImpl) subsets.get(node.getType())).remove(node);
            }
        }
        return null;
    }
}
