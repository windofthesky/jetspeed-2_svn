/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.tools.pamanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class implements methods that are called during the deployment of the application.
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
  * @version $Id$
 */

public class DeployUtilities
{

    private static final Log log = LogFactory.getLog("deployment");

    /**
     * Deploys archives from a war file to the WebApps directory
     * @param webAppsDir The application server directory
     * @param warFile The war file to deploy
     * @throws java.lang.IOException if the target for deployment already exists
     * or there is a problem locating, reading or expanding the source archive.
     */

    public void deployArchive(String webAppsDir, String warFile, String appName) throws IOException
    {
        String warFileName = warFile;
        if (warFileName.indexOf("/") != -1)
            warFileName = warFileName.substring(warFileName.lastIndexOf("/") + 1);
        if (warFileName.indexOf("\\") != -1)
            warFileName = warFileName.substring(warFileName.lastIndexOf("\\") + 1);
        if (warFileName.endsWith(".war"))
            warFileName = warFileName.substring(0, warFileName.lastIndexOf("."));

        log.info("deploying WAR file'" + warFileName + ".war' to WEB-INF/...");

        

        String destination = formatWebApplicationPath(webAppsDir,  appName);
        if (new File(destination).exists())
        {
            throw new IOException(
                "The portlet application archive target, "
                    + destination
                    + ", already exists.  Please remove it before attempting to deploy.");
        }

        JarFile jarFile = new JarFile(warFile);
        Enumeration files = jarFile.entries();
        while (files.hasMoreElements())
        {
            JarEntry entry = (JarEntry) files.nextElement();

            File file = new File(destination, entry.getName());
            File dirF = new File(file.getParent());
            dirF.mkdirs();
            if (entry.isDirectory())
            {
                file.mkdirs();
            }
            else
            {
                byte[] buffer = new byte[1024];
                int length = 0;
                InputStream fis = jarFile.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(file);
                while ((length = fis.read(buffer)) >= 0)
                {
                    fos.write(buffer, 0, length);
                }
                fos.close();
            }

        }

        log.info("Libraries and classes deployment finished!");

    }

    /**
     * getWebXMLPath()
     * Creates the full path to the web.xml.
     * @param webAppsDir The application server directory
     * @param warFile The war file to deploy
     * @throws PortletApplicationException
     */

    public String getWebXMLPath(String webAppsDir, String warFile, String paName) throws PortletApplicationException
    {
        if (webAppsDir.length() == 0 || warFile.length() == 0)
        {
            String msg = "WebAppDir(" + webAppsDir + ") or WarFile (" + warFile + ") not defined! ";
            log.error(msg);
            PortletApplicationException pe = new PortletApplicationException(msg);
            throw pe;
        }

        String webModule = warFile;

        if (webModule.indexOf("/") != -1)
            webModule = webModule.substring(webModule.lastIndexOf("/") + 1);
        if (webModule.indexOf("\\") != -1)
            webModule = webModule.substring(webModule.lastIndexOf("\\") + 1);
        if (webModule.endsWith(".war"))
            webModule = webModule.substring(0, webModule.lastIndexOf("."));

        // Load web.xml
        String webXml = formatWebApplicationPath(webAppsDir, paName)+"/WEB-INF/web.xml";

        return webXml;
    }

    /**
     * processWebXML()
     * Updated the web.xml with the portlet application specific information.
     * @param webXML to full path to the web.xml file
     * @param paName Portlet Application Name
     * @throws PortletApplicationException
     */
    private final static String CONFIG_JAX_FACTORY_DOM = "jax.factory.dom";
    private String jaxDomFactoryProp = "org.apache.xerces.jaxp.DocumentBuilderFactoryImpl";
    private static final String JAX_DOM_PARSER_PROPERTY = "javax.xml.parsers.DocumentBuilderFactory";

