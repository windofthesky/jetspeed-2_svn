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
package org.apache.jetspeed.om.portlet.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.portlet.PreferencesValidator;
import javax.xml.namespace.QName;

import org.apache.jetspeed.components.portletpreferences.PortletPreferencesProvider;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.components.portletregistry.RegistryException;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.common.Support;
import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.EventDefinitionReference;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.PortletInfo;
import org.apache.jetspeed.om.portlet.Preferences;
import org.apache.jetspeed.om.portlet.SecurityRoleRef;
import org.apache.jetspeed.om.portlet.Supports;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.jetspeed.util.JetspeedLongObjectID;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.jetspeed.om.portlet.Language;

/**
 * 
 * PortletDefinitionImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class PortletDefinitionImpl implements PortletDefinition, Serializable, Support, PersistenceBrokerAware
{
    private static PortletRegistry registry;
    private static PortletFactory  portletFactory;
    private static PortletPreferencesProvider portletPreferencesProvider;

    private PortletApplication application;
    
    private Long id;

    protected String portletName;
    protected String portletClass;
    protected String resourceBundle;
    protected String preferenceValidatorClassname;
    
    private List<Language> languages = null;
    private Map<Locale,ResourceBundle> resourceBundles = new HashMap<Locale, ResourceBundle>();
    
    protected List portletEntities;

    /** PortletApplicationDefinition this PortletDefinition belongs to */
    private PortletApplication app;

    /** Metadata property */    
    private Collection metadataFields = null;

    private String jetspeedSecurityConstraint = null;
    
    public PortletDefinitionImpl()
    {
    }

    protected ResourceBundle loadResourceBundle( Locale locale )
    {
        ResourceBundle resourceBundle = null;
        try
        {
            if (getResourceBundle() != null)
            {
                ClassLoader cl = getPortletClassLoader();
                if (cl != null)
                {
                    resourceBundle = ResourceBundle.getBundle(getResourceBundle(), locale, cl);
                }
                else
                {
                    resourceBundle = ResourceBundle.getBundle(getResourceBundle(), locale, Thread.currentThread()
                            .getContextClassLoader());
                }
            }
        }
        catch (MissingResourceException x)
        {
            return null;
        }
        return resourceBundle;
    }
    
    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPortletClass()
     */
    public String getPortletClass()
    {
        return portletClass;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPortletName()
     */
    public String getPortletName()
    {
        return portletName;
    }
    
    public Language getLanguage(Locale locale)
    {
        return null;
    }
    
    public List<Language> getLanguages()
    {
        if ( languages == null )
        {
            languages = new ArrayList<Language>();
        }
        return Collections.unmodifiableList(languages);
    }
    
    public Language addLanguage(Locale locale)
    {
        for (Language l : languages)
        {
            if (l.getLocale().equals(locale))
            {
                throw new IllegalArgumentException("Language already defined");
            }
        }
        LanguageImpl l = new LanguageImpl();
        l.setLocale(locale);
        languages.add(l);
        return l;
    }
    
    public ResourceBundle getResourceBundle(Locale locale)
    {
        return null;
    }

    public PortletApplication getPortletApplicationDefinition()
    {
        return app;
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getPortletClassLoader()
     */
    public ClassLoader getPortletClassLoader()
    {
        return portletFactory.getPortletApplicationClassLoader(app);
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#setPortletName(java.lang.String)
     */
    public void setPortletName( String name )
    {
        this.portletName = name;
    }

    public void setPortletApplication( PortletApplication pa )
    {
        app = (PortletApplication) pa;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        if (obj != null && obj.getClass().equals(getClass()))
        {
            PortletDefinitionImpl pd = (PortletDefinitionImpl) obj;
            boolean sameId = (id != null && pd.id != null && id.equals(pd.id));
            if (sameId)
            {
                return true;
            }
//            boolean sameAppId = (appId == pd.appId);
            boolean sameName = (pd.getPortletName() != null && portletName != null && pd.getPortletName().equals(portletName));
            return sameName;// && sameAppId;
        }
        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hasher = new HashCodeBuilder(1, 3);
        hasher.append(portletName);
        if (app != null)
        {
//            if ( getId() != null )
//            {
//              hasher.append(getId().toString());
//            }
            hasher.append(app.getName());
        }
        return hasher.toHashCode();
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletDefinition#getUniqueName()
     */
    public String getUniqueName()
    {
        if (app != null && portletName != null)
        {
            return app.getName() + "::" + portletName;
        }
        else
        {
            throw new IllegalStateException(
                    "Cannot generate a unique portlet name until the application and portlet name have been set");
        }
    }

    /**
     * Returns localized text of this PortletDefinitions description.
     * 
     * @param locale
     *            Locale to get the description for
     * @return Localized text string of the display name or <code>null</code>
     *         if no Description exists for this locale
     */
    public String getDescriptionText( Locale locale )
    {
        Description desc = getDescription(locale);
        if (desc != null)
        {
            return desc.getDescription();
        }
        return null;
    }
    
    /**
     * <p>
     * store will attempt to perform an atomic persistence call against this
     * portletDefinition.
     * </p>
     * 
     * @see org.apache.pluto.om.portlet.PortletDefinition#store()
     * @throws java.io.IOException
     */
    public void store() throws IOException
    {
        try
        {
            registry.savePortletDefinition(this);
        }
        catch (RegistryException e)
        {
            IOException ioe = new IOException("Failed to store portlet definition: "+e.getMessage());
            ioe.initCause(e);
        }
    }

    public void storeChildren()
    {
//        if (preferenceSet != null)
//        {
//            portletPreferencesProvider.savePreferenceSet(this, preferenceSet);
//        }
    }

    /**
     * <p>
     * getPreferenceValidatorClassname
     * </p>
     * 
     * @return
     */
    public String getPreferenceValidatorClassname()
    {
        return preferenceValidatorClassname;
    }

    /**
     * <p>
     * setPreferenceValidatorClassname
     * </p>
     * 
     * @param string
     *  
     */
    public void setPreferenceValidatorClassname( String string )
    {
        preferenceValidatorClassname = string;
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletApplication#getMetadata()
     */
    public GenericMetadata getMetadata()
    {
        if (metadataFields == null)
        {
            metadataFields = new ArrayList();
        }

        GenericMetadata metadata = new PortletDefinitionMetadataImpl();
        metadata.setFields(metadataFields);

        return metadata;
    }

    /**
     * @see org.apache.jetspeed.om.portlet.PortletApplication#setMetadata(org.apache.jetspeed.om.portlet.GenericMetadata)
     */
    public void setMetadata( GenericMetadata metadata )
    {
        this.metadataFields = metadata.getFields();
    }

    /**
     * @return
     */
    protected Collection getMetadataFields()
    {
        return metadataFields;
    }

    /**
     * @param collection
     */
    protected void setMetadataFields( Collection metadataFields )
    {
        this.metadataFields = metadataFields;
    }
    
    /**
     * @return
     */
    public String getResourceBundle()
    {
        return resourceBundle;
    }

    /**
     * @param string
     */
    public void setResourceBundle(String string)
    {
        resourceBundle = string;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.Support#postLoad(java.lang.Object)
     */
    public void postLoad(Object parameter) throws Exception
    {
    }
    
    public PreferencesValidator getPreferencesValidator()
    {
        return portletFactory.getPreferencesValidator(this);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#getJetspeedSecurityConstraint()
     */
    public String getJetspeedSecurityConstraint()
    {
        return this.jetspeedSecurityConstraint;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite#setJetspeedSecurityConstraint(java.lang.String)
     */
    public void setJetspeedSecurityConstraint(String constraint)
    {
        this.jetspeedSecurityConstraint = constraint;
    }
    
    //
    /// PersistenceBrokerAware interface implementation
    public void afterDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void afterInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        storeChildren();
    }

    public void afterLookup(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void afterUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
        storeChildren();
    }

    public void beforeDelete(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void beforeInsert(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }

    public void beforeUpdate(PersistenceBroker arg0) throws PersistenceBrokerException
    {
    }
    

    public static void setPortletRegistry(PortletRegistry registry)
    {
        PortletDefinitionImpl.registry = registry;
    }

    public static void setPortletFactory(PortletFactory portletFactory)
    {
        PortletDefinitionImpl.portletFactory = portletFactory;
    }

    public static void setPortletPreferencesProvider(PortletPreferencesProvider portletPreferencesProvider)
    {
        PortletDefinitionImpl.portletPreferencesProvider = portletPreferencesProvider;
    }

    public ContainerRuntimeOption addContainerRuntimeOption(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Description addDescription(String lang)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public DisplayName addDisplayName(String lang)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public InitParam addInitParam(String paramName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public SecurityRoleRef addSecurityRoleRef(String roleName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public EventDefinitionReference addSupportedProcessingEvent(QName qname)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public EventDefinitionReference addSupportedProcessingEvent(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public EventDefinitionReference addSupportedPublishingEvent(QName qname)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public EventDefinitionReference addSupportedPublishingEvent(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Supports addSupports(String mimeType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PortletApplication getApplication()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public ContainerRuntimeOption getContainerRuntimeOption(String name)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<ContainerRuntimeOption> getContainerRuntimeOptions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Description getDescription(Locale locale)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Description> getDescriptions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public DisplayName getDisplayName(Locale locale)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDisplayNameText(Locale locale)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<DisplayName> getDisplayNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public InitParam getInitParam(String paramName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<InitParam> getInitParams()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PortletInfo getPortletInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Preferences getPortletPreferences()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public SecurityRoleRef getSecurityRoleRef(String roleName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<SecurityRoleRef> getSecurityRoleRefs()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<EventDefinitionReference> getSupportedProcessingEvents()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<EventDefinitionReference> getSupportedPublishingEvents()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Supports getSupports(String mimeType)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Supports> getSupports()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isSameIdentity(PortletDefinition other)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void addSupportedLocale(String lang)
    {
        // TODO Auto-generated method stub
        
    }

    public void addSupportedPublicRenderParameter(String identifier)
    {
        // TODO Auto-generated method stub
        
    }

    public String getCacheScope()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int getExpirationCache()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public List<String> getSupportedLocales()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getSupportedPublicRenderParameters()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setCacheScope(String cacheScope)
    {
        // TODO Auto-generated method stub
        
    }

    public void setExpirationCache(int expirationCache)
    {
        // TODO Auto-generated method stub
        
    }

    public void setPortletClass(String portletClass)
    {
        // TODO Auto-generated method stub
        
    }
}
