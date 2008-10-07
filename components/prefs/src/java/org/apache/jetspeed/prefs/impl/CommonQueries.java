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

import org.apache.jetspeed.prefs.om.impl.NodeImpl;
import org.apache.jetspeed.prefs.om.impl.PropertyImpl;
import org.apache.jetspeed.prefs.om.impl.PropertyKeyImpl;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.Filter;

/**
 * <p>Preferences implementation common queries.</p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 *
 */
public class CommonQueries
{

    /** The persistence store container. */
    private PersistenceStoreContainer storeContainer;

    /** The store name. */
    private String jetspeedStoreName;

    /**
     * <p>Constructor providing access to the persistence component.</p>
     */
    public CommonQueries(PersistenceStoreContainer storeContainer, String keyStoreName)
    {
        this.storeContainer = storeContainer;
        this.jetspeedStoreName = keyStoreName;
    }

    /**
     * <p>Utility method to create a new <code>PropertyKey</code>
     * by propertyKeyId query.</p>
     * @param propertyKeyIdObject The propertyKeyId object.
     * @return The new query.
     */
    Object newPropertyKeyQueryById(Object propertyKeyIdObject)
    {
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();
        filter.addEqualTo("propertyKeyId", propertyKeyIdObject);
        Object query = store.newQuery(PropertyKeyImpl.class, filter);
        return query;
    }

    /**
     * <p>Utility method to create a new <code>PropertyKey</code>
     * by propertyKeyName query.</p>
     * @param propertyKeyNameObject The propertyKeyName object.
     * @return The new query.
     */
    Object newPropertyKeyQueryByName(Object propertyKeyNameObject)
    {
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();
        filter.addEqualTo("propertyKeyName", propertyKeyNameObject);
        Object query = store.newQuery(PropertyKeyImpl.class, filter);
        return query;
    }

    /**
     * <p>Utility method to create a new <code>Property</code>
     * by propertyKeyId query.</p>
     * @param propertyKeyIdObject The propertyKeyId object.
     * @return The new query.
     */
    Object newPropertyQueryById(Object propertyKeyIdObject)
    {
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();
        filter.addEqualTo("propertyKeyId", propertyKeyIdObject);
        Object query = store.newQuery(PropertyImpl.class, filter);
        return query;
    }

    /**
     * <p>Utility method to create a new <code>Property</code>
     * by nodeId and propertyKeyId query.</p>
     * @param nodeIdObject The nodeId object.
     * @param propertyKeyIdObject The propertyKeyId object.
     * @return The new query.
     */
    Object newPropertyQueryByNodeIdAndPropertyKeyId(Object nodeIdObject, Object propertyKeyIdObject)
    {
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();
        filter.addEqualTo("nodeId", nodeIdObject);
        filter.addEqualTo("propertyKeyId", propertyKeyIdObject);
        Object query = store.newQuery(PropertyImpl.class, filter);
        return query;
    }

    /**
     * <p>Utility method to create a new <code>Node</p>
     * by nodeId query.</p>
     * @param nodeIdObject The nodeId object.
     * @return The new query.
     */
    Object newNodeQueryById(Object nodeIdObject)
    {
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();
        filter.addEqualTo("nodeId", nodeIdObject);
        Object query = store.newQuery(NodeImpl.class, filter);
        return query;
    }

    /**
     * <p>Utility method to create a new <code>Node</code>
     * by parentNodeId, name and type query.</p>
     * @param parentNodeIdObject The parentNodeId object.
     * @param nodeName The node name.
     * @param nodeType The node type.
     * @return The new query.
     */
    Object newNodeQueryByParentIdNameAndType(Object parentNodeIdObject, Object nodeName, Object nodeType)
    {
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();
        filter.addEqualTo("parentNodeId", parentNodeIdObject);
        filter.addEqualTo("nodeName", nodeName);
        filter.addEqualTo("nodeType", nodeType);
        Object query = store.newQuery(NodeImpl.class, filter);
        return query;
    }

    /**
     * <p>Utility method to create a new <code>Node</code>
     * by parentNodeId query.</p>
     * @param parentNodeIdObject The parentNodeId object.
     * @return The new query.
     */
    Object newNodeQueryByParentId(Object parentNodeIdObject)
    {
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();
        filter.addEqualTo("parentNodeId", parentNodeIdObject);
        Object query = store.newQuery(NodeImpl.class, filter);
        return query;
    }

    /**
     * <p>Utility method to create a new <code>Node</code>
     * by fullPath and nodeType query.</p>
     * @param fullPath The fullPath.
     * @param nodeTypeObject The nodeType object.
     * @return The new query.
     */
    Object newNodeQueryByPathAndType(Object fullPath, Object nodeTypeObject)
    {
        PersistenceStore store = getPersistenceStore();
        Filter filter = store.newFilter();
        filter.addEqualTo("fullPath", fullPath);
        filter.addEqualTo("nodeType", nodeTypeObject);
        Object query = store.newQuery(NodeImpl.class, filter);
        return query;
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
