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

import org.apache.jetspeed.om.page.ContentPage;
import org.apache.jetspeed.request.RequestContext;

/**
 * <p>
 * The Content Server Adapter encapsulates all aggregated related
 * activities related to aggregation, lessening the coupling of the
 * aggregator to the content server, which can be disabled.
 * </p>
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface ContentServerAdapter 
{
    /**
     * Pre page aggregation event, prepares the content paths for the 
     * given decorators of the current page being aggregated. Preparing 
     * content paths is the process of putting in the correct decorator
     * paths so that the content server can correctly find the decorator
     * resources.  
     * 
     * @param context Jetspeed portal per request context.
     * @param page The current page being aggregated.
     */
    void prepareContentPaths(RequestContext context, ContentPage page);
    
    /**
     * Adds stylesheets into the response header for a decoration 
     * using the Header Resource component.
     * Styles can be gathered from both page and portlet decorators.
     * 
     * @param context Jetspeed portal per request context.
     * @param decoratorName Name of the decorator holding the style.  
     * @param decoratorType Type of decorator, either portlet or page.
     */
    void addStyle(RequestContext context, String decoratorName, String decoratorType);
    
}
