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

    public Collection getPortletModesCollection()
    {
        return portletModes;
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

    public void addPortletMode(String mode)
    {
        portletModes.add(new PortletMode(mode));

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
