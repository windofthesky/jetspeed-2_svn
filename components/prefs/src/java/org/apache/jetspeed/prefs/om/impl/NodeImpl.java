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
package org.apache.jetspeed.prefs.om.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.Timestamp;

import org.apache.jetspeed.prefs.om.Node;

/**
 * <p>{@link Node} interface implementation.</p>
 * <p>Represents a preferences node.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class NodeImpl implements Node
{
    private long nodeId;
    private Long parentNodeId;
    private Collection nodeProperties;
    private Collection nodeKeys;
    private String nodeName;
    private int nodeType;
    private String fullPath;
    private Timestamp creationDate;

    /**
     * <p>Preferences node implementation default constructor.</p>
     */
    public NodeImpl()
    {
    }

    /**
     * <p>Node constructor given:</p>
     * <ul>
     *  <li>Parent node id,</li>
     *  <li>Property set definition id: Long so that we can
     *  pass null value if the node does not have any properties
     *  associated to it,</li>
     *  <li>Node name,</li>
     *  <li>Node type,</li>
     *  <li>Full path.</li>
     * </ul>
     * @param parentNodeId The parent node id.
     * @param nodeName The node name.
     * @param nodeType The node type.
     * @param fullPath The full path.
     */
    public NodeImpl(Long parentNodeId, String nodeName,
                    int nodeType, String fullPath)
    {
        this.parentNodeId = parentNodeId;
        this.nodeName = nodeName;
        this.nodeType = nodeType;
        this.fullPath = fullPath;
        this.nodeKeys = new ArrayList(0);
        this.nodeProperties = new ArrayList(0);
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getNodeId()
     */
    public long getNodeId()
    {
        return this.nodeId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setNodeId(int)
     */
    public void setNodeId(long nodeId)
    {
        this.nodeId = nodeId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getParentNodeId()
     */
    public Long getParentNodeId()
    {
        return this.parentNodeId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setParentNodeId(java.lang.Long)
     */
    public void setParentNodeId(Long parentNodeId)
    {
        this.parentNodeId = parentNodeId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getNodeProperties()
     */
    public Collection getNodeProperties()
    {
        return this.nodeProperties;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setNodeProperties(java.util.Collection)
     */
    public void setNodeProperties(Collection nodeProperties)
    {
        this.nodeProperties = nodeProperties;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getNodeKeys()
     */
    public Collection getNodeKeys()
    {
        return this.nodeKeys;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setNodeKeys(java.util.Collection)
     */
    public void setNodeKeys(Collection nodeKeys)
    {
        this.nodeKeys = nodeKeys;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getNodeName()
     */
    public String getNodeName()
    {
        return this.nodeName;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setNodeName(java.lang.String)
     */
    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getNodeType()
     */
    public int getNodeType()
    {
        return this.nodeType;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setNodeType(int)
     */
    public void setNodeType(int nodeType)
    {
        this.nodeType = nodeType;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getFullPath()
     */
    public String getFullPath()
    {
        return this.fullPath;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setFullPath(java.lang.String)
     */
    public void setFullPath(String fullPath)
    {
        this.fullPath = fullPath;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        return this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setCreationDate(java.sql.Timestamp)
     */
    public void setCreationDate(Timestamp creationDate)
    {
        this.creationDate = creationDate;
    }

    private Timestamp modifiedDate;

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    /**
     * <p>Convert <code>Node</code> to string.</p>
     * @return The Node string value.
     */
    public String toString()
    {
        String toStringNode = "[[parentNodeId, " + this.parentNodeId + "], "
            + "[nodeName, " + this.nodeName + "], "
            + "[fullPath, " + this.fullPath + "], "
            + "[nodeType, " + this.nodeType + "], "
            + "[nodeKeys, " + this.nodeKeys + "], "
            + "[nodeProperties, " + this.nodeProperties + "], "
            + "[creationDate, " + this.creationDate + "], "
            + "[modifiedDate, " + this.modifiedDate + "]]";
        return toStringNode;
    }

}
