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

public class JSNVPElement
{

    private String key;

    private String value;

    private boolean nullValue;

    public JSNVPElement()
    {
    };

    public JSNVPElement(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    private static final XMLFormat XML = new XMLFormat(JSNVPElement.class)
    {

        @SuppressWarnings("unused")
        public boolean isReferencable()
        {
            return false; // Always manipulates by value.
        }

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            JSNVPElement e = (JSNVPElement) o;
            // xml.add((String) g.get(_key), _key, String.class);

            xml.add(e.key, "name", String.class);
            if (e.nullValue)
            {
                xml.setAttribute("nullValue", true);
            } else
            {
                xml.add(e.value, "value", String.class);
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSNVPElement g = (JSNVPElement) o;
                g.key = StringEscapeUtils.unescapeHtml((String) xml.get("name",
                        String.class));
                g.value = StringEscapeUtils.unescapeHtml((String) xml.get(
                        "value", String.class));
                // DST: not sure what Ate was doing here, but it breaks things
                // we also need to add upport for multiple values and 'readonly', but its not clear what this null value does for us
                //                g.nullValue = xml.getAttribute("nullValue", false);                
                //if (!g.nullValue)
                //{
                // g.value =
                // StringEscapeUtils.unescapeHtml((String)xml.get("value",
                // String.class));
                // }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
        nullValue = value == null;
    }
}
