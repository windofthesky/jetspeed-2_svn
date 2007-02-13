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

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
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
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 */
public class PersistenceBrokerPreferencesProvider extends InitablePersistenceBrokerDaoSupport implements
        PreferencesProvider
{

    private static class NodeCache implements Serializable
    {
        /** The serial uid. */
        private static final long serialVersionUID = 1853381807991868844L;

        Node node;

        String fullpath;

        int type;

        boolean childrenLoaded;

        Collection children;

        public NodeCache(Node node)
        {
  // System.out.println(this.getClass().getName() + "-" + "NodeCache (node)" + node.getFullPath());
            this.node = node;
            this.fullpath = node.getFullPath();
            this.type = node.getNodeType();
        }

        public NodeCache(String fullpath, int type)
        {
            // System.out.println(this.getClass().getName() + "-" + "NodeCache - fullpath=" + fullpath);
                        this.fullpath = fullpath;
            this.type = type;
        }

        public boolean isChildrenLoaded()
        {
            // System.out.println(this.getClass().getName() + "-" + "isChildrenLoaded");
            return childrenLoaded;
        }

        public void setChildrenLoaded(boolean childrenLoaded)
        {
            // System.out.println(this.getClass().getName() + "-" + "setChildrenLoaded");
            this.childrenLoaded = childrenLoaded;
        }

        public String getFullpath()
        {
            // System.out.println(this.getClass().getName() + "-" + "getFullpath=" + fullpath);
           return fullpath;
        }

        public Node getNode()
        {
            // System.out.println(this.getClass().getName() + "-" + "getNode=" + node.getFullPath());
            return node;
        }

        public void setNode(Node node)
        {
            // System.out.println(this.getClass().getName() + "-" + "setFullpath=" + node.getFullPath());
            this.node = node;
        }

        public int getType()
        {
            // System.out.println(this.getClass().getName() + "-" + "getType=" );
            return type;
        }

        public Collection getChildren()
        {
            // System.out.println(this.getClass().getName() + "-" + "getCHildren=" );
            return children;
        }

        public void setChildren(Collection children)
        {
            // System.out.println(this.getClass().getName() + "-" + "setChildren=" );
                       this.children = children;
        }

        public boolean equals(Object obj)
        {
            if (obj != null && obj instanceof NodeCache)
            {
                NodeCache other = (NodeCache) obj;
                return fullpath.equals(other.fullpath) && type == other.type;
            }
            return false;
        }

        public int hashCode()
        {
            return fullpath.hashCode() + type;
        }
        
        public String getCacheKey()
        {
            return fullpath +  "-" +  type;
        }
    }

    private JetspeedCache preferenceCache;
    
    
    /**
     * @param repository
     *            Location of repository mapping file. Must be available within the classpath.
     * @param prefsFactoryImpl
     *            <code>java.util.prefs.PreferencesFactory</code> implementation to use.
     * @param enablePropertyManager
     *            Whether or not we chould be suing the property manager.
     * @throws ClassNotFoundException
     *             if the <code>prefsFactoryImpl</code> argument does not reperesent a Class that exists in the
     *             current classPath.
     */
    public PersistenceBrokerPreferencesProvider(String repositoryPath)
            throws ClassNotFoundException
    {
        super(repositoryPath);
    }

    /**
     * @param repository
     *            Location of repository mapping file. Must be available within the classpath.
     * @param prefsFactoryImpl
     *            <code>java.util.prefs.PreferencesFactory</code> implementation to use.
     * @param enablePropertyManager
     *            Whether or not we chould be suing the property manager.
     * @throws ClassNotFoundException
     *             if the <code>prefsFactoryImpl</code> argument does not reperesent a Class that exists in the
     *             current classPath.
     */
    public PersistenceBrokerPreferencesProvider(String repositoryPath, JetspeedCache preferenceCache)
            throws ClassNotFoundException
    {
        super(repositoryPath);
        this.preferenceCache = preferenceCache;
    }

    protected void addToCache(NodeCache content)
    {
        CacheElement cachedElement = preferenceCache.createElement(content.getCacheKey(), content);
        cachedElement.setTimeToIdleSeconds(preferenceCache.getTimeToIdleSeconds());
        cachedElement.setTimeToLiveSeconds(preferenceCache.getTimeToLiveSeconds());
        preferenceCache.put(cachedElement);        
    }    
  
    private NodeCache getNode(String cacheKey)
    {
        CacheElement cachedElement = preferenceCache.get(cacheKey);
        if (cachedElement != null)
         return (NodeCache)cachedElement.getContent();  
        return null;
    }
    
    /**
     * @see org.apache.jetspeed.prefs.PreferencesProvider#getNode(java.lang.String, int)
     */
    public Node getNode(String fullPath, int nodeType) throws NodeDoesNotExistException
    {
        
        NodeCache key = new NodeCache(fullPath, nodeType);
        NodeCache hit = getNode(key.getCacheKey());
        if (hit != null)
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
            addToCache(new NodeCache(nodeObj));
            return nodeObj;
        }
        else
        {
            throw new NodeDoesNotExistException("No node of type " + nodeType + "found at path: " + fullPath);
        }
    }

    /**
     * @see org.apache.jetspeed.prefs.PreferencesProvider#nodeExists(java.lang.String, int)
     */
    public boolean nodeExists(String fullPath, int nodeType)
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
     * @see org.apache.jetspeed.prefs.PreferencesProvider#createNode(org.apache.jetspeed.prefs.om.Node, java.lang.String, int, java.lang.String)
     */
    public Node createNode(Node parent, String nodeName, int nodeType, String fullPath)
            throws FailedToCreateNodeException, NodeAlreadyExistsException
    {
        if (nodeExists(fullPath, nodeType))
        {
            throw new NodeAlreadyExistsException("Node of type " + nodeType + " already exists at path " + fullPath);
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
                addToCache(new NodeCache(nodeObj));
                return nodeObj;
            }
            catch (Exception e)
            {
                throw new FailedToCreateNodeException("Failed to create node of type " + nodeType + " for the path "
                        + fullPath + ".  " + e.toString(), e);
            }

        }
    }
  
    /**
     * @see org.apache.jetspeed.prefs.PreferencesProvider#getChildren(org.apache.jetspeed.prefs.om.Node)
     */
    public Collection getChildren(Node parentNode)
    {
        NodeCache key = new NodeCache(parentNode);
        NodeCache hit = getNode(key.getCacheKey());
        if (hit == null)
        {
            
            key.setNode(parentNode);
            addToCache(key);
            hit = key;
        }
        if (hit.isChildrenLoaded())
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

    /**
     * @see org.apache.jetspeed.prefs.PreferencesProvider#storeNode(org.apache.jetspeed.prefs.om.Node)
     */
    public void storeNode(Node node)
    {
        NodeCache key = new NodeCache(node);
        preferenceCache.remove(key.getCacheKey()); // not sure we should actually do that, could also just update the node
        getPersistenceBrokerTemplate().store(node);
    }

    /**
     * @see org.apache.jetspeed.prefs.PreferencesProvider#removeNode(org.apache.jetspeed.prefs.om.Node, org.apache.jetspeed.prefs.om.Node)
     */
    public void removeNode(Node parentNode, Node node)
    {
        NodeCache key = new NodeCache(node);
        preferenceCache.remove(key.getCacheKey());
        if ( parentNode != null )
        {
            key = new NodeCache(parentNode);
            NodeCache hit = getNode(key.getCacheKey());
            if ( hit != null && hit.isChildrenLoaded() )
            {
                hit.getChildren().remove(node);
            }
        }
        getPersistenceBrokerTemplate().delete(node);        
    }
    
    /**
     * @see org.apache.jetspeed.prefs.PreferencesProvider#lookupPreference(java.lang.String, java.lang.String, java.lang.String)
     */
    public Collection lookupPreference(String nodeName, String propertyName, String propertyValue)
    {
        Criteria c = new Criteria();
        if (nodeName != null)
        {
            c.addEqualTo("nodeName", nodeName);
        }
        if (propertyName != null)
        {
            c.addEqualTo("nodeProperties.propertyName", propertyName);
        }
        if (propertyValue != null)
        {
            c.addEqualTo("nodeProperties.propertyValue", propertyValue);
        }
        Query query = QueryFactory.newQuery(NodeImpl.class, c);
        Collection children = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return children;       
    }
}