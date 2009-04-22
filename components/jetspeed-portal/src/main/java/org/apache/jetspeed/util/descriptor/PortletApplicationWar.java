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
package org.apache.jetspeed.util.descriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.descriptor.JetspeedDescriptorService;
import org.apache.jetspeed.om.portlet.PortletApplication;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.SecurityRole;
import org.apache.jetspeed.tools.pamanager.PortletApplicationException;
import org.apache.jetspeed.util.DirectoryHelper;
import org.apache.jetspeed.util.FileSystemHelper;
import org.apache.jetspeed.util.MultiFileChecksumHelper;
import org.apache.pluto.container.om.portlet.SecurityRoleRef;

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
    public static final String PORTLET_XML_PATH = "WEB-INF/portlet.xml";
    public static final String WEB_XML_PATH = "WEB-INF/web.xml";
    public static final String EXTENDED_PORTLET_XML_PATH = "WEB-INF/jetspeed-portlet.xml";

    protected static final Logger log = LoggerFactory.getLogger("deployment");

    protected String paName;
    protected String webAppContextRoot;
    protected FileSystemHelper warStruct;
    
    private PortletApplication portletApp;
    private long paChecksum;
    protected JetspeedDescriptorService descriptorService;

    /**
     * @param warFile
     *            {@link org.apache.jetspeed.util.FileSystemHelper}representing
     *            the WAR file we are working with. This
     *            <code>FileSystemHelper</code> can be an actual WAR file or a
     *            directory structure layed out in a WAR-like fashion. name of
     *            the portlet application the <code>warPath</code> contains
     * @param webAppContextRoot
     *            context root relative to the servlet container of this app
     */
    public PortletApplicationWar( FileSystemHelper warStruct, String paName, String webAppContextRoot, JetspeedDescriptorService descriptorService)
    {
        this(warStruct, paName, webAppContextRoot, 0, descriptorService);
    }

    public PortletApplicationWar( FileSystemHelper warStruct, String paName, String webAppContextRoot, long paChecksum, JetspeedDescriptorService descriptorService)
    {
        validatePortletApplicationName(paName);

        this.paName = paName;
        this.webAppContextRoot = webAppContextRoot;
        this.warStruct = warStruct;
        this.paChecksum = paChecksum;
        this.descriptorService = descriptorService;
    }
    
    public long getPortletApplicationChecksum() throws IOException
    {
        if ( this.paChecksum == 0)
        {
            this.paChecksum = MultiFileChecksumHelper.getChecksum(new File[] {
                    new File(warStruct.getRootDirectory(), WEB_XML_PATH),
                    new File(warStruct.getRootDirectory(), PORTLET_XML_PATH),
                    new File(warStruct.getRootDirectory(), EXTENDED_PORTLET_XML_PATH) });
        }
        if (this.paChecksum == 0)
        {
          throw new IOException("Cannot find any deployment descriptor for Portlet Application "+paName);
        }
      return paChecksum;
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
     * createPortletApp
     * </p>
     * Creates a portlet application object based of the WAR file's
     * WEB-INF/portlet.xml
     * 
     * @return @throws
     *         PortletApplicationException
     * @throws Exception
     * @see org.apache.jetspeed.uitl.descriptor.PortletApplicationDescriptor
     */
    public PortletApplication createPortletApp(ClassLoader classLoader) throws Exception
    {
        InputStream webXmlStream = getInputStream(WEB_XML_PATH);
        InputStream portletXmlStream = getInputStream(PORTLET_XML_PATH);
        InputStream extStream = null;
        try
        {
            extStream = getInputStream(EXTENDED_PORTLET_XML_PATH);
        }
        catch (FileNotFoundException e)
        {
            // no problem, file doesn't exist
        }
        try
        {
            portletApp = descriptorService.read(paName, webAppContextRoot, webXmlStream, portletXmlStream, extStream, classLoader);
            validate();
            portletApp.setName(paName);
            portletApp.setContextPath(webAppContextRoot);
            portletApp.setChecksum(paChecksum);
            return portletApp;
        }
        finally
        {
            if (webXmlStream != null)
            {
                webXmlStream.close();
            }
            if (portletXmlStream != null)
            {
                portletXmlStream.close();
            }
            if (null != extStream)
            {
                extStream.close();
            }            
        }
    }

    public PortletApplication createPortletApp() throws Exception
    {
        return createPortletApp(this.getClass().getClassLoader());
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
        if (child == null)             
        {
            throw new FileNotFoundException("Unable to locate file or path " + child);
        }
        FileOutputStream fileOutputStream = new FileOutputStream(child);
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

            return new PortletApplicationWar(target, paName, webAppContextRoot, paChecksum, this.descriptorService);

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
        if (portletApp == null)
        {
            throw new IllegalStateException(
                    "createPortletApp() must be called before invoking validate()");
        }

        List<PortletDefinition> portlets = portletApp.getPortlets();
        for (PortletDefinition portlet : portlets)
        {
            for (SecurityRoleRef roleRef : portlet.getSecurityRoleRefs())
            {
                String roleName = roleRef.getRoleLink();
                if (roleName == null || roleName.length() == 0)
                {
                    roleName = roleRef.getRoleName();
                }
                boolean found = false;
                for (SecurityRole role : portletApp.getSecurityRoles())
                {
                    if (role.getName().equals(roleName))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    String errorMsg = "Undefined security role " + roleName + " referenced from portlet "
                            + portlet.getPortletName();
                    throw new PortletApplicationException(errorMsg);
                }
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
        ArrayList<URL> urls = new ArrayList<URL>();
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
    
    public FileSystemHelper getFileSystem()
    {
        return warStruct;
    }

    public PortletApplication getPortletApp()
    {
        return portletApp;
    }

}
