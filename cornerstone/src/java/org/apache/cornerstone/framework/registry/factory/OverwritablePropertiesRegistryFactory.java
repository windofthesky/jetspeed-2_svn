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

package org.apache.cornerstone.framework.registry.factory;

import java.io.File;
import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.factory.BaseFactory;

public class OverwritablePropertiesRegistryFactory extends BaseFactory
{
    public static final String REVISION = "$Revision$";

    public static final String REG_DIR_NAME = "registry";

	/* (non-Javadoc)
	 * @see org.apache.cornerstone.framework.api.factory.IFactory#createInstance()
	 */
	public Object createInstance() throws CreationException
	{
        throw new CreationException("please use the other signature to pass in registryParentPath");
    }        

    public Object createInstance(Object registryParentPath) throws CreationException
    {
        String registryPath = registryParentPath + File.separator + REG_DIR_NAME;        
        return null;
    }
}