/*	
 * Licensed to the Apache Software Foundation (ASF) under one or more&#13;
 * contributor license agreements.  See the NOTICE file distributed with&#13;
 * this work for additional information regarding copyright ownership.&#13;
 * The ASF licenses this file to You under the Apache License, Version 2.0&#13;
 * (the "License"); you may not use this file except in compliance with&#13;
 * the License.  You may obtain a copy of the License at&#13;
 * &#13;
 *      http://www.apache.org/licenses/LICENSE-2.0&#13;
 * &#13;
 * Unless required by applicable law or agreed to in writing, software&#13;
 * distributed under the License is distributed on an "AS IS" BASIS,&#13;
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.&#13;
 * See the License for the specific language governing permissions and&#13;
 * limitations under the License.&#13;
 */
package org.apache.jetspeed.security.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.security.DependentPrincipalException;
import org.apache.jetspeed.security.JetspeedPermission;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationHandler;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationReference;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PrincipalAlreadyExistsException;
import org.apache.jetspeed.security.PrincipalAssociationNotAllowedException;
import org.apache.jetspeed.security.PrincipalAssociationRequiredException;
import org.apache.jetspeed.security.PrincipalNotFoundException;
import org.apache.jetspeed.security.PrincipalNotRemovableException;
import org.apache.jetspeed.security.PrincipalUpdateException;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalManagerSPI;
import org.apache.jetspeed.security.spi.JetspeedPrincipalPermissionStorageManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;

/**
 * @version $Id$
 */
public abstract class BaseJetspeedPrincipalManager implements JetspeedPrincipalManagerSPI
{
    private static class AssociationHandlerKey
    {
        String name;
        String fromPrincipalType;
        String toPrincipalType;

        public AssociationHandlerKey(JetspeedPrincipalAssociationHandler jpah)
        {
            this(jpah.getAssociationType().getAssociationName(), jpah.getAssociationType().getFromPrincipalType()
                                                                     .getName(), jpah.getAssociationType()
                                                                                     .getToPrincipalType().getName());
        }

        public AssociationHandlerKey(String name, String fromPrincipalType, String toPrincipalType)
        {
            this.name = name;
            this.fromPrincipalType = fromPrincipalType;
            this.toPrincipalType = toPrincipalType;
        }

        public boolean equals(AssociationHandlerKey other)
        {
            return other.name.equals(name) && other.fromPrincipalType.equals(fromPrincipalType) &&
                   other.toPrincipalType.equals(toPrincipalType);
        }

        public int hashCode()
        {
            return name.hashCode() + fromPrincipalType.hashCode() + toPrincipalType.hashCode();
        }
    }

    private JetspeedPrincipalType principalType;
    private Map<AssociationHandlerKey, JetspeedPrincipalAssociationHandler> assHandlers = new HashMap<AssociationHandlerKey, JetspeedPrincipalAssociationHandler>();
    private JetspeedPrincipalAccessManager jpam;
    private JetspeedPrincipalStorageManager jpsm;
    private JetspeedPrincipalPermissionStorageManager jppsm;

    public BaseJetspeedPrincipalManager(JetspeedPrincipalType principalType, JetspeedPrincipalAccessManager jpam,
                                        JetspeedPrincipalStorageManager jpsm,
                                        JetspeedPrincipalPermissionStorageManager jppsm)
    {
        this.principalType = principalType;
        this.jpam = jpam;
        this.jpsm = jpsm;
        this.jppsm = jppsm;
    }
    
    protected final void validatePrincipal(JetspeedPrincipal principal)
    {
        if (!principal.getType().getName().equals(principalType.getName()))
        {
            throw new IllegalArgumentException("Principal is not of type "+principalType.getName());
        }
        if (principal.isTransient())
        {
            throw new IllegalArgumentException("Principal is transient");
        }
    }

    public final JetspeedPrincipalType getPrincipalType()
    {
        return principalType;
    }

    public boolean principalExists(String name)
    {
        return jpam.principalExists(name, principalType);
    }

    public JetspeedPrincipal getPrincipal(String name)
    {
        return jpam.getPrincipal(name, principalType);
    }

    public List<String> getPrincipalNames(String nameFilter)
    {
        return jpam.getPrincipalNames(nameFilter, principalType);
    }

    public List<JetspeedPrincipal> getPrincipals(String nameFilter)
    {
        return jpam.getPrincipals(nameFilter, principalType);
    }

    public List<JetspeedPrincipal> getPrincipalsByAttribute(String attributeName, String attributeValue)
    {
        return jpam.getPrincipalsByAttribute(attributeName, attributeValue, principalType);
    }

    public void removePrincipal(String name) throws PrincipalNotFoundException, PrincipalNotRemovableException,
                                            DependentPrincipalException
    {
        JetspeedPrincipal principal = jpam.getPrincipal(name, principalType);
        if (principal == null)
            throw new PrincipalNotFoundException();
        jpsm.removePrincipal(principal);
    }

