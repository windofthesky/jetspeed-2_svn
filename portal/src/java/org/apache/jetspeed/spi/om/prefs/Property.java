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
package org.apache.jetspeed.spi.om.prefs;

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
    short BOOLEAN_TYPE = 0;

    /** <p>Constant for <code>long</code> property value object type.</p> */
    short LONG_TYPE = 1;

    /** <p>Constant for <code>double</code> property value object type.</p> */
    short DOUBLE_TYPE = 2;

    /** <p>Constant for <code>String</code> property value object type.</p> */
    short STRING_TYPE = 3;

    /** <p>Constant for <code>Timestamp</code> property value object type.</p> */
    short TIMESTAMP_TYPE = 3;

    /**
     * <p>Getter for the property value id.</p>
     * @return The property value id.
     */
    int getPropertyValueId();

    /**
     * <p>Setter for the property value id.</p>
     * @param propertyValueId The property value id.
     */
    void setPropertyValueId(int propertyValueId);

    /**
     * <p>Getter for the node id.</p>
     * @return The node id.
     */
    int getNodeId();

    /**
     * <p>Setter for the node id.</p>
     * @param nodeId The node id.
     */
    void setNodeId(int nodeId);

    /**
     * <p>Getter for the node id.</p>
     * @return The property key id.
     */
    int getPropertyKeyId();

    /**
     * <p>Setter for the property key id.</p>
     * @param propertyKeyId The property key id.
     */
    void setPropertyKeyId(int propertyKeyId);

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
    String getPropertyValue(short valueObjectType);

    /**
     * <p>Utility method used to identify with property value to set
     * based on the value object type.</p>
     * @param valueObjectType The value object type.
     * @param valueObject The value object.
     */
    void setPropertyValue(short valueObjectType, String valueObject);

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
