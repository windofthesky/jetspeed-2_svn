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
package org.apache.jetspeed.services.rest.util;

import javax.ws.rs.core.PathSegment;
import java.util.ArrayList;
import java.util.List;

/**
 * PathSegmentUtils
 * 
 * @version $Id$
 */
public class PathSegmentUtils
{
    
    private PathSegmentUtils()
    {
    }
    
    /**
     * Returns a joined string from the path segments list separated by the separator argument.
     * 
     * @param pathSegments the path segments list.
     * @param separator the separator between path segments.
     * @return
     */
    public static String join(List<PathSegment> pathSegments, String separator)
    {
        return joinWithPrefix(pathSegments, separator, null);
    }
    
    /**
     * Returns a joined string from the path segments list separated by the separator argument.
     * If the prefix argument is non null value, then the prefix string will be prepended.
     * 
     * @param pathSegments the path segments list.
     * @param separator the separator between path segments.
     * @param prefix the prefix string to be prepended.
     * @return
     */
    public static String joinWithPrefix(List<PathSegment> pathSegments, String separator, String prefix)
    {
        if (pathSegments == null)
        {
            return null;
        }
        
        StringBuilder sbPath = new StringBuilder(100);
        
        if (prefix != null)
        {
            sbPath.append(prefix);
        }
        
        boolean first = true;
        
        for (PathSegment pathSegment : pathSegments)
        {
            if (!first)
            {
                sbPath.append(separator);
            }
            else
            {
                first = false;
            }
            
            sbPath.append(pathSegment.getPath());
        }
        
        return sbPath.toString();
    }

    public static List<String> parseNames(String commaSeparated) {
        String names[] = commaSeparated.split(",");
        List<String> ids = new ArrayList<String>(names.length);
        for (String name : names) {
            ids.add(name.trim());
        }
        return ids;
    }

    public static String[] parseNamesArray(String commaSeparated) {
        String names[] = commaSeparated.split(",");
        int index = 0;
        for (String name : names) {
            names[index] = name.trim();
            index++;
        }
        return names;
    }

    public static List<Integer> parseIds(String commaSeparated) throws NumberFormatException {
        String stringIds[] = commaSeparated.split(",");
        List<Integer> ids = new ArrayList<Integer>(stringIds.length);
        for (String stringId : stringIds) {
            ids.add(new Integer(stringId.trim()));
        }
        return ids;
    }

    public static Integer[] parseIdArray(String commaSeparated) throws NumberFormatException {
        String stringIds[] = commaSeparated.split(",");
        Integer ids[] = new Integer[stringIds.length];
        int ix = 0;
        for (String id : stringIds) {
            ids[ix] = new Integer(id.trim());
            ix++;
        }
        return ids;
    }

    
}
