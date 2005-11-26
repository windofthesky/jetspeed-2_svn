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
 * <p>
 * Interface representing a property key/value pair.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface Property extends Serializable, Cloneable
{

    /**
     * <p>
     * Getter for the property value id.
     * </p>
     * 
     * @return The property value id.
     */
    long getPropertyValueId();

    /**
     * <p>
     * Setter for the property value id.
     * </p>
     * 
     * @param propertyValueId The property value id.
     */
    void setPropertyValueId(long propertyValueId);

    /**
     * <p>
     * Getter for the node id.
     * </p>
     * 
     * @return The node id.
     */
    long getNodeId();

    /**
     * <p>
     * Setter for the node id.
     * </p>
     * 
     * @param nodeId The node id.
     */
    void setNodeId(long nodeId);

    /**
     * <p>
     * Getter for the property name.
     * </p>
     * 
     * @return The property name.
     */
    String getPropertyName();

    /**
     * <p>
     * Setter for the property name.
     * </p>
     * 
     * @param propertyName The property name.
     */
    void setPropertyName(String propertyName);

    /**
     * <p>
     * Utility method used to return the property value as a String.
     * </p>
     * 
     * @return The property value as a String.
     */
    String getPropertyValue();

    /**
     * <p>
     * Utility method used to identify with property value to set based on the
     * value object type.
     * </p>
     * 
     * @param valueObject The value object.
     */
    void setPropertyValue(String valueObject);

    /**
     * <p>
     * Getter for creation date.
     * </p>
     * 
     * @return The creation date.
     */
    Timestamp getCreationDate();

    /**
     * <p>
     * Setter for the creation date.
     * </p>
     * 
     * @param creationDate The creation date.
     */
    void setCreationDate(Timestamp creationDate);

    /**
     * <p>
     * Getter for the modified date.
     * </p>
     * 
     * @return The modified date.
     */
    Timestamp getModifiedDate();

    /**
     * <p>
     * Setter for the modified date.
     * </p>
     * 
     * @param modifiedDate The modified date.
     */
    void setModifiedDate(Timestamp modifiedDate);

}
