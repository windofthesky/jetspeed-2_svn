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

package org.apache.jetspeed.security.spi.impl;

import org.apache.jetspeed.security.DependentPrincipalException;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalHierachyAssocationType;
import org.apache.jetspeed.security.PrincipalNotRemovableException;
import org.apache.jetspeed.security.impl.JetspeedPrincipalHierarchyAssocationTypeImpl;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalManagerSPI;
import org.apache.jetspeed.security.JetspeedPrincipalHierachyAssocationType.HierarchyType;

/**
 * @version $Id$
 *
 */
public class SimpleHierarchyPrincipalAssociationHandler extends BaseJetspeedPrincipalAssociationHandler
{

    public SimpleHierarchyPrincipalAssociationHandler(JetspeedPrincipalHierachyAssocationType associationType,
                                                       JetspeedPrincipalManagerSPI manager,
                                                       JetspeedPrincipalAssociationStorageManager jpasm)
    {
        super(associationType, manager, manager, jpasm);
    }

    public SimpleHierarchyPrincipalAssociationHandler(String associationName, JetspeedPrincipalManagerSPI manager,
                                                       HierarchyType hierarchyType, boolean required,
                                                       JetspeedPrincipalAssociationStorageManager jpasm)
    {
        this(new JetspeedPrincipalHierarchyAssocationTypeImpl(associationName, manager.getPrincipalType(), hierarchyType, required), manager, jpasm);
    }

    public void beforeRemoveFrom(JetspeedPrincipal from) throws PrincipalNotRemovableException,
                                                        DependentPrincipalException
    {
    }

    public void beforeRemoveTo(JetspeedPrincipal to) throws PrincipalNotRemovableException, DependentPrincipalException
    {
    }
}
