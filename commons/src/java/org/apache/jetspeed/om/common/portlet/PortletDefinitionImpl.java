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
package org.apache.jetspeed.om.common.portlet;

import java.io.Serializable;
import java.util.Collection;
import java.util.Locale;

import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.jetspeed.om.common.LanguageSetImpl;
import org.apache.jetspeed.om.common.ParameterSetImpl;
import org.apache.jetspeed.om.common.PreferenceImpl;
import org.apache.jetspeed.om.common.PreferenceSetImpl;
import org.apache.jetspeed.om.common.SecurityRoleRefSetImpl;
import org.apache.jetspeed.om.common.DescriptionImpl;
import org.apache.jetspeed.om.common.DescriptionSetImpl;
import org.apache.jetspeed.om.common.DisplayNameImpl;
import org.apache.jetspeed.om.common.DisplayNameSetImpl;
import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.MutableDescriptionSet;
import org.apache.jetspeed.om.common.MutableDisplayName;
import org.apache.jetspeed.om.common.MutableDisplayNameSet;
import org.apache.jetspeed.om.common.ObjectIDImpl;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.om.common.PreferenceComposite;
import org.apache.jetspeed.om.common.extended.PortletParameterSetImpl;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.DisplayName;
import org.apache.pluto.om.common.DisplayNameSet;
import org.apache.pluto.om.common.Language;
import org.apache.pluto.om.common.LanguageSet;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.ParameterSet;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.common.ValidatorDefinition;
import org.apache.pluto.om.portlet.ContentType;
import org.apache.pluto.om.portlet.ContentTypeSet;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.om.servlet.ServletDefinition;

