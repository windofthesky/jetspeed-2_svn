/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.tools.pamanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;



/**
 * This class implements methods that are called during the deployment of the application.
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a> 
  * @version $Id$
 */

public class DeployUtilities
{

    /**
     * Deploys archives from a war file to the WebApps directory
     * @param webAppsDir The application server directory
     * @param warFile The war file to deploy 
     * @throws PortletApplicationException
     */
    
    public void deployArchive(String webAppsDir, String warFile, String appName) throws PortletApplicationException
    {
        String warFileName = warFile;
        if (warFileName.indexOf("/")!=-1)
            warFileName = warFileName.substring(warFileName.lastIndexOf("/")+1);
        if (warFileName.indexOf("\\")!=-1)
            warFileName = warFileName.substring(warFileName.lastIndexOf("\\")+1);
        if (warFileName.endsWith(".war"))
            warFileName = warFileName.substring(0, warFileName.lastIndexOf("."));

        System.out.println("deploying WAR file'"+warFileName+".war' to WEB-INF/...");

        try
        {
            String destination = webAppsDir + appName;
    
            JarFile jarFile = new JarFile(warFile);
            Enumeration files = jarFile.entries();
            while (files.hasMoreElements())
            {
                JarEntry entry = (JarEntry)files.nextElement();
    
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
    
            System.out.println("Libraries and classes deployment finished!");
        }
        catch( Exception e )
        {
            e.printStackTrace();
            PortletApplicationException pe = 
                new PortletApplicationException("Exception while copying jar files to web apps directory '" + 
                                                 webAppsDir +"'" + e.getMessage());            
            throw pe;
        }
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
        if (webAppsDir.length() == 0 || warFile.length() == 0 )
        {
            PortletApplicationException pe = 
                new PortletApplicationException("WebAppDir(" + webAppsDir + ") or WarFile ("+ warFile + ") not defined! ");
            throw pe;
        }

        String webModule = warFile;

        if (webModule.indexOf("/")!=-1)
         webModule = webModule.substring(webModule.lastIndexOf("/")+1);
        if (webModule.indexOf("\\")!=-1)
         webModule = webModule.substring(webModule.lastIndexOf("\\")+1);
        if (webModule.endsWith(".war"))
         webModule = webModule.substring(0, webModule.lastIndexOf("."));

        // Load web.xml
        String webXml = webAppsDir+paName+"/WEB-INF/web.xml";

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
        System.out.println("prepare web archive '"+webXml+"' ...");

        try
        {
            // Read the WEB.XML and add application specific nodes.
            System.setProperty( JAX_DOM_PARSER_PROPERTY, jaxDomFactoryProp);            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            
            DocumentBuilder db = dbf.newDocumentBuilder();
            if (db== null)
            {
              PortletApplicationException e = new PortletApplicationException( "Failed to create an XML DOM!");
              throw e;
            }
           
            Document doc = db.parse(new File(webXml));
            if ( doc == null)
            {
                PortletApplicationException e = 
                    new PortletApplicationException( "Failed to load " + webXml + 
                        "\nMake sure that the file exists in the war file and it's valid XML syntax.");
                throw e;
    
            }
   
            // web.xml loadded -- check if all elements are in the document
            boolean bServletMappingExists  = false;
            boolean bWelcomeFileExists     = false;
            boolean bDocUpdated            = false;    // Only save DOM document when it was updated
    
            NodeList nodes_i   = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < nodes_i.getLength(); i++)
            {
                Node node_i = nodes_i.item(i);
    
                if (   node_i.getNodeType() == Node.ELEMENT_NODE
                    && ((Element) node_i).getTagName().equals("servlet-mapping")) 
                {
                    bServletMappingExists = true;
    
                    Element eServletMapping = (Element) node_i;
                    NodeList nodes_j = eServletMapping.getChildNodes();
                    for (int j = 0; j < nodes_j.getLength(); j++) 
                    {
                        // TBD: Not yet clear what element we try to add. Use <servlet-name>
                        //      for testing.
                        Node node_j = nodes_j.item(j);
                        if ( node_j.getNodeType() == Node.ELEMENT_NODE
                             && ((Element) node_i).getTagName().equals("servlet-name"))
                        {
                            bServletMappingExists = true;
                            System.out.println("Servlet-name node exists");
                        }
                    }

                    // Add <servlet-name> node if it doesn't exist
                    if ( bServletMappingExists == false )
                    {
                        System.out.println("Servlet-name node doesn't exist. Add it to the document.");
                        bDocUpdated = true;

                        Element eServletName = doc.createElement("servlet-name");
                        eServletName.setNodeValue(paName);
                        node_i.appendChild(eServletName);
                    }
                }
                else if (   node_i.getNodeType() == Node.ELEMENT_NODE
                         && ((Element) node_i).getTagName().equals("welcome-file-list"))
                {
                  bWelcomeFileExists = true;  
                }
            }
 
            // Add the <welcome-file-list> node to the document
    
            if ( bWelcomeFileExists == false)
            {
                System.out.println("Adding <welcome-file-list> element to web.xml...");
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
                format.setDoctype
                  ("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN", "http://java.sun.com/j2ee/dtds/web-app_2_3.dtd");

                FileWriter fw = new FileWriter(webXml);
                
                XMLSerializer serial = new XMLSerializer(fw, format);

                serial.asDOMSerializer();
                serial.serialize(doc.getDocumentElement());
                fw.close();

                System.out.println("Updated " + webXml + " saved.....");
                                                                                
            }
        }
        catch(SAXException se)
        {
            PortletApplicationException e = 
                new PortletApplicationException("SAX Exception while ading elements to the web.xml file.\n" + se.getMessage());
            throw e;
        }
        catch(Exception ex)
        {
            PortletApplicationException e = 
                new PortletApplicationException("Unhandled exception while ading elements to the web.xml file.\n" 
                            + ex.getMessage());
            throw e;
        }
            

    }

    /**
    *   Deletes all files and subdirectories under dir
    * @param dir Top lvel directory that needs to be deleted
    *
    */

    public boolean deleteDir(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) 
            {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) 
                {
                    return false;
                }
            }
        }
        
        // The directory is now empty so delete it
        return dir.delete();
    }
}

