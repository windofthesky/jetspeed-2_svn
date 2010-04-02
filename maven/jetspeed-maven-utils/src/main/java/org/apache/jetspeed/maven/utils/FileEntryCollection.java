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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * FileEntryCollection
 * 
 * @version $Id$
 */
public class FileEntryCollection
{
    private ZipFile zipFile;
    private File folder;
    
    public FileEntryCollection(ZipFile zipFile)
    {
        this.zipFile = zipFile;
    }
    
    public FileEntryCollection(File folder)
    {
        if (!folder.isDirectory())
        {
            throw new IllegalArgumentException("Folder not found: " + folder);
        }
        
        this.folder = folder;
    }
    
    public String getName()
    {
        if (zipFile != null)
        {
            return zipFile.getName();
        }
        
        return folder.getPath();
    }
    
    public Enumeration<? extends FileEntry> entries()
    {
        if (zipFile != null)
        {
            return new ZipFileEntryEnumeration(zipFile.entries());
        }
        
        return new FolderFileEntryEnumeration(folder);
    }
    
    public InputStream getInputStream(FileEntry entry) throws IOException
    {
        if (zipFile != null)
        {
            return zipFile.getInputStream((ZipEntry) entry.getEntryObject());
        }
        
        return new FileInputStream((File) entry.getEntryObject());
    }
    
    private static class ZipFileEntryEnumeration implements Enumeration<FileEntry>
    {
        private Enumeration<? extends ZipEntry> zipEntries;
        
        public ZipFileEntryEnumeration(Enumeration<? extends ZipEntry> zipEntries)
        {
            this.zipEntries = zipEntries;
        }

        public boolean hasMoreElements()
        {
            return zipEntries.hasMoreElements();
        }

        public FileEntry nextElement()
        {
            return new FileEntry(zipEntries.nextElement());
        }
    }
    
    private static class FolderFileEntryEnumeration implements Enumeration<FileEntry>
    {
        private File folder;
        private List<String> entryNames;
        private Iterator<String> entryNameIterator;
        
        public FolderFileEntryEnumeration(File folder)
        {
            this.folder = folder;
            entryNames = new LinkedList<String>();
            fillEntryNames(folder.getPath(), folder, entryNames);
            entryNameIterator = entryNames.iterator();
        }

        public boolean hasMoreElements()
        {
            return entryNameIterator.hasNext();
        }

        public FileEntry nextElement()
        {
            String entryName = entryNameIterator.next();
            return new FileEntry(new File(folder, entryName), entryName);
        }
        
        private void fillEntryNames(String basePath, File folder, List<String> entryNames)
        {
            File [] children = folder.listFiles();
            
            if (children != null)
            {
                for (File child : children)
                {
                    String name = child.getPath();
                    
                    if (!name.startsWith(basePath))
                    {
                        throw new IllegalStateException("Child file path does not starts with base path. (" + name + ", " + basePath + ")");
                    }
                    
                    name = name.substring(basePath.length());
                    
                    if (!"/".equals(File.separator))
                    {
                        name = name.replace(File.separator, "/");
                    }
                    
                    if (name.startsWith("/"))
                    {
                        name = name.substring(1);
                    }
                    
                    entryNames.add(name);
                    
                    if (child.isDirectory())
                    {
                        fillEntryNames(basePath, child, entryNames);
                    }
                }
            }
        }
    }
}
