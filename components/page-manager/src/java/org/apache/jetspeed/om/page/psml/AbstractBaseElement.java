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

package org.apache.jetspeed.om.page.psml;

import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.page.BaseElement;


/**
 *
 * @version $Id$
 */
public abstract class AbstractBaseElement implements java.io.Serializable, SecuredResource
{

    private String id = null;

    private String acl = null;

    private String title = null;
    
    public String getId()
    {
         return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getAcl()
    {
        return this.acl;
    }

    public void setAcl(String aclName)
    {
        this.acl = aclName;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();

        // TBD

        return cloned;

    }   // clone
    /**
     * <p>
     * equals
     * </p>
     *
     * @see java.lang.Object#equals(java.lang.Object)
     * @param obj
     * @return
     */
    public boolean equals( Object obj )
    {
        if(obj instanceof BaseElement)
        {
            AbstractBaseElement element = (AbstractBaseElement) obj;
            return id != null && element.getId() != null && id.equals(element.getId());            
        }
        else
        {
            return false;
        }
    }
    
    /**
     * <p>
     * hashCode
     * </p>
     *
     * @see java.lang.Object#hashCode()
     * @return
     */
    public int hashCode()
    {
        return id.hashCode();
    }
    
    /**
     * <p>
     * toString
     * </p>
     *
     * @see java.lang.Object#toString()
     * @return
     */
    public String toString()
    {      
        return getId();
    }
}