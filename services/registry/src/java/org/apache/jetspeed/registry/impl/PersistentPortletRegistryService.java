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
package org.apache.jetspeed.registry.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.cps.BaseCommonService;
import org.apache.jetspeed.cps.CPSInitializationException;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.exception.RegistryException;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.impl.PortletInitParameterImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.preference.impl.DefaultPreferenceImpl;
import org.apache.jetspeed.persistence.LookupCriteria;
import org.apache.jetspeed.persistence.PersistencePlugin;
import org.apache.jetspeed.persistence.PersistenceService;
import org.apache.jetspeed.persistence.TransactionStateException;
import org.apache.jetspeed.registry.JetspeedPortletRegistry;
import org.apache.jetspeed.registry.PortletRegistryService;
import org.apache.jetspeed.util.ArgUtil;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class PersistentPortletRegistryService extends BaseCommonService implements PortletRegistryService
{
    private PersistencePlugin plugin;

    private PersistencePlugin originalPlugin;

    private String originalAlias;

    private static final Log log = LogFactory.getLog(PortletRegistryService.class);

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws CPSInitializationException
    {
        if (!isInitialized())
        {
            // PersistenceService ps = (PersistenceService) ServiceUtil.getServiceByName(PersistenceService.SERVICE_NAME);
            PersistenceService ps = (PersistenceService) CommonPortletServices.getPortalService(PersistenceService.SERVICE_NAME);
            String pluginName = getConfiguration().getString("persistence.plugin.name", "jetspeed");

            plugin = ps.getPersistencePlugin(pluginName);

            setInit(true);
        }

    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getAllPortletDefinitions()
     */
    public List getAllPortletDefinitions()
    {
        try
        {
            LookupCriteria crit = plugin.newLookupCriteria();
            Collection pColl =
                plugin.getCollectionByQuery(PortletDefinitionImpl.class, plugin.generateQuery(PortletDefinitionImpl.class, crit));
            return new ArrayList(pColl);
        }
        catch (Throwable e)
        {
            log.fatal("Unable retrieve portlet definitions.", e);
            throw (RuntimeException) e;
        }

    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletApplication(org.apache.pluto.om.common.ObjectID)
     */
    public MutablePortletApplication getPortletApplication(ObjectID id)
    {

        ArgUtil.notNull(new Object[] { id }, new String[] { "id" }, "getPortletApplication(ObjectID)");

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("id", id);
        Object query = plugin.generateQuery(PortletApplicationDefinitionImpl.class, c);
        return (MutablePortletApplication) plugin.getObjectByQuery(PortletApplicationDefinitionImpl.class, query);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletApplication(java.lang.String)
     */
    public MutablePortletApplication getPortletApplication(String name)
    {

        ArgUtil.notNull(new Object[] { name }, new String[] { "name" }, "getPortletApplication(String)");

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("name", name);
        Object query = plugin.generateQuery(PortletApplicationDefinitionImpl.class, c);
        return (MutablePortletApplication) plugin.getObjectByQuery(PortletApplicationDefinitionImpl.class, query);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletApplications()
     */
    public List getPortletApplications()
    {
        try
        {
            Object query = plugin.generateQuery(PortletApplicationDefinitionImpl.class, plugin.newLookupCriteria());
            return new ArrayList(plugin.getCollectionByQuery(PortletApplicationDefinitionImpl.class, query));
        }
        catch (RuntimeException e)
        {
            log.fatal("failed to retreive portlet application list: " + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#registerPortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public void registerPortletApplication(PortletApplicationDefinition newApp) throws RegistryException
    {

        ArgUtil.notNull(
            new Object[] { newApp },
            new String[] { "newApp" },
            "registerPortletApplication(PortletApplicationDefinition)");

        // use default plugin to register this application
        registerPortletApplication(newApp, plugin);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#updatePortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public void updatePortletApplication(PortletApplicationDefinition app) throws RegistryException
    {
        try
        {
            plugin.prepareForUpdate(app);
        }
        catch (TransactionStateException e)
        {
            throw new RegistryException(e.toString(), e);
        }

    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#removeApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public void removeApplication(PortletApplicationDefinition app) throws RegistryException
    {
        ArgUtil.notNull(new Object[] { app }, new String[] { "app" }, "removeApplication(PortletApplicationDefinition)");

        try
        {
            log.info("Removing portlet application " + ((MutablePortletApplication) app).getName());
            plugin.prepareForDelete(app);
            plugin.invalidateObject(app);
        }
        catch (TransactionStateException e)
        {
			throw new RegistryException(e.toString(), e);
        }
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletDefinition(java.lang.String)
     */
    public PortletDefinitionComposite getPortletDefinitionByUniqueName(String name)
    {

        ArgUtil.notNull(new Object[] { name }, new String[] { "name" }, "getPortletDefinitionByUniqueName(String)");

        //parse out names
        int split = name.indexOf("::");
        if (split < 1)
        {
            throw new IllegalArgumentException(
                "The unique portlet name, \"" + name + "\";  is not well formed.  No \"::\" delimiter was found.");
        }

        String appName = name.substring(0, split);
        String portletName = name.substring((split + 2), name.length());

        // build criteria
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("app.name", appName);
        c.addEqualTo("name", portletName);
        Object query = plugin.generateQuery(PortletDefinitionImpl.class, c);
        PortletDefinitionComposite pdc = (PortletDefinitionComposite) plugin.getObjectByQuery(PortletDefinitionImpl.class, query);

        return pdc;
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#createLanguage(java.util.Locale, java.lang.String, java.lang.String, java.lang.String)
     */
    public Language createLanguage(Locale locale, String title, String shortTitle, String description, Collection keywords)
        throws RegistryException
    {

        ArgUtil.notNull(
            new Object[] { locale },
            new String[] { "locale" },
            "createLanguage(Locale locale, String title, String shortTitle, String description, Collection keywords");

        MutableLanguage lc;
        try
        {
            lc = (MutableLanguage) getNewObjectInstance(Language.class, true);
        }
        catch (TransactionStateException e)
        {
            throw new RegistryException("Unable to add Language to a transaction.");
        }
        lc.setLocale(locale);
        lc.setTitle(title);
        lc.setShortTitle(shortTitle);
        lc.setKeywords(keywords);

        return lc;
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newPreference()
     */
    public PreferenceComposite newPreference()
    {
        return new DefaultPreferenceImpl();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletApplicationByIndetifier(java.lang.String)
     */
    public MutablePortletApplication getPortletApplicationByIndetifier(String ident)
    {
        ArgUtil.notNull(new Object[] { ident }, new String[] { "ident" }, "getPortletApplicationByIndetifier(String ident)");

        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("applicationIdentifier", ident);
        Object query = plugin.generateQuery(PortletApplicationDefinitionImpl.class, c);
        return (MutablePortletApplication) plugin.getObjectByQuery(PortletApplicationDefinitionImpl.class, query);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletDefinitionByIndetifier(java.lang.String)
     */
    public PortletDefinitionComposite getPortletDefinitionByIndetifier(String ident)
    {
        ArgUtil.notNull(new Object[] { ident }, new String[] { "ident" }, "getPortletDefinitionByIndetifier(String ident)");

        LookupCriteria c = plugin.newLookupCriteria();
        
        c.addEqualTo("portletIdentifier", ident);
        Object query = plugin.generateQuery(PortletDefinitionImpl.class, c);
        PortletDefinitionComposite pdc = (PortletDefinitionComposite) plugin.getObjectByQuery(PortletDefinitionImpl.class, query);

        return pdc;        
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#portletApplicationExists(java.lang.String)
     */
    public boolean portletApplicationExists(String appIentity)
    {
        ArgUtil.notNull(new Object[] { appIentity }, new String[] { "appIentity" }, "portletApplicationExists(String appIentity)");

        return getPortletApplicationByIndetifier(appIentity) != null;
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#portletDefinitionExists(java.lang.String)
     */
    public boolean portletDefinitionExists(String portletIndentity)
    {
        ArgUtil.notNull(
            new Object[] { portletIndentity },
            new String[] { "portletIndentity" },
            "portletDefinitionExists(String portletIndentity)");

        return getPortletDefinitionByIndetifier(portletIndentity) != null;
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#portletDefinitionExists(java.lang.String, org.apache.jetspeed.om.common.portlet.PortletApplicationComposite)
     */
    public boolean portletDefinitionExists(String portletName, MutablePortletApplication app)
    {
        ArgUtil.notNull(
            new Object[] { portletName, app },
            new String[] { "portletName", "app" },
            "portletDefinitionExists(String portletName, MutablePortletApplication app)");

        return app.getPortletDefinitionByName(portletName) != null;
    }

    /**
     * Uses a specific pluging to register/deploy the portlet application
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#registerPortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    protected void registerPortletApplication(PortletApplicationDefinition newApp, PersistencePlugin usePlugin)
        throws RegistryException
    {
        ArgUtil.notNull(
            new Object[] { newApp, usePlugin },
            new String[] { "newApp", "usePlugin" },
            "registerPortletApplication(PortletApplicationDefinition newApp, PersistencePlugin usePlugin)");

        LookupCriteria c = usePlugin.newLookupCriteria();
        MutablePortletApplication pac = (MutablePortletApplication) newApp;
        c.addEqualTo("applicationIdentifier", pac.getApplicationIdentifier());
        Object test =
            usePlugin.getObjectByQuery(
                PortletApplicationDefinitionImpl.class,
                usePlugin.generateQuery(PortletApplicationDefinitionImpl.class, c));

        if (test != null)
        {
            String message = "A Portlet Application with the identifier, " + pac.getApplicationIdentifier() + " already exists.";
            log.error(message);
            throw new RegistryException(message);
        }
        else
        {
        	
			try
			{
				plugin.makePersistent(newApp);
			}
			catch (TransactionStateException e1)
			{
				String msg = "Failed to make new portlet application persistent: "+e1.toString();
                log.error(msg, e1);
				throw new RegistryException(msg, e1);
			}
// We should never "hide" commiting a transaction like this.  Transaction should
// be handled out in the open were we can handle exceptions better.  We could 
// eventually use AOP to "hide" transactions completely yet still be able handle
// higher level exceptions and transaction rollbacks correctly.        	
//            try
//            {
//                // commit the transaction     
//                plugin.commitTransaction();
//            }
//            catch (TransactionStateException e)
//            {
//                try
//                {
//                    plugin.rollbackTransaction();
//                }
//                catch (TransactionStateException e1)
//                {
//                    log.error("Failed to rollback transaction.", e);
//                }
//                String msg = "Unable to register new portlet application.";
//                log.error(msg, e);
//                throw new RegistryException(msg, e);
//            }
        }

    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#beginTransaction()
     */
    public void beginTransaction() throws TransactionStateException
    {
        plugin.beginTransaction();

    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#commitTransaction()
     */
    public void commitTransaction() throws TransactionStateException
    {
        plugin.commitTransaction();

    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#rollbackTransaction()
     */
    public void rollbackTransaction() throws TransactionStateException
    {
        plugin.rollbackTransaction();

    }

    /**
     * @see org.apache.jetspeed.registry.PortletRegistryService#getPorletInitParameters(org.apache.jetspeed.persistence.LookupCriteria)
     */
    public List getPortletInitParameters(LookupCriteria criteria)
    {
        if (criteria == null)
        {
            criteria = plugin.newLookupCriteria();
        }

        Object query = plugin.generateQuery(PortletInitParameterImpl.class, criteria);
        return new ArrayList(plugin.getCollectionByQuery(PortletDefinitionImpl.class, query));
    }

    /**
     * @see org.apache.jetspeed.registry.PortletRegistryService#clearCache()
     */
    public void clearCache()
    {
        plugin.clearCache();

    }

    /**
     * @see org.apache.jetspeed.registry.PortletRegistryService#writeLock(java.lang.Object)
     */
    public void writeLock(Object object) throws TransactionStateException
    {
        plugin.prepareForUpdate(object);

    }

    public Object getNewObjectInstance(String interfaze, boolean persistent) throws RegistryException, TransactionStateException
    {
        String className = "undefined";
        try
        {
            className = configuration.getString("implementation." + interfaze);
            if (className == null)
            {
                throw new RegistryException("No implementation has been defined for " + interfaze);
            }
            Class implClazz = Class.forName(className);
            Object newInstance = implClazz.newInstance();
            if (persistent)
            {
                // plugin.makePersistent(newInstance);
            }
            return newInstance;
        }
        catch (Exception e)
        {
            if (e instanceof TransactionStateException)
            {
                throw (TransactionStateException) e;
            }
            throw new RegistryException("Unable create new " + interfaze + ".  " + e.toString(), e);
        }
    }

    public Object getNewObjectInstance(Class interfaze, boolean persistent) throws RegistryException, TransactionStateException
    {
        return getNewObjectInstance(interfaze.getName(), persistent);
    }

    /**
     * @see org.apache.jetspeed.registry.PortletRegistryService#makeDirty(java.lang.Object)
     */
    public void makeDirty(Object object) throws TransactionStateException
    {
        plugin.markDirty(object);

    }

}
