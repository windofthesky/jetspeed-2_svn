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
package org.apache.jetspeed.maven.plugins;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.jetspeed.maven.utils.Artifacts;
import org.apache.jetspeed.tools.deploy.DeployFactory;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * DeployerMojo is a generic Jetspeed Portal artifacts en resources deployer.
 * 
 * @version $Id$
 * @goal deploy
 */
public class DeployMojo extends AbstractMojo
{
    private static final String DEPLOY_FACTORY_CLASS_NAME = "org.apache.jetspeed.tools.deploy.JetspeedDeployFactory";

    public static class Deployment
    {
        private String artifact;
        private String file;
        private String type;
        private String target;
        private String delete;
        private boolean expand;
        private boolean infuse;
        private Boolean stripLoggers;
        private String forcedVersion;
    }
    
    private static class DeploymentObject
    {
        Deployment deployment;
        String name;
        File fileHandle;
        Artifact artifact;
    }
    
    /**
     * The target deploy directory.
     * @parameter
     * @required
     */
    private String targetDeployDirectory;
    
    /**
     * @parameter expression="false";
     */
    private boolean infusionStripLoggers;
    
    /**
     * @parameter expression="2.3";
     */
    private String infusionForcedVersion;
    
    /**
     * When true, INFO log copied/skipped resources
     * @parameter default-value="false"
     */
    private boolean verbose;
    
    /**
     * @parameter
     * @required
     */
    private Deployment[] deployments;
    
    private List objects = new ArrayList();
    
    /** The Maven project.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    /**
     * @parameter
     */
    private Map paths;

    /** @parameter expression="${plugin.introducedDependencyArtifacts}" */
    private Set pluginDependencyArtifacts;
    private Artifacts artifacts;
    
    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        File targetBaseDir = new File(targetDeployDirectory);
        if (targetBaseDir.exists() && targetBaseDir.isFile())
        {
            throw new MojoExecutionException("targetBaseDirectory "+targetDeployDirectory+" points to a file, not a directory");
        }
        else if (!targetBaseDir.exists())
        {
            targetBaseDir.mkdirs();
        }
        
        artifacts = new Artifacts(pluginDependencyArtifacts);
        
        HashMap pathsMap = new HashMap();
        // init default for tomcat5.5
        pathsMap.put("lib","shared/lib");
        pathsMap.put("war", "webapps");
        pathsMap.put("portal", "jetspeed");
        pathsMap.put("deploy", "WEB-INF/deploy");        
        pathsMap.put("local", "WEB-INF/deploy/local");
        if (paths != null)
        {
            pathsMap.putAll(paths);
        }
        
        boolean infusion = false;
        for (int i = 0; i < deployments.length; i++)
        {
            DeploymentObject dobj = new DeploymentObject();
            dobj.deployment = deployments[i];
            if (dobj.deployment.artifact != null)
            {
                dobj.artifact = artifacts.get(dobj.deployment.artifact);
                if (dobj.artifact == null)
                {
                    throw new MojoExecutionException("Artifact "+dobj.deployment.artifact+" dependency not defined");
                }
                dobj.fileHandle = dobj.artifact.getFile();
                dobj.name = dobj.deployment.artifact;
            }
            else if (dobj.deployment.file != null)
            {
                dobj.fileHandle = new File(dobj.deployment.file);
                if (!dobj.fileHandle.exists() || !dobj.fileHandle.isFile())
                {
                    throw new MojoExecutionException("Deployment file "+dobj.deployment.file+" not found");
                }
                dobj.name = dobj.deployment.file;
            }
            if (pathsMap.get(dobj.deployment.type) == null)
            {
                throw new MojoExecutionException("Deployment "+dobj.name+" has unknown type "+dobj.deployment.type);
            }
            if ("war".equals(dobj.deployment.type))
            {
                if (dobj.deployment.infuse)
                {
                    infusion = true;
                    dobj.deployment.stripLoggers = getValue(dobj.deployment.stripLoggers,infusionStripLoggers);
                    dobj.deployment.forcedVersion = getValue(dobj.deployment.forcedVersion,infusionForcedVersion);
                }
            }
            objects.add(dobj);
        }
        
