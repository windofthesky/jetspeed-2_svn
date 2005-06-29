/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.prefs.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.page.document.NodeNotFoundException;
import org.apache.jetspeed.prefs.FailedToCreateNodeException;
import org.apache.jetspeed.prefs.NodeAlreadyExistsException;
import org.apache.jetspeed.prefs.NodeDoesNotExistException;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.impl.NodeImpl;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;

/**
 * <p>
 * PersistenceBrokerPreferencesProvider
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class PersistenceBrokerPreferencesProvider extends InitablePersistenceBrokerDaoSupport implements PreferencesProvider
{

    private static class NodeCache implements Serializable
    {
        Node node;
        String fullpath;
        int type;        
        boolean childrenLoaded;
        Collection children;

        public NodeCache(Node node)
        {
            this.node = node;
            this.fullpath = node.getFullPath();
            this.type = node.getNodeType();
        }
        
        public NodeCache(String fullpath, int type)
        {
            this.fullpath = fullpath;
            this.type     = type;
        }
        
        public boolean isChildrenLoaded()
        {
            return childrenLoaded;
        }

        public void setChildrenLoaded(boolean childrenLoaded)
        {
            this.childrenLoaded = childrenLoaded;
        }

        public String getFullpath()
        {
            return fullpath;
        }

        public Node getNode()
        {
            return node;
        }
        
        public void setNode(Node node)
        {
            this.node = node;
        }
        
        public int getType()
        {
            return type;
        }
        
        public Collection getChildren()
        {
            return children;
        }

        public void setChildren(Collection children)
        {
            this.children = children;
        }
        
        public boolean equals(Object obj)
        {
            if ( obj != null && obj instanceof NodeCache )
            {
                NodeCache other = (NodeCache)obj;
                return fullpath.equals(other.fullpath) && type == other.type;
            }
            return false;
        }
        
        public int hashCode()
        {
            return fullpath.hashCode() + type;
        }
    }
    
    private HashMap nodeMap = new HashMap();
    private boolean enablePropertyManager;

    /**
     * 
     * @param repository Location of repository mapping file.  Must be available within the classpath.
     * @param prefsFactoryImpl <code>java.util.prefs.PreferencesFactory</code> implementation to use.
     * @param enablePropertyManager  Whether or not we chould be suing the property manager.
     * @throws ClassNotFoundException if the <code>prefsFactoryImpl</code> argument does not reperesent
     * a Class that exists in the current classPath.
     */
    public PersistenceBrokerPreferencesProvider(String repositoryPath, boolean enablePropertyManager) throws ClassNotFoundException
    {
        super(repositoryPath);                
        this.enablePropertyManager = enablePropertyManager;
    }
    
    /**
     * <p>
     * Get the node id from the full path.
     * </p>
     * 
     * @param fullPath
     *            The full path.
     * @param nodeType
     *            The node type.
     * @return An array of value returned including:
     * @throws NodeNotFoundException
     *             if the node does not exist
     *  
     */
    public Node getNode( String fullPath, int nodeType ) throws NodeDoesNotExistException
    {
        NodeCache key = new NodeCache(fullPath, nodeType);
        NodeCache hit = (NodeCache)nodeMap.get(key);
        if ( hit != null )
        {
            return hit.getNode();
        }
        
        Criteria c = new Criteria();
        c.addEqualTo("fullPath", fullPath);
        c.addEqualTo("nodeType", new Integer(nodeType));
        Query query = QueryFactory.newQuery(NodeImpl.class, c);

        Node nodeObj = (Node) getPersistenceBrokerTemplate().getObjectByQuery(query);
        if (null != nodeObj)
        {
            key.setNode(nodeObj);           
            nodeMap.put(key, key);
            return nodeObj;
        }
        else
        {
            throw new NodeDoesNotExistException("No node of type " + nodeType + "found at path: " + fullPath);
        }
    }
    
    /**
     * 
     * <p>
     * nodeExists
     * </p>
     *
     * @param fullPath
     * @param nodeType
     * @return
     */
    public boolean nodeExists( String fullPath, int nodeType )
    {
        try
        {
            getNode(fullPath, nodeType);
            return true;
        }
        catch (NodeDoesNotExistException e)
        {
            return false;
        }
    }

    /**
     * <p>
     * Create a new preference node in the backing store.
     * </p>
     * 
     * @param parent
     *            The parent node.
     * @param nodeName
     *            The node name.
     * @param nodeType
     *            The node type.
     * @param fullPath
     *            The node full path.
     * @return the newly created node
     * @throws NodeAlreadyExistsException if a node of the same type having the same path
     * already exists.
     */
    public Node createNode( Node parent, String nodeName, int nodeType, String fullPath )
            throws FailedToCreateNodeException, NodeAlreadyExistsException
    {      
        if (nodeExists(fullPath, nodeType))
        {
            throw new NodeAlreadyExistsException("Node of type "+nodeType+" already exists at path "+fullPath);
        }
        else
        {
            Long parentNodeId = null;
            if (null != parent)
            {
                parentNodeId = new Long(parent.getNodeId());
            }

            Node nodeObj = new NodeImpl(parentNodeId, nodeName, nodeType, fullPath);
            
            try
            {
                getPersistenceBrokerTemplate().store(nodeObj);
                NodeCache key = new NodeCache(nodeObj);
                nodeMap.put(key,key);
                return nodeObj;
            }
            catch (Exception e)
            {
               throw new FailedToCreateNodeException("Failed to create node of type "+nodeType+" for the path "+fullPath+".  "+e.toString(), e);
            }
            
        }
    }
    
    /**
     * 
     * <p>
     * getChildren
     * </p>
     *
     * @see org.apache.jetspeed.prefs.PreferencesProvider#getChildren(org.apache.jetspeed.prefs.om.Node)
     * @param parentNode
     * @return
     */
    public Collection getChildren( Node parentNode )
    {
        NodeCache key = new NodeCache(parentNode);
        NodeCache hit = (NodeCache)nodeMap.get(key);
        if ( hit == null )
        {
            key.setNode(parentNode);
            nodeMap.put(key,key);
            hit = key;
        }
        if ( hit.isChildrenLoaded() )
        {
            return hit.getChildren();
        }
        
        Criteria c = new Criteria();
        c.addEqualTo("parentNodeId", new Long(parentNode.getNodeId()));
        Query query = QueryFactory.newQuery(NodeImpl.class, c);
        Collection children = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        hit.setChildren(children);
        // null or not
        hit.setChildrenLoaded(true);
        return children;
    }
    
    public void storeNode( Node node )
    {
       NodeCache key = new NodeCache(node);
       nodeMap.remove(key);
       getPersistenceBrokerTemplate().store(node);
    }
    
    public void removeNode( Node node )
    {
        NodeCache key = new NodeCache(node);
        nodeMap.remove(key);
       getPersistenceBrokerTemplate().delete(node);
    }

    /**
     * <p>
     * isPropertyManagerEnabled
     * </p>
     *
     * @see org.apache.jetspeed.prefs.PreferencesProvider#isPropertyManagerEnabled()
     * @return
     */
    public boolean isPropertyManagerEnabled()
    {
        return this.enablePropertyManager;
    }
}