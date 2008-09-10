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
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PrincipalNotRemovableException;
import org.apache.jetspeed.security.impl.JetspeedPrincipalAssociationTypeImpl;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationStorageManager;

/**
 * @version $Id$
 *
 */
public class SimpleMemberOfPrincipalAssociationHandler extends BaseJetspeedPrincipalAssociationHandler
{
    public SimpleMemberOfPrincipalAssociationHandler(JetspeedPrincipalAssociationType associationType, JetspeedPrincipalAssociationStorageManager jpasm)
    {
        super(associationType, jpasm);
    }
    
    public SimpleMemberOfPrincipalAssociationHandler(String associationName, JetspeedPrincipalType fromType,JetspeedPrincipalType toType, boolean required, JetspeedPrincipalAssociationStorageManager jpasm)
    {
        this(new JetspeedPrincipalAssociationTypeImpl(associationName, fromType, toType, required), jpasm);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipalAssociationHandler#beforeRemoveFrom(org.apache.jetspeed.security.JetspeedPrincipal)
     */
    public void beforeRemoveFrom(JetspeedPrincipal from) throws PrincipalNotRemovableException,
                                                        DependentPrincipalException
    {
        // nothing to do
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.security.JetspeedPrincipalAssociationHandler#beforeRemoveTo(org.apache.jetspeed.security.JetspeedPrincipal)
     */
    public void beforeRemoveTo(JetspeedPrincipal to) throws PrincipalNotRemovableException, DependentPrincipalException
    {
        // nothing to do
    }
}
