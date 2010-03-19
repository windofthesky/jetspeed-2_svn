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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.settings.Settings;
import org.apache.maven.shared.invoker.CommandLineConfigurationException;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenCommandLineBuilder;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.invoker.SystemOutLogger;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.InterpolationFilterReader;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.WriterFactory;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.interpolation.MapBasedValueSource;
import org.codehaus.plexus.util.interpolation.RegexBasedInterpolator;

/**
 * Based upon org.apache.maven.plugin.invoker.InvokerMojo.java, r655038
 * 
 * @goal mvn
 * @aggregator
 * @requiresDirectInvocation true
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class MvnMojo extends AbstractMojo
{
    private static final Comparator<List<Target>> targetListComparator = new Comparator<List<Target>>()
    {
        public int compare(List<Target> o1, List<Target> o2)
        {
            for (Target t1 : o1)
            {
                for (Target t2 : o2)
                {
                    if (t1.id == t2.id)
                    {
                        return -1;
                    }
                }
            }
            for (Target t2 : o2)
            {
                for (Target t1 : o1)
                {
                    if (t1.id == t2.id)
                    {
                        return 1;
                    }
                }
            }
            if (o1.size() == o2.size())
            {
                return 0;
            }
            else if (o1.size() < o2.size())
            {
                return -1;
            }
            return 1;
        }
    };
    
    public static class Target
    {
        public Target()
        {            
        }
        
        public Target(String name)
        {
            this.name = name;
            init();
        }
        
        public void init()
        {
            if (properties == null)
            {
                properties = new HashMap<String,String>();
            }
        }
        
        protected String id;
        protected String name;
        protected String dir;
        protected String settingsFile;
        protected String goals;
        protected String profiles;
        protected String depends;
        protected Map<String,String> properties;
        protected String mavenOpts;
        
        public String toString()
        {
            return id;
        }
    }
    
    /**
     * @parameter 
     */
    protected String defaultTarget;
    
    /**
     * @parameter default-value="true"
     */
    protected boolean useSettings;
    
    /**
     * @parameter
     */
    protected String mavenOpts;
    
    /**
     * @parameter expression="${target}"
     */
    protected String target;
    
    /**
     * @parameter expression="${list}"
     */
    protected String list;
    
    /**
     * @parameter
     */
    protected Target[] targets;

    /**
     * Common set of properties to pass in on each project's command line, via -D parameters.
     *
     * @parameter
     */
    protected Map properties;

    /**
     * The predefined root project directory
     *
     * @parameter expression="${rootdir}"
     * @readonly
     */
    protected String rootdir;

    /**
     * @component
     */
    protected Invoker invoker;

    
    /**
     * The current user system settings for use in Maven.
     *
     * @parameter expression="${settings}"
     * @required
     * @readonly
     */
    protected Settings settings;    

    
    /** The Maven project.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;
    
    /* (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        // determine rootdir
        if (!StringUtils.isEmpty(rootdir))
        {
            File dir = new File(rootdir);
            if (!dir.exists() || !dir.isDirectory())
            {
                throw new MojoFailureException("Project property rootdir doesn't resolve to an existing directory");
            }
            else
            {
                rootdir = dir.getAbsolutePath();
            }
        }
        else
        {
            MavenProject prj = project;
            while (prj.hasParent() && prj.getParent().getBasedir() != null)
            {
                prj = prj.getParent();
            }
            rootdir = prj.getBasedir().getAbsolutePath();
        }
        
        HashMap<String,Target> targetsMap = new HashMap<String,Target>();
        
        if (targets != null)
        {           
            int i = 0;
            
            for (Target t : targets)
            {
                t.init();
                if (t.id == null)
                {
                    throw new MojoFailureException("Target element["+i+"] encountered without id");
                }
                if (t.name == null && t.depends == null && t.dir == null)
                {
                    throw new MojoFailureException("Target ["+t.id+"] requires at least a name, a dir or depends definition");
                }                
                if (targetsMap.containsKey(t.id))
                {
                    throw new MojoFailureException("Duplicate target id (or derived from its name): "+t.id + "("+targetsMap+")");                    
                }
                if (t.dir != null)
                {
                    t.dir = interpolateRootDir(t.dir);
                }
                if (t.settingsFile != null)
                {
                    t.settingsFile = interpolateRootDir(t.settingsFile);
                }
                // make sure target properties are valid
                if (t.properties != null)
                {
                    for (Iterator<String> iter = t.properties.keySet().iterator(); iter.hasNext(); )
                    {
                        if (t.properties.get(iter.next()) == null)
                        {
                            iter.remove();
                        }
                    }
                }
                targetsMap.put(t.id, t);
                i++;
            }
        }
        
        if (list != null)
        {
            ArrayList<List<Target>> lists = new ArrayList<List<Target>>();
            int maxLength = 0;
            for (Target t : targets)
            {
                if (t.id.length() > maxLength)
                {
                    maxLength = t.id.length();
                }
                lists.add(resolveTargets(targetsMap, null, t.id));
            }
            Collections.sort(lists, targetListComparator);
            
            Target t;
            StringBuffer buffer = new StringBuffer();
            
            System.out.println();
            System.out.println("Available jetspeed:mvn targets:");
            for (List<Target> targetsList : lists)
            {
                t = targetsList.get(targetsList.size()-1);
                buffer.setLength(0);                
                buffer.append("  "+t.id);
                for (int i = t.id.length(); i < maxLength; i++)
                {
                    buffer.append(" ");
                }
                buffer.append(" [");
                for (int i = 0, size = targetsList.size(); i < size; i++)
                {
                    buffer.append(targetsList.get(i).id);
                    if (i <size-1)
                    {
                        buffer.append(", ");
                    }
                }
                buffer.append("]");
                System.out.println(buffer.toString());
            }
            System.out.println();
            return;
        }
        
        String targetId = target;
        if (StringUtils.isEmpty(targetId) || targetId.equals("true"))
        {
            targetId = StringUtils.isEmpty(defaultTarget) ? null : defaultTarget;
        }
        if (targetId == null)
        {
            throw new MojoFailureException("Specify a target on the commandline using -Dtarget=<name> or define a defaultTarget in the plugin configuration.\n"
                                           +"Use parameter -Dlist to show the list of available targets.");
        }
        
        List<Target> targetsList = resolveTargets(targetsMap, null, targetId);
        getLog().info("Resolved target(s) order: "+targetsList);
        for (Target target : targetsList)
        {
            executeTarget(target);                
        }        
    }
    
    protected void executeTarget(Target target) throws MojoExecutionException, MojoFailureException
    {
        if (target.name == null && target.dir == null)
        {
            getLog().info("Executing target: "+target.id+" dependent on ["+target.depends+"] ONLY: all done");
            return;
        }
        else if (target.depends==null)
        {
            getLog().info("Executing target: "+target.id);
        }
        else
        {
            getLog().info("Executing target: "+target.id+" dependent on ["+target.depends+"]");
        }
        
        File targetDir = target.dir != null ? new File(target.dir) : project.getBasedir();
        
        if (!targetDir.exists() || !targetDir.isDirectory())
        {
            throw new MojoFailureException("Invalid or non-existing target directory "+targetDir.getAbsolutePath());
        }
        
        String targetPomName = target.name == null ? "pom.xml" : "jetspeed-mvn-"+target.name+"-pom.xml";
        
        File targetPom = new File(targetDir, targetPomName);
        if (!targetPom.exists() || !targetPom.isFile())
        {
            if (target.dir != null)
            {
                throw new MojoFailureException("Invalid or non-existing target pom "+targetPom.getAbsolutePath());
            }
            targetPom = getProjectFile(project, targetPomName);
        }
        if (targetPom == null)
        {
            throw new MojoFailureException("Target pom file "+targetPomName+" not found in current project directory or one of its parent projects");
        }
        
        StringBuffer targetCmdMessage = new StringBuffer();
        try
        {
            targetCmdMessage.append("Invoking target "+target.id+": "+targetPom.getCanonicalPath());
        }
        catch (IOException ioe)
        {
            getLog().error("Unexpected error: "+ioe.toString());
            throw new MojoExecutionException("Unexpected error: ",ioe);
        }
                
        Properties props = new Properties();
        if (properties != null)
        {
            props.putAll(properties);
        }
        props.putAll(target.properties);
        CompositeMap filter = new CompositeMap(project, props);
        addFileProperties(filter, getLocalOrProjectFile(targetPom.getParentFile(), project, "jetspeed-mvn.properties"));
        if (target.name != null)
        {
            addFileProperties(filter, getLocalOrProjectFile(targetPom.getParentFile(), project, "jetspeed-mvn-"+target.name+".properties"));
            if (!target.id.equals(target.name))
            {
                addFileProperties(filter, getLocalOrProjectFile(targetPom.getParentFile(), project, "jetspeed-mvn-"+target.name+"-"+target.id+".properties"));
            }
        }
        
        if (!props.containsKey("rootdir"))
        {
            props.put("rootdir", rootdir);
        }

        InvocationRequest request = new DefaultInvocationRequest();
        
        if (!StringUtils.isEmpty(target.goals))
        {
            List<String> goals = Arrays.asList(StringUtils.split(target.goals, ", "));
            targetCmdMessage.append(" "+StringUtils.join( goals.iterator(), " " ));
            request.setGoals(goals);
        }
        if (!StringUtils.isEmpty(target.profiles))
        {
            List<String> profiles = Arrays.asList(StringUtils.split(target.profiles, ", "));
            targetCmdMessage.append(" "+StringUtils.join( profiles.iterator(), " " ));
            request.setProfiles(profiles);
        }
        request.setProperties(props);
        request.setInteractive( false );
        request.setShowErrors(getLog().isErrorEnabled());
        request.setDebug(getLog().isDebugEnabled());
        request.setOffline(settings.isOffline());
        if (settings.getLocalRepository() != null)
        {
            request.setLocalRepositoryDirectory(new File(settings.getLocalRepository()));
        }
        String mavenOpts = props.getProperty("jetspeed.mvn.mavenOpts", null);
        if (StringUtils.isEmpty(mavenOpts) && !StringUtils.isEmpty(target.mavenOpts))
        {
            mavenOpts = target.mavenOpts;
        }
        if (StringUtils.isEmpty(mavenOpts) && !StringUtils.isEmpty(this.mavenOpts))
        {
            mavenOpts = this.mavenOpts;
        }
        if (!StringUtils.isEmpty(mavenOpts))
        {
            request.setMavenOpts(mavenOpts);
        }
                
        request.setBaseDirectory( targetPom.getParentFile() );
        
        if (target.name != null)
        {
            targetPom = buildInterpolatedFile( targetPom, targetPom.getParentFile(), targetPomName+".interpolated", filter);
        }
        request.setPomFile(targetPom);
        
        File settingsFile = null;
        String settingsFileName = (String)filter.get("jetspeed.mvn.settings.xml");
        if (settingsFileName == null)
        {
          settingsFileName = target.settingsFile;
        }
        if (settingsFileName != null )
        {
            settingsFileName = interpolateRootDir(settingsFileName);
            settingsFile = new File(settingsFileName);
            if (!settingsFile.exists() || !settingsFile.isFile())
            {
                settingsFile = null;
            }
        }
        if (settingsFile == null && useSettings)
        {
            settingsFile = getLocalOrProjectFile(targetPom.getParentFile(), project, "jetspeed-mvn-settings.xml");
        }
        
        if (settingsFile != null)
        {
            settingsFile = buildInterpolatedFile( settingsFile, settingsFile.getParentFile(), settingsFile.getName()+".interpolated", filter);
            request.setUserSettingsFile(settingsFile);
        }
        try
        {
            getLog().debug( "Executing: " + new MavenCommandLineBuilder().build( request ) );
        }
        catch (CommandLineConfigurationException e)
        {
            getLog().debug( "Failed to display command line: " + e.getMessage() );
        }
        InvocationResult result = null;

        try
        {
            getLog().info(targetCmdMessage.toString());
            if (invoker.getLogger() == null)
            {
                invoker.setLogger(new SystemOutLogger());
            }
            result = invoker.execute( request );
        }
        catch ( MavenInvocationException e )
        {
            getLog().debug( "Error invoking Maven: " + e.getMessage(), e );
            throw new MojoFailureException("...FAILED[error invoking Maven]");
        }

        CommandLineException executionException = result.getExecutionException();
        if ( executionException != null )
        {
            throw new MojoFailureException("...FAILED");
        }
        else if ( ( result.getExitCode() != 0 ) )
        {
            throw new MojoFailureException("...FAILED[code=" + result.getExitCode() + "].");
        }
        else
        {
            getLog().info( "...SUCCESS." );
        }
    }
    
    protected String interpolateRootDir(String str)
    {
        return StringUtils.replace(str, "@rootdir@", rootdir);
    }
    
    protected List<Target> resolveTargets(HashMap<String,Target> targetsMap, List<Target> resolving, String id) throws MojoFailureException
    {        
        List<Target> targets = new ArrayList<Target>();
        
        Target t = targetsMap.get(id);
        if (t == null)
        {
            if (resolving == null)
            {
                t = new Target(id);
                t.init();
            }
            else
            {
                throw new MojoFailureException("Target with id "+id+" undefined");
            }
        }
        if (resolving == null)
        {
            resolving = new ArrayList<Target>();            
        }
        if (resolving.contains(t))
        {
            throw new MojoFailureException("Circular reference encountered for target "+t.id);
        }
        if (t.depends != null)
        {
            resolving.add(t);
            for (String depend : StringUtils.split(t.depends, " ,"))
            {
                List<Target> depends = resolveTargets(targetsMap, resolving, depend);
                for (Target d : depends)
                {
                    if (!targets.contains(d))
                    {
                        targets.add(d);
                    }
                }
            }
            targets.add(t);
            resolving.remove(t);
        }
        else
        {
            targets.add(t);
        }
        return targets;
    }
    
    protected File buildInterpolatedFile( File originalFile, File targetDirectory, String targetFileName, CompositeMap filter )
    throws MojoExecutionException, MojoFailureException
    {
        File interpolatedFile = new File( targetDirectory, targetFileName );
        if ( interpolatedFile.exists() )
        {
            interpolatedFile.delete();
        }
        interpolatedFile.deleteOnExit();

        try
        {
            boolean created = interpolatedFile.createNewFile();
            if ( !created )
            {
                throw new MojoFailureException( "Failure creating file " + interpolatedFile.getPath() );
            }
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( "Failure creating file " + interpolatedFile.getPath() );
        }

        BufferedReader reader = null;
        Writer writer = null;
        try
        {
            // interpolation with token @...@
            reader = new BufferedReader(new InterpolationFilterReader(ReaderFactory.newXmlReader(originalFile), filter, "@", "@" ));
            writer = WriterFactory.newXmlWriter( interpolatedFile );
            String line = null;
            while ( ( line = reader.readLine() ) != null )
            {
                writer.write( line );
                writer.write("\n");
            }
            writer.flush();
        }
        catch ( IOException e )
        {
            String message = "Failure interpolating file: "+e.getMessage();
            getLog().error(message);
            throw new MojoExecutionException( message, e );
        }
        finally
        {
            // IOUtil in p-u is null check and silently NPE
            IOUtil.close( reader );
            IOUtil.close( writer );
        }

        if ( interpolatedFile == null )
        {
            // null check : normally impossible but :-)
            throw new MojoFailureException( "File is null after interpolation" );
        }
        return interpolatedFile;
    }

    protected void addFileProperties(CompositeMap filter, File propsFile) throws MojoExecutionException, MojoFailureException
    {
        if (propsFile != null)
        {
            FileInputStream fin = null;
            Properties props = new Properties();
            try
            {
                fin = new FileInputStream( propsFile );

                props.load( fin );
                RegexBasedInterpolator interpolator = new RegexBasedInterpolator();
                interpolator.addValueSource( new MapBasedValueSource( filter ) );
                for ( Iterator it = props.keySet().iterator(); it.hasNext(); )
                {
                    String key = (String) it.next();
                    String value = props.getProperty( key );
                    value = interpolator.interpolate( value, "" );
                    props.setProperty( key, value );
                }
                
                filter.getProperties().putAll(props);
            }
            catch (IOException ioe)
            {
                throw new MojoFailureException("Failed to read properties from "+propsFile.getAbsolutePath());
            }
            finally
            {
                IOUtil.close( fin );
            }            
        }
    }
    
    protected File getLocalOrProjectFile(File localDir, MavenProject project, String name)
    {
        File file = new File(localDir, name);
        if (!file.exists() || !file.isFile())
        {
            file = getProjectFile(project, name);
        }
        return file;
    }
    
    protected File getProjectFile(MavenProject project, String name)
    {
        File basedir = project.getBasedir();
        if (basedir == null)
        {
            return null;
        }
        File projectFile = new File(basedir, name);
        if (projectFile.exists() && projectFile.isFile())
        {
            // found;
        }
        else if (project.getParent() != null)
        {
            projectFile = getProjectFile(project.getParent(), name);
        }
        else
        {
            projectFile = null;
        }
        return projectFile;
    }
}
