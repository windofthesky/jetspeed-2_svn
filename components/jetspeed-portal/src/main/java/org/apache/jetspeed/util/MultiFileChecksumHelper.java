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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

/**
 * Perform a single checksum calculation for multiple files
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public class MultiFileChecksumHelper
{
    public static long getChecksum(File[] files)
    {
        CheckedInputStream cis = null;
        FileInputStream is = null;
        Checksum checksum = new Adler32();
        byte[] tempBuf = new byte[128];
        
        for ( int i = 0; i < files.length && files[i] != null && files[i].exists() && files[i].isFile(); i++ )
        {
            try 
            {
                is = new FileInputStream(files[i]);
                cis = new CheckedInputStream(is, checksum);
                while (cis.read(tempBuf) >= 0) {}                
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
                if (cis != null)
                {
                    try
                    {
                        cis.close();
                    }
                    catch (IOException ioe) {}
                    cis = null;
                }
                if (is != null)
                {
                    try
                    {
                        is.close();
                    }
                    catch (IOException ioe) {}
                    is = null;
                }
            }
        }
        return checksum.getValue();
    }
}
