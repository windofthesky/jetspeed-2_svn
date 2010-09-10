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
    String getId();

    /**
     * Returns the title in the default Locale
     *
     * @return the page title
     */
    String getTitle();

    /**
     * Sets the title for the default Locale
     *
     * @param title the new title
     */
    void setTitle(String title);

    /**
     * Returns the short title in the default Locale
     *
     * @return the page short title
     */
    String getShortTitle();

    /**
     * Sets the short title for the default Locale
     *
     * @param title the new title
     */
    void setShortTitle(String title);

    /**
     * Returns whether this object is stale and should be
     * refreshed from the page manager. This flag need only
     * be checked against page manager elements that are
     * cached externally outside the scope of a single
     * request, (e.g. cached in a session).
     * 
     * @return stale status flag
     */
    boolean isStale();
}
