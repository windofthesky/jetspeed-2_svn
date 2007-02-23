/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.profiler.rules.impl;

import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.RuleCriterionResolver;
import org.apache.jetspeed.request.RequestContext;

/**
 * Hostname Resolver
 * 
 * @author <a href="mailto:stalherm@goodgulf.net">Frank Stalherm</a>
 * @version $Id:$
 */
public class HostnameCriterionResolver extends StandardResolver implements
        RuleCriterionResolver
{

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.rules.impl.StandardResolver#isControl(org.apache.jetspeed.profiler.rules.RuleCriterion)
     */
    public boolean isControl(RuleCriterion criterion)
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.jetspeed.profiler.rules.impl.StandardResolver#isNavigation(org.apache.jetspeed.profiler.rules.RuleCriterion)
     */
    public boolean isNavigation(RuleCriterion criterion)
    {
        return true;
    }

    public String resolve(RequestContext context, RuleCriterion criterion)
    {
        return getHostname(context.getRequest().getServerName());
    }

    /**
     * extracts the hostname from the servername from RequestContext
     * 
     * @param servername
     *            server name from request
     * @return hostname extracted from server name
     */
    public String getHostname(String servername)
    {
        String hostname = null;

        if (servername != null)
        {
            int idx = servername.indexOf(".");
            if (idx != -1)
            {
                hostname = servername.substring(idx, servername.length());
            } else
            {
                // maybe we already have the hostname
                // testing for IPv6 IP Address
                idx = servername.indexOf(":");
                if (idx != -1)
                {
                    // TODO resolving IP Address?
                    // for now we take this as the host name
                    hostname = servername;
                } else
                {
                    // looks like hostname==servername
                    hostname = servername;
                }
            }
        }
        return hostname;
    }

}
