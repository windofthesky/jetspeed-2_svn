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
package org.apache.jetspeed.factory;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;

/**
 * InlinePortletResourceBundle implementation which provides the inline title, short-title, and keywords as properties
 * from the bundle. Borrowed and improved upon the one provided by the Pluto container.
 * 
 * @version: $Id$
 */
public class InlinePortletResourceBundle extends ListResourceBundle
{
    private final static String TITLE_KEY = "javax.portlet.title";
    private final static String SHORT_TITLE_KEY = "javax.portlet.short-title";
    private final static String KEYWORDS_KEY = "javax.portlet.keywords";

    private Object[][] contents;
    private ResourceBundle parent;

    public InlinePortletResourceBundle(String title, String shortTitle, String keywords)
    {
        this(title,shortTitle,keywords,null);
    }
    
    public InlinePortletResourceBundle(String title, String shortTitle, String keywords, ResourceBundle parent)
    {
        contents = new Object[][]{ { TITLE_KEY, deriveValue(parent, TITLE_KEY, title) },
                                   { SHORT_TITLE_KEY, deriveValue(parent, SHORT_TITLE_KEY, shortTitle) },
                                   { KEYWORDS_KEY, deriveValue(parent, KEYWORDS_KEY, keywords) }};
        setParent(parent);
    }
    
    private static String deriveValue(ResourceBundle parent, String key, String leadingValue)
    {
        String value = leadingValue;
        if (value == null && parent != null)            
        {
            try
            {
                value = parent.getString(key);
            }
            catch (Exception mre)
            {
            }
        }
        return value != null ? value : "";
    }
    
    protected Object[][] getContents()
    {
        return contents;
    }
    
    public void setParent(ResourceBundle parent)
    {
        this.parent = parent;
        super.setParent(parent);
    }
    
    ResourceBundle getParent()
    {
        return parent;
    }
}
