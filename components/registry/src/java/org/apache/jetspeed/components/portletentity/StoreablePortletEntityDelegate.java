/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.components.portletentity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.portlet.impl.StoreablePortletDefinitionDelegate;
import org.apache.jetspeed.om.preference.impl.AbstractPreference;
import org.apache.jetspeed.om.preference.impl.PreferenceSetImpl;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindowList;

/**
 * <p>
 * StoreablePortletEntityDelegate
 * </p>
 * 
 * Use this wrapper when need to allow the portlet
 * container access to a PortletEntity.  It correctly
 * support reset() and store() methods.  It also uses
 * a cloned list of prefernce objects to user access
 * as opposed to directly manipulating the original
 * prefernces directly.
 * 
 * 
 * @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class StoreablePortletEntityDelegate implements PortletEntity, PortletEntityCtrl
{
    private PersistenceStore store;
    private PortletEntity entity;
    private PortletEntityCtrl control;
    private List originalPreferences;

    protected PreferenceSetImpl mutatingPreferencesWrapper = new PreferenceSetImpl();

    protected List mutatingPreferences;

    public StoreablePortletEntityDelegate(
        PortletEntity entity,
        PortletEntityCtrl control,
        List originalPreferences,
	    PersistenceStore store)
    {
        
        if(entity instanceof StoreablePortletEntityDelegate)
        {
            throw new IllegalArgumentException("The \"entity\" argument of the StoreablePortletEntityDelegate cannot be"+
                                               " another StoreablePortletEntityDelegate.");
        }
        this.entity = entity;
        this.control = control;
        this.store = store;
        initMutatingPreferences();

    }

    /** 
     * <p>
     * getId
     * </p>
     * 
     * @see org.apache.pluto.om.entity.PortletEntity#getId()
     * @return
     */
    public ObjectID getId()
    {
        return entity.getId();
    }

    /** 
     * <p>
     * getPreferenceSet
     * </p>
     * 
     * @see org.apache.pluto.om.entity.PortletEntity#getPreferenceSet()
     * @return
     */
    public PreferenceSet getPreferenceSet()
    {
        mutatingPreferencesWrapper.setInnerCollection(mutatingPreferences);
        return mutatingPreferencesWrapper;
    }

    /** 
     * <p>
     * getPortletDefinition
     * </p>
     * 
     * @see org.apache.pluto.om.entity.PortletEntity#getPortletDefinition()
     * @return
     */
    public PortletDefinition getPortletDefinition()
    {
        if(entity.getPortletDefinition() instanceof StoreablePortletDefinitionDelegate)
        {
            return entity.getPortletDefinition();
        }
        else
        {
            return new StoreablePortletDefinitionDelegate((PortletDefinitionComposite)entity.getPortletDefinition(), store);
        }        
    }

    /** 
     * <p>
     * getPortletApplicationEntity
     * </p>
     * 
     * @see org.apache.pluto.om.entity.PortletEntity#getPortletApplicationEntity()
     * @return
     */
    public PortletApplicationEntity getPortletApplicationEntity()
    {
        return entity.getPortletApplicationEntity();
    }

    /** 
     * <p>
     * getPortletWindowList
     * </p>
     * 
     * @see org.apache.pluto.om.entity.PortletEntity#getPortletWindowList()
     * @return
     */
    public PortletWindowList getPortletWindowList()
    {
        return entity.getPortletWindowList();
    }

    /** 
     * <p>
     * getDescription
     * </p>
     * 
     * @see org.apache.pluto.om.entity.PortletEntity#getDescription(java.util.Locale)
     * @param locale
     * @return
     */
    public Description getDescription(Locale locale)
    {
        return getDescription(locale);
    }

    /** 
     * <p>
     * setId
     * </p>
     * 
     * @see org.apache.pluto.om.entity.PortletEntityCtrl#setId(java.lang.String)
     * @param id
     */
    public void setId(String id)
    {
        control.setId(id);

    }

    /** 
     * <p>
     * setPortletDefinition
     * </p>
     * 
     * @see org.apache.pluto.om.entity.PortletEntityCtrl#setPortletDefinition(org.apache.pluto.om.portlet.PortletDefinition)
     * @param portletDefinition
     */
    public void setPortletDefinition(PortletDefinition portletDefinition)
    {
        if(entity.getPortletDefinition() instanceof StoreablePortletDefinitionDelegate)
        {
            control.setPortletDefinition(((StoreablePortletDefinitionDelegate)portletDefinition).getPortlet());
        }
        else
        {
            control.setPortletDefinition(portletDefinition);
        }  

    }

    /** 
     * <p>
     * store
     * </p>
     * 
     * @see org.apache.pluto.om.entity.PortletEntityCtrl#store()
     * @throws java.io.IOException
     */
    public void store() throws IOException
    {
        try
        {

            if (mutatingPreferences != null && mutatingPreferences.size() > 0)
            {
                boolean originalPrefsExist = true;
                if (originalPreferences == null)
                {
                    originalPreferences = new ArrayList(mutatingPreferences.size());
                    originalPrefsExist = false;
                }

                try
                {
                    for (int i = 0; i < mutatingPreferences.size(); i++)
                    {
                        AbstractPreference pref = (AbstractPreference) mutatingPreferences.get(i);
                        if (originalPrefsExist)
                        {
                            AbstractPreference orgPref = (AbstractPreference) originalPreferences.get(i);
                            if (orgPref != null)
                            {
                                BeanUtils.copyProperties(orgPref, pref);
                            }
                            else
                            {
                                originalPreferences.add(pref.clone());
                            }

                        }
                        else
                        {
                            originalPreferences.add(pref.clone());
                        }

                    }
                }
                catch (Exception e1)
                {
                    throw new IOException("Unable to map mutated preferences into the originals: " + e1.toString());
                }

            }

            // PortletEntityAccess.storePortletEntity(this);
            // TODO: this is bad

            Transaction tx = store.getTransaction();
            if (!tx.isOpen())
            {
                tx.begin();
            }
            store.lockForWrite(this);
            tx.checkpoint();
        }
        catch (Exception e)
        {
            throw new IOException("Unable to store Portlet Entity. " + e.toString());
        }

    }

    /** 
     * <p>
     * reset
     * </p>
     * 
     * @see org.apache.pluto.om.entity.PortletEntityCtrl#reset()
     * @throws java.io.IOException
     */
    public void reset() throws IOException
    {
        initMutatingPreferences();

    }

    protected void initMutatingPreferences()
    {
        if(originalPreferences == null )
        {
            originalPreferences = new ArrayList();
        }
        mutatingPreferences = new ArrayList(originalPreferences.size());
        if (originalPreferences != null)
        {

            Iterator itr = originalPreferences.iterator();
            while (itr.hasNext())
            {
                AbstractPreference pref = (AbstractPreference) itr.next();
                mutatingPreferences.add(pref.clone());
            }
        }
    }

    public PortletEntity getPortletEntity()
    {
        return entity;
    }

    public PortletEntityCtrl getPortletEntityCtrl()
    {
        return control;
    }

}
