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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.JetspeedServiceReference;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.servlet.MutableWebApplication;
import org.apache.jetspeed.services.JetspeedPortletServices;
import org.apache.jetspeed.services.PortletServices;
import org.apache.jetspeed.tools.pamanager.PortletApplicationException;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.FileSystemHelper;
import org.apache.pluto.om.common.SecurityRoleRef;
import org.apache.pluto.om.common.SecurityRoleRefSet;
import org.apache.pluto.om.common.SecurityRoleSet;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * <p>
 * This class facilitates operations a portlet applications WAR file or WAR
 * file-like structure.
 * </p>
 * <p>
 * This class is utility class used mainly implementors of
 * {@link org.apache.jetspeed.pamanager.Deployment}and
 * {@link org.apache.jetspeed.pamanager.Registration}to assist in deployment
 * and undeployment of portlet applications.
 * 
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 * @author <a href="mailto:mavery@einnovation.com">Matt Avery </a>
 * @version $Id: PortletApplicationWar.java,v 1.10 2004/07/06 16:56:19 weaver
 *          Exp $
 */
public class PortletApplicationWar
{
    protected static final String PORTLET_XML_PATH = "WEB-INF/portlet.xml";
    protected static final String WEB_XML_PATH = "WEB-INF/web.xml";
    protected static final String EXTENDED_PORTLET_XML_PATH = "WEB-INF/jetspeed-portlet.xml";

    public static final String JETSPEED_SERVLET_XPATH = "/web-app/servlet/servlet-name[contains(child::text(), \"JetspeedContainer\")]";
    public static final String JETSPEED_SERVLET_MAPPING_XPATH = "/web-app/servlet-mapping/servlet-name[contains(child::text(), \"JetspeedContainer\")]";

    protected static final Log log = LogFactory.getLog("deployment");

    protected String paName;
    protected String webAppContextRoot;
    protected FileSystemHelper warStruct;
    private MutableWebApplication webApp;
    private MutablePortletApplication portletApp;
    protected final List openedResources;

    protected static final String[] ELEMENTS_BEFORE_SERVLET = new String[]{"icon", "display-name", "description",
            "distributable", "context-param", "filter", "filter-mapping", "listener", "servlet"};
    protected static final String[] ELEMENTS_BEFORE_SERVLET_MAPPING = new String[]{"icon", "display-name",
            "description", "distributable", "context-param", "filter", "filter-mapping", "listener", "servlet",
            "servlet-mapping"};

    /**
     * @param warFile
     *            {@link org.apache.jetspeed.util.FileSystemHelper}representing
     *            the WAR file we are working with. This
     *            <code>FileSystemHelper</code> can be an actual WAR file or a
     *            directory structure layed out in a WAR-like fashion. name of
     *            the portlet application the <code>warPath</code> contains
     * @param webAppContextRoot
     *            context root relative to the servlet container of this app
     * @throws IOException
     */
    public PortletApplicationWar( FileSystemHelper warStruct, String paName, String webAppContextRoot )
            throws IOException
    {
        validatePortletApplicationName(paName);

        this.paName = paName;
        this.webAppContextRoot = webAppContextRoot;
        this.openedResources = new ArrayList();
        this.warStruct = warStruct;

    }

