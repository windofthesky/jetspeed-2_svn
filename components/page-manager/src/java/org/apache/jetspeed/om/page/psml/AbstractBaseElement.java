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

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.idgenerator.IdGenerator;

/**
 *
 * @version $Id$
 */
public abstract class AbstractBaseElement implements java.io.Serializable
{

    private String id = null;

    private String name = null;

    private String acl = null;

    private String title = null;

    public String getId()
    {
        if (this.id==null)
        {
            // FIXME: not sure how yet, but this shouldn't be here
            // components should have their dependencies wired on construction
            IdGenerator generator = (IdGenerator)Jetspeed.getComponentManager().getComponent("IdGenerator");
            this.id = generator.getNextPeid();
        }
        return this.id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
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
}