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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.spi.om.prefs.impl.PropertyImpl;
import org.apache.jetspeed.spi.om.prefs.impl.NodeImpl;
import org.apache.jetspeed.spi.om.prefs.PropertySetDef;
import org.apache.jetspeed.spi.om.prefs.impl.PropertySetDefImpl;
import org.apache.jetspeed.spi.om.prefs.PropertyKey;
import org.apache.jetspeed.spi.om.prefs.impl.PropertyKeyImpl;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.persistence.TransactionStateException;
import org.apache.jetspeed.spi.services.prefs.PropertyManagerService;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>{@link PropertyManagerService} implementation relying on Jetspeed OJB
 * based persistence plugin for persistence.
 *
 * @author <a href="david@sensova.com">David Le Strat</a>
 */
public class PropertyManagerServiceImpl extends BaseCommonService implements PropertyManagerService
{
    private PersistencePlugin plugin;

    private static final Log log = LogFactory.getLog(PropertyManagerServiceImpl.class);

    /**
     * <p>Default constructor.</p>
     */
    public PropertyManagerServiceImpl()
    {
    }

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws CPSInitializationException
    {
        if (!isInitialized())
        {
            PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
            String pluginName = getConfiguration().getString("persistence.plugin.name", "jetspeed");
            plugin = ps.getPersistencePlugin(pluginName);
            setInit(true);
        }
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#addPropertySetDef(java.lang.String, short)
     */
    public int addPropertySetDef(String propertySetName, short propertySetType) throws PropertyException
    {
        ArgUtil.notNull(
            new Object[] { propertySetName, new Short(propertySetType)},
            new String[] { "propertySetName", "propertySetType" },
            "addPropertySetDef(java.lang.String, java.lang.String)");

        int propertySetDefId = -1;
        // We should not have duplicated property set definition for a
        // specific type.
        try
        {
            propertySetDefId = getPropertySetDefIdByType(propertySetName, propertySetType);
        }
        catch (PropertyException pex)
        {
            PropertySetDef ppsd = new PropertySetDefImpl(propertySetName, propertySetType);
            try
            {
                plugin.beginTransaction();
                plugin.prepareForUpdate(ppsd);
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

        if (-1 != propertySetDefId)
        {
            throw new PropertyException(PropertyException.PROPERTYSET_DEFINITION_ALREADY_EXISTS);
        }
        return getPropertySetDefIdByType(propertySetName, propertySetType);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#getPropertySetDefIdByType(java.lang.String, short)
     */
    public int getPropertySetDefIdByType(String propertySetName, short propertySetType) throws PropertyException
    {
        Short propertySetTypeObject = new Short(propertySetType);
        ArgUtil.notNull(
            new Object[] { propertySetName, propertySetTypeObject },
            new String[] { "propertySetName", "propertySetType" },
            "getPropertySetDefIdByType(java.lang.String, java.lang.String)");

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("propertySetName", propertySetName);
        c.addEqualTo("propertySetType", propertySetTypeObject);
        Object query = plugin.generateQuery(PropertySetDefImpl.class, c);
        PropertySetDef ppsd = (PropertySetDef) plugin.getObjectByQuery(PropertySetDefImpl.class, query);

        if (null != ppsd)
        {
            return ppsd.getPropertySetDefId();
        }
        else
        {
            throw new PropertyException(PropertyException.PROPERTYSET_DEFINITION_NOT_FOUND);
        }
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#removePropertySetDef(int)
     */
    public void removePropertySetDef(int propertySetDefId) throws PropertyException
    {
        Integer propertySetDefIdObject = new Integer(propertySetDefId);

        ArgUtil.notNull(new Object[] { propertySetDefIdObject }, new String[] { "propertySetDefId" }, "removePropertySetDef(int)");

        // We need to remove all property set, property values and
        // keys associated to that set definition.

        // First we remove all property keys associated with that set
        // definition. This removes all associated values.
        removePropertyKeysBySetDef(propertySetDefId);

        // Second remove all associated nodes.
        LookupCriteria c1 = plugin.newLookupCriteria();
        c1.addEqualTo("propertySetDefId", propertySetDefIdObject);
        Object query1 = plugin.generateQuery(NodeImpl.class, c1);

        plugin.deleteByQuery(query1);

        // Finally, remove the set definition.
        LookupCriteria c2 = plugin.newLookupCriteria();
        c2.addEqualTo("propertySetDefId", propertySetDefIdObject);
        Object query2 = plugin.generateQuery(PropertySetDefImpl.class, c2);

        plugin.deleteByQuery(query2);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#updatePropertySetDef(int, java.lang.String, short)
     */
    public void updatePropertySetDef(int propertySetDefId, String propertySetName, short propertySetType)
    {
        Integer propertySetDefIdObject = new Integer(propertySetDefId);
        Short propertySetTypeObject = new Short(propertySetType);

        ArgUtil.notNull(
            new Object[] { propertySetDefIdObject, propertySetName, propertySetTypeObject },
            new String[] { "propertySetDefId", "propertySetName", "propertySetType" },
            "updatePropertySetDef(int, java.lang.String, java.lang.String)");

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("propertySetDefId", propertySetDefIdObject);
        Object query = plugin.generateQuery(PropertySetDefImpl.class, c);
        PropertySetDef ppsd = (PropertySetDef) plugin.getObjectByQuery(PropertySetDefImpl.class, query);

        try
        {
            plugin.beginTransaction();
            plugin.prepareForUpdate(ppsd);
            ppsd.setPropertySetName(propertySetName);
            ppsd.setPropertySetType(propertySetType);
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
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#getAllPropertySetsByType(short)
     */
    public Map getAllPropertySetsByType(short propertySetType) throws PropertyException
    {
        Short propertySetTypeObject = new Short(propertySetType);

        ArgUtil.notNull(
            new Object[] { propertySetTypeObject },
            new String[] { "propertySetType" },
            "getAllPropertySetsByType(java.lang.String)");

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("propertySetType", propertySetTypeObject);
        Object query = plugin.generateQuery(PropertySetDefImpl.class, c);
        Collection propertySetDefs = plugin.getCollectionByQuery(PropertySetDefImpl.class, query);

        if (null != propertySetDefs)
        {
            Map propertySetsByType = new HashMap(propertySetDefs.size());
            for (Iterator i = propertySetDefs.iterator(); i.hasNext();)
            {
                PropertySetDef curppsd = (PropertySetDef) i.next();
                propertySetsByType.put(new Integer(curppsd.getPropertySetDefId()), curppsd.getPropertySetName());
            }
            return propertySetsByType;
        }
        else
        {
            throw new PropertyException(PropertyException.PROPERTYSET_DEFINITION_NOT_FOUND);
        }

    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#addPropertyKey(int, java.util.Collection)
     */
    public void addPropertyKeys(int propertySetDefId, Collection propertyKeys) throws PropertyException
    {
        Integer propertySetDefIdObject = new Integer(propertySetDefId);

        ArgUtil.notNull(
            new Object[] { propertySetDefIdObject, propertyKeys },
            new String[] { "propertySetDefId", "propertyKeys" },
            "addPropertySetDef(int, java.util.Set)");

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("propertySetDefId", propertySetDefIdObject);
        Object query = plugin.generateQuery(PropertySetDefImpl.class, c);
        PropertySetDef ppsd = (PropertySetDef) plugin.getObjectByQuery(PropertySetDefImpl.class, query);
        if (null != ppsd)
        {
            // Create a set of property keys to add to the property set definition.
            Collection propertyKeysObj = new ArrayList(propertyKeys.size());
            for (Iterator i = propertyKeys.iterator(); i.hasNext();)
            {
                Map currentPropertyKey = (Map) i.next();
                PropertyKey ppk =
                    new PropertyKeyImpl(
                        propertySetDefId,
                        (String) currentPropertyKey.get(PROPERTYKEY_NAME),
                        ((Short) currentPropertyKey.get(PROPERTYKEY_TYPE)).shortValue());
                propertyKeysObj.add(ppk);
            }

            try
            {
                plugin.beginTransaction();
                plugin.prepareForUpdate(ppsd);
                // Add the properties to the set.
                ppsd.setPropertyKeys(propertyKeysObj);
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
        else
        {
            throw new PropertyException(PropertyException.PROPERTYSET_DEFINITION_NOT_FOUND);
        }
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#removePropertyKeysBySet(int)
     */
    public void removePropertyKeysBySetDef(int propertySetDefId) throws PropertyException
    {
        Integer propertySetDefIdObject = new Integer(propertySetDefId);

        ArgUtil.notNull(
            new Object[] { propertySetDefIdObject },
            new String[] { "propertySetDefId" },
            "removePropertyKeysBySet(int)");

        Map propertyKeys = getPropertyKeysBySetDef(propertySetDefId);

        if ((null != propertyKeys) && (propertyKeys.size() > 0))
        {
            LookupCriteria c1 = plugin.newLookupCriteria();
            c1.addIn("propertyKeyId", propertyKeys.keySet());
            Object query1 = plugin.generateQuery(PropertyImpl.class, c1);

            plugin.deleteByQuery(query1);
        }

        LookupCriteria c2 = plugin.newLookupCriteria();
        c2.addEqualTo("propertySetDefId", propertySetDefIdObject);
        Object query2 = plugin.generateQuery(PropertyKeyImpl.class, c2);

        plugin.deleteByQuery(query2);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#getPropertyKeysBySetDef(int)
     * TODO Add keys to cache and retrieve from cache when in cache to
     * avoid look up.
     */
    public Map getPropertyKeysBySetDef(int propertySetDefId) throws PropertyException
    {
        Integer propertySetDefIdObject = new Integer(propertySetDefId);

        ArgUtil.notNull(
            new Object[] { propertySetDefIdObject },
            new String[] { "propertySetDefId" },
            "getPropertyKeysBySetDef(int)");

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("propertySetDefId", propertySetDefIdObject);
        Object query = plugin.generateQuery(PropertyKeyImpl.class, c);
        Collection propertyKeys = plugin.getCollectionByQuery(PropertyKeyImpl.class, query);

        if (null != propertyKeys)
        {
            Map propertyKeyIds = new HashMap(propertyKeys.size());
            for (Iterator i = propertyKeys.iterator(); i.hasNext();)
            {
                PropertyKey curppk = (PropertyKey) i.next();
                propertyKeyIds.put((new Integer(curppk.getPropertyKeyId())), curppk.getPropertyKeyName());
            }
            return propertyKeyIds;
        }
        else
        {
            throw new PropertyException(PropertyException.PROPERTYKEY_NOT_FOUND);
        }
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#removePropertyKey(int)
     */
    public void removePropertyKey(int propertyKeyId)
    {
        Integer propertyKeyIdObject = new Integer(propertyKeyId);

        ArgUtil.notNull(new Object[] { propertyKeyIdObject }, new String[] { "propertyKeyId" }, "removePropertyKey(int)");

        // First we remove all property values associated with this key.
        LookupCriteria c1 = plugin.newLookupCriteria();
        c1.addEqualTo("propertyKeyId", propertyKeyIdObject);
        Object query1 = plugin.generateQuery(PropertyImpl.class, c1);

        plugin.deleteByQuery(query1);

        // Second we delete the property key.
        LookupCriteria c2 = plugin.newLookupCriteria();
        c2.addEqualTo("propertyKeyId", propertyKeyIdObject);
        Object query2 = plugin.generateQuery(PropertyKeyImpl.class, c2);

        plugin.deleteByQuery(query2);
    }

    /**
     * @see org.apache.jetspeed.spi.services.prefs.PropertyManagerService#updatePropertyKey(int, java.lang.String)
     */
    public void updatePropertyKey(int propertyKeyId, String propertyKeyName)
    {
        Integer propertyKeyIdObject = new Integer(propertyKeyId);

        ArgUtil.notNull(
            new Object[] { propertyKeyIdObject, propertyKeyName },
            new String[] { "propertyKeyId", "propertyKeyName" },
            "updatePropertyKey(int, java.lang.String)");

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("propertyKeyId", propertyKeyIdObject);
        Object query = plugin.generateQuery(PropertyKeyImpl.class, c);
        PropertyKey ppk = (PropertyKey) plugin.getObjectByQuery(PropertyKeyImpl.class, query);

        try
        {
            plugin.beginTransaction();
            plugin.prepareForUpdate(ppk);
            ppk.setPropertyKeyName(propertyKeyName);
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

}
