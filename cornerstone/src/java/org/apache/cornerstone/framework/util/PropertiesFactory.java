/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cornerstone.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesFactory
{
    public static final String REVISION = "$Revision$";

    public static final String FILE_EXTENSION_PROPERTIES = ".properties";

    public static Properties collectPropertiesInDir(String dirPath)
    {
        Properties properties = new Properties();

        File dir = new File(dirPath);
        if (dir.exists())
        {
            File[] fileArray = dir.listFiles();

            for (int i = 0; i < fileArray.length; i++)
            {
                File currentFile = fileArray[i];
                String currentFilePath = currentFile.getAbsolutePath();
                if (currentFile.isFile() == true && currentFilePath.endsWith(FILE_EXTENSION_PROPERTIES));
                {
                    try
                    {
                        FileInputStream fis = new FileInputStream(currentFile);
                        properties.load(fis);
                    }
                    catch (Exception e)
                    {
                        continue;
                    }
                }
            }
        }

        return properties;
    }
}