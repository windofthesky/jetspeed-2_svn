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
package org.apache.jetspeed.security.spi;

import java.io.Serializable;

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.SecurityException;

/**
 * @version $Id$
 */
public interface JetspeedPrincipalAssociationHandler extends Serializable
{
    JetspeedPrincipalAssociationType getAssociationType();
    
    JetspeedPrincipalManager getManagerFrom();
    JetspeedPrincipalManager getManagerTo();

    void add(JetspeedPrincipal from, JetspeedPrincipal to) throws SecurityException;

    void remove(JetspeedPrincipal from, JetspeedPrincipal to) throws SecurityException;

    /**
     * <p>
     * When the from principal is removed, dependent to principals might need updating or removal themselves first.
     * </p>
     * <p>
     * External authorization providers can remove all the associations of the from principal themselves on this message
     * or do so during the subsequent removal of the principal itself
     * </p>
     */
    void beforeRemoveFrom(JetspeedPrincipal from) throws SecurityException;

    /**
     * <p>
     * When the to principal is removed, dependent from principals might need updating or removal themselves first
     * </p>
     * <p>
     * External authorization providers can remove all the associations of the to principal themselves on this message
     * or do so during the subsequent removal of the principal itself
     * </p>
     */
    void beforeRemoveTo(JetspeedPrincipal to) throws SecurityException;
}
