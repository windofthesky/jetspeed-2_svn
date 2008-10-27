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

import org.apache.jetspeed.om.portlet.DisplayName;
import org.apache.jetspeed.om.portlet.SecurityConstraint;
import org.apache.jetspeed.om.portlet.UserDataConstraint;

/**
 * @version $Id$
 *
 */
public class SecurityConstraintImpl implements SecurityConstraint, Serializable
{
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.SecurityConstraint#addDisplayName(java.lang.String)
     */
    public DisplayName addDisplayName(String lang)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.SecurityConstraint#getDisplayName(java.util.Locale)
     */
    public DisplayName getDisplayName(Locale locale)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.SecurityConstraint#getDisplayNames()
     */
    public List<DisplayName> getDisplayNames()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.portlet.SecurityConstraint#getUserDataConstraint()
     */
    public UserDataConstraint getUserDataConstraint()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.portlet.SecurityConstraint#addPortletName(java.lang.String)
     */
    public void addPortletName(String portletName)
    {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.portlet.SecurityConstraint#getPortletNames()
     */
    public List<String> getPortletNames()
    {
        // TODO Auto-generated method stub
        return null;
    }
}
