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
import java.util.Map;
import java.util.prefs.Preferences;


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

    /**
     * <p>Add a set of property keys to a {@link Preferences} node.  Only
     * keys added to a node can be set on the {@link Preferences} node.</p>
     * <p>Property keys should be passed as a map of:</p>
     * <ul>
     *      <li><code>PROPERTYKEY_NAME</code>: The property key name of type
     *      <code>java.lang.String</code>.</li>
     *      <li><code>PROPERTYKEY_TYPE</code>: The property key type of type
     *      <code>java.lang.Short</code>.</li>
     * </ul>
     * <p>The Map of [PROPERTYKEY_NAME, PROPERTYKEY_TYPE] of properties to be 
     * added to the {@link Preferences} is passed to the method.</p>
     * <p>The property names associated to a node must be unique.</p>
     * @param prefNode The {@link Preferences} node.
     * @param propertyKeys A map property key name / key type.
     * @throws PropertyException Thrown if any property in the
     *                           set in already assigned to a property set
     *                           definition.
     * @throws PreferencesException
     */
    void addPropertyKeys(Preferences prefNode, Map propertyKeysMap) throws PropertyException, PreferencesException;

    /**
     * <p>Returns the property keys available to a {@link Preferences} node whether
     * or node those keys have values assigned to them.</p>
     * <p>Property keys will be returned as a map of:</p>
     * <ul>
     *      <li><code>PROPERTYKEY_NAME</code>: The property key name of type
     *      <code>java.lang.String</code>.</li>
     *      <li><code>PROPERTYKEY_TYPE</code>: The property key type of type
     *      <code>java.lang.Short</code>.</li>
     * </ul>
     * @param prefNode The {@link Preferences} node.
     * @return The map of property keys names / types.
     * @throws PreferencesException
     */
    Map getPropertyKeys(Preferences prefNode) throws PreferencesException;

    /**
     * <p>Remove the specified collection of property keys from the given preferences node.</p>
     * @param prefNode The {@link Preferences} node.
     * @param propertyKeys A collection of property key names.
     * @throws PropertyException Throws if delete fails.
     * @throws PreferencesException
     */
    void removePropertyKeys(Preferences prefNode, Collection propertyKeys) throws PropertyException, PreferencesException;

    /**
     * <p>Update a property key.</p>
     * @param oldPropertyKeyName The old property key name.
     * @param prefNode The {@link Preferences} node.
     * @param newPropertyKey The property key name / type map used to
     *                       update the old property.
     * @throws PropertyException Throws if update fails.
     * @throws PreferencesException
     */
    void updatePropertyKey(String oldPropertyKeyName, Preferences prefNode, Map newPropertyKey)
        throws PropertyException, PreferencesException;

}
