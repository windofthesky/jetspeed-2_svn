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
package org.apache.jetspeed.deployment.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.tools.pamanager.PortletApplicationManagement;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JettyDeployPortletAppEventListener extends DeployPortletAppEventListener
{
    private String jettyContextsDir;
    
    public JettyDeployPortletAppEventListener(PortletApplicationManagement pam, PortletRegistry registry, String webAppDir, String localAppDir, boolean stripLoggers, String jettyContextsDir) throws FileNotFoundException
    {
        super(pam, registry, webAppDir, localAppDir, stripLoggers);
        initJettyContextsDir(jettyContextsDir);
    }

    public JettyDeployPortletAppEventListener(PortletApplicationManagement pam, PortletRegistry registry, String webAppDir, String localAppDir, String localAppStagingDir, boolean stripLoggers, String jettyContextsDir) throws FileNotFoundException
    {
        super(pam, registry, webAppDir, localAppDir, localAppStagingDir, stripLoggers);
        initJettyContextsDir(jettyContextsDir);
    }
    
    private void initJettyContextsDir(String jettyContextsDir) throws FileNotFoundException
    {
        File jettyContextsDirFile = new File(jettyContextsDir);

        if (jettyContextsDirFile.exists())
        {
            try
            {
                this.jettyContextsDir = jettyContextsDirFile.getCanonicalPath();
            }
            catch (IOException e) {}
        }
        else
        {
            throw new FileNotFoundException("The jetty contexts directory \""
                                            + jettyContextsDirFile.getAbsolutePath() + "\" does not exist.");
        }
    }

    protected void deployPortletApplication(DeploymentEvent event) throws DeploymentException
    {        
        try
        {
            String fileName = event.getName();
            String filePath = event.getPath();
            String appName = fileName.substring(0, fileName.length() - 4);
            Document context = getJettyContext(filePath);
            File contextFile = getCurrentJettyContextFile(appName);
            if (contextFile != null)
            {
                if (context == null)
                {
                    context = getCurrentJettyContext(contextFile);
                }
                contextFile.delete();
            }
            if (context == null)
            {
                context = getJettyContextTemplate();
            }
            updateJettyContext(appName, new File(getWebAppDir(), fileName).getAbsolutePath(), context);            
            removeCurrentPA(appName);
            super.deployPortletApplication(event);
            writeJettyContext(appName,context);
        }
        catch (Exception e)
        {
            throw new DeploymentException(e);
        }
    }
    
    protected void removeCurrentPA(String contextName) throws IOException
    {
        File warFile = new File(getWebAppDir(), contextName + ".war");
        if (warFile.exists())
        {
            warFile.delete();
        }
        File warDir = new File(getWebAppDir(), contextName);
        if (warDir.exists() && warDir.isDirectory())
        {
            removeDir(warDir);
        }
    }
    
    protected boolean removeDir(File file)
    {
        if (file.isDirectory())
        {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++)
            {
                boolean success = removeDir(new File(file, children[i]));
                if (!success)
                {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it OR it is a plain file
        return file.delete();        
    }
    
    protected File getCurrentJettyContextFile(String contextName) throws IOException
    {
        File contextFile = new File(jettyContextsDir, contextName+".xml");
        if (contextFile.exists())
        {
            if (contextFile.isDirectory())
            {
                throw new IOException("Cannot deploy application"+contextName+" as there already exists a directory in "+jettyContextsDir+" with the same name");
            }
            return contextFile;
        }
        return null;
    }
    
    protected Document getCurrentJettyContext(File contextFile) throws Exception
    {
        InputStream source = null;
        try
        {
            source = new FileInputStream(contextFile);
            return parseJettyContext(source);
        }
        finally
        {
            if (source != null)
            {
                try
                {
                    source.close();
                }
                catch (IOException e1)
                {
                    // ignore
                }
            }
        }
    }
    
    protected Document getJettyContextTemplate() throws Exception
    {
        InputStream source = null;
        try
        {
            source = getClass().getResourceAsStream("jetty/context-template.xml");
            return parseJettyContext(source);
        }
        finally
        {
            if (source != null)
            {
                try
                {
                    source.close();
                }
                catch (IOException e1)
                {
                    // ignore
                }
            }
        }
    }
    
    protected Document getJettyContext(String fileName) throws Exception
    {
        JarFile jin = null;
        InputStream source = null;
        try
        {
            jin = new JarFile(fileName);
            
            JarEntry src;
            Enumeration<JarEntry> jarEntries = jin.entries();
            while (jarEntries.hasMoreElements())
            {
                src = jarEntries.nextElement();
                String target = src.getName();
                if ("META-INF/jetspeed-jetty-context.xml".equals(target))
                {
                    System.out.println("Found jetspeed-jetty-context.xml");
                    source = jin.getInputStream(src);
                    return parseJettyContext(source);
                }                
            }
            return null;
        }        
        finally
        {
            if (source != null)
            {
                try
                {
                    source.close();
                }
                catch (IOException e1)
                {
                    // ignore
                }
            }
            if (jin != null)
            {
                try
                {
                    jin.close();
                    jin = null;
                }
                catch (IOException e1)
                {
                    // ignore
                }
            }
        }
    }
    
    protected void updateJettyContext(String contextName, String warPath, Document context)
    {
        Element root = context.getDocumentElement();
        NodeList sets = root.getElementsByTagName("Set");
        boolean foundSetWar = false;
        boolean foundSetContextPath = false;
        boolean foundSetConfigurationClasses = false;
        
        
        for (int i = 0, size = sets.getLength(); i < size; i++)
        {
            Element set = (Element)sets.item(i);
            String name = set.getAttribute("name");
            if (name.equals("contextPath"))
            {
                set.setTextContent("/"+contextName);
                foundSetContextPath = true;
            }
            else if (name.equals("resourceBase"))
            {
                root.removeChild(set);
            }
            else if (name.equals("war"))
            {
                set.setTextContent(warPath);
                foundSetWar = true;
            }
            else if (name.equals("configurationClasses"))
            {
                foundSetConfigurationClasses = true;
            }
        }        
        if (!foundSetContextPath)
        {
            Element set = context.createElement("Set");
            set.setAttribute("name", "contextPath");
            set.setTextContent("/"+contextName);
            root.appendChild(set);
        }
        if (!foundSetWar)
        {
            Element set = context.createElement("Set");
            set.setAttribute("name", "war");
            set.setTextContent(warPath);
            root.appendChild(set);
        }
        if (!foundSetConfigurationClasses)
        {
            Element set = context.createElement("Set");
            set.setAttribute("name", "configurationClasses");
            
            root.appendChild(set);
            
            Element array = context.createElement("Array");
            array.setAttribute("type", "java.lang.String");
            
            set.appendChild(array);

            Element item = context.createElement("Item");
            item.setTextContent("org.mortbay.jetty.webapp.WebInfConfiguration");
            array.appendChild(item);

            item = context.createElement("Item");
            item.setTextContent("org.mortbay.jetty.plus.webapp.EnvConfiguration");
            array.appendChild(item);

            item = context.createElement("Item");
            item.setTextContent("org.mortbay.jetty.plus.webapp.Configuration");
            array.appendChild(item);

            item = context.createElement("Item");
            item.setTextContent("org.mortbay.jetty.webapp.JettyWebXmlConfiguration");
            array.appendChild(item);

            item = context.createElement("Item");
            item.setTextContent("org.mortbay.jetty.webapp.TagLibConfiguration");
            array.appendChild(item);
        }
    }
    
    protected void writeJettyContext(String contextName, Document context) throws IOException
    {
        File contextFile = new File(jettyContextsDir, contextName+".xml");
        if (contextFile.exists())
        {
            throw new IOException("Jetty context file "+contextFile.getAbsolutePath()+" found.");
        }
        FileOutputStream output = null;
        try
        {
            output = new FileOutputStream(contextFile);
            DOMSource domSource = new DOMSource(context);
            StreamResult streamResult = new StreamResult(output);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            if (context.getDoctype() != null)
            {
                transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, context.getDoctype().getPublicId());
                transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, context.getDoctype().getSystemId());
            }
            transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(domSource, streamResult);
        }
        catch (TransformerConfigurationException e)
        {
            throw new IOException(e.getMessage());
        }
        catch (TransformerException e)
        {
            throw new IOException(e.getMessage());
        }
        finally
        {
            if (output != null)
            {
                try
                {
                    output.close();
                }
                catch (IOException e1)
                {
                    // ignore
                }
            }
        }
    }

    protected Document parseJettyContext(InputStream source) throws Exception 
    {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        builder.setEntityResolver(new EntityResolver()
        {
            public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId) throws SAXException,
                            java.io.IOException
            {
                if (systemId.equals("http://jetty.mortbay.org/configure.dtd"))
                {
                    return new InputSource(getClass().getResourceAsStream("jetty/configure_6_0.dtd"));
                }
                return null;
            }
        });
        Document document = builder.parse(source);
        return document;
    }
}
