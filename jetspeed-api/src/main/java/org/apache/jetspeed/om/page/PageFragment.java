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
 * <p>A <code>PageFragment</code> is a fragment element that represents
 * a place holder for a root fragment of a page rendered with a page
 * template. It also contains all base fragment information that can
 * override/augment the target page root fragment configuration.</p>
 *
 * @version $Id:$
 */
public interface PageFragment extends BaseFragmentElement, Serializable
{
}
