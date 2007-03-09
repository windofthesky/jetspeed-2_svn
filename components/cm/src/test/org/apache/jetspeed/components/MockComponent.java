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
package org.apache.jetspeed.components;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public interface MockComponent
{
    /**
     * @return Returns the value1.
     */
    int getValue1();

    /**
     * @param value1 The value1 to set.
     */
    void setValue1( int value1 );

    /**
     * @return Returns the value2.
     */
    String getValue2();

    /**
     * @param value2 The value2 to set.
     */
    void setValue2( String value2 );
    
    /**
     * 
     * @return number of components of this type that have been instantiated.
     */
    int componentId();
    
    String getThreadName();
    
    Object getValue(String key);
    
    void setValue(String key, Object value);
}