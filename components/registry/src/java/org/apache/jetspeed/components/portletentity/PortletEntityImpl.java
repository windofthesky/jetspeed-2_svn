/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.components.portletentity;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.Storeable;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.Transaction;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.preference.impl.PrefsPreference;
import org.apache.jetspeed.om.preference.impl.PrefsPreferenceSetImpl;
import org.apache.jetspeed.om.window.impl.PortletWindowListImpl;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.util.StringUtils;

/**
 * Portlet Entity default implementation.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 */
public class PortletEntityImpl implements MutablePortletEntity, Storeable
{

    private long oid;

    private long portletId;

    private JetspeedObjectID id;

    protected PersistenceStore store;

    private static final Log log = LogFactory.getLog(PortletEntityImpl.class);

    protected List originalPreferences;

    protected PrefsPreferenceSetImpl preferenceSet;

    protected Map originalValues;

    private PortletApplicationEntity applicationEntity = null;

    private PortletWindowList portletWindows = new PortletWindowListImpl();

    private PortletEntity modifiedObject = null;

    private PortletDefinitionComposite portletDefinition = null;

    private boolean dirty=false;

    /**
     * <p>
     * setPersistenceStore
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.Storeable#setStore(Object)
     * @param store
     */
    public void setStore( Object store )
    {
        this.store = (PersistenceStore) store;
    }

    public ObjectID getId()
    {
        return id;
    }

    public long getOid()
    {
        return oid;
    }

    public void setId( String id )
    {
        this.id = JetspeedObjectID.createFromString(id);
    }

    /**
     * 
     * <p>
     * getPreferenceSet
     * </p>
     * 
     * @see org.apache.pluto.om.entity.PortletEntity#getPreferenceSet()
     * @return
     */
    public PreferenceSet getPreferenceSet()
    {
        try
        {
            if (preferenceSet == null || !dirty)
            {
                String prefNodePath = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/" + getId() + "/" + PrefsPreference.PORTLET_PREFERENCES_ROOT;
                Preferences prefNode = Preferences.userRoot().node(prefNodePath);
                preferenceSet = new PrefsPreferenceSetImpl(prefNode);

                backupValues(preferenceSet);
                dirty = true;
            }
        }
        catch (BackingStoreException e)
        {
            String msg = "Preference backing store failed: " + e.toString();
            IllegalStateException ise = new IllegalStateException(msg);
            ise.initCause(e);
            throw ise;
        }

        return preferenceSet;
    }

    /**
     * <p>
     * backupValues
     * </p>
     *
     * 
     */
    protected void backupValues(PreferenceSet preferenceSet)
    {
        originalValues = new HashMap();
        Iterator itr = preferenceSet.iterator();
        while (itr.hasNext())
        {
            PrefsPreference pref = (PrefsPreference) itr.next();
         
            String[] currentValues = pref.getValueArray();
            String[] backUp = new String[currentValues.length];
            System.arraycopy( currentValues, 0, backUp ,0, currentValues.length);
            originalValues.put(pref.getName(), backUp);

        }
    }

    public PortletDefinition getPortletDefinition()
    {
        return this.portletDefinition;
    }

    public PortletApplicationEntity getPortletApplicationEntity()
    {
        return applicationEntity;
    }

    public PortletWindowList getPortletWindowList()
    {
        return portletWindows;
    }

    /**
     * 
     * <p>
     * store
     * </p>
     *  
     */
    public void store() throws IOException
    {
        if (store == null)
        {
            throw new IllegalStateException("You must call PortletEntityImpl.setStore() before "
                    + "invoking PortletEntityImpl.store().");
        }
        
    
        try
        {
            prepareTransaction(store);            
			store.lockForWrite(this);
			if(preferenceSet != null)
			{
				preferenceSet.flush();
			}			
            store.getTransaction().checkpoint();
            dirty = false;
            if(preferenceSet != null)
            {
                backupValues(preferenceSet);
            }
        }
        catch (Exception e)
        {           
            String msg = "Failed to store portlet entity:" + e.toString();
            IOException ioe = new IOException(msg);
            ioe.initCause(e);
            store.getTransaction().rollback();
            throw ioe;
        }

    }

    /**
     * 
     * <p>
     * reset
     * </p>
     * 
     */

    public void reset() throws IOException
    {
        try
        {
            if(originalValues != null && preferenceSet != null )
            {
            	Iterator prefs = preferenceSet.iterator();
            	
            	while(prefs.hasNext())
            	{
            		PrefsPreference pref = (PrefsPreference) prefs.next();
            		if(originalValues.containsKey(pref.getName()))
            		{
            			pref.setValues((String[]) originalValues.get(pref.getName()));
            		}
            		else
            		{
            			preferenceSet.remove(pref);
            		}
            		preferenceSet.flush();
            	}    		
            	
            	Iterator keys = originalValues.keySet().iterator();
            	while(keys.hasNext())
            	{
            	    String key = (String) keys.next();
            	    if(preferenceSet.get(key) == null)
            	    {
            	        preferenceSet.add(key,Arrays.asList((String[])originalValues.get(key)));
            	    }
            	}
            }
            dirty = false;
            backupValues(preferenceSet);
        }
        catch (BackingStoreException e)
        {
            String msg = "Preference backing store failed: "+e.toString();
            IOException ioe = new IOException(msg);
            ioe.initCause(e);
            throw ioe;
        }
        
    }

    // internal methods used for debugging purposes only

    public String toString()
    {
        return toString(0);
    }

    public String toString( int indent )
    {
        StringBuffer buffer = new StringBuffer(1000);
        StringUtils.newLine(buffer, indent);
        buffer.append(getClass().toString());
        buffer.append(":");
        StringUtils.newLine(buffer, indent);
        buffer.append("{");
        StringUtils.newLine(buffer, indent);
        buffer.append("id='");
        buffer.append(oid);
        buffer.append("'");
        StringUtils.newLine(buffer, indent);
        buffer.append("definition-id='");
        buffer.append(portletDefinition.getId().toString());
        buffer.append("'");

        StringUtils.newLine(buffer, indent);
        //buffer.append(((PreferenceSetImpl)preferences).toString(indent));

        StringUtils.newLine(buffer, indent);
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * @see org.apache.pluto.om.entity.PortletEntity#getDescription(java.util.Locale)
     */
    public Description getDescription( Locale arg0 )
    {
        return portletDefinition.getDescription(arg0);
    }

    /**
     * <p>
     * setPortletDefinition
     * </p>
     * 
     * @param composite
     *  
     */
    public void setPortletDefinition( PortletDefinition composite )
    {
        portletDefinition = (PortletDefinitionComposite) composite;
    }

    /**
     * Checks to see if the <code>store</code>'s current transaction
     * needs to be started or not.
     * @param store
     */
    protected void prepareTransaction(PersistenceStore store)
    {
        Transaction tx = store.getTransaction();
        if (!tx.isOpen())
        {
            tx.begin();
        }
    }

}