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

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/*
 * File System Directory Utilities. Some utilities that java.io doesn't give us.
 *
 *    rmdir() - removes a directory and all subdirectories and files underneath.
 *
 * @author David S. Taylor <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 *
 */
public class DirectoryUtils
{
    private final static Log log = LogFactory.getLog(DirectoryUtils.class);
            
    public static void main(String[] args)
    {
        DirectoryUtils.rmdir(new File(args[0]));
    }

    /**
     *  Removes a directory and all subdirectories and files beneath it.
     *
     * @param directory The name of the root directory to be deleted.
     * @return boolean If all went successful, returns true, otherwise false.
     * 
     */
    public static final boolean rmdir(File dir)
    {    
		if (dir.isDirectory())
		{
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++)
			{
				boolean success = rmdir(new File(dir, children[i]));
				if (!success)
				{
					return false;
				}
			}
		}

		// The directory is now empty so delete it OR it is a plain file
		return dir.delete();
    }

    /**
     *  Recursive deletion engine, traverses through all subdirectories, 
     *  attempting to delete every file and directory it comes across.
     *  NOTE: this version doesn't do any security checks, nor does it 
     *  check for file modes and attempt to change them.
     *
     * @param path The directory path to be traversed.
     * 
     */            
//    private static void deleteTraversal(String path)
//    {
//        File file = new File(path);
//        if (file.isFile()) 
//        {
//            try 
//            {
//                file.delete();
//            }
//            catch (Exception e)
//            {
//                log.error("Failed to Delete file: " + path + " : " , e);
//                file.deleteOnExit(); // try to get it later...
//            }
//        } 
//        else if (file.isDirectory()) 
//        {
//            if (!path.endsWith(File.separator))
//                path += File.separator;
//
//            String list[] = file.list();
//
//            // Process all files recursivly
//            for(int ix = 0; list != null && ix < list.length; ix++)
//                deleteTraversal(path + list[ix]);
//
//            // now try to delete the directory
//            try 
//            {
//                file.delete();
//            }
//            catch (Exception e)
//            {
//                log.error("Failed to Delete directory: " + path + " : " , e);
//                file.deleteOnExit(); // try to get it later...
//            }
//            
//        }
//    }            
}


