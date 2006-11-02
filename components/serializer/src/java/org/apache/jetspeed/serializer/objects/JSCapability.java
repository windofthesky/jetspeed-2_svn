/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.serializer.objects;
import org.apache.jetspeed.capabilities.Capability;
/**
 * Jetspeed Serializer - Capability Wrapper
 * <p>
 * Wrapper to process XML representation of a capability
 * 
 * @author <a href="mailto:hajo@bluesunrise.com">Hajo Birthelmer</a>
 * @version $Id: $
 */
public class JSCapability extends JSSimpleIDName
{
	public JSCapability(Capability source)
	{
		super(source.getCapabilityId(),source.getName());   
		
	}
    public JSCapability()
    {
        super();   
        
    }
}
