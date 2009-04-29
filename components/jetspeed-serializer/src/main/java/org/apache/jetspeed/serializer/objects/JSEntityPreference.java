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
 * Jetspeed Serialized (JS) User
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JSEntityPreference
{

    private String principalName;
    private JSNVPElements preferences = null;


    public JSEntityPreference()
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


    /***************************************************************************
     * SERIALIZER
     */
    private static final XMLFormat XML = new XMLFormat(JSEntityPreference.class)
    {

        public void write(Object o, OutputElement xml)
                throws XMLStreamException
        {
            try
            {
                JSEntityPreference g = (JSEntityPreference) o;
                String s = g.getPrincapalName();
                if ((s == null) || (s.length() == 0)) s = "no-principal";
                xml.setAttribute("principal-name", s);
                if ((g.preferences != null) && (g.preferences.size()>0))
                	xml.add(g.preferences);

            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        public void read(InputElement xml, Object o)
        {
            try
            {
                JSEntityPreference g = (JSEntityPreference) o;
                g.principalName = StringEscapeUtils.unescapeHtml(xml.getAttribute("principal-name", "-"));
                
                
                Object o1 = null;
 

				while (xml.hasNext())
				{
					o1 = xml.getNext(); // mime
					
		                        if (o1 instanceof JSNVPElements)
		                        	g.preferences  = (JSNVPElements) o1;
                }
                
 
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    };


    /**
     * @return the princapalName
     */
    public String getPrincapalName()
    {
        return principalName;
    }

    /**
     * @param princapalName the princapalName to set
     */
    public void setPrincapalName(String princapalName)
    {
        this.principalName = princapalName;
    }


}