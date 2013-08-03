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
package org.apache.jetspeed.profiler;

import org.apache.jetspeed.profiler.rules.RuleCriterion;

import java.util.Iterator;

/**
 * <p>Profile Locators are used to locate profiled portal resources such as
 * pages, documents, and fragments. A locator contains properties describing
 * the actually resource to be located. Since the locator is based on properties
 * that are usually related to a user or other subject's profile, it is referred
 * to as a profile locator.</p>
 * 
 * <p>Profiles can be created from a normalized <i>Profile Locator Path</i>
 * The format of the path is name/value pairs of all property, separated by a <i>path separator</i>.
 * An example locator path:</p>
 * 
 *      <pre>page:default.psml:artist:al-stewart:song:on-the-border</pre>
 *      <pre>path:/sports/football/nfl/chiefs:language:en</pre>
 * 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface ProfileLocator 
{
    public final static String PAGE_LOCATOR = "page";
    public final static String SECURITY_LOCATOR = "security";

    public final static String PATH_SEPARATOR = ":";
    
    /**
     * Initialize this page context.
     *
     * @param profiler The profiler initializing this locator.
     * @param requestPath The request path used to create this locator.
     */
    void init(Profiler profiler, String requestPath);

    /**
     * Initialize this page context.
     *
     * @param profiler The profiler initializing this locator.
     * @param requestPath The request path used to create this locator.
     * @param requestServerName The request server name used to create this locator.
     */
    void init(Profiler profiler, String requestPath, String requestServerName);

    /**
     * Get an iterator over the locator's properties.
     * Elements are returned as @link ProfileLocatorProperty array. 
     *  
     * @return an iterator over the profile locator properties
     */
    Iterator<ProfileLocatorProperty []> iterator();
        
    /**
     * Add a property based on a @link org.apache.jetspeed.profiler.rules.RuleCriterion
     * and a value. Rule criteria are templates for locating profile properties.
     * The value is combined with the rule to create a property.
     * 
     * @param criterion The rule criterion on which this property is based.
     * @param isControl The control classification for property.
     * @param isNavigation The navigation classification for property.
     * @param value The value to set on the property.
     */        
    void add(RuleCriterion criterion, boolean isControl, boolean isNavigation, String value);

    /**
     * Add a property based on a Simple name and value.
     * 
     * @param name The name of the property.
     * @param isControl The control classification for property.
     * @param isNavigation The control classification for property.
     * @param value The value to set on the property.
     */            
    void add(String name, boolean isControl, boolean isNavigation, String value);
    
    /**
     * Add a property based on a Simple name and value assumed
     * to be control property.
     * 
     * @param name The name of the property.
     * @param value The value to set on the property.
     */            
    void add(String name, String value);
    
    /**
     * For a given property name, get a property of type @link ProfileLocatorProperty
     *  
     * @param name The name of the property
     * @return a property of type @link ProfileLocatorProperty
     */
    String getValue(String name);
    
    /**
     * For a given property name, return control status of property.
     *  
     * @param name The name of the property
     * @return control classification flag
     */
    boolean isControl(String name);

    /**
     * For a given property name, return navigation status of property.
     *  
     * @param name The name of the property
     * @return navigation classification flag
     */
    boolean isNavigation(String name);
    
    /**
     * <p>Profiles can be created from a normalized <i>Profile Locator Path</i>
     * The format of the path is name:value pairs of all property, separated by a <i>path separator</i>.
     * Note: all locator property elements are assumed to be control properties.
     * An example locator path:</p>
     * 
     *      <pre>:page:default.psml:artist:air:song:all-i-need</pre>
     * 
     * @param path The normalized path as shown above from which the locator is created.
     */
    void createFromLocatorPath(String path);
    
    /**
     * Profiles can be converted to a normalized <i>Profile Locator Path</i>
     * The format of the path is name/value pairs of all property, separated by a <i>path separator</i>.
     * An example locator path:
     * 
     *      <pre>:page:default.psml:artist:joni-mitchell:song:cary</pre>
     * 
     * @return The normalized path as shown above.
     */
    String getLocatorPath();
        
    /**
     * Normalize profile properties obtained from profile locator iterators
     * into a <i>Profile Locator Path</i>.
     * 
     * @param properties The array of profile properties.
     * @return The normalized path for properties.
     */
    String getLocatorPath(ProfileLocatorProperty [] properties);
        
    /**
     * Returns a normalized path. @see #getLocatorPath()
     * 
     * @return The normalized path representation of this locator.
     */
    String toString();
    
    /**
     * Locators are intended to be sufficient to locate managed pages, so the request
     * path must be generally available in the event it is not otherwise captured in a
     * rule criterion.
     * 
     * @return The request path
     */
    String getRequestPath();
    
    /**
     * Retain the request server name to support additional page location mapping
     * not part of the profiler rule criterion.
     * 
     * @return The request server name
     */
    String getRequestServerName();
}
