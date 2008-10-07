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
public final class JetspeedPrincipalAssociationReference
{
    public enum Type
    {
        FROM, TO
    };

    public final Type type;
    public final JetspeedPrincipal ref;
    public final String associationName;

    public JetspeedPrincipalAssociationReference(Type type, JetspeedPrincipal ref, String associationName)
    {
        this.type = type;
        this.ref = ref;
        this.associationName = associationName;
        if (type == null || ref.getId() == null || ref.getName() == null || associationName == null)
        {
            throw new NullPointerException();
        }
    }

    public boolean equals(JetspeedPrincipalAssociationReference jar)
    {
        return (jar != null && jar.type == this.type && jar.ref.getClass() == ref.getClass() && jar.ref.getName() == ref.getName());
    }
}
