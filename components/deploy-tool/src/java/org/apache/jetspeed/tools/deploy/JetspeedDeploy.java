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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.util.jar.JarInputStream;
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
 * @author <a href="mailto:taylor@apache.org">Dain Sundstrom</a>
 * @author <a href="mailto:dsundstrom@gluecode.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedDeploy implements Deploy
{
    public static void main(String[] args) throws Exception 
    {        
        if (args.length < 2 || args.length > 3) 
        {
            System.out.println("Usage: java -jar jsdeploy.jar [-r] INPUT OUTPUT");
            System.out.println("       -r Register at Init");
            System.exit(1);
            return;
        }
        if (args.length == 3)
        {
            if (args[0].equalsIgnoreCase("-r"))
            {
                new JetspeedDeploy(args[1], args[2], true);
            }
            else
            {
                System.out.println("Usage: java -jar jsdeploy.jar [-r] INPUT OUTPUT");
                System.out.println("       -r Register at Init");
                System.exit(1);
                return;                
            }
        }
        else
        {
            new JetspeedDeploy(args[0], args[1], false);
        }
    }

    private final byte[] buffer = new byte[4096];
    
    public JetspeedDeploy(String inputName, String outputName, boolean registerAtInit) throws Exception 
    {
        JarInputStream jin = null;
        JarOutputStream jout = null;
        try 
        {
            String portletApplicationName = getPortletApplicationName(outputName);
            jin = new JarInputStream(new FileInputStream(inputName));
            jout = new JarOutputStream(new FileOutputStream(outputName), jin.getManifest());
                
            // copy over all of the files in the input war to the output
            // war except for web.xml, portlet.xml, and context.xml which
            // we parse for use later
            Document webXml = null;
            Document portletXml = null;
            Document contextXml = null;
            ZipEntry src;
            while ((src = jin.getNextEntry()) != null) 
            {
                String target = src.getName();
                if ("WEB-INF/web.xml".equals(target)) 
                {
                    System.out.println("Found web.xml");
                    webXml = parseXml(jin);
                } 
                else if ("WEB-INF/portlet.xml".equals(target)) 
                {
                    System.out.println("Found WEB-INF/portlet.xml");
                    portletXml = parseXml(jin);
                } 
                else if ("META-INF/context.xml".equals(target))
                {
                    System.out.println("Found META-INF/context.xml");
                    contextXml = parseXml(jin);
                } 
                else 
                {
                    addFile(target, jin, jout);
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
                
            JetspeedWebApplicationRewriter webRewriter = new JetspeedWebApplicationRewriter(webXml, portletApplicationName, registerAtInit);
            webRewriter.processWebXML();
            JetspeedContextRewriter contextRewriter = new JetspeedContextRewriter(contextXml, portletApplicationName);
            contextRewriter.processContextXML();
                
            // write the web.xml, portlet.xml, and context.xml files
            addFile("WEB-INF/web.xml", webXml, jout);
            addFile("WEB-INF/portlet.xml", portletXml, jout);
            addFile("META-INF/context.xml", contextXml, jout);
                
            if(webRewriter.isPortletTaglibAdded())
            {
                System.out.println("Attempting to add portlet.tld to war...");
                InputStream is = this.getClass().getResourceAsStream("/org/apache/jetspeed/tools/deploy/portlet.tld");
                if(is == null)
                {
                    System.out.println("Failed to find portlet.tld in classpath");
                }
                else
                {
                    System.out.println("Adding portlet.tld to war...");
                        
                    addFile("WEB-INF/tld/portlet.tld", is, jout);
                    is.close();
                }
            }
                
            jout.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
                
            if(jin != null) 
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
            if(jout != null) {
                try {
                    jout.close();
                    jout = null;
                } catch (IOException e1) {
                    // ignore
                }
            }
            new File(outputName).delete();
        }
    }
        
    private Document parseXml(InputStream jin) throws Exception {
        // Parse using the local dtds instead of remote dtds. This
        // allows to deploy the application offline
        SAXBuilder saxBuilder = new SAXBuilder();
        saxBuilder.setEntityResolver(new EntityResolver()
            {
                public InputSource resolveEntity( java.lang.String publicId, java.lang.String systemId )
                    throws SAXException, java.io.IOException
                {                    
                    if (systemId.equals("http://java.sun.com/dtd/web-app_2_3.dtd"))
                    {
                        return new InputSource(getClass().getResourceAsStream("web-app_2_3.dtd"));
                    }
                    return null;
                }
            });
        Document document = saxBuilder.build(new UncloseableInputStream(jin));
        return document;
    }

    private void addFile(String path, InputStream source, JarOutputStream jos) throws IOException 
    {
        jos.putNextEntry(new ZipEntry(path));
        try {
            int count;
            while ((count = source.read(buffer)) > 0) {
                jos.write(buffer, 0, count);
            }
        } finally {
            jos.closeEntry();
        }
    }
        
    private void addFile(String path, Document source, JarOutputStream jos) throws IOException {
        if (source != null)
        {
            jos.putNextEntry(new ZipEntry(path));
            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            try {
                xmlOutputter.output(source, jos);
            } finally {
                jos.closeEntry();
            }
        }
    }
        
    private String getPortletApplicationName(String path)
    {
        File file = new File(path);
        String name = file.getName();
        String portletApplicationName = name;
            
        int index = name.lastIndexOf(".");
        if (index > -1)
        {
            portletApplicationName = name.substring(0, index); 
        }
        return portletApplicationName;
    }

    private class UncloseableInputStream extends InputStream {
        private final InputStream in;

        public UncloseableInputStream(InputStream in) {
            this.in = in;
        }

        public int read() throws IOException {
            return in.read();
        }

        public int read(byte b[]) throws IOException {
            return in.read(b);
        }

        public int read(byte b[], int off, int len) throws IOException {
            return in.read(b, off, len);
        }

        public long skip(long n) throws IOException {
            return in.skip(n);
        }

        public int available() throws IOException {
            return in.available();
        }

        public void close() throws IOException {
            // not closeable
        }

        public void mark(int readlimit) {
            in.mark(readlimit);
        }

        public void reset() throws IOException {
            in.reset();
        }

        public boolean markSupported() {
            return in.markSupported();
        }
    }
}
