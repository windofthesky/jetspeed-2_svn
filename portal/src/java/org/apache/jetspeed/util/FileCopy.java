/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.util;

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