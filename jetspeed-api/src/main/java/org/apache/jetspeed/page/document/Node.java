/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.page.document;

import java.util.Locale;

import org.apache.jetspeed.om.page.BaseElement;
import org.apache.jetspeed.om.portlet.GenericMetadata;

/**
 * <p>
 * Node
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface Node extends BaseElement
{
    String PATH_SEPARATOR = "/";
    char PATH_SEPARATOR_CHAR = '/';

    /**
     * 
     * <p>
     * getParent
     * </p>
     *
     * @return
     */
    Node getParent();
    
    /**
     * 
     * <p>
     * setParent
     * </p>
     *
     * @param parent
     */
    void setParent(Node parent);
    
    /**
     * 
     * <p>
     * getPath
     * </p>
     *
     * @return
     */
    String getPath();

    /**
     * 
     * <p>
     * getName
     * </p>
     * 
     * Returns the name of this node relative to 
     * <code>Node.getParent().getPath()</code>
     *
     * @return Name, relative to the parent node.
     */
    String getName();
    
    /**
     * 
     * <p>
     * setPath
     * </p>
     * Sets the full-qualified path of this node.
     *
     * @param path
     */
    void setPath(String path);
    
   
    /**
     * 
     * <p>
     * getMetadata
     * </p>
     *
     * @return
     */
    GenericMetadata getMetadata();

    /**
     * 
     * <p>
     * getTitle
     * </p>
     * Returns the title for the specified locale.
     *
     * @param locale
     * @return localized title of this Node.
     */
    String getTitle(Locale locale);

    /**
     * 
     * <p>
     * getShortTitle
     * </p>
     * Returns the short title for the specified locale.
     *
     * @param locale
     * @return localized title of this Node.
     */
    String getShortTitle(Locale locale);

    /**
     * 
     * <p>
     * getType
     * </p>
     * 
     * @return
     */
    String getType();
    
    /**
     * 
     * <p>
     * getUrl
     * </p>
     * 
     * @return
     */
    String getUrl();
    
    /**
     * 
     * <p>
     * isHidden
     * </p>
     * <p>
     *  Whether or not this Node should be hidden in terms of the view.  This MUST NOT restrict
     *  the presence of this node in terms of being returned in 
     *  {@link NodeSets org.apache.jetspeed.page.document.NodeSet}. 
     * </p>
     * @return hidden flag
     */
    boolean isHidden();

    /**
     * 
     * <p>
     * setHidden
     * </p>
     * <p>
     *  Whether or not this Node should be hidden in terms of the view.
     * </p>
     * @param hidden flag
     */
    void setHidden(boolean hidden);
}
