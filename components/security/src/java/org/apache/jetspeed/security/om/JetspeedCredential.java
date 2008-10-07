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
import java.sql.Timestamp;

/**
 * <p>Interface representing a security credential.</p>
 * <p>The credential value represents the value of the credential
 * such as a password.</p>
 * <p>For now, we do not have custom credentials classes and 
 * credentials support only 1 credential (i.e. 1 password).<p>
 * <p>The credential type represents whether a credential is private or
 * public:</p>
 * <ul>
 *  <li>Private credential: type == 0</li>
 *  <li>Public credential: type == 1</li>
 * </ul>
 * <p>The credential classname represent the class of credential.  For password
 * this field is null.
 * </p>
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 * TODO Add multiple credentials support.
 */
public interface JetspeedCredential extends Serializable, Cloneable
{
    /**
     * <p>Getter for the credential id.</p>
     * @return The credential id.
     */
    int getCredentialId();

    /**
     * <p>Setter for the credential id.</p>
     * @param credentialId The credential id.
     */
    void setCredentialId(int credentialId);

    /**
     * <p>Getter for the principal id.</p>
     * @return The principal id.
     */
    int getPrincipalId();

    /**
     * <p>Setter for the principal id.</p>
     * @param principalId The principal id.
     */
    void setPrincipalId(int principalId);

    /**
     * <p>Getter for the credential value.</p>
     * @return The credential value.
     */
    String getValue();

    /**
     * <p>Setter for the credential value.</p>
     * @param value The credential value.
     */
    void setValue(String value);

    /**
     * <p>Getter for the credential type.</p>
     * <ul>
     *  <li>Private credential: type == 0</li>
     *  <li>Public credential: type == 1</li>
     * </ul>
     * @return The credential type.
     */
    short getType();

    /**
     * <p>Setter for the credential type.</p>
     * <ul>
     *  <li>Private credential: type == 0</li>
     *  <li>Public credential: type == 1</li>
     * </ul>
     * @param type The credential type.
     */
    void setType(short type);

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