        DeployFactory deployFactory = null;
        if (infusion)
        {                
            if (deployFactory == null)
            {
                try
                {
                    Class dfClass = Class.forName(DEPLOY_FACTORY_CLASS_NAME, true, Thread.currentThread().getContextClassLoader());
                    deployFactory = (DeployFactory) dfClass.newInstance();
                }
                catch (Exception e)
                {
                    throw new MojoExecutionException("Cannot find or load DeployFactory class "+ DEPLOY_FACTORY_CLASS_NAME, e);
                }
            }
        }
        
        for (Iterator iter = objects.iterator(); iter.hasNext(); )
        {
            DeploymentObject dobj = (DeploymentObject)iter.next();
            File targetDir;
            if ("deploy".equals(dobj.deployment.type)||"local".equals(dobj.deployment.type))
            {
                targetDir = new File(new File(new File(targetBaseDir,(String)pathsMap.get("war")), (String)pathsMap.get("portal")), (String)pathsMap.get(dobj.deployment.type));                
            }
            else
            {
                targetDir = new File(targetBaseDir,(String)pathsMap.get(dobj.deployment.type));
            }
            if (targetDir.exists() && targetBaseDir.isFile())
            {
                throw new MojoExecutionException("targetDirectory "+targetDir.getAbsolutePath()+" points to a file, not a directory");
            }
            else if (!targetDir.exists())
            {
                targetDir.mkdirs();
            }
            File target = null;
            if ("war".equals(dobj.deployment.type))
            {
                if (!dobj.deployment.expand)
                {
                    if (dobj.deployment.target == null)
                    {
                        if (dobj.artifact != null)
                        {
                            target = new File(targetDir, dobj.artifact.getArtifactId() + "." + dobj.artifact.getType());
                        }
                        else
                        {
                            target = new File(targetDir, dobj.fileHandle.getName());
                        }
                    }
                    else
                    {
                        target = new File(targetDir, dobj.deployment.target);
                    }
                    if (target.isDirectory())
                    {
                        throw new MojoExecutionException("Resolved target file "+ target.getAbsolutePath()+" points to a directory");
                    }
                }
                else
                {
                    if (dobj.deployment.target != null)
                    {
                        target = new File(targetDir, dobj.deployment.target);
                    }
                    else
                    {
                        String name = dobj.fileHandle.getName();
                        int index = name.lastIndexOf(".");
                        if (index > -1)
                        {
                            name = name.substring(0, index);
                        }
                        target = new File(targetDir, name);
                    }
                    if (target.isFile())
                    {
                        throw new MojoExecutionException("Resolved target directory "+ target.getAbsolutePath()+" points to a file");
                    }
                    if (target.exists() && !rmdir(target))
                    {
                        throw new MojoExecutionException("Failed to remove target directory "+ target.getAbsolutePath());
                    }
                }
                if (dobj.deployment.delete != null)
                {
                    File delete = null;
                    if (dobj.deployment.expand)
                    {
                        delete = new File(target, dobj.deployment.delete);
                    }
                    else
                    {
                        delete = new File(target.getParentFile(), dobj.deployment.delete);
                    }
                    if (delete.exists())
                    {
                        if (delete.isDirectory())
                        {
                            if (!rmdir(delete))
                            {
                                throw new MojoExecutionException("Failed to delete directory "+ delete.getAbsolutePath());
                            }
                        }
                        else
                        {
                            if (!delete.delete())
                            {
                                throw new MojoExecutionException("Failed to delete file "+ target.getAbsolutePath());
                            }
                        }
                    }
                }
            }
            else 
            {
                if (dobj.deployment.target != null)
                {
                    target = new File(targetDir, dobj.deployment.target);
                }
                else
                {
                    target = new File(targetDir, dobj.fileHandle.getName());
                }
                
                if ("lib".equals(dobj.deployment.type) && dobj.artifact != null)
                {
                    deleteMatchingFiles(targetDir, dobj.artifact.getArtifactId(), dobj.artifact.getType());
                }
            }
            if (dobj.deployment.infuse)
            {                
                File tmpTarget = null;
                String targetPath;            
                if (dobj.deployment.expand)
                {
                    try
                    {
                        tmpTarget = File.createTempFile(target.getName(), "");
                        targetPath = tmpTarget.getAbsolutePath();
                    }
                    catch (IOException e)
                    {
                        throw new MojoExecutionException("Failed to create temporary file",e);
                    }
                }
                else
                {
                    targetPath = target.getAbsolutePath();
                }
                try
                {
                    deployFactory.getInstance(dobj.fileHandle.getAbsolutePath(), targetPath, dobj.deployment.stripLoggers.booleanValue(), dobj.deployment.forcedVersion);
                }
                catch (Exception e)
                {
                    throw new MojoExecutionException("Failed to infuse "+dobj.fileHandle.getAbsolutePath(),e);
                }
                if (tmpTarget != null)
                {
                    try
                    {
                        expandWar(tmpTarget, target);
                    }
                    catch (IOException e)
                    {
                        throw new MojoExecutionException("Failed to expand infused war",e);
                    }
                    finally
                    {
                        tmpTarget.delete();
                    }
                }
            }
            else
            {
                try
                {
                    copyFile(dobj.fileHandle, target);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    throw new MojoExecutionException("Failed to copy "+dobj.fileHandle.getAbsolutePath() +" to "+target.getAbsolutePath(),e);
                }
            }
        }
    }
    
    private static String getValue(String value, String defaultValue)
    {
        return value != null ? value : defaultValue;
    }
    
    private static Boolean getValue(Boolean value, boolean defaultValue)
    {
        return value != null ? value : new Boolean(defaultValue);
    }
    
    private static final void deleteMatchingFiles(File dir, final String namePrefix, final String ext)
    {
        File[] matched = dir.listFiles(new FileFilter(){

            public boolean accept(File file)
            {
                String name = file.getName();
                int index = name.lastIndexOf(".");
                return index > -1 && file.isFile() && ext.equals(name.substring(index+1)) && name.startsWith(namePrefix);
            }});
        
        if (matched != null)
        {
            for (int i = 0; i < matched.length; i++ )
            {
                matched[i].delete();
            }
        }
    }
    
    private void copyFile(File src, File dest) throws IOException
    {
        FileChannel srcChannel = null;
        FileChannel dstChannel = null;

        try
        {
            dest.createNewFile();
            srcChannel = new FileInputStream(src).getChannel();
            dstChannel = new FileOutputStream(dest).getChannel();
            dstChannel.transferFrom(srcChannel, 0, srcChannel.size());
            srcChannel.close();
            dstChannel.close();
        }
        finally
        {
            if ( srcChannel != null && srcChannel.isOpen() )
            {
                try
                {
                    srcChannel.close();
                }
                catch (Exception e)
                {
                }
            }
            if ( dstChannel != null && dstChannel.isOpen() )
            {
                try
                {
                    dstChannel.close();
                }
                catch (Exception e)
                {
                }
            }
        }
    }

    private static final boolean rmdir(File dir)
    {    
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                boolean success = rmdir(new File(dir, children[i]));
                if (!success)
                {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it OR it is a plain file
        return dir.delete();
    }

    private static void expandWar( File file, File target) throws IOException
    {
        JarFile jarFile = new JarFile(file);

        if (!target.exists())
        {
            target.mkdirs();
        }

        Enumeration entries = jarFile.entries();
        while (entries.hasMoreElements())
        {
            JarEntry jarEntry = (JarEntry) entries.nextElement();
            String name = jarEntry.getName();
            if (jarEntry.isDirectory())
            {
                new File(target, name).mkdir();
            }
            else
            {
                copyEntryToFile(jarFile, target, jarEntry);
            }
        }
    }

    /**
     * <p>
     * copyEntryToFile
     * </p>
     * 
     * @param jarFile
     * @param target
     * @param jarEntry
     * @throws IOException
     * @throws FileNotFoundException
     */
    private static void copyEntryToFile( JarFile jarFile, File target, JarEntry jarEntry ) throws IOException,
            FileNotFoundException
    {
        String name = jarEntry.getName();
        File file = new File(target, name);
        if (!file.getParentFile().exists())
        {
            file.getParentFile().mkdirs();
        }
        file.createNewFile();

        InputStream is = null;
        OutputStream os = null;
        try
        {
            is = jarFile.getInputStream(jarEntry);
            os = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0)
            {
                os.write(buf, 0, len);
            }
        }
        finally
        {
            if (is != null)
            {
                is.close();
            }

            if (os != null)
            {
                os.close();
            }
        }
    }
}
