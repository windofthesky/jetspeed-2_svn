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
package org.apache.jetspeed.tools.deploy;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.xpath.XPathConstants;

/**
 * Utilities for manipulating the web.xml deployment descriptor version 2.4
 * 
 * @author Nicolas Dutertry
 * @version $Id$
 */
class JetspeedWebApplicationRewriter2_4 extends JetspeedWebApplicationRewriter
{
    public static final String JETSPEED_SERVLET_XPATH = "/js:web-app/js:servlet/js:servlet-name[contains(child::text(), \"JetspeedContainer\")]";
    public static final String JETSPEED_SERVLET_MAPPING_XPATH = "/js:web-app/js:servlet-mapping/js:servlet-name[contains(child::text(), \"JetspeedContainer\")]";
    public static final String JSP_CONFIG_XPATH = "/js:web-app/js:jsp-config";
    public static final String PORTLET_TAGLIB_XPATH = "/js:web-app/js:jsp-config/js:taglib/js:taglib-uri[contains(child::text(), \"http://java.sun.com/portlet\")]";
    public static final String PORTLET20_TAGLIB_XPATH = "/js:web-app/js:jsp-config/js:taglib/js:taglib-uri[contains(child::text(), \"http://java.sun.com/portlet_2_0\")]";
    
    public JetspeedWebApplicationRewriter2_4(Document doc, String portletApplication)
    {
        super(doc, portletApplication);
    }

    public JetspeedWebApplicationRewriter2_4(Document doc)
    {
        super(doc);
    }
    
    /**
     * Returns the jetspeed servlet xpath.
     * 
     * @return jetspeed servlet xpath
     */
    protected String getJetspeedServletXPath()
    {
        return JETSPEED_SERVLET_XPATH.replace("js:", getNamespacePrefix());
    }
    
    /**
     * Returns the jetspeed servlet mapping xpath.
     * 
     * @return jetspeed servlet mapping xpath
     */
    protected String getJetspeedServletMappingXPath()
    {
        return JETSPEED_SERVLET_MAPPING_XPATH.replace("js:", getNamespacePrefix());
    }

    /**
     * Returns the portlet taglib xpath.
     * 
     * @return portlet taglib xpath
     */
    protected String getPortletTagLibXPath()
    {
        return PORTLET_TAGLIB_XPATH.replace("js:", getNamespacePrefix());
    }

    /**
     * Returns the portlet 2.0 taglib xpath.
     * 
     * @return portlet 2.0 taglib xpath
     */
    protected String getPortlet20TagLibXPath()
    {
        return PORTLET20_TAGLIB_XPATH.replace("js:", getNamespacePrefix());
    }

    /**
     * Inserts the jetspeed servlet into web.xml
     * 
     * @param root
     * @throws Exception
     */
    protected void insertJetspeedServlet(Element root) throws Exception
    {
        String namespace = root.getNamespaceURI();
        Element jetspeedServletElement = root.getOwnerDocument().createElementNS(namespace, "servlet");
        Element servletName = root.getOwnerDocument().createElementNS(namespace, "servlet-name");
        servletName.setTextContent(JETSPEED_CONTAINER);
        Element servletDspName = root.getOwnerDocument().createElementNS(namespace, "display-name");
        servletDspName.setTextContent(JETSPEED_SERVLET_DISPLAY_NAME);
        Element servletDesc = root.getOwnerDocument().createElementNS(namespace, "description");
        servletDesc.setTextContent(JETSPEED_SERVLET_DESCRIPTION);
        Element servletClass = root.getOwnerDocument().createElementNS(namespace, "servlet-class");
        servletClass.setTextContent(JETSPEED_SERVLET_CLASS);
        jetspeedServletElement.appendChild(servletDesc);
        jetspeedServletElement.appendChild(servletDspName);
        jetspeedServletElement.appendChild(servletName);
        jetspeedServletElement.appendChild(servletClass);
        insertContextNameParam(jetspeedServletElement);
        insertLoadOnStartup(jetspeedServletElement);
        insertElementCorrectly(root, jetspeedServletElement, getElementsBeforeServlet());
    }

