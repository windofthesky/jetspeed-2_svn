/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.jetspeed.om.impl.AbstractSupportSet;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;

/**
 * 
 * PortletDefinitionListImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class PortletDefinitionListImpl extends AbstractSupportSet implements PortletDefinitionList, Serializable
{
    /** Used to build a quick lookup reference */
    private HashMap portletDefinitionlocator = new HashMap();
    private HashMap portletByName = new HashMap();

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionList#iterator()
     */
    public Iterator iterator()
    {
        return super.iterator();
    }

    /**
     * @see org.apache.pluto.om.portlet.PortletDefinitionList#get(org.apache.pluto.om.common.ObjectID)
     */
    public PortletDefinition get(ObjectID id)
    {
        return (PortletDefinition) portletDefinitionlocator.get(id);
    }

    /**
     * Retrieves a <code>PortletDefinition</code> from this 
     * collection by the PortletDefinitions proper name
     * @param name Proper name of PortletDefinition to locate.
     * @return PortletDefinition matching <code>name</code> or <code>null</code>
     * if no PortletDefinition within this PortletApplication has that name.
     */
    public PortletDefinition get(String name)
    {
        return (PortletDefinition) portletByName.get(name);
    }

    /**
     * @see java.util.Collection#add(java.lang.Object)
     */
    public boolean add(Object o)
    {
        PortletDefinition pd = (PortletDefinition) o;
        portletDefinitionlocator.put(pd.getId(), pd);
        portletByName.put(pd.getName(), pd);
        return super.add(pd);
    }

    /**
     * @see java.util.Collection#remove(java.lang.Object)
     */
    public boolean remove(Object o)
    {
        PortletDefinition pd = (PortletDefinition) o;
        portletDefinitionlocator.remove(pd.getName());
        portletByName.remove(pd.getName());
        return super.remove(pd);
    }

}
