/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
import java.util.ArrayList;
import java.util.Collection;

import org.apache.jetspeed.security.om.InternalPermission;
import org.apache.jetspeed.security.om.InternalPrincipal;

/**
 * <p>
 * {@link InternalPrincipal}interface implementation.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class InternalPrincipalImpl implements InternalPrincipal
{
    /** The serial version uid. */
    private static final long serialVersionUID = 3615655651128923549L;

    /** The principal id. */
    private long principalId;

    /** The class name. */
    private String classname;

    /** The is mapping only. */
    private boolean isMappingOnly = false;

    /** The principal type such as user | group | role */
    private String type;
    
    /** The name of the principal */
    private String name;

    /** The collection of permissions. */
    private Collection<InternalPermission> permissions;

    /** The creation date. */
    private Timestamp creationDate;

    /** The modified date. */
    private Timestamp modifiedDate;
    
    /** The enabled state. */
    private boolean enabled = true;

    /**
     * <p>
     * The special attribute telling OJB the object's concrete type.
     * </p>
     * <p>
     * NOTE: this attribute MUST be called ojbConcreteClass
     * </p>
     */
    protected String ojbConcreteClass;

    /**
     * <p>
     * InternalPrincipal implementation default constructor.
     * </p>
     */
    public InternalPrincipalImpl()
    {
    }

    /**
     * <p>
     * InternalPrincipal constructor given a classname and name.
     * </p>
     * 
     * @param classname The classname.
     * @param fullPath The full path.
     */
    public InternalPrincipalImpl(String classname, String type, String name)
    {
        this.ojbConcreteClass = classname;
        this.classname = classname;
        this.type = type;
        this.name = name;
        this.permissions = new ArrayList<InternalPermission>();
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#getPrincipalId()
     */
    public long getPrincipalId()
    {
        return this.principalId;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#setPrincipalId(long)
     */
    public void setPrincipalId(long principalId)
    {
        this.principalId = principalId;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#getClassname()
     */
    public String getClassname()
    {
        return this.classname;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#setClassname(java.lang.String)
     */
    public void setClassname(String classname)
    {
        this.ojbConcreteClass = classname;
        this.classname = classname;
    }

    /**
     * @return Returns the isMappingOnly.
     */
    public boolean isMappingOnly()
    {
        return isMappingOnly;
    }

    /**
     * @param isMappingOnly The isMappingOnly to set.
     */
    public void setMappingOnly(boolean isMappingOnly)
    {
        this.isMappingOnly = isMappingOnly;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#getType()
     */
    public String getType()
    {
        return this.type;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#setType(java.lang.String)
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#getName()
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#getPermissions()
     */
    public Collection<InternalPermission> getPermissions()
    {
        return this.permissions;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#setPermissions(java.util.Collection)
     */
    public void setPermissions(Collection<InternalPermission> permissions)
    {
        this.permissions = permissions;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        return this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#setCreationDate(java.sql.Timestamp)
     */
    public void setCreationDate(Timestamp creationDate)
    {
        this.creationDate = creationDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.security.om.InternalPrincipal#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    /** 
     * @see org.apache.jetspeed.security.om.InternalPrincipal#isEnabled()
     */
    public boolean isEnabled()
    {
        return enabled;
    }
    
    /** 
     * @see org.apache.jetspeed.security.om.InternalPrincipal#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    public String getFullPath() // TODO: remove this
    {
        return "";    
    }
}