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
package org.apache.jetspeed.spi.om.prefs.impl;

import java.util.Collection;
import java.sql.Timestamp;

import org.apache.jetspeed.spi.om.prefs.Node;
import org.apache.jetspeed.spi.om.prefs.PropertySetDef;

/**
 * <p>{@link Node} interface implementation.</p>
 * <p>Represents a preferences node.</p>
 *
 * @author <a href="david@sensova.com">David Le Strat</a>
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
     * @see org.apache.jetspeed.spi.om.prefs.Node#getNodeId()
     */
    public int getNodeId()
    {
        return this.nodeId;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#setNodeId(int)
     */
    public void setNodeId(int nodeId)
    {
        this.nodeId = nodeId;
    }

    private Integer parentNodeId;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#getParentNodeId()
     */
    public Integer getParentNodeId()
    {
        return this.parentNodeId;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#setParentNodeId(java.lang.Integer)
     */
    public void setParentNodeId(Integer parentNodeId)
    {
        this.parentNodeId = parentNodeId;
    }

    private Collection properties;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#getProperties()
     */
    public Collection getProperties()
    {
        return this.properties;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#setProperties(java.util.Collection)
     */
    public void setProperties(Collection properties)
    {
        this.properties = properties;
    }

    private Integer propertySetDefId;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#getPropertySetDefId()
     */
    public Integer getPropertySetDefId()
    {
        return this.propertySetDefId;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#setPropertySetDefId(java.lang.Integer)
     */
    public void setPropertySetDefId(Integer propertySetDefId)
    {
        this.propertySetDefId = propertySetDefId;
    }

    private PropertySetDef propertySetDef;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#getPropertySetDef()
     */
    public PropertySetDef getPropertySetDef()
    {
        return this.propertySetDef;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#setPropertySetDef(org.apache.jetspeed.spi.om.prefs.PropertySetDef)
     */
    public void setPropertySetDef(PropertySetDef propertySetDef)
    {
        this.propertySetDef = propertySetDef;
    }

    private String nodeName;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#getNodeName()
     */
    public String getNodeName()
    {
        return this.nodeName;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#setNodeName(java.lang.String)
     */
    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
    }

    private short nodeType;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#getNodeType()
     */
    public short getNodeType()
    {
        return this.nodeType;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#setNodeType(short)
     */
    public void setNodeType(short nodeType)
    {
        this.nodeType = nodeType;
    }

    private String fullPath;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#getFullPath()
     */
    public String getFullPath()
    {
        return this.fullPath;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#setFullPath(java.lang.String)
     */
    public void setFullPath(String fullPath)
    {
        this.fullPath = fullPath;
    }

    private Timestamp creationDate;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#getCreationDate()
     */
    public Timestamp getCreationDate()
    {
        return this.creationDate;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#setCreationDate(java.sql.Timestamp)
     */
    public void setCreationDate(Timestamp creationDate)
    {
        this.creationDate = creationDate;
    }

    private Timestamp modifiedDate;

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#getModifiedDate()
     */
    public Timestamp getModifiedDate()
    {
        return this.modifiedDate;
    }

    /**
     * @see org.apache.jetspeed.spi.om.prefs.Node#setModifiedDate(java.sql.Timestamp)
     */
    public void setModifiedDate(Timestamp modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

}
