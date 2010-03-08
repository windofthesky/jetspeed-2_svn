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

import org.apache.jetspeed.om.page.Link;

/**
 * LinkBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="link")
public class LinkBean extends DocumentBean
{
    private static final long serialVersionUID = 1L;
    
    private String skin;
    private String url;
    private String target;
    
    public LinkBean()
    {
        
    }
    
    public LinkBean(Link link)
    {
        super(link);
        skin = link.getSkin();
        url = link.getUrl();
        target = link.getTarget();
    }

    public String getSkin()
    {
        return skin;
    }

    public void setSkin(String skin)
    {
        this.skin = skin;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }
    
}
