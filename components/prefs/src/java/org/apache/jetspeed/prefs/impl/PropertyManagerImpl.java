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

import org.apache.jetspeed.prefs.NodeDoesNotExistException;
import org.apache.jetspeed.prefs.PreferencesException;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.prefs.PropertyException;
import org.apache.jetspeed.prefs.PropertyManager;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.prefs.om.PropertyKey;
import org.apache.jetspeed.prefs.om.impl.PropertyKeyImpl;
import org.apache.jetspeed.util.ArgUtil;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * <p>
 * {@link PropertyManager}implementation relying on Jetspeed OJB based
 * persistence plugin for persistence.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class PropertyManagerImpl extends PersistenceBrokerDaoSupport implements PropertyManager
{
    /** User <tt>Preferences<tt> node type. */
    private static final int USER_NODE_TYPE = 0;

    /** System <tt>Preferences</tt> node type. */
    private static final int SYSTEM_NODE_TYPE = 1;

    protected PreferencesProvider prefsProvider;

    /**
     * <p>
     * Constructor providing access to the PreferencesProvider component.
     * </p>
     */
    public PropertyManagerImpl( PreferencesProvider prefsProvider )
    {
        super();
        this.prefsProvider = prefsProvider;
    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#addPropertyKeys(java.util.prefs.Preferences,
     *      java.util.Map)
     */
    public void addPropertyKeys( Preferences prefNode, Map propertyKeysMap ) throws PropertyException,
            PreferencesException
    {
        ArgUtil.notNull(new Object[]{prefNode, propertyKeysMap}, new String[]{"prefNode", "propertyKeysMap",},
                "addPropertyKeys(java.util.prefs.Preferences, java.util.Collection)");

        Node nodeObj = getNode(prefNode);

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
                    if (logger.isDebugEnabled())
                        logger.debug("Existing Property: " + (String) propertyKeysMap.get(currentPropertyKeyName));
                    foundKey = true;
                    newPropertyKeys.add(existingPpk);
                    break;
                }
            }
            if (!foundKey)
            {
                if (logger.isDebugEnabled())
                    logger.debug("New Property: " + currentPropertyKeyName);
                PropertyKey ppk = new PropertyKeyImpl(currentPropertyKeyName, ((Integer) propertyKeysMap
                        .get(currentPropertyKeyName)).intValue());
                newPropertyKeys.add(ppk);
            }
        }

        // Add the properties keys.

        if (logger.isDebugEnabled())
            logger.debug("Node: " + nodeObj.toString());
        if (logger.isDebugEnabled())
            logger.debug("Node property keys: " + newPropertyKeys.toString());

        nodeObj.setNodeKeys(newPropertyKeys);
        nodeObj.setModifiedDate(new Timestamp(System.currentTimeMillis()));
        prefsProvider.storeNode(nodeObj);

    }

    /**
     * @throws PreferencesException
     * @see org.apache.jetspeed.prefs.PropertyManager#getPropertyKeys(java.util.prefs.Preferences)
     */
    public Map getPropertyKeys( Preferences prefNode ) throws PreferencesException
    {
        ArgUtil.notNull(new Object[]{prefNode}, new String[]{"prefNode"},
                "getPropertyKeys(java.util.prefs.Preferences)");

        Node nodeObj = getNode(prefNode);

        Collection keys = nodeObj.getNodeKeys();
        HashMap propertyKeysMap = new HashMap(keys.size());
        for (Iterator i = keys.iterator(); i.hasNext();)
        {
            PropertyKey curpk = (PropertyKey) i.next();
            propertyKeysMap.put(curpk.getPropertyKeyName(), new Integer(curpk.getPropertyKeyType()));
        }
        return propertyKeysMap;

    }

    /**
     * @throws PreferencesException
     * @see org.apache.jetspeed.prefs.PropertyManager#removePropertyKeys(java.util.prefs.Preferences,
     *      java.util.Collection)
     */
    public void removePropertyKeys( Preferences prefNode, Collection propertyKeys ) throws PropertyException,
            PreferencesException
    {
        ArgUtil.notNull(new Object[]{prefNode, propertyKeys}, new String[]{"prefNode", "propertyKeys"},
                "removePropertyKeys(java.util.prefs.Preferences, java.util.Collection)");

        Node nodeObj = getNode(prefNode);

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

        nodeObj.setNodeKeys(newKeys);
        nodeObj.setNodeProperties(newProperties);
        nodeObj.setModifiedDate(new Timestamp(System.currentTimeMillis()));
        prefsProvider.storeNode(nodeObj);

    }

    /**
     * @throws PreferencesException
     * @see org.apache.jetspeed.prefs.PropertyManager#updatePropertyKey(java.lang.String,
     *      java.util.prefs.Preferences, java.util.Map)
     */
    public void updatePropertyKey( String oldPropertyKeyName, Preferences prefNode, Map newPropertyKey )
            throws PropertyException, PreferencesException
    {
        ArgUtil.notNull(new Object[]{oldPropertyKeyName, prefNode, newPropertyKey}, new String[]{"oldPropertyKeyName",
                "prefNode", "newPropertyKey"},
                "updatePropertyKey(java.lang.String, java.util.prefs.Preferences, java.util.Map)");

        Node nodeObj = getNode(prefNode);

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

                    curPropKey.setPropertyKeyName(newKey);
                    curPropKey.setPropertyKeyType(((Integer) newPropertyKey.get(newKey)).intValue());
                    curPropKey.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                    if (logger.isDebugEnabled())
                        logger.debug("Updated property key: " + curPropKey.toString());

                    getPersistenceBrokerTemplate().store(curPropKey);

                }
            }
        }

    }

    /**
     * <p>
     * getNode
     * </p>
     * 
     * @param prefNode
     * @return @throws
     *         NodeDoesNotExistException
     */
    protected Node getNode( Preferences prefNode ) throws NodeDoesNotExistException
    {
        Node nodeObj;
        if (prefNode.isUserNode())
        {
            nodeObj = prefsProvider.getNode(prefNode.absolutePath(), USER_NODE_TYPE);
        }
        else
        {
            nodeObj = prefsProvider.getNode(prefNode.absolutePath(), SYSTEM_NODE_TYPE);
        }
        return nodeObj;
    }
}