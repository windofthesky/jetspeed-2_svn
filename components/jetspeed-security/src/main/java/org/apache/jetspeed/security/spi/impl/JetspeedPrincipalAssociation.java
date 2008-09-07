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

import java.io.Serializable;

import org.apache.jetspeed.security.JetspeedPrincipal;

/**
 * @version $Id$
 */
public class JetspeedPrincipalAssociation implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    @SuppressWarnings("unused")
    private JetspeedPrincipal from;
    @SuppressWarnings("unused")
    private JetspeedPrincipal to;
    @SuppressWarnings("unused")
    private String associationName;
    
    public JetspeedPrincipalAssociation()
    {
        // needed for OJB/JPA although in practice it should *never* be needed to be loaded
        // as the only operations to be used are insert/delete, never update
    }

    public JetspeedPrincipalAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName)
    {        
        this.from = from;
        this.to = to;
        this.associationName = associationName;
    }

    public JetspeedPrincipal getFrom()
    {
        return from;
    }

    public JetspeedPrincipal getTo()
    {
        return to;
    }

    public String getAssociationName()
    {
        return associationName;
    }        
}
