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
 * <p>Interface representing a property key.</p>
 *
 * @author <a href="david@sensova.com">David Le Strat</a>
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
     * <p>Getter for the property set definition id.</p>
     * @return The property set definition id.
     */
    int getPropertySetDefId();

    /**
     * <p>Setter for the property set definition id.</p>
     * @param propertySetDefId The property set definition id.
     */
    void setPropertySetDefId(int propertySetDefId);

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
