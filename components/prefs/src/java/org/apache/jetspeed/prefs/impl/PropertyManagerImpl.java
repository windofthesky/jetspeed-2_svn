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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;
import org.apache.jetspeed.prefs.PropertyManager;
import org.apache.jetspeed.prefs.om.PropertySetDef;
import org.apache.jetspeed.prefs.om.PropertyKey;
import org.apache.jetspeed.prefs.om.impl.PropertyImpl;
import org.apache.jetspeed.prefs.om.impl.PropertyKeyImpl;
import org.apache.jetspeed.prefs.om.impl.PropertySetDefImpl;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>{@link PropertyManager} implementation relying on Jetspeed OJB
 * based persistence plugin for persistence.
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class PropertyManagerImpl implements PropertyManager
{
    private static final Log log = LogFactory.getLog(PropertyManagerImpl.class);

    /** The persistence store container. */
    private PersistenceStoreContainer storeContainer;

    /** The store name. */
    private String jetspeedStoreName;

    /** Common queries. **/
    private CommonQueries commonQueries;

    /**
     * <p>Constructor providing access to the persistence component.</p>
     */
    public PropertyManagerImpl(PersistenceStoreContainer storeContainer, String keyStoreName)
    {
        if (storeContainer == null)
        {
            throw new IllegalArgumentException("storeContainer cannot be null for PropertyManagerImpl");
        }

        this.storeContainer = storeContainer;
        this.jetspeedStoreName = keyStoreName;
        this.commonQueries = new CommonQueries(storeContainer, keyStoreName);
    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#addPropertySetDef(java.lang.String, short)
     */
    public void addPropertySetDef(String propertySetName, short propertySetType) throws PropertyException
    {
        Short propertySetTypeObject = new Short(propertySetType);
        ArgUtil.notNull(
            new Object[] { propertySetName, propertySetTypeObject },
            new String[] { "propertySetName", "propertySetType" },
            "addPropertySetDef(java.lang.String, java.lang.String)");

        // We should not have duplicated property set definition for a
        // specific type.
        PersistenceStore store = getPersistenceStore();
        PropertySetDef ppsd =
            (PropertySetDef) store.getObjectByQuery(
                commonQueries.newPropertySetDefQueryByNameAndType(propertySetName, propertySetTypeObject));
        if (null == ppsd)
        {
            ppsd = new PropertySetDefImpl(propertySetName, propertySetType);
            try
            {
                store.lockForWrite(ppsd);
                store.getTransaction().checkpoint();
            }
            catch (LockFailedException lfe)
            {
                throw new PropertyException("Unable to lock PropertySetDef for update: " + lfe.toString(), lfe);
            }
        }
        else
        {
            throw new PropertyException(PropertyException.PROPERTYSET_DEFINITION_ALREADY_EXISTS);
        }
    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#getPropertySetDefIdByType(java.lang.String, short)
     */
    //    public int getPropertySetDefIdByType(String propertySetName, short propertySetType) throws PropertyException
    //    {
    //        Short propertySetTypeObject = new Short(propertySetType);
    //        ArgUtil.notNull(
    //            new Object[] { propertySetName, propertySetTypeObject },
    //            new String[] { "propertySetName", "propertySetType" },
    //            "getPropertySetDefIdByType(java.lang.String, java.lang.String)");
    //
    //        PersistenceStore store = getPersistenceStore();
    //        PropertySetDef ppsd =
    //            (PropertySetDef) store.getObjectByQuery(
    //                commonQueries.newPropertySetDefQueryByNameAndType(propertySetName, propertySetTypeObject));
    //        if (null != ppsd)
    //        {
    //            return ppsd.getPropertySetDefId();
    //        }
    //        else
    //        {
    //            throw new PropertyException(PropertyException.PROPERTYSET_DEFINITION_NOT_FOUND);
    //        }
    //    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#removePropertySetDef(java.lang.String, short)
     */
    public void removePropertySetDef(String propertySetName, short propertySetType) throws PropertyException
    {
        Short propertySetTypeObject = new Short(propertySetType);
        ArgUtil.notNull(
            new Object[] { propertySetName, propertySetTypeObject },
            new String[] { "propertySetName", "propertySetType" },
            "removePropertySetDef(java.lang.String, short)");

        // We need to remove all property set, property values and
        // keys associated to that set definition.
        PersistenceStore store = getPersistenceStore();
        PropertySetDef ppsd =
            (PropertySetDef) store.getObjectByQuery(
                commonQueries.newPropertySetDefQueryByNameAndType(propertySetName, propertySetTypeObject));
        if (null == ppsd)
        {
            if (log.isDebugEnabled())
                log.debug("Property set definition is null. Nothing to remove.");
            return;
        }
        try
        {
            store.deletePersistent(ppsd);
            store.getTransaction().checkpoint();
        }
        catch (LockFailedException lfe)
        {
            throw new PropertyException("Unable to remove property set definition: " + lfe.toString(), lfe);

        }
    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#updatePropertySetDef(java.lang.String, java.lang.String, short)
     */
    public void updatePropertySetDef(String newPropertySetName, String oldPropertySetName, short propertySetType)
        throws PropertyException
    {
        Short propertySetTypeObject = new Short(propertySetType);

        ArgUtil.notNull(
            new Object[] { newPropertySetName, oldPropertySetName, propertySetTypeObject },
            new String[] { "newPropertySetName", "oldPropertySetName", "propertySetType" },
            "updatePropertySetDef(java.lang.String, java.lang.String, short)");

        PersistenceStore store = getPersistenceStore();
        PropertySetDef ppsd =
            (PropertySetDef) store.getObjectByQuery(
                commonQueries.newPropertySetDefQueryByNameAndType(oldPropertySetName, propertySetTypeObject));
        try
        {
            store.lockForWrite(ppsd);
            ppsd.setPropertySetName(newPropertySetName);
            ppsd.setModifiedDate(new Timestamp(System.currentTimeMillis()));
            store.getTransaction().checkpoint();
        }
        catch (LockFailedException lfe)
        {
            throw new PropertyException("Unable to lock PropertySetDef for update: " + lfe.toString(), lfe);
        }
    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#getAllPropertySetsByType(short)
     */
    public Collection getAllPropertySetsByType(short propertySetType) throws PropertyException
    {
        Short propertySetTypeObject = new Short(propertySetType);
        ArgUtil.notNull(
            new Object[] { propertySetTypeObject },
            new String[] { "propertySetType" },
            "getAllPropertySetsByType(short)");

        PersistenceStore store = getPersistenceStore();
        Collection propertySetDefs = store.getCollectionByQuery(commonQueries.newPropertySetDefQueryByType(propertySetTypeObject));
        if (null != propertySetDefs)
        {
            ArrayList propertySetsByType = new ArrayList(propertySetDefs.size());
            for (Iterator i = propertySetDefs.iterator(); i.hasNext();)
            {
                PropertySetDef curppsd = (PropertySetDef) i.next();
                propertySetsByType.add(curppsd.getPropertySetName());
            }
            return propertySetsByType;
        }
        else
        {
            throw new PropertyException(PropertyException.PROPERTYSET_DEFINITION_NOT_FOUND);
        }

    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#addPropertyKeys(java.lang.String, short, java.util.Collection)
     */
    public void addPropertyKeys(String propertySetName, short propertySetType, Collection propertyKeys) throws PropertyException
    {
        Short propertySetTypeObject = new Short(propertySetType);
        ArgUtil.notNull(
            new Object[] { propertySetName, propertySetTypeObject, propertyKeys },
            new String[] { "propertySetName", "propertySetType", "propertyKeys" },
            "addPropertyKeys(java.lang.String, short, java.util.Collection)");

        PersistenceStore store = getPersistenceStore();
        PropertySetDef ppsd =
            (PropertySetDef) store.getObjectByQuery(
                commonQueries.newPropertySetDefQueryByNameAndType(propertySetName, propertySetTypeObject));
        if (null != ppsd)
        {
            // Create a set of property keys to add to the property set definition.
            Collection propertyKeysObj = new ArrayList(propertyKeys.size());
            for (Iterator i = propertyKeys.iterator(); i.hasNext();)
            {
                Map currentPropertyKey = (Map) i.next();
                PropertyKey ppk =
                    new PropertyKeyImpl(
                        ppsd.getPropertySetDefId(),
                        (String) currentPropertyKey.get(PROPERTYKEY_NAME),
                        ((Short) currentPropertyKey.get(PROPERTYKEY_TYPE)).shortValue());
                propertyKeysObj.add(ppk);
            }

            // Add the properties to the set.
            try
            {
                store.lockForWrite(ppsd);
                ppsd.setPropertyKeys(propertyKeysObj);
                ppsd.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                store.getTransaction().checkpoint();
            }
            catch (LockFailedException lfe)
            {
                throw new PropertyException("Unable to lock PropertySetDef for update: " + lfe.toString(), lfe);
            }
        }
        else
        {
            throw new PropertyException(PropertyException.PROPERTYSET_DEFINITION_NOT_FOUND);
        }
    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#removePropertyKeysBySet(int)
     */
    /*public void removePropertyKeysBySetDef(int propertySetDefId) throws PropertyException
    {
        Integer propertySetDefIdObject = new Integer(propertySetDefId);
    
        ArgUtil.notNull(
            new Object[] { propertySetDefIdObject },
            new String[] { "propertySetDefId" },
            "removePropertyKeysBySet(int)");
    
        Map propertyKeys = getPropertyKeysBySetDef(propertySetDefId);
    
        PersistenceStore store = getPersistenceStore();
        log.info("\n\n_______________ HERE0_____________\n\n");
        try
        {
            // Remove properties.
            if ((null != propertyKeys) && (propertyKeys.size() > 0))
            {
                log.info("\n\n_______________ HERE1_____________\n\n");
    
                Filter filter = store.newFilter();
                filter.addIn("propertyKeyId", propertyKeys.keySet());
                Object query = store.newQuery(PropertyImpl.class, filter);
                store.deleteAll(query);
            }
            // Remove property keys.
            store.deleteAll(commonQueries.newPropertyKeyQueryByPropertySetDefId(propertySetDefIdObject));
        }
        catch (LockFailedException lfe)
        {
            throw new PropertyException("Unable to remove property keys: " + lfe.toString(), lfe);
        }
    }
    */

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#getPropertyKeysBySetDef(java.lang.String, short)
     */
    public Collection getPropertyKeysBySetDef(String propertySetName, short propertySetType) throws PropertyException
    {
        Short propertySetTypeObject = new Short(propertySetType);
        ArgUtil.notNull(
            new Object[] { propertySetName, propertySetTypeObject },
            new String[] { "propertySetName", "propertySetType" },
            "getPropertyKeysBySetDef(java.lang.String, java.lang.String)");

        PersistenceStore store = getPersistenceStore();
        PropertySetDef ppsd =
            (PropertySetDef) store.getObjectByQuery(
                commonQueries.newPropertySetDefQueryByNameAndType(propertySetName, propertySetTypeObject));
        if (null != ppsd)
        {
            Collection propertyKeys = ppsd.getPropertyKeys();
            ArrayList propertyKeyNames = new ArrayList(propertyKeys.size());
            for (Iterator i = propertyKeys.iterator(); i.hasNext();)
            {
                PropertyKey curppk = (PropertyKey) i.next();
                propertyKeyNames.add(curppk.getPropertyKeyName());
            }
            return propertyKeyNames;
        }
        else
        {
            throw new PropertyException(PropertyException.PROPERTYSET_DEFINITION_NOT_FOUND);
        }
    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#removePropertyKey(int)
     */
    public void removePropertyKey(int propertyKeyId) throws PropertyException
    {
        Integer propertyKeyIdObject = new Integer(propertyKeyId);

        ArgUtil.notNull(new Object[] { propertyKeyIdObject }, new String[] { "propertyKeyId" }, "removePropertyKey(int)");

        PersistenceStore store = getPersistenceStore();
        try
        {
            // First we remove all property values associated with this key.
            store.deleteAll(commonQueries.newPropertyQueryById(propertyKeyIdObject));
            // Second we delete the property key.
            store.deleteAll(commonQueries.newPropertyKeyQueryById(propertyKeyIdObject));
        }
        catch (LockFailedException lfe)
        {
            throw new PropertyException("Unable to remove property key: " + lfe.toString(), lfe);
        }
    }

    /**
     * @see org.apache.jetspeed.prefs.PropertyManager#updatePropertyKey(java.lang.String, java.lang.String, java.lang.String, short)
     */
    public void updatePropertyKey(
        String newPropertyKeyName,
        String oldPropertyKeyName,
        String propertySetName,
        short propertySetType)
        throws PropertyException
    {
        Short propertySetTypeObject = new Short(propertySetType);
        ArgUtil.notNull(
            new Object[] { newPropertyKeyName, oldPropertyKeyName, propertySetName, propertySetTypeObject },
            new String[] { "newPropertyKeyName", "oldPropertyKeyName", "propertySetName", "propertySetType" },
            "updatePropertyKey(java.lang.String, java.lang.String, java.lang.String, short)");

        PersistenceStore store = getPersistenceStore();
        PropertySetDef ppsd =
            (PropertySetDef) store.getObjectByQuery(
                commonQueries.newPropertySetDefQueryByNameAndType(propertySetName, propertySetTypeObject));
        if (null != ppsd)
        {
            Collection propertyKeys = ppsd.getPropertyKeys();
            ArrayList newPropertyKeys = new ArrayList(propertyKeys.size());
            for (Iterator i = propertyKeys.iterator(); i.hasNext();)
            {
                PropertyKey curPropertyKey = (PropertyKey) i.next();
                if (curPropertyKey.getPropertyKeyName().equals(oldPropertyKeyName))
                {
                    curPropertyKey.setPropertyKeyName(newPropertyKeyName);
                    curPropertyKey.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                }
                newPropertyKeys.add(curPropertyKey);
            }

            try
            {
                store.lockForWrite(ppsd);
                ppsd.setPropertyKeys(newPropertyKeys);
                ppsd.setModifiedDate(new Timestamp(System.currentTimeMillis()));
                store.getTransaction().checkpoint();
            }
            catch (LockFailedException lfe)
            {
                throw new PropertyException("Unable to lock PropertyKey for update: " + lfe.toString(), lfe);
            }
        }
        else
        {
            throw new PropertyException(PropertyException.PROPERTYSET_DEFINITION_NOT_FOUND);
        }
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