    public void processWebXML(String webXml, String paName) throws PortletApplicationException
    {
        log.info("prepare web archive '" + webXml + "' ...");

        try
        {
            // Read the WEB.XML and add application specific nodes.
            System.setProperty(JAX_DOM_PARSER_PROPERTY, jaxDomFactoryProp);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            if (db == null)
            {
                String msg = "Failed to create an XML DOM!";
                log.error(msg);
                PortletApplicationException e = new PortletApplicationException(msg);
                throw e;
            }

            // Use the local dtd instead of remote dtd. This
            // allows to deploy the application offline
            db.setEntityResolver(new EntityResolver()
            {
                public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId)
                    throws SAXException, java.io.IOException
                {

                    if (systemId.equals("http://java.sun.com/dtd/web-app_2_3.dtd"))
                    {
                        return new InputSource(new FileInputStream("./web-app_2_3.dtd"));
                    }
                    else
                        return null;
                }
            });

            Document doc = db.parse(new File(webXml));
            if (doc == null)
            {
                String msg =
                    "Failed to load " + webXml + "\nMake sure that the file exists in the war file and it's valid XML syntax.";
                log.error(msg);
                PortletApplicationException e = new PortletApplicationException(msg);
                throw e;

            }

            // web.xml loadded -- check if all elements are in the document
            boolean bServletMappingExists = false;
            boolean bWelcomeFileExists = false;
            boolean bDocUpdated = false; // Only save DOM document when it was updated

            NodeList nodes_i = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodes_i.getLength(); i++)
            {
                Node node_i = nodes_i.item(i);

                if (node_i.getNodeType() == Node.ELEMENT_NODE && ((Element) node_i).getTagName().equals("servlet-mapping"))
                {
                    bServletMappingExists = true;

                    Element eServletMapping = (Element) node_i;
                    NodeList nodes_j = eServletMapping.getChildNodes();
                    for (int j = 0; j < nodes_j.getLength(); j++)
                    {
                        // TBD: Not yet clear what element we try to add. Use <servlet-name>
                        //      for testing.
                        Node node_j = nodes_j.item(j);
                        if (node_j.getNodeType() == Node.ELEMENT_NODE && ((Element) node_i).getTagName().equals("servlet-name"))
                        {
                            bServletMappingExists = true;
                            log.info("Servlet-name node exists");
                        }
                    }

                    // Add <servlet-name> node if it doesn't exist
                    if (bServletMappingExists == false)
                    {
                        log.info("Servlet-name node doesn't exist. Add it to the document.");
                        bDocUpdated = true;

                        Element eServletName = doc.createElement("servlet-name");
                        eServletName.setNodeValue(paName);
                        node_i.appendChild(eServletName);
                    }
                }
                else if (node_i.getNodeType() == Node.ELEMENT_NODE && ((Element) node_i).getTagName().equals("welcome-file-list"))
                {
                    bWelcomeFileExists = true;
                }
            }

            // Add the <welcome-file-list> node to the document

            if (bWelcomeFileExists == false)
            {
                log.info("Adding <welcome-file-list> element to web.xml...");
                bDocUpdated = true;

                // Create Welcome-file node
                Element eWelcomeFile = doc.createElement("welcome-file");
                Text txt = doc.createTextNode("Index.jsp");
                eWelcomeFile.appendChild(txt);

                // Create welcome-file-list node
                Element eWelcomeList = doc.createElement("welcome-file-list");

                // Add welcome-file node to welcome-file-list node
                eWelcomeList.appendChild(eWelcomeFile);

                // Add the welcome-file-list to the document
                doc.getDocumentElement().appendChild(eWelcomeList);

            }

            // Persit DOM Docuemnt to disk
            if (bDocUpdated == true)
            {
                OutputFormat format = new OutputFormat();
                format.setIndenting(true);
                format.setIndent(4);
                // Doctype gets removed by the DOM. Add it otherwise Tomcat throws an exception
                format.setDoctype(
                    "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN",
                    "http://java.sun.com/dtd/web-app_2_3.dtd");

                FileWriter fw = new FileWriter(webXml);

                XMLSerializer serial = new XMLSerializer(fw, format);

                serial.asDOMSerializer();
                serial.serialize(doc.getDocumentElement());
                fw.close();

                System.out.println("Updated " + webXml + " saved.....");

            }
        }
        catch (SAXException se)
        {
            String msg = "SAX Exception while ading elements to the web.xml file.\n" + se.getMessage();
            log.error(msg);
            throw new PortletApplicationException(msg);
        }
        catch (Exception ex)
        {
            String msg = "Unhandled exception while ading elements to the web.xml file.\n" + ex.getMessage();
            log.error(msg);
            throw new PortletApplicationException(msg);
        }

    }
	
	/**
	 * Builds a well-formed relative path to the web application directory
	 * that contains the web application defined by the <code>appName</code>
	 * @param webAppsDir location of the servlet container's web application directory
	 * @param appName name of the web application.
	 * @return well-formed relative path to the web application's directory
	 */
    public String formatWebApplicationPath(String webAppsDir, String appName)
    {
        if (!appName.startsWith("/") && !webAppsDir.endsWith("/") && !webAppsDir.endsWith("\\"))
        {
           return webAppsDir + "/" + appName;
        }
        else
        {
           return webAppsDir + appName;
        }
    }

}
