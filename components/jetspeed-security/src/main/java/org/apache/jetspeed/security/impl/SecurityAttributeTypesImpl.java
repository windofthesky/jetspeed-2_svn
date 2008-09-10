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

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jetspeed.security.SecurityAttributeType;
import org.apache.jetspeed.security.SecurityAttributeTypes;

/**
 * @version $Id$
 *
 */
public class SecurityAttributeTypesImpl implements SecurityAttributeTypes
{

    private Map<String,SecurityAttributeType> securityAttributeTypes = new LinkedHashMap<String, SecurityAttributeType>();
    private Map<String,Map<String,SecurityAttributeType>> categoriesMap = new HashMap<String,Map<String,SecurityAttributeType>>();
    private boolean readOnly;
    private boolean extendable;
	
	public SecurityAttributeTypesImpl(boolean extendable, boolean readOnly, List<SecurityAttributeType> securityAttributeTypes)
	{
		this.extendable = extendable;
		this.readOnly = readOnly;
		for (SecurityAttributeType type : securityAttributeTypes)
		{
		    if (this.securityAttributeTypes.put(type.getName(), type) != null)
		    {
		        throw new IllegalArgumentException("Duplicate SecurityAttributeType name: "+type.getName());
		    }
		    Map<String,SecurityAttributeType> categoryMap = categoriesMap.get(type.getCategory());
		    if (categoryMap == null)
		    {
		        categoryMap = new LinkedHashMap<String,SecurityAttributeType>();
		        categoriesMap.put(type.getCategory(), categoryMap);
		    }
		    categoryMap.put(type.getName(), type);
		}
		for (Map.Entry<String, Map<String, SecurityAttributeType>> entry : categoriesMap.entrySet())
		{
		    entry.setValue(Collections.unmodifiableMap(entry.getValue()));
		}
        this.securityAttributeTypes = Collections.unmodifiableMap(this.securityAttributeTypes);
	}

    public SecurityAttributeTypesImpl(List<SecurityAttributeType> securityAttributeTypes)
    {
        this(true, false, securityAttributeTypes);
    }
    
	public Map<String, SecurityAttributeType> getAttributeTypeMap()
	{	
		return securityAttributeTypes;
	}

	public Map<String, SecurityAttributeType> getAttributeTypeMap(String category)
	{
	    Map<String, SecurityAttributeType> map = categoriesMap.get(category);
	    if (map == null)
	    {
	        return Collections.EMPTY_MAP;
	    }
	    return map;
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
