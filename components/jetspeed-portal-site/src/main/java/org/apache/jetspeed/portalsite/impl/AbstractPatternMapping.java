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
package org.apache.jetspeed.portalsite.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class specifies an abstract pattern mapping definition for use
 * by the portal-site content type mapper component mappings.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class AbstractPatternMapping
{
    private String pattern;
    private Pattern compiledPattern;
    
    /**
     * Construct pattern mapping.
     * 
     * @param pattern mapping pattern
     */
    protected AbstractPatternMapping(String pattern)
    {
        this.pattern = pattern;
        this.compiledPattern = Pattern.compile(pattern);
    }
    
    /**
     * Allocate new matcher for mapping pattern.
     * 
     * @param input input string
     * @return allocated matcher
     */
    protected Matcher getPatternMatcher(String input)
    {
        return compiledPattern.matcher(input);
    }
}
