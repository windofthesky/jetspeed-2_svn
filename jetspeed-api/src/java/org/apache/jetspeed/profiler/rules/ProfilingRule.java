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
package org.apache.jetspeed.profiler.rules;

import java.io.Serializable;
import java.util.Collection;
import org.apache.jetspeed.profiler.ProfileLocator;
import org.apache.jetspeed.profiler.Profiler;
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
public interface ProfilingRule extends Serializable
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
    /** Session Attribute */    
    public final static String SESSION_ATTRIBUTE = "session";
    /** User attributes as defined in the Portlet spec 1.0 PLT.17 */
    public final static String USER_ATTRIBUTE = "user";
    /** Composite Capabilities and Preference Profile as defined http://www.w3.org/TR/NOTE-CCPP/ */
    public final static String CCPP_PROPERTY = "ccpp";
    
    /**
     * Standard properties used traditionally in Jetspeed
     */
    public final static String STANDARD_PAGE = "page";
    public final static String STANDARD_GROUP_ROLE_USER = "group.role.user";         
    public final static String STANDARD_USER = "user";
    public final static String STANDARD_GROUP = "group";
    public final static String STANDARD_ROLE = "role";
    public final static String STANDARD_MEDIATYPE = "mediatype";
    public final static String STANDARD_COUNTRY = "country";
    public final static String STANDARD_LANGUAGE = "language";
    public final static String STANDARD_ROLE_FALLBACK = "roles";

    /**
     * Given a criterion name, look up a value resolver
     * 
     * @param name The name of the criterion
     * @return
     */
    RuleCriterionResolver getResolver(String name);    
    
    /**
     * Applying the profiling rule generates a generic profile locator.
     * With this locator we can then locate a profiling resource.
     * 
     * @param context
     * @param service
     * @return
     */
    ProfileLocator apply(RequestContext context, Profiler service);
    
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
