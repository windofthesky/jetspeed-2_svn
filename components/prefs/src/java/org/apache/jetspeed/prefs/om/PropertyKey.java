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
package org.apache.jetspeed.prefs.om;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>Interface representing a property key.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface PropertyKey extends Serializable, Cloneable
{

    /**
     * <p>Getter for the property key id.</p>
     * @return The property key id.
     */
    int getPropertyKeyId();

    /**
     * <p>Setter for the property key id.</p>
     * @param propertyKeyId The property key id.
     */
    void setPropertyKeyId(int propertyKeyId);

    /**
     * <p>Getter for the property key name.</p>
     * @return The property key name.
     */
    String getPropertyKeyName();

    /**
     * <p>Setter for the property key name.</p>
     * @param propertyKeyName The property key name.
     */
    void setPropertyKeyName(String propertyKeyName);

    /**
     * <p>Getter for the property key type.</p>
     * <ul>
     *     <li>0=Boolean,</li>
     *     <li>1=Long,</li>
     *     <li>2=Double,</li>
     *     <li>3=String,</li>
     *     <li>4=Timestamp</li>
     * </ul>
     * @return The property key type.
     */
    short getPropertyKeyType();

    /**
     * <p>Setter for the property key type.</p>
     * <ul>
     *     <li>0=Boolean,</li>
     *     <li>1=Long,</li>
     *     <li>2=Double,</li>
     *     <li>3=String,</li>
     *     <li>4=Timestamp</li>
     * </ul>
     * @param propertyKeyType The property key type.
     */
    void setPropertyKeyType(short propertyKeyType);

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
