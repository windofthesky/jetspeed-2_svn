/*
 * Copyright 2000-2004 The Apache Software Foundation.
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

package org.apache.jetspeed.pipeline.valve;

import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.pipeline.PipelineException;

/**
 * NOTE: This class will be deprecated once we merge with Summit
 *
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public interface ValveContext
{
    /**
     * <p>Cause the <code>invoke()</code> method of the next Valve
     * that is part of the Pipeline currently being processed (if any)
     * to be executed, passing on the specified request and response
     * objects plus this <code>ValveContext</code> instance.
     * Exceptions thrown by a subsequently executed Valve will be
     * passed on to our caller.</p>
     *
     * <p>If there are no more Valves to be executed, execution of
     * this method will result in a no op.</p>
     *
     * @param data The run-time information, including the servlet
     * request and response we are processing.
     *
     * @exception IOException Thrown by a subsequent Valve.
     * @exception SummitException Thrown by a subsequent Valve.
     * @exception SummitException No further Valves configured in the
     * Pipeline currently being processed.
     */
    public void invokeNext(RequestContext request)
        throws PipelineException;
}
