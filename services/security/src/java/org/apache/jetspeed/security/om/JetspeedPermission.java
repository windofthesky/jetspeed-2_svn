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
