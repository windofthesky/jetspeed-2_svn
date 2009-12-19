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
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.page.ContentFragment;

/**
 * ContentFragmentBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="fragment")
public class ContentFragmentBean implements Serializable
{

    private static final long serialVersionUID = 1L;
    
    private String id;
    private String type;
    private String name;
    private boolean locked;
    private String decorator;
    private String mode;
    private String state;
    private Map<String, String> properties;
    private Collection<ContentFragmentBean> contentFragmentBeans;
    
    
    public ContentFragmentBean()
    {
        
    }
    
    public ContentFragmentBean(final ContentFragment contentFragment)
    {
        id = contentFragment.getId();
        type = contentFragment.getType();
        name = contentFragment.getName();
        locked = contentFragment.isLocked();
        decorator = contentFragment.getDecorator();
        
        mode = contentFragment.getMode();
        state = contentFragment.getState();
        properties = contentFragment.getPropertiesMap();
        
        Collection<ContentFragment> childContentFragments = contentFragment.getFragments();
        
        if (childContentFragments != null && !childContentFragments.isEmpty())
        {
            ArrayList<ContentFragmentBean> childContentFragmentBeanList = new ArrayList<ContentFragmentBean>();
            
            for (ContentFragment childContentFragment : childContentFragments)
            {
                childContentFragmentBeanList.add(new ContentFragmentBean(childContentFragment));
            }
            
            contentFragmentBeans = childContentFragmentBeanList;
        }
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public boolean isLocked()
    {
        return locked;
    }
    
    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }
    
    public String getDecorator()
    {
        return decorator;
    }

    public void setDecorator(String decorator)
    {
        this.decorator = decorator;
    }

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, String> properties)
    {
        this.properties = properties;
    }

    @XmlElementWrapper(name="fragments")
    @XmlElements(@XmlElement(name="fragment"))
    public Collection<ContentFragmentBean> getContentFragmentBeans()
    {
        return contentFragmentBeans;
    }

    public void setContentFragmentBeans(Collection<ContentFragmentBean> contentFragmentBeans)
    {
        this.contentFragmentBeans = contentFragmentBeans;
    }
    
}
