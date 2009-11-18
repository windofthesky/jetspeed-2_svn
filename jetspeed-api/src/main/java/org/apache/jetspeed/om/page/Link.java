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

/**
 * <p>
 * Link
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface Link extends Document
{
    String DOCUMENT_TYPE = ".link";
    
    /**
     * Returns the name of the skin associated to this link
     */
    String getSkin();

    /**
     * Defines the skin for this link. This skin should be
     * known by the portal.
     *
     * @param skinName the name of the new skin applied to this link
     */
    void setSkin(String skinName);

    /**
     * @param url The url to set.
     */
    void setUrl( String url );

    /**
     * @return Returns the target.
     */
    String getTarget();

    /**
     * @param target The target to set.
     */
    void setTarget( String target );
}
