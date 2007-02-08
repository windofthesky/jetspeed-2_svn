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
import java.util.prefs.Preferences;

import javolution.xml.XMLFormat;
import javolution.xml.sax.Attributes;
import javolution.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.pluto.om.common.Preference;

public class JSNVPElements
{

    private HashMap myMap = new HashMap();

    public int size()
    {
    	return myMap.size();
    	
    }
    public JSNVPElements()
    {
    }
    
 
    public HashMap getMyMap()
	{
		return myMap;
	}

    public void add(String key, String value)
    {
    	myMap.put(key,value);
    }

	/**
     * @param arg0
     */
    public JSNVPElements(Preferences preferences)
    {
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
    private static final XMLFormat XML = new XMLFormat(JSNVPElements.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSNVPElements g = (JSNVPElements) o;
                Iterator _it = g.myMap.keySet().iterator();
                while (_it.hasNext())
                {
                    String _key = (String) _it.next();
                    JSNVPElement elem = new JSNVPElement(_key,(String)g.myMap.get(_key));
                    xml.add(elem,"preference",JSNVPElement.class);
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
                JSNVPElements g = (JSNVPElements) o;
                Object o1 = null;
                

				while (xml.hasNext())
				{
					JSNVPElement elem = (JSNVPElement)xml.get("preference",JSNVPElement.class);
                    g.myMap.put(elem.getKey(), elem.getValue());
				}
            } catch (Exception e)
            {
                /**
                 * while annoying invalid entries in the file should be
                 * just disregarded
                 */
                e.printStackTrace();
            }
        }
    };

    
    
}
