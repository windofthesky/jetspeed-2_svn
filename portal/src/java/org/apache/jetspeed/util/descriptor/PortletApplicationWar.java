/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.util.descriptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.AllFileSelector;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.VFS;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.tools.pamanager.PortletApplicationException;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.common.SecurityRoleSet;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

/**
 * This class facilitates opertions a portlet applications WAR file or WAR file-like 
 * structure.
 * 
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 * @version $Id$
 */
public class PortletApplicationWar
{
    protected static final String PORTLET_XML_PATH = "WEB-INF/portlet.xml";
    protected static final String WEB_XML_PATH = "WEB-INF/web.xml";
    protected static final String EXTENDED_PORTLET_XML_PATH = "WEB-INF/jetspeed-portlet.xml";
    
    public static final String JETSPEED_SERVLET_XPATH = "/web-app/servlet[servlet-name=\"JetspeedContainer\"";
    public static final String JETSPEED_SERVLET_MAPPING_XPATH = "/web-app/servlet-mapping[servlet-name=\"JetspeedContainer\"]";

    protected static final Log log = LogFactory.getLog("deployment");

    protected String paName;
    protected String webAppContextRoot;
    protected Locale locale;
    protected String webAppDisplayName;
    protected FileObject warStruct;
    protected FileSystemManager fsManager;
    private MutableWebApplication webApp;
    private MutablePortletApplication portletApp;

    protected static final Element JETSPEED_SERVLET_ELEMENT = new Element("servlet");
    protected static final Element JETSPEED_SERVLET_MAPPING_ELEMENT = new Element("servlet-mapping");
    protected static final String[] ELEMENTS_BEFORE_SERVLET = new String[]{"icon", "display-name", "description",
            "distributable", "context-param", "filter", "filter-mapping", "listener"};
    protected static final String[] ELEMENTS_BEFORE_SERVLET_MAPPING = new String[]{"icon", "display-name",
            "description", "distributable", "context-param", "filter", "filter-mapping", "listener", "servlet"};

    static
    {
        Element servletName = new Element("servlet-name").addContent("JetspeedContainer");
        Element servletDspName = new Element("display-name").addContent("Jetspeed Container");
        Element servletDesc = new Element("description").addContent("MVC Servlet for Jetspeed Portlet Applications");
        Element servletClass = new Element("servlet-class")
                .addContent("org.apache.jetspeed.container.JetspeedContainerServlet");
        JETSPEED_SERVLET_ELEMENT.addContent(servletName);
        JETSPEED_SERVLET_ELEMENT.addContent(servletDspName);
        JETSPEED_SERVLET_ELEMENT.addContent(servletDesc);
        JETSPEED_SERVLET_ELEMENT.addContent(servletClass);

        Element servletMapName = new Element("servlet-name").addContent("JetspeedContainer");
        Element servletUrlPattern = new Element("url-pattern").addContent("/container/*");

        JETSPEED_SERVLET_MAPPING_ELEMENT.addContent(servletMapName);
        JETSPEED_SERVLET_MAPPING_ELEMENT.addContent(servletUrlPattern);
    }

    /**
     * @param warPath
     *                  Path to a war file or war file structure
     * @param paName
     *                  name of the portlet application the <code>warPath</code>
     *                  contains
     * @param webAppContextRoot
     *                  context root relative to the servlet container of this app
     * @param locale
     *                  locale to deploy to
     * @param webAppDisplayName
     * @throws IOException
     */
    public PortletApplicationWar( String warPath, String paName, String webAppContextRoot, Locale locale,
            String webAppDisplayName ) throws IOException
    {
        if (paName == null || paName.startsWith("/") || paName.startsWith("\\") || paName.endsWith("/")
                || paName.endsWith("\\"))
        {
            throw new IllegalStateException("Invalid paName \"" + paName
                    + "\".  paName cannot be null nor can it begin nor end with any slashes.");
        }

        fsManager = VFS.getManager();
        File warPathFile = new File(warPath).getAbsoluteFile();

        if (!warPathFile.exists())
        {
            throw new FileNotFoundException(warPathFile.getPath() + " does not exist.");
        }

        if (warPathFile.isDirectory())
        {
            warStruct = fsManager.resolveFile(warPathFile.toURL().toExternalForm());
        }
        else if (warPathFile.isFile())
        {
            warStruct = fsManager.resolveFile("jar:" + warPathFile.getAbsolutePath());
        }

        this.paName = paName;
        this.webAppContextRoot = webAppContextRoot;
        this.locale = locale;
        this.webAppDisplayName = webAppDisplayName;
    }

