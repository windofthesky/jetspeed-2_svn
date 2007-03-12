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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.xpath.XPath;

/**
 * Utilities for manipulating the web.xml deployment descriptor
 * 
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 * @author <a href="mailto:mavery@einnovation.com">Matt Avery </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: WebDescriptorUtilities.java,v 1.2 2004/05/12 22:25:04 taylor
 *                Exp $
 */
public abstract class JetspeedWebApplicationRewriter
{
    public static final String JETSPEED_CONTAINER = "JetspeedContainer";
    public static final String JETSPEED_SERVLET_CLASS = "org.apache.jetspeed.container.JetspeedContainerServlet";
    public static final String JETSPEED_SERVLET_DISPLAY_NAME = "Jetspeed Container";
    public static final String JETSPEED_SERVLET_DESCRIPTION = "MVC Servlet for Jetspeed Portlet Applications";
    public static final String NAMESPACE_PREFIX = "js";
    protected static final String WEB_XML_PATH = "WEB-INF/web.xml";

    private Document document;
    private String portletApplication;
    private boolean changed = false;
    private boolean portletTaglibAdded = false;
    
    public JetspeedWebApplicationRewriter(Document doc, String portletApplication)
    {
            this.document = doc;
            this.portletApplication = portletApplication;
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
        try
        {
            Element root = document.getRootElement();
        
            Object jetspeedServlet = getXPath(getJetspeedServletXPath()).selectSingleNode(document);
            Object jetspeedServletMapping = getXPath(getJetspeedServletMappingXPath()).selectSingleNode(document);
            Object portletTaglib = getXPath(getPortletTagLibXPath()).selectSingleNode(document);
            
            if (!document.hasRootElement())
            {
                root = new Element("web-app");
                document.setRootElement(root);
            }
        
            if (jetspeedServlet == null)
            {
                insertJetspeedServlet(root);
                changed = true;
            }
            else
            {
                // double check for register at Init
                if (jetspeedServlet instanceof Element)
                {
                    Parent jetspeedServletElement =((Element)jetspeedServlet).getParent();
                    if (null == getXPath("js:init-param/js:param-name[contains(child::text(), \"contextName\")]").selectSingleNode(jetspeedServletElement))
                    {
                      insertContextNameParam((Element)jetspeedServletElement);
                    }
                    if (null == getXPath("js:load-on-startup").selectSingleNode(jetspeedServletElement))
                    {
                        insertLoadOnStartup((Element) jetspeedServletElement);
                    }
                }
            }
    
            if (jetspeedServletMapping == null)
            {
                insertJetspeedServletMapping(root);
                changed = true;
            }
            
            if(portletTaglib == null)
            {
                insertPortletTagLib(root);
                changed = true;
                portletTaglibAdded = true;
            }
        }
        catch (Exception e)
        {
            throw new Exception("Unable to process web.xml for infusion " + e.toString(), e);
        }
    
    }
    
    protected void insertContextNameParam(Element jetspeedServletElement)
    {
        Namespace namespace = jetspeedServletElement.getNamespace();
        Element param2Name = new Element("param-name", namespace).addContent("contextName");
        Element param2Value = new Element("param-value", namespace).addContent(portletApplication); 
        Element init2Param = new Element("init-param", namespace);
        init2Param.addContent(param2Name);
        init2Param.addContent(param2Value);
        jetspeedServletElement.addContent(init2Param);                    
        
    }
    
    protected void insertLoadOnStartup(Element jetspeedServletElement)
    {
        Namespace namespace = jetspeedServletElement.getNamespace();
        Element loadOnStartup = new Element("load-on-startup", namespace).addContent("0");
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
        int count = 0;
        for (int i = 0; i < allChildren.size(); i++)
        {
            Element element = (Element) allChildren.get(i);
            if (elementsBeforeList.contains(element.getName()))
            {
                // determine the Content index of the element to insert after
                insertAfter = root.indexOf(element);
            }
            count++;
        }
    
        insertAfter = (count == 0) ? 0 : insertAfter + 1;
        
        try
        {
            root.addContent(insertAfter, toInsert);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            root.addContent(toInsert);
        }
    }
    
    /**
     * @return Returns the portletTaglibAdded.
     */
    public boolean isPortletTaglibAdded()
    {
        return portletTaglibAdded;
    }
    
    /**
     * Returns the xpath containing the namespace prefix 'js' mapped to the document
     * default namespace.
     * 
     * @param path
     * @return XPath
     * @throws JDOMException
     */
    protected XPath getXPath(String path) throws JDOMException
    {
        XPath xpath = XPath.newInstance(path);
        Element root = document.getRootElement();
        if(root != null)
        {
            if(StringUtils.isNotEmpty(root.getNamespaceURI()))
            {
                xpath.addNamespace(NAMESPACE_PREFIX, root.getNamespaceURI());
            }
        }
        return xpath;
    }
    
    /**
     * Returns the jetspeed servlet xpath.
     * The returned path must contain the namespace prefix 'js'.
     * 
     * @return jetspeed servlet xpath
     */
    protected abstract String getJetspeedServletXPath();
    
    /**
     * Returns the jetspeed servlet mapping xpath.
     * The returned path must contain the namespace prefix 'js'.
     * 
     * @return jetspeed servlet mapping xpath
     */
    protected abstract String getJetspeedServletMappingXPath();
    
    /**
     * Returns the portlet taglib xpath.
     * The returned path must contain the namespace prefix 'js'.
     * 
     * @return portlet taglib xpath
     */
    protected abstract String getPortletTagLibXPath();
    
    /**
     * Inserts the jetspeed servlet into web.xml
     * 
     * @param root
     * @throws Exception
     */
    protected abstract void insertJetspeedServlet(Element root) throws Exception;
    
    /**
     * Inserts the jetspeed servlet mapping into web.xml
     * 
     * @param root
     * @throws Exception
     */
    protected abstract void insertJetspeedServletMapping(Element root) throws Exception;
    
    /**
     * Inserts the portlet taglib into web.xml
     * 
     * @param root
     * @throws Exception
     */
    protected abstract void insertPortletTagLib(Element root) throws Exception;
}
