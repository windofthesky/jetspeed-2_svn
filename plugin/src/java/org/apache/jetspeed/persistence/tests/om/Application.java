package org.apache.jetspeed.persistence.tests.om;
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

/**
 *
 *  This is a trivial object we will use to test basic peristence on.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 * @since 1.0
 */
public class Application
{ 
  
    /** Holds value of property name. */
    private String name;

    /** Holds value of property version. */
    private String version;

    /** Holds the optional application identifier from the portlet.xml */
    private String applicationIdentifier;

    /** Description */
    private String description;

    /**
     * @see org.apache.jetspeed.om.common.Application#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#setName(String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#getVersion()
     */
    public String getVersion()
    {
        return this.version;
    }

    /**
     * @see org.apache.jetspeed.om.common.Application#setVersion(String)
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
     * @return
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param string
     */
    public void setDescription(String string)
    {
        description = string;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#setApplicationIdentifier(java.lang.String)
     */
    public void setApplicationIdentifier(String applicationIdentifier)
    {
        this.applicationIdentifier = applicationIdentifier;
    }

    /**
     * @see org.apache.jetspeed.om.common.portlet.PortletApplicationComposite#getApplicationIdentifier()
     */
    public String getApplicationIdentifier()
    {
        return this.applicationIdentifier;
    }

}
