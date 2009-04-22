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
package org.apache.jetspeed.pipeline.valve;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.healthcheck.validators.HealthCheckValidator;
import org.apache.jetspeed.healthcheck.validators.HealthCheckValidatorResult;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * Valve that performs a health check based on the validators injected from a Spring configutation file.
 * 
 * @author <a href="mailto:ruben.carvalho@fmr.com">Ruben Carvalho</a>
 * @version $Id$
 */
public class HealthCheckValve extends AbstractValve
{
    static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final Logger log = LoggerFactory.getLogger(HealthCheckValve.class);
    public static boolean isInfoEnabled = log.isInfoEnabled();
    public static boolean isDebugEnabled = log.isDebugEnabled();
    /**
     * Spring property validators. List of validators to be executed by this Valve
     */
    private List validators;
    /**
     * Spring property successMessage. This String is added to the response if all validators succeed
     */
    private String successMessage;
    /**
     * Spring property failMessage. The string to be added to the response if one of the validators fails
     */
    private String failMessage;
    /**
     * Spring property addValidationMessagesToResponse. Whether messages returned in the validator result should be
     * appended to the http response or not
     */
    private boolean addValidationMessagesToResponse;
    /**
     * Spring property stopValidationOnError. Whether the valve execution should continue or stop if one validator fails
     */
    private boolean stopValidationOnError;

    public HealthCheckValve(List validators, String successMessage, String failMessage,
                            boolean addValidationMessagesToResponse, boolean stopValidationOnError)
    {
        this.validators = validators;
        this.successMessage = successMessage;
        this.failMessage = failMessage;
        this.addValidationMessagesToResponse = addValidationMessagesToResponse;
        this.stopValidationOnError = stopValidationOnError;
    }

    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {
        if (isDebugEnabled)
        {
            log.debug("Starting method: HealthCheckValve.invoke()");
        }
        List messages = new ArrayList();
        HttpServletResponse response = request.getResponse();
        boolean healthCheckStatus = true;
        try
        {
            // iterate all validators and execute its validate method.
            for (Iterator it = validators.iterator(); it.hasNext();)
            {
                HealthCheckValidator hcv = (HealthCheckValidator) it.next();
                if (isDebugEnabled)
                {
                    log.debug("Starting validator execution: " + hcv.getClass().getName());
                }
                HealthCheckValidatorResult result = null;
                // execute the validator until it succeeds or until the
                // number of retries runs out
                for (int i = 0; i <= hcv.getNumberOfRetries(); i++)
                {
                    result = hcv.validate();
                    if (isDebugEnabled)
                    {
                        log.debug("Validator execution: " +
                                  (result.getHealthCheckResult() == HealthCheckValidatorResult.VALIDATOR_SUCCEEDED));
                    }
                    if (result.getHealthCheckResult() == HealthCheckValidatorResult.VALIDATOR_SUCCEEDED)
                    {
                        // the validator succeeded so stop this loop and go for
                        // the next validator
                        break;
                    }
                    if ((i + 1) <= hcv.getNumberOfRetries())
                    {
                        // the validator did not succeed. If there are any
                        // retries left and if a retry delay was defined then
                        // wait before re-executing the same validator
                        if (hcv.getRetryDelay() > 0)
                        {
                            try
                            {
                                Thread.sleep(hcv.getRetryDelay());
                            }
                            catch (InterruptedException e)
                            {
                            }
                        }
                    }
                }
                if (result != null)
                {
                    if (addValidationMessagesToResponse)
                    {
                        messages.add(result.getResultMessage());
                    }
                    if (!(result.getHealthCheckResult() == HealthCheckValidatorResult.VALIDATOR_SUCCEEDED))
                    {
                        // this validator failed so mark the health check as
                        // failed
                        healthCheckStatus = false;
                        if (stopValidationOnError)
                        {
                            // stopValidationOnError is true so stop the health
                            // check
                            break;
                        }
                    }
                }
            }
            PrintWriter pw = response.getWriter();
            if (healthCheckStatus)
            {
                // if all validators succeeded, add the success message to the
                // http response
                pw.write(successMessage);
            }
            else
            {
                pw.write(failMessage);
            }
            if (addValidationMessagesToResponse)
            {
                for (Iterator it = messages.iterator(); it.hasNext();)
                {
                    pw.write(LINE_SEPARATOR + (String)it.next());
                }
            }
        }
        catch (Exception e)
        {
            log.error("Exception while running HealthCheckValve", e);
            // if any exceptions occur, even runtime exceptions, simply reset
            // the response's buffer and add any messages to it (if required)
            try
            {
                if (!response.isCommitted())
                {
                    response.resetBuffer();
                }
                PrintWriter pw = response.getWriter();
                pw.write(failMessage);
                if (addValidationMessagesToResponse)
                {
                    for (Iterator it = messages.iterator(); it.hasNext();)
                    {
                        pw.write(LINE_SEPARATOR + (String)it.next());
                    }
                }
            }
            catch (Exception e1)
            {
                log.error("Exception while running HealthCheckValve", e1);
            }
        }
    }
}