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
package org.apache.jetspeed.profiler.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.Valve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * ProfilerValveImpl
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: ProfilerValveImpl.java 388733 2006-03-25 09:03:58Z rwatler $
 */
public class CreatePageValveImpl extends AbstractValve implements Valve
{
    protected Logger log = LoggerFactory.getLogger(CreatePageValveImpl.class);
    
    private PortletActionSecurityBehavior securityBehavior;

    /**
     * CreatePageValveImpl - constructor
     *
     * @param behavior the behavior to create new page for new user
     */
    public CreatePageValveImpl(PortletActionSecurityBehavior behavior)
    {
        this.securityBehavior = behavior;
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.pipeline.valve.Valve#invoke(org.apache.jetspeed.request.RequestContext,
     *      org.apache.jetspeed.pipeline.valve.ValveContext)
     */
    public void invoke( RequestContext request, ValveContext context ) throws PipelineException
    {
        securityBehavior.createNewPageOnEdit(request);
        context.invokeNext(request);
    }

}
