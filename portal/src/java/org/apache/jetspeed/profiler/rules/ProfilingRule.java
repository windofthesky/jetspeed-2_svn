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
package org.apache.jetspeed.profiler.rules;

import java.util.Collection;
import java.util.SortedMap;

import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.ProfilerService;
import org.apache.jetspeed.request.RequestContext;

/**
 * A ProfilingRule defines a list of criteria used when evaluating a request
 * to determine the location of a specific resource. Profiling rules are 
 * used by the Profiler Service to generically locate portal resources
 * based on the decoupled criteria for known portlet request data.
 * A rule consists of an ordered list of criteria which should be applied
 * in the given order of the SortedMap provided by this rule.
 * Following this order, fallback searches may be applied to find resources
 * using a less-specific algorithm until the least specific resource criterion
 * is considered. When all criteria are exhausted, the rule will fail.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface ProfilingRule
{
    /**
     * Define the basic supported rule types in the default Jetspeed implementation.
     * Other rule types can be added. 
     * Rule types define a grouping of rule parameters.
     * For example,  request parameters refer to parameters on the request
     */ 
    
    /** Standard rule criteria used by Jetspeed traditionally such as media type, language, username, role */
    public final static String STANDARD = "standard";
    /** Request parameters as defined in the Portlet spec 1.0 PLT.11.1.1 */ 
    public final static String REQUEST_PARAMETER = "request";
    /** Request attributes as defined in the Portlet spec 1.0 PLT.11.1.3 */
    public final static String REQUEST_ATTRIBUTE = "attribute";
    /** Request parameters as defined in the Portlet spec 1.0 PLT.11.1.4 */    
    public final static String REQUEST_PROPERTY = "property";
    /** User attributes as defined in the Portlet spec 1.0 PLT.17 */
    public final static String USER_ATTRIBUTE = "user";
    /** Composite Capabilities and Preference Profile as defined http://www.w3.org/TR/NOTE-CCPP/ */
    public final static String CCPP_PROPERTY = "ccpp";
    
    /**
     * Standard properties used traditionally in Jetspeed
     */
    public final static String STANDARD_NAME = "name";     
    public final static String STANDARD_USER = "user";
    public final static String STANDARD_GROUP = "group";
    public final static String STANDARD_ROLE = "role";
    public final static String STANDARD_MEDIATYPE = "mediatype";
    public final static String STANDARD_COUNTRY = "country";
    public final static String STANDARD_LANGUAGE = "language";

    /**
     * Applying the profiling rule generates a generic profile locator.
     * With this locator we can then locate a profiling resource.
     * 
     * @param context
     * @param service
     * @return
     */
    ProfileLocator apply(RequestContext context, ProfilerService service);
    
    /**
     * Returns a sorted map (ordered) of rule criteria.
     * Each criteria consists of a normalized property/attribute/parameter 
     * associated with a request type.
     * 
     * @return a sorted map of rule criteria.
     */         
    Collection getRuleCriteria();
                     
    /**
     * Gets the unique identifier for this rule
     * 
     * @return The unique identifier
     */
    String getId();

    /**
     * Sets the unique identifier for this rule
     * 
     * @param id The unique identifier
     */    
    void setId(String id);
    
    /**
     * Gets the title used for with the rule for displaying descriptive text.
     * 
     * @return The title of this rule.
     */
    String getTitle();
    
    /**
     * Set the title used for with the rule for displaying descriptive text.
     * 
     * @param title The title of this rule.
     */
    void setTitle(String title);
    
    /**
     * Get the implementing classname of this rule from the database.
     * The class must exist in the hiearchy and in fact refers to itself when instantiated.
     * 
     * @return The classname of this instance.
     */
    String getClassname();
    
    /**
     * Sets the implementing classname of this rule from the database.
     * The class must exist in the hiearchy and in fact refers to itself when instantiated.
     * 
     * @param classname The classname of this instance.
     */
    void setClassname(String classname);
    
                           
}
