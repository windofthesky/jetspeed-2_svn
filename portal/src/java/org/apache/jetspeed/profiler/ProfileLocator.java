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
 *      <pre>/desktop/default-desktop.psml/page/default.psml/artist/al-stewart/song/on-the-border</pre>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface ProfileLocator 
{
    public final static String PATH_SEPARATOR = "/";
        
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
     * The format of the path is name/value pairs of all property, separated by a <i>path separator</i>.
     * An example locator path:</p>
     * 
     *      <pre>/desktop/default-desktop.psml/page/default.psml/artist/air/song/all-i-need</pre>
     * 
     * @param path The normalized path as shown above from which the locator is created.
     */
    void createFromLocatorPath(String path);
    
    /**
     * <p>Profiles can be converted to a normalized <i>Profile Locator Path</i>
     * The format of the path is name/value pairs of all property, separated by a <i>path separator</i>.
     * An example locator path:</p>
     * 
     *      <pre>/desktop/default-desktop.psml/page/default.psml/artist/joni-mitchell/song/cary</pre>
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
