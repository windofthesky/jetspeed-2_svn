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
package org.apache.jetspeed.services.registry;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.exception.RegistryException;
import org.apache.jetspeed.om.common.MutableLanguage;
import org.apache.jetspeed.om.common.portlet.ContentTypeComposite;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;

import org.apache.jetspeed.util.ServiceUtil;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;

/**
 * <P>This is a commodity static accessor class around the
 * <code>RegistryService</code></P>
 *
 * @see org.apache.jetspeed.services.registry.RegistryService
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class JetspeedPortletRegistry
{

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getAllPortletDefinitions()
     */
    public static List getAllPortletDefinitions()
    {
        return getService().getAllPortletDefinitions();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletApplication(org.apache.pluto.om.common.ObjectID)
     */
    public static MutablePortletApplication getPortletApplication(ObjectID id)
    {
        return getService().getPortletApplication(id);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletApplication(java.lang.String)
     */
    public static MutablePortletApplication getPortletApplication(String name)
    {
        return getService().getPortletApplication(name);
    }

    public static PortletDefinitionComposite getPortletDefinitionByUniqueName(String name)
    {
        return getService().getPortletDefinitionByUniqueName(name);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletApplications()
     */
    public static List getPortletApplications()
    {
        return getService().getPortletApplications();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newContentType()
     */
    public static ContentTypeComposite newContentType()
    {
        return getService().newContentType();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newLanguage()
     */
    public static MutableLanguage newLanguage()
    {
        return getService().newLanguage();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#createLanguage(java.util.Locale, java.lang.String, java.lang.String, java.lang.String)
     */
    public static Language createLanguage(
        Locale locale,
        String title,
        String shortTitle,
        String description,
        Collection keywords)
    {
        return getService().createLanguage(locale, title, shortTitle, description, keywords);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newPortletApplication()
     */
    public static MutablePortletApplication newPortletApplication()
    {
        return getService().newPortletApplication();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newPortletDefinition()
     */
    public static PortletDefinitionComposite newPortletDefinition()
    {
        return getService().newPortletDefinition();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#newWebApplication()
     */
    public static MutableWebApplication newWebApplication()
    {
        return getService().newWebApplication();
    }

    public static PreferenceComposite newPreference()
    {
        return getService().newPreference();
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#registerPortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public static void registerPortletApplication(PortletApplicationDefinition newApp)
        throws RegistryException
    {
        getService().registerPortletApplication(newApp);

    }

    /**
      * @see org.apache.jetspeed.services.registry.PortletRegistryService#registerPortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition, java.lang.String)
      */
    public static void registerPortletApplication(PortletApplicationDefinition newApp, String system)
        throws RegistryException
    {
        getService().registerPortletApplication(newApp, system);

    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#removeApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public static void removeApplication(PortletApplicationDefinition app) throws RegistryException
    {
        getService().removeApplication(app);

    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#updatePortletApplication(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public static void updatePortletApplication(PortletApplicationDefinition app)
    {
        getService().updatePortletApplication(app);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletApplicationByIndetifier(java.lang.String)
     */
    public static MutablePortletApplication getPortletApplicationByIndetifier(String ident)
    {
        return getService().getPortletApplicationByIndetifier(ident);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#getPortletDefinitionByIndetifier(java.lang.String)
     */
    public static PortletDefinitionComposite getPortletDefinitionByIndetifier(String ident)
    {
        return getService().getPortletDefinitionByIndetifier(ident);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#portletApplicationExists(java.lang.String)
     */
    public static boolean portletApplicationExists(String appIentity)
    {
        return getService().portletApplicationExists(appIentity);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#portletDefinitionExists(java.lang.String)
     */
    public static boolean portletDefinitionExists(String portletIndentity)
    {
        return getService().portletDefinitionExists(portletIndentity);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#portletDefinitionExists(java.lang.String, org.apache.jetspeed.om.common.portlet.PortletApplicationComposite)
     */
    public static boolean portletDefinitionExists(String portletName, MutablePortletApplication app)
    {
        return getService().portletDefinitionExists(portletName, app);
    }

    private static final PortletRegistryService getService()
    {
        return (PortletRegistryService) ServiceUtil.getServiceByName(
            PortletRegistryService.SERVICE_NAME);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#setDeploymentSystem(java.lang.String, java.lang.String)
     *
     */
    public static void setDeploymentSystem(String system, String alias)
    {
        getService().setDeploymentSystem(system, alias);
    }

    /**
     * @see org.apache.jetspeed.services.registry.PortletRegistryService#resetDeploymentSystem()
     */
    public static void resetDeploymentSystem()
    {
        getService().resetDeploymentSystem();
    }

}
