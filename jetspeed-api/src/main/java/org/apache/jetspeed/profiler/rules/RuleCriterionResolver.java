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
package org.apache.jetspeed.profiler.rules;

import java.io.Serializable;

import org.apache.jetspeed.request.RequestContext;

/**
 * Resolves rule criterion based on a single criterion and 
 * runtime request context state. Note all resolvers should
 * look at the criterion's value if they fail to find it
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface RuleCriterionResolver extends Serializable
{    
    /** resolve the parameter via the request parameter, then value */        
    public static final String REQUEST = "request";

    /** resolve the parameter via a session attribute */
    public static final String SESSION = "session";
    
    /** look in the request first, then session */
    public static final String REQUEST_SESSION = "request.session";
    
    /** look at hard-coded criterion value only */
    public final static String  HARD_CODED = "hard.coded";
    
    /** look for group, then role, then user */
    public final static String GROUP_ROLE_USER = "group.role.user";

    /** first check request parameter, then check user in the request context */             
    public final static String USER = "user";
    
    /** first check request parameter, then check group in the request context */             
    public final static String GROUP = "group";
    
    /** first check request parameter, then check role in the request context */             
    public final static String ROLE = "role";
    
    /** first check request parameter, then check media type in the request context */             
    public final static String MEDIATYPE = "mediatype";
    
    /** first check request parameter, then check country code in the request context */                 
    public final static String COUNTRY = "country";

    /** first check request parameter, then user agent in the request context */                 
    public final static String USER_AGENT = "user.agent";
    
    /** first check request parameter, then check language in the request context */                 
    public final static String LANGUAGE = "language";
    
    public final static String ROLE_FALLBACK = "roles";

    /** resolve the parameter via the request path, then value */        
    public static final String PATH = "path";

    /** resolve the parameter via the request path, then value */        
    public static final String PAGE = "page";
    
    /** look in the request path first, then session */
    public static final String PATH_SESSION = "path.session";
    
    /** look in user attributes */
    public static final String USER_ATTRIBUTE = "user.attribute";
    
    /** change the current navigation path */
    public static final String NAVIGATION = "navigation";
    
    /**
     * Resolver the value for a criterion.
     * 
     * @param context The request context.
     * @param criterion The criterion being evaluated.
     * @return The value of the criterion or null if not found.
     *         Returns null to indicate to subclasses to continue processing.
     */        
    String resolve(RequestContext context, RuleCriterion criterion);
    
    /**
     * Gets the control classification of the resolver.
     * 
     * @return The control class flag
     */    
    boolean isControl(RuleCriterion criterion);

    /**
     * Gets the navigation classification of the resolver.
     * 
     * @return The control class flag
     */    
    boolean isNavigation(RuleCriterion criterion);
    
}
