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
package org.apache.jetspeed.components.portletregistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.impl.LanguageImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.StoreablePortletDefinitionDelegate;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

import org.apache.jetspeed.om.impl.PortletDisplayNameImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionLocalizedFieldImpl;
import org.apache.jetspeed.om.portlet.impl.ContentTypeImpl;
import org.apache.jetspeed.om.impl.PortletInitParameterImpl;
import org.apache.jetspeed.om.preference.impl.DefaultPreferenceImpl;
import org.apache.jetspeed.om.impl.SecurityRoleRefImpl;
import org.apache.jetspeed.om.impl.PortletDescriptionImpl;

/**
 * <p>
 * PortletRegistryComponentImpl
 * </p>
 * <p>
 * Component for accessing the Portlet registry.
 * </p>
 * 
 * <table border="1">
 * <tr>
 * <th>Configuration Key</th>
 * <th>Optional?</th>
 * <th>Default</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>persistence.store.name</td>
 * <td>true</td>
 * <td>jetspeed</td>
 * <td>Name of the persistence store that will be used for persistence operations.</td>
 * </tr>
 * </table>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $ $
 *  
 */
public class PortletRegistryComponentImpl implements PortletRegistryComponent
{
    private static final Log log = LogFactory.getLog(PortletRegistryComponentImpl.class);
    protected static final String KEY_STORE_NAME = "persistence.store.name";
    private PersistenceStoreContainer storeContainer;
    private String jetspeedStoreName;
    private Class portletDefClass;
    private Class portletAppClass;

    /**
     *  
     */
    public PortletRegistryComponentImpl(PersistenceStoreContainer storeContainer, String keyStoreName) throws RegistryException
    {
        if (storeContainer == null)
        {
            throw new IllegalArgumentException("storeContainer cannot be null for PortletRegistryComponentImpl");
        }
        this.storeContainer = storeContainer;
        jetspeedStoreName = keyStoreName;
        portletDefClass = PortletDefinitionImpl.class;
        portletAppClass = PortletApplicationDefinitionImpl.class;
    }

    /**
     * <p>
     * createLanguage
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#createLanguage(java.util.Locale, java.lang.String,
     *      java.lang.String, java.lang.String, java.util.Collection)
     * @param locale
     * @param title
     * @param shortTitle
     * @param description
     * @param keywords
     * @return @throws
     *         RegistryException
     */
    public Language createLanguage(Locale locale, String title, String shortTitle, String description, Collection keywords)
        throws RegistryException
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

    /**
     * <p>
     * getAllPortletDefinitions
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#getAllPortletDefinitions()
     * @return
     */
    public List getAllPortletDefinitions()
    {
        PersistenceStore store = getPersistenceStore();
        prepareTransaction(store);
        return new ArrayList(store.getExtent(portletDefClass));
    }

    public PersistenceStore getPersistenceStore()
    {
        return storeContainer.getStoreForThread(jetspeedStoreName);
    }

    /**
     * <p>
     * getPortletApplication
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#getPortletApplication(org.apache.pluto.om.common.ObjectID)
     * @param id
     * @return
     */
    public MutablePortletApplication getPortletApplication(ObjectID id)
    {
        PersistenceStore store = getPersistenceStore();
        prepareTransaction(store);
        Filter filter = store.newFilter();
        filter.addEqualTo("id", id);
        Object query = store.newQuery(portletAppClass, filter);
        return (MutablePortletApplication) store.getObjectByQuery(query);
    }

    private void prepareTransaction(PersistenceStore store)
    {
        if (store.getTransaction() == null || !store.getTransaction().isOpen())
        {
            store.getTransaction().begin();
        }
    }

    /**
     * <p>
     * getPortletApplication
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#getPortletApplication(java.lang.String)
     * @param name
     * @return
     */
    public MutablePortletApplication getPortletApplication(String name)
    {
        PersistenceStore store = getPersistenceStore();
        prepareTransaction(store);
        Filter filter = store.newFilter();
        filter.addEqualTo("name", name);
        Object query = store.newQuery(portletAppClass, filter);
        return (MutablePortletApplication) store.getObjectByQuery(query);
    }

