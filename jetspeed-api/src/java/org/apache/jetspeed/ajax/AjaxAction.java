/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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
     * @param resultMap map of action parameters passed to the builder context
     * @return success is true, failure is false
     * @throws Exception
     */
    public boolean run(RequestContext requestContext, Map resultMap) throws AJAXException;
}
