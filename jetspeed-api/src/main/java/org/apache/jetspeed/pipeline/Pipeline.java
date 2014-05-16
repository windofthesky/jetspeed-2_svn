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
package org.apache.jetspeed.pipeline;

import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.request.RequestContext;

import java.io.IOException;

/**
 * Jetspeed Pipeline
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public interface Pipeline
{
    void initialize()
        throws PipelineException;
    /**
     * <p>Add a new Valve to the end of the pipeline.</p>
     *
     * @param valve Valve to be added.
     *
     * @exception IllegalStateException If the pipeline has not been
     * initialized.
     */
    void addValve(Valve valve);

    /**
     * <p>Return the set of all Valves in the pipeline.  If there are no
     * such Valves, a zero-length array is returned.</p>
     *
     * @return An array of valves.
     */
    Valve[] getValves();

    /**
     * <p>Return the set of all cleanup Valves in the pipeline.  If there
     * are no such Valves, a zero-length array is returned.</p>
     *
     * @return An array of cleanup valves.
     */
    Valve[] getCleanupValves();

    /**
     * <p>Cause the specified request and response to be processed by
     * the sequence of Valves associated with this pipeline, until one
     * of these Valves decides to end the processing.</p>
     *
     * <p>The implementation must ensure that multiple simultaneous
     * requests (on different threads) can be processed through the
     * same Pipeline without interfering with each other's control
     * flow.</p>
     *
     * @param context The run-time information, including the servlet
     * request and response we are processing.
     *
     * @exception IOException an input/output error occurred.
     */
    void invoke(RequestContext context)
        throws PipelineException;

    /**
     * <p>Remove the specified Valve from the pipeline, if it is found;
     * otherwise, do nothing.</p>
     *
     * @param valve Valve to be removed.
     */
    void removeValve(Valve valve);
    
    /**
     * Get the name of the pipeine
     * 
     * @return name of the pipeline
     */
    String getName();

}