    /**
     * Inserts the jetspeed servlet mapping into web.xml
     * 
     * @param root
     * @throws Exception
     */
    protected void insertJetspeedServletMapping(Element root) throws Exception
    {
        String namespace = root.getNamespaceURI();
        Element jetspeedServletMappingElement = root.getOwnerDocument().createElementNS(namespace, "servlet-mapping");
        
        Element servletMapName = root.getOwnerDocument().createElementNS(namespace, "servlet-name");
        servletMapName.setTextContent(JETSPEED_CONTAINER);
        Element servletUrlPattern = root.getOwnerDocument().createElementNS(namespace, "url-pattern");
        servletUrlPattern.setTextContent("/container/*");

        jetspeedServletMappingElement.appendChild(servletMapName);
        jetspeedServletMappingElement.appendChild(servletUrlPattern);

        insertElementCorrectly(root, jetspeedServletMappingElement, getElementsBeforeServletMapping());
    }

    /**
     * Inserts the portlet taglib into web.xml
     * 
     * @param root
     * @throws Exception
     */
    protected void insertPortletTagLib(Element root) throws Exception
    {
        String namespace = root.getNamespaceURI();
        Element jspConfig = (Element)getXPath().evaluate(JSP_CONFIG_XPATH, root.getOwnerDocument(), XPathConstants.NODE);
        if (jspConfig == null)
        {
            jspConfig = root.getOwnerDocument().createElementNS(namespace,"jsp-config");
            insertElementCorrectly(root, jspConfig, getElementsBeforeJspConfig());
        }
        Element taglib = root.getOwnerDocument().createElementNS(namespace, "taglib");
        Element taguri = root.getOwnerDocument().createElementNS(namespace, "taglib-uri");
        taguri.setTextContent("http://java.sun.com/portlet");
        Element taglocation = root.getOwnerDocument().createElementNS(namespace, "taglib-location");
        taglocation.setTextContent("/WEB-INF/tld/portlet.tld");
        
        taglib.appendChild(taguri);
        taglib.appendChild(taglocation);
        
        insertElementCorrectly(jspConfig, taglib, getElementsBeforeJspConfigTaglib());
    }

    /**
     * Inserts the portlet 2.0 taglib into web.xml
     * 
     * @param root
     * @throws Exception
     */
    protected void insertPortlet20TagLib(Element root) throws Exception
    {
        String namespace = root.getNamespaceURI();
        Element jspConfig = (Element)getXPath().evaluate(JSP_CONFIG_XPATH, root.getOwnerDocument(), XPathConstants.NODE);
        if (jspConfig == null)
        {
            jspConfig = root.getOwnerDocument().createElementNS(namespace,"jsp-config");
            insertElementCorrectly(root, jspConfig, getElementsBeforeJspConfig());
        }
        Element taglib = root.getOwnerDocument().createElementNS(namespace, "taglib");
        Element taguri = root.getOwnerDocument().createElementNS(namespace, "taglib-uri");
        taguri.setTextContent("http://java.sun.com/portlet_2_0");
        Element taglocation = root.getOwnerDocument().createElementNS(namespace, "taglib-location");
        taglocation.setTextContent("/WEB-INF/tld/portlet_2_0.tld");
        
        taglib.appendChild(taguri);
        taglib.appendChild(taglocation);
        
        insertElementCorrectly(jspConfig, taglib, getElementsBeforeJspConfigTaglib());
    }

    /**
     * Return 'web-app' element names before 'servlet' in the ordered web.xml 2.4 schema.
     *
     * @return element names
     */
    protected String [] getElementsBeforeServlet() {
        return new String[]{"description", "display-name", "icon", "distributable", "context-param", "filter",
                "filter-mapping", "listener", "servlet"};
    }

    /**
     * Return 'web-app' element names before 'servlet-mapping' in the ordered web.xml 2.4 schema.
     *
     * @return element names
     */
    protected String [] getElementsBeforeServletMapping() {
        return new String[]{"description", "display-name", "icon", "distributable", "context-param", "filter",
                "filter-mapping", "listener", "servlet", "servlet-mapping"};
    }

    /**
     * Return 'web-app' element names before 'jsp-config' in the ordered web.xml 2.4 schema.
     *
     * @return element names
     */
    protected String [] getElementsBeforeJspConfig() {
        return new String[]{"description", "display-name", "icon", "distributable", "context-param", "filter",
                "filter-mapping", "listener", "servlet", "servlet-mapping", "session-config", "mime-mapping",
                "welcome-file-list", "error-page", "jsp-config"};
    }

    /**
     * Return 'jsp-config' element names before 'taglib' in the ordered web.xml 2.4 schema.
     *
     * @return element names
     */
    protected String [] getElementsBeforeJspConfigTaglib() {
        return new String[]{"taglib"};
    }
}
