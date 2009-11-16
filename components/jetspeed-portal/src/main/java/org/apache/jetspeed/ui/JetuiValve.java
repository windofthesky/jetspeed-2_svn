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

package org.apache.jetspeed.ui;

import org.apache.jetspeed.aggregator.PageAggregator;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * Invokes the aggregator service in the request pipeline
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 */
public class JetuiValve
       extends AbstractValve
{
    private PageAggregator aggregator;
    
    public JetuiValve(PageAggregator aggregator)
    {
        this.aggregator = aggregator;
    }

    public void invoke( RequestContext request, ValveContext context )
    throws PipelineException
    {
        try
        {
            aggregator.build(request);
        }
        catch (Exception e)
        {
            throw new PipelineException(e.toString(), e);
        }        
        context.invokeNext( request );        
    }
    
    public String toString()
    {
        return "JetuiValve";
    }
}