/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.prefs.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.prefs.PropertyException;
import org.apache.jetspeed.prefs.PropertyManager;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.prefs.om.PropertyKey;
import org.apache.jetspeed.prefs.om.impl.PropertyKeyImpl;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>
 * {@link PropertyManager}implementation relying on Jetspeed OJB based
 * persistence plugin for persistence.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class PropertyManagerImpl implements PropertyManager
{
    private static final Log log = LogFactory.getLog(PropertyManagerImpl.class);

    /** User <tt>Preferences<tt> node type. */
    private static final int USER_NODE_TYPE = 0;

    /** System <tt>Preferences</tt> node type. */
    private static final int SYSTEM_NODE_TYPE = 1;

    /** Common queries. * */
    private CommonQueries commonQueries;

    private PersistenceStore persistenceStore;
    


    /**
     * <p>
     * Constructor providing access to the persistence component.
     * </p>
     */
    public PropertyManagerImpl(PersistenceStore persistenceStore)
    {
        if (persistenceStore == null)
        {
            throw new IllegalArgumentException("persistenceStore cannot be null for PropertyManagerImpl");
        }
        
        

        this.persistenceStore = persistenceStore;
        this.commonQueries = new CommonQueries(persistenceStore);
    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#addPropertyKeys(java.util.prefs.Preferences,
     *      java.util.Map)
     */
    public void addPropertyKeys(Preferences prefNode, Map propertyKeysMap) throws PropertyException
    {
        ArgUtil.notNull(new Object[]{prefNode, propertyKeysMap}, new String[]{"prefNode", "propertyKeysMap",},
                "addPropertyKeys(java.util.prefs.Preferences, java.util.Collection)");

        Node nodeObj;
        if (prefNode.isUserNode())
        {
            nodeObj = (Node) persistenceStore.getObjectByQuery(commonQueries.newNodeQueryByPathAndType(prefNode
                    .absolutePath(), new Integer(USER_NODE_TYPE)));
        }
        else
        {
            nodeObj = (Node) persistenceStore.getObjectByQuery(commonQueries.newNodeQueryByPathAndType(prefNode
                    .absolutePath(), new Integer(SYSTEM_NODE_TYPE)));
        }
        if (null != nodeObj)
        {
            // Get the existing property keys.
            Collection propertyKeys = nodeObj.getNodeKeys();
            ArrayList newPropertyKeys = new ArrayList(propertyKeysMap.size());
            for (Iterator i = propertyKeysMap.keySet().iterator(); i.hasNext();)
            {
                boolean foundKey = false;
                String currentPropertyKeyName = (String) i.next();
                for (Iterator j = propertyKeys.iterator(); j.hasNext();)
                {
                    PropertyKey existingPpk = (PropertyKey) j.next();
                    if (propertyKeysMap.containsKey(existingPpk.getPropertyKeyName()))
                    {
                        if (log.isDebugEnabled())
                            log.debug("Existing Property: " + (String) propertyKeysMap.get(currentPropertyKeyName));
                        foundKey = true;
                        newPropertyKeys.add(existingPpk);
                        break;
                    }
                }
                if (!foundKey)
                {
                    if (log.isDebugEnabled())
                        log.debug("New Property: " + currentPropertyKeyName);
                    PropertyKey ppk = new PropertyKeyImpl(currentPropertyKeyName, ((Integer) propertyKeysMap
                            .get(currentPropertyKeyName)).intValue());
                    newPropertyKeys.add(ppk);
                }
            }

            // Add the properties keys.
            try
            {
                if (log.isDebugEnabled())
                    log.debug("Node: " + nodeObj.toString());
                if (log.isDebugEnabled())
                    log.debug("Node property keys: " + newPropertyKeys.toString());
                persistenceStore.lockForWrite(nodeObj);
                nodeObj.setNodeKeys(newPropertyKeys);
                nodeObj.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                persistenceStore.getTransaction().checkpoint();
            }
            catch (Exception e)
            {
                String msg = "Unable to lock Node for update.";
                log.error(msg, e);
                persistenceStore.getTransaction().rollback();
                throw new PropertyException(msg, e);
            }
        }
        else
        {
            throw new PropertyException(PropertyException.NODE_NOT_FOUND);
        }
    }
    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#getPropertyKeys(java.util.prefs.Preferences)
     */
    public Map getPropertyKeys(Preferences prefNode)
    {
        ArgUtil.notNull(new Object[]{prefNode}, new String[]{"prefNode"},
                "getPropertyKeys(java.util.prefs.Preferences)");

        Node nodeObj;
        if (prefNode.isUserNode())
        {
            nodeObj = (Node) persistenceStore.getObjectByQuery(commonQueries.newNodeQueryByPathAndType(prefNode
                    .absolutePath(), new Integer(USER_NODE_TYPE)));
        }
        else
        {
            nodeObj = (Node) persistenceStore.getObjectByQuery(commonQueries.newNodeQueryByPathAndType(prefNode
                    .absolutePath(), new Integer(SYSTEM_NODE_TYPE)));
        }
        if (null != nodeObj)
        {
            Collection keys = nodeObj.getNodeKeys();
            HashMap propertyKeysMap = new HashMap(keys.size());
            for (Iterator i = keys.iterator(); i.hasNext();)
            {
                PropertyKey curpk = (PropertyKey) i.next();
                propertyKeysMap.put(curpk.getPropertyKeyName(), new Integer(curpk.getPropertyKeyType()));
            }
            return propertyKeysMap;
        }
        else
        {
            return new HashMap(0);
        }
    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#removePropertyKeys(java.util.prefs.Preferences,
     *      java.util.Collection)
     */
    public void removePropertyKeys(Preferences prefNode, Collection propertyKeys) throws PropertyException
    {
        ArgUtil.notNull(new Object[]{prefNode, propertyKeys}, new String[]{"prefNode", "propertyKeys"},
                "removePropertyKeys(java.util.prefs.Preferences, java.util.Collection)");

        Node nodeObj;
        if (prefNode.isUserNode())
        {
            nodeObj = (Node) persistenceStore.getObjectByQuery(commonQueries.newNodeQueryByPathAndType(prefNode
                    .absolutePath(), new Integer(USER_NODE_TYPE)));
        }
        else
        {
            nodeObj = (Node) persistenceStore.getObjectByQuery(commonQueries.newNodeQueryByPathAndType(prefNode
                    .absolutePath(), new Integer(SYSTEM_NODE_TYPE)));
        }
        if (null != nodeObj)
        {
            Collection properties = nodeObj.getNodeProperties();
            ArrayList newProperties = new ArrayList(properties.size());
            Collection keys = nodeObj.getNodeKeys();
            ArrayList newKeys = new ArrayList(keys.size());
            for (Iterator i = properties.iterator(); i.hasNext();)
            {
                Property curProp = (Property) i.next();
                PropertyKey curPropKey = (PropertyKey) curProp.getPropertyKey();
                if ((null != curPropKey) && (!propertyKeys.contains(curProp.getPropertyKey().getPropertyKeyName())))
                {
                    newProperties.add(curProp);
                }
            }
            for (Iterator j = newKeys.iterator(); j.hasNext();)
            {
                PropertyKey curPropKey = (PropertyKey) j.next();
                if (!propertyKeys.contains(curPropKey.getPropertyKeyName()))
                {
                    newKeys.add(curPropKey);
                }
            }
            // Remove the properties keys.
            try
            {
                persistenceStore.lockForWrite(nodeObj);
                nodeObj.setNodeKeys(newKeys);
                nodeObj.setNodeProperties(newProperties);
                nodeObj.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                persistenceStore.getTransaction().checkpoint();
            }
            catch (Exception e)
            {
                String msg = "Unable to lock Node for update.";
                log.error(msg, e);
                persistenceStore.getTransaction().rollback();
                throw new PropertyException(msg, e);
            }
        }
        else
        {
            throw new PropertyException(PropertyException.NODE_NOT_FOUND);
        }
    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#updatePropertyKey(java.lang.String,
     *      java.util.prefs.Preferences, java.util.Map)
     */
    public void updatePropertyKey(String oldPropertyKeyName, Preferences prefNode, Map newPropertyKey)
            throws PropertyException
    {
        ArgUtil.notNull(new Object[]{oldPropertyKeyName, prefNode, newPropertyKey}, new String[]{"oldPropertyKeyName",
                "prefNode", "newPropertyKey"},
                "updatePropertyKey(java.lang.String, java.util.prefs.Preferences, java.util.Map)");

        Node nodeObj;
        if (prefNode.isUserNode())
        {
            nodeObj = (Node) persistenceStore.getObjectByQuery(commonQueries.newNodeQueryByPathAndType(prefNode
                    .absolutePath(), new Integer(USER_NODE_TYPE)));
        }
        else
        {
            nodeObj = (Node) persistenceStore.getObjectByQuery(commonQueries.newNodeQueryByPathAndType(prefNode
                    .absolutePath(), new Integer(SYSTEM_NODE_TYPE)));
        }
        if (null != nodeObj)
        {
            Collection keys = nodeObj.getNodeKeys();
            for (Iterator i = keys.iterator(); i.hasNext();)
            {
                PropertyKey curPropKey = (PropertyKey) i.next();
                if (curPropKey.getPropertyKeyName().equals(oldPropertyKeyName))
                {
                    for (Iterator j = newPropertyKey.keySet().iterator(); j.hasNext();)
                    {
                        String newKey = (String) j.next();
                        // Update the property key.
                        try
                        {
                            persistenceStore.lockForWrite(curPropKey);
                            curPropKey.setPropertyKeyName(newKey);
                            curPropKey.setPropertyKeyType(((Integer) newPropertyKey.get(newKey)).intValue());
                            curPropKey.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                            if (log.isDebugEnabled())
                                log.debug("Updated property key: " + curPropKey.toString());
                            persistenceStore.getTransaction().checkpoint();
                        }
                        catch (Exception e)
                        {
                            String msg = "Unable to lock Node for update.";
                            log.error(msg, e);
                            persistenceStore.getTransaction().rollback();
                            throw new PropertyException(msg, e);
                        }
                    }
                }
            }
        }
        else
        {
            throw new PropertyException(PropertyException.NODE_NOT_FOUND);
        }
    }
}