    /**
     * 
     * <p>
     * createWebApp
     * </p>
     * Creates a web applicaiton object based on the values in this WAR's
     * WEB-INF/web.xml
     * 
     * @return @throws
     *              PortletApplicationException
     * @throws IOException
     */
    public MutableWebApplication createWebApp() throws PortletApplicationException, IOException
    {
        Reader webXmlReader = getReader(WEB_XML_PATH);

        try
        {
            WebApplicationDescriptor webAppDescriptor = new WebApplicationDescriptor(webXmlReader, webAppContextRoot,
                    locale, webAppDisplayName);
            webApp = webAppDescriptor.createWebApplication();
            return webApp;
        }

        finally
        {
            try
            {
                if (webXmlReader != null)
                {
                    webXmlReader.close();
                }
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }

    }

    /**
     * 
     * <p>
     * createPortletApp
     * </p>
     * Creates a portlet application object based of the WAR file's
     * WEB-INF/portlet.xml
     * 
     * @return @throws
     *              PortletApplicationException
     * @throws IOException
     */
    public MutablePortletApplication createPortletApp() throws PortletApplicationException, IOException
    {
        Reader portletXmlReader = getReader(PORTLET_XML_PATH);
        try
        {
            PortletApplicationDescriptor paDescriptor = new PortletApplicationDescriptor(portletXmlReader, paName);
            portletApp = paDescriptor.createPortletApplication();
            // validate(portletApplication);

            try
            {
                Reader extMetaDataXml = getReader(EXTENDED_PORTLET_XML_PATH);
                ExtendedPortletMetadata extMetaData = new ExtendedPortletMetadata(extMetaDataXml, portletApp);
                extMetaData.load();
            }
            catch (IOException e)
            {
                log.info("Did not load exteneded metadata as it most likely does not exist.  " + e.toString());
            }
            catch (MetaDataException e)
            {
                log.warn("Failed to load existing metadata.  " + e.toString(), e);
            }

            return portletApp;
        }
        finally
        {
            if (portletXmlReader != null)
            {
                portletXmlReader.close();
            }
        }
    }

    /**
     * 
     * <p>
     * getReader
     * </p>
     * 
     * @param path
     * @return @throws
     *              IOException
     */
    protected Reader getReader( String path ) throws IOException
    {
        return new InputStreamReader(getInputStream(path));
    }

    /**
     * 
     * <p>
     * getInputStream
     * </p>
     * 
     * @param path
     * @return @throws
     *              IOException
     */
    protected InputStream getInputStream( String path ) throws IOException
    {
        FileObject child = warStruct.resolveFile(path);
        if (child == null)
        {
            throw new IOException("Unable to locate file or path " + child);
        }

        return child.getContent().getInputStream();
    }

    /**
     * 
     * <p>
     * copyWar
     * </p>
     * Copies the entire WAR structure to the path defined in
     * <code>targetAppRoot</code>
     * 
     * @param targetAppRoot
     * @throws IOException
     */
    public void copyWar( String targetAppRoot ) throws IOException
    {
        FileObject target = fsManager.resolveFile(new File(targetAppRoot).getAbsolutePath());
        if (!target.exists())
        {
            target.createFolder();
        }

        target.copyFrom(warStruct, new AllFileSelector());
    }

    public void copyWarAndProcessWebXml( String targetAppRoot ) throws IOException, MetaDataException
    {
        copyWar(targetAppRoot);
        InputStream webXmlIn = null;
        OutputStream webXmlOut = null;
        try
        {
            webXmlIn = getInputStream(WEB_XML_PATH);
            webXmlOut = new FileOutputStream(targetAppRoot + WEB_XML_PATH);
            processWebXML(webXmlIn, webXmlOut);
        }
        finally
        {
            if (webXmlIn != null)
            {
                webXmlIn.close();
            }

            if (webXmlOut != null)
            {
                webXmlOut.close();
            }
        }

    }

    /**
     * @param targetWebXml
     *                  web.xml to "infuse" with J2 specific information
     * @throws IOException
     *                   when there is a problem reading or writing web.xml
     *                   information
     * @throws PortletApplicationException
     */
    public void processWebXML( String targetWebXml ) throws MetaDataException, IOException
    {
        InputStream webXmlIn = null;
        OutputStream webXmlOut = null;
        try
        {
            webXmlIn = getInputStream(WEB_XML_PATH);
            webXmlOut = new FileOutputStream(targetWebXml);
            processWebXML(webXmlIn, webXmlOut);
        }
        finally
        {
            if (webXmlIn != null)
            {
                webXmlIn.close();
            }

            if (webXmlOut != null)
            {
                webXmlOut.close();
            }
        }

    }

    /**
     * 
     * <p>
     * removeWar
     * </p>
     * Deletes this WAR. If the WAR is a file structure and not an actual WAR
     * file, all children are delted first, then the directory is removed.
     * 
     * @throws IOException
     *                   if there is an error removing the WAR from the file system.
     */
    public void removeWar() throws IOException
    {
        warStruct.delete(new AllFileSelector());
        warStruct.delete();
    }

    /**
     * Validate a PortletApplicationDefinition tree AFTER its
     * WebApplicationDefinition has been loaded. Currently, only the security
     * role references of the portlet definitions are validated:
     * <ul>
     * <li>A security role reference should reference a security role through a
     * roleLink. A warning message is logged if a direct reference is used.
     * <li>For a security role reference a security role must be defined in the
     * web application. An error message is logged and a
     * PortletApplicationException is thrown if not.
     * </ul>
     * 
     * @param app
     *                  The PortletApplicationDefinition to validate
     * @throws PortletApplicationException
     */
    public void validate() throws PortletApplicationException
    {
        if (portletApp == null || webApp == null)
        {
            throw new IllegalStateException(
                    "createWebApp() and createPortletApp() must be called before invoking validate()");
        }

        SecurityRoleSet roles = webApp.getSecurityRoles();
        Collection portlets = portletApp.getPortletDefinitions();
        Iterator portletIterator = portlets.iterator();
        while (portletIterator.hasNext())
        {
            PortletDefinition portlet = (PortletDefinition) portletIterator.next();
            SecurityRoleRefSet securityRoleRefs = portlet.getInitSecurityRoleRefSet();
            Iterator roleRefsIterator = securityRoleRefs.iterator();
            while (roleRefsIterator.hasNext())
            {
                SecurityRoleRef roleRef = (SecurityRoleRef) roleRefsIterator.next();
                String roleName = roleRef.getRoleLink();
                if (roleName == null || roleName.length() == 0)
                {
                    roleName = roleRef.getRoleName();
                }
                if (roles.get(roleName) == null)
                {
                    String errorMsg = "Undefined security role " + roleName + " referenced from portlet "
                            + portlet.getName();
                    throw new PortletApplicationException(errorMsg);
                }
            }
        }
    }
   
    /**
     * 
     * <p>
     * processWebXML
     * </p>
     * 
     * Infuses this the <code>webXmlIn</code>, which is expected to be the contents of a web.xml web
     * application descriptor, with <code>servlet</code> and a <code>servlet-mapping</code> element 
     * for the JetspeedContainer servlet.  This is only done if the descriptor does not already contain these 
     * items.
     *
     * @param webXmlIn web.xml content that is not yet infused 
     * @param webXmlOut target for infused web.xml content
     * @throws MetaDataException if there is a problem infusing
     */
    protected void processWebXML( InputStream webXmlIn, OutputStream webXmlOut ) throws MetaDataException
    {
        SAXBuilder builder = new SAXBuilder();

        try
        {
            Document doc = builder.build(webXmlIn);

            Element root = doc.getRootElement();

            boolean changed = false;

            Object jetspeedServlet = XPath.selectSingleNode(doc, JETSPEED_SERVLET_XPATH);
            Object jetspeedServletMapping = XPath.selectSingleNode(doc, JETSPEED_SERVLET_MAPPING_XPATH);

            log.debug("web.xml already contains servlet for the JetspeedContainer servlet.");
            log.debug("web.xml already contains servlet-mapping for the JetspeedContainer servlet.");

            if (jetspeedServlet == null)
            {
               insertElementCorrectly(root, JETSPEED_SERVLET_ELEMENT, ELEMENTS_BEFORE_SERVLET);
                changed = true;
            }

            if (jetspeedServletMapping == null)
            {
                insertElementCorrectly(root, JETSPEED_SERVLET_MAPPING_ELEMENT, ELEMENTS_BEFORE_SERVLET_MAPPING);
                changed = true;
            }

            if (changed)
            {
                XMLOutputter output = new XMLOutputter();
                output.setIndent("  ");
                output.setNewlines(true);
                output.setTrimAllWhite(true);
                output.output(doc, webXmlOut);
            }
        }
        catch (Exception e)
        {
            throw new MetaDataException("Unable to process web.xml for infusion " + e.toString(), e);
        }

    }
    
    /**
     * 
     * <p>
     * insertElementCorrectly
     * </p>
     *
     * @param root JDom element representing the &lt; web-app &gt; 
     * @param toInsert JDom element to insert into the web.xml hierarchy.
     * @param elementsBefore an array of web.xml elements that should be defined
     * before the element we want to insert.  This order should be the order defined by the 
     * web.xml's DTD type definition.
     */
    protected void insertElementCorrectly( Element root, Element toInsert, String[] elementsBefore )
    {
        List allChildren = root.getChildren();
        List elementsBeforeList = Arrays.asList(elementsBefore);
        int insertAfter = 0;
        for (int i = 0; i < allChildren.size(); i++)
        {
            Element element = (Element) allChildren.get(i);            
            if (elementsBeforeList.contains(element.getName()))
            {
                insertAfter = i;
            }
        }
        
        if(insertAfter == 0 || (insertAfter + 1) >= allChildren.size())
        {
        	root.addContent(toInsert);        	
        }
        else 
        {
        	allChildren.add((insertAfter+1), toInsert);
        }

    }

    /**
     * 
     * <p>
     * close
     * </p>
     * Closes any resource this PortletApplicationWar may have opened.
     * 
     * @throws IOException
     */
    public void close() throws IOException
    {
        warStruct.close();
    }

}