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
package org.apache.jetspeed.prefs;

import java.util.Collection;

import org.apache.jetspeed.prefs.impl.PropertyException;

/**
 * <p>Service used to manage property and property set definition.<p>
 * <p>A property set definition defines a property set and the possible
 * properties assigned to that set. All or a subset of the property
 * set definition properties can be assigned to a node.<p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface PropertyManager
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
     * @throws PropertyException Thrown if the property set
     *                           definition already exists.
     */
    void addPropertySetDef(String propertySetName, short propertySetType) throws PropertyException;

    /**
     * <p>Get a property set definition id for a specify
     * property set name and property set type.</p>
     * @param propertySetName The property set definition name to lookup.
     * @param propertySetType The property set definition type to lookup.
     * @return The property set definition id.
     * @throws PropertyException Throwns if no property set definition is found.
     */
    //int getPropertySetDefIdByType(String propertySetName, short propertySetType) throws PropertyException;

    /**
     * <p>Remove a property set definition and all associated
     * property keys and values as well as property sets.</p>
     * @param propertySetName The property set definition name to remove.
     * @param propertySetType The property set definition type to remove.
     * @throws PropertyException Thrown if the property set definition
     *                           does not exist.
     */
    void removePropertySetDef(String propertySetName, short propertySetType) throws PropertyException;

    /**
     * <p>Update a property set definition.</p>
     * @param newPropertySetName The new property set name.
     * @param oldPropertySetName The old property set name.
     * @param propertySetType The property set type.
     * @throws PropertyException Thrown if the update fails.
     */
    void updatePropertySetDef(String newPropertySetName, String oldPropertySetName, short propertySetType)
        throws PropertyException;

    /**
     * <p>This method returns a collection of property set definition
     * name for a given property set type.</p>
     * @param propertySetType The property set definition type.
     * @return Th e collection of property set definition for the given type.
     * @throws PropertyException Thrown if no property set definition is found.
     *
     */
    Collection getAllPropertySetsByType(short propertySetType) throws PropertyException;

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
     * @param propertySetName The property set definition name to add keys to.
     * @param propertySetType The property set definition type to add keys to.
     * @param propertyKeys A map property key name / key type.
     * @throws PropertyException Thrown if any property in the
     *                           set in already assigned to a property set
     *                           definition.
     */
    void addPropertyKeys(String propertySetName, short propertySetType, Collection propertyKeys) throws PropertyException;

    /**
     * <p>Remove all the property keys associated with a specific
     * set definition and all values associated to the keys.</p>
     * @param propertySetDefId The property set definition id.
     * @throws PropertyException Thrown if the property set definition
     *                           is not found.
     */
    //void removePropertyKeysBySetDef(int propertySetDefId) throws PropertyException;

    /**
     * <p>Retrieve all property keys id/name maps for a property
     * set definition.<p>
     * @param propertySetName The property set definition name to get the keys from.
     * @param propertySetType The property set definition type to get the keys from.
     * @return Collection of property keys names.
     * @throws PropertyException Throwns if no property set definition is found.
     */
    Collection getPropertyKeysBySetDef(String propertySetName, short propertySetType) throws PropertyException;

    /**
     * <p>Remove a property key.  This will remove the property key and
     * all property values associated with this key.</p>
     * @param propertyKeyId The property key id.
     * @throws PropertyException Throws if delete fails.
     */
    void removePropertyKey(int propertyKeyId) throws PropertyException;

    /**
     * <p>Update a property key.</p>
     * @param newPropertyKeyName The new property key name.
     * @param oldPropertyKeyName The old property key name.
     * @param propertySetName The property set definition name for which update the key.
     * @param propertySetType The property set definition type for which update the key
     * @throws PropertyException Throws if update fails.
     */
    void updatePropertyKey(String newPropertyKeyName, String oldPropertyKeyName, String propertySetName, short propertySetType)
        throws PropertyException;

}
