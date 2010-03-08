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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.page.document.Node;

/**
 * NodeBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="node")
public class NodeBean extends BaseElementBean
{
    private static final long serialVersionUID = 1L;
    
    private String path;
    private String name;
    private GenericMetadataBean metadataBean;
    private String type;
    private String url;
    private boolean hidden;
    
    public NodeBean()
    {
        
    }
    
    public NodeBean(Node node)
    {
        super(node);
        path = node.getPath();
        name = node.getName();
        
        GenericMetadata metadata = node.getMetadata();
        
        if (metadata != null)
        {
            metadataBean = new GenericMetadataBean(metadata);
        }
        
        type = node.getType();
        url = node.getUrl();
        hidden = node.isHidden();
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        this.path = path;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @XmlElement(name="metadata")
    public GenericMetadataBean getMetadataBean()
    {
        return metadataBean;
    }

    public void setMetadataBean(GenericMetadataBean metadataBean)
    {
        this.metadataBean = metadataBean;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }
    
}
