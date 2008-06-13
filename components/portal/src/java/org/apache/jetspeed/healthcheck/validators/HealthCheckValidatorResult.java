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

/**
 * This class is returned as the result of executing the validation method from a Validator class
 * 
 * @author <a href="mailto:ruben.carvalho@fmr.com">Ruben Carvalho</a>
 * @version $Id$
 */
public class HealthCheckValidatorResult
{
    /**
     * Validator constant which indicates a successful validation
     */
    public static final int VALIDATOR_SUCCEEDED = 101;

    /**
     * Validator constant which indicates a failed validation
     */
    public static final int VALIDATOR_FAILED = 100;

    /**
     * Result code. One of <code>VALIDATOR_*</code>
     */
    private int healthCheckResult;

    /**
     * The result message
     */
    private String resultMessage;

    public HealthCheckValidatorResult()
    {
        this.healthCheckResult = HealthCheckValidatorResult.VALIDATOR_SUCCEEDED;
        this.resultMessage = "";
    }

    public HealthCheckValidatorResult(int healthCheckResult, String resultMessage)
    {
        this.healthCheckResult = healthCheckResult;
        this.resultMessage = resultMessage;
    }

    /**
     * This method returns the result of running <code>HealthCheckValidator.validate()</code>.<br> <br> The result
     * has to be one of the constants VALIDATOR_*
     * 
     * @return Result from the execution of the validate() method.
     */
    public int getHealthCheckResult()
    {
        return healthCheckResult;
    }

    /**
     * Setter method for healthCheckResult
     * 
     * @param healthCheckResult
     *            The new code for healthCheckResult
     */
    public void setHealthCheckResult(int healthCheckResult)
    {
        this.healthCheckResult = healthCheckResult;
    }

    /**
     * This method returns a message for this validator's execution.<br> <br> This method should not be used to check
     * if a validator ran successfully or not. That should be done by checking
     * <code>healthCheckResult() == VALIDATOR_FAILED</code>
     * 
     * @return The execution message (if any)
     */
    public String getResultMessage()
    {
        return resultMessage;
    }

    /**
     * Setter method for resultMessage
     * 
     * @param resultMessage
     *            The new result message
     */
    public void setResultMessage(String resultMessage)
    {
        this.resultMessage = resultMessage;
    }
}