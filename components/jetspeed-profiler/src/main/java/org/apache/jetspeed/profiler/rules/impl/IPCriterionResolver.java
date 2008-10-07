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
package org.apache.jetspeed.profiler.rules.impl;

import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;

/**
 * IPCriterionResolver
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:philip.donaghy@gmail.com">Philip Mark Donaghy</a>
 * @version $Id: IPCriterionResolver.java 351839 2005-12-02 21:20:57Z taylor $
 */
public class IPCriterionResolver extends StandardResolver implements
        RuleCriterionResolver
{

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#resolve(org.apache.jetspeed.request.RequestContext,
     *      org.apache.jetspeed.profiler.rules.RuleCriterion)
     */
    public String resolve(RequestContext context, RuleCriterion criterion)
    {
        // look for override
        String value = super.resolve(context, criterion);
        if (value != null) { return value.toLowerCase(); }

        // Note IP addresses can vary depending on the client
        // Konqueror 3.4.2 returns IPv6 e.g. 0:0:0:0:0:0:0:1
        // Firefox 1.0.7 returns IPv4 e.g. 127.0.0.1
        // This is the value used to resolve pages in the _ip directory
        // TODO create an option to convert all IPv4 addresses to IPv6
        return context.getRequest().getRemoteAddr();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.rules.RuleCriterionResolver#isControl()
     */
    public boolean isControl(RuleCriterion criterion)
    {
        return true;
    }

}
