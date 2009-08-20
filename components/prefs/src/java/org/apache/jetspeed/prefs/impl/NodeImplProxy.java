/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package org.apache.jetspeed.prefs.impl;


import java.sql.Timestamp;
import java.util.Collection;

import org.apache.jetspeed.prefs.om.Node;

public class NodeImplProxy implements  Node
{
    private Node node = null;
    private String fullPath = null;
    private int nodeType = -1;
    private boolean dirty = false;
    private static PersistenceBrokerPreferencesProvider provider;


    protected Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public Timestamp getCreationDate()
	{
        Node node = getNode();
        if (node != null)
        {
            return node.getCreationDate();
        }
        throw new RuntimeException("Node not defined, getCreationDate() invoked on negative cache entry");
	}

	public String getFullPath()
	{
        Node node = getNode();
        if (node != null)
        {
            return node.getFullPath();
        }
        return fullPath;
	}

	public Timestamp getModifiedDate()
	{
        Node node = getNode();
        if (node != null)
        {
            return node.getModifiedDate();
        }
        throw new RuntimeException("Node not defined, getModifiedDate() invoked on negative cache entry");
	}

	public long getNodeId()
	{
        Node node = getNode();
        if (node != null)
        {
            return node.getNodeId();
        }
        throw new RuntimeException("Node not defined, getNodeId() invoked on negative cache entry");
	}

	public Collection getNodeKeys()
	{
        Node node = getNode();
        if (node != null)
        {
            return node.getNodeKeys();
        }
        throw new RuntimeException("Node not defined, getNodeKeys() invoked on negative cache entry");
	}

	public String getNodeName()
	{
        Node node = getNode();
        if (node != null)
        {
            return node.getNodeName();
        }
        throw new RuntimeException("Node not defined, getNodeName() invoked on negative cache entry");
	}

	public Collection getNodeProperties()
	{
        Node node = getNode();
        if (node != null)
        {
            return node.getNodeProperties();	
        }
        throw new RuntimeException("Node not defined, getNodeProperties() invoked on negative cache entry");
	}

	public int getNodeType()
	{
        Node node = getNode();
        if (node != null)
        {
            return node.getNodeType();
        }
        return nodeType;
	}

	public Long getParentNodeId()
	{
        Node node = getNode();
        if (node != null)
        {
            return node.getParentNodeId();
        }
        throw new RuntimeException("Node not defined, getParentNodeId() invoked on negative cache entry");
	}

	public void setCreationDate(Timestamp creationDate)
	{
	    getNode().setCreationDate(creationDate);		
	}

	public void setFullPath(String fullPath)
	{
	    Node node = getNode();
	    if (node != null)
	    {
	        node.setFullPath(fullPath);
	    }
	    else
	    {
	        this.fullPath = fullPath;
	    }
	}

	public void setModifiedDate(Timestamp modifiedDate)
	{
		getNode().setModifiedDate(modifiedDate);		
	}

	public void setNodeId(long nodeId)
	{
		getNode().setNodeId(nodeId);		
	}

	public void setNodeKeys(Collection nodeKeys)
	{
		getNode().setNodeKeys(nodeKeys);
	}

	public void setNodeName(String nodeName)
	{
		getNode().setNodeName(nodeName);
	}

	public void setNodeProperties(Collection nodeProperties)
	{
		getNode().setNodeProperties(nodeProperties);		
	}

	public void setNodeType(int nodeType)
	{
        Node node = getNode();
        if (node != null)
        {
            node.setNodeType(nodeType);
        }
        else
        {
            this.nodeType = nodeType;
        }
	}

	public void setParentNodeId(Long parentNodeId)
	{
		getNode().setParentNodeId(parentNodeId);
	}

	public NodeImplProxy(Node node)
    {
        this.node = node;
    }

    public NodeImplProxy(String fullPath, int nodeType)
    {
        this.fullPath = fullPath;
        this.nodeType = nodeType;
    }

    public static void setProvider(PersistenceBrokerPreferencesProvider p)
    {
    	provider = p;
    }
    
    public Node getNode() 
    {
        if (dirty)
        	reset();
        return node;
    }
    

    protected void invalidate()
    {
        this.dirty = true;
    }
    
    public void setNode(Node node)
    {
    	this.node = node;
        this.fullPath = null;
        this.nodeType = -1;
    }

    protected void reset()
    {
        try
        {
            provider.redoNode(this,node.getFullPath(), node.getNodeType());
            dirty = false;
        }
        catch (Exception e)
        {
            try
            {
                // try again, we may have ran out of connections as reproduced May 2008
                provider.redoNode(this,node.getFullPath(), node.getNodeType());
                dirty = false;                
            }
            catch (Exception e2)
            {
                throw new RuntimeException("Failed to reset preference node. Unable to load node.", e2);                 
            }
            e.printStackTrace();            
        }
    }        

}
