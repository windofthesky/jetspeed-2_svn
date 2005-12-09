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
import java.sql.Date;
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
 * <p>The credential classname represent the class of credential.
 * </p>
 * TODO Add multiple credentials support.
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 * @version $Id$
 */
public interface InternalCredential extends Serializable, Cloneable
{
    /** Private credentials type. */
    public static final int PRIVATE = 0;
    /** Public credentials type. */
    public static final int PUBLIC = 1;
    
    /**
     * Maximum allowed java.sql.Date value (according to the specs).
     * <em>Note:</em><br>
     * The concrete value is default time zone dependent and should <em>only</em>
     * be used for setting Date fields, not to <em>compare<em> against.
     */
    public static final Date MAX_DATE = Date.valueOf("8099-01-01");

    /**
     * <p>Getter for the credential id.</p>
     * @return The credential id.
     */
    long getCredentialId();

    /**
     * <p>Setter for the credential id.</p>
     * @param credentialId The credential id.
     */
    void setCredentialId(long credentialId);

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
     * <p>Getter for the update required state</p>
     * @return true if required
     */
    boolean isUpdateRequired();
    
    /**
     * <p>Setter for the update required state</p>
     * @param updateRequired the update required state
     */
    void setUpdateRequired(boolean updateRequired);
    
    /**
     * <p>Getter for the encoded state</p>
     * @return true if encoded
     */
    boolean isEncoded();
    
    /**
     * Setter for the encoded state</p>
     * @param encoded The encoded state
     */
    void setEncoded(boolean encoded);
    
    /**
     * <p>Getter for the enabled state</p>
     * @return true if enabled
     */
    boolean isEnabled();
    
    /**
     * Setter for the enabled state</p>
     * @param enabled The enabled state
     */
    void setEnabled(boolean enabled);
    
    /**
     * <p>Getter for the current number of authentication failures in a row.</p>
     * <ul>
     *   <li>-1: never tried yet</li>
     *   <li> 0: none, or last attempt was successful</li>
     *   <li>>0: number of failures</li>
     * </ul>
     * @return The number of authentication failures
     */
    int getAuthenticationFailures();
    
    /**
     * <p>Setter for the number of authentication failures</p>
     * @param authenticationFailures The number of authentication failures
     */
    void setAuthenticationFailures(int authenticationFailures);

    /**
     * Getter for the expired state.</p>
     * @return true if expired
     */
    boolean isExpired();
    
    /**
     * Setter for the expired state.</p>
     * @param expired The expired state
     */
    void setExpired(boolean expired);
    
    /**
     * <p>Getter for the expiration date.</p>
     * @return The expiration date.
     */
    Date getExpirationDate();
    
    /**
     * <p>Setter for the expiration date.</p>
     * @param expirationDate The expiration date.
     */
    void setExpirationDate(Date expirationDate);
    
    /**
     * <p>Getter for the credential type.</p>
     * <ul>
     *  <li>Private credential: type == 0</li>
     *  <li>Public credential: type == 1</li>
     * </ul>
     * @return The credential type.
     */
    int getType();

    /**
     * <p>Setter for the credential type.</p>
     * <ul>
     *  <li>Private credential: type == 0</li>
     *  <li>Public credential: type == 1</li>
     * </ul>
     * @param type The credential type.
     */
    void setType(int type);

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

    /**
     * <p>Getter for the previous authentication date</p>
     * @return The previous authentication date.
     */
    Timestamp getPreviousAuthenticationDate();
    
    /**
     * <p>Setter for the previous authentication date</p>
     * @param previousAuthenticationDate The previous authentication date.
     */
    void setPreviousAuthenticationDate(Timestamp previousAuthenticationDate);

    /**
     * <p>Getter for the last authentication date</p>
     * @return The last authentication date.
     */
    Timestamp getLastAuthenticationDate();
    
    /**
     * <p>Setter for the last authentication date</p>
     * @param lastAuthenticationDate The last authentication date.
     */
    void setLastAuthenticationDate(Timestamp lastAuthenticationDate);
}
