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
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;

/**
 * GenericMetadataBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="metadata")
public class GenericMetadataBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private Collection<LocalizedFieldBean> localizedFieldBeans;
    
    public GenericMetadataBean()
    {
        
    }
    
    public GenericMetadataBean(final GenericMetadata metadata)
    {
        Collection<LocalizedField> fields = metadata.getFields();
        
        if (fields != null)
        {
            List<LocalizedFieldBean> localizedFieldBeanList = new ArrayList<LocalizedFieldBean>();
            
            for (LocalizedField field : fields)
            {
                localizedFieldBeanList.add(new LocalizedFieldBean(field));
            }
        
            localizedFieldBeans = localizedFieldBeanList;
        }
    }
    
    @XmlElementWrapper(name="fields")
    @XmlElements(@XmlElement(name="field"))
    public Collection<LocalizedFieldBean> getLocalizedFieldBeans()
    {
        return localizedFieldBeans;
    }
    
    public void setLocalizedFieldBeans(Collection<LocalizedFieldBean> localizedFieldBeans)
    {
        this.localizedFieldBeans = localizedFieldBeans;
    }
    
}
