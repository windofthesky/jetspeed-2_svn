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

import java.util.Collection;
import java.sql.Timestamp;

import org.apache.jetspeed.prefs.om.Node;
import org.apache.jetspeed.prefs.om.PropertySetDef;

/**
 * <p>{@link Node} interface implementation.</p>
 * <p>Represents a preferences node.</p>
 *
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat</a>
 */
public class NodeImpl implements Node
{

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
     *  <li>Property set definition id: Integer so that we can
     *  pass null value if the node does not have any properties
     *  associated to it,</li>
     *  <li>Node name,</li>
     *  <li>Node type,</li>
     *  <li>Full path.</li>
     * </ul>
     * @param parentNodeId The parent node id.
     * @param propertySetDefId The property set definition id.
     * @param nodeName The node name.
     * @param nodeType The node type.
     * @param fullPath The full path.
     */
    public NodeImpl(Integer parentNodeId, Integer propertySetDefId,
                    String nodeName, short nodeType, String fullPath)
    {
        this.parentNodeId = parentNodeId;
        this.propertySetDefId = propertySetDefId;
        this.nodeName =
            ((nodeName != null) ? nodeName.toLowerCase() : nodeName);
        this.nodeType = nodeType;
        this.fullPath = fullPath;
        this.creationDate = new Timestamp(System.currentTimeMillis());
        this.modifiedDate = this.creationDate;
    }

    private int nodeId;

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getNodeId()
     */
    public int getNodeId()
    {
        return this.nodeId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setNodeId(int)
     */
    public void setNodeId(int nodeId)
    {
        this.nodeId = nodeId;
    }

    private Integer parentNodeId;

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getParentNodeId()
     */
    public Integer getParentNodeId()
    {
        return this.parentNodeId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setParentNodeId(java.lang.Integer)
     */
    public void setParentNodeId(Integer parentNodeId)
    {
        this.parentNodeId = parentNodeId;
    }

    private Collection properties;

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getProperties()
     */
    public Collection getProperties()
    {
        return this.properties;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setProperties(java.util.Collection)
     */
    public void setProperties(Collection properties)
    {
        this.properties = properties;
    }

    private Integer propertySetDefId;

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getPropertySetDefId()
     */
    public Integer getPropertySetDefId()
    {
        return this.propertySetDefId;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setPropertySetDefId(java.lang.Integer)
     */
    public void setPropertySetDefId(Integer propertySetDefId)
    {
        this.propertySetDefId = propertySetDefId;
    }

    private PropertySetDef propertySetDef;

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getPropertySetDef()
     */
    public PropertySetDef getPropertySetDef()
    {
        return this.propertySetDef;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setPropertySetDef(org.apache.jetspeed.prefs.om.PropertySetDef)
     */
    public void setPropertySetDef(PropertySetDef propertySetDef)
    {
        this.propertySetDef = propertySetDef;
    }

    private String nodeName;

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

    private short nodeType;

    /**
     * @see org.apache.jetspeed.prefs.om.Node#getNodeType()
     */
    public short getNodeType()
    {
        return this.nodeType;
    }

    /**
     * @see org.apache.jetspeed.prefs.om.Node#setNodeType(short)
     */
    public void setNodeType(short nodeType)
    {
        this.nodeType = nodeType;
    }

    private String fullPath;

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

    private Timestamp creationDate;

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

}
