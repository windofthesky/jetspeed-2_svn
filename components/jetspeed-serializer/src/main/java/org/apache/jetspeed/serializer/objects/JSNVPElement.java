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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javolution.xml.XMLFormat;
import javolution.xml.sax.Attributes;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;

public class JSNVPElement
{
    private static final String READONLY = "readonly";
    private static final String NULLVALUE = "nullValue";
    private static final String NAME = "name";
    private static final String VALUE = "value";
    
    private String key;

    private String value;
    
    private String [] values;

    private Map<String, String> attributes = new LinkedHashMap<String, String>();

    public JSNVPElement()
    {
    }

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
            xml.setAttribute(READONLY, e.isReadOnly() ? "true" : "false");
            for (Map.Entry<String,String> entry : e.attributes.entrySet())
            {
                if (entry.getKey().equals(READONLY) || entry.getKey().equals(NULLVALUE))
                {
                    ; // skip
                }
                else
                {
                    xml.setAttribute(entry.getKey(), entry.getValue());
                }
            }
            xml.setAttribute(NAME, e.key);
            if (e.isNullValue())
            {
                xml.setAttribute(NULLVALUE, "true");
            }
            else if (e.getValue() != null)
            {
                xml.setAttribute(VALUE, e.value);
            }            
            else if (e.values != null)
            {
                if (e.values.length == 1)
                {
                    xml.setAttribute(VALUE, e.values[0]);
                }
                else
                {
                    for(int count=0;count<e.values.length;count++)
                    {
                        xml.add(e.values[count], VALUE, String.class);
                    }
                }
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSNVPElement g = (JSNVPElement) o;
                Attributes attribs = xml.getAttributes();

                for (int i = 0, len = attribs.getLength(); i < len; i++)
                {
                    try
                    {
                        String _key = StringEscapeUtils.unescapeHtml(attribs.getLocalName(i).toString());
                        String _value = StringEscapeUtils.unescapeHtml(attribs.getValue(i).toString());
                        g.setAttribute(_key,_value);
                    } catch (Exception e)
                    {
                        /**
                         * while annoying invalid entries in the file should be
                         * just disregarded
                         */
                        e.printStackTrace();
                    }
                }
                
                g.key = g.getAttributes().get("name");
                if (g.key == null)
                {
                    g.key = StringEscapeUtils.unescapeHtml((String) xml.get("name",String.class));
                }                
                
                if (g.key != null && !g.isNullValue())
                {
                    g.value = g.getAttributes().get("value");
                    if (g.value == null)
                    {
                        ArrayList<String> strings = new ArrayList<String>();
                        while (xml.hasNext())
                        {
                            strings.add(StringEscapeUtils.unescapeHtml((String) xml.get("value", String.class)));
                        }
                        if (strings.size() > 1)
                        {
                            g.values = strings.toArray(new String[strings.size()]);
                        }
                        else
                        {
                            g.value = strings.get(0);
                        }
                    }
                }
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
    
    public String [] getValues()
    {
        return values;
    }

    public void setValue(String value)
    {
        this.value = value;
        this.values = null;
        setNullValue(value == null);
    }
    
    public void setValues(String [] values)
    {
        this.value = null;
        this.values = values;
        setNullValue(values == null);
    }
    /**
     * @return the isReadOnly
     */
    public boolean isReadOnly()
    {
        return attributes.get(READONLY) != null;
    }
    /**
     * @param isReadOnly the isReadOnly to set
     */
    public void setReadOnly(boolean isReadOnly)
    {
        if (isReadOnly)
        {
            attributes.put(READONLY, "true");
        }
        else
        {
            attributes.remove(READONLY);
        }
    }
    
    /**
     * @return the isNullValue
     */
    public boolean isNullValue()
    {
        return attributes.get(NULLVALUE) != null;
    }
    /**
     * @param isNullValue the isNullValue to set
     */
    public void setNullValue(boolean isNullValue)
    {
        if (isNullValue)
        {
            attributes.put(NULLVALUE, "true");
        }
        else
        {
            attributes.remove(NULLVALUE);
        }
    }

    public Map<String, String> getAttributes()
    {
        return attributes;
    }

    public String getAttribute(String key)
    {
        return attributes.get(key);
    }
    
    public void setAttribute(String key, String value)
    {
        if (key != null && value != null && key.trim().length() > 0)
        {
            if (READONLY.equals(key))
            {
                setReadOnly(Boolean.parseBoolean(value));
            }
            else if (NULLVALUE.equals(key))
            {
                setNullValue(Boolean.parseBoolean(value));
            }
            else
            {
                attributes.put(key,value);
            }
        }
    }
    
    public void removeAttribute(String key)
    {
        attributes.remove(key);
    }
}
