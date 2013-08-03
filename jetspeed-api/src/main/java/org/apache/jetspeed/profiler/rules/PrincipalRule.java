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
 * PrincipalRule is a paired association from principal to rule.
 * This pair is unique in that there can only be a one entry for a principal which maps to a rule.
 * This association is used by the profiler to determine which profiling rule to apply for a principal.
 * If a rule is not found, there should be a default system wide rule.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface PrincipalRule extends Serializable
{
    /**
     * Gets the name of the principal in this principal/rule/locator pair association.
     * The principal name identifies the uniqueness of the relationship.
     * It is used for keyed lookups to find the rule associated with this principal. 
     *  
     * @return The name of the principal in this association.
     */    
    String getPrincipalName();

    /**
     * Sets the name of the principal in this principal/rule/locator pair association.
     * The principal name identifies the uniqueness of the relationship.
     * It is used for keyed lookups to find the rule associated with this principal. 
     *  
     * @param name The name of the principal in this association.
     */        
    void setPrincipalName(String name);

    /**
     * Gets the name of the locator in this principal/rule/locator pair association.
     * The principal + locator name identifies the uniqueness of the relationship.
     * It is used for keyed lookups to find the rule associated with this principal  
     * for a given locator 
     *  
     * @return The name of the locator in this association.
     */    
    String getLocatorName();

    /**
     * Sets the name of the locator in this principal/locator/rule pair association.
     * The principal name + locator name identifies the uniqueness of the relationship.
     * It is used for keyed lookups to find the rule associated with this principal
     * for a given locator 
     *  
     * @param name The name of the locator in this association.
     */        
    void setLocatorName(String name);

    /**
     * Gets the profiling rule associated with the principal name 
     * 
     * @return The profiling rule associated with the principal name
     */
    ProfilingRule getProfilingRule();

    /**
     * Sets the profiling rule associated with the principal name 
     * 
     * @param rule The profiling rule associated with the principal name
     */
    void setProfilingRule(ProfilingRule rule);
    
}
