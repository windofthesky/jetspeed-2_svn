/*
 * Copyright 2008 The Apache Software Foundation
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
package org.apache.jetspeed.om.portlet.impl;

import javax.xml.namespace.QName;

import org.apache.jetspeed.om.portlet.PortletQName;


public class PortletQNameImpl implements PortletQName
{
    private String localPart;
    private String prefix;
    private String namespace;

    public PortletQNameImpl()
    {}
    
    public PortletQNameImpl(QName qname)
    {
        this.namespace = qname.getNamespaceURI();
        if (this.namespace != null && this.namespace.equals(""))
            this.namespace = null;
        this.prefix = qname.getPrefix();
        if (this.prefix != null && this.prefix.equals(""))
            this.prefix = null;
        this.localPart = qname.getLocalPart();        
    }
    
    public String getLocalPart()
    {
        return this.localPart;
    }

    public String getNamespace()
    {
        return this.namespace;
    }

    public String getPrefix()
    {
        return this.prefix;
    }

    public QName getQName()
    {
        if (namespace == null)
        {
            return new QName(localPart);
        }
        else if (prefix == null)
        {
            return new QName(namespace, localPart);
        }
        else
        {
            return new QName(namespace, localPart, prefix);
        }
    }
    
    public boolean equals(Object qname)
    {
        return (this.toString().equals(qname.toString()));
    }
    
    public String toString()
    {
        return ((this.getNamespace() == null) ? "" : this.getNamespace() + "//:") + 
               ((this.getPrefix() == null) ? "" : this.getPrefix() + ":") +
               ((this.getLocalPart() == null) ? "" : this.getLocalPart());
    }

}
