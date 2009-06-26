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
 * Jetspeed Serialized (JS) SSORemoteUser
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class JSSSOSiteRemoteUser
{
    private String principalName;

    private String principalType;
    
    private String name;
    
    private char[] password;

    public JSSSOSiteRemoteUser()
    {
    }

    public String getPrincipalName()
    {
        return principalName;
    }

    public void setPrincipalName(String principalName)
    {
        this.principalName = principalName;
    }

    public String getPrincipalType()
    {
        return principalType;
    }

    public void setPrincipalType(String principalType)
    {
        this.principalType = principalType;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public char[] getPassword()
    {
        return password;
    }

    public void setPassword(char[] password)
    {
        this.password = password;
    }

    public void setUserCredential(String name, char[] password)
    {
        setName(name);
        setPassword(password);
    }

    /***************************************************************************
     * SERIALIZER
     */
    @SuppressWarnings("unused")
    private static final XMLFormat XML = new XMLFormat(JSSSOSiteRemoteUser.class)
    {
        public void write(Object o, OutputElement xml) throws XMLStreamException
        {
            try
            {
                JSSSOSiteRemoteUser g = (JSSSOSiteRemoteUser) o;

                xml.setAttribute("principalName", g.getPrincipalName());
                xml.setAttribute("principalType", g.getPrincipalType());
                xml.setAttribute("name", g.getName());
                if (g.getPassword() != null)
                {
                    xml.setAttribute("password", new String(g.getPassword()));
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
                JSSSOSiteRemoteUser g = (JSSSOSiteRemoteUser) o;

                g.setPrincipalName(StringEscapeUtils.unescapeHtml(xml.getAttribute("principalName", (String)null)));
                g.setPrincipalType(StringEscapeUtils.unescapeHtml(xml.getAttribute("principalType", (String)null)));
                g.setName(StringEscapeUtils.unescapeHtml(xml.getAttribute("name", (String)null)));
                String passwordString = StringEscapeUtils.unescapeHtml(xml.getAttribute("password", (String)null));
                g.setPassword((passwordString != null) ? passwordString.toCharArray() : null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    };
}
