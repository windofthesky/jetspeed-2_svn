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

package org.apache.jetspeed.om.impl;

import java.io.Serializable;
import java.util.Locale;

import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.MutableDescriptionSet;
import org.apache.jetspeed.om.common.ParameterComposite;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.Parameter;

/**
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class ParameterImpl implements ParameterComposite, Serializable
{

    private String name;
    private String value;
    private String description;

    protected long parameterId;

    protected long parentId;

    private MutableDescriptionSet descriptions;

    /**
     * @see org.apache.pluto.om.common.Parameter#getName()
     */
    public String getName()
    {
        return name;
    }

    /**
     * @see org.apache.pluto.om.common.Parameter#getValue()
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @see org.apache.pluto.om.common.ParameterCtrl#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;

    }

    /**
     * @see org.apache.pluto.om.common.ParameterCtrl#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        this.value = value;

    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Parameter)
        {
            Parameter p = (Parameter) obj;
            return this.getName().equals(p.getName());
        }

        return false;

    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hash = new HashCodeBuilder(17, 77);
        return hash.append(name).toHashCode();
    }

    /**
     * @see org.apache.pluto.om.common.Parameter#getDescription(java.util.Locale)
     */
    public Description getDescription(Locale arg0)
    {
        if (descriptions != null)
        {
            return descriptions.get(arg0);
        }
        return null;

    }

    /**
     * @see org.apache.pluto.om.common.ParameterCtrl#setDescriptionSet(org.apache.pluto.om.common.DescriptionSet)
     */
    public void setDescriptionSet(DescriptionSet arg0)
    {
        this.descriptions = (MutableDescriptionSet) arg0;
    }

    /**
     * @see org.apache.jetspeed.om.common.ParameterComposite#addDescription(java.util.Locale, java.lang.String)
     */
    public void addDescription(Locale locale, String desc)
    {
        if (descriptions == null)
        {
            descriptions = new DescriptionSetImpl(MutableDescription.TYPE_PARAMETER);
        }

        descriptions.addDescription(new DescriptionImpl(locale, desc, MutableDescription.TYPE_PARAMETER));

    }

    public void addDescription(Description desc)
    {
        if (descriptions == null)
        {
            descriptions = new DescriptionSetImpl(MutableDescription.TYPE_PARAMETER);
        }

        descriptions.addDescription(desc);

    }

    /**
     * Remove when Castor is mapped correctly
     * 
     * @deprecated
     * @param desc
     */
    public void setDescription(String desc)
    {
        System.out.println("Setting description..." + desc);
        addDescription(Locale.getDefault(), desc);
        System.out.println("Description Set " + desc);
    }

    /**
     *  Remove when Castor is mapped correctly
     * @deprecated
     * @return
     */
    public String getDescription()
    {

        Description desc = getDescription(Locale.getDefault());

        if (desc != null)
        {
            return desc.getDescription();
        }

        return null;
    }

}
