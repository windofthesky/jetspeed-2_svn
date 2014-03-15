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
package org.apache.jetspeed.om.page.impl;

import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.page.document.impl.DocumentImpl;

import java.util.Collection;

/**
 * LinkImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class LinkImpl extends DocumentImpl implements Link
{
    private String skin;
    private String target;
    private String url;

    public LinkImpl()
    {
        super(new LinkSecurityConstraintsImpl());
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.impl.NodeImpl#newPageMetadata(java.util.Collection)
     */
    public PageMetadataImpl newPageMetadata(Collection<LocalizedField> fields)
    {
        PageMetadataImpl pageMetadata = new PageMetadataImpl(LinkMetadataLocalizedFieldImpl.class);
        pageMetadata.setFields(fields);
        return pageMetadata;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.impl.BaseElementImpl#grantViewActionAccess()
     */
    public boolean grantViewActionAccess()
    {
        // always allow links that reference absolute urls since these
        // are probably not a security related concern but rather
        // should always be viewable, (subject to folder access)
        String hrefUrl = getUrl();
        return ((hrefUrl != null) && (hrefUrl.startsWith("http://") || hrefUrl.startsWith("https://")));
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getUrl()
     */
    public String getUrl()
    {
        return url;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Link#setUrl(java.lang.String)
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Link#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Link#setSkin(java.lang.String)
     */
    public void setSkin(String skin)
    {
        this.skin = skin;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Link#getTarget()
     */
    public String getTarget()
    {
        return target;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Link#setTarget(java.lang.String)
     */
    public void setTarget(String target)
    {
        this.target = target;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.page.document.Node#getType()
     */
    public String getType()
    {
        return DOCUMENT_TYPE;
    }
}
