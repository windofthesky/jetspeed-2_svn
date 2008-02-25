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
package org.apache.jetspeed.om.registry;

// Java imports
import java.util.Vector;

// Jetspeed imports
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.registry.MetaInfo;

/**
 * Interface for manipulatin the security entries on the registry entries
 *
 * 
 * @author <a href="mailto:paulsp@apache.org">Paul Spencer</a>
 * @version $Id$
 */

public interface SecurityEntry 
{

    /** Getter for property accesses.
     * @return Value of property accesses.
     */
    public Vector getAccesses();
    
    /** Setter for property accesses.
     * @param accesses New value of property accesses.
     */
    public void setAccesses(Vector accesses);
    
    /** Getter for property name.
     * @return Value of property name.
     */
    public String getName();
    
    /** Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name);
    
    /** Getter for property description.
     * @return Value of property description.
     */
    public String getDescription();
    
    /** Setter for property description.
     * @param description New value of property description.
     */
    public void setDescription(String description);
    
    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef();
    
    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef);
    
    /** Getter for property title.
     * @return Value of property title.
     */
    public String getTitle();
    
    /** Setter for property title.
     * @param title New value of property title.
     */
    public void setTitle(String title);
    
    /** Getter for property metaInfo.
     * @return Value of property metaInfo.
     */
    public MetaInfo getMetaInfo();
    
    /** Setter for property metaInfo.
     * @param metaInfo New value of property metaInfo.
     */
    public void setMetaInfo(MetaInfo metaInfo);
    
    /** Getter for property hidden.
     * @return Value of property hidden.
     */
    public boolean isHidden();
    
    /** Setter for property hidden.
     * @param hidden New value of property hidden.
     */
    public void setHidden(boolean hidden);
    
    /** Getter for property id.
     * @return Value of property id.
     */
    public long getId();
    
    /**
     * Aututhorizes action for a role
     *
     * @param role requesting action
     * @param action being requested
     * @return <CODE>true</CODE> if action is allowed for role
     */    
    public boolean allowsRole(String role, String action);
    
    /**
     * Aututhorizes action for a named user
     *
     * @param userName requesting action
     * @param action being requested
     * @return <CODE>true</CODE> if action is allowed for named user
     */    
    public boolean allowsUser(String userName, String action);
    
    /**
     * Aututhorizes action for a named user
     *
     * @param userName requesting action
     * @param action being requested
     * @param ownerUserName Onwers username 
     * @return <CODE>true</CODE> if action is allowed for named user
     */    
    public boolean allowsUser(String userName, String action, String ownerUserName);
}