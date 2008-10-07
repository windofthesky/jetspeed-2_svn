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

import org.apache.jetspeed.security.om.JetspeedPrincipal;

/**
 * <p>{@link JetspeedPrincipal} interface implementation.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class JetspeedPrincipalImpl implements JetspeedPrincipal
{
    /** 
     * <p>The special attribute telling OJB the object's concrete type.</p>
     * <p>NOTE: this attribute MUST be called ojbConcreteClass</p>
     */
    protected String ojbConcreteClass;

    /**
     * <p>JetspeedPrincipal implementation default constructor.</p>
     */
    public JetspeedPrincipalImpl()
    {
    }

    /**
     * <p>JetspeedPrincipal constructor given a classname and name.</p>
     * @param classname The classname.
     * @param fullPath The full path.
     */
    public JetspeedPrincipalImpl(String classname, String fullPath)
    {
        this.ojbConcreteClass = classname;
        this.classname = classname;
        this.fullPath = fullPath;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }

    private int principalId;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#getPrincipalId()
     */
    public int getPrincipalId()
    {
        return this.principalId;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#setPrincipalId(int)
     */
    public void setPrincipalId(int principalId)
    {
        this.principalId = principalId;
    }

    private String classname;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#getClassname()
     */
    public String getClassname()
    {
        return this.classname;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#setClassname(java.lang.String)
     */
    public void setClassname(String classname)
    {
        this.ojbConcreteClass = classname;
        this.classname = classname;
    }

    private String fullPath;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#getFullPath()
     */
    public String getFullPath()
    {
        return this.fullPath;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#setFullPath(java.lang.String)
     */
    public void setFullPath(String fullPath)
    {
        this.fullPath = fullPath;
    }

    private Collection permissions;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#getPermissions()
     */
    public Collection getPermissions()
    {
        return this.permissions;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#setPermissions(java.util.Collection)
     */
    public void setPermissions(Collection permissions)
    {
        this.permissions = permissions;
    }

    private Timestamp creationDate;
 
    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        return this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#setCreationDate(java.sql.Timestamp)
     */
    public void setCreationDate(Timestamp creationDate)
    {
        this.creationDate = creationDate;
    }

    private Timestamp modifiedDate;

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.JetspeedPrincipal#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

}
