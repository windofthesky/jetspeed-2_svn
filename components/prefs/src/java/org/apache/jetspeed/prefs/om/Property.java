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
 * <p>Interface representing a property key/value pair.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface Property extends Serializable, Cloneable
{

    /** <p>Constant for <code>boolean</code> property value object type.</p> */
    int BOOLEAN_TYPE = 0;

    /** <p>Constant for <code>long</code> property value object type.</p> */
    int LONG_TYPE = 1;

    /** <p>Constant for <code>double</code> property value object type.</p> */
    int DOUBLE_TYPE = 2;

    /** <p>Constant for <code>String</code> property value object type.</p> */
    int STRING_TYPE = 3;

    /** <p>Constant for <code>Timestamp</code> property value object type.</p> */
    int TIMESTAMP_TYPE = 3;

    /**
     * <p>Getter for the property value id.</p>
     * @return The property value id.
     */
    long getPropertyValueId();

    /**
     * <p>Setter for the property value id.</p>
     * @param propertyValueId The property value id.
     */
    void setPropertyValueId(long propertyValueId);

    /**
     * <p>Getter for the node id.</p>
     * @return The node id.
     */
    long getNodeId();

    /**
     * <p>Setter for the node id.</p>
     * @param nodeId The node id.
     */
    void setNodeId(long nodeId);

    /**
     * <p>Getter for the node id.</p>
     * @return The property key id.
     */
    long getPropertyKeyId();

    /**
     * <p>Setter for the property key id.</p>
     * @param propertyKeyId The property key id.
     */
    void setPropertyKeyId(long propertyKeyId);

    /**
     * <p>Getter for the property key object.</p>
     * @return The property key object.
     */
    PropertyKey getPropertyKey();

    /**
     * <p>Setter for the property key object.</p>
     * @param propertyKey The property key object.
     */
    void setPropertyKey(PropertyKey propertyKey);

    /**
     * <p>Utility method used to return the property value 
     * as a String.</p>
     * @param valueObjectType The value object type.
     * @return The property value as a String.
     */
    String getPropertyValue(int valueObjectType);

    /**
     * <p>Utility method used to identify with property value to set
     * based on the value object type.</p>
     * @param valueObjectType The value object type.
     * @param valueObject The value object.
     */
    void setPropertyValue(int valueObjectType, String valueObject);

    /**
     * <p>Getter for the boolean property value.</p>
     * @return The boolean property value.
     */
    boolean getBooleanPropertyValue();

    /**
     * <p>Setter for the boolean property value.</p>
     * @param booleanPropertyValue The boolean property value.
     */
    void setBooleanPropertyValue(boolean booleanPropertyValue);

    /**
     * <p>Getter for the date property value.</p>
     * @return The date property value.
     */
    Timestamp getDatePropertyValue();

    /**
     * <p>Setter for the date property value.</p>
     * @param datePropertyValue The date property value.
     */
    void setDatePropertyValue(Timestamp datePropertyValue);

    /**
     * <p>Getter for the long property value.</p>
     * @return The long property value.
     */
    long getLongPropertyValue();

    /**
     * <p>Setter for the long property value.</p>
     * @param longPropertyValue The long property value.
     */
    void setLongPropertyValue(long longPropertyValue);

    /**
     * <p>Getter for the double property value.</p>
     * @return The double property value.
     */
    double getDoublePropertyValue();

    /**
     * <p>Setter for the double property value.</p>
     * @param doublePropertyValue The double property value.
     */
    void setDoublePropertyValue(double doublePropertyValue);

    /**
     * <p>Getter for the text property value.</p>
     * @return The text property value.
     */
    String getTextPropertyValue();

    /**
     * <p>Setter for the text property value.</p>
     * @param textPropertyValue The text property value.
     */
    void setTextPropertyValue(String textPropertyValue);

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
