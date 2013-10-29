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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationReference;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.JetspeedPrincipalManagerProvider;
import org.apache.jetspeed.security.JetspeedPrincipalQueryContext;
import org.apache.jetspeed.security.JetspeedPrincipalResultList;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PrincipalManagerEventListener;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAccessManager;
import org.apache.jetspeed.security.spi.JetspeedPrincipalAssociationHandler;
import org.apache.jetspeed.security.spi.JetspeedPrincipalManagerSPI;
import org.apache.jetspeed.security.spi.JetspeedPrincipalStorageManager;
import org.apache.jetspeed.security.spi.impl.SynchronizationStateAccess;

/**
 * @version $Id$
 */
public abstract class BaseJetspeedPrincipalManager implements JetspeedPrincipalManagerSPI
{
    private static class AssociationHandlerKey implements Serializable
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
        
        public boolean equals(Object o)
        {
            if (o != null && o instanceof AssociationHandlerKey)
            {
                AssociationHandlerKey other = (AssociationHandlerKey)o;
                return other.name.equals(name) && other.fromPrincipalType.equals(fromPrincipalType) &&
                other.toPrincipalType.equals(toPrincipalType);
            }
            return false;
        }

        public int hashCode()
        {
            return name.hashCode() + fromPrincipalType.hashCode() + toPrincipalType.hashCode();
        }
    }

    private JetspeedPrincipalType principalType;
    private List<JetspeedPrincipalAssociationType> associationTypes = new ArrayList<JetspeedPrincipalAssociationType>();
    private Map<AssociationHandlerKey, JetspeedPrincipalAssociationHandler> assHandlers = new HashMap<AssociationHandlerKey, JetspeedPrincipalAssociationHandler>();
    private Map<AssociationHandlerKey, JetspeedPrincipalAssociationType> reqAssociations = new HashMap<AssociationHandlerKey, JetspeedPrincipalAssociationType>();
    private JetspeedPrincipalAccessManager jpam;
    private JetspeedPrincipalStorageManager jpsm;
    //added for removing circular dependciese
    protected static JetspeedPrincipalManagerProvider jpmp;
    private List<PrincipalManagerEventListener> listeners = new LinkedList();
    
    public BaseJetspeedPrincipalManager(JetspeedPrincipalType principalType, JetspeedPrincipalAccessManager jpam,
                                        JetspeedPrincipalStorageManager jpsm)
    {
        this.principalType = principalType;
        this.jpam = jpam;
        this.jpsm = jpsm;
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
    
    public static void setJetspeedPrincipalManagerProvider(JetspeedPrincipalManagerProvider Jpmp)
    {
    	jpmp = Jpmp;
    }
    
    public JetspeedPrincipalManagerProvider getPrincipalManagerProvider()
    {
    	if(jpmp==null)
    	{
    		jpmp= Jetspeed.getComponentManager().lookupComponent("org.apache.jetspeed.security.spi.JetspeedPrincipalManagerProvider");
    	}
    	return jpmp;
    }
    
    public JetspeedPrincipalAccessManager getPrincipalAccessManager()
    {
        return jpam;
    }
    
    public final JetspeedPrincipalType getPrincipalType()
    {
        return principalType;
    }
    
    public final JetspeedPrincipalManager getPrincipalManager()
    {
        return this;
    }
    
    public List<JetspeedPrincipalAssociationType> getAssociationTypes()
    {
        return Collections.unmodifiableList(associationTypes);
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

    public JetspeedPrincipalResultList getPrincipals(JetspeedPrincipalQueryContext queryContext)
    {
        return jpam.getPrincipals(queryContext, principalType);
    }
    
    public List<? extends JetspeedPrincipal> getPrincipalsByAttribute(String attributeName, String attributeValue)
    {
        return jpam.getPrincipalsByAttribute(attributeName, attributeValue, principalType);
    }

    public void removePrincipal(String name) throws SecurityException
    {
        JetspeedPrincipal principal = jpam.getPrincipal(name, principalType);
        if (principal == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(principalType.getName(),name));
        }
        removePrincipal(principal);
    }
    

    public final List<? extends JetspeedPrincipal> getAssociatedFrom(String principalFromName, JetspeedPrincipalType from, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(associationName, from.getName(), principalType.getName())))
        {
            return Collections.emptyList();
        }
        return jpam.getAssociatedFrom(principalFromName, from, principalType, associationName);
    }

    public final List<String> getAssociatedNamesFrom(String principalFromName, JetspeedPrincipalType  from, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(associationName, from.getName(), principalType.getName())))
        {
            return Collections.emptyList();
        }
        return jpam.getAssociatedNamesFrom(principalFromName, from, principalType, associationName);
    }

    public final List<String> getAssociatedNamesTo(String principalToName, JetspeedPrincipalType to, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(associationName, principalType.getName(), to.getName())))
        {
            return Collections.emptyList();
        }
        return jpam.getAssociatedNamesTo(principalToName, principalType, to, associationName);
    }

    public final List<? extends JetspeedPrincipal> getAssociatedTo(String principalToName, JetspeedPrincipalType to, String associationName)
    {
        if ( !assHandlers.containsKey(new AssociationHandlerKey(associationName, principalType.getName(), to.getName())))
        {
            return Collections.emptyList();
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
            associationTypes.add(jpah.getAssociationType());
            if (jpah.getAssociationType().isRequired() && jpah.getAssociationType().getFromPrincipalType().getName().equals(principalType.getName()))
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
    public void addPrincipal(JetspeedPrincipal principal, Set<JetspeedPrincipalAssociationReference> associations)
        throws SecurityException
    {
        validatePrincipal(principal);
        // don't check required associations during synchronization
        if (!isSynchronizing()){
            Map<AssociationHandlerKey, JetspeedPrincipalAssociationType> reqAss = new HashMap<AssociationHandlerKey, JetspeedPrincipalAssociationType>(reqAssociations);
            if (associations != null)
            {
                AssociationHandlerKey key = null;
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
                        key = new AssociationHandlerKey(ref.associationName, principalType.getName(), ref.ref.getType().getName());
                    }
                    if (!assHandlers.containsKey(key))
                    {
                        if (ref.type == JetspeedPrincipalAssociationReference.Type.FROM)
                        {
                            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_UNSUPPORTED.createScoped(ref.ref.getType().getName(), ref.associationName, principal.getType().getName()));
                        }
                        else
                        {
                            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_UNSUPPORTED.createScoped(principal.getType().getName(), ref.associationName, ref.ref.getType().getName()));
                        }
                    }
                    reqAss.remove(key);
                }
            }
            if (!reqAss.isEmpty())
            {
                JetspeedPrincipalAssociationType assType = reqAss.values().iterator().next();
                throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_REQUIRED.createScoped(assType.getFromPrincipalType().getName(),
                                                                                                          assType.getAssociationName(),
                                                                                                          assType.getToPrincipalType().getName()));
            }
        }
        jpsm.addPrincipal(principal, associations);
        if (associations != null)
        {
            for ( JetspeedPrincipalAssociationReference ref : associations)
            {
                if (ref.type == JetspeedPrincipalAssociationReference.Type.FROM)
                {
                    addAssociation(ref.ref, principal, ref.associationName);
                }
                else
                {
                    addAssociation(principal, ref.ref, ref.associationName);
                }
            }
        }
        notifyNewPrincipal(principal);
    }

    public void removePrincipal(JetspeedPrincipal principal) throws SecurityException
    {
        validatePrincipal(principal);
        for (JetspeedPrincipalAssociationHandler jpah : assHandlers.values())
        {
            if (jpah.getAssociationType().getFromPrincipalType().getName().equals(principalType.getName()))
            {
                jpah.beforeRemoveFrom(principal);
            }
            if (jpah.getAssociationType().getToPrincipalType().getName().equals(principalType.getName()))
            {
                jpah.beforeRemoveTo(principal);
            }
        }
        jpsm.removePrincipal(principal);
        notifyRemovedPrincipal(principal);
    }     
    
    public void updatePrincipal(JetspeedPrincipal principal) throws SecurityException
    {
        validatePrincipal(principal);
        if (principal.isReadOnly() && !isSynchronizing())
        {
            throw new SecurityException(SecurityException.PRINCIPAL_IS_READ_ONLY.createScoped(principal.getType().getName(), principal.getName()));
        }
        jpsm.updatePrincipal(principal);
        notifyUpdatedPrincipal(principal);
    }

    /**
     * addListener - add principal manager event listener
     *
     * @param listener principal manager event listener
     */
    public void addListener(PrincipalManagerEventListener listener)
    {
        // add listener to listeners list
        synchronized (listeners)
        {
            listeners.add(listener);
        }
    }
       
    /**
     * removeListener - remove principal manager event listener
     *
     * @param listener principal manager event listener
     */
    public void removeListener(PrincipalManagerEventListener listener)
    {
        // remove listener from listeners list
        synchronized (listeners)
        {  
        	listeners.remove(listener);
        }
    }
    
    //
    // JetspeedPrincipalAssociationHandler interface invocations
    //
    public void addAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName) throws SecurityException
    {
        AssociationHandlerKey key = new AssociationHandlerKey(associationName, from.getType().getName(), to.getType().getName());        
        JetspeedPrincipalAssociationHandler jpah = assHandlers.get(key);
        
        if (jpah == null)
        {
            throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_UNSUPPORTED.createScoped(from.getType().getName(), associationName, to.getType().getName()));
        }
        if (from.isTransient() || from.getId() == null)
        {
            JetspeedPrincipal pfrom = jpah.getManagerFrom().getPrincipal(from.getName());
            if (pfrom == null)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(from.getType().getName(), from.getName()));
            }
            from = pfrom;
        }
        if (to.isTransient() || to.getId() == null)
        {
            JetspeedPrincipal pto = jpah.getManagerTo().getPrincipal(to.getName());
            if (pto == null)
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(to.getType().getName(), to.getName()));
            }
            to = pto;
        }
        jpah.add(from, to);
        notifyAddedAssociation(from,to, associationName);
    }
    
    public void transferAssociationFrom(JetspeedPrincipal from, JetspeedPrincipal to, JetspeedPrincipal target,
                                        String associationName) throws SecurityException
    {
        // TODO Auto-generated method stub
        
    }

    public void transferAssociationTo(JetspeedPrincipal from, JetspeedPrincipal to, JetspeedPrincipal target,
                                      String associationName) throws SecurityException
    {
        // TODO Auto-generated method stub
        
    }

    public void removeAssociation(JetspeedPrincipal from, JetspeedPrincipal to, String associationName) throws SecurityException
    {
        AssociationHandlerKey key = new AssociationHandlerKey(associationName, from.getType().getName(), to.getType().getName());
        JetspeedPrincipalAssociationHandler jpah = assHandlers.get(key);
        
        if (jpah != null)
        {
            if (jpah.getAssociationType().isRequired() && !isSynchronizing())
            {
                JetspeedPrincipalAssociationType assType = jpah.getAssociationType();
                throw new SecurityException(SecurityException.PRINCIPAL_ASSOCIATION_REQUIRED.createScoped(assType.getFromPrincipalType().getName(),
                                                                                                          assType.getAssociationName(),
                                                                                                          assType.getToPrincipalType().getName()));             
            }
            if (from.isTransient() || from.getId() == null)
            {
                JetspeedPrincipal pfrom = jpah.getManagerFrom().getPrincipal(from.getName());
                if (pfrom == null)
                {
                    throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(from.getType().getName(), from.getName()));
                }
                from = pfrom;
            }
            if (to.isTransient() || to.getId() == null)
            {
                JetspeedPrincipal pto = jpah.getManagerTo().getPrincipal(to.getName());
                if (pto == null)
                {
                    throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(to.getType().getName(), to.getName()));
                }
                to = pto;
            }
            jpah.remove(from, to);
            notifyRemovedAssociation(from,to, associationName);
        }
    }
    
    protected boolean isSynchronizing(){
        return SynchronizationStateAccess.isSynchronizing();
    }
    /**
     * notifyNewPrincipal - notify principal manager event listeners of
     *                 new principal event
     *
     * @param principal New principal
     */
    public void notifyNewPrincipal(JetspeedPrincipal principal) throws SecurityException
    {
        // copy listeners list to reduce synchronization deadlock
        List<PrincipalManagerEventListener> listenersList = null;
        synchronized (listeners)
        {
            listenersList = new ArrayList(listeners);
        }
        for(PrincipalManagerEventListener listener : listenersList)
        {
                listener.newPrincipal(principal);
        }
    }

    /**
     * notifyUpdatedPrincipal - notify page manager event listeners of
     *                         updated node event
     *
     * @param node updated managed node if known
     */
    public void notifyUpdatedPrincipal(JetspeedPrincipal principal) throws SecurityException
    {
	   List<PrincipalManagerEventListener> listenersList = null;
       synchronized (listeners)
       {
           listenersList = new ArrayList(listeners);
       }
       for(PrincipalManagerEventListener listener : listenersList)
       {
               listener.updatePrincipal(principal);
       }
    }

    /**
     * notifyRemovedPrincipal - notify principal manager event listeners of
     *                          removed principal event
     *
     * @param principal removed managed principal if known
     */
    public void notifyRemovedPrincipal(JetspeedPrincipal principal)
    {
 	   List<PrincipalManagerEventListener> listenersList = null;
       synchronized (listeners)
       {
           listenersList = new ArrayList(listeners);
       }
       for(PrincipalManagerEventListener listener : listenersList)
       {
               listener.removePrincipal(principal);
       }
    }

    /**
     * notifyAddedAssociation - notify principal manager event listeners of
     *                          addedd association event
     *
     * @param principal removed managed principal if known
     */
    public void notifyAddedAssociation(JetspeedPrincipal fromPrincipal,JetspeedPrincipal toPrincipal, String associationName)
    {
 	   List<PrincipalManagerEventListener> listenersList = null;
       synchronized (listeners)
       {
           listenersList = new ArrayList(listeners);
       }
       for(PrincipalManagerEventListener listener : listenersList)
       {
               listener.associationAdded(fromPrincipal,toPrincipal, associationName);
       }
    }

    /**
     * notifyRemovedAssociation - notify principal manager event listeners of
     *                          removed association event
     *
     * @param principal removed managed principal if known
     */
    public void notifyRemovedAssociation(JetspeedPrincipal fromPrincipal,JetspeedPrincipal toPrincipal, String associationName)
    {
 	   List<PrincipalManagerEventListener> listenersList = null;
       synchronized (listeners)
       {
           listenersList = new ArrayList(listeners);
       }
       for(PrincipalManagerEventListener listener : listenersList)
       {
               listener.associationRemoved(fromPrincipal,toPrincipal, associationName);
       }
    }
}
