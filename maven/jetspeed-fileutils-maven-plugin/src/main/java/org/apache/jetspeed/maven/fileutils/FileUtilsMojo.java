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
package org.apache.jetspeed.maven.fileutils;


import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @author vkumar <a href="vkumar@apache.org">Vivek Kumar</a>
 * @goal event
 */
public class FileUtilsMojo extends AbstractMojo
{
    /**
     * The Source directory on which event need to happen
     * 
     * @parameter expression="${srcDirectoryPath}"
     * @required
     */
    private String srcDirectoryPath;
    /**
     * The destination directory on which event need to happen
     * 
     * @parameter expression="${destDirectoryPath}"
     */
    private String destDirectoryPath;
    /**
     * The Event need to occur
     * 
     * @parameter expression="${event}"
     * @required
     */
    private String event;
    
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (event.equals("copy"))
        {
            copy(srcDirectoryPath, destDirectoryPath);
        }
        if (event.equals("move"))
        {
            move(srcDirectoryPath, destDirectoryPath);
        }
        else if (event.equals("delete"))
        {
            delete(srcDirectoryPath);
        }
    }
    
    private static void delete(String srcDirectoryPath) throws MojoExecutionException
    {
        try
        {
            FileUtils.deleteDirectory(new File(srcDirectoryPath));
        }
        catch (IOException IOex)
        {
            throw new MojoExecutionException("Error in deleting the directory", IOex);
        }
    }

    private static void copy(String srcDirectoryPath, String destDir) throws MojoExecutionException
    {
        try
        {
            FileUtils.copyDirectory(new File(srcDirectoryPath), new File(destDir), FileFilterUtils.makeSVNAware(null));
        }
        catch (IOException IOex)
        {
            throw new MojoExecutionException("Error in copying the directory", IOex);
        }
    }

    private static void move(String srcDirectoryPath, String destDir) throws MojoExecutionException
    {
        try
        {
            FileUtils.moveDirectory(new File(srcDirectoryPath), new File(destDir));
        }
        catch (IOException IOex)
        {
            throw new MojoExecutionException("Error in moving the directory", IOex);
        }
    }
}
