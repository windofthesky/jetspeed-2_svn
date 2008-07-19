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

package org.apache.jetspeed.attribute;

import java.util.Set;

/**
 * {@link GenericAttribute} container
 * <p>
 * Generic Attributes can be leveraged to store value(s) for a fixed set of predefined attribute names.
 * </p>
 * <p>
 * The whole set of provided attributes can be readonly, or individual attributes can be readonly.<br/>
 * When the whole set is readonly, all similar sets (e.g. with the same {@#getType} will be readonly.
 * </p>
 * <p>
 * The {@link #getAttribute(), current} set of attributes can contain a subset of all the {@link #getSupportedNames() supported}
 * attributes.
 * </p>
 * Trying to access an unsupported attribute through {@link #getAttribute(String)} will throw a {@link GenericAttributeRuntimeException}
 * of type {@link GenericAttributeRuntimeException#ATTRIBUTE_UNSUPPORTED}.
 * @version $Id$
 *
 */
public interface GenericAttributes
{
    String getType();
    int size();
    boolean isEmpty();
    boolean isReadOnly();
    Set<String> getNames();
    Set<String> getSupportedNames();
    Set<GenericAttribute> getAttributes();
    GenericAttribute getAttribute(String name)  throws GenericAttributeRuntimeException;
}
