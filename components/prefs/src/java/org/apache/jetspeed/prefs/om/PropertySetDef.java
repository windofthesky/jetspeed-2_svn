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
     * <p>Getter for the set nodes.</p>
     * @return The nodes collection.
     */
    Collection getNodes();

    /**
     * <p>Setter for the set nodes.</p>
     * @param nodes The nodes set.
     */
    void setNodes(Collection nodes);

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

