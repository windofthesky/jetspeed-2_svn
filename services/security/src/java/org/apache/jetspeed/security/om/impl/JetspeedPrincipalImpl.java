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
