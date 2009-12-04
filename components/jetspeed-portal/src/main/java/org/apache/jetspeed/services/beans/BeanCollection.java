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
import java.util.Collection;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * BeansCollection
 * 
 * @version $Id$
 */
@XmlRootElement(name="collection")
public class BeanCollection<T> implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private Collection<T> collection;
    
    private int totalSize = -1;
    
    private int beginIndex = -1;
    
    public BeanCollection()
    {
        
    }
    
    protected Collection<T> getCollection()
    {
        return collection;
    }
    
    protected void setCollection(Collection<T> collection)
    {
        this.collection = collection;
    }
    
    @XmlAttribute(name="size")
    public int getSize()
    {
        return (collection != null ? collection.size() : 0);
    }
    
    public void setSize()
    {
        
    }
    
    @XmlAttribute(name="totalSize")
    public int getTotalSize()
    {
        return totalSize;
    }
    
    public void setTotalSize(int totalSize)
    {
        this.totalSize = totalSize;
    }
    
    @XmlAttribute(name="beginIndex")
    public int getBeginIndex()
    {
        return beginIndex;
    }
    
    public void setBeginIndex(int beginIndex)
    {
        this.beginIndex = beginIndex;
    }
    
}
