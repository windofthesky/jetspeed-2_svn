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
import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;
import org.apache.jetspeed.components.util.system.ClassLoaderSystemResourceUtilImpl;
import org.apache.jetspeed.components.util.system.SystemResourceUtil;
import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.Property;
import org.apache.jetspeed.prefs.om.PropertyKey;
import org.apache.jetspeed.prefs.om.impl.NodeImpl;
import org.apache.jetspeed.prefs.om.impl.PropertyImpl;
import org.apache.jetspeed.prefs.om.impl.PropertyKeyImpl;
import org.apache.jetspeed.prefs.PropertyManager;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>{@link Preferences} implementation relying on Jetspeed
 * OJB based persistence plugin.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PreferencesImpl extends AbstractPreferences
{
    /** Preferences assembly script. */
    private static String PREFS_CONTAINER_GROOVY = "org/apache/jetspeed/containers/prefs.container.groovy";
 
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

    /** The property manager. */
    private PropertyManager pms;

    /** BackingStore availability flag. */
    private boolean isBackingStoreAvailable = true;

    /** The current node id. */
    private int nodeId = -1;

    /** The current <code>Node</code> object. */
    private Node node = null;

    /** The current node parent node id. */
    private int parentNodeId = -1;

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

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        SystemResourceUtil sysRes = new ClassLoaderSystemResourceUtilImpl(cl);
        InputStream is = cl.getResourceAsStream(PREFS_CONTAINER_GROOVY);
        
        Reader scriptReader = new InputStreamReader(is);
        try
        {
            cm = new ComponentManager(scriptReader, ComponentManager.GROOVY);
            Class containerClass = Class.forName("org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer");
            this.storeContainer = (PersistenceStoreContainer) cm.getComponent(containerClass);
            Class propertyMgrClass = Class.forName("org.apache.jetspeed.prefs.PropertyManager");
            this.pms = (PropertyManager) cm.getComponent(propertyMgrClass);
            // TODO We should get the store name from assembly
            this.jetspeedStoreName = "jetspeed";
        }
        catch (ClassNotFoundException cnfe)
        {
            if(log.isErrorEnabled()) log.error("ClassNotFoundException: " + cnfe);
        }
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
            // Get parent node id.
            int[] parentRetrievalResult = getParentNodeId(parent, nodeType);
            if (parentRetrievalResult[ERROR_CODE] != ERROR_SUCCESS)
            {
                result[ERROR_CODE] = parentRetrievalResult[ERROR_CODE];
                return result;
            }
            parentNodeId = new Integer(parentRetrievalResult[NODE_ID]);

        }
        // Check if node exists.
        Object[] nodeRetrievalResult = getNode(this.absolutePath(), nodeType);
        if (((Integer) nodeRetrievalResult[ERROR_CODE]).intValue() == ERROR_SUCCESS)
        {
            result[NODE_ID] = ((Integer) nodeRetrievalResult[NODE_ID]).intValue();
            result[ERROR_CODE] = ERROR_SUCCESS;
            result[DISPOSITION] = DISPOSITION_EXISTING_NODE;

            return result;
        }

        // If does not exist, create.
        Node nodeObj = new NodeImpl(parentNodeId, null, nodeName, nodeType, fullPath);
        PersistenceStore store = getPersistenceStore();
        try
        {
            store.lockForWrite(nodeObj);
            store.getTransaction().checkpoint();

            result[NODE_ID] = nodeObj.getNodeId();
            result[ERROR_CODE] = ERROR_SUCCESS;
            result[DISPOSITION] = DISPOSITION_NEW_NODE;
        }
        catch (LockFailedException lfe)
        {
            result[ERROR_CODE] = ERROR_NODE_CREATION_FAILED;
        }

        return result;
    }

    /**
     * <p>Get the parent node id from the parent object.</p>
     * @param parent The parent.
     * @param nodeType The node type.
     * @return An array of value returned including:
     *         <ul>
     *          <li>At index NODE_ID: The parent node id.</li>
     *          <li>At index ERROR_CODE: The error code.</li>
     *         </ul>
     */
    private int[] getParentNodeId(PreferencesImpl parent, short nodeType)
    {
        int[] result = new int[ARRAY_SIZE];

        if (this.parentNodeId != -1)
        {
            result[NODE_ID] = this.parentNodeId;
            result[ERROR_CODE] = ERROR_SUCCESS;
            return result;
        }

        PersistenceStore store = getPersistenceStore();
        Node nodeObj =
            (Node) store.getObjectByQuery(commonQueries.newNodeQueryByPathAndType(parent.absolutePath(), new Short(nodeType)));
        if (null != nodeObj)
        {
            result[NODE_ID] = nodeObj.getNodeId();
            result[ERROR_CODE] = ERROR_SUCCESS;
            return result;
        }
        else
        {
            result[ERROR_CODE] = ERROR_PARENT_NOT_FOUND;
            return result;
        }
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
        PropertyKey propKey = getPropertyKeyByName(key.toLowerCase());
        if (null != propKey)
        {
            PersistenceStore store = getPersistenceStore();
            Property prop =
                (Property) store.getObjectByQuery(commonQueries.newPropertyQueryById(new Integer(propKey.getPropertyKeyId())));
            if (null != prop)
            {
                value = prop.getPropertyValue(propKey.getPropertyKeyType());
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
        if (null != nodeObj)
        {
            Collection propCol = nodeObj.getProperties();
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
        int propertySetDefId = 0;
        Object[] nodeResult = getNode(this.absolutePath(), this.nodeType);

        if (((Integer) nodeResult[ERROR_CODE]).intValue() != ERROR_SUCCESS)
        {
            log.error("Could not get node id. Returned error code " + nodeResult[ERROR_CODE]);
            return;
        }

        // Check that node name is a property set.
        //        try
        //        {
        //            // TODO This broke
        //            propertySetDefId = pms.getPropertySetDefIdByType(this.name().toLowerCase(), this.nodeType);
        //        }
        //        catch (PropertyException pe)
        //        {
        //            log.error(PropertyException.PROPERTYSET_DEFINITION_NOT_FOUND);
        //            return;
        //        }
        // The property set exists. Add the property key/value if defined.
        PropertyKey propKey = getPropertyKeyByName(key);
        if (null != propKey)
        {
            addProperty((Node) nodeResult[NODE], new Integer(propertySetDefId), propKey, value);
        }
        else
        {
            log.error(PropertyException.PROPERTYKEY_NOT_FOUND);
        }
    }

    /**
     * <p>Add property key/value pair to a property set node.</p>
     * @param nodeObj The node object.
     * @param propertySetDefId The property set definition id.
     * @param propKey The property key id.
     * @param value The property value.
     */
    private void addProperty(Node nodeObj, Integer propertySetDefId, PropertyKey propKey, String value)
    {
        PersistenceStore store = getPersistenceStore();
        // Check that the node has been associated to a property set definition.
        Integer nodePropSetDefId = nodeObj.getPropertySetDefId();
        if (null == nodePropSetDefId)
        {
            // TODO Should be able to add property directly to node.
            // Associate the node to the property set definition.
            nodeObj.setPropertySetDefId(propertySetDefId);
            try
            {
                store.lockForWrite(nodeObj);
                store.getTransaction().checkpoint();
            }
            catch (LockFailedException lfe)
            {
                log.error("Unable to lock Node for update: " + lfe.toString());
            }
        }

        // Check if the property value already exists.
        Property prop = getPropertyById(propKey.getPropertyKeyId());
        // If the property does not exist, create it.
        if (null == prop)
        {
            prop = new PropertyImpl(propKey.getPropertyKeyId(), nodeObj.getNodeId(), propKey.getPropertyKeyType(), value);
        }
        else
        {
            prop.setPropertyValue(propKey.getPropertyKeyType(), value);
        }

        // Update the property.
        try
        {
            store.lockForWrite(prop);
            store.getTransaction().checkpoint();
        }
        catch (LockFailedException lfe)
        {
            log.error("Unable to lock Property for update: " + lfe.toString());
        }
    }

    /**
     * <p>Get property by id.</p>
     * @param propertyKeyId The property key id.
     * @return The property.
     */
    private Property getPropertyById(int propertyKeyId)
    {
        Integer propertyKeyIdObject = new Integer(propertyKeyId);

        ArgUtil.notNull(new Object[] { propertyKeyIdObject }, new String[] { "propertyKeyId" }, "getPropertyById(int)");

        PersistenceStore store = getPersistenceStore();
        Property prop = (Property) store.getObjectByQuery(commonQueries.newPropertyQueryById(propertyKeyIdObject));
        return prop;
    }

    /**
     * <p>Get property key by id.</p>
     * @param key The property key name.
     * @return The property key.
     */
    private PropertyKey getPropertyKeyByName(String key)
    {
        ArgUtil.notNull(new Object[] { key }, new String[] { "propertyKeyName" }, "getPropertyKeyByName(java.lang.String)");

        PersistenceStore store = getPersistenceStore();
        PropertyKey propKey = (PropertyKey) store.getObjectByQuery(commonQueries.newPropertyKeyQueryByName(key.toLowerCase()));
        return propKey;
    }

    /**
     * @see java.util.prefs.Preferences#removeNodeSpi()
     */
    public void removeNodeSpi() throws BackingStoreException
    {
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

        // Delete the node.
        try
        {
            PersistenceStore store = getPersistenceStore();
            store.deletePersistent(nodeResult[NODE]);
        }
        catch (LockFailedException lfe)
        {
            throw new BackingStoreException("Unable to lock Node for deletion: " + lfe.toString());
        }
    }

    /**
     * @see java.util.prefs.Preferences#removeSpi(java.lang.String)
     */
    public void removeSpi(String key)
    {
        Object[] nodeResult = getNode(this.absolutePath(), this.nodeType);

        if (((Integer) nodeResult[ERROR_CODE]).intValue() == ERROR_SUCCESS)
        {
            PropertyKey propKey = getPropertyKeyByName(key);
            if (null != propKey)
            {
                try
                {
                    PersistenceStore store = getPersistenceStore();
                    store.deleteAll(
                        commonQueries.newPropertyQueryByNodeIdAndPropertyKeyId(
                            new Integer(((Node) nodeResult[NODE]).getNodeId()),
                            new Integer(propKey.getPropertyKeyId())));
                }
                catch (LockFailedException lfe)
                {
                    log.error("Unable to remove property keys: " + lfe.toString());
                }
            }
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
