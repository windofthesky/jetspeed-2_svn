/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.portletregistry;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.impl.LanguageImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.springframework.dao.DataAccessException;

/**
 * <p>
 * OjbPortletRegistry
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class PersistenceBrokerPortletRegistry extends InitablePersistenceBrokerDaoSupport implements PortletRegistry
{
    
    /**
     * The separator used to create a unique portlet name as
     * {portletApplication}::{portlet}
     */
    static final String PORTLET_UNIQUE_NAME_SEPARATOR = "::";

    /**
     *  
     */
    public PersistenceBrokerPortletRegistry(String repositoryPath, PortletFactory portletFactory)
    {
        super(repositoryPath);
        PortletDefinitionImpl.setPortletRegistry(this);
        PortletDefinitionImpl.setPortletFactory(portletFactory);
    }

    public Language createLanguage( Locale locale, String title, String shortTitle, String description,
            Collection keywords ) throws RegistryException
    {
        try
        {
            MutableLanguage lc = new LanguageImpl();
            lc.setLocale(locale);
            lc.setTitle(title);
            lc.setShortTitle(shortTitle);
            lc.setKeywords(keywords);
            return lc;
        }
        catch (Exception e)
        {
            throw new RegistryException("Unable to create language object.");
        }
    }

    public Collection getAllPortletDefinitions()
    {
        Criteria c = new Criteria();
        Collection list = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(PortletDefinitionImpl.class, c));
        postLoadColl(list);
        return list;
    }

    public MutablePortletApplication getPortletApplication( ObjectID id )
    {
        Criteria c = new Criteria();
        c.addEqualTo("id", new Long(id.toString()));
        MutablePortletApplication app = (MutablePortletApplication) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(PortletApplicationDefinitionImpl.class, c));
        postLoad(app);
        return app;
    }

    public MutablePortletApplication getPortletApplication( String name )
    {
        Criteria c = new Criteria();
        c.addEqualTo("name", name);
        MutablePortletApplication app = (MutablePortletApplication) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(PortletApplicationDefinitionImpl.class, c));
        postLoad(app);
        return app;
    }

    public MutablePortletApplication getPortletApplicationByIdentifier( String identifier )
    {
        Criteria c = new Criteria();
        c.addEqualTo("applicationIdentifier", identifier);
        MutablePortletApplication app = (MutablePortletApplication) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(PortletApplicationDefinitionImpl.class, c));
        postLoad(app);
        return app;
    }

    public Collection getPortletApplications()
    {
        Criteria c = new Criteria();
        Collection list = getPersistenceBrokerTemplate().getCollectionByQuery(
                QueryFactory.newQuery(PortletApplicationDefinitionImpl.class, c));
        postLoadColl(list);
        return list;
    }

    public PortletDefinitionComposite getPortletDefinitionByIdentifier( String identifier )
    {
        Criteria c = new Criteria();
        c.addEqualTo("portletIdentifier", identifier);
        PortletDefinitionComposite def = (PortletDefinitionComposite) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(PortletDefinitionImpl.class, c));
        if (def != null && def.getPortletApplicationDefinition() == null)
        {
            final String msg = "getPortletDefinitionByIdentifier() returned a PortletDefinition that has no parent PortletApplication.";
            throw new IllegalStateException(msg);
        }

        postLoad(def);
        return def;
    }

    public PortletDefinitionComposite getPortletDefinitionByUniqueName( String name )
    {
        String appName = PortletRegistryHelper.parseAppName(name);
        String portletName = PortletRegistryHelper.parsePortletName(name);

        Criteria c = new Criteria();
        c.addEqualTo("app.name", appName);
        c.addEqualTo("name", portletName);

        PortletDefinitionComposite def = (PortletDefinitionComposite) getPersistenceBrokerTemplate().getObjectByQuery(
                QueryFactory.newQuery(PortletDefinitionImpl.class, c));
        if (def != null && def.getPortletApplicationDefinition() == null)
        {
            final String msg = "getPortletDefinitionByIdentifier() returned a PortletDefinition that has no parent PortletApplication.";
            throw new IllegalStateException(msg);
        }

        postLoad(def);
        return def;
    }

    public boolean portletApplicationExists( String appIdentity )
    {
        return getPortletApplicationByIdentifier(appIdentity) != null;
    }
    
    public boolean namedPortletApplicationExists( String appName )
    {
        return getPortletApplication(appName) != null;
    }

    public boolean portletDefinitionExists( String portletName, MutablePortletApplication app )
    {
        return getPortletDefinitionByUniqueName(app.getName() + "::" + portletName) != null;
    }

    public boolean portletDefinitionExists( String portletIdentity )
    {
        return getPortletDefinitionByIdentifier(portletIdentity) != null;
    }

    public void registerPortletApplication( PortletApplicationDefinition newApp ) throws RegistryException
    {
        getPersistenceBrokerTemplate().store(newApp);
    }

    public void removeApplication( PortletApplicationDefinition app ) throws RegistryException
    {
        getPersistenceBrokerTemplate().delete(app);
        
        String appNodePath = MutablePortletApplication.PREFS_ROOT + "/" +((MutablePortletApplication)app).getName();
        try
        {
            if(Preferences.systemRoot().nodeExists(appNodePath))
            {                   
                Preferences node = Preferences.systemRoot().node(appNodePath);
               // log.info("Removing Application preference node "+node.absolutePath());
                node.removeNode();
            }
        }
        catch (BackingStoreException e)
        {
           throw new RegistryException(e.toString(), e);
        }

    }

    public void updatePortletApplication( PortletApplicationDefinition app ) throws RegistryException
    {
        getPersistenceBrokerTemplate().store(app);

    }

    private void postLoad( Object obj )
    {
        if (obj != null)
        {

            if (obj instanceof Support)
            {
                try
                {
                    ((Support) obj).postLoad(obj);
                }
                catch (Exception e)
                {
                }
            }
        }

    }

    private void postLoadColl( Collection coll )
    {

        if (coll != null && !coll.isEmpty())
        {
            Iterator itr = coll.iterator();
            Object test = itr.next();
            if (test instanceof Support)
            {
                Support testSupport = (Support) test;
                try
                {
                    testSupport.postLoad(testSupport);
                }
                catch (Exception e1)
                {

                }
                while (itr.hasNext())
                {
                    Support support = (Support) itr.next();
                    try
                    {
                        support.postLoad(support);
                    }
                    catch (Exception e)
                    {
                    }
                }
            }

        }

    }

    public void savePortletDefinition( PortletDefinition portlet ) throws FailedToStorePortletDefinitionException
    {
        try
        {
            getPersistenceBrokerTemplate().store(portlet);
        }
        catch (DataAccessException e)
        {
            
           throw new FailedToStorePortletDefinitionException(portlet, e);
        }

    }
    
    
}