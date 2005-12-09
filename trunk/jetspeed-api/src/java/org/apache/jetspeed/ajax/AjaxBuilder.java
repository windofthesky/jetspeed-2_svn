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

import java.util.Map;
import org.apache.jetspeed.request.RequestContext;

/**
 * An Ajax request is made up of an action and builder phases.
 * Implement this interface for the Ajax builder phase.
 * The builder can add additional information to the requiest context,
 * and it also provides the template used for building the result stream
 * sent back over the Ajax request.
 *
 * @author <href a="mailto:taylor@apache.org">David Sean Taylor</a>
 */
public interface AjaxBuilder
{    
    /**
     * @return the name of the template used for this builder
     */
    public String getTemplate();

    /**
     * @return the name of the error template used for this builder
     */    
    public String getErrorTemplate();
    
    /**
     * Build the normal context template
     * 
     * @param requestContext The Jetspeed Request Context
     * @param contextVars Context variables to be substituted into template
     * @return true on success false onerror
     */
    public boolean buildContext(RequestContext requestContext, Map contextVars);
    
    /**
     * Build the error context template
     * 
     * @param requestContext The Jetspeed Request Context
     * @param contextVars Context variables to be substituted into template
     * @return true on success false onerror
     */
    public boolean buildErrorContext(RequestContext requestContext, Map contextVars);
}
