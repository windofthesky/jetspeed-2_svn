/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.tools.deploy;

import java.io.InputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Utilities for manipulating the web.xml deployment descriptor
 * 
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 * @author <a href="mailto:mavery@einnovation.com">Matt Avery </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: WebDescriptorUtilities.java,v 1.2 2004/05/12 22:25:04 taylor
 *                Exp $
 */
public class JetspeedWebApplicationRewriter
{
    public static final String REGISTER_AT_INIT = "registerAtInit";
    public static final String JETSPEED_CONTAINER = "JetspeedContainer";
    public static final String JETSPEED_SERVLET_XPATH = "/web-app/servlet/servlet-name[contains(child::text(), \"JetspeedContainer\")]";
    public static final String REGISTER_AT_INIT_XPATH = "/init-param/param-name[contains(child::text(), \"registerAtInit\")]";
    public static final String JETSPEED_SERVLET_MAPPING_XPATH = "/web-app/servlet-mapping/servlet-name[contains(child::text(), \"JetspeedContainer\")]";
    protected static final String WEB_XML_PATH = "WEB-INF/web.xml";

    protected static final String[] ELEMENTS_BEFORE_SERVLET = new String[]{"icon", "display-name", "description",
            "distributable", "context-param", "filter", "filter-mapping", "listener", "servlet"};
    protected static final String[] ELEMENTS_BEFORE_SERVLET_MAPPING = new String[]{"icon", "display-name",
            "description", "distributable", "context-param", "filter", "filter-mapping", "listener", "servlet",
            "servlet-mapping"};
      
    private Document document;
    private String portletApplication;
    private boolean changed = false;
    private boolean registerAtInit = false;
    
    
    public JetspeedWebApplicationRewriter(Document doc, String portletApplication, boolean registerAtInit)
    {
            this.document = doc;
            this.portletApplication = portletApplication;
            this.registerAtInit = registerAtInit;
    }

    public JetspeedWebApplicationRewriter(Document doc)
    {
            this.document = doc;
    }
    
    /**
     * 
     * <p>
     * processWebXML
     * </p>
     * 
     * Infuses this PortletApplicationWar's web.xml file with
     * <code>servlet</code> and a <code>servlet-mapping</code> element for
     * the JetspeedContainer servlet. This is only done if the descriptor does
     * not already contain these items.
     * 
     * @throws MetaDataException
     *             if there is a problem infusing
     */
    public void processWebXML()
    throws Exception
    {
        SAXBuilder builder = new SAXBuilder();
        Writer webXmlWriter = null;
        InputStream webXmlIn = null;
    
        try
        {
            // Use the local dtd instead of remote dtd. This
            // allows to deploy the application offline
            builder.setEntityResolver(new EntityResolver()
            {
                public InputSource resolveEntity( java.lang.String publicId, java.lang.String systemId )
                        throws SAXException, java.io.IOException
                {
    
                    if (systemId.equals("http://java.sun.com/dtd/web-app_2_3.dtd"))
                    {
                        return new InputSource(getClass().getResourceAsStream("web-app_2_3.dtd"));
                    }
                    else return null;
                }
            });
    
    
            Element root = document.getRootElement();
        
            Object jetspeedServlet = XPath.selectSingleNode(document, JETSPEED_SERVLET_XPATH);
            Object jetspeedServletMapping = XPath.selectSingleNode(document, JETSPEED_SERVLET_MAPPING_XPATH);
            if (!document.hasRootElement())
            {
                root = new Element("web-app");
                document.setRootElement(root);
            }
        
            if (jetspeedServlet == null)
            {
                Element jetspeedServletElement = new Element("servlet");
                Element servletName = (Element) new Element("servlet-name").addContent(JETSPEED_CONTAINER);
                Element servletDspName = (Element) new Element("display-name").addContent("Jetspeed Container");
                Element servletDesc = (Element) new Element("description")
                        .addContent("MVC Servlet for Jetspeed Portlet Applications");
                Element servletClass = (Element) new Element("servlet-class")
                        .addContent("org.apache.jetspeed.container.JetspeedContainerServlet");
                jetspeedServletElement.addContent(servletName);
                jetspeedServletElement.addContent(servletDspName);
                jetspeedServletElement.addContent(servletDesc);
                jetspeedServletElement.addContent(servletClass);
                if (this.registerAtInit)
                {
                    insertRegisterAtInit(jetspeedServletElement);
                }
                insertElementCorrectly(root, jetspeedServletElement, ELEMENTS_BEFORE_SERVLET);
                changed = true;
            }
            else
            {
                // double check for register at Init
                if (this.registerAtInit && jetspeedServlet instanceof Element)
                {
                    Element jetspeedServletElement =(Element)jetspeedServlet;
                    if (null == XPath.selectSingleNode(jetspeedServletElement, REGISTER_AT_INIT_XPATH))
                    {
                        insertRegisterAtInit(jetspeedServletElement);
                    }
                }
            }
    
            if (jetspeedServletMapping == null)
            {
    
                Element jetspeedServletMappingElement = new Element("servlet-mapping");
    
                Element servletMapName = (Element) new Element("servlet-name").addContent(JETSPEED_CONTAINER);
                Element servletUrlPattern = (Element) new Element("url-pattern").addContent("/container/*");
    
                jetspeedServletMappingElement.addContent(servletMapName);
                jetspeedServletMappingElement.addContent(servletUrlPattern);
    
                insertElementCorrectly(root, jetspeedServletMappingElement, ELEMENTS_BEFORE_SERVLET_MAPPING);
                changed = true;
            }        
        }
        catch (Exception e)
        {
            throw new Exception("Unable to process web.xml for infusion " + e.toString(), e);
        }
    
    }
    
