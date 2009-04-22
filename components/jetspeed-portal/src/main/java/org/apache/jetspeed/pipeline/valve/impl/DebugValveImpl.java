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

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * <p>
 * Debug Valve
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: $
 *
 */
public class DebugValveImpl extends AbstractValve 
{
    private static final Logger log = LoggerFactory.getLogger(DebugValveImpl.class);

    public DebugValveImpl()
    {
    }

    public void invoke(RequestContext request, ValveContext context) throws PipelineException
    {     
        debugHeaders(request.getRequest());
        context.invokeNext(request);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "DebugValveImpl";
    }
    
    private void debugHeaders( HttpServletRequest req )
    {
        log.info("-- Jetspeed Debug Valve: Debugging standard headers --");
        java.util.Enumeration e = req.getHeaderNames();
        while (e.hasMoreElements())
        {
            String name = (String) e.nextElement();
            String value = req.getHeader(name);
            log.info("http header = " + name + " : " + value);
//            System.out.println("http header = " + name + " : " + value);            
        }
    }
}