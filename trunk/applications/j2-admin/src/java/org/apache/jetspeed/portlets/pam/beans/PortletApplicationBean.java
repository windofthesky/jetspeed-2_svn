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
package org.apache.jetspeed.portlets.pam.beans;

import java.util.Collection;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.portlet.PortletApplication;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.portlet.PortletDefinitionList;
import org.apache.pluto.om.servlet.WebApplicationDefinition;

/**
 * This portlet is a browser over all the portlet applications in the system.
 *
 * @author <a href="mailto:jford@apache.com">Jeremy Ford</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: PortletApplicationBean.java 348264 2005-11-22 22:06:45Z taylor $
 */
public class PortletApplicationBean implements PortletApplication
{
    PortletApplication pa;
    
    
    public PortletApplicationBean(PortletApplication pa)
    {
        this.pa = pa;
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#getMetadata()
     */
    public GenericMetadata getMetadata()
    {
        return pa.getMetadata();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#getName()
     */
    public String getName()
    {
        return pa.getName();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#getPortletDefinitions()
     */
    public Collection getPortletDefinitions()
    {
        return pa.getPortletDefinitions();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#getPortletDefinitionByName(java.lang.String)
     */
    public PortletDefinition getPortletDefinitionByName(String name)
    {
        return pa.getPortletDefinitionByName(name);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#getUserAttributeRefs()
     */
    public Collection getUserAttributeRefs()
    {
        return pa.getUserAttributeRefs();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#getUserAttributes()
     */
    public Collection getUserAttributes()
    {
        return pa.getUserAttributes();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#getApplicationIdentifier()
     */
    public String getApplicationIdentifier()
    {
        return pa.getApplicationIdentifier();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#getDescription()
     */
    public String getDescription()
    {
        return pa.getDescription();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.portlet.PortletApplication#getApplicationType()
     */
    public int getApplicationType()
    {
        return pa.getApplicationType();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.portlet.PortletApplicationDefinition#getId()
     */
    public ObjectID getId()
    {
        return pa.getId();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.portlet.PortletApplicationDefinition#getVersion()
     */
    public String getVersion()
    {
        return pa.getVersion();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.portlet.PortletApplicationDefinition#getPortletDefinitionList()
     */
    public PortletDefinitionList getPortletDefinitionList()
    {
        return pa.getPortletDefinitionList();
    }

    /* (non-Javadoc)
     * @see org.apache.pluto.om.portlet.PortletApplicationDefinition#getWebApplicationDefinition()
     */
    public WebApplicationDefinition getWebApplicationDefinition()
    {
        return pa.getWebApplicationDefinition();
    }

    public Collection getJetspeedServices()
    {
        return pa.getJetspeedServices();
    }
}
