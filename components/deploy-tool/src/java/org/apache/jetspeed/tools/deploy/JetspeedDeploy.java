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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Makes a web application Deploy-ready for Jetspeed.
 * 
 * @author <a href="mailto:taylor@apache.org">Dain Sundstrom </a>
 * @author <a href="mailto:dsundstrom@gluecode.com">David Sean Taylor </a>
 * @version $Id$
 */
public class JetspeedDeploy implements Deploy
{
    public static void main(String[] args) throws Exception
    {
        if (args.length < 2)
        {
            printUsage();
            System.exit(1);
            return;
        }
        
        boolean stripLoggers = false;
        String version = null;
        for(int i = 0; i < args.length-2; i++) {
            String option = args[i];
            if(option.equals("-s")) {
                stripLoggers = true;
            } else if(option.equals("-v") && i < args.length-3) {
                version = args[i+1];
                i++;
            } else {
                // invalid option
                printUsage();
                System.exit(1);
                return;
            }
        }
        
        new JetspeedDeploy(args[args.length-2], args[args.length-1], stripLoggers, version);
    }
    
    private static void printUsage() {
        System.out.println("Usage: java -jar jetspeed-deploy-tools-<version>.jar [options] INPUT OUTPUT");
        System.out.println("Options:");
        System.out.println("  -s: stripLoggers - remove commons-logging[version].jar and/or log4j[version].jar from war");
        System.out.println("                     (required when targetting application servers like JBoss)");
        System.out.println("  -v VERSION: force servlet specification version to handle web.xml");
        System.out.println("                     (default will automatically determine version)");
    }

    private final byte[] buffer = new byte[4096];

    public JetspeedDeploy(String inputName, String outputName, boolean stripLoggers) throws Exception {
        this(inputName, outputName, stripLoggers, null);
    }
    
