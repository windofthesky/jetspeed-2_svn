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
import java.util.Collection;

/**
 * <p>Interface representing a property set definition. A property set
 * definition represents the possible properties that can be associated
 * to a specific node given that the node name matches the property
 * set name.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 *
 */
public interface PropertySetDef extends Serializable, Cloneable
{

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
     * <p>Getter for the property set name.</p>
     * @return The property set name.
     */
    String getPropertySetName();

    /**
     * <p>Setter for the property set name.</p>
     * @param propertySetName The property set name.
     */
    void setPropertySetName(String propertySetName);

    /**
     * <p>Getter for the property set type.</p>
     * @return The property set type.
     */
    short getPropertySetType();

    /**
     * <p>Setter for the property set type.</p>
     * @param propertySetType The property set type.
     */
    void setPropertySetType(short propertySetType);

    /**
     * <p>Getter for the set definition property keys.</p>
     * @return The property keys collection.
     */
    Collection getPropertyKeys();

    /**
     * <p>Setter for the set definition property keys.</p>
     * @param propertyKeys The property keys set.
     */
    void setPropertyKeys(Collection propertyKeys);

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

