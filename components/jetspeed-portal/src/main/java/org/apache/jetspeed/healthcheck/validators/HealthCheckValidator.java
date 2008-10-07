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
package org.apache.jetspeed.healthcheck.validators;

import org.apache.jetspeed.pipeline.valve.HealthCheckValve;

/**
 * Interface to be implemented by validator classes which will be injected in {@link HealthCheckValve}.
 * 
 * @author <a href="mailto:ruben.carvalho@fmr.com">Ruben Carvalho</a>
 * @version $Id$
 */
public interface HealthCheckValidator
{
    static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    /**
     * This method performs the validation and returns the status of the execution
     * 
     * @return An instance of <code>HealthCheckValidatorResult</code> with the result code
     */
    HealthCheckValidatorResult validate();

    /**
     * This method returns the number of times the validate() method should be re-executed if it fails the first time.
     * 
     * @return Number of times to re-execute validate()
     */
    int getNumberOfRetries();

    /**
     * This method returns the amount of time between each execution of the validate() method
     * 
     * @return The amount of time between each execution of validate()
     */
    long getRetryDelay();
}