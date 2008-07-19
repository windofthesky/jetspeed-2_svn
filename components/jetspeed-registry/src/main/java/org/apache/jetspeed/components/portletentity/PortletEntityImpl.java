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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import javax.portlet.PortletMode;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.aggregator.RenderTrackable;
import org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.MutablePortletEntity;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.portlet.PrincipalAware;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.preference.PreferenceSetComposite;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.portlet.impl.FragmentPortletDefinition;
import org.apache.jetspeed.om.preference.FragmentPreference;
import org.apache.jetspeed.om.preference.impl.PreferenceSetImpl;
import org.apache.jetspeed.om.window.impl.PortletWindowListImpl;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.request.RequestContextComponent;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.Preference;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.util.StringUtils;

/**
 * Portlet Entity default implementation.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id: PortletEntityImpl.java,v 1.9 2005/04/29 13:59:08 weaver Exp $
 */
public class PortletEntityImpl implements MutablePortletEntity, PrincipalAware, RenderTrackable
{   
    private Long oid;
    private JetspeedObjectID id;
    private static PortletPreferencesProvider portletPreferencesProvider;
    private static PortletEntityAccessComponent portletEntityAccess;    
    private static PortletRegistry registry;
    private static RequestContextComponent requestContextComponent;
    private static PageManager pageManager;
    
    protected PreferenceSetComposite pagePreferenceSet;
    protected Map perPrincipalPrefs = new HashMap();
    private PortletApplicationEntity applicationEntity = null;
    private PortletWindowList portletWindows = new PortletWindowListImpl();
    private PortletDefinitionComposite portletDefinition = null;  
    protected String portletName;
    protected String appName;
    private boolean dirty = false;
    private Fragment fragment;
    
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

    public Long getOid()
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
        if (isEditDefaultsMode())
        {
            return getPreferenceSetFromPage();
        }
        else
        {
            Principal currentUser = getPrincipal();
            return getPreferenceSet(currentUser);
        }
    }

    public PreferenceSet getPreferenceSet(Principal principal)
    {
        PreferenceSet preferenceSet = (PreferenceSet)perPrincipalPrefs.get(principal);
//        PrefsPreferenceSetImpl preferenceSet = (PrefsPreferenceSetImpl) perPrincipalPrefs.get(principal);
        if (preferenceSet == null || !dirty)
        {
            retrievePortletPreferencesProvider();
            preferenceSet = portletPreferencesProvider.getPreferenceSet(this, principal.getName());
            perPrincipalPrefs.put(principal, preferenceSet);
            /*
             * TODO:  MergeSharedPreferences is broken AFAIK, this features needs to be reevaluated now that we also have edit_defaults!
            if (pac.isMergeSharedPreferences())
            {
                mergePreferencesSet(preferenceSet);
            }
            */
            dirty = true;
        }
        return preferenceSet;
    }
    
    private PreferenceSet getPreferenceSetFromPage()
    {
        PreferenceSetComposite preferenceSet = this.pagePreferenceSet;
        
        if (preferenceSet == null || !dirty)
        {
            preferenceSet = new PreferenceSetImpl();
            this.pagePreferenceSet = preferenceSet;
            
            List fragmentPreferences = this.fragment.getPreferences();
            
            if (fragmentPreferences != null)
            {
                for (Iterator it = fragmentPreferences.iterator(); it.hasNext(); )
                {
                    FragmentPreference preference = (FragmentPreference) it.next();
                    List preferenceValues = preference.getValueList();
                    PreferenceComposite pref = (PreferenceComposite)preferenceSet.add(preference.getName(), preferenceValues);
                    pref.setReadOnly(Boolean.toString(preference.isReadOnly()));
                }
            }
            dirty = true;
        }
        return preferenceSet;
    }
    
