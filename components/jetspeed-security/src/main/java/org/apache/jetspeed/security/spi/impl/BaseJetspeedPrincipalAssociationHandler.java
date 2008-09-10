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

import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationHandler;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.PrincipalAssociationNotAllowedException;
import org.apache.jetspeed.security.PrincipalAssociationRequiredException;
import org.apache.jetspeed.security.PrincipalAssociationUnsupportedException;
import org.apache.jetspeed.security.PrincipalNotFoundException;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationStorageManager;

/**
 * @version $Id$
 *
 */
public abstract class BaseJetspeedPrincipalAssociationHandler implements JetspeedPrincipalAssociationHandler
{
    private JetspeedPrincipalAssociationType associationType;
    private JetspeedPrincipalAssociationStorageManager jpasm;

    
    
    public BaseJetspeedPrincipalAssociationHandler(JetspeedPrincipalAssociationType associationType,
                                                   JetspeedPrincipalAssociationStorageManager jpasm)
    {
        this.associationType = associationType;
        this.jpasm = jpasm;
    }

    public void add(JetspeedPrincipal from, JetspeedPrincipal to) throws PrincipalNotFoundException,
                                                                 PrincipalAssociationNotAllowedException, PrincipalAssociationUnsupportedException
    {
        if (from.getType().equals(associationType.getFromPrincipalType()) && to.getType().equals(associationType.getToPrincipalType()))
        {
            jpasm.addAssociation(from, to, associationType.getAssociationName());
        }
    }

    public JetspeedPrincipalAssociationType getAssociationType()
    {
        return associationType;
    }

    public void remove(JetspeedPrincipal from, JetspeedPrincipal to) throws PrincipalAssociationRequiredException
    {
        if (from.getType().equals(associationType.getFromPrincipalType()) && to.getType().equals(associationType.getToPrincipalType()))
        {
            jpasm.removeAssociation(from, to, associationType.getAssociationName());
        }
    }
}
