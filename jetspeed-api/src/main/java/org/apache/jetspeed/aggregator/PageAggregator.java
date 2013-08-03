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

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.om.page.ContentFragment;
import org.apache.jetspeed.request.RequestContext;

import java.io.IOException;

/**
 * Defines the interface for rendering content for a page. Page aggregation handles pipeline requests for rendering
 * full pages of layouts and portlets.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface PageAggregator extends Aggregator 
{
    /**
     * Defines the interface for rendering content for a page.
     *
     * @param rc the request context holding runtime request parameters to be normalized
     * @param root the root fragment to start rendering from
     * @throws JetspeedException
     * @throws IOException
     */
    void renderContent(RequestContext rc, ContentFragment root) throws JetspeedException, IOException;
}
