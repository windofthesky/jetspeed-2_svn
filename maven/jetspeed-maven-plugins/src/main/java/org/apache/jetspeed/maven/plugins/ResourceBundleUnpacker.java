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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.repository.ArtifactRepositoryFactory;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.ProjectUtils;
import org.apache.maven.shared.downloader.DownloadException;
import org.apache.maven.shared.downloader.DownloadNotFoundException;
import org.apache.maven.shared.downloader.Downloader;
import org.codehaus.plexus.util.StringUtils;

/**
 * @version $Id$
 *
 */
public class ResourceBundleUnpacker
{
    public static File getRemoteResourceBundle(String artifactDescriptor, Downloader downloader, ArtifactRepository localRepository, List remoteRepositories, ArtifactRepositoryFactory artifactRepositoryFactory, MavenSession mavenSession) throws MojoExecutionException
    {
        // groupId:artifactId:version
        String[] s = StringUtils.split( artifactDescriptor, ":" );

        if ( s.length != 3 )
        {
            throw new MojoExecutionException( "The resource bundle configured must specify a groupId, artifactId, and" 
                + " version for a remote resource bundle. " 
                + "Must be of the form <resourceBundle>groupId:artifactId:version</resourceBundle>" );
        }

        try
        {
            return downloader.download( s[0], s[1], s[2], localRepository,
                                                 ProjectUtils.buildArtifactRepositories( remoteRepositories,
                                                     artifactRepositoryFactory,
                                                     mavenSession.getContainer() ) );

        }
        catch ( DownloadException e )
        {
            throw new MojoExecutionException( "Error downloading resources JAR.", e );
        }
        catch ( DownloadNotFoundException e )
        {
            throw new MojoExecutionException( "Resources JAR cannot be found.", e );
        }
        catch ( InvalidRepositoryException e )
        {
            throw new MojoExecutionException( "Resources JAR cannot be found.", e );
        }
    }
    
    public static void unpackResources(File resourceBundleFile, String targetBaseDirectory, Resources[] resources, Log log, boolean verbose) throws MojoExecutionException
    {
        File targetBaseDir = new File(targetBaseDirectory);
        if ( targetBaseDir.exists())
        {
            if (!targetBaseDir.isDirectory())
            throw new MojoExecutionException("Invalid targetBaseDirectory "+targetBaseDir.getAbsolutePath()+": not a directory");
        }
        else
        {
            targetBaseDir.mkdirs();
        }
        ZipInputStream zis = null;
        try
        {
            zis = new ZipInputStream( new FileInputStream( resourceBundleFile ) );
            ZipEntry ze = null;
            InputStream is = null;
            File firstDestFile;

            while ( ( ze = zis.getNextEntry() ) != null )
            {
                if (!ze.isDirectory())
                {
                    firstDestFile = null;
                    for ( int i = 0; i < resources.length; i++ )
                    {
                        String destFileName = resources[i].getDestFileName(ze.getName(), targetBaseDirectory);
                        if ( destFileName != null )
                        {
                            File destFile = new File(destFileName);
                            if ( destFile.exists() )
                            {
                                if (!destFile.isFile() )
                                {
                                    throw new MojoExecutionException("Destination "+destFile.getAbsolutePath()+" already exists and is not a file");
                                }
                                if ( destFile.lastModified() >= ze.getTime() || !resources[i].isOverwrite() )
                                {
                                    if (verbose)
                                    {
                                        log.info(ze.getName()+" skipped: already exists at "+destFile.getAbsolutePath());
                                    }
                                    else
                                    {
                                        log.debug(ze.getName()+" skipped: already exists at "+destFile.getAbsolutePath());
                                    }
                                    continue;
                                }
                            }
                            else
                            {
                                destFile.getParentFile().mkdirs();
                            }
                            byte[] buffer = new byte[1024];
                            int length = 0;
                            FileOutputStream fos = null;
                            try
                            {
                                if (firstDestFile == null)
                                {
                                    firstDestFile = destFile;
                                    is = zis;
                                }
                                else
                                {
                                    is = new FileInputStream(firstDestFile);
                                }
                                fos = new FileOutputStream( destFile );

                                while ( ( length =
                                    is.read( buffer ) ) >= 0 )
                                {
                                    fos.write( buffer, 0, length );
                                }

                                fos.close();
                                fos = null;
                            }
                            finally
                            {
                                if (is != zis)
                                {
                                    try
                                    {
                                        is.close();
                                    }
                                    catch (IOException e)
                                    {                                        
                                    }
                                }
                                
                                if ( fos != null )
                                {
                                    try
                                    {
                                        fos.close();
                                    }
                                    catch ( IOException e )
                                    {
                                    }
                                }
                            }
                            destFile.setLastModified(ze.getTime());
                            if (verbose)
                            {
                                log.info(ze.getName()+" extracted to "+destFile.getAbsolutePath());
                            }
                            else
                            {
                                log.debug(ze.getName()+" extracted to "+destFile.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }
        catch ( IOException ioe )
        {
            throw new MojoExecutionException("Error while expanding " + resourceBundleFile.getPath(), ioe);
        }
        finally
        {
            if ( zis != null )
            {
                try
                {
                    zis.close();
                }
                catch ( IOException e )
                {
                }
            }
        }
    }
}
