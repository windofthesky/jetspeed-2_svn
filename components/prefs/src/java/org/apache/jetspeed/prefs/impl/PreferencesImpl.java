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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.components.ComponentManager;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.impl.DefaultPersistenceStoreContainer;
import org.apache.jetspeed.prefs.PreferencesProvider;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.prefs.om.PropertyKey;
import org.apache.jetspeed.prefs.om.impl.NodeImpl;
import org.apache.jetspeed.prefs.om.impl.PropertyImpl;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>{@link Preferences} implementation relying on Jetspeed
 * OJB based persistence plugin.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PreferencesImpl extends AbstractPreferences
{

    /** User <tt>Preferences<tt> node type. */
    private static final short USER_NODE_TYPE = 0;

    /** System <tt>Preferences</tt> node type. */
    private static final short SYSTEM_NODE_TYPE = 1;

    /** The component manager. */
    private ComponentManager cm;

    /** The persistence store container. */
    private PersistenceStoreContainer storeContainer;

    /** The store name. */
    private String jetspeedStoreName;

    /** Common queries. **/
    private CommonQueries commonQueries;

    /** BackingStore availability flag. */
    private boolean isBackingStoreAvailable = true;

    /** The current node id. */
    private int nodeId = -1;

    /** The current <code>Node</code> object. */
    private Node node = null;

    /** The current node type. */
    private short nodeType = -1;

    /** Logger. */
    private static final Log log = LogFactory.getLog(PreferencesImpl.class);

    // Constants used to interpret returns of functions.
    private static final int ARRAY_SIZE = 4;
    private static final int NODE_ID = 0;
    private static final int NODE = 1;
    private static final int ERROR_CODE = 2;
    private static final int DISPOSITION = 3;
    private static final int DISPOSITION_NEW_NODE = 0;
    private static final int DISPOSITION_EXISTING_NODE = 1;
    private static final int ERROR_SUCCESS = 0;
    private static final int ERROR_NODE_NOT_FOUND = 1;
    private static final int ERROR_NODE_ALREADY_EXISTS = 2;
    private static final int ERROR_NODE_CREATION_FAILED = 3;
    private static final int ERROR_PARENT_NOT_FOUND = 4;

    /** User root node. */
    static Preferences userRoot = new PreferencesImpl(null, "", USER_NODE_TYPE);

    /** System root node. */
    static Preferences systemRoot = new PreferencesImpl(null, "", SYSTEM_NODE_TYPE);

    /**
     * <p>Constructs a root node in the underlying
     * datastore if they have not yet been created.</p>
     * <p>Logs a warning if the underlying datastore is
     * unavailable.</p>
     * @param parent The parent object.
     * @param nodeName The node name.
     * @param nodeType The node type.
     */
    public PreferencesImpl(PreferencesImpl parent, String nodeName, short nodeType)
    {
        super(parent, nodeName);

        if (log.isDebugEnabled())
            log.debug("Constructing node: " + nodeName);
        PreferencesProvider prefProvider = PreferencesProviderImpl.prefProvider;
        this.storeContainer = prefProvider.getStoreContainer();
        this.jetspeedStoreName = prefProvider.getStoreKeyName();
        this.commonQueries = new CommonQueries(storeContainer, jetspeedStoreName);

        this.nodeType = nodeType;
        int[] result = createPrefNode(parent, nodeName, nodeType, this.absolutePath());
        if (result[ERROR_CODE] != ERROR_SUCCESS)
        {
            String warning =
                "Could not create node " + nodeName + " of type " + nodeType + ". Returned error code " + result[ERROR_CODE];
            if (log.isWarnEnabled())
            {
                log.warn(warning);
            }
            // Backing store is not available.
            isBackingStoreAvailable = false;
            return;
        }
        // Check if a new node.
        newNode = (result[DISPOSITION] == DISPOSITION_NEW_NODE);

    }

    /**
     * <p>Create a new preference node in the backing store.</p>
     * @param parent The parent node.
     * @param nodeName The node name.
     * @param nodeType The node type.
     * @param fullPath The node full path.
     * @return The operation status code.
     */
    private int[] createPrefNode(PreferencesImpl parent, String nodeName, short nodeType, String fullPath)
    {
        int[] result = new int[ARRAY_SIZE];
        Integer parentNodeId = null;

        if (null != parent)
        {
            if (log.isDebugEnabled())
                log.debug("Current node parent: " + parent.nodeId);
            // Get child node
            Object[] nodeFromParentRetrievalResult = getChildNode(new Integer(parent.nodeId), nodeName, new Short(nodeType));
            if (((Integer) nodeFromParentRetrievalResult[ERROR_CODE]).intValue() == ERROR_SUCCESS)
            {
                result[NODE_ID] = ((Integer) nodeFromParentRetrievalResult[NODE_ID]).intValue();
                result[ERROR_CODE] = ERROR_SUCCESS;
                result[DISPOSITION] = DISPOSITION_EXISTING_NODE;
                return result;
            }
            else
            {
                parentNodeId = new Integer(parent.nodeId);
            }

        }
        // Check if node exists.
        Object[] nodeRetrievalResult = getNode(fullPath, nodeType);
        if (((Integer) nodeRetrievalResult[ERROR_CODE]).intValue() == ERROR_SUCCESS)
        {
            result[NODE_ID] = ((Integer) nodeRetrievalResult[NODE_ID]).intValue();
            result[ERROR_CODE] = ERROR_SUCCESS;
            result[DISPOSITION] = DISPOSITION_EXISTING_NODE;
            return result;
        }

        // If does not exist, create.
        Node nodeObj = new NodeImpl(parentNodeId, nodeName, nodeType, fullPath);
        if (log.isDebugEnabled())
            log.debug("New node: " + nodeObj.toString());
        PersistenceStore store = getPersistenceStore();
        try
        {
            store.lockForWrite(nodeObj);
            store.getTransaction().checkpoint();

            result[NODE_ID] = nodeObj.getNodeId();
            result[ERROR_CODE] = ERROR_SUCCESS;
            result[DISPOSITION] = DISPOSITION_NEW_NODE;

            this.nodeId = nodeObj.getNodeId();
            this.node = nodeObj;
        }
        catch (Exception e)
        {
            String msg = "Unable to store Node.";
            log.error(msg, e);
            store.getTransaction().rollback();

            result[ERROR_CODE] = ERROR_NODE_CREATION_FAILED;
        }

        return result;
    }

    /**
     * <p>Get the node id from the full path.</p>
     * @param fullPath The full path.
     * @param nodeType The node type.
     * @return An array of value returned including:
     *         <ul>
     *          <li>At index NODE_ID: The node id.</li>
     *          <li>At index NODE: The node object.</li>
     *          <li>At index ERROR_CODE: The error code.</li>
     *         </ul>
     */
    private Object[] getNode(String fullPath, short nodeType)
    {
        Object[] result = new Object[ARRAY_SIZE];
        if (log.isDebugEnabled())
            log.debug("Getting node: [[nodeId, " + this.nodeId + "], [fullPath, " + fullPath + "], [nodeType, " + nodeType + "]]");

        if (this.nodeId != -1 && (null != this.node))
        {
            result[NODE_ID] = new Integer(this.nodeId);
            result[NODE] = this.node;
            result[ERROR_CODE] = new Integer(ERROR_SUCCESS);
            return result;
        }

        PersistenceStore store = getPersistenceStore();
        Node nodeObj = (Node) store.getObjectByQuery(commonQueries.newNodeQueryByPathAndType(fullPath, new Short(nodeType)));
        if (null != nodeObj)
        {
            result[NODE_ID] = new Integer(nodeObj.getNodeId());
            result[NODE] = nodeObj;
            result[ERROR_CODE] = new Integer(ERROR_SUCCESS);
            if (log.isDebugEnabled())
                log.debug("Found node: " + nodeObj.getFullPath());
            this.node = nodeObj;
            this.nodeId = nodeObj.getNodeId();
            return result;
        }
        else
        {
            result[ERROR_CODE] = new Integer(ERROR_NODE_NOT_FOUND);
            return result;
        }
    }

    /**
     * <p>Get the child node from the parent node.</p>
     * @param parentIdObject The parent node id.
     * @return An array of value returned including:
     *         <ul>
     *          <li>At index NODE_ID: The node id.</li>
     *          <li>At index NODE: The node object.</li>
     *          <li>At index ERROR_CODE: The error code.</li>
     *         </ul>
     */
    private Object[] getChildNode(Integer parentIdObject, String nodeName, Short nodeType)
    {
        Object[] result = new Object[ARRAY_SIZE];
        PersistenceStore store = getPersistenceStore();
        Node nodeObj =
            (Node) store.getObjectByQuery(commonQueries.newNodeQueryByParentIdNameAndType(parentIdObject, nodeName, nodeType));
        if (null != nodeObj)
        {
            result[NODE_ID] = new Integer(nodeObj.getNodeId());
            result[NODE] = nodeObj;
            result[ERROR_CODE] = new Integer(ERROR_SUCCESS);
            if (log.isDebugEnabled())
                log.debug("Found child node: " + nodeObj.getFullPath());
            this.nodeId = nodeObj.getNodeId();
            this.node = nodeObj;
            return result;
        }
        else
        {
            result[ERROR_CODE] = new Integer(ERROR_NODE_NOT_FOUND);
            return result;
        }
    }

    /**
     * @see java.util.prefs.Preferences#childrenNamesSpi()
     */
    public String[] childrenNamesSpi() throws BackingStoreException
    {
        Object[] parentResult = getNode(this.absolutePath(), this.nodeType);

        if (((Integer) parentResult[ERROR_CODE]).intValue() != ERROR_SUCCESS)
        {
            String warning = "Could not get node id. Returned error code " + parentResult[ERROR_CODE];
            if (log.isWarnEnabled())
            {
                log.warn(warning);
            }
            throw new BackingStoreException(warning);
        }

        PersistenceStore store = getPersistenceStore();
        Collection nodes = store.getCollectionByQuery(commonQueries.newNodeQueryByParentId(parentResult[NODE_ID]));
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
            // The returned array is of size zero if this node has no preferences.
            return new String[0];
        }
    }

    /**
     * @see java.util.prefs.Preferences#childSpi(java.lang.String)
     */
    public AbstractPreferences childSpi(String name)
    {
        return new PreferencesImpl(this, name, this.nodeType);
    }

    /**
     * @see java.util.prefs.Preferences#flushSpi()
     */
    public void flushSpi() throws BackingStoreException
    {
        // Never used. Not implemented.
    }

    /**
     * @see java.util.prefs.Preferences#getSpi(java.lang.String)
     */
    public String getSpi(String key)
    {
        String value = null;
        Object[] nodeResult = getNode(this.absolutePath(), this.nodeType);

        if (((Integer) nodeResult[ERROR_CODE]).intValue() != ERROR_SUCCESS)
        {
            log.error("Could not get node id. Returned error code " + nodeResult[ERROR_CODE]);
            return value;
        }

        // Get the property set def.
        Node nodeObj = (Node) nodeResult[NODE];
        Collection properties = nodeObj.getNodeProperties();
        if (log.isDebugEnabled())
            log.debug("Node properties: " + properties.size());
        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();
            if (curProp.getPropertyKey().getPropertyKeyName().equals(key))
            {
                value = curProp.getPropertyValue(curProp.getPropertyKey().getPropertyKeyType());
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

        PersistenceStore store = getPersistenceStore();
        Node nodeObj = (Node) store.getObjectByQuery(commonQueries.newNodeQueryById(new Integer(this.nodeId)));
        if (log.isDebugEnabled())
            log.debug("Fetching keys for node: " + nodeObj.toString());
        if (null != nodeObj)
        {
            Collection propCol = nodeObj.getNodeProperties();
            if ((null != propCol) && propCol.size() > 0)
            {
                for (Iterator j = propCol.iterator(); j.hasNext();)
                {
                    Property curprop = (Property) j.next();
                    propertyNames.add(curprop.getPropertyKey().getPropertyKeyName());
                }
            }
            else
            {
                log.error("Could not retrieve property values for node " + nodeObj.getFullPath());
            }
        }
        return (String[]) propertyNames.toArray(new String[0]);
    }

    /**
     * @see java.util.prefs.Preferences#putSpi(java.lang.String, java.lang.String)
     * <p>In addition to java.util.prefs.Preferences, this implementation
     * is enforcing that node used as property sets have been defined
     * as such and that only the keys defined associated to the property
     * set can be added as properties of the current node.</p> 
     */
    public void putSpi(String key, String value)
    {
        Object[] nodeResult = getNode(this.absolutePath(), this.nodeType);

        if (((Integer) nodeResult[ERROR_CODE]).intValue() != ERROR_SUCCESS)
        {
            log.error("Could not get node id. Returned error code " + nodeResult[ERROR_CODE]);
            return;
        }

        // Get the property set def.
        Node nodeObj = (Node) nodeResult[NODE];
        Collection nodeKeys = nodeObj.getNodeKeys();
        Collection properties = nodeObj.getNodeProperties();
        ArrayList newProperties = new ArrayList(properties.size() + 1);
        boolean foundProp = false;
        boolean foundKey = false;
        // First if the property exists, update its value.
        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();
            if (curProp.getPropertyKey().getPropertyKeyName().equals(key))
            {
                foundProp = true;
                foundKey = true;
                if (log.isDebugEnabled())
                    log.debug("Update existing property: [" + key + ", " + value + "]");

                curProp.setPropertyValue(curProp.getPropertyKey().getPropertyKeyType(), value);
                curProp.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            }
            newProperties.add(curProp);
        }
        // The property does not already exist.  Create a new property, if
        // the property key exits and is associated to this node.
        if (!foundProp)
        {
            for (Iterator i = nodeKeys.iterator(); i.hasNext();)
            {
                PropertyKey curpk = (PropertyKey) i.next();
                if (curpk.getPropertyKeyName().equals(key))
                {
                    foundKey = true;
                    if (log.isDebugEnabled())
                        log.debug("New property value: [" + key + ", " + value + "]");

                    newProperties.add(
                        new PropertyImpl(nodeObj.getNodeId(), curpk.getPropertyKeyId(), curpk, curpk.getPropertyKeyType(), value));
                }
            }
        }
        if (!foundKey)
        {
            if (log.isWarnEnabled())
                log.warn(PropertyException.PROPERTYKEY_NOT_FOUND);
            return;
        }
        // Update node.
        PersistenceStore store = getPersistenceStore();
        if (log.isDebugEnabled())
            log.debug("Updated properties: " + newProperties.size());
        // What going on:
        if (newProperties.size() > 0)
            log.debug("Properties: " + ((Property) newProperties.get(0)).toString());

        try
        {
            store.lockForWrite(nodeObj);
            nodeObj.setNodeProperties(newProperties);
            nodeObj.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            if (log.isDebugEnabled())
                log.debug("Node for update: " + nodeObj.toString());
            store.getTransaction().checkpoint();
        }
        catch (Exception e)
        {
            String msg = "Unable to update Node.";
            log.error(msg, e);
            store.getTransaction().rollback();
        }

    }

    /**
     * @see java.util.prefs.Preferences#removeNodeSpi()
     */
    public void removeNodeSpi() throws BackingStoreException
    {
        if (log.isDebugEnabled())
            log.debug("Attempting to remove node: " + this.absolutePath());
        Object[] nodeResult = getNode(this.absolutePath(), this.nodeType);

        if (((Integer) nodeResult[ERROR_CODE]).intValue() != ERROR_SUCCESS)
        {
            String warning = "Could not get node id. Returned error code " + nodeResult[ERROR_CODE];
            if (log.isWarnEnabled())
            {
                log.warn(warning);
            }
            throw new BackingStoreException(warning);
        }
        PersistenceStore store = getPersistenceStore();
        try
        {
            Node nodeObj = (Node) nodeResult[NODE];
            if (log.isDebugEnabled())
                log.debug("Remove node: " + nodeObj.getNodeName());
            store.deletePersistent(nodeObj);
            store.getTransaction().checkpoint();
        }
        catch (Exception e)
        {
            String msg = "Unable to remove Node.";
            log.error(msg, e);
            store.getTransaction().rollback();
        }
    }

    /**
     * @see java.util.prefs.Preferences#removeSpi(java.lang.String)
     */
    public void removeSpi(String key)
    {
        Object[] nodeResult = getNode(this.absolutePath(), this.nodeType);

        if (((Integer) nodeResult[ERROR_CODE]).intValue() != ERROR_SUCCESS)
        {
            log.error("Could not get node id. Returned error code " + nodeResult[ERROR_CODE]);
            return;
        }

        // Get the property set def.
        Node nodeObj = (Node) nodeResult[NODE];
        Collection properties = nodeObj.getNodeProperties();
        ArrayList newProperties = new ArrayList(properties.size());
        for (Iterator i = properties.iterator(); i.hasNext();)
        {
            Property curProp = (Property) i.next();
            if (!(curProp.getPropertyKey().getPropertyKeyName().equals(key)))
            {
                newProperties.add(curProp);
            }
        }
        // Update node.
        PersistenceStore store = getPersistenceStore();
        try
        {
            store.lockForWrite(nodeObj);
            nodeObj.setNodeProperties(newProperties);
            nodeObj.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            store.getTransaction().checkpoint();
        }
        catch (Exception e)
        {
            String msg = "Unable to update Node.";
            log.error(msg, e);
            store.getTransaction().rollback();
        }
    }

    /**
     * @see java.util.prefs.Preferences#syncSpi()
     */
    public void syncSpi() throws BackingStoreException
    {
        // Never used. Not implemented.
    }

    /**
     * <p>Utility method to get the persistence store and initiate
     * the transaction if not open.</p>
     * @return The persistence store.
     */
    protected PersistenceStore getPersistenceStore()
    {
        PersistenceStore store = storeContainer.getStoreForThread(jetspeedStoreName);
        if (!store.getTransaction().isOpen())
        {
            store.getTransaction().begin();
        }
        return store;
    }

}
