/*
 * Created on Apr 23, 2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSelectInfo;
import org.apache.commons.vfs.FileSelector;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemManager;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.impl.VFSClassLoader;
import org.apache.jetspeed.components.util.ContextClassLoaderAlteringProxy;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.deployer.Deployer;
import org.nanocontainer.integrationkit.ContainerBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ClassHierarchyIntrospector;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver </a>
 *  
 */
public class DependencyAwareDeployer implements Deployer
{

    private final FileSystemManager fileSystemManager;
    private static final Log log = LogFactory.getLog(DependencyAwareDeployer.class);

    public DependencyAwareDeployer( FileSystemManager fileSystemManager )
    {
        this.fileSystemManager = fileSystemManager;
    }

    /**
     * Deploys an application.
     * 
     * @param applicationFolder
     *            the root applicationFolder of the application.
     * @param parentClassLoader
     *            the classloader that loads the application classes.
     * @param parentContainerRef
     *            reference to the parent container (can be used to lookup
     *            components form a parent container).
     * @return an ObjectReference holding a PicoContainer with the deployed
     *         components
     * @throws org.apache.commons.vfs.FileSystemException
     *             if the file structure was bad.
     * @throws org.nanocontainer.integrationkit.PicoCompositionException
     *             if the deployment failed for some reason.
     */
    public ObjectReference deploy( FileObject applicationFolder, ClassLoader parentClassLoader, ObjectReference parentContainerRef )
            throws FileSystemException, ClassNotFoundException
    {
        // ClassLoader applicationClassLoader = new VFSClassLoader(applicationFolder, fileSystemManager, parentClassLoader);
        List jars = getJarDependecies(applicationFolder);
        jars.add(applicationFolder);
        FileObject[] allJars = (FileObject[]) jars.toArray(new FileObject[jars.size()]);
        
        ClassLoader applicationClassLoader = new VFSClassLoader(allJars, fileSystemManager, parentClassLoader);
        FileObject deploymentScript = getDeploymentScript(applicationFolder);

        ObjectReference result = new SimpleReference();

        String extension = "." + deploymentScript.getName().getExtension();
        Reader scriptReader = new InputStreamReader(deploymentScript.getContent().getInputStream());
        String builderClassName = NanoContainer.getBuilderClassName(extension);

        NanoContainer nanoContainer = new NanoContainer(scriptReader, builderClassName, applicationClassLoader);
        ContainerBuilder builder = nanoContainer.getContainerBuilder();
        
        ContainerBuilder builderProxy = (ContainerBuilder) Proxy.newProxyInstance(applicationClassLoader, new Class[]{ContainerBuilder.class}, new ContextClassLoaderAlteringProxy(builder, applicationClassLoader));
        
        builderProxy.buildContainer(result, parentContainerRef, null);
        PicoContainer realContainer = (PicoContainer) result.get();
        
        Class[] containerInterfaces = ClassHierarchyIntrospector.getAllInterfaces(realContainer.getClass());
        
        PicoContainer parentContainer = realContainer.getParent();
        if(parentContainer != null && realContainer instanceof MutablePicoContainer)            
        {
            Class[] parentContainerInterfaces = ClassHierarchyIntrospector.getAllInterfaces(parentContainer.getClass());
            ((MutablePicoContainer)realContainer).setParent((PicoContainer) Proxy.newProxyInstance(applicationClassLoader, parentContainerInterfaces, new ContextClassLoaderAlteringProxy(parentContainer, applicationClassLoader)));
        }
        
        PicoContainer proxyContainer = (PicoContainer) Proxy.newProxyInstance(applicationClassLoader, containerInterfaces, new ContextClassLoaderAlteringProxy(realContainer, applicationClassLoader));
        result.set(proxyContainer);

        return result;
    }

    protected FileObject getDeploymentScript( FileObject applicationFolder ) throws FileSystemException
    {
        final FileObject metaInf = applicationFolder.getChild("META-INF");
        if (metaInf == null)
        {
            throw new FileSystemException("Missing META-INF folder in " + applicationFolder.getName().getPath());
        }
        final FileObject[] nanocontainerScripts = metaInf.findFiles(new FileSelector()
        {
            public boolean includeFile( FileSelectInfo fileSelectInfo ) throws Exception
            {
                return fileSelectInfo.getFile().getName().getBaseName().startsWith("nanocontainer");
            }

            public boolean traverseDescendents( FileSelectInfo fileSelectInfo ) throws Exception
            {
                return true;
            }
        });
        if (nanocontainerScripts == null || nanocontainerScripts.length < 1)
        {
            throw new FileSystemException("No deployment script (nanocontainer.[groovy|bsh|js|py|xml]) in " + applicationFolder.getName().getPath()
                    + "/META-INF");
        }
        return nanocontainerScripts[0];
    }

    protected List getJarDependecies( FileObject applicationFolder ) throws FileSystemException
    {
        final FileObject libDir = applicationFolder.getChild("lib");
        final ArrayList jars = new ArrayList();
        log.info("Checking for container Jar dependencies...");
        if (libDir != null && libDir.getType().equals(FileType.FOLDER))
        {
            final FileObject[] libFiles = libDir.getChildren();
            for (int i = 0; i < libFiles.length; i++)
            {
                if (libFiles[i].getName().getExtension().equals("jar"))
                {
                    log.info("Getting Jar dependency, "+libFiles[i].getName()+", for container");
                    jars.add(libFiles[i]);
                }
            }
        }
        
        return jars;
    }

}