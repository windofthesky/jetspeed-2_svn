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
package org.apache.jetspeed.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * HttpUtils
 * 
 * @version $Id$
 */
public class HttpUtils
{
    private HttpUtils()
    {
    }
    
    public static Map<String, String[]> parseQueryString(String queryString)
    {
        return parseQueryString(queryString, "ISO-8859-1");
    }
    
    public static Map<String, String[]> parseQueryString(String queryString, String encoding)
    {
        if (StringUtils.isBlank(queryString))
        {
            return Collections.emptyMap();
        }
        
        Map<String, String[]> paramMap = new HashMap<String, String[]>();
        
        for (String pair : StringUtils.split(queryString, "&"))
        {
            String [] nameAndValue = StringUtils.split(pair, "=", 2);
            
            if (nameAndValue.length == 2)
            {
                String name = nameAndValue[0];
                String value = nameAndValue[1];
                
                if (encoding != null)
                {
                    try
                    {
                        name = URLDecoder.decode(name, encoding);
                        value = URLDecoder.decode(value, encoding);
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        throw new IllegalArgumentException("Unsupported encoding: " + encoding);
                    }
                }
                
                String [] values = paramMap.get(name);
                
                if (values == null)
                {
                    paramMap.put(name, new String [] { value });
                }
                else
                {
                    String [] newValues = new String[values.length + 1];
                    System.arraycopy(values, 0, newValues, 0, values.length);
                    newValues[values.length] = value;
                    paramMap.put(name, newValues);
                }
            }
        }
        
        return paramMap;
    }
    
}
