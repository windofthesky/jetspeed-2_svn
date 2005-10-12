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

import java.util.Iterator;

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
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#add(org.apache.jetspeed.page.document.Node)
     */
    public void add(Node node)
    {
        // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#get(java.lang.String)
     */
    public Node get(String name)
    {
        return null; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#iterator()
     */
    public Iterator iterator()
    {
        return null; // NYI
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
        return -1; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#contains(org.apache.jetspeed.page.document.Node)
     */
    public boolean contains(Node node)
    {
        return false; // NYI
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.NodeSet#isEmpty()
     */
    public boolean isEmpty()
    {
        return true; // NYI
    }
}
