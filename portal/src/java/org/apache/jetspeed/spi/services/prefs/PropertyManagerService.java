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
package org.apache.jetspeed.spi.services.prefs;

import java.util.Collection;
import java.util.Map;

import org.apache.jetspeed.cps.CommonService;
import org.apache.jetspeed.spi.services.prefs.impl.PropertyException;

/**
 * <p>Service used to manage property and property set definition.<p>
 * <p>A property set definition defines a property set and the possible
 * properties assigned to that set. All or a subset of the property
 * set definition properties can be assigned to a node.<p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface PropertyManagerService extends CommonService
{

    /** The name of the service. */
    String SERVICE_NAME = "PropertyManager";

    /** The name of the property key name property. */
    String PROPERTYKEY_NAME = "propertyKeyName";

    /** The name of the property key type property. */
    String PROPERTYKEY_TYPE = "propertyKeyType";

    /**
     * <p>Add a property set definition.</p>
     * @param propertySetName The property set name.
     * @param propertySetType The property set type.
     * @return The property set definition id.
     * @throws PropertyException Thrown if the property set
     *                           definition already exists.
     */
    int addPropertySetDef(String propertySetName,
                          short propertySetType)
        throws PropertyException;

    /**
     * <p>Get a property set definition id for a specify
     * property set name and property set type.</p>
     * @param propertySetName The property set definition name to lookup.
     * @param propertySetType The property set definition type to lookup.
     * @return The property set definition id.
     * @throws PropertyException Throwns if no property set definition is found.
     */
    int getPropertySetDefIdByType(String propertySetName,
                                  short propertySetType)
        throws PropertyException;

    /**
     * <p>Remove a property set definition and all associated
     * property keys and values as well as property sets.</p>
     * @param propertySetDefId The property set definition id.
     * @throws PropertyException Thrown if the property set definition
     *                           does not exist.
     */
    void removePropertySetDef(int propertySetDefId)
        throws PropertyException;

    /**
     * <p>Update a property set definition.</p>
     * @param propertySetDefId The property set definition id.
     * @param propertySetName The property set name.
     * @param propertySetType The property set type.
     */
    void updatePropertySetDef(int propertySetDefId,
                              String propertySetName,
                              short propertySetType);

    /**
     * <p>Retrieve all property set definition for a specific property set
     * type.  This method returns a collection of property set definition
     * id and property set name map.</p>
     * @param propertySetType The property set definition type.
     * @return Property set definition / property set definition
     *         name map.
     * @throws PropertyException Thrown if no property set definition is found.
     *
     */
    Map getAllPropertySetsByType(short propertySetType)
        throws PropertyException;

    /**
     * <p>Add a set of property keys to a property set definition.</p>
     * <p>Each Property key should be passed as a map of:</p>
     * <ul>
     *      <li><code>PROPERTYKEY_NAME</code>: The property key name of type
     *      <code>java.lang.String</code>.</li>
     *      <li><code>PROPERTYKEY_TYPE</code>: The property key type of type
     *      <code>java.lang.Short</code>.</li>
     * </ul>
     * <p>The collection of properties to be added is passed to the method.</p>
     * @param propertySetDefId The property set definition id.
     * @param propertyKeys A map property key name / key type.
     * @throws PropertyException Thrown if any property in the
     *                           set in already assigned to a property set
     *                           definition.
     */
    void addPropertyKeys(int propertySetDefId,
                         Collection propertyKeys)
        throws PropertyException;

    /**
     * <p>Remove all the property keys associated with a specific
     * set definition and all values associated to the keys.</p>
     * @param propertySetDefId The property set definition id.
     * @throws PropertyException Thrown if the property set definition
     *                          is not found.
     */
    void removePropertyKeysBySetDef(int propertySetDefId)
        throws PropertyException;

    /**
     * <p>Retrieve all property keys id/name maps for a property
     * set definition.<p>
     * @param propertySetId The property set id.
     * @return Map of property keys id/name.
     * @throws PropertyException Throwns if no property set definition is found.
     */
    Map getPropertyKeysBySetDef(int propertySetId)
        throws PropertyException;

    /**
     * <p>Remove a property key.  This will remove the property key and
     * all property values associated with this key.</p>
     * @param propertyKeyId The property key id.
     */
    void removePropertyKey(int propertyKeyId);

    /**
     * <p>Update a property key.</p>
     * @param propertyKeyId The property key id.
     * @param propertyKeyName The property key name.
     */
    void updatePropertyKey(int propertyKeyId, String propertyKeyName);

}
