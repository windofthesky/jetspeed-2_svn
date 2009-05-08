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
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


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
    
    public static final String JETSPEED_CONTAINER = "JetspeedContainer";
    public static final String JETSPEED_SERVLET_CLASS = "org.apache.jetspeed.container.JetspeedContainerServlet";
    public static final String JETSPEED_SERVLET_DISPLAY_NAME = "Jetspeed Container";
    public static final String JETSPEED_SERVLET_DESCRIPTION = "MVC Servlet for Jetspeed Portlet Applications";
    public static final String NAMESPACE_PREFIX = "js";
    protected static final String WEB_XML_PATH = "WEB-INF/web.xml";

    private Document document;
    private String namespace;
    private String prefix;
    private Element  root;
    private String portletApplication;
    private boolean changed = false;
    private boolean portletTaglibAdded = false;
    private boolean portlet20TaglibAdded = false;
    private XPath xpath;

    
    
    public JetspeedWebApplicationRewriter(Document doc, String portletApplication)
    {
        this(doc);
        this.portletApplication = portletApplication;
    }

    public JetspeedWebApplicationRewriter(Document doc)
    {
        this.document = doc;
        this.root = doc.getDocumentElement();
        this.namespace = root.getNamespaceURI();
        xpath = XPathFactory.newInstance().newXPath();
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
            Element root = document.getDocumentElement();
            Object jetspeedServlet =xpath.evaluate(getJetspeedServletXPath(), document, XPathConstants.NODE);
            Object jetspeedServletMapping = xpath.evaluate(getJetspeedServletMappingXPath(), document, XPathConstants.NODE);
            Object portletTaglib = xpath.evaluate(getPortletTagLibXPath(), document, XPathConstants.NODE);
            Object portlet20Taglib = xpath.evaluate(getPortlet20TagLibXPath(), document, XPathConstants.NODE);
            
            if (!document.hasChildNodes())
            {
                root = document.createElement("web-app");
                document.appendChild(root);
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
                    Element jetspeedServletElement = (Element)((Element)jetspeedServlet).getParentNode();
                    if (null == xpath.evaluate(prefix+"init-param/"+prefix+"param-name[contains(child::text(), \"contextName\")]",jetspeedServletElement, XPathConstants.NODE))
                    {
                      insertContextNameParam(jetspeedServletElement);
                    }
                    if (null == xpath.evaluate(prefix+"load-on-startup", jetspeedServletElement, XPathConstants.NODE))
                    {
                        insertLoadOnStartup(jetspeedServletElement);
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
            if(portlet20Taglib == null)
            {
                insertPortlet20TagLib(root);
                changed = true;
                portlet20TaglibAdded = true;
            }
            
        }
        catch (Exception e)
        {
            throw new Exception("Unable to process web.xml for infusion " + e.toString(), e);
        }
    
    }
    
    protected void insertContextNameParam(Element jetspeedServletElement)
    {
        String namespace = jetspeedServletElement.getNamespaceURI();
        Element init2Param = jetspeedServletElement.getOwnerDocument().createElementNS(namespace, "init-param");        
        jetspeedServletElement.appendChild(init2Param);                    
        Element param2Name = jetspeedServletElement.getOwnerDocument().createElementNS(namespace, "param-name");
        param2Name.setTextContent("contextName");
        Element param2Value = jetspeedServletElement.getOwnerDocument().createElementNS(namespace, "param-value");
        param2Value.setTextContent(portletApplication);
        init2Param.appendChild(param2Name);
        init2Param.appendChild(param2Value);        
    }
    
    protected void insertLoadOnStartup(Element jetspeedServletElement)
    {
        String namespace = jetspeedServletElement.getNamespaceURI();
        Element loadOnStartup = jetspeedServletElement.getOwnerDocument().createElementNS(namespace, "load-on-startup");
        loadOnStartup.setTextContent("0");
        jetspeedServletElement.appendChild(loadOnStartup);        
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
     *            element representing the &lt; web-app &gt;
     * @param toInsert
     *            element to insert into the web.xml hierarchy.
     * @param elementsBefore
     *            an array of web.xml elements that should be defined before the
     *            element we want to insert. This order should be the order
     *            defined by the web.xml's DTD or XSD type definition.
     */
    protected void insertElementCorrectly( Element root, Element toInsert, String[] elementsBefore )
    {
        NodeList allChildren = root.getChildNodes();
        List<String> elementsBeforeList = Arrays.asList(elementsBefore);
        Node insertBefore = null;
        for (int i = 0; i < allChildren.getLength(); i++)
        {
            Node node = allChildren.item(i);
            if (insertBefore == null)
            {
                insertBefore = node;
            }
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                if (elementsBeforeList.contains(node.getNodeName()))
                {
                    insertBefore = null;
                }
                else
                {
                    break;
                }
            }
        }
        if (insertBefore == null)
        {
            root.appendChild(toInsert);
        }
        else
        {
            root.insertBefore(toInsert, insertBefore);
        }
    }
    
    protected String getNamespacePrefix()
    {
        return prefix;
    }
    
    /**
     * @return Returns the portletTaglibAdded.
     */
    public boolean isPortletTaglibAdded()
    {
        return portletTaglibAdded;
    }
    
    /**
     * @return Returns the portletTaglibAdded.
     */
    public boolean isPortlet20TaglibAdded()
    {
        return portlet20TaglibAdded;
    }
    
    protected XPath getXPath()
    {
        return xpath;
    }
    
    /**
     * Returns the jetspeed servlet xpath.
     * The returned path must contain the namespace prefix.
     * 
     * @return jetspeed servlet xpath
     */
    protected abstract String getJetspeedServletXPath();
    
    /**
     * Returns the jetspeed servlet mapping xpath.
     * The returned path must contain the namespace prefix.
     * 
     * @return jetspeed servlet mapping xpath
     */
    protected abstract String getJetspeedServletMappingXPath();
    
    /**
     * Returns the portlet taglib xpath.
     * The returned path must contain the namespace prefix.
     * 
     * @return portlet taglib xpath
     */
    protected abstract String getPortletTagLibXPath();
    
    /**
     * Returns the portlet 2.0 taglib xpath.
     * The returned path must contain the namespace prefix.
     * 
     * @return portlet 2.0 taglib xpath
     */
    protected abstract String getPortlet20TagLibXPath();
    
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

    /**
     * Inserts the portlet 2.0 taglib into web.xml
     * 
     * @param root
     * @throws Exception
     */
    protected abstract void insertPortlet20TagLib(Element root) throws Exception;
}