    public JetspeedDeploy(String inputName, String outputName, boolean stripLoggers, String forcedVersion) throws Exception
    {
        File tempFile = null;
        JarFile jin = null;
        JarOutputStream jout = null;
        FileChannel srcChannel = null;
        FileChannel dstChannel = null;

        try
        {
            String portletApplicationName = getPortletApplicationName(outputName);
            tempFile = File.createTempFile(portletApplicationName, "");
            tempFile.deleteOnExit();

            jin = new JarFile(inputName);
            jout = new JarOutputStream(new FileOutputStream(tempFile));

            // copy over all of the files in the input war to the output
            // war except for web.xml, portlet.xml, and context.xml which
            // we parse for use later
            Document webXml = null;
            Document portletXml = null;
            Document contextXml = null;
            boolean taglibFound = false;
            ZipEntry src;
            InputStream source;
            Enumeration zipEntries = jin.entries();
            while (zipEntries.hasMoreElements())
            {
                src = (ZipEntry) zipEntries.nextElement();
                source = jin.getInputStream(src);
                try
                {
                    String target = src.getName();
                    if ("WEB-INF/web.xml".equals(target))
                    {
                        System.out.println("Found web.xml");
                        webXml = parseXml(source);
                    }
                    else if ("WEB-INF/portlet.xml".equals(target))
                    {
                        System.out.println("Found WEB-INF/portlet.xml");
                        portletXml = parseXml(source);
                    }
                    else if ("META-INF/context.xml".equals(target))
                    {
                        System.out.println("Found META-INF/context.xml");
                        contextXml = parseXml(source);
                    }
                    else if ("WEB-INF/tld/portlet.tld".equals(target))
                    {
                        taglibFound = true;
                    }
                    else
                    {
                        if ( stripLoggers && target.endsWith(".jar") &&
                             (target.startsWith("WEB-INF/lib/commons-logging") || target.startsWith("WEB-INF/lib/log4j")))
                        {
                            System.out.println("Stripping logger "+target);
                            continue;
                        }
                        addFile(target, source, jout);
                    }
                }
                finally
                {
                    source.close();
                }
            }

            if (webXml == null)
            {
                throw new IllegalArgumentException("WEB-INF/web.xml");
            }
            if (portletXml == null)
            {
                throw new IllegalArgumentException("WEB-INF/portlet.xml");
            }
            
            JetspeedWebApplicationRewriterFactory webRewriterFactory = new JetspeedWebApplicationRewriterFactory();
            JetspeedWebApplicationRewriter webRewriter = webRewriterFactory.getInstance(
                    webXml,
                    portletApplicationName,
                    forcedVersion);
            webRewriter.processWebXML();
            JetspeedContextRewriter contextRewriter = new JetspeedContextRewriter(contextXml, portletApplicationName);
            contextRewriter.processContextXML();

            // write the web.xml, portlet.xml, and context.xml files
            addFile("WEB-INF/web.xml", webXml, jout);
            addFile("WEB-INF/portlet.xml", portletXml, jout);
            addFile("META-INF/context.xml", contextXml, jout);

            if (webRewriter.isPortletTaglibAdded() || !taglibFound)
            {
                System.out.println("Attempting to add portlet.tld to war...");
                InputStream is = this.getClass().getResourceAsStream("/org/apache/jetspeed/tools/deploy/portlet.tld");
                if (is == null)
                {
                    System.out.println("Failed to find portlet.tld in classpath");
                }
                else
                {
                    System.out.println("Adding portlet.tld to war...");

                    try
                    {
                        addFile("WEB-INF/tld/portlet.tld", is, jout);
                    }
                    finally
                    {
                        is.close();
                    }
                }
            }

            jout.close();
            jin.close();
            jin = null;
            jout = null;

            System.out.println("Creating war " + outputName + " ...");
            System.out.flush();
            // Now copy the new war to its destination
            srcChannel = new FileInputStream(tempFile).getChannel();
            dstChannel = new FileOutputStream(outputName).getChannel();
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
            srcChannel.close();
            srcChannel = null;
            dstChannel.close();
            dstChannel = null;
            tempFile.delete();
            tempFile = null;
            System.out.println("War " + outputName + " created");
            System.out.flush();
        }
        finally
        {
            if (srcChannel != null && srcChannel.isOpen())
            {
                try
                {
                    srcChannel.close();
                }
                catch (IOException e1)
                {
                    // ignore
                }
            }
            if (dstChannel != null && dstChannel.isOpen())
            {
                try
                {
                    dstChannel.close();
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
            if (jout != null)
            {
                try
                {
                    jout.close();
                    jout = null;
                }
                catch (IOException e1)
                {
                    // ignore
                }
            }
            if (tempFile != null && tempFile.exists())
            {
                tempFile.delete();
            }
        }
    }

    protected Document parseXml(InputStream source) throws Exception
    {
        // Parse using the local dtds instead of remote dtds. This
        // allows to deploy the application offline
        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setEntityResolver(new EntityResolver()
        {
            public InputSource resolveEntity(java.lang.String publicId, java.lang.String systemId) throws SAXException,
                            java.io.IOException
            {
                if (systemId.equals("http://java.sun.com/dtd/web-app_2_3.dtd"))
                {
                    return new InputSource(getClass().getResourceAsStream("web-app_2_3.dtd"));
                }
                return null;
            }
        });
        Document document = saxBuilder.build(source);
        return document;
    }

    protected void addFile(String path, InputStream source, JarOutputStream jos) throws IOException
    {
        jos.putNextEntry(new ZipEntry(path));
        try
        {
            int count;
            while ((count = source.read(buffer)) > 0)
            {
                jos.write(buffer, 0, count);
            }
        }
        finally
        {
            jos.closeEntry();
        }
    }

    protected void addFile(String path, Document source, JarOutputStream jos) throws IOException
    {
        if (source != null)
        {
            jos.putNextEntry(new ZipEntry(path));
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            try
            {
                xmlOutputter.output(source, jos);
            }
            finally
            {
                jos.closeEntry();
            }
        }
    }

    protected String getPortletApplicationName(String path)
    {
        File file = new File(path);
        String name = file.getName();
        String portletApplicationName = name;

        int index = name.lastIndexOf("-infused.war");
        if (index > -1)
        {
            portletApplicationName = name.substring(0, index);            
        }
        else
        {
            index = name.lastIndexOf(".");
            if (index > -1)
            {
                portletApplicationName = name.substring(0, index);
            }            
        }
        return portletApplicationName;
    }
}