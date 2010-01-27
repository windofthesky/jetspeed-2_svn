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
package org.apache.jetspeed.spaces;

import java.util.Locale;


/**
 * Container for spaces
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface Environment
{    
    public static final String META_ENV_OWNER   = "env-owner";
	
	/**
	 * Retrieve the name of the environment
	 * 
	 * @return the name of the environment
	 */
    String getName();
    
    /**
     * Retrieve the localized title of the environment for the requested locale
     * 
     * @param locale the locale of the string
     * @return the localized title
     */
    String getTitle(Locale locale);

    /**
     * Retrieve unlocalized title of the environment 
     * 
     * @return the unlocalized title
     */
    String getTitle();
    
    /**
     * Set the title of environment for the given locale
     * 
     * @param locale the locale of the string
     * @param title
     */
    void setTitle(String title, Locale locale);

    /**
     * Set the title of environment 
     * 
     * @param title
     */
    void setTitle(String title);
    
	/**
	 * Retrieve the path of the environment, which can be implementation specific
	 * 
	 * @return the path to where the environment is stored
	 */    
    String getPath();
        
    /**
     * Retrieve the description of this environment
     * @param locale the locale of the string
     * @return
     */
    String getDescription(Locale locale);
    
    /**
     * Sets the description on this environment
     *  
     * @param description
     * @param locale the locale of the string
     */
    void setDescription(String description, Locale locale);

    /**
     * Retrieve the description of this environment
     * @return
     */
    String getDescription();
    
    /**
     * Sets the description on this environment
     *  
     * @param description
     */
    void setDescription(String description);
    
    /**
     * Retrieves the owner, a portal user name, for this environment
     * 
     * @return the name of the environment owner
     */
    String getOwner();

    /**
     * Sets the owner on this environment
     *  
     * @param owner
     */
    void setOwner(String owner);
        
}
