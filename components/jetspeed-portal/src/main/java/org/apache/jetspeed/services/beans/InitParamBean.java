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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.portlet.Description;
import org.apache.jetspeed.om.portlet.InitParam;

/**
 * InitParamBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="initParam")
public class InitParamBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String paramName;
    private String paramValue;
    private Collection<DescriptionBean> descriptionBeans;
    
    public InitParamBean()
    {
        
    }
    
    public InitParamBean(final InitParam initParam)
    {
        paramName = initParam.getParamName();
        paramValue = initParam.getParamValue();
        
        ArrayList<DescriptionBean> descriptionBeanList = new ArrayList<DescriptionBean>();
        
        for (Description description : initParam.getDescriptions())
        {
            descriptionBeanList.add(new DescriptionBean(description));
        }
        
        descriptionBeans = descriptionBeanList;
    }

    public String getParamName()
    {
        return paramName;
    }

    public void setParamName(String paramName)
    {
        this.paramName = paramName;
    }

    public String getParamValue()
    {
        return paramValue;
    }

    public void setParamValue(String paramValue)
    {
        this.paramValue = paramValue;
    }
    
    @XmlElementWrapper(name="descriptions")
    @XmlElements(@XmlElement(name="description"))
    public Collection<DescriptionBean> getDescriptionBeans()
    {
        return descriptionBeans;
    }

    public void setDescriptionBeans(Collection<DescriptionBean> descriptionBeans)
    {
        this.descriptionBeans = descriptionBeans;
    }
    
    
}
