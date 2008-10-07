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
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.jetspeed.maven.utils.Artifacts;
import org.apache.jetspeed.tools.deploy.DeployFactory;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

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
        private String destination;
        private String targetName;
        private Boolean stripVersionId;
        private String delete;
        private Boolean expand;
        private boolean expandKeepExt;
        private Boolean infuse;
        private Boolean infusionStripLoggers;
        private String infusionForcedVersion;
    }
    
    private static class DeploymentObject
    {
        private String type;
        private Deployment deployment;        
        private File src;
        private File targetDir;
        private Artifact artifact;
    }
    
    /**
     * The target base directory.
     * @parameter
     * @required
     */
    private String targetBaseDir;
    
    /**
     * The name of the portal web application
     * @parameter expression="jetspeed"
     */
    private String portalName;
    
    /**
     * @parameter expression="false";
     */
    private Boolean infusionStripLoggers;
    
    /**
     * @parameter expression="2.3"
     */
    private String infusionForcedVersion;
    
    /**
     * @parameter
     * @required
     */
    private Deployment[] deployments;
    
    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;
    
    /**
     * @parameter
     */
    private Map destinations;

    /** @parameter expression="${plugin.introducedDependencyArtifacts}" */
    private Set pluginDependencyArtifacts;
    
    private Artifacts artifacts;
    
    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        HashMap destMap = new HashMap();
        if (destinations != null)
        {
            destMap.putAll(destinations);
        }
        // init default for tomcat5.5
        if (!destMap.containsKey("system"))
        {
            destMap.put("system","common/endorsed");
        }
        if (!destMap.containsKey("lib"))
        {
            destMap.put("lib","shared/lib");
        }
        if (!destMap.containsKey("war"))
        {
            destMap.put("war", "webapps");
        }
        if (!destMap.containsKey("deploy"))
        {
            destMap.put("deploy", ((String)destMap.get("war"))+"/"+portalName+"/WEB-INF/deploy");
        }
        if (!destMap.containsKey("local"))
        {
            destMap.put("local", ((String)destMap.get("deploy"))+"/local");
        }
        
        File targetBaseDir = new File(this.targetBaseDir);
        if (targetBaseDir.exists() && targetBaseDir.isFile())
        {
            throw new MojoExecutionException("targetBaseDir "+this.targetBaseDir+" points to a file, not a directory");
        }
        File portalDeployDir = new File(targetBaseDir,(String)destMap.get("deploy"));                
        File localPortalDeployDir = new File(targetBaseDir, (String)destMap.get("local"));                
        
        artifacts = new Artifacts(pluginDependencyArtifacts);
        
        boolean infusion = false;
        
        List objects = new ArrayList();
                
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
                dobj.src = dobj.artifact.getFile();
                if (dobj.src == null)
                {
                    String location = localRepository.pathOf(dobj.artifact);
                    if (location == null)
                    {
                        throw new MojoExecutionException("Artifact "+dobj.deployment.artifact+" could not be found in local repository");
                    }
                    dobj.src = new File(localRepository.getBasedir(), location);
                }
                dobj.type = dobj.artifact.getType();
            }
            else if (dobj.deployment.file != null)
            {
                dobj.src = new File(dobj.deployment.file);
                if (!dobj.src.exists() || !dobj.src.isFile())
                {
                    throw new MojoExecutionException("Deployment file "+dobj.deployment.file+" not found");
                }
                int index = dobj.deployment.file.lastIndexOf(".");
                if (index > -1 && index < dobj.deployment.file.length()-1)
                {
                    dobj.type = dobj.deployment.file.substring(index+1);
                }
            }
            else
            {
                throw new MojoExecutionException("A deployment requires an artifact of file specification");
            }
            
            if ("war".equals(dobj.type))
            {
                dobj.deployment.destination = getValue(dobj.deployment.destination,"war");
                if ("war".equals(dobj.deployment.destination))
                {
                    dobj.deployment.expand = getValue(dobj.deployment.expand, Boolean.FALSE);
                    dobj.deployment.infuse = getValue(dobj.deployment.infuse, Boolean.FALSE);
                    dobj.deployment.infusionStripLoggers = getValue(dobj.deployment.infusionStripLoggers, infusionStripLoggers);
                    dobj.deployment.infusionForcedVersion = getValue(dobj.deployment.infusionForcedVersion, infusionForcedVersion);
                }
                else
                {
                    dobj.deployment.expand = Boolean.FALSE;
                    dobj.deployment.infuse = Boolean.FALSE;
                }
            }
            else
            {
                dobj.deployment.destination = getValue(dobj.deployment.destination, "jar".equals(dobj.type) ? "lib" : null);
                dobj.deployment.expand = Boolean.FALSE;
                dobj.deployment.infuse = Boolean.FALSE;
            }
            if (destMap.get(dobj.deployment.destination) == null)
            {
                throw new MojoExecutionException("Unknown or unspecified deployment destination: \""+dobj.deployment.destination+"\"");
            }
            
            if (dobj.deployment.infuse.booleanValue())
            {
                infusion = true;
            }
            boolean portalDeploy = "deploy".equals(dobj.deployment.destination);
            boolean localPortalDeploy = "local".equals(dobj.deployment.destination);
            
            if (dobj.deployment.targetName == null)
            {
                if (dobj.artifact != null)
                {
                    Boolean defaultStripVersionId = "war".equals(dobj.type) || "war".equals(dobj.deployment.destination) || portalDeploy || localPortalDeploy ? Boolean.TRUE : Boolean.FALSE;
                    if (getValue(dobj.deployment.stripVersionId, defaultStripVersionId).booleanValue())
                    {
                        dobj.deployment.targetName = dobj.artifact.getArtifactId() + "." + dobj.artifact.getType();
                    }
                    else
                    {
                        dobj.deployment.targetName = dobj.artifact.getArtifactId() + "-"+ dobj.artifact.getVersion() + "." + dobj.artifact.getType();
                    }
                }
                else
                {
                    dobj.deployment.targetName = dobj.src.getName();
                }
                if (dobj.deployment.expand.booleanValue() && !dobj.deployment.expandKeepExt)
                {
                    int index = dobj.deployment.targetName.lastIndexOf(".");
                    if (index > -1)
                    {
                        dobj.deployment.targetName = dobj.deployment.targetName.substring(0, index);
                    }
                }
            }
            if (portalDeploy)
            {
                dobj.targetDir = portalDeployDir;
            }
            else if (localPortalDeploy)
            {
                dobj.targetDir = localPortalDeployDir;
            }
            else
            {
                dobj.targetDir = new File(targetBaseDir, (String)destMap.get(dobj.deployment.destination));
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
        
        String deploySelection = System.getProperty("deploy");
        if (deploySelection != null)
        {
            List selection = new ArrayList();
            StringTokenizer st = new StringTokenizer(deploySelection,",;");
            while (st.hasMoreTokens())
            {
                String destination = st.nextToken();
                String name = null;
                int split = destination.indexOf(":");
                if (split > 0)
                {
                    name = destination.substring(split+1);
                    destination = destination.substring(0, split);
                }
                for (Iterator iter = objects.iterator(); iter.hasNext(); )
                {
                    DeploymentObject dobj = (DeploymentObject)iter.next();
                    if (dobj.deployment.destination.equals(destination))
                    {
                        if (name == null || name.equals(dobj.deployment.targetName) && !(selection.contains(dobj)))
                        {
                            selection.add(dobj);
                        }
                    }
                }
            }
            objects = selection;
        }
        
        if (objects.size() > 0)
        {
            getLog().info("Deploying to targetBaseDir "+targetBaseDir.getAbsolutePath());
        }
        
        for (Iterator iter = objects.iterator(); iter.hasNext(); )
        {
            DeploymentObject dobj = (DeploymentObject)iter.next();

            String infoPostFix = "";
            if (dobj.deployment.infuse.booleanValue())
            {
                infoPostFix += " (infused";
                if (dobj.deployment.expand.booleanValue())
                {
                    infoPostFix += ", expanded";
                }
                infoPostFix += ")";
            }
            else if (dobj.deployment.expand.booleanValue())
            {
                infoPostFix += " (expanded)";
            }
            
            getLog().info("  deploying to "+dobj.deployment.destination+": "+dobj.deployment.targetName + infoPostFix);
            
            checkMkdirs(dobj.targetDir);
            
            if (dobj.deployment.delete != null)
            {
                File delete = new File(dobj.targetDir, dobj.deployment.delete);

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
                            throw new MojoExecutionException("Failed to delete file "+ delete.getAbsolutePath());
                        }
                    }
                }
            }
            
            File target = new File(dobj.targetDir, dobj.deployment.targetName);
            
            if (target.exists())
            {
                if (dobj.deployment.expand.booleanValue() && !target.isDirectory())
                {
                    throw new MojoExecutionException("Resolved target directory "+ target.getAbsolutePath()+" points to a file");
                }
                else if (!dobj.deployment.expand.booleanValue() && target.isDirectory())
                {
                    throw new MojoExecutionException("Resolved target file "+ target.getAbsolutePath()+" points to a directory");
                }
            }
            
            if ("lib".equals(dobj.deployment.destination) && dobj.artifact != null)
            {
                deleteMatchingFiles(dobj.targetDir, dobj.artifact.getArtifactId(), dobj.artifact.getType());
            }

            if (dobj.deployment.infuse.booleanValue())
            {                
                File tmpTarget = null;
                try
                {
                    tmpTarget = File.createTempFile(dobj.src.getName()+".infused-", "");
                    tmpTarget.deleteOnExit();
                }
                catch (IOException e)
                {
                    throw new MojoExecutionException("Failed to create temporary file",e);
                }
                try
                {
                    deployFactory.getInstance(dobj.src.getAbsolutePath(), tmpTarget.getAbsolutePath(), dobj.deployment.targetName, dobj.deployment.infusionStripLoggers.booleanValue(), dobj.deployment.infusionForcedVersion);
                    dobj.src = tmpTarget;
                }
                catch (Exception e)
                {
                    throw new MojoExecutionException("Failed to infuse "+dobj.src.getAbsolutePath(),e);
                }
            }
            if (dobj.deployment.expand.booleanValue())
            {
                try
                {
                    if (target.exists() && !rmdir(target))
                    {
                        throw new MojoExecutionException("Failed to delete directory "+ target.getAbsolutePath());
                    }
                    expandWar(dobj.src, target);
                }
                catch (IOException e)
                {
                    throw new MojoExecutionException("Failed to expand infused war",e);
                }
            }
            else
            {
                try
                {
                    copyFile(dobj.src, target);
                }
                catch (Exception e)
                {
                    throw new MojoExecutionException("Failed to copy "+dobj.src.getAbsolutePath() +" to "+target.getAbsolutePath(),e);
                }
            }
        }
    }
    
    private static String getValue(String value, String defaultValue)
    {
        return value != null ? value : defaultValue;
    }
    
    private static Boolean getValue(Boolean value, Boolean defaultValue)
    {
        return value != null ? value : defaultValue;
    }
    
    private static void checkMkdirs(File dir) throws MojoExecutionException
    {
        if (dir.exists())
        {
            if (dir.isFile())
            {
                throw new MojoExecutionException("Target "+dir.getAbsolutePath()+" points to a file, not a directory");
            }
        }
        else
        {
            dir.mkdirs();
        }
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