    private void insertRegisterAtInit(Element jetspeedServletElement)
    {
        Element paramName = (Element) new Element("param-name").addContent(REGISTER_AT_INIT);
        Element paramValue = (Element) new Element("param-value").addContent("1"); 
        Element initParam = new Element("init-param");
        initParam.addContent(paramName);
        initParam.addContent(paramValue);
        jetspeedServletElement.addContent(initParam);
        
        Element param2Name = (Element) new Element("param-name").addContent("portletApplication");
        Element param2Value = (Element) new Element("param-value").addContent(portletApplication); 
        Element init2Param = new Element("init-param");
        init2Param.addContent(param2Name);
        init2Param.addContent(param2Value);
        jetspeedServletElement.addContent(init2Param);                    
        
        Element loadOnStartup = (Element) new Element("load-on-startup").addContent("100");
        jetspeedServletElement.addContent(loadOnStartup);        
    }
    
    public boolean isChanged()
    {
        return changed;
    }
    
    /**
     * 
     * <p>
     * insertElementCorrectly
     * </p>
     * 
     * @param root
     *            JDom element representing the &lt; web-app &gt;
     * @param toInsert
     *            JDom element to insert into the web.xml hierarchy.
     * @param elementsBefore
     *            an array of web.xml elements that should be defined before the
     *            element we want to insert. This order should be the order
     *            defined by the web.xml's DTD type definition.
     */
    protected void insertElementCorrectly( Element root, Element toInsert, String[] elementsBefore )
    throws Exception
    {
        List allChildren = root.getChildren();
        List elementsBeforeList = Arrays.asList(elementsBefore);
        toInsert.detach();
        int insertAfter = 0;
        for (int i = 0; i < allChildren.size(); i++)
        {
            Element element = (Element) allChildren.get(i);
            if (elementsBeforeList.contains(element.getName()))
            {
                // determine the Content index of the element to insert after
                insertAfter = root.indexOf(element);
            }
        }
    
        try
        {
            root.addContent((insertAfter + 1), toInsert);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            root.addContent(toInsert);
        }
    }
    
    
}