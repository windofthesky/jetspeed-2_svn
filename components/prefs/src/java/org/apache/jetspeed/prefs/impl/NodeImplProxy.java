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
    private boolean dirty = false;
    private static PersistenceBrokerPreferencesProvider provider;


    protected Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	public Timestamp getCreationDate()
	{
		return getNode().getCreationDate();
		}

	public String getFullPath()
	{
		return getNode().getFullPath();
	}

	public Timestamp getModifiedDate()
	{
		return getNode().getModifiedDate();
	}

	public long getNodeId()
	{
		return getNode().getNodeId();
	}

	public Collection getNodeKeys()
	{
		return getNode().getNodeKeys();
	}

	public String getNodeName()
	{
		return getNode().getNodeName();
	}

	public Collection getNodeProperties()
	{
		return getNode().getNodeProperties();	
	}

	public int getNodeType()
	{
		return getNode().getNodeType();
	}

	public Long getParentNodeId()
	{
		return getNode().getParentNodeId();
	}

	public void setCreationDate(Timestamp creationDate)
	{
		getNode().setCreationDate(creationDate);		
	}

	public void setFullPath(String fullPath)
	{
		getNode().setFullPath(fullPath);		
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
		getNode().setNodeType(nodeType);		
	}

	public void setParentNodeId(Long parentNodeId)
	{
		getNode().setParentNodeId(parentNodeId);
	}

	public NodeImplProxy(Node node)
    {
        this.node = node;
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
    		e.printStackTrace();
    		node = null;
    	}
    }
    

}
