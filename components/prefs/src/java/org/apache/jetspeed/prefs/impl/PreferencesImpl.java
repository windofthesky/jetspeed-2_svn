/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import java.util.Iterator;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.prefs.FailedToCreateNodeException;
import org.apache.jetspeed.prefs.NodeAlreadyExistsException;
import org.apache.jetspeed.prefs.NodeDoesNotExistException;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.prefs.om.impl.PropertyImpl;

/**
 * <p>
 * S {@link Preferences}implementation relying on Jetspeed OJB based
 * persistence plugin.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 *  @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 */
public class PreferencesImpl extends AbstractPreferences
{

    /** User <tt>Preferences<tt> node type. */
    public static final int USER_NODE_TYPE = 0;

    /** System <tt>Preferences</tt> node type. */
    public static final int SYSTEM_NODE_TYPE = 1;

    /** Logger. */
    private static final Log log = LogFactory.getLog(PreferencesImpl.class);

    PreferencesProviderWrapper ppw;

    String nodeName;
    
    int nodeType;
    
    /**
     * <p>
     * Constructs a root node in the underlying datastore if they have not yet
     * been created.
     * </p>
     * <p>
     * Logs a warning if the underlying datastore is unavailable.
     * </p>
     * 
     * @param parent The parent object.
     * @param nodeName The node name.
     * @param nodeType The node type.
     */
    PreferencesImpl(PreferencesImpl parent, PreferencesProviderWrapper ppw, String nodeName, int nodeType) throws IllegalStateException
    {
        super(parent, nodeName);
        this.ppw = ppw;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        // aggressively fetch/create node
        getNode();
    }        

    /**
     * @see java.util.prefs.Preferences#childrenNamesSpi()
     */
    public String[] childrenNamesSpi() throws BackingStoreException
    {
        Collection nodes = ppw.provider().getChildren(getNode());

        if (null != nodes)
        {
            ArrayList childrenNames = new ArrayList(nodes.size());
            for (Iterator i = nodes.iterator(); i.hasNext();)
            {
                Node curnode = (Node) i.next();
                childrenNames.add(curnode.getNodeName());
            }
            return (String[]) childrenNames.toArray(new String[0]);
        }
        else
        {
            // The returned array is of size zero if this node has no
            // preferences.
            return new String[0];
        }
    }

    /**
     * @see java.util.prefs.Preferences#childSpi(java.lang.String)
     */
    public AbstractPreferences childSpi(String name)
    {
        return new PreferencesImpl(this, ppw, name, nodeType);
    }

    /**
     * @see java.util.prefs.Preferences#flushSpi()
     */
    public void flushSpi() throws BackingStoreException
    {
    }

    /**
     * @see java.util.prefs.Preferences#getSpi(java.lang.String)
     */
    public String getSpi(String key)
    {
        String value = null;
        Collection properties = getNode().getNodeProperties();
        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();
            if ((null != curProp) && (null != curProp.getPropertyName()) && (curProp.getPropertyName().equals(key)))
            {
                value = curProp.getPropertyValue();
            }
        }
        return value;
    }

    /**
     * @see java.util.prefs.Preferences#keysSpi()
     */
    public String[] keysSpi()
    {
        ArrayList propertyNames = new ArrayList();

        Collection propCol = getNode().getNodeProperties();
        if ((null != propCol) && propCol.size() > 0)
        {
            for (Iterator j = propCol.iterator(); j.hasNext();)
            {
                Property curprop = (Property) j.next();
                if ((null != curprop) && (null != curprop.getPropertyName())
                        && !propertyNames.contains(curprop.getPropertyName()))
                {
                    propertyNames.add(curprop.getPropertyName());
                }
            }
        }

        return (String[]) propertyNames.toArray(new String[propertyNames.size()]);
    }

    /**
     * @see java.util.prefs.Preferences#putSpi(java.lang.String,
     *      java.lang.String)
     */
    public void putSpi(String key, String value)
    {
        Node node = getNode();
        Collection properties = node.getNodeProperties();
        if (null == properties)
        {
            log.error("Could not retrieve node property: [key: " + key + ", value:" + value + "]");
            return;
        }

        // If the property exists, update its value.
        boolean propFound = false;
        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();
            if ((null != curProp) && (null != curProp.getPropertyName()) && curProp.getPropertyName().equals(key))
            {
                propFound = true;
                curProp.setPropertyValue(value);
                curProp.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                if (log.isDebugEnabled())
                {
                    log.debug("Update existing property: " + curProp.toString());
                }
                // Property found, we break.
                break;
            }
        }
        if (!propFound)
        {
            properties.add(new PropertyImpl(node.getNodeId(), key, value));
        }

        ppw.provider().storeNode(node);

        // mark stored node as old
        newNode = false;
    }

    /**
     * @see java.util.prefs.Preferences#removeNodeSpi()
     */
    public void removeNodeSpi() throws BackingStoreException
    {
        // remove node from db
        Node parentNode = null;
        Preferences parent = parent();
        if (parent != null && parent instanceof PreferencesImpl)
        {
            parentNode = ((PreferencesImpl) parent).getNode();
        }
        ppw.provider().removeNode(parentNode, getNode());

        // mark removed node as new
        newNode = true;
    }

    /**
     * @see java.util.prefs.Preferences#removeSpi(java.lang.String)
     */
    public void removeSpi(String key)
    {
        boolean removed = false;
        Node node = getNode();
        Collection properties = node.getNodeProperties();
        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();

            if ((curProp.getPropertyName().equals(key)))
            {
                i.remove();
                removed = true;
            }
        }
        if (removed)
        {
            ppw.provider().storeNode(node);

            // mark stored node as old
            newNode = false;
        }
    }

    /**
     * @see java.util.prefs.Preferences#syncSpi()
     */
    public void syncSpi() throws BackingStoreException
    {
        flushSpi();
    }

    /**
     * 
     * <p>
     * getNode
     * </p>
     * 
     * @return
     */
    public Node getNode()
    {
        Node node;
        try
        {
            node = ppw.provider().getNode(absolutePath(), nodeType);
            newNode = false;
        }
        catch (NodeDoesNotExistException e1)
        {
            try
            {
                Preferences parent = parent();
                if ((parent != null) && (parent instanceof PreferencesImpl))
                {
                    node = ppw.provider().createNode(((PreferencesImpl)parent).getNode(), nodeName, nodeType, absolutePath());
                }
                else
                {
                    node = ppw.provider().createNode(null, nodeName, nodeType, absolutePath());
                }
                newNode = true;
            }
            catch (FailedToCreateNodeException e)
            {
                IllegalStateException ise = new IllegalStateException("Failed to create new Preferences of type " + nodeType + " for path " + absolutePath());
                ise.initCause(e);
                throw ise;
            }
            catch (NodeAlreadyExistsException e)
            {
                try
                {
                    node = ppw.provider().getNode(absolutePath(), nodeType);
                    newNode = false;
                }
                catch (NodeDoesNotExistException e2)
                {
                    // If we get this at this point something is very wrong
                    IllegalStateException ise = new IllegalStateException("Unable to create node for Preferences of type " + nodeType + " for path " + absolutePath() + ". " +
                                                                          "If you see this exception at this, it more than likely means that the Preferences backing store is corrupt.");
                    ise.initCause(e2);
                    throw ise;
                }
            }
        }
        return node;
    }
}