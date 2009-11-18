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
package org.apache.jetspeed.portlets.layout;

import org.apache.jetspeed.om.page.ContentFragment;


/**
 * This exception indicates that an attempt was made get the coordinates
 * within a layout for a fragment that is not within that layout.
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @see org.apache.jetspeed.portlets.layout.ColumnLayout
 */
public class FragmentNotInLayoutException extends LayoutException
{
    public FragmentNotInLayoutException(ContentFragment fragment)
    {
       super("The fragment "+fragment != null ?fragment.getId():"{null fragment}"+" could not be located in this layout.");
       
    }

}
