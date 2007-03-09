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
package org.apache.jetspeed.cache.file;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/*
 * File Copy Utilities. Some utilities that java.io doesn't give us.
 *
 *    copy() - copies one file source to another file destination.
 *    copyFromURL)() - copies from a URL source to a file destination.
 *
 *   NOTE: tried to be a good Commons-citizen and use io out of the sandbox
 *         at the time it was dependent on an older version of commons-lang for a predicate class bs bs
 *
 *  @author David S. Taylor <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */

public class FileCopy 
{
    public static final int BUFFER_SIZE = 4096;

    /*
     *  Copies one file source to another file destination. 
     *
     * @param source The source file.
     * @param destination The destination file.
     * @throws IOException When an IO error occurs, this exception is thrown.
     */
    public static final void copy(String source, String destination)
                throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];    
        BufferedInputStream input;
        BufferedOutputStream output;

        input = new BufferedInputStream(new FileInputStream(source));
        output = new BufferedOutputStream(new FileOutputStream(destination));

        copyStream(input, output, buffer);

        input.close();
        output.close();
    }

    /*
     *  Copies from a URL source to a file destination.
     *
     * @param source The source URL.
     * @param destination The destination file.
     * @throws IOException When an IO error occurs, this exception is thrown.
     */
    public static final void copyFromURL(String source, String destination)
              throws IOException
    {
        byte[] buffer = new byte[BUFFER_SIZE];    
        URL url = new URL(source);
          BufferedInputStream input;
          BufferedOutputStream output;
        
        
        input = new BufferedInputStream(new DataInputStream(url.openStream()));
        output = new BufferedOutputStream(new FileOutputStream(destination));
        
        copyStream(input, output, buffer);
        
        input.close();
        output.close();
    }

    /*
     *  Generic copy from a input stream to an output stream.
     *
     * @param input The source input stream.
     * @param output The destination output stream.
     * @param buffer The user provided buffer.
     * @throws IOException When an IO error occurs, this exception is thrown.
     */
    public static final void copyStream(InputStream input,
                                        OutputStream output,
                                        byte[] buffer)
                throws IOException
    {
        int bytesRead;

        while((bytesRead = input.read(buffer)) != -1)
            output.write(buffer, 0, bytesRead);
    }

}