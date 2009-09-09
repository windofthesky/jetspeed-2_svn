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
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

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
import org.apache.jetspeed.om.portlet.Preference;
import org.apache.jetspeed.om.portlet.PublicRenderParameter;
import org.apache.jetspeed.om.portlet.SecurityConstraint;
import org.apache.jetspeed.om.portlet.SecurityRole;
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
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.pluto.container.om.portlet.CustomPortletMode;
import org.apache.pluto.container.om.portlet.CustomWindowState;
import org.apache.pluto.container.om.portlet.Description;
import org.apache.pluto.container.om.portlet.PortletApplicationDefinition;
import org.apache.pluto.container.om.portlet.PortletInfo;
import org.apache.pluto.container.PortletAppDescriptorService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Jetspeed Descriptor service for loading portlet applications in a Jetspeed format.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedDescriptorServiceImpl implements JetspeedDescriptorService
{
    private static class XPathNamespaceContext implements NamespaceContext
    {
        private String namespaceURI;
        private String prefix;
        
        public XPathNamespaceContext(String prefix)
        {
            this(prefix,XMLConstants.XML_NS_URI);
        }

        public XPathNamespaceContext(String prefix, String namespaceURI)
        {
            this.prefix = prefix;
            this.namespaceURI = namespaceURI;
        }

        public String getNamespaceURI(String prefix)
        {
            if (prefix == null)
            {
                throw new NullPointerException("Null prefix");
            }
            else if (this.prefix.equals(prefix))
            {
                return namespaceURI;
            }
            else if ("xml".equals(prefix))
            {
                return XMLConstants.XML_NS_URI;
            }
            return XMLConstants.NULL_NS_URI;
        }

        public String getPrefix(String namespaceURI)
        {
            throw new UnsupportedOperationException();
        }

        @SuppressWarnings("unchecked")
        public Iterator getPrefixes(String namespaceURI)
        {
            throw new UnsupportedOperationException();
        }
    }

    private static final String NAMESPACE_PREFIX = "js";
    
    private PortletAppDescriptorService plutoDescriptorService;
    DocumentBuilderFactory domFactory;
    
    public JetspeedDescriptorServiceImpl(PortletAppDescriptorService plutoDescriptorService)
    {
        this.plutoDescriptorService = plutoDescriptorService;
    }
    
    public PortletApplication read(String name, String contextPath, InputStream webDescriptor, InputStream portletDescriptor, InputStream jetspeedPortletDescriptor, ClassLoader paClassLoader) throws Exception
    {
        PortletApplicationDefinition pad = plutoDescriptorService.read(name, contextPath, portletDescriptor);
        PortletApplication pa = null;
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try
        {
            Thread.currentThread().setContextClassLoader(paClassLoader);
            pa = upgrade(pad);
        }
        finally
        {
            Thread.currentThread().setContextClassLoader(cl);
        }
        loadWebDescriptor(pa, webDescriptor);
        if (jetspeedPortletDescriptor != null)
        {
            loadJetspeedPortletDescriptor(pa, jetspeedPortletDescriptor);
        }
        return pa;
    }

    protected void loadWebDescriptor(PortletApplication pa, InputStream webDescriptor) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
    {
        if (domFactory == null)
        {
            domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
        }
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
                return null;
            }
        });
        
        Document document = builder.parse(webDescriptor);
        Element root = document.getDocumentElement();
        String namespace = root.getNamespaceURI();

        XPath xpath = XPathFactory.newInstance().newXPath();
        String prefix;
        
        if(namespace!= null && namespace.length() > 0)
        {
            prefix = NAMESPACE_PREFIX+":";
            xpath.setNamespaceContext(new XPathNamespaceContext(NAMESPACE_PREFIX, namespace));
        }
        else
        {
            prefix = XMLConstants.DEFAULT_NS_PREFIX;
            xpath.setNamespaceContext(new XPathNamespaceContext(XMLConstants.DEFAULT_NS_PREFIX));
        }
        
        NodeList nodes;
        NodeList children;
        Element element;
        
        // retrieve display-name entries
        nodes = (NodeList)xpath.evaluate("/"+prefix+"web-app/"+prefix+"display-name", document, XPathConstants.NODESET);
        if (nodes != null)
        {
            DisplayName d;
            for (int i = 0, size = nodes.getLength(); i < size; i++)
            {
                element = (Element)nodes.item(i);
                String lang = element.getAttributeNS(XMLConstants.XML_NS_URI, "lang");
                if (lang == null)
                {
                    lang = "en";
                }
                d = pa.getDisplayName(JetspeedLocale.convertStringToLocale(lang));
                if (d == null)
                {
                    d = pa.addDisplayName(lang);
                }
                // else: overwrite display-name with last found entry
                
                d.setDisplayName(element.getTextContent().trim());
            }
        }

        // retrieve description entries
        nodes = (NodeList)xpath.evaluate("/"+prefix+"web-app/"+prefix+"description", document, XPathConstants.NODESET);
        if (nodes != null)
        {
            Description d;
            for (int i = 0, size = nodes.getLength(); i < size; i++)
            {
                element = (Element)nodes.item(i);
                String lang = element.getAttributeNS(XMLConstants.XML_NS_URI, "lang");
                if (lang == null)
                {
                    lang = "en";
                }
                d = pa.getDescription(JetspeedLocale.convertStringToLocale(lang));
                if (d == null)
                {
                    d = pa.addDescription(lang);
                }
                // else: overwrite description with last found entry
                
                d.setDescription(element.getTextContent().trim());
            }
        }
        
        // retrieve security-role
        nodes = (NodeList)xpath.evaluate("/"+prefix+"web-app/"+prefix+"security-role", document, XPathConstants.NODESET);
        if (nodes != null)
        {
            String roleName;
            SecurityRole r;
            Description d;
            for (int i = 0, nsize = nodes.getLength(); i < nsize; i++)
            {
                element = (Element)nodes.item(i);
                children = element.getElementsByTagName("role-name");
                if (children != null && children.getLength() != 0)
                {
                    roleName = children.item(0).getTextContent().trim();
                    if (roleName.length() > 0)
                    {
                        r = null;
                        for (SecurityRole sr : pa.getSecurityRoles())
                        {
                            if (sr.getName().equals(roleName))
                            {
                                r = sr;
                                break;
                            }
                        }
                        if (r == null)
                        {
                            r = pa.addSecurityRole(roleName);
                        }
                        // else: overwrite or merge existing descriptions with those of this last found entry
                        
                        children = element.getElementsByTagName("description");
                        if (children != null)
                        {
                            for (int j = 0, csize = children.getLength(); j < csize; j++)
                            {
                                element = (Element)children.item(j);
                                String lang = element.getAttributeNS(XMLConstants.XML_NS_URI, "lang");
                                if (lang == null)
                                {
                                    lang = "en";
                                }
                                if (r.getDescription(JetspeedLocale.convertStringToLocale(lang)) == null)
                                {
                                    d = r.addDescription(lang);
                                    d.setDescription(element.getTextContent());
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // retrieve locale-encoding-mapping
        nodes = (NodeList)xpath.evaluate("/"+prefix+"web-app/"+prefix+"locale-encoding-mapping-list/"+prefix+"locale-encoding-mapping", document, XPathConstants.NODESET);
        if (nodes != null)
        {
            String locale;
            String encoding;
            
            for (int i = 0, nsize = nodes.getLength(); i < nsize; i++)
            {
                element = (Element)nodes.item(i);
                children = element.getElementsByTagName("locale");
                if (children != null && children.getLength() != 0)
                {
                    locale = children.item(0).getTextContent().trim();
                    if (locale.length() > 0)
                    {
                        
                        children = element.getElementsByTagName("encoding");
                        if (children != null && children.getLength() != 0)
                        {
                            encoding = children.item(0).getTextContent().trim();
                            if (encoding.length() > 0)
                            {
                                pa.addLocaleEncodingMapping(JetspeedLocale.convertStringToLocale(locale), encoding);
                            }
                        }
                    }
                }
            }
        }
    }
    
    protected PortletApplication upgrade(PortletApplicationDefinition pa)
    {
        PortletApplication jpa = new PortletApplicationDefinitionImpl();        
        jpa.setDefaultNamespace(pa.getDefaultNamespace());
        jpa.setResourceBundle(pa.getResourceBundle());
        jpa.setVersion(pa.getVersion());
        for (org.apache.pluto.container.om.portlet.PortletDefinition pd : pa.getPortlets())
        {
            PortletDefinition jpd = jpa.addPortlet(pd.getPortletName());
            upgradePortlet(jpd, pd);
        }
        for (org.apache.pluto.container.om.portlet.ContainerRuntimeOption cro : pa.getContainerRuntimeOptions())
        {
            ContainerRuntimeOption jcro = jpa.addContainerRuntimeOption(cro.getName());
            for (String value : cro.getValues())
            {
                jcro.addValue(value);
            }
        }
        for (org.apache.pluto.container.om.portlet.CustomPortletMode cpm : pa.getCustomPortletModes())
        {
            CustomPortletMode jcpm = jpa.addCustomPortletMode(cpm.getPortletMode());
            jcpm.setPortalManaged(cpm.isPortalManaged());
            for (org.apache.pluto.container.om.portlet.Description desc : cpm.getDescriptions())
            {
                Description jdesc = jcpm.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }            
        }
        for (org.apache.pluto.container.om.portlet.CustomWindowState cws : pa.getCustomWindowStates())
        {
            CustomWindowState jcws = jpa.addCustomWindowState(cws.getWindowState());
            for (org.apache.pluto.container.om.portlet.Description desc : cws.getDescriptions())
            {
                Description jdesc = jcws.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }            
        }        
        for (org.apache.pluto.container.om.portlet.EventDefinition ed : pa.getEventDefinitions())
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
            for (org.apache.pluto.container.om.portlet.Description desc : ed.getDescriptions())
            {
                Description jdesc = jed.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }                        
        }
        for (org.apache.pluto.container.om.portlet.FilterMapping fm : pa.getFilterMappings())
        {
            FilterMapping jfm = jpa.addFilterMapping(fm.getFilterName());
            for (String portletName : fm.getPortletNames())
            {
                jfm.addPortletName(portletName);
            }
        }
        for (org.apache.pluto.container.om.portlet.Filter f : pa.getFilters())
        {
            Filter jf = jpa.addFilter(f.getFilterName());
            jf.setFilterClass(f.getFilterClass());
            for (org.apache.pluto.container.om.portlet.Description desc : f.getDescriptions())
            {
                Description jdesc = jf.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }                                   
            for (org.apache.pluto.container.om.portlet.DisplayName dn : f.getDisplayNames())
            {
                DisplayName jdn = jf.addDisplayName(dn.getLang());
                jdn.setDisplayName(dn.getDisplayName());
            }
            for (org.apache.pluto.container.om.portlet.InitParam ip : f.getInitParams())
            {
                InitParam jip = jf.addInitParam(ip.getParamName());
                jip.setParamValue(ip.getParamValue());
                for (org.apache.pluto.container.om.portlet.Description desc : ip.getDescriptions())
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
        for (org.apache.pluto.container.om.portlet.Listener l : pa.getListeners())
        {
            Listener jl = jpa.addListener(l.getListenerClass());
            for (org.apache.pluto.container.om.portlet.Description desc : l.getDescriptions())
            {
                Description jdesc = jl.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }                                        
            for (org.apache.pluto.container.om.portlet.DisplayName dn : l.getDisplayNames())
            {
                DisplayName jdn = jl.addDisplayName(dn.getLang());
                jdn.setDisplayName(dn.getDisplayName());
            }
        }
        for (org.apache.pluto.container.om.portlet.PublicRenderParameter prd : pa.getPublicRenderParameters())
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
            for (org.apache.pluto.container.om.portlet.Description desc : prd.getDescriptions())
            {
                Description jdesc = jprp.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }
        }
        for (org.apache.pluto.container.om.portlet.SecurityConstraint sc :  pa.getSecurityConstraints())
        {
            SecurityConstraint jsc = jpa.addSecurityConstraint(sc.getUserDataConstraint().getTransportGuarantee());
            for (org.apache.pluto.container.om.portlet.DisplayName dn : sc.getDisplayNames())
            {
                DisplayName jdn = jsc.addDisplayName(dn.getLang());
                jdn.setDisplayName(dn.getDisplayName());
            }
            for (String portletName : sc.getPortletNames())
            {
                jsc.addPortletName(portletName);
            }            
        }
        for (org.apache.pluto.container.om.portlet.UserAttribute ua : pa.getUserAttributes())
        {
            UserAttribute jua = jpa.addUserAttribute(ua.getName());
            for (org.apache.pluto.container.om.portlet.Description desc : ua.getDescriptions())
            {
                Description jdesc = jua.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }                                                    
        }
        return jpa;
    }

    protected void upgradePortlet(PortletDefinition jpd, org.apache.pluto.container.om.portlet.PortletDefinition pd)
    {
        jpd.setCacheScope(pd.getCacheScope());
        jpd.setExpirationCache(pd.getExpirationCache());
        jpd.setPortletClass(pd.getPortletClass());
        jpd.setResourceBundle(pd.getResourceBundle());
        jpd.setPreferenceValidatorClassname(pd.getPortletPreferences().getPreferencesValidator());
        for (org.apache.pluto.container.om.portlet.Preference preference : pd.getPortletPreferences().getPortletPreferences())
        {
            Preference jpref = jpd.getDescriptorPreferences().addPreference(preference.getName());
            jpref.setReadOnly(preference.isReadOnly());
            for (String value : preference.getValues())
            {
                jpref.addValue(value);
            }
            
        }        
        for (org.apache.pluto.container.om.portlet.ContainerRuntimeOption cro : pd.getContainerRuntimeOptions())
        {
            ContainerRuntimeOption jcro = jpd.addContainerRuntimeOption(cro.getName());
            for (String value : cro.getValues())
            {
                jcro.addValue(value);
            }
        }
        for (org.apache.pluto.container.om.portlet.Description desc : pd.getDescriptions())
        {
            Description jdesc = jpd.addDescription(desc.getLang());
            jdesc.setDescription(desc.getDescription());
        }                        
        for (org.apache.pluto.container.om.portlet.DisplayName dn : pd.getDisplayNames())
        {
            DisplayName jdn = jpd.addDisplayName(dn.getLang());
            jdn.setDisplayName(dn.getDisplayName());
        }
        for (org.apache.pluto.container.om.portlet.InitParam ip : pd.getInitParams())
        {
            InitParam jip = jpd.addInitParam(ip.getParamName());
            jip.setParamValue(ip.getParamValue());
            for (org.apache.pluto.container.om.portlet.Description desc : ip.getDescriptions())
            {
                Description jdesc = jip.addDescription(desc.getLang());
                jdesc.setDescription(desc.getDescription());
            }                                        
        }
        for (org.apache.pluto.container.om.portlet.SecurityRoleRef srr : pd.getSecurityRoleRefs())
        {
            SecurityRoleRef jsrr = jpd.addSecurityRoleRef(srr.getRoleName());
            jsrr.setRoleLink(srr.getRoleLink());
        }
        
        // First load the required default PortletInfo Language using the English Locale
        Language defaultLanguage = addLanguage(jpd, pd.getPortletInfo(), JetspeedLocale.getDefaultLocale(), false);
        for (String localeString : pd.getSupportedLocales())
        {
            Locale locale = JetspeedLocale.convertStringToLocale(localeString);
            if (locale.equals(JetspeedLocale.getDefaultLocale()))
            {
                defaultLanguage.setSupportedLocale(true);
            }
            else
            {
                addLanguage(jpd, pd.getPortletInfo(), locale, true);
            }
        }
        
        for (org.apache.pluto.container.om.portlet.EventDefinitionReference ed : pd.getSupportedProcessingEvents())
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
        for (org.apache.pluto.container.om.portlet.EventDefinitionReference ed : pd.getSupportedPublishingEvents())
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
        for (org.apache.pluto.container.om.portlet.Supports supports : pd.getSupports())
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
    
    protected Language addLanguage(PortletDefinition jpd, PortletInfo info, Locale locale, boolean supportedLocale)
    {                
        Language l = jpd.addLanguage(locale);
        l.setSupportedLocale(supportedLocale);
        l.setTitle(info.getTitle());
        l.setShortTitle(info.getShortTitle());
        l.setKeywords(info.getKeywords());
        if (locale != null && jpd.getResourceBundle() != null)
        {
            try
            {
                ResourceBundle bundle = ResourceBundle.getBundle(jpd.getResourceBundle(), locale, Thread.currentThread().getContextClassLoader());
                String value = bundle.getString(Language.JAVAX_PORTLET_TITLE);
                if (value != null && !value.equals(""))
                {
                    // use the value provided by the resource bundle
                    l.setTitle(value);
                }
                value = bundle.getString(Language.JAVAX_PORTLET_SHORT_TITLE);
                if (value != null && !value.equals(""))
                {
                    // use the value provided by the resource bundle
                    l.setShortTitle(value);
                }
                value = bundle.getString(Language.JAVAX_PORTLET_KEYWORDS);
                if (value != null && !value.equals(""))
                {
                    // user the value provided by the resource bundle
                    l.setKeywords(value);
                }
            }
            catch (MissingResourceException e)
            {
                // ignore
            }
        }
        return l;
    }
    
    protected void loadJetspeedPortletDescriptor(PortletApplication app, InputStream in) throws IOException
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
}
