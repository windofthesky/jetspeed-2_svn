/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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