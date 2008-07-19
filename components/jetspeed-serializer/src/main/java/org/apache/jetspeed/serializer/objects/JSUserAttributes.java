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

package org.apache.jetspeed.serializer.objects;

import java.util.prefs.*;

public class JSUserAttributes extends JSNVPElements
{


	/**
	 * @param preferences
	 */
	public JSUserAttributes(Preferences preferences)
	{
        // TODO: the JSNVPElements class doesn't support/use Preferences anymore
        //       because of its dual usage for PortletPreferences as well
        //       goto break these two usages apart and provide separate implementations
        // NOTE: JSVNPElements is't very well implemented anyway (doesn't seem to be able to handle multi-value elements...)
//		super(preferences);
	    super();
	}
    public JSUserAttributes()
    {
        super();
    }
}
