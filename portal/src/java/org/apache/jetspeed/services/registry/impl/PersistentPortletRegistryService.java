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
package org.apache.jetspeed.services.registry.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.BaseService;
import org.apache.fulcrum.InitializationException;
import org.apache.jetspeed.exception.RegistryException;
import org.apache.jetspeed.om.common.BaseLanguage;
import org.apache.jetspeed.om.common.BasePreference;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.PreferenceComposite;
import org.apache.jetspeed.om.common.portlet.BaseContentType;
import org.apache.jetspeed.om.common.portlet.BasePortletApplicationDefinition;
import org.apache.jetspeed.om.common.portlet.BasePortletDefinition;
import org.apache.jetspeed.om.common.portlet.ContentTypeComposite;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.servlet.BaseWebApplicationDefinition;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.services.perisistence.LookupCriteria;
import org.apache.jetspeed.services.perisistence.PersistencePlugin;
import org.apache.jetspeed.services.perisistence.PersistenceService;
import org.apache.jetspeed.services.registry.PortletRegistryService;
import org.apache.jetspeed.util.ServiceUtil;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;


/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class PersistentPortletRegistryService extends BaseService implements PortletRegistryService
{
    private PersistencePlugin plugin;
    
    private PersistencePlugin originalPlugin;
    
    private String originalAlias;

    private static final Log log = LogFactory.getLog(PortletRegistryService.class);

    /**
     * @see org.apache.fulcrum.Service#init()
     */
    public void init() throws InitializationException
    {
        if (!isInitialized())
        {
            PersistenceService ps =
                (PersistenceService) ServiceUtil.getServiceByName(PersistenceService.SERVICE_NAME);

            String pluginName = getConfiguration().getString("persistence.plugin.name", "jetspeed");

            plugin = ps.getPlugin(pluginName);

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
            Collection pColl = plugin.getCollectionByQuery(BasePortletDefinition.class, 
                                                           plugin.generateQuery(BasePortletDefinition.class, crit));
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
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("id", id);
        Object query = plugin.generateQuery(BasePortletApplicationDefinition.class, c);
        return (MutablePortletApplication) plugin.getObjectByQuery(
            BasePortletApplicationDefinition.class,
            query);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletApplication(java.lang.String)
     */
    public MutablePortletApplication getPortletApplication(String name)
    {
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("name", name);
        Object query = plugin.generateQuery(BasePortletApplicationDefinition.class, c);
        return (MutablePortletApplication) plugin.getObjectByQuery(
            BasePortletApplicationDefinition.class,
            query);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletApplications()
     */
    public List getPortletApplications()
    {
        try
        {
            Object query =
                plugin.generateQuery(
                    BasePortletApplicationDefinition.class,
                    plugin.newLookupCriteria());
            return new ArrayList(
                plugin.getCollectionByQuery(BasePortletApplicationDefinition.class, query));
        }
        catch (RuntimeException e)
        {            
           log.fatal("failed to retreive portlet application list: "+e.getMessage(), e);
           throw e;
        }
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newContentType()
     */
    public ContentTypeComposite newContentType()
    {

        return new BaseContentType();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newLanguage()
     */
    public MutableLanguage newLanguage()
    {
        return new BaseLanguage();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newPortletApplication()
     */
    public MutablePortletApplication newPortletApplication()
    {
        return new BasePortletApplicationDefinition();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newPortletDefinition()
     */
    public PortletDefinitionComposite newPortletDefinition()
    {
        return new BasePortletDefinition();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newWebApplication()
     */
    public MutableWebApplication newWebApplication()
    {
        return new BaseWebApplicationDefinition();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#registerPortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public void registerPortletApplication(PortletApplicationDefinition newApp)
        throws RegistryException
    {
        // use default plugin to register this application
        registerPortletApplication(newApp, plugin);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#updatePortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public void updatePortletApplication(PortletApplicationDefinition app)
    {
        plugin.update(app);

    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#removeApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public void removeApplication(PortletApplicationDefinition app)
    {
    	System.out.println("Removing portlet application "+app);    	
        plugin.delete(app);        

    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletDefinition(java.lang.String)
     */
    public PortletDefinitionComposite getPortletDefinitionByUniqueName(String name)
    {
        // TODO: we may need to lookup on appname + name
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("name", name);
        Object query = plugin.generateQuery(BasePortletDefinition.class, c);
        return (PortletDefinitionComposite) plugin.getObjectByQuery(
            BasePortletDefinition.class,
            query);        
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#createLanguage(java.util.Locale, java.lang.String, java.lang.String, java.lang.String)
     */
    public Language createLanguage(
        Locale locale,
        String title,
        String shortTitle,
        String description,
        Collection keywords)
    {
       MutableLanguage lc = newLanguage();
       lc.setLocale(locale);       
       lc.setTitle( title);
       lc.setShortTitle(shortTitle);
       lc.setKeywords(keywords);
       
       return lc;
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newPreference()
     */
    public PreferenceComposite newPreference()
    {        
        return new BasePreference();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletApplicationByIndetifier(java.lang.String)
     */
    public MutablePortletApplication getPortletApplicationByIndetifier(String ident)
    {
        LookupCriteria c = plugin.newLookupCriteria();
        c.addEqualTo("applicationIdentifier", ident);
        Object query = plugin.generateQuery(BasePortletApplicationDefinition.class, c);
        return (MutablePortletApplication) plugin.getObjectByQuery(
            BasePortletApplicationDefinition.class,
            query);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletDefinitionByIndetifier(java.lang.String)
     */
    public PortletDefinitionComposite getPortletDefinitionByIndetifier(String ident)
    {
        Iterator appItr = getAllPortletDefinitions().iterator();
        ArrayList portlets = new ArrayList();
        while(appItr.hasNext())
        {
            PortletDefinitionComposite pd = (PortletDefinitionComposite) appItr.next();
            
            if(pd.getPortletIdentifier() != null && pd.getPortletIdentifier().equals(ident))
            {
                return pd;
            }
        }
        return null;
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#portletApplicationExists(java.lang.String)
     */
    public boolean portletApplicationExists(String appIentity)
    {        
        return getPortletApplicationByIndetifier(appIentity) != null;
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#portletDefinitionExists(java.lang.String)
     */
    public boolean portletDefinitionExists(String portletIndentity)
    {        
        return getPortletDefinitionByIndetifier(portletIndentity) != null;
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#portletDefinitionExists(java.lang.String, org.apache.jetspeed.om.common.portlet.PortletApplicationComposite)
     */
    public boolean portletDefinitionExists(String portletName, MutablePortletApplication app)
    {
        return app.getPortletDefinitionByName(portletName) != null;
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#registerPortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition, java.lang.String)
     */
    public void registerPortletApplication(PortletApplicationDefinition newApp, String system)
        throws RegistryException
    {
        PersistenceService ps =
                (PersistenceService) ServiceUtil.getServiceByName(PersistenceService.SERVICE_NAME);
        PersistencePlugin usePlugin = ps.getPlugin(system);
        registerPortletApplication(newApp, usePlugin);        
    }
    
    /**
     * Uses a specific pluging to register/deploy the portlet application
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#registerPortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    protected void registerPortletApplication(PortletApplicationDefinition newApp, PersistencePlugin usePlugin)
        throws RegistryException
    {
        LookupCriteria c = usePlugin.newLookupCriteria();
        MutablePortletApplication pac = (MutablePortletApplication) newApp;
        c.addEqualTo("applicationIdentifier", pac.getApplicationIdentifier());
        Object test =
        usePlugin.getObjectByQuery(
                BasePortletApplicationDefinition.class,
        usePlugin.generateQuery(BasePortletApplicationDefinition.class, c));

        if (test != null)
        {
            String message =
                "A Portlet Application with the identifier, " + pac.getApplicationIdentifier() + " already exists.";
            log.error(message);
            throw new RegistryException(message);
        }
        else
        {
            usePlugin.add(pac);
        }

    }




    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#setDeploymentSystem(java.lang.String, java.lang.String)
     */
    public void setDeploymentSystem(String system, String alias)
    {
        
        if(system != null)
        {
            PersistenceService ps =
                                    (PersistenceService) ServiceUtil.getServiceByName(PersistenceService.SERVICE_NAME);
            originalPlugin = this.plugin; 
                    this.plugin = ps.getPlugin(system);
        }
        
       
        if(alias != null)
        {   
            this.originalAlias = plugin.getDbAlias();         
            this.plugin.setDbAlias(alias); 
        }
        
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#resetDeploymentSystem()
     */
    public void resetDeploymentSystem()
    {
        if(originalPlugin != null)
        {
            plugin = originalPlugin;
        }
        
        if(originalAlias != null)
        {
            plugin.setDbAlias(originalAlias);
        }

    }

}
