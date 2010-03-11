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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.ListIterator;

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
     */
    private String srcDirectoryPath;

    /**
     * The destination directory on which event need to happen
     * 
     * @parameter expression="${destDirectoryPath}"
     */
    private String destDirectoryPath;

    /**
     * The target file
     * 
     * @parameter expression="${srcFilePath}"
     */
    private String srcFilePath;

    /**
     * The edit pattern, (accepts url encoded string)
     * 
     * @parameter expression="${editPattern}"
     */
    private String editPattern;

    /**
     * The edit replace pattern, (accepts url encoded string)
     * 
     * @parameter expression="${replacePattern}"
     */
    private String replacePattern;

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
            if (srcDirectoryPath != null)
            {
                copy(srcDirectoryPath, destDirectoryPath);
            }
            else if (srcFilePath != null)
            {
                copyFile(srcFilePath, destDirectoryPath);                
            }
        }
        if (event.equals("move"))
        {
            if (srcDirectoryPath != null)
            {
                move(srcDirectoryPath, destDirectoryPath);
            }
            else if (srcFilePath != null)
            {
                moveFile(srcFilePath, destDirectoryPath);                
            }
        }
        else if (event.equals("delete"))
        {
            if (srcDirectoryPath != null)
            {
                delete(srcDirectoryPath);
            }
            else if (srcFilePath != null)
            {
                deleteFile(srcFilePath);
            }
        }
        else if (event.equals("edit"))
        {
            if ((srcFilePath != null) && (editPattern != null))
            {
                try
                {
                    editFile(srcFilePath, URLDecoder.decode(editPattern, "UTF-8"), URLDecoder.decode(((replacePattern != null) ? replacePattern : ""), "UTF-8"));
                }
                catch (UnsupportedEncodingException uee)
                {
                }
            }
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
            throw new MojoExecutionException("Error in deleting directory", IOex);
        }
    }

    private static void deleteFile(String srcFilePath) throws MojoExecutionException
    {
        FileUtils.deleteQuietly(new File(srcFilePath));
    }

    private static void copy(String srcDirectoryPath, String destDir) throws MojoExecutionException
    {
        try
        {
            FileUtils.copyDirectory(new File(srcDirectoryPath), new File(destDir), FileFilterUtils.makeSVNAware(null));
        }
        catch (IOException IOex)
        {
            throw new MojoExecutionException("Error in copying directory", IOex);
        }
    }

    private static void copyFile(String srcFilePath, String destDir) throws MojoExecutionException
    {
        try
        {
            FileUtils.copyFileToDirectory(new File(srcFilePath), new File(destDir));
        }
        catch (IOException IOex)
        {
            throw new MojoExecutionException("Error in copying file to directory", IOex);
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
            throw new MojoExecutionException("Error in moving directory", IOex);
        }
    }

    private static void moveFile(String srcFilePath, String destDir) throws MojoExecutionException
    {
        try
        {
            FileUtils.moveFileToDirectory(new File(srcFilePath), new File(destDir), true);
        }
        catch (IOException IOex)
        {
            throw new MojoExecutionException("Error in moving file to directory", IOex);
        }
    }

    private static void editFile(String srcFilePath, String editPattern, String replacePattern) throws MojoExecutionException
    {
        try
        {
            File srcFile = new File(srcFilePath);
            List lines = FileUtils.readLines(srcFile);
            for (ListIterator i = lines.listIterator(); i.hasNext();)
            {
                i.set(((String)i.next()).replaceAll(editPattern, replacePattern));
            }
            FileUtils.writeLines(srcFile, lines);
        }
        catch (IOException IOex)
        {
            throw new MojoExecutionException("Error in editing file", IOex);
        }
    }
}
