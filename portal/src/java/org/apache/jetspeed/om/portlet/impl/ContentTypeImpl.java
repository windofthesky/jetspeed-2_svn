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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import javax.portlet.PortletMode;

import org.apache.jetspeed.om.common.portlet.ContentTypeComposite;
import org.apache.jetspeed.util.HashCodeBuilder;
import org.apache.pluto.om.portlet.ContentType;

/**
 * 
 * ContentTypeImpl
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class ContentTypeImpl implements ContentTypeComposite, Serializable
{

    private String contentType;
    protected Collection portletModes; 
    /**
     *  field that represents a FK relationship to the parent portlet.
     * Required by some O/R tools like OJB.
     */
    protected long portletId;

    protected long contentTypeId;

    public ContentTypeImpl()
    {
        portletModes = new HashSet();
    }

    /**
     * @see org.apache.pluto.om.portlet.ContentType#getContentType()
     */
    public String getContentType()
    {
        return contentType;
    }

    /**
     * @see org.apache.pluto.om.portlet.ContentType#getPortletModes()
     */
    public Iterator getPortletModes()
    {
        return portletModes.iterator();
    }

    /**
     * @see org.apache.pluto.om.portlet.ContentTypeCtrl#setContentType(java.lang.String)
     */
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof ContentType)
        {
            ContentType cType = (ContentType) obj;
            return this.getContentType().equals(cType.getContentType());
        }

        return false;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        HashCodeBuilder hasher = new HashCodeBuilder(27, 87);
        hasher.append(contentType);
        return hasher.toHashCode();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.ContentTypeComposite#addPortletMode(javax.portlet.PortletMode)
     */
    public void addPortletMode(PortletMode mode)
    {
        portletModes.add(mode);

    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.ContentTypeComposite#setModes(java.util.Collection)
     */
    public void setPortletModes(Collection modes)
    {
        portletModes = modes;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.ContentTypeComposite#supportsPortletMode(javax.portlet.PortletMode)
     */
    public boolean supportsPortletMode(PortletMode mode)
    {
        return portletModes.contains(mode);
    }

}
