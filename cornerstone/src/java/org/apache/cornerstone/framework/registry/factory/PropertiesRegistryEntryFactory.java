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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.api.registry.IRegistryEntry;
import org.apache.cornerstone.framework.factory.BaseFactory;
import org.apache.cornerstone.framework.registry.BaseRegistryEntry;
import org.apache.log4j.Logger;

public class PropertiesRegistryEntryFactory extends BaseFactory
{
    public static final String REVISION = "$Revision$";

    public static PropertiesRegistryEntryFactory getSingleton()
    {
        return _Singleton;
    }

    /* (non-Javadoc)
     * @see org.apache.cornerstone.framework.factory.BaseFactory#createInstance()
     */
    public Object createInstance() throws CreationException
    {
    	throw new CreationException("please use the other signature that uses a File");
    }

    /**
     * Creates a RegistryEntry object
     * @return Object as RegistryEntry
     */
    public Object createInstance(Object file) throws CreationException
    {
        File registryEntryFile = (File) file;
        FileInputStream fis = null;
        try
        {
            Properties registryProperties = new Properties();
            fis = new FileInputStream(registryEntryFile);
            registryProperties.load(fis);
            IRegistryEntry registryEntry = new BaseRegistryEntry(registryProperties);
            return registryEntry;
        }
        catch(IOException e)
        {
            String message = "failed to read registry entry file '" + registryEntryFile.getAbsolutePath() + "'";
            _Logger.error(message, e);
            throw new CreationException(message, e);
        }
        finally
        {
        	if (fis != null)
            {   
				try
				{
					fis.close();
				}
				catch (IOException e1)
				{
                    _Logger.error("failed to close registry entry file '" + registryEntryFile.getAbsolutePath() + "'");
				}
            }
        }
    }
    
    private static Logger _Logger = Logger.getLogger(PropertiesRegistryEntryFactory.class);
    private static PropertiesRegistryEntryFactory _Singleton = new PropertiesRegistryEntryFactory();
}