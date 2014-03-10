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
package org.apache.jetspeed.locator;

/**
 * TemplateDescriptor
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface TemplateDescriptor extends LocatorDescriptor
{
    /**
     * The absolute file system path to the template
     *  
     * @return absolute path
     */    
    String getAbsolutePath();
    
    /**
     * The absolute file system path to the template
     *  
     * @param path the absolute path
     */    
    void setAbsolutePath(String path);
    
	/**
	 * Returns the template path relative to the applications root
	 * @return Application-relative path
	 */
	public String getAppRelativePath();
	/**
	 * Sets the template path relative to the applications root
	 * @param string Application-relative path
	 */
	public void setAppRelativePath(String string);
}
