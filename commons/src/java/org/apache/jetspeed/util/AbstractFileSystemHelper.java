/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;


/**
 * implements common directory and jar operations
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id$
 */
public abstract class AbstractFileSystemHelper
{
    public abstract File getRootDirectory();
    
    public long getChecksum(String path)
    {
        File child = new File(getRootDirectory(), path);
        if (child == null || !child.exists())
        {
            return 0;
        }
        
        CheckedInputStream cis = null;        
        long checksum = 0;
        try 
        {
            cis = new CheckedInputStream(new FileInputStream(child), new Adler32());
            byte[] tempBuf = new byte[128];
            while (cis.read(tempBuf) >= 0) 
            {
            }
            checksum = cis.getChecksum().getValue();
        } 
        catch (IOException e) 
        {
            checksum = 0;
        }
        finally
        {
            if (cis != null)
            {
                try
                {
                    cis.close();
                }
                catch (IOException ioe)
                {                    
                }
            }
        }
        return checksum;
    }

}
