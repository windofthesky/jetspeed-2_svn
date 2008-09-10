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
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PrincipalAlreadyExistsException;
import org.apache.jetspeed.security.PrincipalAssociationNotAllowedException;
import org.apache.jetspeed.security.PrincipalAssociationRequiredException;
import org.apache.jetspeed.security.PrincipalAssociationUnsupportedException;
import org.apache.jetspeed.security.PrincipalNotFoundException;
import org.apache.jetspeed.security.PrincipalNotRemovableException;
import org.apache.jetspeed.security.PrincipalReadOnlyException;
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
    private Map<AssociationHandlerKey, JetspeedPrincipalAssociationType> reqAssociations = new HashMap<AssociationHandlerKey, JetspeedPrincipalAssociationType>();
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

    public List<? extends JetspeedPrincipal> getPrincipals(String nameFilter)
    {
        return jpam.getPrincipals(nameFilter, principalType);
    }

    public List<? extends JetspeedPrincipal> getPrincipalsByAttribute(String attributeName, String attributeValue)
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
    

    public final List<? extends JetspeedPrincipal> getAssociatedFrom(String principalFromName, JetspeedPrincipalType from, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(from.getName(), principalType.getName(), associationName)))
        {
            // TODO: should we throw an exception here???
            return Collections.EMPTY_LIST;
        }
        return jpam.getAssociatedFrom(principalFromName, from, principalType, associationName);
    }

    public final List<String> getAssociatedNamesFrom(String principalFromName, JetspeedPrincipalType  from, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(from.getName(), principalType.getName(), associationName)))
        {
            // TODO: should we throw an exception here???
            return Collections.EMPTY_LIST;
        }
        return jpam.getAssociatedNamesFrom(principalFromName, from, principalType, associationName);
    }

    public final List<String> getAssociatedNamesTo(String principalToName, JetspeedPrincipalType to, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(principalType.getName(), to.getName(), associationName)))
        {
            // TODO: should we throw an exception here???
            return Collections.EMPTY_LIST;
        }
        return jpam.getAssociatedNamesTo(principalToName, principalType, to, associationName);
    }

    public final List<? extends JetspeedPrincipal> getAssociatedTo(String principalToName, JetspeedPrincipalType to, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(principalType.getName(), to.getName(), associationName)))
        {
            // TODO: should we throw an exception here???
            return Collections.EMPTY_LIST;
        }
        return jpam.getAssociatedTo(principalToName, principalType, to, associationName);
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
            if (jpah.getAssociationType().isRequired())
            {
                reqAssociations.put(key,jpah.getAssociationType());
            }
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
        throws PrincipalAssociationNotAllowedException, PrincipalAlreadyExistsException, PrincipalAssociationRequiredException, PrincipalNotFoundException, PrincipalAssociationUnsupportedException
    {
        validatePrincipal(principal);
        if (associations != null)
        {
            AssociationHandlerKey key = null;
            Map<AssociationHandlerKey, JetspeedPrincipalAssociationType> reqAss = new HashMap<AssociationHandlerKey, JetspeedPrincipalAssociationType>(reqAssociations);
            for (JetspeedPrincipalAssociationReference ref : associations)
            {
                if (ref.ref.isTransient())
                {
                    throw new IllegalArgumentException("Associated principal of type "+ref.ref.getType().getName() +" is transient");
                }
                if (ref.type == JetspeedPrincipalAssociationReference.Type.FROM)
                {
                    key = new AssociationHandlerKey(ref.associationName, ref.ref.getType().getName(), principalType.getName());
                }
                else
                {
                    key = new AssociationHandlerKey(ref.associationName, ref.ref.getType().getName(), principalType.getName());
                }
                if (!assHandlers.containsKey(key))
                {
                    throw new PrincipalAssociationNotAllowedException();
                }
                reqAss.remove(key);
            }
            if (!reqAss.isEmpty())
            {
                // TODO: proper named message or better replace with SecurityException(KeyedMessage)
                throw new PrincipalAssociationRequiredException();
            }
        }
        jpsm.addPrincipal(principal, associations);
        if (associations != null)
        {
            for ( JetspeedPrincipalAssociationReference ref : associations)
            {
                if (ref.type == JetspeedPrincipalAssociationReference.Type.FROM)
                {
                    addAssociation(ref.associationName, ref.ref, principal);
                }
                else
                {
                    addAssociation(ref.associationName, principal, ref.ref);
                }
            }
        }
    }

    public void removePrincipal(JetspeedPrincipal principal) throws PrincipalNotFoundException,
                                                            PrincipalNotRemovableException, DependentPrincipalException
    {
        validatePrincipal(principal);
        for (JetspeedPrincipalAssociationHandler jpah : assHandlers.values())
        {
            if (jpah.getAssociationType().getFromPrincipalType().getName().equals(principalType.getName()))
            {
                jpah.beforeRemoveFrom(principal);
            }
            else
            {
                jpah.beforeRemoveTo(principal);
            }
        }
        jpsm.removePrincipal(principal);
    }

    public void updatePrincipal(JetspeedPrincipal principal) throws PrincipalUpdateException,
                                                            PrincipalNotFoundException, PrincipalReadOnlyException
    {
        validatePrincipal(principal);
        if (principal.isReadOnly())
        {
            throw new PrincipalReadOnlyException();
        }
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
    public void addAssociation(String associationName, JetspeedPrincipal from, JetspeedPrincipal to) throws PrincipalNotFoundException, PrincipalAssociationNotAllowedException, PrincipalAssociationUnsupportedException
    {
        AssociationHandlerKey key = new AssociationHandlerKey(associationName, from.getType().getName(), to.getType().getName());        
        JetspeedPrincipalAssociationHandler jpah = assHandlers.get(key);
        
        if (jpah == null)
        {
            throw new PrincipalAssociationNotAllowedException();
        }
        
        jpah.add(from, to);
    }

    public void removeAssociation(String associationName, JetspeedPrincipal from, JetspeedPrincipal to) throws PrincipalAssociationRequiredException
    {
        AssociationHandlerKey key = new AssociationHandlerKey(associationName, from.getType().getName(), to.getType().getName());
        JetspeedPrincipalAssociationHandler jpah = assHandlers.get(key);
        
        if (jpah != null)
        {
            if (jpah.getAssociationType().isRequired())
            {
                throw new PrincipalAssociationRequiredException();
            }
            jpah.remove(from, to);
        }
    }
}
