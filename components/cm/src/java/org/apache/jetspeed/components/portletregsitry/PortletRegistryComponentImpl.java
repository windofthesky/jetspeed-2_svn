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
package org.apache.jetspeed.components.portletregsitry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.components.omfactory.OMFactory;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.components.persistence.store.PersistenceStoreContainer;
import org.apache.jetspeed.components.persistence.store.impl.LockFailedException;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.portlet.PortletDefinition;

/**
 * <p>
 * PortletRegistryComponentImpl
 * </p>
 * <p>
 * Component for accessing the Portlet registry.
 * </p>
 * 
 *  <table border="1">
 *    <tr>
 *     <th>Configuration Key</th>
 *     <th>Optional?</th>
 *     <th>Default</th>
 *     <th>Description</th>
 *    </tr>
 *    <tr>
 *     <td>
 *      persistence.store.name
 *     </td>
 *     <td>
 *      true 
 *     </td>
 *     <td>
 *      jetspeed
 *     </td>
 *     <td>
 *      Name of the persistence store that will be  
 *      used for persistence operations.
 *     </td>
 *    </tr>
 *   </table>
 *

 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $ $
 *
 */
public class PortletRegistryComponentImpl implements org.apache.jetspeed.components.portletregsitry.PortletRegistryComponent
{

    protected static final String KEY_STORE_NAME = "persistence.store.name";
    private PersistenceStoreContainer storeContainer;
        private String jetspeedStoreName;
    private Class portletDefClass;
    private Class portletAppClass;
    private OMFactory omFactory;

    /**
     * 
     */
    public PortletRegistryComponentImpl(PersistenceStoreContainer storeContainer, String keyStoreName, OMFactory omFactory)
        throws RegistryException
    {
        if (storeContainer == null)
        {
            throw new IllegalArgumentException("storeContainer cannot be null for PortletRegistryComponentImpl");
        }



        if (omFactory == null)
        {
            throw new IllegalArgumentException("omFactory cannot be null for PortletRegistryComponentImpl");
        }

        this.storeContainer = storeContainer;
        
        this.omFactory = omFactory;
        jetspeedStoreName = keyStoreName;

        try
        {
            portletDefClass = omFactory.getImplementation(PortletDefinition.class);
            portletAppClass = omFactory.getImplementation(PortletApplicationDefinition.class);
        }
        catch (ClassNotFoundException e)
        {
            throw new RegistryException("Unable to identify implementation classes " + e.toString(), e);
        }
    }

    /** 
     * <p>
     * createLanguage
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#createLanguage(java.util.Locale, java.lang.String, java.lang.String, java.lang.String, java.util.Collection)
     * @param locale
     * @param title
     * @param shortTitle
     * @param description
     * @param keywords
     * @return
     * @throws RegistryException
     */
    public Language createLanguage(Locale locale, String title, String shortTitle, String description, Collection keywords)
        throws RegistryException
    {

        try
        {
            MutableLanguage lc = (MutableLanguage) omFactory.newInstance(Language.class);
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

    protected PersistenceStore getPersistenceStore()
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
        if (!store.getTransaction().isOpen())
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
     * getPortletApplicationByIndetifier
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#getPortletApplicationByIndetifier(java.lang.String)
     * @param ident
     * @return
     */
    public MutablePortletApplication getPortletApplicationByIndetifier(String ident)
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
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#getPortletDefinitionByIndetifier(java.lang.String)
     * @param ident
     * @return
     */
    public PortletDefinitionComposite getPortletDefinitionByIndetifier(String ident)
    {
        PersistenceStore store = getPersistenceStore();
        prepareTransaction(store);
        Filter filter = store.newFilter();
        filter.addEqualTo("portletIdentifier", ident);
        Object query = store.newQuery(portletDefClass, filter);
        return (PortletDefinitionComposite) store.getObjectByQuery(query);
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
        PortletDefinitionComposite pdc = (PortletDefinitionComposite) store.getObjectByQuery(query);

        return pdc;
    }

    /** 
     * <p>
     * portletApplicationExists
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#portletApplicationExists(java.lang.String)
     * @param appIentity
     * @return
     */
    public boolean portletApplicationExists(String appIentity)
    {
        return getPortletApplicationByIndetifier(appIentity) != null;
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
    public boolean portletDefinitionExists(String portletIndentity)
    {
        return getPortletDefinitionByIndetifier(portletIndentity) != null;
    }

    /** 
     * <p>
     * portletDefinitionExists
     * </p>
     * 
     * @see org.apache.jetspeed.registry.PortletRegistryComponentImpl#portletDefinitionExists(java.lang.String, org.apache.jetspeed.om.common.portlet.MutablePortletApplication)
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

        try
        {
            store.deletePersistent(app);
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

}
