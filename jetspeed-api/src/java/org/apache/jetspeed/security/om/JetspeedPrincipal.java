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
package org.apache.jetspeed.security.om;

import java.io.Serializable;
import java.util.Collection;
import java.sql.Timestamp;

/**
 * <p>Interface representing a policy principal.  This will be used by the 
 * {@link org.apache.jetspeed.security.impl.RdbmsPolicy} to retrieve specify
 * which permissions are applied on which principal according to the JAAS policy:</p>
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
public interface JetspeedPrincipal extends Serializable, Cloneable
{

    /**
     * <p>Getter for the principal id.</p>
     * @return The principal id.
     */
    long getPrincipalId();

    /**
     * <p>Setter for the principal id.</p>
     * @param principalId The principal id.
     */
    void setPrincipalId(long principalId);

    /**
     * <p>Getter for the principal classname.</p>
     * @return The principal classname.
     */
    String getClassname();

    /**
     * <p>Setter for the principal classname.</p>
     * @param classname The principal classname.
     */
    void setClassname(String classname);

 
    /**
     * <p>Getter for the principal full path.</p>
     * <p>The full path allows to retrieve the principal preferences from
     * the preferences services.</p>
     * @return The principal full path.
     */
    String getFullPath();

    /**
     * <p>Setter for the principal name.</p>
     * <p>The full path allows to retrieve the principal preferences from
     * the preferences services.</p>
     * @param fullPath The principal full path.
     */
    void setFullPath(String fullPath);

    /**
     * <p>Getter for the principal permissions.</p>
     * @return The principal permissions.
     */
    Collection getPermissions();

    /**
     * <p>Setter for the principal permissions.</p>
     * @param permissions The principal permissions.
     */
    void setPermissions(Collection permissions);

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

}
