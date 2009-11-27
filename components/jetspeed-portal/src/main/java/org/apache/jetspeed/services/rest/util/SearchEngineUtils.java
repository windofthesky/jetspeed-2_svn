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

import java.util.Collection;
import java.util.Map;

import org.apache.jetspeed.search.ParsedObject;

/**
 * SearchEngineUtils
 * 
 * @version $Id$
 */
public class SearchEngineUtils
{
    
    private SearchEngineUtils()
    {
        
    }
    
    public static Object getField(final ParsedObject parsedObject, final String fieldName, final Object defaultValue)
    {
        Map fields = parsedObject.getFields();
        
        if (fields == null)
        {
            return null;
        }
        
        Object field = fields.get(fieldName);
        
        if (field == null)
        {
            return defaultValue;
        }
        
        return field;
    }
    
    public static String getFieldAsString(final ParsedObject parsedObject, final String fieldName, final String defaultValue)
    {
        Object field = getField(parsedObject, fieldName, defaultValue);
        
        if (field instanceof Collection)
        {
            return (String) ((Collection) field).iterator().next();
        }
        else
        {
            return (String) field;
        }
    }
    
    public static String getPortletUniqueName(final ParsedObject parsedObject)
    {
        if (!"portlet".equals(parsedObject.getType()))
        {
            return null;
        }
        
        String portletName = getFieldAsString(parsedObject, "ID", null);
        
        if (portletName == null)
        {
            return null;
        }
        
        String applicationName = getFieldAsString(parsedObject, "portlet_application", null);
        
        if (applicationName == null)
        {
            return null;
        }
        
        return applicationName + "::" + portletName;
    }
}
