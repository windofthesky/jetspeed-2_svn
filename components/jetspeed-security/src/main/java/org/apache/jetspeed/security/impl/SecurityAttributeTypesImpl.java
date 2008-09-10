/*	
 * Licensed to the Apache Software Foundation (ASF) under one or more&#13;
 * contributor license agreements.  See the NOTICE file distributed with&#13;
 * this work for additional information regarding copyright ownership.&#13;
 * The ASF licenses this file to You under the Apache License, Version 2.0&#13;
 * (the "License"); you may not use this file except in compliance with&#13;
 * the License.  You may obtain a copy of the License at&#13;
 * &#13;
 *      http://www.apache.org/licenses/LICENSE-2.0&#13;
 * &#13;
 * Unless required by applicable law or agreed to in writing, software&#13;
 * distributed under the License is distributed on an "AS IS" BASIS,&#13;
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.&#13;
 * See the License for the specific language governing permissions and&#13;
 * limitations under the License.&#13;
 */
package org.apache.jetspeed.security.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.security.SecurityAttributeType;
import org.apache.jetspeed.security.SecurityAttributeTypes;

/**
 * @version $Id$
 *
 */
public class SecurityAttributeTypesImpl implements SecurityAttributeTypes
{

    private Map<String,SecurityAttributeType> securityAttributeTypes = new HashMap<String, SecurityAttributeType>();
    private boolean readOnly;
    private boolean extendable;
	
	public SecurityAttributeTypesImpl(boolean extendable, boolean readOnly, Map<String, SecurityAttributeType> securityAttributeTypes)
	{
		this.extendable = extendable;
		this.readOnly = readOnly;
		this.securityAttributeTypes = securityAttributeTypes;
	}

	public Map<String, SecurityAttributeType> getAttributeTypeMap()
	{	
		return securityAttributeTypes;
	}

	public Map<String, SecurityAttributeType> getAttributeTypeMap(String category)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isExtendable()
	{
		return extendable;
	}

	public boolean isReadOnly()
	{
		return readOnly;
	}

}
