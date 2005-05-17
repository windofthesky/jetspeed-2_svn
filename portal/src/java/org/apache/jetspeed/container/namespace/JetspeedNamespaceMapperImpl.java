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

import org.apache.pluto.om.common.ObjectID;


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
        if ( this.prefix == null )
        {
            this.prefix = DEFAULT_PREFIX;
        }        
    }
    
    public JetspeedNamespaceMapperImpl()
    {
        this(null);
    }
    
    public String getPrefix()
    {
        return prefix;
    }
    
    public String encode(String ns, String name)
    {
        StringBuffer buffer = new StringBuffer(50);
        buffer.append(prefix);
        buffer.append(ns);
        buffer.append('_');
        buffer.append(name);
        return buffer.toString();
    }

    public String encode(String ns1, String ns2, String name)
    {
        StringBuffer buffer = new StringBuffer(50);
        buffer.append(prefix);
        buffer.append(ns1);
        buffer.append('_');
        buffer.append(ns2);
        buffer.append('_');
        buffer.append(name);
        return buffer.toString();
    }

    public String decode(String ns, String name)
    {
        if (!name.startsWith(prefix)) return null;
        StringBuffer buffer = new StringBuffer(50);
        buffer.append(prefix);
        buffer.append(ns);
        buffer.append('_');
        if (!name.startsWith(buffer.toString())) return null;
        return name.substring(buffer.length());
    }

    public String encode(long id, String name)
    {
        return encode(new Long(id).toString(),name);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.util.NamespaceMapper#encode(org.apache.pluto.om.common.ObjectID, java.lang.String)
     */
    public String encode(ObjectID ns, String name)
    {
        return encode(ns.toString(),name);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.util.NamespaceMapper#encode(org.apache.pluto.om.common.ObjectID, org.apache.pluto.om.common.ObjectID, java.lang.String)
     */
    public String encode(ObjectID ns1, ObjectID ns2, String name)
    {
        return encode(ns1.toString(),ns2.toString(),name);
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.util.NamespaceMapper#decode(org.apache.pluto.om.common.ObjectID, java.lang.String)
     */
    public String decode(ObjectID ns, String name)
    {
        return decode(ns.toString(),name);
    }

}
