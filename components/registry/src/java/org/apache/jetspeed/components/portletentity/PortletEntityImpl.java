/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.jetspeed.aggregator.RenderTrackable;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreRuntimeExcpetion;
import org.apache.jetspeed.components.persistence.store.RemovalAware;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.portlet.PrincipalAware;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.impl.FragmentPortletDefinition;
import org.apache.jetspeed.om.preference.impl.PrefsPreference;
import org.apache.jetspeed.om.preference.impl.PrefsPreferenceSetImpl;
import org.apache.jetspeed.om.window.impl.PortletWindowListImpl;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.util.StringUtils;

/**
 * Portlet Entity default implementation.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id: PortletEntityImpl.java,v 1.9 2005/04/29 13:59:08 weaver Exp $
 */
public class PortletEntityImpl implements MutablePortletEntity, PrincipalAware, RemovalAware, RenderTrackable
{   
    private long oid;
    private JetspeedObjectID id;
    protected static PortletEntityAccessComponent pac;    
    protected static PortletRegistry registry;
    protected static RequestContextComponent rcc;
    
    protected Map perPrincipalPrefs = new HashMap();
    protected Map originalValues;
    private PortletApplicationEntity applicationEntity = null;
    private PortletWindowList portletWindows = new PortletWindowListImpl();
    private PortletDefinitionComposite portletDefinition = null;  
    protected String portletName;
    protected String appName;
    private boolean dirty = false;
    private Fragment fragment;
    private ThreadLocal fragmentPortletDefinition = new ThreadLocal();
    
    protected transient int timeoutCount = 0;
    protected transient long expiration = 0;
    
    public PortletEntityImpl(Fragment fragment)
    {
        setFragment(fragment);
    }

    public PortletEntityImpl()
    {
        super();
    }

