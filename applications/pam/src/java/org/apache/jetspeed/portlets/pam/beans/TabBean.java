/*
 * Copyright 2004 The Apache Software Foundation.
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

/**
 *
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $ID$
 */
public class TabBean
{
    private String id;
    private String messageId;
    private String description;
    
    public TabBean()
    {
        
    }
    
    public TabBean(String id, String description)
    {
        this.id = id;
        this.description = description;
    }

    /**
     * @return Returns the description.
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * @param description The description to set.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }
    /**
     * @return Returns the id.
     */
    public String getId()
    {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(String id)
    {
        this.id = id;
    }
    /**
     * @return Returns the messageId.
     */
    public String getMessageId()
    {
        return messageId;
    }
    /**
     * @param messageId The messageId to set.
     */
    public void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }
    
    public boolean equals(Object obj)
    {
        boolean result = false;
        
        if(obj instanceof TabBean)
        {
            TabBean tab = (TabBean)obj;
            result = tab.id.equals(id);
        }
        
        return result;
    }
}
