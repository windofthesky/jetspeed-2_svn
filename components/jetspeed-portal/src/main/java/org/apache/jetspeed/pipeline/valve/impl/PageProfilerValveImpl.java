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

import org.apache.jetspeed.administration.PortalConfigurationConstants;
import org.apache.jetspeed.pipeline.PipelineException;
import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.apache.jetspeed.pipeline.valve.PageProfilerValve;
import org.apache.jetspeed.pipeline.valve.ValveContext;
import org.apache.jetspeed.request.RequestContext;

/**
 * Determines whether to invoke the Page or Profiler valve dependent 
 * on URL requested pipeline. 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id:$
 */

public class PageProfilerValveImpl extends AbstractValve implements PageProfilerValve
{
	private PageProfilerValve profilerValve;
	private PageProfilerValve pageValve;
	private boolean useProfiler = false;
	
    public PageProfilerValveImpl(PageProfilerValve profilerValve, PageProfilerValve pageValve, String customizationMethod)
    {
    	this.profilerValve = profilerValve;
    	this.pageValve = pageValve;
    	this.useProfiler = (customizationMethod.equals(PortalConfigurationConstants.JETUI_CUSTOMIZATION_SERVER));
    }
    
    public String toString()
    {
        return "PageProfilerValve";
    }

	@Override
	public void invoke(RequestContext request, ValveContext context)
			throws PipelineException 
	{
		if (useProfiler)
			profilerValve.invoke(request, context);
		else
			pageValve.invoke(request, context);
	}
}
