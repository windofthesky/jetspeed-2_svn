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

/**
 * Makes a web application Deploy-ready for Jetspeed. 
 *
 * @author <a href="mailto:taylor@apache.org">Dain Sundstrom</a>
 * @author <a href="mailto:dsundstrom@gluecode.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedDeploy 
{
    public static void main(String[] args) throws Exception 
    {
        if (args.length != 2) {
            System.out.println("Usage: java -jar jsdeploy.jar INPUT OUTPUT");
            System.exit(1);
            return;
        }

        new JetspeedDeploy(args[0], args[1]);
    }

    private final byte[] buffer = new byte[4096];
    
    public JetspeedDeploy(String inputName, String outputName) throws Exception 
    {
        JarInputStream jin = null;
        JarOutputStream jout = null;
        try 
        {
            String portletApplicationName = getPortletApplicationName(outputName);
            jin = new JarInputStream(new FileInputStream(inputName));
            jout = new JarOutputStream(new FileOutputStream(outputName), jin.getManifest());
            
            // copy over all of the files in the input war to the output
            // war except for web.xml and portlet.xml, which we parse for
            // use later
            Document webXml = null;
            Document portletXml = null;
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
            
            JetspeedWebApplicationRewriter rewriter = new JetspeedWebApplicationRewriter(webXml, portletApplicationName, true);
            rewriter.processWebXML();
            
            // mung the web.xml
            //webXml.getRootElement().setAttribute("foo", "bar");

            // write the web.xml and portlet.xml files
            addFile("WEB-INF/web.xml", webXml, jout);
            addFile("WEB-INF/portlet.xml", portletXml, jout);
            
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
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(new UncloseableInputStream(jin));
        return document;
    }

    public void addFile(String path, InputStream source, JarOutputStream jos) throws IOException 
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

    public void addFile(String path, Document source, JarOutputStream jos) throws IOException {
        jos.putNextEntry(new ZipEntry(path));

        XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            xmlOutputter.output(source, jos);
        } finally {
            jos.closeEntry();
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