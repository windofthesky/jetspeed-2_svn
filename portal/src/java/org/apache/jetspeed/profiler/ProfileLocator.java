/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.Iterator;

import org.apache.jetspeed.profiler.rules.RuleCriterion;

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
 *      <pre>desktop:default-desktop.psml:page:default.psml:artist:al-stewart:song:on-the-border</pre>
 *      <pre>desktop:default-desktop.psml:path:/sports/football/nfl/chiefs:language:en</pre>
 * 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface ProfileLocator 
{
    public final static String PATH_SEPARATOR = ":";
        
    /**
     * Get an iterator over the locator's properties.
     * Elements are returned as @link ProfileLocatorProperty 
     *  
     * @return an iterator over the profile locator properties
     */
    Iterator iterator();    
        
    /**
     * Add a property based on a @link org.apache.jetspeed.profiler.rules.RuleCriterion
     * and a value. Rule criteria are templates for locating profile properties.
     * The value is combined with the rule to create a property.
     * 
     * @param criterion The rule criterion on which this property is based.
     * @param value The value to set on the property.
     */        
    void add(RuleCriterion criterion, String value);

    /**
     * Add a property based on a simple name and value.
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
     * <p>Profiles can be created from a normalized <i>Profile Locator Path</i>
     * The format of the path is name:value pairs of all property, separated by a <i>path separator</i>.
     * An example locator path:</p>
     * 
     *      <pre>:desktop:default-desktop.psml:page:default.psml:artist:air:song:all-i-need</pre>
     * 
     * @param path The normalized path as shown above from which the locator is created.
     */
    void createFromLocatorPath(String path);
    
    /**
     * <p>Profiles can be converted to a normalized <i>Profile Locator Path</i>
     * The format of the path is name/value pairs of all property, separated by a <i>path separator</i>.
     * An example locator path:</p>
     * 
     *      <pre>:desktop:default-desktop.psml:page:default.psml:artist:joni-mitchell:song:cary</pre>
     * 
     * @return The normalized path as shown above.
     */
    String getLocatorPath();
        
    /**
     * Returns a normalized path. @see #getLocatorPath()
     * 
     * @return The normalized path representation of this locator.
     */
    String toString();
    
}
