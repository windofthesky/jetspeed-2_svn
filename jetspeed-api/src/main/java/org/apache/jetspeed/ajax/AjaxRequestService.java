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
package org.apache.jetspeed.ajax;

import org.apache.jetspeed.request.RequestContext;

import java.util.Map;

/**
 * An Ajax request is made up of an action and builder phases.
 * This interface defines the entry point into Ajax Request processing
 * typically used in a Jetspeed pipeline.
 *
 * @author <href a="mailto:taylor@apache.org">David Sean Taylor</a>
 */
public interface AjaxRequestService {
    /**
     * Entry point into processing an Ajax request
     *
     * @param requestContext the request context holding runtime request parameters to be normalized
     * @throws AJAXException
     */
    public void process(RequestContext requestContext) throws AJAXException;

    /**
     * Retrieve the map of configured actions. Actions are mapped from action names to
     * actual {@link AjaxAction} implementations. Examples:
     *
     * {@code
     * <entry key="moveleft">
     *   <ref bean="AjaxMovePortletLeft" />
     * </entry>
     * <entry key="moveright">
     *   <ref bean="AjaxMovePortletRight" />
     * </entry>
     * }
     *
     * @return the map of configured actions
     */
    public Map<String, AjaxAction> getActionMap();
}
