/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
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
package org.apache.jetspeed.om.registry.base;

// Jetspeed imports
import org.apache.jetspeed.om.registry.SecurityAllow;

/**
 * Interface for manipulatin the Security Allow on the registry entries
 * 
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id$
 */
public class BaseSecurityAllow implements SecurityAllow, java.io.Serializable
{
    /** Holds value of property role. */
    private String role;
    
    /** Holds value of property user. */
    private String user;
    
    /** Holds value of property owner. */
    private boolean owner = false;
    
    /** Creates new BaseSecurityAllow */
    public BaseSecurityAllow()
    {
    }

    /**
     * Create a new BaseSecurityAllow that sets the owner property
     *
     * @param owner Set the owner property
     */
    public BaseSecurityAllow(boolean owner)
    {
        this.owner = owner;
    }
    
    /** Getter for property role.
     * @return Value of property role.
     */
    public String getRole()
    {
        return role;
    }
    
    /** Setter for property role.
     * @param role New value of property role.
     */
    public void setRole(String role)
    {
        this.role = role;
    }
    
    /** Getter for property user.
     * @return Value of property user.
     */
    public String getUser()
    {
        return user;
    }
    
    /** Setter for property user.
     * @param user New value of property user.
     */
    public void setUser(String user)
    {
        this.user = user;
    }
    
    /** Getter for property owner.
     * @return Value of property owner.
     */
    public boolean isOwner()
    {
        return this.owner;
    }
    
    /** Setter for property owner.
     * @param owner New value of property owner.
     */
    public void setOwner(boolean owner)
    {
        this.owner = owner;
    }
    
}
