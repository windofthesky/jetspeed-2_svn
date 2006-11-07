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

package org.apache.jetspeed.serializer.objects;

/**
 * Serialized Name Value Pairs
 * <info>
 *   <name>user.first.name</name>
 *   <value>Paul</value>
 * </info>
 * 
 * @author <a href="mailto:hajo@bluesunrsie.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.*;

public class JSUserAttributes extends JSNameValuePairs
{


	/**
	 * @param preferences
	 */
	public JSUserAttributes(Preferences preferences)
	{
		super(preferences);
	}
    public JSUserAttributes()
    {
        super();
    }
}