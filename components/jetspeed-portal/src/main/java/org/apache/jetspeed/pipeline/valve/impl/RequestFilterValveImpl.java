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

package org.apache.jetspeed.pipeline.valve.impl;

import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.RequestFilterValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This valve implementation filters incoming requests, sending a NOT_FOUND
 * response to requests that are filtered.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class RequestFilterValveImpl extends AbstractFilterValveImpl implements RequestFilterValve {

    private static final Logger log = LoggerFactory.getLogger(RequestFilterValveImpl.class);

    /** Valve name. */
    private String name;

    /**
     * Named valve constructor.
     *
     * @param name name of valve
     */
    public RequestFilterValveImpl(String name) {
        this.name = name;
    }

    @Override
    public void invoke(RequestContext request, ValveContext context) throws PipelineException {

        // get request path relative to pipeline/servlet path
        String requestPath = request.getRequest().getPathInfo();

        // test request path includes and excludes
        if (!includesRequestPath(requestPath) || excludesRequestPath(requestPath)) {
            if (log.isDebugEnabled()) {
                log.debug("Request filtered by " + request.getPipeline().getName() + "." + name + " request path: " + requestPath);
            }
            try {
                request.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException ioe) {
                if (log.isDebugEnabled()) {
                    log.error("Unexpected exception sending error for filtered request, (" + requestPath + "): " + ioe, ioe);
                }
            }
            return;
        }

        // continue valve execution on pipeline
        context.invokeNext(request);
    }

    /**
     * Get valve name.
     *
     * @return valve name
     */
    public String getName() {
        return name;
    }
}
