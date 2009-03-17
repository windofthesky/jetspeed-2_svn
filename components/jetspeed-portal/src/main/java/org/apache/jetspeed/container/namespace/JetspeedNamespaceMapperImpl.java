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
package org.apache.jetspeed.container.namespace;

import org.apache.pluto.container.PortletWindowID;


/**
 * Jetspeed implementation of Name space mapping for creating named attributes.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedNamespaceMapperImpl implements JetspeedNamespaceMapper
{
    private String prefix;
    
    public JetspeedNamespaceMapperImpl(String prefix)
    {
        this.prefix = prefix;
        if (this.prefix == null)
        {
            this.prefix = DEFAULT_PREFIX;
        }        
    }
    
    public JetspeedNamespaceMapperImpl()
    {
        this(null);
    }
    
    /*
     * Public Pluto APIs, encode, decode on PortletWindowID, String
     */
    public String encode(PortletWindowID namespace, String name)
    {
        return encode(namespace.toString(), name);        
    }
    
    public String decode(PortletWindowID namespace, String name)
    {
        return decode(namespace.toString(), name);
    }
    
    /*
     * Jetspeed API, getPrefix
     */    
    public String getPrefix()
    {
        return prefix;
    }
    
    /*
     * Implementation
     */
    protected String encode(String ns, String name)
    {
        return join(prefix, ns, "_", name, null, null);
    }

    public String encode(String ns1, String ns2, String name)
    {
        return join(prefix, ns1, "_", ns2, "_", name);
    }

    public String decode(String ns, String name)
    {
        if (!name.startsWith(prefix))
            return null;
        String tmp = join(prefix, ns, "_", null, null, null);
        if (!name.startsWith(tmp))
            return null;
        return name.substring(tmp.length());
    }
    
    protected static String join(String s1, String s2, String s3, String s4,
            String s5, String s6)
    {
        int len = 0;
        if (s1 != null)
        {
            len += s1.length();
            if (s2 != null)
            {
                len += s2.length();
                if (s3 != null)
                {
                    len += s3.length();
                    if (s4 != null)
                    {
                        len += s4.length();
                        if (s5 != null)
                        {
                            len += s5.length();
                            if (s6 != null)
                            {
                                len += s6.length();
                            }
                        }
                    }
                }
            }
        }
        char[] buffer = new char[len];
        int index = 0;
        if (s1 != null)
        {
            len = s1.length();
            s1.getChars(0, len, buffer, index);
            index += len;
            if (s2 != null)
            {
                len = s2.length();
                s2.getChars(0, len, buffer, index);
                index += len;
                if (s3 != null)
                {
                    len = s3.length();
                    s3.getChars(0, len, buffer, index);
                    index += len;
                    if (s4 != null)
                    {
                        len = s4.length();
                        s4.getChars(0, len, buffer, index);
                        index += len;
                        if (s5 != null)
                        {
                            len = s5.length();
                            s5.getChars(0, len, buffer, index);
                            index += len;
                            if (s6 != null)
                            {
                                len = s6.length();
                                s6.getChars(0, len, buffer, index);
                            }
                        }
                    }
                }
            }
        }
        return new String(buffer);
    }
}
