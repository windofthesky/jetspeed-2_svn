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

package org.apache.jetspeed.om.portlet.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.xml.namespace.QName;

import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.PublicRenderParameter;

/**
 * @version $Id$
 *
 */
public class PublicRenderParameterImpl implements PublicRenderParameter, Serializable
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.PublicRenderParameter#addDescription(java.lang.String)
     */
    public Description addDescription(String lang)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.PublicRenderParameter#getDescription(java.util.Locale)
     */
    public Description getDescription(Locale locale)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.PublicRenderParameter#getDescriptions()
     */
    public List<Description> getDescriptions()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.portlet.PublicRenderParameter#addAlias(javax.xml.namespace.QName)
     */
    public void addAlias(QName alias)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.portlet.PublicRenderParameter#getAliases()
     */
    public List<QName> getAliases()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.portlet.PublicRenderParameter#getIdentifier()
     */
    public String getIdentifier()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.portlet.PublicRenderParameter#getName()
     */
    public String getName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.portlet.PublicRenderParameter#getQName()
     */
    public QName getQName()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
