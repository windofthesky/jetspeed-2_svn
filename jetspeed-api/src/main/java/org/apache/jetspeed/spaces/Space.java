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

import java.util.List;
import java.util.Locale;

/**
 * Space object represents a single space in the Jetspeed site or somewhere else
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface Space
{
    public static final String META_SPACE_OWNER = "space-owner";
    public static final String META_SPACE_DOMAIN_PATH = "space-domain-path";
    public static final String META_SPACE_IMAGE = "space-image";
	
	/**
	 * Retrieve the name of the space
	 * 
	 * @return the name of the space
	 */	
    String getName();

    /**
     * Retrieve the localized title of the space for the requested locale
     * 
     * @return the localized title
     */
    String getTitle(Locale locale);

    /**
     * Retrieve the unlocalized title of the space 
     * 
     * @return the unlocalized title
     */
    String getTitle();
    
    /**
     * Retrieve the localized short title of the space for the requested locale
     * 
     * @return the localized title
     */
    String getShortTitle(Locale locale);

    /**
     * Retrieve the unlocalized short title of the space 
     * 
     * @return the unlocalized title
     */
    String getShortTitle();
    
    /**
     * Set the title of space for the given locale
     * 
     * @param locale
     * @param title
     */
    void setTitle(String title, Locale locale);

    /**
     * Set the title of space 
     * 
     * @param title
     */
    void setTitle(String title);
    
    /**
     * Set the short title of space for the given locale
     * 
     * @param locale
     * @param title
     */
    void setShortTitle(String shortTitle, Locale locale);

    /**
     * Set the short title of space 
     * 
     * @param title
     */
    void setShortTitle(String shortTitle);
    
	/**
	 * Retrieve the path of the space, which can be implementation specific
	 * 
	 * @return the path to where the space is stored
	 */    
    String getPath();
        
    /**
     * Retrieve the description of this space
     * 
     * @param locale the locale of the string
     * @return
     */
    String getDescription(Locale locale);
    
    /**
     * Retrieve the description of this space
     * 
     * @return
     */
    String getDescription();
    
    /**
     * Sets the description on this space
     *  
     * @param description
     * @param locale the locale of the string
     */
    void setDescription(String description, Locale locale);
    
    /**
     * Sets the description on this space
     *  
     * @param description
     */
    void setDescription(String description);
    
    /**
     * Retrieves the owner, a portal user name, for this space
     * 
     * @return the name of the space owner
     */
    String getOwner();
    
    /**
     * Sets the owner on this environment
     *  
     * @param owner
     */
    void setOwner(String owner);
    
    /**
     * Retrieves the image associated with this space
     * 
     * @return a portal context relative path to the image
     */
    String getImage();
    
    /**
     * Sets the portal context relative path to the image associated with this space
     * 
     * @param pathToImage the portal context relative path to the image
     */
    void setImage(String pathToImage);
       
    /**
     * Retrieves the default dashboard for this space. There can only be one dashboard per space
     * 
     * @return a dashboard name, the page which represents the dashboard 
     */
    String getDashboard();    
    
    /**
     * Sets the dashboard for this space. Dashboards are really just pages
     * 
     * @param page
     */
    void setDashboard(String dashboard);

    /**
     * Retrieves the domain path associated with this space 
     * For example, employees.localhost.com, or employees.* where * is the SERVER_NAME of this request
     *  
     * @return the domain path string
     */
    String getDomainPath();
    
    /**
     * Sets the domain path associated with this space
     * For example, employees.localhost.com, or employees.* where * is the SERVER_NAME of this request
     * 
     * @param domain the domain path string
     */
    void setDomainPath(String domain);
    
    /**
     * Retrieves the theme name for this space
     * 
     * @return the theme name
     */
    String getTheme();
    
    /**
     * Sets the theme name to be associated with this space
     * All pages in this space will inherit the theme (unless the theme is overriden on the specific page or folder)
     * 
     * @param themeName the name of the theme
     */
    void setTheme(String themeName);

    /**
     * Retrieve the list of secured roles constraining access to this space
     * 
     * @return the list of role name strings 
     */
    List<String> getSecuredRoles();
    
    /**
     * Add a role to the list of required roles to view this space
     * @param role
     */
    void addSecuredRole(String role);
    
    /**
     * Remove a role from the list of required roles to view this space
     * 
     * @param role
     */
    void removeSecuredRole(String role);

    /**
     * Retrieve the list of secured groups constraining access to this space
     * 
     * @return the list of group name strings 
     */    
    List<String> getSecuredGroup();

    /**
     * Add a group to the list of required group to view this space
     * @param group
     */    
    void addSecuredGroup(String group);
    
    /**
     * Remove a group from the list of required roles to view this space
     * 
     * @param group
     */    
    void removeSecuredGroup(String group);
    
    /**
     * Retrieve the list of secured users constraining access to this space
     * 
     * @return the list of users name strings 
     */    
    List<String> getSecuredUsers();
    
    /**
     * Add a username to the list of users who can view this page
     * @param user the name of user
     */    
    void addSecuredUser(String user);
    
    /**
     * Remove a user from the list of required users to view this space
     * 
     * @param user the name of user
     */    
    void removeSecuredUser(String user);
    
    /**
     * Get a list of page templates configured for this space
     *  
     * @return a list of one or more page templates
     */
//    List<String> getTemplates();
    
    /**
     * Add a template to the list of templates for this space
     * 
     * @param template
     */
//    void addTemplate(String template);
    
    /**
     * Remove a template from the list of templates for this space
     * 
     * @param template
     */
//    void removeTemplate(String template);    

    /**
     * Get the security constraint on this space
     */
    String getSecurityConstraint();

    /**
     * Set the security constraint on this space
     */
    void setSecurityConstraint(String constraint);
}
