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
package org.apache.jetspeed.om.page;

import org.apache.jetspeed.om.common.SecuredResource;

/**
 * BaseElement
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface BaseElement extends SecuredResource
{
    /**
     * Returns the unique Id of this element. This id is guaranteed to be unique
     * from the complete portal and is suitable to be used as a unique key.
     *
     * @return the unique id of this element.
     */
    public String getId();

    /**
     * Modifies the id of this element. This id must not be null and must be unique
     * for the portal.
     *
     * @param id the new id for this element
     */
    public void setId(String id);

    /**
     * Returns the title in the default Locale
     *
     * @return the page title
     */
    public String getTitle();

    /**
     * Sets the title for the default Locale
     *
     * @param title the new title
     */
    public void setTitle(String title);

        
}
