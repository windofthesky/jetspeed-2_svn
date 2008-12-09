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
package org.apache.jetspeed.maven.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @author vkumar <a href="vkumar@apache.org">Vivek Kumar</a>
 */
public class FileSystemUtils
{
    public static void delete(String srcDirectoryPath) throws MojoExecutionException
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

    public static void copy(String srcDirectoryPath, String destDir) throws MojoExecutionException
    {
        try
        {
            FileUtils.copyDirectory(new File(srcDirectoryPath), new File(destDir));
        }
        catch (IOException IOex)
        {
            throw new MojoExecutionException("Error in coping the directory", IOex);
        }
    }
}
