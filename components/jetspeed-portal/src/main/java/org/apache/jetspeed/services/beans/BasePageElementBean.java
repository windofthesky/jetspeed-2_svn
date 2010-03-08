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

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.page.BasePageElement;
import org.apache.jetspeed.om.page.Fragment;

/**
 * BasePageElementBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="basePageElement")
public class BasePageElementBean extends BaseFragmentsElementBean
{
    private static final long serialVersionUID = 1L;
    
    private String skin;
    private String defaultLayoutDecorator;
    private String defaultPortletDecorator;
    
    public BasePageElementBean()
    {
        
    }
    
    public BasePageElementBean(BasePageElement basePageElement)
    {
        super(basePageElement);
        skin = basePageElement.getSkin();
        defaultLayoutDecorator = basePageElement.getDefaultDecorator(Fragment.LAYOUT);
        defaultPortletDecorator = basePageElement.getDefaultDecorator(Fragment.PORTLET);
    }

    public String getSkin()
    {
        return skin;
    }

    public void setSkin(String skin)
    {
        this.skin = skin;
    }

    public String getDefaultLayoutDecorator()
    {
        return defaultLayoutDecorator;
    }

    public void setDefaultLayoutDecorator(String defaultLayoutDecorator)
    {
        this.defaultLayoutDecorator = defaultLayoutDecorator;
    }

    public String getDefaultPortletDecorator()
    {
        return defaultPortletDecorator;
    }

    public void setDefaultPortletDecorator(String defaultPortletDecorator)
    {
        this.defaultPortletDecorator = defaultPortletDecorator;
    }
    
}
