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
package org.apache.jetspeed.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface FileSystemHelper
{
    /**
     * 
     * <p>
     * copyFrom
     * </p>
     *
     * @param directory Directory to copy content from
     * @throws {@link java.io.IOException} if the <code>directory.isDirectory</code>
     * returns <code>false</code>
     */
    void copyFrom(File directory) throws IOException;
    
    /**
     * 
     * <p>
     * copyFrom
     * </p>
     *
     * @param directory
     * @param fileFilter
     * @throws IOException
     */
    void copyFrom(File directory, FileFilter fileFilter) throws IOException;
    
    /**
     * 
     * <p>
     * remove
     * </p>
     * Removes the underlying directory structure from the root directory down.
     * 
     * @return <code>true</code> if the removal war successful, otherwise returns
     * <code>false</code>.
     */
    boolean remove();
    
    /**
     * 
     * <p>
     * getRootDirectory
     * </p>
     *
     * @return the root of the directory structure
     */
    File getRootDirectory();    
    
    /**
     * 
     * <p>
     * close
     * </p>
     *
     * Cleans up resources opened up specifically by this FileSystemHelper 
     *
     */
    void close() throws IOException;
    
    /**
     * 
     * <p>
     * getSourcePath
     * </p>
     * 
     * Returns the true location of this FileSystemHelper backing object on
     * the file system.  This IS NOT always as the path of the object returned
     * from the {@link FileSystemHelper#getRootDirectory} method.
     *
     * @return the true location of this FileSystemHelper backing object. 
     */
    String getSourcePath();
    
    /**
     * Given a path to a resource in this file system, return a checksum 
     * on that resource's content.
     * 
     * @param pathToResource
     * @return checksum of the content of the resource
     */
    long getChecksum(String pathToResource);
}
