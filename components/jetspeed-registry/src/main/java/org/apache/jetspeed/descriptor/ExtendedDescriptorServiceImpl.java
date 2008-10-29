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
package org.apache.jetspeed.descriptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.jetspeed.om.portlet.ContainerRuntimeOption;
import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.EventDefinition;
import org.apache.jetspeed.om.portlet.Filter;
import org.apache.jetspeed.om.portlet.FilterMapping;
import org.apache.jetspeed.om.portlet.InitParam;
import org.apache.jetspeed.om.portlet.Language;
import org.apache.jetspeed.om.portlet.Listener;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.PublicRenderParameter;
import org.apache.jetspeed.om.portlet.SecurityConstraint;
import org.apache.jetspeed.om.portlet.SecurityRoleRef;
import org.apache.jetspeed.om.portlet.Supports;
import org.apache.jetspeed.om.portlet.UserAttribute;
import org.apache.jetspeed.om.portlet.UserAttributeRef;
import org.apache.jetspeed.om.portlet.impl.CustomPortletModeImpl;
import org.apache.jetspeed.om.portlet.impl.CustomWindowStateImpl;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.jetspeed.jaxb.MetadataType;
import org.apache.jetspeed.om.portlet.jetspeed.jaxb.Portlet;
import org.apache.jetspeed.om.portlet.jetspeed.jaxb.PortletApp;
import org.apache.jetspeed.om.portlet.jetspeed.jaxb.Service;
import org.apache.jetspeed.om.servlet.WebApplicationDefinition;
import org.apache.jetspeed.om.servlet.impl.WebApplicationDefinitionImpl;
import org.apache.jetspeed.tools.deploy.JetspeedWebApplicationRewriter;
import org.apache.jetspeed.tools.deploy.JetspeedWebApplicationRewriterFactory;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.pluto.descriptors.services.jaxb.PortletAppDescriptorServiceImpl;
import org.apache.pluto.om.portlet.CustomPortletMode;
import org.apache.pluto.om.portlet.CustomWindowState;
import org.apache.pluto.om.portlet.Description;
import org.apache.pluto.om.portlet.PortletApplicationDefinition;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Extends Pluto Descriptor service for loading portlet applications in a Jetspeed format.
 * Additionally, has two APIs to load extended Jetspeed descriptor information (jetspeed-portlet.xml) 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class ExtendedDescriptorServiceImpl extends PortletAppDescriptorServiceImpl implements ExtendedDescriptorService
{
    public PortletApplication createPortletApplicationDefinition()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public PortletApplication read(InputStream in) throws IOException
    {
        PortletApplicationDefinition pad = super.read(in);
        // TODO: do extended processing here 
        return upgrade(pad);
    }

    public void write(PortletApplication portletDescriptor, OutputStream out)
            throws IOException
    {
        // TODO Auto-generated method stub

    }

    public void write(PortletApplicationDefinition portletDescriptor,
            OutputStream out) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    protected PortletApplication upgrade(PortletApplicationDefinition pa)
    {
        PortletApplication jpa = new PortletApplicationDefinitionImpl();
        jpa.setDefaultNamespace(pa.getDefaultNamespace());
        //pa.setDescription(pad.get) // TODO: 2.2 should we get this from the web.xml (as well as <display-name> and <security-role>
        // not upgradable: checksum, revision
        jpa.setName(pa.getName());
        jpa.setResourceBundle(pa.getResourceBundle());
        jpa.setVersion(pa.getVersion());
        for (org.apache.pluto.om.portlet.PortletDefinition pd : pa.getPortlets())
        {
            PortletDefinition jpd = jpa.addPortlet(pd.getPortletName());
            upgradePortlet(jpd, pd);
        }
        for (org.apache.pluto.om.portlet.ContainerRuntimeOption cro : pa.getContainerRuntimeOptions())
        {
            ContainerRuntimeOption jcro = jpa.addContainerRuntimeOption(cro.getName());
            for (String value : cro.getValues())
            {
                jcro.addValue(value);
            }
        }
        for (org.apache.pluto.om.portlet.CustomPortletMode cpm : pa.getCustomPortletModes())
        {
            CustomPortletMode jcpm = jpa.addCustomPortletMode(cpm.getPortletMode());
            jcpm.setPortalManaged(cpm.isPortalManaged());
            for (org.apache.pluto.om.portlet.Description desc : cpm.getDescriptions())
            {
                Description jdesc = jcpm.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }            
        }
        for (org.apache.pluto.om.portlet.CustomWindowState cws : pa.getCustomWindowStates())
        {
            CustomWindowState jcws = jpa.addCustomWindowState(cws.getWindowState());
            for (org.apache.pluto.om.portlet.Description desc : cws.getDescriptions())
            {
                Description jdesc = jcws.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }            
        }        
        for (org.apache.pluto.om.portlet.EventDefinition ed : pa.getEventDefinitions())
        {
            EventDefinition jed = null;
            if (ed.getQName() != null)
            {
                jed = jpa.addEventDefinition(ed.getQName());
            }
            else
            {
                jed =jpa.addEventDefinition(ed.getName());
            }
            jed.setValueType(ed.getValueType());
            for (QName alias : ed.getAliases())
            {
                jed.addAlias(alias);
            }
            for (org.apache.pluto.om.portlet.Description desc : ed.getDescriptions())
            {
                Description jdesc = jed.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }                        
        }
        for (org.apache.pluto.om.portlet.FilterMapping fm : pa.getFilterMappings())
        {
            FilterMapping jfm = jpa.addFilterMapping(fm.getFilterName());
            for (String portletName : fm.getPortletNames())
            {
                jfm.addPortletName(portletName);
            }
        }
        for (org.apache.pluto.om.portlet.Filter f : pa.getFilters())
        {
            Filter jf = jpa.addFilter(f.getFilterName());
            jf.setFilterClass(f.getFilterClass());
            for (org.apache.pluto.om.portlet.Description desc : f.getDescriptions())
            {
                Description jdesc = jf.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }                                   
            for (org.apache.pluto.om.portlet.DisplayName dn : f.getDisplayNames())
            {
                DisplayName jdn = jf.addDisplayName(dn.getLang());
                jdn.setDisplayName(dn.getDisplayName());
            }
            for (org.apache.pluto.om.portlet.InitParam ip : f.getInitParams())
            {
                InitParam jip = jf.addInitParam(ip.getParamName());
                jip.setParamValue(ip.getParamValue());
                for (org.apache.pluto.om.portlet.Description desc : ip.getDescriptions())
                {
                    Description jdesc = jip.addDescription(desc.getLang());
                    jdesc.setDescription(desc.getDescription());
                }                                        
            }
            for (String lc : f.getLifecycles())
            {
                jf.addLifecycle(lc);
            }            
        }
        for (org.apache.pluto.om.portlet.Listener l : pa.getListeners())
        {
            Listener jl = jpa.addListener(l.getListenerClass());
            for (org.apache.pluto.om.portlet.Description desc : l.getDescriptions())
            {
                Description jdesc = jl.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }                                        
            for (org.apache.pluto.om.portlet.DisplayName dn : l.getDisplayNames())
            {
                DisplayName jdn = jl.addDisplayName(dn.getLang());
                jdn.setDisplayName(dn.getDisplayName());
            }
        }
        for (org.apache.pluto.om.portlet.PublicRenderParameter prd : pa.getPublicRenderParameters())
        {            
            PublicRenderParameter jprp = null;
            if (prd.getQName() != null)
            {
                jprp = jpa.addPublicRenderParameter(prd.getQName(), prd.getIdentifier());
            }
            else
            {
                jprp = jpa.addPublicRenderParameter(prd.getName(), prd.getIdentifier());
            }
            for (QName alias : prd.getAliases())
            {
                jprp.addAlias(alias);
            }
            for (org.apache.pluto.om.portlet.Description desc : prd.getDescriptions())
            {
                Description jdesc = jprp.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }
        }
        for (org.apache.pluto.om.portlet.SecurityConstraint sc :  pa.getSecurityConstraints())
        {
            SecurityConstraint jsc = jpa.addSecurityConstraint(sc.getUserDataConstraint().getTransportGuarantee());
            for (org.apache.pluto.om.portlet.DisplayName dn : sc.getDisplayNames())
            {
                DisplayName jdn = jsc.addDisplayName(dn.getLang());
                jdn.setDisplayName(dn.getDisplayName());
            }
            for (String portletName : sc.getPortletNames())
            {
                jsc.addPortletName(portletName);
            }            
        }
        for (org.apache.pluto.om.portlet.UserAttribute ua : pa.getUserAttributes())
        {
            UserAttribute jua = jpa.addUserAttribute(ua.getName());
            for (org.apache.pluto.om.portlet.Description desc : ua.getDescriptions())
            {
                Description jdesc = jua.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }                                                    
        }
        return jpa;
    }

    protected void upgradePortlet(PortletDefinition jpd, org.apache.pluto.om.portlet.PortletDefinition pd)
    {
        jpd.setCacheScope(pd.getCacheScope());
        jpd.setExpirationCache(pd.getExpirationCache());
        jpd.setPortletClass(pd.getPortletClass());
        jpd.setResourceBundle(pd.getResourceBundle());
        for (org.apache.pluto.om.portlet.ContainerRuntimeOption cro : pd.getContainerRuntimeOptions())
        {
            ContainerRuntimeOption jcro = jpd.addContainerRuntimeOption(cro.getName());
            for (String value : cro.getValues())
            {
                jcro.addValue(value);
            }
        }
        for (org.apache.pluto.om.portlet.Description desc : pd.getDescriptions())
        {
            Description jdesc = jpd.addDescription(desc.getLang());
            jdesc.setDescription(desc.getDescription());
        }                        
        for (org.apache.pluto.om.portlet.DisplayName dn : pd.getDisplayNames())
        {
            DisplayName jdn = jpd.addDisplayName(dn.getLang());
            jdn.setDisplayName(dn.getDisplayName());
        }
        for (org.apache.pluto.om.portlet.InitParam ip : pd.getInitParams())
        {
            InitParam jip = jpd.addInitParam(ip.getParamName());
            jip.setParamValue(ip.getParamValue());
            for (org.apache.pluto.om.portlet.Description desc : ip.getDescriptions())
            {
                Description jdesc = jip.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }                                        
        }
        for (org.apache.pluto.om.portlet.SecurityRoleRef srr : pd.getSecurityRoleRefs())
        {
            SecurityRoleRef jsrr = jpd.addSecurityRoleRef(srr.getRoleName());
            jsrr.setRoleLink(srr.getRoleLink());
        }
        
        boolean defaultLocaleProcessed = false;
        for (String locale : pd.getSupportedLocales())
        {
            jpd.addSupportedLocale(locale);
            if (addLanguage(jpd, JetspeedLocale.convertStringToLocale(locale)).equals(JetspeedLocale.getDefaultLocale()))
            {
                defaultLocaleProcessed = true;
            }                        
        }
        if (!defaultLocaleProcessed)
        {
            addLanguage(jpd, JetspeedLocale.getDefaultLocale());
            defaultLocaleProcessed = true;
        }
        
        for (org.apache.pluto.om.portlet.EventDefinitionReference ed : pd.getSupportedProcessingEvents())
        {
            if (ed.getQName() != null)
            {
                jpd.addSupportedProcessingEvent(ed.getQName());
            }
            else
            {
                jpd.addSupportedProcessingEvent(ed.getName());
            }
        }
        for (String sprd : pd.getSupportedPublicRenderParameters())
        {
            jpd.addSupportedPublicRenderParameter(sprd);
        }
        for (org.apache.pluto.om.portlet.EventDefinitionReference ed : pd.getSupportedPublishingEvents())
        {
            if (ed.getQName() != null)
            {
                jpd.addSupportedPublishingEvent(ed.getQName());
            }
            else
            {
                jpd.addSupportedPublishingEvent(ed.getName());
            }
        }
        for (org.apache.pluto.om.portlet.Supports supports : pd.getSupports())
        {
            Supports jsupports = jpd.addSupports(supports.getMimeType());
            for (String pm : supports.getPortletModes())
            {
                jsupports.addPortletMode(pm);
            }
            for (String ws : supports.getWindowStates())
            {
                jsupports.addWindowState(ws);
            }
        }
    }
    
    protected Language addLanguage(PortletDefinition jpd, Locale locale)
    {
        ResourceBundle bundle = jpd.getResourceBundle(locale);
        Language l = jpd.addLanguage(locale);
        String value = bundle.getString(Language.JAVAX_PORTLET_TITLE);
        if (!value.equals(""))
        {
            l.setTitle(value);
        }
        value = bundle.getString(Language.JAVAX_PORTLET_SHORT_TITLE);
        if (!value.equals(""))
        {
            l.setShortTitle(value);
        }
        value = bundle.getString(Language.JAVAX_PORTLET_SHORT_TITLE);
        if (!value.equals(""))
        {
            l.setKeywords(value);
        }
        return l;
    }
    
    public void readExtended(InputStream in, PortletApplication app) throws IOException
    {
        try
        {
            JAXBContext jc = JAXBContext.newInstance("org.apache.jetspeed.om.portlet.jetspeed.jaxb");
            Unmarshaller u = jc.createUnmarshaller();
            PortletApp pa = (PortletApp) u.unmarshal(in);
            app.setJetspeedSecurityConstraint(pa.getSecurityConstraintRef());
            for (Service s : pa.getServices())
            {
                app.addJetspeedServiceReference(s.getName());
            }
            for (MetadataType m : pa.getMetadata())
            {
                if (m.getContent() != null)
                {
                    app.getMetadata().addField(JetspeedLocale.convertStringToLocale(m.getLang()), m.getMetadataName(), m.getContent());
                }
            }
            for (Portlet p : pa.getPortlets())
            {
                PortletDefinition pd = app.getPortlet(p.getPortletName());
                if (pd != null)
                {
                    pd.setJetspeedSecurityConstraint(p.getSecurityConstraintRef());
                    for (MetadataType m : p.getMetadata())
                    {
                        if (m.getContent() != null)
                        {
                            pd.getMetadata().addField(JetspeedLocale.convertStringToLocale(m.getLang()), m.getMetadataName(), m.getContent());
                        }
                    }
                }
            }
            
            for (org.apache.jetspeed.om.portlet.jetspeed.jaxb.CustomPortletMode cpm : pa.getCustomPortletModes())
            {
                if (cpm.getName() != null && cpm.getMappedName() != null && !cpm.getName().equals(cpm.getMappedName()))
                {
                    CustomPortletMode jcpm = app.getCustomPortletMode(cpm.getMappedName());
                    if (jcpm != null && app.getCustomPortletMode(cpm.getName()) == null)
                    {
                        ((CustomPortletModeImpl)jcpm).setMappedName(cpm.getMappedName());
                    }
                }
            }
            for (org.apache.jetspeed.om.portlet.jetspeed.jaxb.CustomWindowState cws : pa.getCustomWindowStates())
            {
                if (cws.getName() != null && cws.getMappedName() != null && !cws.getName().equals(cws.getMappedName()))
                {
                    CustomWindowState jcws = app.getCustomWindowState(cws.getMappedName());
                    if (jcws != null && app.getCustomWindowState(cws.getName()) == null)
                    {
                        ((CustomWindowStateImpl)jcws).setMappedName(cws.getMappedName());
                    }
                }
            }
            for (org.apache.jetspeed.om.portlet.jetspeed.jaxb.UserAttributeRef ref : pa.getUserAttributeRefs())
            {
                UserAttributeRef jref = app.addUserAttributeRef(ref.getName());
                jref.setNameLink(ref.getNameLink());
                Description desc = jref.addDescription("en");
                desc.setDescription(ref.getDescription());
            }
        }
        catch (JAXBException je)
        {
            throw new IOException(je.getMessage());
        }
    }
        
    public WebApplicationDefinition readServletDescriptor(InputStream is) throws IOException
    {
        Document doc = this.parseXml(is);        
        JetspeedWebApplicationRewriterFactory rewriterFactory = new JetspeedWebApplicationRewriterFactory();        
        try
        {
            JetspeedWebApplicationRewriter rewriter = rewriterFactory.getInstance(doc);
            // TODO: 2.2 implement this, get the security roles
//          digester.addCallMethod("web-app/security-role", "addRole", 0);
//          digester.addBeanPropertySetter("web-app/security-role/description", "description");
//          digester.addBeanPropertySetter("web-app/security-role/role-name", "roleName");
            WebApplicationDefinition webapp = new WebApplicationDefinitionImpl();
//          webapp.setContextRoot(contextRoot);
//          webapp.setDescription(contextRoot);            
            return webapp;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage());
        }
    }
    
    protected Document parseXml(InputStream source) throws IOException
    {
        try
        {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true); 
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            builder.setEntityResolver(new EntityResolver()
            {
                public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId) throws SAXException,
                                java.io.IOException
                {
                    if (systemId.equals("http://java.sun.com/dtd/web-app_2_3.dtd"))
                    {
                        return new InputSource(getClass().getResourceAsStream("web-app_2_3.dtd"));
                    }
                    if (systemId.equals("http://java.sun.com/dtd/web-app_2_4.dtd"))
                    {
                        return new InputSource(getClass().getResourceAsStream("web-app_2_4.dtd"));
                    }
                    return null;
                }
            });
            Document document = builder.parse(source);
            return document;
        }
        catch (Exception e)
        {
            throw new IOException(e.toString());
        }
    }
        
}
