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
package org.apache.jetspeed.locator;

import java.util.Iterator;

/**
 * TemplateLocator interface
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface TemplateLocator
{        
    /**
     * Locate an template using Jetspeed template location algorithm
     *
     * @param locator The template locator
     * @return The template found, or null if not found.
     * @throws TemplateLocatorException
     */
    TemplateDescriptor locateTemplate(LocatorDescriptor locator)
        throws TemplateLocatorException;

    /**
     * Factory to create template locators of the given type.
     * Known supported locator types, but not limited to: 
     *      <code>portlet</code>
     *      <code>email</code>
     * 
     * @param The type of locator to create
     * @return a general template locator of the given type
     * @throws TemplateLocatorException if factory exception or if not valid locator type
     */
    LocatorDescriptor createLocatorDescriptor(String type)
        throws TemplateLocatorException;

    /**
     * Creates a locator from a string of format (where brackets are optional]:
     *
     *   template/<templateType>/[media-type/<mediaType>]/[language/<language>]/[country/<country>]]/name/<templateName
     * 
     * @param string the string representation of a template locator 
     * @throws TemplateLocatorException
     */        
    LocatorDescriptor createFromString(String string)
        throws TemplateLocatorException;
        
    /** 
     * Query for a collection of templates given template locator criteria.
     *
     * @param locator The template locator criteria.
     * @return The result list of {@link Template} objects matching the locator criteria.
     */
    public Iterator query(LocatorDescriptor locator);
    
}
