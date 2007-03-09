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
package org.apache.jetspeed.aggregator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * PortletValve
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletValve extends AbstractValve
{
    private static final Log log = LogFactory.getLog( PortletValve.class );
    private PortletAggregator aggregator;
    
    public PortletValve(PortletAggregator aggregator)
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
            throw new PipelineException(e);
        }
        // Pass control to the next Valve in the Pipeline
        context.invokeNext( request );
    }

    public String toString()
    {
        return "PortletValve";
    }
}
