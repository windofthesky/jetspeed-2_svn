/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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


/**
 * Name space mapping for creating named attributes.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class NamespaceMapper
{
    public static final String PREFIX = "js_";

    public static String encode(String ns, String name)
    {
        StringBuffer buffer = new StringBuffer(50);
        buffer.append(PREFIX);
        buffer.append(ns);
        buffer.append('_');
        buffer.append(name);
        return buffer.toString();
    }

    public static String encode(long id, String name)
    {
        StringBuffer buffer = new StringBuffer(50);
        buffer.append(PREFIX);
        buffer.append(new Long(id).toString());
        buffer.append('_');
        buffer.append(name);
        return buffer.toString();
    }

    public static String encode(String ns1, String ns2, String name)
    {
        StringBuffer buffer = new StringBuffer(50);
        buffer.append(PREFIX);
        buffer.append(ns1);
        buffer.append('_');
        buffer.append(ns2);
        buffer.append('_');
        buffer.append(name);
        return buffer.toString();
    }

    public static String decode(String ns, String name)
    {
        if (!name.startsWith(PREFIX)) return null;
        StringBuffer buffer = new StringBuffer(50);
        buffer.append(PREFIX);
        buffer.append(ns);
        buffer.append('_');
        if (!name.startsWith(buffer.toString())) return null;
        return name.substring(buffer.length());
    }

}