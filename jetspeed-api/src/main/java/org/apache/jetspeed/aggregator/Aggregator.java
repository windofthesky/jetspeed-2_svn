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
import org.apache.jetspeed.request.RequestContext;

import java.io.IOException;

/**
 * Basic aggregation interface. The role of the aggregator service is to abstract the generation of web snippets, such
 * as portlets, and build a response buffer into the provided request context.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface Aggregator 
{
    /**
     * Builds the portlet set defined in the context into a portlet tree. The response buffer is also
     * set into the request context.
     *
     * @param context the request context holding runtime request parameters to be normalized
     */
    public void build(RequestContext context)
        throws JetspeedException, IOException;

}