    /**
     * <p>
     * getPortletApplicationByIdentifier
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#getPortletApplicationByIdentifier(java.lang.String)
     * @param ident
     * @return
     */
    public MutablePortletApplication getPortletApplicationByIdentifier(String ident)
    {
        PersistenceStore store = getPersistenceStore();
        prepareTransaction(store);
        Filter filter = store.newFilter();
        filter.addEqualTo("applicationIdentifier", ident);
        Object query = store.newQuery(portletAppClass, filter);
        return (MutablePortletApplication) store.getObjectByQuery(query);
    }

    /**
     * <p>
     * getPortletApplications
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#getPortletApplications()
     * @return
     */
    public List getPortletApplications()
    {
        PersistenceStore store = getPersistenceStore();
        prepareTransaction(store);
        return new ArrayList(store.getExtent(portletAppClass));
    }

    /**
     * <p>
     * getPortletDefinitionByIndetifier
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#getPortletDefinitionByIdentifier(java.lang.String)
     * @param ident
     * @return
     */
    public PortletDefinitionComposite getPortletDefinitionByIdentifier(String ident)
    {
        PersistenceStore store = getPersistenceStore();
        prepareTransaction(store);
        Filter filter = store.newFilter();
        filter.addEqualTo("portletIdentifier", ident);
        Object query = store.newQuery(portletDefClass, filter);
        PortletDefinitionComposite portlet = (PortletDefinitionComposite) store.getObjectByQuery(query);
        if (portlet != null)
        {
            if (portlet.getPortletApplicationDefinition() == null)
            {
                final String msg =
                    "getPortletDefinitionByIdentifier() returned a PortletDefinition that has no parent PortletApplication.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
            return getStoreableInstance(portlet);
        }
        else
        {
            return null;
        }
    }

    /**
     * <p>
     * getPortletDefinitionByUniqueName
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#getPortletDefinitionByUniqueName(java.lang.String)
     * @param name
     * @return
     */
    public PortletDefinitionComposite getPortletDefinitionByUniqueName(String name)
    {
        PersistenceStore store = getPersistenceStore();
        prepareTransaction(store);

        //parse out names
        int split = name.indexOf("::");
        if (split < 1)
        {
            throw new IllegalArgumentException(
                "The unique portlet name, \"" + name + "\";  is not well formed.  No \"::\" delimiter was found.");
        }
        String appName = name.substring(0, split);
        String portletName = name.substring((split + 2), name.length());

        // build filter
        Filter filter = store.newFilter();
        filter.addEqualTo("app.name", appName);
        filter.addEqualTo("name", portletName);
        Object query = store.newQuery(portletDefClass, filter);
        PortletDefinitionComposite portlet = (PortletDefinitionComposite) store.getObjectByQuery(query);
        if (portlet != null)
        {
            if (portlet.getPortletApplicationDefinition() == null)
            {
                filter = store.newFilter();
                filter.addEqualTo("name", appName);
                query = store.newQuery(portletAppClass, filter);
                MutablePortletApplication app = (MutablePortletApplication) store.getObjectByQuery(query);
                if (null == app)
                {
                    final String msg =
                        "getPortletDefinitionByUniqueName() returned a PortletDefinition that has no parent PortletApplication.";
                    log.error(msg);
                    throw new IllegalStateException(msg);
                }
                portlet.setPortletApplicationDefinition(app);
            }
            return getStoreableInstance(portlet);
        }
        else
        {
            return null;
        }
    }

    /**
     * <p>
     * portletApplicationExists
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#portletApplicationExists(java.lang.String)
     * @param appIdentity
     * @return
     */
    public boolean portletApplicationExists(String appIdentity)
    {
        return getPortletApplicationByIdentifier(appIdentity) != null;
    }

    /**
     * <p>
     * portletDefinitionExists
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#portletDefinitionExists(java.lang.String)
     * @param portletIndentity
     * @return
     */
    public boolean portletDefinitionExists(String portletIdentity)
    {
        return getPortletDefinitionByIdentifier(portletIdentity) != null;
    }

    /**
     * <p>
     * portletDefinitionExists
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#portletDefinitionExists(java.lang.String,
     *      org.apache.jetspeed.om.common.portlet.MutablePortletApplication)
     * @param portletName
     * @param app
     * @return
     */
    public boolean portletDefinitionExists(String portletName, MutablePortletApplication app)
    {
        return getPortletDefinitionByUniqueName(app.getName() + "::" + portletName) != null;
    }

    /**
     * <p>
     * registerPortletApplication
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#registerPortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     * @param newApp
     * @throws RegistryException
     */
    public void registerPortletApplication(PortletApplicationDefinition newApp) throws RegistryException
    {
        PersistenceStore store = getPersistenceStore();
        prepareTransaction(store);
        try
        {
            store.makePersistent(newApp);
            store.getTransaction().checkpoint();
        }
        catch (LockFailedException e)
        {
            throw new RegistryException("Unable to lock PortletApplicaiton for makePersistent: " + e.toString(), e);
        }
    }

    /**
     * <p>
     * removeApplication
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#removeApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     * @param app
     * @throws TransactionStateException
     */
    public void removeApplication(PortletApplicationDefinition app) throws RegistryException
    {
        PersistenceStore store = getPersistenceStore();
        prepareTransaction(store);

        Filter filter = store.newFilter();
        try
        {
            Iterator portlets = app.getPortletDefinitionList().iterator();
            while (portlets.hasNext())
            {
                PortletDefinitionImpl curPortlet = (PortletDefinitionImpl) portlets.next();

                filter.addEqualTo("parentId", curPortlet.getId());
                store.deleteAll(store.newQuery(PortletDefinitionLocalizedFieldImpl.class, filter));
                store.getTransaction().checkpoint();

                filter = store.newFilter();
                filter.addEqualTo("parentId", curPortlet.getId());
                store.deleteAll(store.newQuery(PortletDisplayNameImpl.class, filter));
                store.getTransaction().checkpoint();

                filter = store.newFilter();
                filter.addEqualTo("portletId", curPortlet.getId());
                store.deleteAll(store.newQuery(LanguageImpl.class, filter));
                store.getTransaction().checkpoint();

                filter = store.newFilter();
                filter.addEqualTo("portletId", curPortlet.getId());
                store.deleteAll(store.newQuery(ContentTypeImpl.class, filter));
                store.getTransaction().checkpoint();

                filter = store.newFilter();
                filter.addEqualTo("parentId", curPortlet.getId());
                store.deleteAll(store.newQuery(PortletInitParameterImpl.class, filter));
                store.getTransaction().checkpoint();

                filter = store.newFilter();
                filter.addEqualTo("parentId", curPortlet.getId());
                store.deleteAll(store.newQuery(DefaultPreferenceImpl.class, filter));
                store.getTransaction().checkpoint();

                filter = store.newFilter();
                filter.addEqualTo("portletId", curPortlet.getId());
                store.deleteAll(store.newQuery(SecurityRoleRefImpl.class, filter));
                store.getTransaction().checkpoint();

                filter = store.newFilter();
                filter.addEqualTo("parentId", curPortlet.getId());
                store.deleteAll(store.newQuery(PortletDescriptionImpl.class, filter));
                store.getTransaction().checkpoint();

                filter = store.newFilter();
                filter.addEqualTo("id", curPortlet.getId());
                store.deleteAll(store.newQuery(PortletDefinitionImpl.class, filter));
                store.getTransaction().checkpoint();
            }
            filter = store.newFilter();
            filter.addEqualTo("id", app.getId());
            store.deleteAll(store.newQuery(MutablePortletApplication.class, filter));
            store.getTransaction().checkpoint();
        }
        catch (LockFailedException e)
        {
            throw new RegistryException("Unable to lock PortletApplication for deletion: " + e.toString(), e);
        }
    }

    /**
     * <p>
     * updatePortletApplication
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#updatePortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     * @param app
     * @throws RegistryException
     */
    public void updatePortletApplication(PortletApplicationDefinition app) throws RegistryException
    {
        try
        {
            PersistenceStore store = getPersistenceStore();
            prepareTransaction(store);
            store.lockForWrite(app);
            store.getTransaction().checkpoint();
        }
        catch (LockFailedException e)
        {
            throw new RegistryException("Unable to lock PortletApplicaiton for update: " + e.toString(), e);
        }
    }

    public PortletDefinitionComposite getStoreableInstance(PortletDefinitionComposite portlet)
    {
        if (portlet != null)
        {
            return new StoreablePortletDefinitionDelegate(portlet, getPersistenceStore());
        }
        else
        {
            return null;
        }
    }
}
