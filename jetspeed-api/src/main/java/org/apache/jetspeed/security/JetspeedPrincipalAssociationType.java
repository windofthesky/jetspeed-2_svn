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

/**
 * @version $Id$
 */
public interface JetspeedPrincipalAssociationType
{
    String IS_A = "is_a"; // FROM node is a TO node.
    String IS_PART_OF = "part_of"; // FROM node is part of TO node.
    
    String getAssociationName();

    JetspeedPrincipalType getFromPrincipalType();

    JetspeedPrincipalType getToPrincipalType();

    boolean isReadonlyMapping(); // mapped associations cannot be added or removed other then by removal of the Principal(s) itself
}
