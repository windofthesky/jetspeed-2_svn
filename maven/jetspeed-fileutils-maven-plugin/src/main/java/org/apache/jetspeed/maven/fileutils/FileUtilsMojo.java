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


import org.apache.jetspeed.maven.utils.FileSystemUtils;
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
            FileSystemUtils.copy(srcDirectoryPath, destDirectoryPath);
        }
        if (event.equals("move"))
        {
            FileSystemUtils.copy(srcDirectoryPath, destDirectoryPath);
            FileSystemUtils.delete(srcDirectoryPath);
        }
        else if (event.equals("delete"))
        {
            FileSystemUtils.delete(srcDirectoryPath);
        }
    }
}
