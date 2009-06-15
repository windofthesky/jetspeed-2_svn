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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.jetspeed.cache.CacheElement;
import org.apache.jetspeed.cache.DistributedCacheObject;
import org.apache.jetspeed.cache.JetspeedCache;
import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.prefs.FailedToCreateNodeException;
import org.apache.jetspeed.prefs.NodeAlreadyExistsException;
import org.apache.jetspeed.prefs.NodeDoesNotExistException;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.prefs.om.impl.NodeImpl;
import org.apache.jetspeed.prefs.om.impl.PropertyImpl;
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
    private List preloadedApplications;
    private boolean preloadEntities = false;
    
    /**
     * @param repositoryPath
     *            Location of repository mapping file. Must be available within the classpath.
     * @throws ClassNotFoundException
     *             if the <code>prefsFactoryImpl</code> argument does not reperesent a Class that exists in the
     *             current classPath.
     */
    public PersistenceBrokerPreferencesProvider(String repositoryPath)
            throws ClassNotFoundException
    {
        super(repositoryPath);
        NodeImplProxy.setProvider(this);
        this.preloadedApplications = new LinkedList();
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

    public PersistenceBrokerPreferencesProvider(String repositoryPath, JetspeedCache preferenceCache, List apps, boolean preloadEntities)
    throws ClassNotFoundException
    {
        this(repositoryPath);
        this.preferenceCache = preferenceCache;
        this.preloadedApplications = apps;
        this.preloadEntities = preloadEntities;
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
	    		NodeCache hit = getNode(s);
	    		if (hit != null)
                {
	    			v.add(hit.getNode());
                }
                else
                {
                    int index = s.lastIndexOf("-");
                    if (index > 0)
                    {
                        String fullPath = s.substring(0, index);
                        int type = Integer.parseInt(s.substring(index + 1));
                        Node node = getNode(fullPath, type);
                        if (node != null)
                        {
                            v.add(node);
                        }
                    }
                }
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
        try
        {
            getPersistenceBrokerTemplate().store(hit.getNode()); // avoid racing condition with the db and with cluster notification
                                                                 // do the db first
            preferenceCache.remove(key.getCacheKey());           // not sure we should actually do that, could also just update the node
            addToCache(key);
        }
        catch (Exception e)
        {
            preferenceCache.removeQuiet(key.getCacheKey());      // remove problematic nodes from cache
            throw new RuntimeException("Failed to store node of type " + node.getNodeType() + " for the path " + node.getFullPath() + ".  " + e.toString(), e);
        }
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
    
    public Property createProperty(Node node, String name, Object value)
    {
        return new PropertyImpl(node.getNodeId(), name, value);
    }

    public void preload() throws Exception
    {
        // ensure base root preference nodes exist
        Node systemRoot = null;
        if (!nodeExists("/", PreferencesImpl.SYSTEM_NODE_TYPE))
        {
            systemRoot = createNode(null, "", PreferencesImpl.SYSTEM_NODE_TYPE, "/");
        }
        else
        {
            systemRoot = getNode("/", PreferencesImpl.SYSTEM_NODE_TYPE);
        }
        if (!nodeExists("/" + MutablePortletApplication.PREFS_ROOT, PreferencesImpl.SYSTEM_NODE_TYPE))
        {
            createNode(systemRoot, MutablePortletApplication.PREFS_ROOT, PreferencesImpl.SYSTEM_NODE_TYPE, "/" + MutablePortletApplication.PREFS_ROOT);
        }
        if (!nodeExists("/" + MutablePortletEntity.PORTLET_ENTITY_ROOT, PreferencesImpl.SYSTEM_NODE_TYPE))
        {
            createNode(systemRoot, MutablePortletEntity.PORTLET_ENTITY_ROOT, PreferencesImpl.SYSTEM_NODE_TYPE, "/" + MutablePortletEntity.PORTLET_ENTITY_ROOT);
        }
        if (!nodeExists("/", PreferencesImpl.USER_NODE_TYPE))
        {
            createNode(null, "", PreferencesImpl.USER_NODE_TYPE, "/");
        }
        // preload portlet application
        Iterator apps = this.preloadedApplications.iterator();
        while (apps.hasNext())
        {
            String appName = (String)apps.next();
            preloadApplicationPreferences(appName);
        }
        // preload portlet entities
        if (preloadEntities)
        {
            preloadAllEntities();
        }
    }
    
    public void preloadApplicationPreferences(String portletApplicationName) throws NodeDoesNotExistException
    {
        String portletDefPrefPath = "/" + MutablePortletApplication.PREFS_ROOT + "/" + portletApplicationName + "/";
//        + PortletDefinitionComposite.PORTLETS_PREFS_ROOT + "/" + portlet.getName() + "/"
//        + MutablePortletApplication.PORTLET_PREFERENCES_ROOT;
//        NodeCache key = new NodeCache(portletDefPrefPath, 1);
//        NodeCache hit = getNode(key.getCacheKey());
//        if (hit != null)
//        {
//            return 1;
//            //return hit.getNode();
//        }        
        long start = System.currentTimeMillis();        
        int count = loadNodeAndAllChildren(portletDefPrefPath);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("++++ PREFS:PA loaded " + count + " pref nodes for app " + portletDefPrefPath + " in " + elapsed + " milliseconds.");
    }
    
    protected int loadNodeAndAllChildren(String path)
    {
        int count = 0;
        NodeCache root = null;
        Criteria c = new Criteria();
        c.addLike("fullPath", path + "%");
        //c.addOrderBy("fullPath");
        Query query = QueryFactory.newQuery(NodeImpl.class, c);
        Collection result = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        // TODO: ensure that we always get the first node back first
        if (result == null || result.isEmpty())
        {
            return count;           
        }
        Iterator ri = result.iterator();
        if (ri.hasNext())
        {
            Node n = (Node)ri.next();
            NodeImplProxy proxy = new NodeImplProxy(n);
            root = new NodeCache(proxy);
            addToCache(root);
            count++;
        }
        else
        {
            return count;        
        }
        Map parents = new HashMap();
        parents.put(new Long(root.getNode().getNodeId()), root);
        while (ri.hasNext())
        {
            // build children and subchildren
            Node subNode = (Node)ri.next();
            //System.out.println("*** Preloading: " + subNode.getFullPath());
            // add to current node
            NodeCache nodeKey = new NodeCache(subNode.getFullPath(), subNode.getNodeType());
            NodeCache lookup = getNode(nodeKey.getCacheKey());
            if (lookup == null)
            {
                NodeImplProxy proxy = new NodeImplProxy(subNode);
                nodeKey.setNode(proxy);
                addToCache(nodeKey);
                lookup = nodeKey;
            }
            NodeCache parent = (NodeCache)parents.get(subNode.getParentNodeId());
            if (parent != null)
            {
                if (parent.getChildren() == null)
                    parent.setChildren(new ArrayList());
                parent.getChildren().add(lookup.getCacheKey());
                count += parent.getChildren().size();
            }
            parents.put(new Long(subNode.getNodeId()), lookup);
            count++;
        }         
        return count;
    }
    
    public void preloadAllEntities() throws NodeDoesNotExistException
    {
        String entitiesRoot = "/" + MutablePortletEntity.PORTLET_ENTITY_ROOT + "/";
        long start = System.currentTimeMillis();        
        int count = loadNodeAndAllChildren(entitiesRoot);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("++++ PREFS:ENTITIES loaded " + count + " total entity pref nodes in " + elapsed + " milliseconds.");
    }
 
    public void destroy()
    {
        NodeImplProxy.setProvider(null);
        preferenceCache = null;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.prefs.PreferencesProvider#clearCachedApplicationPreferences(java.lang.String)
     */
    public void clearCachedApplicationPreferences(String portletApplicationName)
    {
        String portletDefPrefPath = "/" + MutablePortletApplication.PREFS_ROOT + "/" + portletApplicationName + "/";
        long start = System.currentTimeMillis();        
        int count = clearCachedNodeAndAllChildren(portletDefPrefPath);
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("++++ PREFS:PA clear cached " + count + " pref nodes for app " + portletDefPrefPath + " in " + elapsed + " milliseconds.");
    }    

    /**
     * Clear node and all children based on specified path from cache.
     * 
     * @param path full path of node to remove
     * @return count of nodes cleared from cache
     */
    protected int clearCachedNodeAndAllChildren(String path)
    {
        int count = 0;
        String root = path.substring(0, path.length()-1);
        List preferenceCacheKeys = preferenceCache.getKeys();
        for (Iterator iter = preferenceCacheKeys.iterator(); iter.hasNext();)
        {
            CacheElement preferenceCacheElement = preferenceCache.get(iter.next());
            if (preferenceCacheElement != null)
            {
                String preferenceFullPath = ((NodeCache)preferenceCacheElement.getContent()).getNode().getFullPath();
                if (preferenceFullPath.startsWith(path) || preferenceFullPath.equals(root))
                {
                    if (preferenceCache.removeQuiet(preferenceCacheElement.getKey()))
                    {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}