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

import org.apache.jetspeed.request.RequestContext;

/**
 * Resolves rule criterion based on a single criterion and 
 * runtime request context state. Note all resolvers should
 * look at the criterion's value if they fail to find it
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface RuleCriterionResolver
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
    
    /** first check request parameter, then check language in the request context */                 
    public final static String LANGUAGE = "language";
    
    public final static String ROLE_FALLBACK = "roles";

    /** resolve the parameter via the request path, then value */        
    public static final String PATH = "path";
    
    /**
     * Resolver the value for a criterion.
     * 
     * @param context The request context.
     * @param criterion The criterion being evaluated.
     * @return The value of the criterion or null if not found.
     *         Returns null to indicate to subclasses to continue processing.
     */        
    String resolve(RequestContext context, RuleCriterion criterion);
    
}
