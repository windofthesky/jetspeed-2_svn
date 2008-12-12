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

import org.apache.commons.lang.StringEscapeUtils;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;


/**
* Jetspeed Serializer - Simple User Roles Wrapper
* <p>
* Wrapper to process XML representation of a set of user roles - used only for binding
* 
* @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
* @version $Id: $
*/
public class JSUserRoles
{
	String roles;
	
	
    public JSUserRoles()
    {
    }
    public JSUserRoles(String s)
	{
		roles = s;
	}
	public String toString()
	{
		return roles;
	}

    
    
    private static final XMLFormat XML = new XMLFormat(JSUserRoles.class)
	{
		public void write(Object oo, OutputElement xml)
		throws XMLStreamException
		{
	        xml.addText(((JSUserRoles)oo).roles); 
	    }
		public void read(InputElement xml, Object oo)
		throws XMLStreamException
		{
	        ((JSUserRoles)oo).roles = StringEscapeUtils.unescapeHtml(xml.getText().toString());
	    }
	};
}
