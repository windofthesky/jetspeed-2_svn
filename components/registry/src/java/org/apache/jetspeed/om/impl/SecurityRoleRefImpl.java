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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.SecurityRoleRefComposite;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.jetspeed.util.JetspeedLocale;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.DescriptionSet;
import org.apache.pluto.om.common.SecurityRoleRef;

/**
 * 
 * SecurityRoleRefImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class SecurityRoleRefImpl implements SecurityRoleRefComposite, Serializable
{

    protected long id;
    protected long portletId;
    private String link;
    private String name;
    private Collection descriptions;
    private DescriptionSetImpl descCollWrapper = new DescriptionSetImpl(DescriptionImpl.TYPE_SEC_ROLE_REF);

    private static final Log log = LogFactory.getLog(SecurityRoleRefImpl.class);

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRef#getRoleLink()
     */
    public String getRoleLink()
    {
        return link;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRef#getRoleName()
     */
    public String getRoleName()
    {
        return name;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRefCtrl#setRoleLink(java.lang.String)
     */
    public void setRoleLink(String value)
    {
        this.link = value;
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRefCtrl#setRoleName(java.lang.String)
     */
    public void setRoleName(String name)
    {
        this.name = name;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof SecurityRoleRef)
        {
            SecurityRoleRef aRef = (SecurityRoleRef) obj;
            return this.getRoleName().equals(aRef.getRoleName());
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {

        HashCodeBuilder hasher = new HashCodeBuilder(21, 81);
        hasher.append(name);
        return hasher.toHashCode();
    }

    /**
     * @see org.apache.pluto.om.common.SecurityRoleRef#getDescription(java.util.Locale)
     */
    public Description getDescription(Locale arg0)
    {
        if (descriptions != null)
        {
            descCollWrapper.setInnerCollection(descriptions);
            return descCollWrapper.get(arg0);
        }
        return null;
    }

    /**
     * @see org.apache.jetspeed.om.common.SecurityRoleRefComposite#addDescription(org.apache.pluto.om.common.Description)
     */
    public void addDescription(Description description)
    {
        if (descriptions == null)
        {
            descriptions = new ArrayList();
        }
        descCollWrapper.setInnerCollection(descriptions);
        descCollWrapper.addDescription(description);
    }

    /**
     * @see org.apache.jetspeed.om.common.SecurityRoleRefComposite#setDescriptionSet(org.apache.pluto.om.common.DescriptionSet)
     */
    public void setDescriptionSet(DescriptionSet descriptions)
    {
        this.descriptions = ((DescriptionSetImpl) descriptions).getInnerCollection();

    }

    /**
     * We should be using one of the more locale-specific methods.
     * 
     * 
     * @see org.apache.pluto.om.common.SecurityRoleRefCtrl#setDescription(java.lang.String)
     * @param arg0
     */
    public void setDescription(String arg0)
    {
        MutableDescription descObj =new SecurityRoleRefDescriptionImpl();
            
        
        descObj.setLocale(JetspeedLocale.getDefaultLocale());
        descObj.setDescription(arg0);

    }

}