    /**
     * <p>
     * validatePortletApplicationName
     * </p>
     * 
     * @param paName
     */
    private void validatePortletApplicationName( String paName )
    {
        if (paName == null || paName.startsWith("/") || paName.startsWith("\\") || paName.endsWith("/")
                || paName.endsWith("\\"))
        {
            throw new IllegalStateException("Invalid paName \"" + paName
                    + "\".  paName cannot be null nor can it begin nor end with any slashes.");
        }
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
     *         PortletApplicationException
     * @throws IOException
     * @see org.apache.jetspeed.util.descriptor.WebApplicationDescriptor
     */
    public MutableWebApplication createWebApp() throws PortletApplicationException, IOException
    {
        Reader webXmlReader = getReader(WEB_XML_PATH);

        try
        {
            WebApplicationDescriptor webAppDescriptor = new WebApplicationDescriptor(webXmlReader, webAppContextRoot);
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
     *         PortletApplicationException
     * @throws IOException
     * @see org.apache.jetspeed.uitl.descriptor.PortletApplicationDescriptor
     */
    public MutablePortletApplication createPortletApp() throws PortletApplicationException, IOException
    {
        Reader portletXmlReader = getReader(PORTLET_XML_PATH);
        PortletServices cps = JetspeedPortletServices.getSingleton();
        
        try
        {
            PortletApplicationDescriptor paDescriptor = new PortletApplicationDescriptor(portletXmlReader, paName);
            portletApp = paDescriptor.createPortletApplication();
            // validate(portletApplication);
            Reader extMetaDataXml = null;
            try
            {
                extMetaDataXml = getReader(EXTENDED_PORTLET_XML_PATH);
                if (extMetaDataXml != null)
                {
                    ExtendedPortletMetadata extMetaData = new ExtendedPortletMetadata(extMetaDataXml, portletApp);
                    extMetaData.load();
                    // validate services
                    Iterator services = extMetaData.portletApp.getJetspeedServices().iterator();
                    while (services.hasNext())
                    {
                        JetspeedServiceReference jsr = (JetspeedServiceReference)services.next();
                        if (null == cps.getService(jsr.getName()))
                        {
                            throw new PortletApplicationException("Invalid Portlet Service Requested: " + jsr.getName());
                        }                        
                    }
                }
            }
            catch (IOException e)
            {
                log.info("Did not load exteneded metadata as it most likely does not exist.  " + e.toString());
            }
            catch (MetaDataException e)
            {
                log.warn("Failed to load existing metadata.  " + e.toString(), e);
            }
            finally
            {
                if (null != extMetaDataXml)
                {
                    extMetaDataXml.close();
                }
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
     * Returns a <code>java.io.Reader</code> to a resource within this WAR's
     * structure.
     * 
     * @param path
     *            realtive to an object within this WAR's file structure
     * @return java.io.Reader to the file within the WAR
     * @throws IOException
     *             if the path does not exist or there was a problem reading the
     *             WAR.
     *  
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
     * Returns a <code>java.io.InputStream</code> to a resource within this
     * WAR's structure.
     * 
     * @param path
     *            realtive to an object within this WAR's file structure
     * @return java.io.InputStream to the file within the WAR
     * @throws IOException
     *             if the path does not exist or there was a problem reading the
     *             WAR.
     */
    protected InputStream getInputStream( String path ) throws IOException
    {
        File child = new File(warStruct.getRootDirectory(), path);
        if (child == null || !child.exists())
        {
            throw new FileNotFoundException("Unable to locate file or path " + child);
        }

        FileInputStream fileInputStream = new FileInputStream(child);
        openedResources.add(fileInputStream);
        return fileInputStream;
    }

    /**
     * 
     * <p>
     * getOutputStream
     * </p>
     * 
     * Returns a <code>java.io.OutputStream</code> to a resource within this
     * WAR's structure.
     * 
     * @param path
     *            realtive to an object within this WAR's file structure
     * @return java.io.Reader to the file within the WAR
     * @throws IOException
     *             if the path does not exist or there was a problem reading the
     *             WAR.
     */
    protected OutputStream getOutputStream( String path ) throws IOException
    {
        File child = new File(warStruct.getRootDirectory(), path);
        if (child == null || !child.exists())
        {
            throw new FileNotFoundException("Unable to locate file or path " + child);
        }

        FileOutputStream fileOutputStream = new FileOutputStream(child);
        openedResources.add(fileOutputStream);
        return fileOutputStream;
    }

    protected Writer getWriter( String path ) throws IOException
    {
        return new OutputStreamWriter(getOutputStream(path));
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
     *            target to copy this WAR's content to. If the path ends in
     *            <code>.war</code> or <code>.jar</code>. The war will be
     *            copied into that file in jar format.
     * @return PortletApplicationWar representing the newly created WAR.
     * @throws IOException
     */
    public PortletApplicationWar copyWar( String targetAppRoot ) throws IOException
    {
        // FileObject target = fsManager.resolveFile(new
        // File(targetAppRoot).getAbsolutePath());
        FileSystemHelper target = new DirectoryHelper(new File(targetAppRoot));
        try
        {
            target.copyFrom(warStruct.getRootDirectory());

            return new PortletApplicationWar(target, paName, webAppContextRoot);

        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            target.close();

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
     *             if there is an error removing the WAR from the file system.
     */
    public void removeWar() throws IOException
    {
        if (warStruct.getRootDirectory().exists())
        {
            warStruct.remove();
        }
        else
        {
            throw new FileNotFoundException("PortletApplicationWar ," + warStruct.getRootDirectory()
                    + ", does not exist.");
        }
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
     * Infuses this PortletApplicationWar's web.xml file with
     * <code>servlet</code> and a <code>servlet-mapping</code> element for
     * the JetspeedContainer servlet. This is only done if the descriptor does
     * not already contain these items.
     * 
     * @throws MetaDataException
     *             if there is a problem infusing
     */
    public void processWebXML() throws MetaDataException
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

            webXmlIn = getInputStream(WEB_XML_PATH);
            Document doc = builder.build(webXmlIn);

            Element root = doc.getRootElement();
            webXmlIn.close();

            boolean changed = false;

            Object jetspeedServlet = XPath.selectSingleNode(doc, JETSPEED_SERVLET_XPATH);
            Object jetspeedServletMapping = XPath.selectSingleNode(doc, JETSPEED_SERVLET_MAPPING_XPATH);
            if (doc.getRootElement().getChildren().size() == 0)
            {
                throw new MetaDataException("Source web.xml has no content!!!");
            }

            log.debug("web.xml already contains servlet for the JetspeedContainer servlet.");
            log.debug("web.xml already contains servlet-mapping for the JetspeedContainer servlet.");

            if (jetspeedServlet == null)
            {
                Element jetspeedServletElement = new Element("servlet");
                Element servletName = (Element) new Element("servlet-name").addContent("JetspeedContainer");
                Element servletDspName = (Element) new Element("display-name").addContent("Jetspeed Container");
                Element servletDesc = (Element) new Element("description")
                        .addContent("MVC Servlet for Jetspeed Portlet Applications");
                Element servletClass = (Element) new Element("servlet-class")
                        .addContent("org.apache.jetspeed.container.JetspeedContainerServlet");
                jetspeedServletElement.addContent(servletName);
                jetspeedServletElement.addContent(servletDspName);
                jetspeedServletElement.addContent(servletDesc);
                jetspeedServletElement.addContent(servletClass);

                insertElementCorrectly(root, jetspeedServletElement, ELEMENTS_BEFORE_SERVLET);
                changed = true;
            }

            if (jetspeedServletMapping == null)
            {

                Element jetspeedServletMappingElement = new Element("servlet-mapping");

                Element servletMapName = (Element) new Element("servlet-name").addContent("JetspeedContainer");
                Element servletUrlPattern = (Element) new Element("url-pattern").addContent("/container/*");

                jetspeedServletMappingElement.addContent(servletMapName);
                jetspeedServletMappingElement.addContent(servletUrlPattern);

                insertElementCorrectly(root, jetspeedServletMappingElement, ELEMENTS_BEFORE_SERVLET_MAPPING);
                changed = true;
            }

            if (changed)
            {
                System.out.println("Writing out infused web.xml for " + paName);
                XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
                webXmlWriter = getWriter(WEB_XML_PATH);
                output.output(doc, webXmlWriter);
                webXmlWriter.flush();

            }

        }
        catch (Exception e)
        {
            throw new MetaDataException("Unable to process web.xml for infusion " + e.toString(), e);
        }
        finally
        {
            if (webXmlWriter != null)
            {
                try
                {
                    webXmlWriter.close();
                }
                catch (IOException e1)
                {

                }
            }

            if (webXmlIn != null)
            {
                try
                {
                    webXmlIn.close();
                }
                catch (IOException e1)
                {

                }
            }
        }

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

        Iterator resources = openedResources.iterator();
        while (resources.hasNext())
        {
            try
            {
                Object res = resources.next();
                if (res instanceof InputStream)
                {
                    ((InputStream) res).close();
                }
                else if (res instanceof OutputStream)
                {
                    ((OutputStream) res).close();
                }
            }
            catch (Exception e)
            {

            }
        }

    }

    /**
     * 
     * <p>
     * createClassloader
     * </p>
     * 
     * Use this method to create a classloader based on this wars structure.
     * I.e. it will create a ClassLoader containing the contents of
     * WEB-INF/classes and WEB-INF/lib and the ClassLoader will be searched in
     * that order.
     * 
     * 
     * @param parent
     *            Parent ClassLoader. Can be <code>null</code>
     * @return @throws
     *         IOException
     */
    public ClassLoader createClassloader( ClassLoader parent ) throws IOException
    {
        ArrayList urls = new ArrayList();
        File webInfClasses = null;

        webInfClasses = new File(warStruct.getRootDirectory(), ("WEB-INF/classes/"));
        if (webInfClasses.exists())
        {
            log.info("Adding " + webInfClasses.toURL() + " to class path.");
            urls.add(webInfClasses.toURL());
        }

        File webInfLib = new File(warStruct.getRootDirectory(), "WEB-INF/lib");

        if (webInfLib.exists())
        {
            File[] jars = webInfLib.listFiles();

            for (int i = 0; i < jars.length; i++)
            {
                File jar = jars[i];
                log.info("Adding " + jar.toURL() + " to class path.");
                urls.add(jar.toURL());
            }
        }

        return new URLClassLoader((URL[]) urls.toArray(new URL[urls.size()]), parent);
    }

    /**
     * @return Returns the paName.
     */
    public String getPortletApplicationName()
    {
        return paName;
    }

    /**
     * 
     * <p>
     * getDeployedPath
     * </p>
     * 
     * @return A string representing the path to this WAR in the form of a URL
     *         or <code>null</code> is the URL could not be created.
     */
    public String getDeployedPath()
    {
        try
        {
            return warStruct.getRootDirectory().toURL().toExternalForm();
        }
        catch (MalformedURLException e)
        {
            return null;
        }
    }
}