/*    
 * TODO:  MergeSharedPreferences is broken AFAIK, this features needs to be reevaluated now that we also have edit_defaults!
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
*/
    public PortletDefinition getPortletDefinition()
    {
        // there are cases when jetspeed gets initialized before
        // all of the portlet web apps have.  In this event, premature
        // access to the portal would cause portlet entities to be cached
        // with their associated window without there corresponding PortletDefinition
        // (becuase the PortletApplication has yet to be registered).
        if(this.portletDefinition == null)
        {
            retrievePortletRegistry();
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
        // JS2-852: don't use thread local
        retrieveRequestContextComponent();
        RequestContext rc = requestContextComponent.getRequestContext();
        String entityFragmentKey = getEntityFragmentKey();
        PortletDefinition fpd = null;
        if (rc != null)
        {
            fpd= (PortletDefinition)rc.getAttribute(entityFragmentKey);
        }
        if (fpd == null)
        {
            fpd = new FragmentPortletDefinition(this.portletDefinition, fragment);
            if (rc != null)
            {
                rc.setAttribute(entityFragmentKey, fpd);
            }
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
        retrievePortletEntityAccess();
        try
        {
            portletEntityAccess.storePortletEntity(this);
        }
        catch (PortletEntityNotStoredException e)
        {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);            
            throw ioe;
        }
    }
    
    public void storeChildren()
    {
        if (isEditDefaultsMode())
        {
            storeToPage();
        }
        else
        {
            Principal currentUser = getPrincipal();
            store(currentUser);
        }
    }
    
    private void store(Principal principal)
    {
        PreferenceSetComposite preferenceSet = (PreferenceSetComposite)perPrincipalPrefs.get(principal);
        
        if (preferenceSet != null)
        {
            retrievePortletPreferencesProvider();
            portletPreferencesProvider.savePreferenceSet(this, principal.getName(), preferenceSet);
        }
        dirty = false;
    }
    
    private void storeToPage()
    {
        retrievePageManager();
        retrieveRequestContextComponent();
        
        PreferenceSet preferenceSet = this.pagePreferenceSet;
        List preferences = new ArrayList();
        
        for (Iterator it = preferenceSet.iterator(); it.hasNext(); )
        {
            Preference pref = (Preference) it.next();
            
            FragmentPreference preference = pageManager.newFragmentPreference();
            preference.setName(pref.getName());
            List preferenceValues = new ArrayList();
            
            for (Iterator iterVals = pref.getValues(); iterVals.hasNext(); )
            {
                preferenceValues.add(iterVals.next());
            }
            
            preference.setValueList(preferenceValues);
            preferences.add(preference);
        }
        
        this.fragment.setPreferences(preferences);
        
        try
        {
            pageManager.updatePage(requestContextComponent.getRequestContext().getPage());
        }
        catch (Exception e)
        {
        }
        
        dirty = false;
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
        dirty = true;
        getPreferenceSet(getPrincipal());        
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
            retrieveRequestContextComponent();
            RequestContext rc = requestContextComponent.getRequestContext();
            if (rc != null)
            {
                rc.getRequest().removeAttribute(getEntityFragmentKey());
            }
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
        retrieveRequestContextComponent();
        if (requestContextComponent == null)
        {
            // TODO: shouldn't be possible anymore
            return new PortletEntityUserPrincipal(NO_PRINCIPAL);
        }            
        RequestContext rc = requestContextComponent.getRequestContext();
        if (rc == null)
        {
            return new PortletEntityUserPrincipal(NO_PRINCIPAL);
        }
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
        retrieveRequestContextComponent();
        RequestContext rc = requestContextComponent.getRequestContext();
        if (rc != null)
        {
            rc.getRequest().removeAttribute(getEntityFragmentKey());
        }
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

    private boolean isEditDefaultsMode()
    {
        boolean editDefaultsMode = false;
        
        PortletWindow curWindow = null;
        
        if (this.portletWindows != null)
        {
            try
            {
                curWindow = (PortletWindow) this.portletWindows.iterator().next();
            }
            catch (Exception e)
            {
            }
        }
        retrieveRequestContextComponent();
        RequestContext context = requestContextComponent.getRequestContext();
        
        try
        {
            PortletMode curMode = context.getPortalURL().getNavigationalState().getMode(curWindow);
            editDefaultsMode = (JetspeedActions.EDIT_DEFAULTS_MODE.equals(curMode));
        }
        catch (Exception e)
        {
        }
        return editDefaultsMode;
    }

    protected String getEntityFragmentKey()
    {
        String entityId = (this.getId() == null) ? "-unknown-entity" : this.getId().toString();
        return "org.apache.jetspeed" + entityId ;
    }
    
    private void retrievePortletRegistry()
    {
        if (registry == null)
        {
            registry = (PortletRegistry)Jetspeed.getComponentManager().getComponent("portletRegistry");
        }
    }

    private void retrieveRequestContextComponent()
    {
        if (requestContextComponent == null)
        {
            requestContextComponent = (RequestContextComponent)Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.request.RequestContextComponent");
        }
    }

    private void retrievePortletEntityAccess()
    {
        if (portletEntityAccess == null)
        {
            portletEntityAccess = (PortletEntityAccessComponent)Jetspeed.getComponentManager().getComponent("portletEntityAccess");
        }
    }

    private void retrievePortletPreferencesProvider()
    {
        if (portletPreferencesProvider == null)
        {
            portletPreferencesProvider = (PortletPreferencesProvider)Jetspeed.getComponentManager().getComponent("portletPreferencesProvider");
        }
    }

    private void retrievePageManager()
    {
        if (pageManager == null)
        {
            pageManager = (PageManager)Jetspeed.getComponentManager().getComponent("org.apache.jetspeed.page.PageManager");
        }
    }
}