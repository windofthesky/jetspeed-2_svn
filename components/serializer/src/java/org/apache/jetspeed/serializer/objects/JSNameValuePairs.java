/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.apache.jetspeed.serializer.objects;

/**
 * Serialized Name Value Pairs <info> <name>user.first.name</name> <value>Paul</value>
 * </info>
 * 
 * @author <a href="mailto:hajo@bluesunrsie.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.prefs.Preferences;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;
import javolution.xml.sax.Attributes;

public class JSNameValuePairs
{

    private HashMap myMap = null;

    public JSNameValuePairs()
    {
    }
    
 
    /**
     * @param arg0
     */
    public JSNameValuePairs(Preferences preferences)
    {
        myMap = new HashMap();
        try
        {
            String[] strings = preferences.keys();
            if ((strings != null) && (strings.length > 0))
            {
                int i = strings.length;
                for (int j = 0; j < i; j++)
                    myMap.put(strings[j], preferences.get(strings[j], "?????"));
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSNameValuePairs.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSNameValuePairs g = (JSNameValuePairs) o;
                Iterator _it = g.myMap.keySet().iterator();
                while (_it.hasNext())
                {
                    String _key = (String) _it.next();
                    // xml.add((String) g.get(_key), _key, String.class);
                    xml.setAttribute(_key, (String) g.myMap.get(_key));
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {

            try
            {
                JSNameValuePairs g = (JSNameValuePairs) o;
                Attributes attribs = xml.getAttributes();
                int len = attribs.getLength();

                for (int i = 0; i < len; i++)
                {
                    try
                    {
                        String _key = attribs.getLocalName(i).toString();
                        String _value = attribs.getValue(i).toString();
                        g.myMap.put(_key, _value);
                    } catch (Exception e)
                    {
                        /**
                         * while annoying invalid entries in the file should be
                         * just disregarded
                         */
                        e.printStackTrace();
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };

}
