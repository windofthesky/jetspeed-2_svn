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
