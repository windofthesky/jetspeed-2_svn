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
package org.apache.jetspeed.prefs.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.DistributedCacheObject;
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

    private static class NodeCache implements DistributedCacheObject
    {
        /** The serial uid. */
        private static final long serialVersionUID = 1853381807991868844L;
        NodeImplProxy node = null;
        String key = null;;
        Collection children = null;;

        public NodeCache(NodeImplProxy node)
        {
            // System.out.println(this.getClass().getName() + "-" + "NodeCache (node)" + node.getFullPath());
            this.node = node;
            this.key = node.getFullPath() + "-" + node.getNodeType();
        }

        public NodeCache(String fullpath, int type)
        {
            // System.out.println(this.getClass().getName() + "-" + "NodeCache - fullpath=" + fullpath);
            this.key = fullpath + "-" + type;
        }

        public boolean isChildrenLoaded()
        {
            // System.out.println(this.getClass().getName() + "-" + "isChildrenLoaded");
            return children != null;
        }



        public NodeImplProxy getNode()
        {
            // System.out.println(this.getClass().getName() + "-" + "getNode=" + node.getFullPath());
            return node;
        }

        public void setNode(NodeImplProxy node)
        {
            // System.out.println(this.getClass().getName() + "-" + "setFullpath=" + node.getFullPath());
            this.node = node;
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
                return getKey().equals(other.getKey());
            }
            return false;
        }

        public int hashCode()
        {
            return getKey().hashCode();
        }
        
        public String getCacheKey()
        {
            return getKey();
        }

		public String getKey()
		{
			return key;
		}

		
	    public void notifyChange(int action)
	    {

	    	switch (action)
	    	{
	    		case CacheElement.ActionAdded:
//					System.out.println("CacheObject Added =" + this.getKey());
	    			break;
	    		case CacheElement.ActionChanged:
//					System.out.println("CacheObject Changed =" + this.getKey());
					if (this.node != null)
						this.node.invalidate();
	    			break;
	    		case CacheElement.ActionRemoved:
//					System.out.println("CacheObject Removed =" + this.getKey());
					if (this.node != null)
						this.node.invalidate();
	    			break;
	    		case CacheElement.ActionEvicted:
//					System.out.println("CacheObject Evicted =" + this.getKey());
					if (this.node != null)
						this.node.invalidate();
	    			break;
	    		case CacheElement.ActionExpired:
//					System.out.println("CacheObject Expired =" + this.getKey());
					if (this.node != null)
						this.node.invalidate();
	    			break;
	    		default:
					System.out.println("CacheObject - UNKOWN OPRERATION =" + this.getKey());
	    			return;
	    	}
	    	return;
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
        NodeImplProxy.setProvider(this);
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
        this(repositoryPath);
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
        	NodeImplProxy proxy = new NodeImplProxy(nodeObj);
            addToCache(new NodeCache(proxy));
            return proxy;
           
        }
        else
        {
            throw new NodeDoesNotExistException("No node of type " + nodeType + "found at path: " + fullPath);
        }
    }
    /**
     * @see org.apache.jetspeed.prefs.PreferencesProvider#getNode(java.lang.String, int)
     */
    public void redoNode(NodeImplProxy proxy, String fullPath, int nodeType) throws NodeDoesNotExistException
    {
        
        Criteria c = new Criteria();
        c.addEqualTo("fullPath", fullPath);
        c.addEqualTo("nodeType", new Integer(nodeType));
        Query query = QueryFactory.newQuery(NodeImpl.class, c);

        Node nodeObj = (Node) getPersistenceBrokerTemplate().getObjectByQuery(query);
        if (null != nodeObj)
        {
        	proxy.setNode(nodeObj);
        	NodeCache cn = new NodeCache(nodeObj.getFullPath(), nodeObj.getNodeType());
        	cn.setNode(proxy);
            addToCache(cn);
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
        NodeCache key = new NodeCache(fullPath, nodeType);
        if (preferenceCache.isKeyInCache(key))
        	return true;
        Criteria c = new Criteria();
        c.addEqualTo("fullPath", fullPath);
        c.addEqualTo("nodeType", new Integer(nodeType));
        Query query = QueryFactory.newQuery(NodeImpl.class, c);

        Node nodeObj = (Node) getPersistenceBrokerTemplate().getObjectByQuery(query);
        if (null != nodeObj)
        {
        	NodeImplProxy proxy = new NodeImplProxy(nodeObj);
            addToCache(new NodeCache(proxy));
            return true;
        }
        else
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
            	NodeImplProxy proxy = new NodeImplProxy(nodeObj);
                addToCache(new NodeCache(proxy));
                return proxy;
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
        NodeCache key = new NodeCache(parentNode.getFullPath(), parentNode.getNodeType());

        NodeCache hit = getNode(key.getCacheKey());
        if (hit == null)
        {
        	NodeImplProxy proxy = new NodeImplProxy(parentNode);
            hit = new NodeCache(proxy);
            addToCache(hit);
        }
        if (hit.isChildrenLoaded())
        {
            return resolveChildren(hit.getChildren());
        }

        Criteria c = new Criteria();
        c.addEqualTo("parentNodeId", new Long(parentNode.getNodeId()));
        Query query = QueryFactory.newQuery(NodeImpl.class, c);
        Collection children = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        hit.setChildren(cacheChildren(children));
        // null or not
        return children;
    }

    
    private Collection resolveChildren(Collection children)
    {
    	if (children == null)
    		return null;
    	try
    	{
	    	Iterator it = children.iterator();
	    	Vector v = new Vector();
	    	while (it.hasNext())
	    	{
	    		String s = (String) it.next();
	    		NodeCache hit =getNode(s);
	    		if (hit != null)
	    			v.add(hit.getNode());
	    	}
	    	return v;
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    		return null;
    	}
    }

    
    private Collection cacheChildren(Collection children)
    {
    	Iterator it = children.iterator();
    	Vector v = new Vector();
    	while (it.hasNext())
    	{
    		   Node key = (Node)it.next();	
    	       NodeCache nodeKey = new NodeCache(key.getFullPath(),key.getNodeType());
    	       NodeCache hit = getNode(nodeKey.getCacheKey());
   	           if (hit == null)
   	           {
   	    		   NodeImplProxy proxy = new NodeImplProxy(key);
   	    		   nodeKey.setNode(proxy);
   	    	       addToCache(nodeKey);
   	    	       hit= nodeKey;
   	           }
    	        v.add(hit.getCacheKey());
    	}
    	return v;
    }

    /**
     * @see org.apache.jetspeed.prefs.PreferencesProvider#storeNode(org.apache.jetspeed.prefs.om.Node)
     */
    public void storeNode(Node node)
    {
    	NodeImplProxy hit = null;
    	if (node instanceof NodeImplProxy)
    	{
    		hit = (NodeImplProxy)node;
    	}
    	else
    	{
    		//System.out.println("WARNING!!!!STORE NODE!!!!!!!!!!!! -  Illegal Node element passed");
    		hit = new NodeImplProxy(node);
    	}
    	
        NodeCache key = new NodeCache(hit);
        getPersistenceBrokerTemplate().store(hit.getNode()); // avoid racing condition with the db and with cluster notification
        											// do the db first
        preferenceCache.remove(key.getCacheKey()); // not sure we should actually do that, could also just update the node
        addToCache(key);
    }

    /**
     * @see org.apache.jetspeed.prefs.PreferencesProvider#removeNode(org.apache.jetspeed.prefs.om.Node, org.apache.jetspeed.prefs.om.Node)
     */
    public void removeNode(Node parentNode, Node node)
    {
    	NodeImplProxy hit = null;
    	NodeImplProxy parentHit = null;

    	if (node instanceof NodeImplProxy)
    	{
    		getPersistenceBrokerTemplate().delete(((NodeImplProxy)node).getNode());  //avoid race conditions - do this first    
    	}
    	else
    		getPersistenceBrokerTemplate().delete(node);  //avoid race conditions - do this first    
    		
    	if (node instanceof NodeImplProxy)
    	{
    		hit = (NodeImplProxy)node;
    	}
    	else
    	{
    		//System.out.println("WARNING!!!!REMOVE NODE!!!!!!!!!!!! -  Illegal Node element passed");
    		hit = new NodeImplProxy(node);
    	}
        NodeCache key = new NodeCache(hit);
        preferenceCache.remove(key.getCacheKey());
        if ( parentNode != null )
        {
        	if (parentNode instanceof NodeImplProxy)
        	{
        		parentHit = (NodeImplProxy)parentNode;
        	}
        	else
        	{
        		//System.out.println("WARNING!!!!REMOVE NODE!!!!!!!!!!!! -  Illegal Node element passed");
        		parentHit = new NodeImplProxy(parentNode);
        	}
        	NodeCache parentKey = new NodeCache(parentHit);
        	parentKey = getNode(parentKey.getCacheKey());
            if ( parentKey != null && parentKey.isChildrenLoaded() )
            {
            	parentKey.getChildren().remove(key.getCacheKey());
            }
        }
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
        Collection proxied = new ArrayList();
        Iterator iter = children.iterator();
        while (iter.hasNext())
        {
            NodeImpl node = (NodeImpl)iter.next();              
            NodeCache key = new NodeCache(node.getFullPath(), node.getNodeType());
            NodeCache hit = getNode(key.getCacheKey());
            if (hit == null)
            {
                NodeImplProxy proxy = new NodeImplProxy(node);
                addToCache(new NodeCache(proxy));
                proxied.add(proxy);
            }            
            else
            {
                proxied.add(hit.getNode());
            }
        }
        return proxied;       
    }
}