/**
 * 
 * PortletDefinitionImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PortletDefinitionImpl implements PortletDefinitionComposite, Serializable
{
    private long id;
    private String className;
    private String name;
    private String portletIdentifier;
    private LanguageSetImpl languageSet = null;
    private ParameterSetImpl parameterSet;
    private SecurityRoleRefSet securityRoleRefSet;

    private MutableDisplayNameSet displayNames;
    private MutableDescriptionSet descriptions;

    /** PortletApplicationDefinition this PortletDefinition belongs to */
    private MutablePortletApplication app;
    /** UID of the PortletApplicationDefinition */
    // protected ObjectID appId;
    protected long appId;

    private PreferenceSetImpl prefSet = null;
    private ValidatorDefinition vd;
    private ContentTypeSetComposite contentTypes;

    private ClassLoader portletClassLoader;

    private String expirationCache;

    public PortletDefinitionImpl()
    {
        super();

        try
        {
            parameterSet = new PortletParameterSetImpl();
            securityRoleRefSet = new SecurityRoleRefSetImpl();
            contentTypes = new ContentTypeSetImpl();

        }
        catch (RuntimeException e)
        {
            System.out.println("Failed to fully construct Portlet Definition");
            e.printStackTrace();
        }
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getId()
     */
    public ObjectID getId()
    {
        ObjectIDImpl oid = new ObjectIDImpl();
        oid.setValue(id);
        return oid;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getClassName()
     */
    public String getClassName()
    {
        return className;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getName()
     */
    public String getName()
    {
        return name;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getLanguageSet()
     */
    public LanguageSet getLanguageSet()
    {
        return languageSet;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getInitParameterSet()
     */
    public ParameterSet getInitParameterSet()
    {
        return parameterSet;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getInitSecurityRoleRefSet()
     */
    public SecurityRoleRefSet getInitSecurityRoleRefSet()
    {
        return securityRoleRefSet;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPreferenceSet()
     */
    public PreferenceSet getPreferenceSet()
    {
        return prefSet;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setPreferenceSet(org.apache.pluto.om.common.PreferenceSet)
     */
    public void setPreferenceSet(PreferenceSet preferences)
    {
        this.prefSet = (PreferenceSetImpl) preferences;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getValidatorDefinition()
     */
    public ValidatorDefinition getValidatorDefinition()
    {
        return vd;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getContentTypeSet()
     */
    public ContentTypeSet getContentTypeSet()
    {
        return contentTypes;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPortletApplicationDefinition()
     */
    public PortletApplicationDefinition getPortletApplicationDefinition()
    {
        return app;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getServletDefinition()
     */
    public ServletDefinition getServletDefinition()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getExpirationCache()
     */
    public String getExpirationCache()
    {
        return expirationCache;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPortletClassLoader()
     */
    public ClassLoader getPortletClassLoader()
    {
        return portletClassLoader;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setId(java.lang.String)
     */
    public void setId(String oid)
    {
        id = new Long(oid).longValue();
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setClassName(java.lang.String)
     */
    public void setClassName(String className)
    {
        this.className = className;

    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setPortletClassLoader(java.lang.ClassLoader)
     */
    public void setPortletClassLoader(ClassLoader loader)
    {
        this.portletClassLoader = loader;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addLanguage(org.apache.pluto.om.common.Language)
     */
    public void addLanguage(Language lang)
    {
        if (languageSet == null)
        {
            languageSet = new LanguageSetImpl();
        }
        languageSet.add(lang);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setContentTypeSet(org.apache.pluto.om.portlet.ContentTypeSet)
     */
    public void setContentTypeSet(ContentTypeSet contentTypes)
    {
        this.contentTypes = (ContentTypeSetImpl) contentTypes;

    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setInitParameterSet(org.apache.pluto.om.common.ParameterSet)
     */
    public void setInitParameterSet(ParameterSet parameters)
    {
        this.parameterSet = (ParameterSetImpl) parameters;

    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setInitSecurityRoleRefSet(org.apache.pluto.om.common.SecurityRoleRefSet)
     */
    public void setInitSecurityRoleRefSet(SecurityRoleRefSet securityRefs)
    {
        this.securityRoleRefSet = (SecurityRoleRefSetImpl) securityRefs;

    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setLanguageSet(org.apache.pluto.om.common.LanguageSet)
     */
    public void setLanguageSet(LanguageSet languages)
    {
        this.languageSet = (LanguageSetImpl) languages;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setInitParameter(java.lang.String, java.lang.String, java.lang.String)
     */
    public ParameterComposite addInitParameter(String name, String value, DescriptionSet description)
    {
        ParameterComposite pc = addInitParameter(name, value);
        pc.setDescriptionSet(description);

        return pc;

    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addInitParameter(java.lang.String, java.lang.String, java.lang.String, java.util.Locale)
     */
    public ParameterComposite addInitParameter(String name, String value, String description, Locale locale)
    {
        ParameterComposite param = addInitParameter(name, value);
        param.addDescription(locale, description);
        return param;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setInitParameter(java.lang.String, java.lang.String)
     */
    public ParameterComposite addInitParameter(String name, String value)
    {
        return (ParameterComposite) parameterSet.add(name, value);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setExpirationCache(java.lang.String)
     */
    public void setExpirationCache(String cache)
    {
        expirationCache = cache;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addContentType(org.apache.pluto.om.portlet.ContentType)
     */
    public void addContentType(ContentType cType)
    {
        contentTypes.addContentType(cType);
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addPreference(java.lang.String, java.util.Collection)
     */
    public PreferenceComposite addPreference(String name, Collection values)
    {
        // PreferenceComposite pref = JetspeedPortletRegistry.newPreference();
        PreferenceComposite pref = new PreferenceImpl();
        pref.setName(name);
        pref.setValues(values);
        if (prefSet == null)
        {
            prefSet = new PreferenceSetImpl();
        }
        prefSet.add(pref);

        return pref;
    }

    public void setPortletIdentifier(String portletIdentifier)
    {
        this.portletIdentifier = portletIdentifier;
    }

    public String getPortletIdentifier()
    {
        return this.portletIdentifier;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setPortletApplicationDefinition(org.apache.pluto.om.portlet.PortletApplicationDefinition)
     */
    public void setPortletApplicationDefinition(PortletApplicationDefinition pad)
    {
        app = (MutablePortletApplication) pad;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof PortletDefinitionComposite)
        {
            PortletDefinitionComposite portlet = (PortletDefinitionComposite) obj;
            System.out.println("portlet name: " + name);
            if (app == null && portlet.getPortletApplicationDefinition() == null)
            {
                System.out.println("portlet name: " + name);
                return name.equals(portlet.getName());
            }
            else if (app != null && portlet.getPortletApplicationDefinition() != null)
            {
                return name.equals(portlet.getName())
                    && app.getName().equals(((MutablePortletApplication) portlet.getPortletApplicationDefinition()).getName());
            }
            else
            {
                return false;
            }
        }

        return false;

    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hasher = new HashCodeBuilder(1, 3);
        hasher.append(name);
        if (app != null)
        {
            hasher.append(app.getName());
        }
        return hasher.toHashCode();
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#getUniqueName()
     */
    public String getUniqueName()
    {
        if (app != null && name != null)
        {
            return app.getName() + ":" + name;
        }
        else
        {
            return null;
        }

    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getDescription(java.util.Locale)
     */
    public Description getDescription(Locale arg0)
    {
        if (descriptions != null)
        {
            return descriptions.get(arg0);
        }
        return null;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getDisplayName(java.util.Locale)
     */
    public DisplayName getDisplayName(Locale arg0)
    {
        if (displayNames != null)
        {
            return displayNames.get(arg0);
        }

        return null;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setDescriptions(org.apache.pluto.om.common.DescriptionSet)
     */
    public void setDescriptions(DescriptionSet arg0)
    {
        this.descriptions = (MutableDescriptionSet) arg0;

    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionCtrl#setDisplayNames(org.apache.pluto.om.common.DisplayNameSet)
     */
    public void setDisplayNames(DisplayNameSet arg0)
    {
        this.displayNames = (MutableDisplayNameSet) arg0;
    }

    /**
     * Returns localized text of this PortletDefinitions display name.
     * 
     * @param locale Locale to get the display name for
     * @return Localized text string of the display name or <code>null</code>
     * if no DisplayName exists for this locale
     */
    public String getDisplayNameText(Locale locale)
    {
        DisplayName dn = getDisplayName(locale);
        if (dn != null)
        {
            return dn.getDisplayName();
        }
        return null;
    }

    /**
     * Returns localized text of this PortletDefinitions description.
     * 
     * @param locale Locale to get the description for
     * @return Localized text string of the display name or <code>null</code>
     * if no Description exists for this locale
     */
    public String getDescriptionText(Locale locale)
    {
        Description desc = getDescription(locale);
        if (desc != null)
        {
            return desc.getDescription();
        }
        return null;

    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addDescription(java.util.Locale, java.lang.String)
     */
    public void addDescription(Locale locale, String description)
    {
        if (descriptions == null)
        {
            descriptions = new DescriptionSetImpl(MutableDescription.TYPE_PORTLET);
        }

        descriptions.addDescription(new DescriptionImpl(locale, description, MutableDescription.TYPE_PORTLET));

    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#addDisplayName(java.util.Locale, java.lang.String)
     */
    public void addDisplayName(Locale locale, String displayName)
    {
        if (displayNames == null)
        {
            displayNames = new DisplayNameSetImpl(MutableDisplayName.TYPE_PORTLET);
        }

        displayNames.addDisplayName(new DisplayNameImpl(locale, displayName, MutableDisplayName.TYPE_PORTLET));

    }

    /**
     *  Remove when Castor is mapped correctly
     * @deprecated
     * @return
     */
    public String getDisplayName()
    {
        DisplayName dn = getDisplayName(Locale.getDefault());
        if (dn != null)
        {
            return dn.getDisplayName();
        }
        return null;
    }

    /**
     *  Remove when Castor is mapped correctly
     * @deprecated
     * @param dn
     */
    public void setDisplayName(String dn)
    {
        addDisplayName(Locale.getDefault(), dn);
    }

    /**
     *  Remove when Castor is mapped correctly
     * @deprecated
     * @return
     */
    public String getDescription()
    {
        Description desc = getDescription(Locale.getDefault());
        if (desc != null)
        {
            return desc.getDescription();
        }
        return null;
    }

    /**
     *  Remove when Castor is mapped correctly
     * @deprecated
     * @param desc
     */
    public void setDescription(String desc)
    {
        addDescription(Locale.getDefault(), desc);
    }

}
