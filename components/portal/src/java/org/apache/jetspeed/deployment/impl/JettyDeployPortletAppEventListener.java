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
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.deployment.DeploymentEvent;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.tools.pamanager.PortletApplicationManagement;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
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
    
    protected Document getCurrentJettyContext(File contextFile) throws IOException
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
    
    protected Document getJettyContextTemplate() throws IOException
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
    
    protected Document getJettyContext(String fileName) throws IOException
    {
        JarFile jin = null;
        InputStream source = null;
        try
        {
            jin = new JarFile(fileName);
            
            ZipEntry src;
            Enumeration zipEntries = jin.entries();
            while (zipEntries.hasMoreElements())
            {
                src = (ZipEntry) zipEntries.nextElement();
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
        Element root = context.getRootElement();
        Iterator iter = root.getChildren("Set").iterator();
        boolean foundSetWar = false;
        boolean foundSetContextPath = false;
        boolean foundSetConfigurationClasses = false;
        
        while (iter.hasNext())
        {
            Element set = (Element)iter.next();
            String name = set.getAttribute("name").getName();
            if (name.equals("contextPath"))
            {
                set.setText("/"+contextName);
                foundSetContextPath = true;
            }
            else if (name.equals("resourceBase"))
            {
                iter.remove();
            }
            else if (name.equals("war"))
            {
                set.setText(warPath);
                foundSetWar = true;
            }
            else if (name.equals("configurationClasses"))
            {
                foundSetConfigurationClasses = true;
            }
        }        
        if (!foundSetContextPath)
        {
            root.addContent(new Element("Set").setAttribute(new Attribute("name", "contextPath")).setText("/"+contextName));
        }
        if (!foundSetWar)
        {
            root.addContent(new Element("Set").setAttribute(new Attribute("name", "war")).setText(warPath));
        }
        if (!foundSetConfigurationClasses)
        {
            Element array = new Element("Array").setAttribute(new Attribute("type","java.lang.String"));
            array.addContent(new Element("Item").setText("org.mortbay.jetty.webapp.WebInfConfiguration"));
            array.addContent(new Element("Item").setText("org.mortbay.jetty.plus.webapp.EnvConfiguration"));
            array.addContent(new Element("Item").setText("org.mortbay.jetty.plus.webapp.Configuration"));
            array.addContent(new Element("Item").setText("org.mortbay.jetty.webapp.JettyWebXmlConfiguration"));
            array.addContent(new Element("Item").setText("org.mortbay.jetty.webapp.TagLibConfiguration"));
            root.addContent(new Element("Set").setAttribute(new Attribute("name", "configurationClasses")).setContent(array));
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
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            xmlOutputter.output(context, output);
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

    protected Document parseJettyContext(InputStream source) throws IOException 
    {
        // Parse using the local dtds instead of remote dtds. This
        // allows to deploy the application offline
        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setEntityResolver(new EntityResolver()
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
        try
        {
            Document document = saxBuilder.build(source);
            return document;
        }
        catch (JDOMException e)
        {
            IOException ioException = new IOException("Parse failure: "+e.getMessage());
            ioException.fillInStackTrace();
            throw ioException;
        }
    }
}
