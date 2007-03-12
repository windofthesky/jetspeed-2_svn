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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

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
    
    protected static final String[] ELEMENTS_BEFORE_SERVLET = new String[]{"description", "display-name", "icon", 
            "distributable", "context-param", "filter", "filter-mapping", "listener", "servlet"};
    protected static final String[] ELEMENTS_BEFORE_SERVLET_MAPPING = new String[]{"description", "display-name", "icon", 
            "distributable", "context-param", "filter", "filter-mapping", "listener", "servlet",
            "servlet-mapping"};
    
    protected static final String[] ELEMENTS_BEFORE_JSP_CONFIG = new String[]{"description", "display-name", "icon", 
        "distributable", "context-param", "filter", "filter-mapping", "listener", "servlet",
        "servlet-mapping", "session-config", "mime-mapping", "welcome-file-list", "error-page", "jsp-config"};
    
    protected static final String[] ELEMENTS_BEFORE_TAGLIB_MAPPING = new String[]{"taglib"};
      
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
        return JETSPEED_SERVLET_XPATH;
    }
    
    /**
     * Returns the jetspeed servlet mapping xpath.
     * 
     * @return jetspeed servlet mapping xpath
     */
    protected String getJetspeedServletMappingXPath()
    {
        return JETSPEED_SERVLET_MAPPING_XPATH;
    }

    /**
     * Returns the portlet taglib xpath.
     * 
     * @return portlet taglib xpath
     */
    protected String getPortletTagLibXPath()
    {
        return PORTLET_TAGLIB_XPATH;
    }

    /**
     * Inserts the jetspeed servlet into web.xml
     * 
     * @param root
     * @throws Exception
     */
    protected void insertJetspeedServlet(Element root) throws Exception
    {
        Namespace namespace = root.getNamespace();
        Element jetspeedServletElement = new Element("servlet", namespace);
        Element servletName = new Element("servlet-name", namespace).addContent(JETSPEED_CONTAINER);
        Element servletDspName = new Element("display-name", namespace).addContent(JETSPEED_SERVLET_DISPLAY_NAME);
        Element servletDesc = new Element("description", namespace)
                .addContent(JETSPEED_SERVLET_DESCRIPTION);
        Element servletClass = new Element("servlet-class", namespace)
                .addContent(JETSPEED_SERVLET_CLASS);
        // order is important 
        jetspeedServletElement.addContent(servletDesc);
        jetspeedServletElement.addContent(servletDspName);
        jetspeedServletElement.addContent(servletName);
        jetspeedServletElement.addContent(servletClass);
        insertContextNameParam(jetspeedServletElement);
        insertLoadOnStartup(jetspeedServletElement);
        insertElementCorrectly(root, jetspeedServletElement, ELEMENTS_BEFORE_SERVLET);
    }

    /**
     * Inserts the jetspeed servlet mapping into web.xml
     * 
     * @param root
     * @throws Exception
     */
    protected void insertJetspeedServletMapping(Element root) throws Exception
    {
        Namespace namespace = root.getNamespace();
        Element jetspeedServletMappingElement = new Element("servlet-mapping", namespace);
        
        Element servletMapName = new Element("servlet-name", namespace).addContent(JETSPEED_CONTAINER);
        Element servletUrlPattern = new Element("url-pattern", namespace).addContent("/container/*");

        jetspeedServletMappingElement.addContent(servletMapName);
        jetspeedServletMappingElement.addContent(servletUrlPattern);

        insertElementCorrectly(root, jetspeedServletMappingElement, ELEMENTS_BEFORE_SERVLET_MAPPING);
    }

    /**
     * Inserts the portlet taglib into web.xml
     * 
     * @param root
     * @throws Exception
     */
    protected void insertPortletTagLib(Element root) throws Exception
    {
        Namespace namespace = root.getNamespace();
        Element jspConfig = (Element)getXPath(JSP_CONFIG_XPATH).selectSingleNode(root.getDocument());
        if(jspConfig == null)
        {
            jspConfig = new Element("jsp-config", namespace);
            insertElementCorrectly(root, jspConfig, ELEMENTS_BEFORE_JSP_CONFIG);
        }
        Element taglib = new Element ("taglib", namespace);
        Element taguri = new Element("taglib-uri", namespace).addContent("http://java.sun.com/portlet");
        Element taglocation = new Element("taglib-location", namespace).addContent("/WEB-INF/tld/portlet.tld");
        
        taglib.addContent(taguri);
        taglib.addContent(taglocation);
        
        insertElementCorrectly(jspConfig, taglib, ELEMENTS_BEFORE_TAGLIB_MAPPING);
    }
}
