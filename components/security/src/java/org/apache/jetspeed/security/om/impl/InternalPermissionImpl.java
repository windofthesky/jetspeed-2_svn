/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.om.impl;

import java.sql.Timestamp;
import java.util.Collection;

import org.apache.jetspeed.security.om.InternalPermission;

/**
 * <p>{@link InternalPermission} interface implementation.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class InternalPermissionImpl implements InternalPermission
{

    /**
     * <p>InternalPermission implementation default constructor.</p>
     */
    public InternalPermissionImpl()
    {
    }

    /**
     * <p>InternalPermission constructor.</p>
     * @param classname The classname.
     * @param name The name.
     * @param actions The actions.
     */
    public InternalPermissionImpl(String classname, String name, String actions)
    {
        this.classname = classname;
        this.name = name;
        this.actions = actions;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }

    private long permissionId;

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#getPermissionId()
     */
    public long getPermissionId()
    {
        return this.permissionId;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#setPermissionId(long)
     */
    public void setPermissionId(long permissionId)
    {
        this.permissionId = permissionId;
    }

    private String classname;

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#getClassname()
     */
    public String getClassname()
    {
        return this.classname;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#setClassname(java.lang.String)
     */
    public void setClassname(String classname)
    {
        this.classname = classname;
    }

    private String name;

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    private String actions;

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#getActions()
     */
    public String getActions()
    {
        return this.actions;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#setActions(java.lang.String)
     */
    public void setActions(String actions)
    {
        this.actions = actions;
    }

    private Collection principals;

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#getPrincipals()
     */
    public Collection getPrincipals()
    {
        return this.principals;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#setPrincipals(java.util.Collection)
     */
    public void setPrincipals(Collection principals)
    {
        this.principals = principals;
    }

    private Timestamp creationDate;

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        return this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#setCreationDate(java.sql.Timestamp)
     */
    public void setCreationDate(Timestamp creationDate)
    {
        this.creationDate = creationDate;
    }

    private Timestamp modifiedDate;

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPermission#equals(java.lang.Object)
     */
    public boolean equals(Object object)
    {  
        if (!(object instanceof InternalPermission))
            return false;

        InternalPermission p = (InternalPermission) object;
        boolean isEqual =
            ((p.getClassname().equals(this.getClassname())) && (p.getName().equals(this.getName())) && (p.getActions().equals(this.getActions())));
        return isEqual;
    }

}
