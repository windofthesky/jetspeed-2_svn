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
 *     "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" or
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

package org.apache.jetspeed.om.page.psml;

import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;

import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Property;

/**
 * @version $Id$
 */
public class FragmentImpl extends AbstractBaseElement implements Fragment, java.io.Serializable
{

    private String type = null;

    private String state = null;

    private String decorator = null;

    private String skin = null;

    private Map properties = new Hashtable();

    private List fragments = new ArrayList();

    private transient List psmlProperties = new Vector();

    public FragmentImpl()
    {}

    public String getType()
    {
        return this.type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getState()
    {
        return this.state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getDecorator()
    {
        return this.decorator;
    }

    public void setDecorator(String decoratorName)
    {
        this.decorator = decoratorName;
    }

    public String getSkin()
    {
        return this.skin;
    }

    public void setSkin(String skin)
    {
        this.skin = skin;
    }

    public boolean isReference()
    {
        return false;
    }

    public List getFragments()
    {
        return this.fragments;
    }

    public Set getLayoutProperties()
    {
        return this.properties.keySet();
    }

    public Map getProperties(String layoutName)
    {
        return (Map)this.properties.get(layoutName);
    }

    public List getProperties()
    {
        synchronized (properties)
        {
            this.psmlProperties.clear();

            Iterator i = this.properties.keySet().iterator();

            while(i.hasNext())
            {
                String layout = (String)i.next();
                Map lprop = (Map)this.properties.get(layout);

                Iterator i2 = lprop.keySet().iterator();

                while(i2.hasNext())
                {
                    String name = (String)i.next();
                    String value = (String)lprop.get(name);

                    Property property = new PropertyImpl();
                    property.setLayout(layout);
                    property.setName(name);
                    property.setValue(value);

                    this.psmlProperties.add(property);
                }
            }
        }

        return this.psmlProperties;
    }

    public void setProperties(List psmlProperties)
    {
        synchronized (properties)
        {
            this.psmlProperties = psmlProperties;

            Iterator i = this.psmlProperties.iterator();

            while(i.hasNext())
            {
                Property prop = (Property)i.next();

                String name = prop.getName();
                String value = prop.getValue();

                if ((name == null)||(value == null))
                {
                    continue;
                }

                String layout = prop.getLayout();
                if (layout == null)
                {
                    layout = "";
                }

                Map lprop = (Map)this.properties.get(layout);
                if (lprop == null)
                {
                    lprop = new Hashtable();
                    this.properties.put(layout,lprop);
                }

                lprop.put(name,value);
            }
        }
    }

    public void setFragments(List fragements)
    {
        this.fragments = fragments;
    }

    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();

        // TBD: copy the properties and fragment structures

        return cloned;

    }   // clone

}
