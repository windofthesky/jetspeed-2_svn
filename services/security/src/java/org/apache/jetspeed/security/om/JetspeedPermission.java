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
package org.apache.jetspeed.security.om;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * <p>Interface representing a policy permission.  This will be used by the 
 * {@link org.apache.jetspeed.security.impl.RdbmsPolicy} to retrieve a permission
 * policy according to JAAS where permission are used in JAAS:</p>
 * <pre>
 * <code>grant [SignedBy "signer_names"] [, CodeBase "URL"]
 *             [, JetspeedPrincipal [principal_class_name] "principal_name"]
 *             [, JetspeedPrincipal [principal_class_name] "principal_name"] ... 
 *       {
 *                  permission permission_class_name [ "target_name" ] 
 *                                                   [, "action"] [, SignedBy "signer_names"];
 *                  permission ...
 *       };
 * </code>
 * </pre>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface JetspeedPermission extends Serializable, Cloneable
{

    /**
     * <p>Getter for the permission id.</p>
     * @return The permission id.
     */
    int getPermissionId();

    /**
     * <p>Setter for the permission id.</p>
     * @param permissionId The permission id.
     */
    void setPermissionId(int permissionId);

    /**
     * <p>Getter for the permission classname.</p>
     * @return The permission classname.
     */
    String getClassname();

    /**
     * <p>Setter for the permission classname.</p>
     * @param classname The permission classname.
     */
    void setClassname(String classname);

    /**
     * <p>Getter for the permission resource name.</p>
     * @return The permission resource name.
     */
    String getName();

    /**
     * <p>Setter for the permission resource name.</p>
     * @param name The permission resource name.
     */
    void setName(String name);
    
    /**
     * <p>Getter for the permission actions.</p>
     * @return The permission actions.
     */
    String getActions();

    /**
     * <p>Setter for the permission actions.</p>
     * @param actions The permission actions.
     */
    void setActions(String actions);

    /**
     * <p>Getter for the permission principals.</p>
     * @return The permission principals.
     */
    Collection getPrincipals();

    /**
     * <p>Setter for the permission principals.</p>
     * @param principals The permission principals.
     */
    void setPrincipals(Collection principals);

    /**
     * <p>Getter for creation date.</p>
     * @return The creation date.
     */
    Timestamp getCreationDate();

    /**
     * <p>Setter for the creation date.</p>
     * @param creationDate The creation date.
     */
    void setCreationDate(Timestamp creationDate);

    /**
     * <p>Getter for the modified date.</p>
     * @return The modified date.
     */
    Timestamp getModifiedDate();

    /**
     * <p>Setter for the modified date.</p>
     * @param modifiedDate The modified date.
     */
    void setModifiedDate(Timestamp modifiedDate);

    /**
     * <p>Equals method used to appropriately compare 2 {@link JetspeedPermission} objects.</p>
     * @param object The object to compare with.
     * @return The comparison result.
     */
    boolean equals(Object object);
}
