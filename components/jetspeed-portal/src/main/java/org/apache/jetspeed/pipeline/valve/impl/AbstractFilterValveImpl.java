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

import org.apache.jetspeed.pipeline.valve.AbstractValve;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * Abstract valve implementation supporting request path includes
 * and excludes.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public abstract class AbstractFilterValveImpl extends AbstractValve {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /** List of Ant style expression include request path patterns. */
    private List<String> includes;

    /** List of Ant style expression exclude request path patterns. */
    private List<String> excludes;

    /**
     * Test request path against includes.
     *
     * @param requestPath request path
     * @return included result
     */
    protected boolean includesRequestPath(String requestPath) {
        // assume request included if no includes are specified
        if ((includes == null) || includes.isEmpty()) {
            return true;
        }
        // test includes
        for (String include : includes) {
            if (PATH_MATCHER.match(include, requestPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Test request path against excludes.
     *
     * @param requestPath request path
     * @return excluded result
     */
    protected boolean excludesRequestPath(String requestPath) {
        // assume request not excluded if no excludes are specified
        if ((excludes == null) || excludes.isEmpty()) {
            return false;
        }
        // test excludes
        for (String exclude : excludes) {
            if (PATH_MATCHER.match(exclude, requestPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get list of include request path patterns.
     *
     * @return list of Ant style expression include patterns
     */
    public List<String> getIncludes() {
        return includes;
    }

    /**
     * Set list of include request path patterns.
     *
     * @param includes list of Ant style expression include patterns
     */
    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    /**
     * Get list of exclude request path patterns.
     *
     * @return list of Ant style expression exclude patterns
     */
    public List<String> getExcludes() {
        return excludes;
    }

    /**
     * Set list of exclude request path patterns.
     *
     * @param excludes list of Ant style expression exclude patterns
     */
    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }
}
