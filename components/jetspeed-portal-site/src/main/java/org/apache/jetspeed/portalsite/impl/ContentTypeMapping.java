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

/**
 * This class specifies a content type mapping definition for use
 * by the portal-site content type mapper component.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id:$
 */
public class ContentTypeMapping extends AbstractPatternMapping
{
    private String mappedType;
    private boolean mappedTypeReplacements;
    
    /**
     * Construct content type pattern mapping.
     * 
     * @param pattern request path matching pattern
     * @param mappedType resulting content type
     */
    public ContentTypeMapping(String pattern, String mappedType)
    {
        super(pattern);
        this.mappedType = mappedType;
        this.mappedTypeReplacements = (mappedType.indexOf('$') != -1);
    }

    /**
     * Match content type pattern against request path, replacing
     * all subsequence expressions in the mapped type.
     * 
     * @param requestPath request path to match
     * @return mapped content type or null if not matched
     */
    public String map(String requestPath)
    {
        Matcher patternMatcher = getPatternMatcher(requestPath);
        if (patternMatcher.find())
        {
            String type = mappedType;
            if (mappedTypeReplacements)
            {
                for (int i = 0; (i <= patternMatcher.groupCount()); i++)
                {
                    String groupMatch = requestPath.substring(patternMatcher.start(i), patternMatcher.end(i));
                    type = type.replace("$"+i, groupMatch);
                }
            }
            return type;
        }
        return null;
    }
}
