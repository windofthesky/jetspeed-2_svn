/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.page.impl;

import java.util.List;

import org.apache.jetspeed.om.common.SecurityConstraints;

/**
 * Content security constraints implementation.
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class ContentSecurityConstraints implements SecurityConstraints
{
    private boolean mutable;
    private String owner;
    private List constraints;
    private List constraintsRefs;
    
    /**
     * Construct new security constraints implementation.
     * 
     * @param mutable mutable flag
     * @param owner owner constraint
     * @param constraints constraints list
     * @param constraintsRefs constraints references list
     */
    public ContentSecurityConstraints(boolean mutable, String owner, List constraints, List constraintsRefs)
    {
        this.mutable = mutable;
        this.owner = owner;
        this.constraints = constraints;
        this.constraintsRefs = constraintsRefs;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#getOwner()
     */
    public String getOwner()
    {
        return owner;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#getSecurityConstraints()
     */
    public List getSecurityConstraints()
    {
        return constraints;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#getSecurityConstraintsRefs()
     */
    public List getSecurityConstraintsRefs()
    {
        return constraintsRefs;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#isEmpty()
     */
    public boolean isEmpty()
    {
        return ((owner == null) && ((constraints == null) || constraints.isEmpty()) && ((constraintsRefs == null) || constraintsRefs.isEmpty()));
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#setOwner(java.lang.String)
     */
    public void setOwner(String owner)
    {
        if (!mutable)
        {
            throw new UnsupportedOperationException("ContentSecurityConstraints.setOwner()");
        }
        this.owner = owner;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#setSecurityConstraints(java.util.List)
     */
    public void setSecurityConstraints(List constraints)
    {
        if (!mutable)
        {
            throw new UnsupportedOperationException("ContentSecurityConstraints.setSecurityConstraints()");
        }
        this.constraints = constraints;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.SecurityConstraints#setSecurityConstraintsRefs(java.util.List)
     */
    public void setSecurityConstraintsRefs(List constraintsRefs)
    {
        if (!mutable)
        {
            throw new UnsupportedOperationException("ContentSecurityConstraints.setSecurityConstraintsRefs()");
        }
        this.constraintsRefs = constraintsRefs;
    }
}
