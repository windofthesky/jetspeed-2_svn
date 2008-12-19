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
package org.apache.jetspeed.security;

import java.io.Serializable;

/**
 * @version $Id$
 */
public interface SecurityAttributeType extends Serializable
{
    /** 
     * build-in supported SecurityAttributeType category "info"
     * This category is also used for custom/extended attributes which are not pre-defined
     */
    String INFO_CATEGORY = "info";
    
    /** 
     * build-in supported SecurityAttributeType category "jetspeed"
     * This category is used for jetspeed internal attributes
     */
    String JETSPEED_CATEGORY = "jetspeed";

    enum DataType { STRING }

    String getName();

    String getCategory();

    DataType getDataType();

    boolean isReadOnly();

    boolean isRequired();

    boolean isRegistered();
}
