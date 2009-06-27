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
package org.apache.jetspeed.serializer.objects;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Jetspeed Compatibility Serialized (JS) EntityPreference
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class JSEntityPreferenceCompat
{
    private String name;
    private JSNVPElements preferences;


    public JSEntityPreferenceCompat()
    {
    }
 
    /**
     * @return Returns the preferences.
     */
    public JSNVPElements getPreferences()
    {
        return preferences;
    }

    /**
     * @param preferences
     *            The preferences to set.
     */
    public void setPreferences(JSNVPElements preferences)
    {
        this.preferences = preferences;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSEntityPreferenceCompat.class)
    {
        public void write(Object o, OutputElement xml) throws XMLStreamException
        {
            try
            {
                JSEntityPreferenceCompat g = (JSEntityPreferenceCompat) o;
                xml.setAttribute("name", ((g.getName() != null) ? g.getName() : "-"));

                if ((g.preferences != null) && (g.preferences.size()>0))
                {
                    xml.add(g.preferences);
                }

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSEntityPreferenceCompat g = (JSEntityPreferenceCompat) o;
                g.name = StringEscapeUtils.unescapeHtml(xml.getAttribute("name", "-"));
                
                Object o1 = null;
                while (xml.hasNext())
                {
                    o1 = xml.getNext();
                    
                    if (o1 instanceof JSNVPElements)
                    {
                        g.preferences  = (JSNVPElements) o1;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
}
