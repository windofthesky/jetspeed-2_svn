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

import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.impl.JetspeedPrincipalAssociationTypeImpl;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalManagerSPI;

/**
 * @version $Id$
 *
 */
public class IsMemberOfPrincipalAssociationHandler extends BaseJetspeedPrincipalAssociationHandler
{
    public IsMemberOfPrincipalAssociationHandler(JetspeedPrincipalManagerSPI from,JetspeedPrincipalManagerSPI to, JetspeedPrincipalAssociationStorageManager jpasm)
    {
        super(new JetspeedPrincipalAssociationTypeImpl(JetspeedPrincipalAssociationType.IS_MEMBER_OF, from.getPrincipalType(), to.getPrincipalType(), false, false, false, false), from, to, jpasm);
    }
    
    public IsMemberOfPrincipalAssociationHandler(String associationName, JetspeedPrincipalManagerSPI from,JetspeedPrincipalManagerSPI to, JetspeedPrincipalAssociationStorageManager jpasm)
    {
        super(new JetspeedPrincipalAssociationTypeImpl(associationName, from.getPrincipalType(), to.getPrincipalType(), false, false, false, false), from, to, jpasm);
    }
}
