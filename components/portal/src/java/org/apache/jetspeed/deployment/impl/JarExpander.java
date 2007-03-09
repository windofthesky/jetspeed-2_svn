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
package org.apache.jetspeed.deployment.impl;

import org.apache.jetspeed.util.DirectoryHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * JarExpander
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id$
 */
public class JarExpander
{
    public static void expand(File srcFile, File targetDir) throws IOException
    {
        if (targetDir.exists())
        {
            DirectoryHelper cleanup = new DirectoryHelper(targetDir);
            cleanup.remove();
            cleanup.close();
        }

        targetDir.mkdirs();
        JarFile jarFile = new JarFile(srcFile);
        
        try
        {
            Enumeration entries = jarFile.entries();

            InputStream is = null;
            OutputStream os = null;

            byte[] buf = new byte[1024];
            int len;

            while (entries.hasMoreElements())
            {
                JarEntry jarEntry = (JarEntry) entries.nextElement();
                String name = jarEntry.getName();
                File entryFile = new File(targetDir, name);

                if (jarEntry.isDirectory())
                {
                    entryFile.mkdir();
                }
                else
                {
                    if (!entryFile.getParentFile().exists())
                    {
                        entryFile.getParentFile().mkdirs();
                    }

                    entryFile.createNewFile();

                    try
                    {
                        is = jarFile.getInputStream(jarEntry);
                        os = new FileOutputStream(entryFile);

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
        }
        finally
        {
            if (jarFile != null)
            {
                jarFile.close();
            }
        }
    }
}