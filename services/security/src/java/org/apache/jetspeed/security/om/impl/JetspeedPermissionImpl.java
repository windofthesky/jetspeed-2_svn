/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
