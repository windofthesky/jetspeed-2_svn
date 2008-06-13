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

import java.util.List;

/**
 * Bean to be used by validators that need to execute a validation query against a list of datasources
 * 
 * @author <a href="mailto:ruben.carvalho@fmr.com">Ruben Carvalho</a>
 * @version $Id$
 */
public class DataSourcesValidationBean
{
    /**
     * The SQL query.
     */
    private String validationQuery;
    /**
     * List of datasources.
     */
    private List datasources;

    public DataSourcesValidationBean(String validationQuery, List datasources)
    {
        this.validationQuery = validationQuery;
        this.datasources = datasources;
    }

    /**
     * Getter method for validationQuery
     * 
     * @return The validation query.
     */
    public String getValidationQuery()
    {
        return validationQuery;
    }

    /**
     * Getter method for datasources
     * 
     * @return The list of datasources.
     */
    public List getDatasources()
    {
        return datasources;
    }
}