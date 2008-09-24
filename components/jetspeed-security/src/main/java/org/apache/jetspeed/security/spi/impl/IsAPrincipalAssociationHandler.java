/*
 * Copyright 2006 Hippo
 *
 * Licensed under the Apache License, Version 2.0 (the  "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" 
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
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
public class IsAPrincipalAssociationHandler extends BaseJetspeedPrincipalAssociationHandler
{
    public IsAPrincipalAssociationHandler(JetspeedPrincipalManagerSPI manager, JetspeedPrincipalAssociationStorageManager jpasm)
    {
        super(new JetspeedPrincipalAssociationTypeImpl(JetspeedPrincipalAssociationType.IS_A, manager.getPrincipalType(), manager.getPrincipalType(), false, true, true, false), manager, manager, jpasm);
    }
    
    public IsAPrincipalAssociationHandler(String associationName, JetspeedPrincipalManagerSPI manager, JetspeedPrincipalAssociationStorageManager jpasm)
    {
        super(new JetspeedPrincipalAssociationTypeImpl(associationName, manager.getPrincipalType(), manager.getPrincipalType(), false, true, true, false), manager, manager, jpasm);
    }
}
