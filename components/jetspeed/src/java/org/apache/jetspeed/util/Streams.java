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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.io.InputStreamReader;

/**
 * Utility functions related to Streams.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class Streams
{
  static final int BLOCK_SIZE=4096;

  public static void drain(InputStream r,OutputStream w) throws IOException
  {
      byte[] bytes=new byte[BLOCK_SIZE];
      try
      {
        int length=r.read(bytes);
        while(length!=-1)
        {
            if(length!=0)
                {
                    w.write(bytes,0,length);
                }
            length=r.read(bytes);
        }
    }
    finally
    {
      bytes=null;
    }

  }

  public static void drain(Reader r,Writer w) throws IOException
  {
    char[] bytes=new char[BLOCK_SIZE];
    try
    {
        int length=r.read(bytes);
        while(length!=-1)
        {
            if(length!=0)
            {
                w.write(bytes,0,length);
            }
            length=r.read(bytes);
        }
    }
    finally
    {
        bytes=null;
    }

  }

  public static void drain(Reader r,OutputStream os) throws IOException
  {
        Writer w=new OutputStreamWriter(os);
        drain(r,w);
        w.flush();
  }

  public static void drain(InputStream is, Writer w) throws IOException
  {
      Reader r = new InputStreamReader(is);
      drain(r,w);
      w.flush();
  }

  public static byte[] drain(InputStream r) throws IOException
  {
        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
        drain(r,bytes);
        return bytes.toByteArray();
  }

  public static String getAsString(InputStream is)
  {
      int c=0;
      char lineBuffer[]=new char[128], buf[]=lineBuffer;
      int room= buf.length, offset=0;
      try
      {
          loop: while (true)
          {
            // read chars into a buffer which grows as needed
                switch (c = is.read() )
                {
                    case -1: break loop;

                    default: if (--room < 0)
                             {
                                 buf = new char[offset + 128];
                                 room = buf.length - offset - 1;
                                 System.arraycopy(lineBuffer, 0,
                                          buf, 0, offset);
                                 lineBuffer = buf;
                             }
                             buf[offset++] = (char) c;
                             break;
                }
          }
      }
      catch(IOException ioe)
      {
          ioe.printStackTrace();
      }
      if ((c == -1) && (offset == 0))
      {
          return null;
      }
      return String.copyValueOf(buf, 0, offset);
  }



}
