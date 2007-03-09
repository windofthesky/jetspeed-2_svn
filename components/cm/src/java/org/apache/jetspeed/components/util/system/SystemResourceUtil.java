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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>
 * SystemResourceUtil
 * </p>
 * <p>
 *   This is a simple component that allows location of system resources
 *   based on implementation.  Sources could be anyone or combination of:
 *   the file system, classloaders, VFS source (see the Virtual File System
 *   project: http://jakarta.apache.org/commons/sandbox/vfs/)
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface SystemResourceUtil
{
	/**
	 * 
	 * @return The root from were this system is running
	 */
	String getSystemRoot();
	
	/**
	 * Creates a fully qualified path to the <code>relativePath</code>
	 * as a {@link java.net.URL}
	 * @param relativePath
	 * @return
	 */
	URL getURL(String relativePath) throws MalformedURLException;

}