    public final List<JetspeedPrincipal> getAssociatedFrom(String principalName, JetspeedPrincipalType to, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(principalType.getName(), to.getName(), associationName)))
        {
            // TODO: should we throw an exception here???
            return Collections.EMPTY_LIST;
        }
        return jpam.getAssociatedFrom(principalName, principalType, to, associationName);
    }

    public final List<String> getAssociatedNamesFrom(String principalName, JetspeedPrincipalType to, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(principalType.getName(), to.getName(), associationName)))
        {
            // TODO: should we throw an exception here???
            return Collections.EMPTY_LIST;
        }
        return jpam.getAssociatedNamesFrom(principalName, principalType, to, associationName);
    }

    public final List<String> getAssociatedNamesTo(String principalName, JetspeedPrincipalType from, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(from.getName(), principalType.getName(), associationName)))
        {
            // TODO: should we throw an exception here???
            return Collections.EMPTY_LIST;
        }
        return jpam.getAssociatedNamesTo(principalName, principalType, from, associationName);
    }

    public final List<JetspeedPrincipal> getAssociatedTo(String principalName, JetspeedPrincipalType from, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(from.getName(), principalType.getName(), associationName)))
        {
            // TODO: should we throw an exception here???
            return Collections.EMPTY_LIST;
        }
        return jpam.getAssociatedTo(principalName, principalType, from, associationName);
    }

    //
    // JetspeedPrincipalManagerSPI interface implementation
    //
    public void addAssociationHandler(JetspeedPrincipalAssociationHandler jpah)
    {
        if (jpah.getAssociationType().getFromPrincipalType().getName().equals(principalType.getName()) ||
            jpah.getAssociationType().getToPrincipalType().getName().equals(principalType.getName()))
        {
            AssociationHandlerKey key = new AssociationHandlerKey(jpah);
            if (assHandlers.containsKey(key))
            {
                throw new IllegalStateException("An AssociationHandler for " +
                                                jpah.getAssociationType().getAssociationName() + " already defined");
            }
            assHandlers.put(key, jpah);
        }
        else
        {
            throw new IllegalArgumentException("AssociationHandler is not handling a " + principalType.getName() +
                                               " JetspeedPrincipal");
        }
    }

    //
    // JetspeedPrincipalStorageManager interface implementation
    //
    public final boolean isMapped()
    {
        return jpsm.isMapped();
    }

    public void addPrincipal(JetspeedPrincipal principal, Set<JetspeedPrincipalAssociationReference> associations)
                                                                                                                  throws PrincipalAlreadyExistsException,
                                                                                                                  PrincipalAssociationRequiredException
    {
        validatePrincipal(principal);
        jpsm.addPrincipal(principal, associations);
    }

    public void removePrincipal(JetspeedPrincipal principal) throws PrincipalNotFoundException,
                                                            PrincipalNotRemovableException, DependentPrincipalException
    {
        validatePrincipal(principal);
        jpsm.removePrincipal(principal);
    }

    public void updatePrincipal(JetspeedPrincipal principal) throws PrincipalUpdateException,
                                                            PrincipalNotFoundException
    {
        validatePrincipal(principal);
        jpsm.updatePrincipal(principal);
    }

    //
    // JetspeedPrincipalPermissionStorageManager interface implementation
    //
    public void grantPermission(JetspeedPrincipal principal, JetspeedPermission permission)
    {
        validatePrincipal(principal);
        jppsm.grantPermission(principal, permission);
    }

    public void revokeAll(JetspeedPrincipal principal)
    {
        jppsm.revokeAll(principal);
        validatePrincipal(principal);
    }

    public void revokePermission(JetspeedPrincipal principal, JetspeedPermission permission)
    {
        validatePrincipal(principal);
        jppsm.revokePermission(principal, permission);
    }
    
    //
    // JetspeedPrincipalAssociationHandler interface invocations
    //
    public void addAssociation(String associationName, JetspeedPrincipal from, JetspeedPrincipal to) throws PrincipalNotFoundException, PrincipalAssociationNotAllowedException
    {
        AssociationHandlerKey key = new AssociationHandlerKey(associationName, from.getType().getName(), to.getName());
        
        if (!assHandlers.containsKey(key))
        {
            throw new PrincipalAssociationNotAllowedException();
        }
        
        JetspeedPrincipalAssociationHandler jpah = assHandlers.get(key);
        jpah.add(from, to);
    }

    public void removeAssociation(String associationName, JetspeedPrincipal from, JetspeedPrincipal to) throws PrincipalNotFoundException, PrincipalAssociationRequiredException
    {
        AssociationHandlerKey key = new AssociationHandlerKey(associationName, from.getType().getName(), to.getName());
        JetspeedPrincipalAssociationHandler jpah = assHandlers.get(key);
        
        if (jpah != null)
        {
            jpah.remove(from, to);
        }
    }
    
}
