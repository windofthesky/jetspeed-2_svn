/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.common.portlet;

import java.io.Serializable;
import java.util.Collection;

import javax.portlet.PortletMode;

import org.apache.pluto.om.portlet.ContentType;
/**
 * 
 * ContentTypeComposite
 * 
 * Combines the <code>org.apache.pluto.common.ContentType</code>
 * and <code>org.apache.pluto.common.ContentTypeCtrl</code> interfaces
 * into single interface for use in Jetspeed.
 * 
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface ContentTypeComposite extends ContentType, Serializable
{
    void setPortletModes(Collection modes);

    /**
     * Adds a mode to be supported by this <code>ContentType</code>.  If the mode
     * already exists, the same mode is NOT added again.
     * @param mode portlet mode to add.
     */
    void addPortletMode(PortletMode mode);

    /**
     * Checks whether or not the <code>mode</code>
     * is supported by this <code>ContentType</code>
     * @param mode portlet mode to check
     * @return <code>true</code> if the <code>mode</code> is
     * supported, otherwise <code>false</code>.
     */
    boolean supportsPortletMode(PortletMode mode);

    /**
     * 
     * @param contentType
     */
    void setContentType(String contentType);
}
