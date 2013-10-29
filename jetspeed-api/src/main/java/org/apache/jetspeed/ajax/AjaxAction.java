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
 * Implement this interface for the Ajax action phase.
 * The action should put any parameters or results it wants 
 * passed on to the builders in the resultMap
 *
 * @author <href a="mailto:taylor@apache.org">David Sean Taylor</a>
 */
public interface AjaxAction
{
	 
    /**
     * The action should put any parameters or results it wants 
     * passed on to the builders in the resultMap
     * This method runs an Ajax action.
     *  
     * @param requestContext The Jetspeed Request Context
     * @param resultMap map of action parameters (any object) passed to the builder context
     * @return success is true, failure is false
     * @throws AJAXException
     */
    public boolean run(RequestContext requestContext, Map<String,Object> resultMap) throws AJAXException;

    /**
     * Same as run method, but runs in batch mode, as a hint to the action
     * that it is running a multiple action and can delay its update
     * runBatch currently supports pageManager.updatePage
     *  
     * @param requestContext The Jetspeed Request Context
     * @param resultMap map of action parameters passed to the builder context
     * @return success is true, failure is false
     * @throws AJAXException
     */    
    public boolean runBatch(RequestContext requestContext, Map<String,Object> resultMap) throws AJAXException;
    
    /**
     * Checks to see if the current subject has access to to execute this action.
     * 
     * @param context
     * @return true if access granted, false if denied.
     */
    public boolean checkAccess(RequestContext context, String action);
    
}
