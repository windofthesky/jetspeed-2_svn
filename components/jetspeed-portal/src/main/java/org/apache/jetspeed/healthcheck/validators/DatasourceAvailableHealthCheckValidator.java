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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * Validator to check if the defined datasources are up and running
 * 
 * @author <a href="mailto:ruben.carvalho@fmr.com">Ruben Carvalho</a>
 * @version $Id$
 */
public class DatasourceAvailableHealthCheckValidator implements HealthCheckValidator
{
    private static final Logger log = LoggerFactory.getLogger(DatasourceAvailableHealthCheckValidator.class);
    public static boolean isInfoEnabled = log.isInfoEnabled();
    public static boolean isDebugEnabled = log.isDebugEnabled();
    /**
     * Spring property resources. Maps the database name to a list of datasources.
     */
    private Map resources;
    /**
     * Spring property numberOfRetries.
     */
    private int numberOfRetries;
    /**
     * Spring property retryDelay.
     */
    private long retryDelay;
    /**
     * Spring property stopValidationOnError.
     */
    private boolean stopValidationOnError;
    /**
     * Spring property requireAllValid.
     */
    private boolean requireAllValid;

    public DatasourceAvailableHealthCheckValidator(Map resources, int numberOfRetries, long retryDelay,
                                                   boolean stopValidationOnError, boolean requireAllValid)
    {
        this.resources = resources;
        this.numberOfRetries = numberOfRetries;
        this.retryDelay = retryDelay;
        this.stopValidationOnError = stopValidationOnError;
        this.requireAllValid = requireAllValid;
    }

    public HealthCheckValidatorResult validate()
    {
        if (isDebugEnabled)
        {
            log.debug("Starting method: DatasourceAvailableHealthCheckValidator.validate()");
        }
        HealthCheckValidatorResult result = new HealthCheckValidatorResult();
        boolean allDataSourcesStatus = true;
        StringBuffer messages = new StringBuffer();
        try
        {
            Set dbNames = resources.keySet();
            for (Iterator it = dbNames.iterator(); it.hasNext();)
            {
                String dbName = (String) it.next();
                if (messages.length()>0)
                {
                    messages.append(LINE_SEPARATOR);
                }
                messages.append(dbName + ":");
                if (isDebugEnabled)
                {
                    log.debug("Database: " + dbName);
                }
                DataSourcesValidationBean dsBean = (DataSourcesValidationBean) resources.get(dbName);
                String validationQuery = dsBean.getValidationQuery();
                boolean dbStatus = true;
                for (Iterator it2 = dsBean.getDatasources().iterator(); it2.hasNext();)
                {
                    DataSource ds = (DataSource) it2.next();
                    dbStatus = isDatasourceValid(validationQuery, ds);
                    if (dbStatus)
                    {
                        // the ds is up
                        if (!requireAllValid)
                        {
                            // only 1 datasource is required to be available so
                            // we can interrupt this loop
                            break;
                        }
                    }
                    else
                    {
                        // the ds is not available
                        if (requireAllValid)
                        {
                           // all datasource(s) need to be available so
                           // we can interrupt this loop
                           break;
                        }
                    }
                }
                if (dbStatus)
                {
                    messages.append(" is up");
                }
                else
                {
                    // none of the datasources for this DB is available so
                    // fail the whole validator
                    allDataSourcesStatus = false;
                    messages.append(" is down");
                }
                if (stopValidationOnError && !allDataSourcesStatus)
                {
                    // the validator has failed and stopValidationOnError
                    // is true so we have to interrupt the validator
                    break;
                }
            }
            if (!allDataSourcesStatus)
            {
                result.setHealthCheckResult(HealthCheckValidatorResult.VALIDATOR_FAILED);
            }
        }
        catch (Exception e)
        {
            // if any exceptions occur, even runtime exceptions, return a failed
            // result
            log.error("Exception while running the datasource validator", e);
            result.setHealthCheckResult(HealthCheckValidatorResult.VALIDATOR_FAILED);
            messages.append("Exception while running the datasource validator: " + e.getMessage());
        }
        if (isDebugEnabled)
        {
            log.debug(messages.toString());
        }
        result.setResultMessage(messages.toString());
        return result;
    }

    /**
     * Checks is a datasource is valid or not by executing a <code>validationQuery</code>
     * 
     * @param validationQuery
     *            The query to be executed
     * @param dataSource
     *            The datasource to be checked
     * @return Whether the datasource is available or not
     */
    private boolean isDatasourceValid(String validationQuery, DataSource dataSource)
    {
        boolean dsStatus = true;
        Connection con = null;
        Statement stmt = null;
        try
        {
            con = dataSource.getConnection();
            stmt = con.createStatement();
            stmt.execute(validationQuery);
        }
        catch (SQLException ex)
        {
            dsStatus = false;
            log.error("The datasource is not available", ex);
        }
        finally
        {
            JdbcUtils.closeStatement(stmt);
            JdbcUtils.closeConnection(con);
        }
        return dsStatus;
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