/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.spi.services.prefs.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.persistence.TransactionStateException;
import org.apache.jetspeed.spi.om.prefs.Node;
import org.apache.jetspeed.spi.om.prefs.Property;
import org.apache.jetspeed.spi.om.prefs.PropertyKey;
import org.apache.jetspeed.spi.om.prefs.impl.NodeImpl;
import org.apache.jetspeed.spi.om.prefs.impl.PropertyImpl;
import org.apache.jetspeed.spi.om.prefs.impl.PropertyKeyImpl;
import org.apache.jetspeed.spi.services.prefs.PropertyManagerService;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>{@link Preferences} implementation relying on Jetspeed
 * OJB based persistence plugin.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PreferencesImpl extends AbstractPreferences
{
    /**
     * <p>Persistence pluging service.</p>
     */
    private PersistencePlugin plugin = null;
    private PersistenceService ps = null;

    /**
     * <p>The preferences property manager.</p>
     */
    private PropertyManagerService pms = null;

    /**
     * <p>BackingStore availability flag.</p>
     */
    private boolean isBackingStoreAvailable = true;

    /**
     * <p>The current node id.</p>
     */
    private int nodeId = -1;

    /**
     * <p>The current <code>Node</code> object.</p>
     */
    private Node node = null;

    /**
     * <p>The current node parent node id.</p>
     */
    private int parentNodeId = -1;

    /**
     * <p>The current node type.</p>
     */
    private short nodeType = -1;

    /**
     * <p>Logger</p>
     */
    private static final Log log = LogFactory.getLog(PreferencesImpl.class);

    /**
     * <p>Constants used to interpret returns of functions.</p>
     */
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

    /** 
     * <p>User <tt>Preferences<tt> node type.<p>        
     */
    private static final short USER_NODE_TYPE = 0;

    /** 
     * System <tt>Preferences</tt> node type.         
     */
    private static final short SYSTEM_NODE_TYPE = 1;

    /** 
     * User root node.
     */
    static final Preferences userRoot = new PreferencesImpl(null, "", USER_NODE_TYPE);

    /** 
     * System root node.
     */
    static final Preferences systemRoot = new PreferencesImpl(null, "", SYSTEM_NODE_TYPE);

    /**
     * <p>Returns the {@link PersistenceService}.</p>
     * TODO This should be improved.
     */
    protected void getPersistenceService()
    {
        if (ps == null)
        {
            ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
        }
        if (plugin == null)
        {
            plugin = ps.getPersistencePlugin(ps.getConfiguration().getString("persistence.plugin.name", "jetspeed"));

        }
    }

    /**
     * <p>Returns the {@link PropertyManagerService}.</p>
     * @return The PropertyManagerService.
     */
    protected PropertyManagerService getPropertyManagerService()
    {
        if (pms == null)
        {
            pms = (PropertyManagerService) CommonPortletServices.getPortalService(PropertyManagerService.SERVICE_NAME);
        }
        return pms;
    }

    /**
     * <p>Constructs a root node in the underlying
     * datastore if they have not yet been created.</p>
     * <p>Logs a warning if the underlying datastore is
     * unavailable.</p>
     * @param parent The parent object.
     * @param nodeName The node name.
     * @param nodeType The node type.
     */
    private PreferencesImpl(PreferencesImpl parent, String nodeName, short nodeType)
    {
        super(parent, nodeName);
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

        // Get persistence service.
        getPersistenceService();

        // If does not exist, create.
        Node nodeObj = new NodeImpl(parentNodeId, null, nodeName, nodeType, fullPath);
        try
        {
            plugin.beginTransaction();
            plugin.prepareForUpdate(nodeObj);
            plugin.commitTransaction();

            result[NODE_ID] = nodeObj.getNodeId();
            result[ERROR_CODE] = ERROR_SUCCESS;
            result[DISPOSITION] = DISPOSITION_NEW_NODE;
        }
        catch (TransactionStateException e)
        {
            try
            {
                plugin.rollbackTransaction();
            }
            catch (TransactionStateException e1)
            {
                log.error("Failed to rollback transaction.", e);
            }
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

        //Get persistence service.
        getPersistenceService();
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("fullPath", parent.absolutePath());
        c.addEqualTo("nodeType", new Short(nodeType));
        Object query = plugin.generateQuery(NodeImpl.class, c);
        Node nodeObj = (Node) plugin.getObjectByQuery(NodeImpl.class, query);

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

        //Get persistence service.
        getPersistenceService();
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("fullPath", fullPath);
        c.addEqualTo("nodeType", new Short(nodeType));
        Object query = plugin.generateQuery(NodeImpl.class, c);
        Node nodeObj = (Node) plugin.getObjectByQuery(NodeImpl.class, query);

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
        // Get persistence service.
        getPersistenceService();
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

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("parentNodeId", parentResult[NODE_ID]);
        Object query = plugin.generateQuery(NodeImpl.class, c);
        Collection nodes = plugin.getCollectionByQuery(NodeImpl.class, query);

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
        // Get persistence service.
        getPersistenceService();

        String value = null;
        PropertyKey propKey = getPropertyKeyByName(key.toLowerCase());
        if (null != propKey)
        {
            LookupCriteria c = plugin.newLookupCriteria();
            c.addEqualTo("propertyKeyId", new Integer(propKey.getPropertyKeyId()));
            Object query = plugin.generateQuery(PropertyImpl.class, c);
            Property prop = (Property) plugin.getObjectByQuery(PropertyImpl.class, query);
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

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("nodeId", new Integer(this.nodeId));
        Object query = plugin.generateQuery(NodeImpl.class, c);
        Node nodeObj = (Node) plugin.getObjectByQuery(NodeImpl.class, query);

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
        PropertyManagerService pms = getPropertyManagerService();
        int propertySetDefId = 0;
        Object[] nodeResult = getNode(this.absolutePath(), this.nodeType);

        if (((Integer) nodeResult[ERROR_CODE]).intValue() != ERROR_SUCCESS)
        {
            log.error("Could not get node id. Returned error code " + nodeResult[ERROR_CODE]);
            return;
        }

        // Check that node name is a property set.
        try
        {
            propertySetDefId = pms.getPropertySetDefIdByType(this.name().toLowerCase(), this.nodeType);
        }
        catch (PropertyException pe)
        {
            log.error(PropertyException.PROPERTYSET_DEFINITION_NOT_FOUND);
            return;
        }
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
        // Get persistence service.
        getPersistenceService();

        // Check that the node has been associated to a property set definition.
        Integer nodePropSetDefId = nodeObj.getPropertySetDefId();
        if (null == nodePropSetDefId)
        {
            // Associate the node to the property set definition.
            nodeObj.setPropertySetDefId(propertySetDefId);
            try
            {
                plugin.beginTransaction();
                plugin.prepareForUpdate(nodeObj);
                plugin.commitTransaction();
            }
            catch (TransactionStateException e)
            {
                try
                {
                    plugin.rollbackTransaction();
                }
                catch (TransactionStateException e1)
                {
                    log.error("Failed to rollback transaction.", e);
                }
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
            plugin.beginTransaction();
            plugin.prepareForUpdate(prop);
            plugin.commitTransaction();
        }
        catch (TransactionStateException e)
        {
            try
            {
                plugin.rollbackTransaction();
            }
            catch (TransactionStateException e1)
            {
                log.error("Failed to rollback transaction.", e);
            }
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

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("propertyKeyId", propertyKeyIdObject);
        Object query = plugin.generateQuery(PropertyImpl.class, c);
        Property prop = (Property) plugin.getObjectByQuery(PropertyImpl.class, query);

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

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("propertyKeyName", key.toLowerCase());
        Object query = plugin.generateQuery(PropertyKeyImpl.class, c);
        PropertyKey propKey = (PropertyKey) plugin.getObjectByQuery(PropertyKeyImpl.class, query);

        return propKey;
    }

    /**
     * @see java.util.prefs.Preferences#removeNodeSpi()
     */
    public void removeNodeSpi() throws BackingStoreException
    {
        // Get persistence service.
        getPersistenceService();
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
            plugin.beginTransaction();
            plugin.prepareForDelete(nodeResult[NODE]);
            plugin.commitTransaction();
        }
        catch (TransactionStateException e)
        {
            try
            {
                plugin.rollbackTransaction();
            }
            catch (TransactionStateException e1)
            {
                log.error("Failed to rollback transaction.", e);
            }
        }
    }

    /**
     * @see java.util.prefs.Preferences#removeSpi(java.lang.String)
     */
    public void removeSpi(String key)
    {
        // Get persistence service.
        getPersistenceService();

        Object[] nodeResult = getNode(this.absolutePath(), this.nodeType);

        if (((Integer) nodeResult[ERROR_CODE]).intValue() == ERROR_SUCCESS)
        {
            PropertyKey propKey = getPropertyKeyByName(key);
            if (null != propKey)
            {
                LookupCriteria c = plugin.newLookupCriteria();
                c.addEqualTo("nodeId", new Integer(((Node) nodeResult[NODE]).getNodeId()));
                c.addEqualTo("propertyKeyId", new Integer(propKey.getPropertyKeyId()));
                Object query = plugin.generateQuery(PropertyImpl.class, c);
                plugin.deleteByQuery(query);
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

}
