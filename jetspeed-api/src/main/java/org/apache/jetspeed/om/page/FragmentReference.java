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

import java.io.Serializable;

/**
 * <p>A <code>FragmentReference</code> is a fragment element that represents
 * a place holder for a referenced fragment in a Page. It also contains all
 * base fragment information that can override/augment the target fragment
 * configuration.</p>
 *
 * @version $Id:$
 */
public interface FragmentReference extends BaseFragmentElement, Serializable
{
    /**
     * Returns the id of the referenced fragment element.
     *
     * @return the referenced fragment id.
     */
    String getRefId();

    /**
     * Sets the id of the referenced fragment element.
     *
     * @param refId the id of the referenced fragment element.
     */
    void setRefId(String refId);
}
