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

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.factory.PortletFactory;
import org.apache.jetspeed.om.portlet.PortletApplication;

/**
 * Validator to check if the defined required applications have been initialised in the Jetspeed engine
 * 
 * @author <a href="mailto:ruben.carvalho@fmr.com">Ruben Carvalho</a>
 * @version $Id$
 */
public class PortletApplicationAvailableHeathCheckValidator implements HealthCheckValidator
{
    private static final Logger log = LoggerFactory.getLogger(PortletApplicationAvailableHeathCheckValidator.class);
    public static boolean isInfoEnabled = log.isInfoEnabled();
    public static boolean isDebugEnabled = log.isDebugEnabled();
    /**
     * Spring property numberOfRetries.
     */
    private int numberOfRetries;
    /**
     * Spring property retryDelay.
     */
    private long retryDelay;
    /**
     * Spring property retryDelay.
     */
    private List requiredPortletApplications;
    /**
     * Spring property portletRegistry.
     */
    private PortletRegistry portletRegistry;
    /**
     * Spring property portletFactory.
     */
    private PortletFactory portletFactory;
    /**
     * Spring property stopValidationOnError.
     */
    private boolean stopValidationOnError;

    public PortletApplicationAvailableHeathCheckValidator(List requiredPortletApplications, int numberOfRetries,
                                                          long retryDelay, PortletRegistry portletRegistry,
                                                          PortletFactory portletFactory, boolean stopValidationOnError)
    {
        this.requiredPortletApplications = requiredPortletApplications;
        this.numberOfRetries = numberOfRetries;
        this.retryDelay = retryDelay;
        this.portletRegistry = portletRegistry;
        this.portletFactory = portletFactory;
        this.stopValidationOnError = stopValidationOnError;
    }

    public HealthCheckValidatorResult validate()
    {
        HealthCheckValidatorResult result = new HealthCheckValidatorResult();
        boolean allPAStatus = true;
        StringBuffer messages = new StringBuffer();
        try
        {
            // check if all required apps have been registered and are
            // available.
            for (Iterator it = requiredPortletApplications.iterator(); it.hasNext();)
            {
                // the portlet application name
                String paName = (String) it.next();
                if (isDebugEnabled)
                {
                    log.debug("Checking portlet application: " + paName);
                }
                if (messages.length()>0)
                {
                    messages.append(LINE_SEPARATOR);
                }
                messages.append(paName + ": ");
                PortletApplication pa = portletRegistry.getPortletApplication(paName, true);
                boolean thisPAStatus = portletFactory.isPortletApplicationRegistered(pa);
                if (thisPAStatus)
                {
                    messages.append("is up");
                }
                else
                {
                    messages.append("is down");
                    allPAStatus = false;
                    if (stopValidationOnError)
                    {
                        break;
                    }
                }
            }
            if (!allPAStatus)
            {
                result.setHealthCheckResult(HealthCheckValidatorResult.VALIDATOR_FAILED);
            }
        }
        catch (Exception e)
        {
            // if any exceptions occur, even runtime exceptions, return a failed
            // result
            log.error("Exception while running the portlet application validator", e);
            result.setHealthCheckResult(HealthCheckValidatorResult.VALIDATOR_FAILED);
            messages.append("Exception while running the portlet application validator: " + e.getMessage());
        }
        if (isDebugEnabled)
        {
            log.debug(messages.toString());
        }
        result.setResultMessage(messages.toString());
        return result;
    }

    public int getNumberOfRetries()
    {
        return numberOfRetries;
    }

    public long getRetryDelay()
    {
        return retryDelay;
    }
}