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
    long getNodeId();

    /**
     * <p>Setter for the node id.</p>
     * @param nodeId The node id.
     */
    void setNodeId(long nodeId);

    /**
     * <p>Getter for the parent node id.</p>
     * <p>Passed as an Integer to be able to pass null if no parent
     * is associated to a node.</p>
     * @return The parent node id.
     */
    Long getParentNodeId();

    /**
     * <p>Setter for the parent node id.</p>
     * @param parentNodeId The parent node id.
     */
    void setParentNodeId(Long parentNodeId);

    /**
     * <p>Getter for the node properties.</p>
     * @return The node properties.
     */
    Collection getNodeProperties();

    /**
     * <p>Setter for the node properties.</p>
     * @param properties The node properties.
     */
    void setNodeProperties(Collection nodeProperties);

    /**
     * <p>Getter for the keys associated to a specific nodes.</p>
     * @return The node keys.
     */
    Collection getNodeKeys();

    /**
     * <p>Setter for the keys associated to a specific nodes.</p>
     * @param nodeKeys The node keys.
     */
    void setNodeKeys(Collection nodeKeys);

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
    int getNodeType();

    /**
     * <p>Setter for the node type.</p>
     * <ul>
     *     <li>0=user,</li>
     *     <li>1=system,</li>
     * </ul>
     * @param nodeType The node type.
     */
    void setNodeType(int nodeType);

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
