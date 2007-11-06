/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.serializer;


/**
 * Jetspeed Serializer Factory
 * 
 * <p>Interface for creating serializers. Serializer keep some state so they should be recreated as needed
 * We will revisit this class in the refactoring in 2.2 as Im not really sure why we need a primary and secondary class
 *  but I think its related to the dependencies fixed in these issues, see:
 * http://issues.apache.org/jira/browse/JS2-771 and http://issues.apache.org/jira/browse/JS2-770 
 * </p> 
 */
public interface JetspeedSerializerFactory
{

    /** Create basic Jetspeed Serializer */
    public final static String PRIMARY = "primary";
    /** Create a secondary Jetspeed Serializer (registry data) */
    public final static String SECONDARY = "secondary";
    
    /**
     * Create a Jetspeed Serializer of one of the two supported types
     * @param serializerType eithe PRIMARY OR SECONDARY
     * @return
     */
    public JetspeedSerializer create(String serializerType);
    
}
