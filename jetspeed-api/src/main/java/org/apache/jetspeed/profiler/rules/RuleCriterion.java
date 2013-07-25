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

/**
 * A RuleCriterion specifies one criterion in a list of profiling rule criteria.
 * This list is used to build normalized profiling locator and then 
 * locate a portal resource based on the request. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface RuleCriterion extends Serializable
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
     * @param type The request type associated with this criterion.
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
     * @param order The fallback type for this criterion, should be a valid value as shown above.
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
     * @param name The name of the request parameter, attribute or property.
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
     * @param value The value of the request parameter, attribute or property.
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
     * @param ruleId The rule's unique identifier
     */    
    void setRuleId(String ruleId);
    
}
