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
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import org.apache.cornerstone.framework.api.factory.CreationException;
import org.apache.cornerstone.framework.api.registry.IRegistry;
import org.apache.cornerstone.framework.api.registry.IRegistryEntry;
import org.apache.cornerstone.framework.constant.Constant;
import org.apache.cornerstone.framework.factory.BaseFactory;
import org.apache.cornerstone.framework.registry.BaseRegistry;
import org.apache.cornerstone.framework.util.Util;
import org.apache.log4j.Logger;

public class PropertiesRegistryFactory extends BaseFactory
{
    public static final String REVISION = "$Revision$";

    public static final String SHORT_HAND = "shortHand";
    public static final String META_SHORT_HAND = Constant.META + Constant.DOT + SHORT_HAND + Constant.DASH;
    public static final String REG_DIR_NAME = "registry";
    public static final String REG_FILE_EXTENSION = ".reg.properties";

    public static final String CONFIG_FILE_NAMES_TO_SKIP = "fileNamesToSkip";

    public static PropertiesRegistryFactory getSingleton()
    {
        return _Singleton;
    }

    public Object createInstance() throws CreationException
    {
        throw new CreationException("please use the other signature to pass in registryParentPath");
    }        

    public Object createInstance(Object registryParentPath) throws CreationException
    {
        String registryPath = registryParentPath + File.separator + REG_DIR_NAME;        
                
        // create the Registry
        IRegistry registry = BaseRegistry.getSingleton();

        // for every file in this path added to the 
        // registry as RegistryEntry
        //
        File registryDir = new File(registryPath);
        File[] domains = registryDir.listFiles();
        if (domains != null)
        {
            PropertiesRegistryEntryFactory registryEntryFactory = PropertiesRegistryEntryFactory.getSingleton();
            for ( int k = 0; k < domains.length; k++ )
            {
                String domainName = domains[k].getName();
                if (skipFile(domainName)) continue;

                if ( domains[k].isDirectory() )
                {
                    File domainDir = new File(domains[k].getAbsolutePath());
                    File[] interfaces = domainDir.listFiles();
                    for ( int i = 0; i < interfaces.length; i++ )
                    {
                        String interfaceName = interfaces[i].getName();
                        if (skipFile(interfaceName)) continue;

                        if ( interfaces[i].isDirectory() )
                        {
                            File[] implementations = interfaces[i].listFiles();
                            for (int j = 0; j < implementations.length; j++)
                            {
                            	IRegistryEntry registryEntry = (IRegistryEntry)registryEntryFactory.createInstance(implementations[j]);
                                String implementationName = implementations[j].getName();
                            	String registryEntryName =
                                    implementationName.startsWith(Constant.DOT) ?
									"" :
                                    implementationName.substring(0, implementationName.indexOf(REG_FILE_EXTENSION));
                            	registry.register(domainName, interfaceName, registryEntryName, registryEntry);
                            }
                        }
                        else if (interfaceName.startsWith(META_SHORT_HAND))
                        {
                            String nameValuePair = interfaceName.substring(META_SHORT_HAND.length());
                            int dash = nameValuePair.indexOf(Constant.DASH);
                            if (dash > 0)
                            {
                                String alias = nameValuePair.substring(0, dash);
                                String fullName = nameValuePair.substring(dash + 1);
                                registry.setInterfaceShortHand(domainName, alias, fullName);
                            }
                        }
                        else
                        {
                        	_Logger.info("Unrecognized file '" + interfaces[i].getName() + "' found in registry '" + registryPath + "'; ignored");
                        }
                    }
                }
                else
                {
                    _Logger.info("Unrecognized file '" + domains[k].getName() + "' found in registry '" + registryPath + "'; ignored");
                }
            }
        }

        return registry;
    }

    protected PropertiesRegistryFactory()
    {
        super();
        String names = getConfigProperty(CONFIG_FILE_NAMES_TO_SKIP);
        List nameList = Util.convertStringsToList(names);
        _setOfFileNamesToSkip = new HashSet();
        _setOfFileNamesToSkip.addAll(nameList);
    }

    protected boolean skipFile(String fileName)
    {
        if (_setOfFileNamesToSkip != null)
        {
            return _setOfFileNamesToSkip.contains(fileName);
        }
        else
        {
            return false;
        }
    }

    private static Logger _Logger = Logger.getLogger(PropertiesRegistryFactory.class);
    private static PropertiesRegistryFactory _Singleton = new PropertiesRegistryFactory();
    protected Set _setOfFileNamesToSkip;
}