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

import java.util.Comparator;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;


public class JSSecurityAttributes extends JSNVPElements
{
    private static final Comparator securityAttrComparator = new Comparator<JSNVPElement>()
    {
        public int compare(JSNVPElement o1, JSNVPElement o2)
        {
            int result = 0;
            String o1Category = o1.getAttribute("category");
            String o2Category = o2.getAttribute("category");
            if (o1Category != null && o2Category != null)
            {
                result = o1Category.compareTo(o2Category);
            }
            if (result == 0)
            {
                result = o1.getKey().compareTo(o2.getKey());
            }
            return result;
        }
    };
    
    public JSSecurityAttributes()
    {
        super("SecurityAttribute", securityAttrComparator);
    }
    
    /***************************************************************************
     * SERIALIZER
     */
    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSSecurityAttributes.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSSecurityAttributes g = (JSSecurityAttributes) o;
                
                for (JSNVPElement element : g.getValues())
                {
                    xml.add(element, g.getItemElementName(), JSNVPElement.class);
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
                JSSecurityAttributes g = (JSSecurityAttributes) o;
                
                while (xml.hasNext())
                {
                    JSNVPElement elem = (JSNVPElement)xml.get(g.getItemElementName(), JSNVPElement.class);
                    g.add(elem);
                }
            } 
            catch (Exception e)
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
