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

package org.apache.cornerstone.framework.constant;

/**
This class holds all framework-wide constants.  Class specific contants
should NOT be put here but in their own classes.
*/

public class Constant
{
    public static final String REVISION = "$Revision$";

    public static final String DOT = ".";
    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String DASH = "-";
    public static final String SLASH = "/";
    public static final String META = "_";

    /**
    The deliminator of configuration names.
    For example, "a.b", "service.className", etc.
    */
    public static final String CONF_DELIM = DOT;

    /**
    The string token deliminator.
    */
    public static final String TOKEN_DELIM = ",";

    /**
    The extension of configuration files.
    */
    public static final String FILE_EXTENSION_PROPERTIES = ".properties";

    /**
     * The main key for the MBeanManager to lookup by
     * for all object that were published by the BasePubSubmanager
     * within the BaseMonitoredObject.
     */
    public static final String JMX_MANAGED = "jmxManaged";

    public static final String UTF8 = "UTF-8";
    public static final String LOAD_ON_DEMAND = "loadOnDemand";
    public static final String TRUE = "true";

    public static final String INSTANCE = "instance";
    public static final String FACTORY = "factory";
    public static final String PARENT = "parent";
    public static final String CLASS_NAME = "className";
    public static final String NAME = "name";

    public static final String INSTANCE_CLASS_NAME = INSTANCE + DOT + CLASS_NAME;
    public static final String FACTORY_CLASS_NAME = FACTORY + DOT + CLASS_NAME;
    public static final String PARENT_NAME = PARENT + DOT + NAME;

    public static final String PROPERTY = "property";
    public static final String PROPERTY_DOT = PROPERTY + DOT;

    public static final String PRODUCT = "product";
    public static final String PRODUCT_DOT = PRODUCT + DOT;

    public static final String REGISTRY = "registry";
    public static final String SERVICE = "service";
    public static final String ACTION = "action";
    public static final String IMPLEMENTATION = "implementation";

	public static final String DRIVER = "driver";
	public static final String CONNECTION = "connection";
	public static final String URL = "url";

	public static final String METHOD_GET_SINGLETON = "getSingleton";
}