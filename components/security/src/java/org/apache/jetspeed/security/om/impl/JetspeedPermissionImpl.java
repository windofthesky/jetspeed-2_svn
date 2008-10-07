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

import org.apache.jetspeed.security.om.JetspeedPermission;

/**
 * <p>{@link JetspeedPermission} interface implementation.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class JetspeedPermissionImpl implements JetspeedPermission
{

    /**
     * <p>JetspeedPermission implementation default constructor.</p>
     */
    public JetspeedPermissionImpl()
    {
    }

    /**
     * <p>JetspeedPermission constructor.</p>
     * @param classname The classname.
     * @param name The name.
     * @param actions The actions.
     */
    public JetspeedPermissionImpl(String classname, String name, String actions)
    {
        this.classname = classname;
        this.name = name;
        this.actions = actions;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }

    private int permissionId;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#getPermissionId()
     */
    public int getPermissionId()
    {
        return this.permissionId;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#setPermissionId(int)
     */
    public void setPermissionId(int permissionId)
    {
        this.permissionId = permissionId;
    }

    private String classname;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#getClassname()
     */
    public String getClassname()
    {
        return this.classname;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#setClassname(java.lang.String)
     */
    public void setClassname(String classname)
    {
        this.classname = classname;
    }

    private String name;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    private String actions;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#getActions()
     */
    public String getActions()
    {
        return this.actions;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#setActions(java.lang.String)
     */
    public void setActions(String actions)
    {
        this.actions = actions;
    }

    private Collection principals;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#getPrincipals()
     */
    public Collection getPrincipals()
    {
        return this.principals;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#setPrincipals(java.util.Collection)
     */
    public void setPrincipals(Collection principals)
    {
        this.principals = principals;
    }

    private Timestamp creationDate;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        return this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#setCreationDate(java.sql.Timestamp)
     */
    public void setCreationDate(Timestamp creationDate)
    {
        this.creationDate = creationDate;
    }

    private Timestamp modifiedDate;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPermission#equals(java.lang.Object)
     */
    public boolean equals(Object object)
    {  
        if (!(object instanceof JetspeedPermission))
            return false;

        JetspeedPermission p = (JetspeedPermission) object;
        boolean isEqual =
            ((p.getClassname().equals(this.getClassname())) && (p.getName().equals(this.getName())) && (p.getActions().equals(this.getActions())));
        return isEqual;
    }

}
