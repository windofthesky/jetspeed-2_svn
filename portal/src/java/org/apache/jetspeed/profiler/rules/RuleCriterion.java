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

/**
 * A RuleCriterion specifies one criterion in a list of profiling rule criteria.
 * This list is used to build normalized profiling locator and then 
 * locate a portal resource based on the request. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface RuleCriterion 
{
    public static final int FALLBACK_CONTINUE = 1;
    public static final int FALLBACK_STOP = 0;
    public static final int FALLBACK_LOOP = 2;
    
    /**
     * Gets the rule request type for this specific criterion.
     * Rule types determine which type of request property, parameter or attribute
     * to look at when building a profiling locator.
     *  
     * @return The request type associated with this criterion.
     */
    String getType();

    /**
     * Sets the rule request type for this specific criterion.
     * Rule types determine which type of request property, parameter or attribute
     * to look at when building a profiling locator.
     *  
     * @param The request type associated with this criterion.
     */    
    void setType(String type);

    /**
     * Sets the fallback order for this criterion.
     * Lower numbers are returned first during iteration.
     * Higher numbers should be put on the locator stack first.
     * 
     * @return The fallback order for this criterion.
     */
    int getFallbackOrder();
    
    
    /**
     * Gets the fallback order for this criterion.
     * Lower numbers are returned first during iteration.
     * Higher numbers should be put on the locator stack first.
     * 
     * @param order The fallback order for this criterion.
     */
    void setFallbackOrder(int order);

    /**
     * Gets the fallback type for this criterion. 
     * Fallback types are used when locating a profiled resource.
     * The type tells the Profiling rule what to do next on failed criterion matching.
     * 
     * Known values:
     * 
     *   FALLBACK_CONTINUE - evaluate this criterion and if it fails continue to the next criterion
     *   FALLBACK_STOP - evaluate this criterion and if it fails stop evaluation criteria for this rule
     *   FALLBACK_LOOP - evaluate this criterion and if it fails continue evaluating
     * 
     * @return The fallback type for this criterion, should be a valid value as shown above.
     */
    int getFallbackType();

    /**
     * Sets the fallback type for this criterion. 
     * Fallback types are used when locating a profiled resource.
     * The type tells the Profiling rule what to do next on failed criterion matching.
     * 
     * Known values:
     * 
     *   FALLBACK_CONTINUE - evaluate this criterion and if it fails continue to the next criterion
     *   FALLBACK_STOP - evaluate this criterion and if it fails stop evaluation criteria for this rule
     *   FALLBACK_LOOP - evaluate this criterion and if it fails continue evaluating
     * 
     * @param The fallback type for this criterion, should be a valid value as shown above.
     */    
    void setFallbackType(int order);
    
    /**
     * Gets the name of the parameter, attribute or property in the portal request.
     * This name is used to lookup the value of the request parameter, attribute, or 
     * property when building a profile locator.
     *  
     * @return The name of the request parameter, attribute or property.
     */    
    String getName();

    /**
     * Sets the name of the parameter, attribute or property in the portal request.
     * This name is used to lookup the value of the request parameter, attribute, or 
     * property when building a profile locator.
     *  
     * @param The name of the request parameter, attribute or property.
     */        
    void setName(String name);

   
    /**
     * Gets the value of the parameter, attribute or property in the portal request.
     *  
     * @return The value of the request parameter, attribute or property.
     */    
    String getValue();

    /**
     * Sets the value of the parameter, attribute or property in the portal request.
     *  
     * @param The value of the request parameter, attribute or property.
     */        
    void setValue(String value);

    /**
     * Gets the unique rule identifier for the associated owner rule 
     * 
     * @return The rule's unique identifier
     */
    String getRuleId();

    /**
     * Sets the unique rule identifier for the associated owner rule 
     * 
     * @param id The rule's unique identifier
     */    
    void setRuleId(String ruleId);
    
}
