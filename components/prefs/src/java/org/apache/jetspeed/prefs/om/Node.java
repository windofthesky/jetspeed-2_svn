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
import java.util.Collection;
import java.sql.Timestamp;

/**
 * <p>Interface representing a {@link java.util.prefs.Preferences}
 * node.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public interface Node extends Serializable, Cloneable
{

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
     * <p>Getter for the parent node id.</p>
     * <p>Passed as an Integer to be able to pass null if no parent
     * is associated to a node.</p>
     * @return The parent node id.
     */
    Integer getParentNodeId();

    /**
     * <p>Setter for the parent node id.</p>
     * @param parentNodeId The parent node id.
     */
    void setParentNodeId(Integer parentNodeId);

    /**
     * <p>Getter for the node properties.</p>
     * @return The property set properties.
     */
    Collection getProperties();

    /**
     * <p>Setter for the node properties.</p>
     * @param properties The property set properties.
     */
    void setProperties(Collection properties);

    /**
     * <p>Getter for the property set definition id.</p>
     * @return The property set definition id.
     */
    Integer getPropertySetDefId();

    /**
     * <p>Setter for the property set definition id.</p>
     * @param propertySetDefId The property set definition id.
     */
    void setPropertySetDefId(Integer propertySetDefId);

    /**
     * <p>Getter for the property set definition.</p>
     * @return The property set definition.
     */
    PropertySetDef getPropertySetDef();

    /**
     * <p>Setter for the property set definition.</p>
     * @param propertySetDef The property set definition.
     */
    void setPropertySetDef(PropertySetDef propertySetDef);

    /**
     * <p>Getter for the node name.</p>
     * @return The node name.
     */
     String getNodeName();

    /**
     * <p>Setter for the node name.</p>
     * @param nodeName The node name.
     */
     void setNodeName(String nodeName);

    /**
     * <p>Getter for the node type.</p>
     * <ul>
     *     <li>0=user,</li>
     *     <li>1=system,</li>
     * </ul>
     * @return The node type.
     */
    short getNodeType();

    /**
     * <p>Setter for the node type.</p>
     * <ul>
     *     <li>0=user,</li>
     *     <li>1=system,</li>
     * </ul>
     * @param nodeType The node type.
     */
    void setNodeType(short nodeType);

    /**
     * <p>Getter for the full path.</p>
     * @return The full path.
     */
    String getFullPath();

    /**
     * <p>Setter for the full path.</p>
     * @param fullPath The full path.
     */
    void setFullPath(String fullPath);

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