    public static final String NO_PRINCIPAL = "no-principal";
    public static final String ENTITY_DEFAULT_PRINCIPAL = "entity-default";

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
        Principal currentUser = getPrincipal();
        return getPreferenceSet(currentUser);
    }

    public PreferenceSet getPreferenceSet(Principal principal)
    {
        PrefsPreferenceSetImpl preferenceSet = (PrefsPreferenceSetImpl) perPrincipalPrefs.get(principal);
        try
        {
            if (preferenceSet == null || !dirty)
            {
                String prefNodePath = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/" + getId() +"/"+ principal.getName() +"/"
                        + PrefsPreference.PORTLET_PREFERENCES_ROOT;
                Preferences prefNode = Preferences.userRoot().node(prefNodePath);               
                preferenceSet = new PrefsPreferenceSetImpl(prefNode);
                perPrincipalPrefs.put(principal, preferenceSet);
                if (pac.isMergeSharedPreferences())
                {
                    mergePreferencesSet(preferenceSet);
                }
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
    
    private void mergePreferencesSet(PrefsPreferenceSetImpl userPrefSet)
    throws BackingStoreException
    {
        String sharedNodePath = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/" + 
                                getId() +"/"+ NO_PRINCIPAL +"/" +
                                PrefsPreference.PORTLET_PREFERENCES_ROOT;                
        Preferences sharedNode = Preferences.userRoot().node(sharedNodePath);     
        if (sharedNode == null)
            return;
        PrefsPreferenceSetImpl sharedSet = new PrefsPreferenceSetImpl(sharedNode);
        if (sharedSet.size() == 0)
            return;
        Set names = userPrefSet.getNames();
        Iterator sharedPrefs = sharedSet.iterator();
        int index = 0;
        while (sharedPrefs.hasNext())
        {
            PrefsPreference sharedPref = (PrefsPreference) sharedPrefs.next();
// this seems limiting, removing if (names.contains(sharedPref.getName()))
            List prefs = Arrays.asList(sharedPref.getValueArray());
            userPrefSet.add(sharedPref.getName(), prefs);
            index++;
        }        
    }

    /**
     * <p>
     * backupValues
     * </p>
     * 
     *  
     */
    protected void backupValues( PreferenceSet preferenceSet )
    {
        originalValues = new HashMap();
        Iterator itr = preferenceSet.iterator();
        while (itr.hasNext())
        {
            PrefsPreference pref = (PrefsPreference) itr.next();

            String[] currentValues = pref.getValueArray();
            String[] backUp = new String[currentValues.length];
            System.arraycopy(currentValues, 0, backUp, 0, currentValues.length);
            originalValues.put(pref.getName(), backUp);

        }
    }

    public PortletDefinition getPortletDefinition()
    {
        // there are cases when jetspeed gets initialized before
        // all of the portlet web apps have.  In this event, premature
        // access to the portal would cause portlet entities to be cached
        // with their associated window without there corresponding PortletDefinition
        // (becuase the PortletApplication has yet to be registered).
        if(this.portletDefinition == null)
        {
            PortletDefinition pd = registry.getPortletDefinitionByIdentifier(getPortletUniqueName());
            if ( pd != null )
            {
              // only store a really found PortletDefinition
              // to prevent an IllegalArgumentException to be thrown
              setPortletDefinition(pd);
            }
            else
            {
                return null;
            }
        }        
        
        // Wrap the portlet defintion every request thread
        PortletDefinition fpd = (PortletDefinition)fragmentPortletDefinition.get();
        if (fpd == null)
        {
            fpd = new FragmentPortletDefinition(this.portletDefinition, fragment);
            fragmentPortletDefinition.set(fpd);
        }        
        return fpd;
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
        store(getPrincipal());
    }
    
    public void store(Principal principal) throws IOException
    {
        if (pac == null)
        {
            throw new IllegalStateException("You must call PortletEntityImpl.setPorteltEntityDao() before "
                    + "invoking PortletEntityImpl.store().");
        }

        PreferenceSet preferenceSet = (PreferenceSet)perPrincipalPrefs.get(principal);
        pac.storePreferenceSet(preferenceSet, this);
        dirty = false;
        if (preferenceSet != null)
        {
            backupValues(preferenceSet);
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
        PrefsPreferenceSetImpl preferenceSet = (PrefsPreferenceSetImpl) perPrincipalPrefs.get(getPrincipal());
        try
        {
            if (originalValues != null && preferenceSet != null)
            {
                Iterator prefs = preferenceSet.iterator();

                while (prefs.hasNext())
                {
                    PrefsPreference pref = (PrefsPreference) prefs.next();
                    if (originalValues.containsKey(pref.getName()))
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
                while (keys.hasNext())
                {
                    String key = (String) keys.next();
                    if (preferenceSet.get(key) == null)
                    {
                        preferenceSet.add(key, Arrays.asList((String[]) originalValues.get(key)));
                    }
                }
            }
            dirty = false;
            backupValues(preferenceSet);
        }
        catch (BackingStoreException e)
        {
            String msg = "Preference backing store failed: " + e.toString();
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
        if(portletDefinition != null)
        {
            buffer.append(portletDefinition.getId().toString());
        }
        else
        {
            buffer.append("null");
        }
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
        if(composite != null)
        {
            portletDefinition = (PortletDefinitionComposite) composite;
            // if the portletDefinition is modified, clear threadlocal fragmentPortletDefinition cache
            fragmentPortletDefinition.set(null);
            this.appName = ((MutablePortletApplication)portletDefinition.getPortletApplicationDefinition()).getName();
            this.portletName = portletDefinition.getName();
        }
        else
        {
            throw new IllegalArgumentException("Cannot pass a null PortletDefinition to a PortletEntity.");
        }
    }

    /**
     * @return Returns the principal.
     */
    public Principal getPrincipal()
    {
        if (rcc == null)
        {
            return new PortletEntityUserPrincipal(NO_PRINCIPAL);
        }            
        RequestContext rc = rcc.getRequestContext();
        Principal principal = rc.getUserPrincipal();
        if (principal == null)
        {
            principal = new PortletEntityUserPrincipal(NO_PRINCIPAL);
        }
        return principal;
    }

    class PortletEntityUserPrincipal implements Principal
    {
        String name;

        protected PortletEntityUserPrincipal( String name )
        {
            this.name = name;
        }

        /**
         * <p>
         * getName
         * </p>
         * 
         * @see java.security.Principal#getName()
         * @return
         */
        public String getName()
        {
            return name;
        }

        /**
         * <p>
         * equals
         * </p>
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         * @param obj
         * @return
         */
        public boolean equals( Object obj )
        {
            if (obj != null && obj instanceof Principal)
            {
                Principal p = (Principal) obj;
                return name != null && p.getName() != null && name.equals(p.getName());
            }
            else
            {
                return false;
            }
        }

        /**
         * <p>
         * hashCode
         * </p>
         * 
         * @see java.lang.Object#hashCode()
         * @return
         */
        public int hashCode()
        {
            if (name != null)
            {
                return (getClass().getName()+ ":" + name).hashCode();
            }
            else
            {
                return -1;
            }
        }

        /**
         * <p>
         * toString
         * </p>
         * 
         * @see java.lang.Object#toString()
         * @return
         */
        public String toString()
        {
            return name;
        }
    }
    /**
     * <p>
     * postRemoval
     * </p>
     *
     * @see org.apache.jetspeed.components.persistence.store.RemovalAware#postRemoval(org.apache.jetspeed.components.persistence.store.PersistenceStore)
     * @param store
     * @throws {@link org.apache.jetspeed.persistence.store.PersistenceStoreRuntimeExcpetion}
     * if the removal of the {@link java.util.prefs.Preference} related to this entity fails
     */
    public void postRemoval( PersistenceStore store )
    {
      

    }
    /**
     * <p>
     * preRemoval
     * </p>
     *	not implemented.
     *
     * @see org.apache.jetspeed.components.persistence.store.RemovalAware#preRemoval(org.apache.jetspeed.components.persistence.store.PersistenceStore)
     * @param store
     */
    public void preRemoval( PersistenceStore store )
    {
        String rootForEntity = MutablePortletEntity.PORTLET_ENTITY_ROOT + "/" + getId();
        try
        {
            if(Preferences.userRoot().nodeExists(rootForEntity))
            {
                Preferences.userRoot().node(rootForEntity).removeNode();
            }
        }
        catch (BackingStoreException e)
        {           
            throw new PersistenceStoreRuntimeExcpetion(e.toString(), e);
        }        

    }
    public String getPortletUniqueName()
    {
        if(this.appName != null && this.portletName != null)
        {
            return this.appName+"::"+this.portletName;
        }
        else if(fragment != null)
        {
            return fragment.getName();
        }
        else
        {
            return null;
        }
    }

    public void setFragment(Fragment fragment)
    {
        this.fragment = fragment;
        // if the fragment is set, clear threadlocal fragmentPortletDefinition cache
        fragmentPortletDefinition.set(null);
    }

    public int getRenderTimeoutCount()
    {
        return timeoutCount;
    }
    
    public synchronized void incrementRenderTimeoutCount()
    {
        timeoutCount++;
    }
    
    public synchronized void setExpiration(long expiration)
    {
        this.expiration = expiration;
    }
    
    public long getExpiration()
    {
        return this.expiration;
    }
    
    public void success()
    {
        timeoutCount = 0;
    }
    
    public void setRenderTimeoutCount(int timeoutCount)
    {
        this.timeoutCount = timeoutCount;
    }

}