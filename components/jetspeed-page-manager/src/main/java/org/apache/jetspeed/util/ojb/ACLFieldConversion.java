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
package org.apache.jetspeed.util.ojb;

import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * ACLFieldConversion 
 *
 * OJB field conversion: Helps transparently map ACL List members
 * to/from database table column that that contains an ordered
 * CSV list of strings.
 */
public class ACLFieldConversion implements FieldConversion
{
    private static final String DELIM = ",";

    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#javaToSql(java.lang.Object)
     */
    public Object javaToSql(Object arg0) throws ConversionException
    {
        if (arg0 instanceof List)
        {
            @SuppressWarnings("unchecked")
            List<String> csvList = (List)arg0;
            if (csvList.size() > 1)
            {
                StringBuffer buffer = null;
                for (String value : csvList)
                {
                    if (value.length() > 0)
                    {
                        if (buffer == null)
                        {
                            buffer = new StringBuffer(255);
                        }
                        else
                        {
                            buffer.append(DELIM);
                        }
                        buffer.append(value);
                    }
                }
                if (buffer != null)
                {
                    return buffer.toString();
                }
            }
            else if (!csvList.isEmpty())
            {
                String value = (String)csvList.get(0);
                if (value.length() > 0)
                {
                    return value;
                }
            }
            return "";
        }
        return arg0;
    }

    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#sqlToJava(java.lang.Object)
     */
    public Object sqlToJava(Object arg0) throws ConversionException
    {
        if (arg0 instanceof String)
        {
            List<String> aclList = new ArrayList<String>(4);
            StringTokenizer tokens = new StringTokenizer((String) arg0, DELIM);
            while (tokens.hasMoreTokens())
            {
                String value = tokens.nextToken().trim();
                if (value.length() > 0)
                {
                    aclList.add(value);
                }
            }
            return aclList;
        }
        return arg0;
    }
}
