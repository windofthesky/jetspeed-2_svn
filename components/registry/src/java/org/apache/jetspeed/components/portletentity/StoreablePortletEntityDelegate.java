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
package org.apache.jetspeed.components.portletentity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
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
        this.entity = entity;
        this.control = control;
        this.store = store;

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
        return entity.getPreferenceSet();
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
        return entity.getPortletDefinition();
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
        setPortletDefinition(portletDefinition);

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
            //		  PersistenceStoreContainer pContainer = (PersistenceStoreContainer) PicoBootstrapContainer.getComponentInstance(PersistenceStoreContainer.class);
            //		  PersistenceStore store = pContainer.getStore("jetspeed");
            //		  Transaction tx = store.getTransaction();
            //		  if(!tx.isOpen())
            //		  {
            //			  tx.begin();
            //		  }
            //		  store.lockForWrite(this);
            //		  tx.checkpoint();
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
