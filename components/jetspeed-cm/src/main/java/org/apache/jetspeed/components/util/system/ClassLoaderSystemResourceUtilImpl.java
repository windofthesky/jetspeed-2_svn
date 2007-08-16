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
package org.apache.jetspeed.components.util.system;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * ClassLoaderSystemResourceUtilImpl
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ClassLoaderSystemResourceUtilImpl implements SystemResourceUtil
{
	
	private ClassLoader cl;
	
	/**
	 * 
	 * @param cl ClassLoader that will be used to locate a resource
	 */
	public ClassLoaderSystemResourceUtilImpl(ClassLoader cl)
	{
		this.cl = cl;
	}


    /**
     * For this implementation, always returns "/"
     */
    public String getSystemRoot()
    {        
        return "/";
    }

    /**
     * @see org.apache.jetspeed.components.util.system.SystemResourceUtil#getURL(java.lang.String)
     */
    public URL getURL(String relativePath) throws MalformedURLException
    {        
        return cl.getResource(convertFSSeperatorToSlash(relativePath));
    }
    
    private String convertFSSeperatorToSlash(String path)
    {
    	return path.replace(File.separatorChar, '/');
    }

}
