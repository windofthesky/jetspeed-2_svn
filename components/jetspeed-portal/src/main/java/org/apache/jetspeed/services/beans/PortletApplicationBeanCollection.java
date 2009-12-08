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
package org.apache.jetspeed.services.beans;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * PortletApplicationBeans
 * 
 * @version $Id$
 */
@XmlRootElement(name="data")
public class PortletApplicationBeanCollection extends BeanCollection
{
    private static final long serialVersionUID = 1L;
    
    private Collection<PortletApplicationBean> applicationBeans;

    public PortletApplicationBeanCollection()
    {
        super();
    }
    
    @XmlElementWrapper(name="applications")
    @XmlElements(@XmlElement(name="application"))
    public Collection<PortletApplicationBean> getApplications()
    {
        return applicationBeans;
    }
    
    public void setApplications(Collection<PortletApplicationBean> applicationBeans)
    {
        this.applicationBeans = applicationBeans;
    }
}
