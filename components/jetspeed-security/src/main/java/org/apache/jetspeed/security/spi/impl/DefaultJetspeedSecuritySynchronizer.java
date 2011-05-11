/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalAssociationReference;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.JetspeedPrincipalManagerProvider;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.mapping.SecurityEntityManager;
import org.apache.jetspeed.security.mapping.impl.BaseEntitySearchResultHandler;
import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.SecurityEntityRelationType;
import org.apache.jetspeed.security.spi.JetspeedSecuritySynchronizer;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @author <a href="mailto:ate@douma.nu>Ate Douma</a>
 * @version $Id$
 */
public class DefaultJetspeedSecuritySynchronizer implements JetspeedSecuritySynchronizer
{
    protected static final Logger logger = LoggerFactory.getLogger(JetspeedSecuritySynchronizer.class);
    protected static final Logger feedbackLogger = LoggerFactory.getLogger(DefaultJetspeedSecuritySynchronizer.class);
    
    protected JetspeedPrincipalManagerProvider principalManagerProvider;
    protected SecurityEntityManager securityEntityManager;
    protected boolean abortOnError = true;
    protected int feedbackAfterSyncCount = 500;

    /**
     * @param principalManagerProvider
     * @param securityEntityManager
     */
    public DefaultJetspeedSecuritySynchronizer(JetspeedPrincipalManagerProvider principalManagerProvider, SecurityEntityManager securityEntityManager)
    {
        this.principalManagerProvider = principalManagerProvider;
        this.securityEntityManager = securityEntityManager;
    }
    
    public void setAbortOnError(boolean abort)
    {
        this.abortOnError = abort;
    }
    
    public void setFeedbackAfterSyncCount(int count)
    {
        this.feedbackAfterSyncCount = count;
    }

    public synchronized void synchronizeAll() throws SecurityException
    {
        setSynchronizing(true);
        try
        {
            feedbackLogger.info("Synchronizing all entities");
            final Map<String,Set<String>> processing = new HashMap<String,Set<String>>();
            final Map<String,Map<String,String>> processed = new HashMap<String,Map<String,String>>(); 
            for (final String type : securityEntityManager.getSupportedEntityTypes())
            {
                BaseEntitySearchResultHandler handler = new BaseEntitySearchResultHandler()
                {
                    @Override
                    protected boolean processSearchResult(Entity entity, int pageSize, int pageIndex, int index)
                    {
                        try
                        {
                            if (feedbackAfterSyncCount > 0 && index % feedbackAfterSyncCount == 0)
                            {
                                feedbackLogger.info("Synchronizing {}s - processed: {}", type, index);
                            }
                            synchronizeEntity(entity, processing, processed);
                        }
                        catch (SecurityException e)
                        {
                            setFeedback(e);
                            return false;
                        }
                        return true;
                    }
                };
                feedbackLogger.info("Synchronizing all {}s", type);
                securityEntityManager.getAllEntities(type,handler);
                if (handler.getFeedback() != null)
                {
                    feedbackLogger.error("Synchronizing {}s aborted. Processed: {}", type, handler.getSize());
                    throw (SecurityException)handler.getFeedback();
                }
                feedbackLogger.info("Synchronizing {}s done. Processed: {}", type, handler.getSize());
            }
            feedbackLogger.info("Synchronizing all entities done.");
        }
        finally
        {
            setSynchronizing(false);
        }
    }

    public synchronized void synchronizePrincipalsByType(final String type) throws SecurityException
    {
        setSynchronizing(true);
        try
        {
            final Map<String,Set<String>> processing = new HashMap<String,Set<String>>();
            final Map<String,Map<String,String>> processed = new HashMap<String,Map<String,String>>(); 
            BaseEntitySearchResultHandler handler = new BaseEntitySearchResultHandler()
            {
                @Override
                protected boolean processSearchResult(Entity entity, int pageSize, int pageIndex, int index)
                {
                    try
                    {
                        if (feedbackAfterSyncCount > 0 && index % feedbackAfterSyncCount == 0)
                        {
                            feedbackLogger.info("Synchronizing {}s - processed: {}", type, index);
                        }
                        synchronizeEntity(entity, processing, processed);
                    }
                    catch (SecurityException e)
                    {
                        if (abortOnError)
                        {
                            feedbackLogger.error("Failed to synchronize {}: {}", type, entity.getInternalId());
                            setFeedback(e);
                            return false;
                        }
                        feedbackLogger.warn("Failed to synchronize {}: {}", type, entity.getInternalId());
                        logger.error("Failed to synchronize "+type+": "+entity, e);
                        return true;
                    }
                    return true;
                }
            };
            feedbackLogger.info("Synchronizing {}s", type);
            securityEntityManager.getAllEntities(type, handler);
            if (handler.getFeedback() != null)
            {
                feedbackLogger.error("Synchronizing {}s aborted. Processed: {}", type, handler.getSize());
                throw (SecurityException)handler.getFeedback();
            }
            feedbackLogger.info("Synchronizing {}s done. Processed: {}", type, handler.getSize());
        }
        finally
        {
            setSynchronizing(false);
        }
    }

    public synchronized void synchronizeUserPrincipal(String name) throws SecurityException
    {
        setSynchronizing(true);
        try
        {
            feedbackLogger.debug("Synchronizing UserPrincipal({})", name);
            Entity userEntity = securityEntityManager.getEntity(JetspeedPrincipalType.USER, name);
            if (userEntity != null)
            {
                synchronizeEntity(userEntity, new HashMap<String,Set<String>>(), new HashMap<String,Map<String,String>>());
            }
            else 
            {
                throw new SecurityException(SecurityException.PRINCIPAL_DOES_NOT_EXIST.createScoped(JetspeedPrincipalType.USER, name));
            }
        }
        finally
        {
            setSynchronizing(false);
        }
    }

    protected JetspeedPrincipal synchronizeEntity(final Entity entity, final Map<String,Set<String>> processing, final Map<String,Map<String,String>> processed) throws SecurityException
    {
        JetspeedPrincipal principal = null;
        if (processing != null && processing.get(entity.getType()) != null && processing.get(entity.getType()).contains(entity.getId()))
        {
            // TODO: throw proper security exception type
            throw new IllegalStateException("Circular relationship detected for Entity type "+entity.getType()+" id: "+entity.getId());
        }
        if (processed.get(entity.getType()) != null && processed.get(entity.getType()).containsKey(entity.getId()))
        {
            String principalName = processed.get(entity.getType()).get(entity.getId());
            return principalName != null ? getJetspeedPrincipal(entity.getType(),principalName) : null;
        }
        feedbackLogger.debug("Synchronizing entity {} id: {}",entity.getType(),entity.getId());
        // synchronize and collect Entity from relations first
        Set<JetspeedPrincipalAssociationReference> toAssociations = synchronizeEntityFromRelations(entity, processing, processed);
        // create or update entity itself including all its from associations
        principal = synchronizeEntity(entity, toAssociations);
        Map<String,String> entitiesMap = processed.get(entity.getType());
        if (entitiesMap == null)
        {
            entitiesMap = new HashMap<String,String>();
            processed.put(entity.getType(), entitiesMap);
        }
        entitiesMap.put(entity.getId(), entity.getId());
        return principal;
    }
    
    protected Set<JetspeedPrincipalAssociationReference> synchronizeEntityFromRelations(final Entity entity, final Map<String,Set<String>> processing, final Map<String,Map<String,String>> processed) throws SecurityException
    {
        final Set<JetspeedPrincipalAssociationReference> toAssociations = new HashSet<JetspeedPrincipalAssociationReference>();
        // loop through all relation types for this entity type
        for (final SecurityEntityRelationType relationTypeForThisEntity : securityEntityManager.getSupportedEntityRelationTypes(entity.getType()))
        {
            if (relationTypeForThisEntity.getFromEntityType().equals(entity.getType()))
            {
                final String toEntityType = relationTypeForThisEntity.getToEntityType();
                final Map<String,String> processedToType = processed.containsKey(toEntityType) ? processed.get(toEntityType) : new HashMap<String,String>();
                final Set<String> processingToType = processing.containsKey(toEntityType) ? processing.get(toEntityType) : null;
                BaseEntitySearchResultHandler handler = new BaseEntitySearchResultHandler()
                {
                    @Override
                    protected boolean processSearchResult(Entity relatedEntity, int pageSize, int pageIndex, int index)
                    {
                        try
                        {
                            JetspeedPrincipal principal = null;
                            if (processingToType != null && processingToType.contains(relatedEntity.getId()))
                            {
                                // TODO: throw proper security exception type
                                throw new IllegalStateException("Circular relationship detected for Entity type "+toEntityType+" id: "+relatedEntity.getId());
                            }
                            else if (processedToType != null && processedToType.containsKey(relatedEntity.getId()))
                            {
                                String principalName = processed.get(relatedEntity.getType()).get(relatedEntity.getId());
                                principal = principalName != null ? getJetspeedPrincipal(relatedEntity.getType(),principalName) : null;
                            }
                            else
                            {
                                Set<String> processingFromType = processing.get(entity.getType());
                                if (processingFromType == null)
                                {
                                    processingFromType = new HashSet<String>();
                                    processing.put(entity.getType(), processingFromType);
                                }
                                processingFromType.add(entity.getId());
                                principal = synchronizeEntity(relatedEntity, processing, processed);
                            }
                            if (principal != null)
                            {
                                toAssociations.add(new JetspeedPrincipalAssociationReference(JetspeedPrincipalAssociationReference.Type.TO, principal, relationTypeForThisEntity.getRelationType()));
                            }
                        }
                        catch (SecurityException e)
                        {
                            setFeedback(e);
                            return false;
                        }
                        return true;
                    }
                };
                securityEntityManager.getRelatedEntitiesFrom(entity, relationTypeForThisEntity, handler);
                if (handler.getFeedback() != null)
                {
                    throw (SecurityException)handler.getFeedback();
                }
                
                Set<String> processingFromType = processing.get(entity.getType());
                if (processingFromType != null)
                {
                    processingFromType.remove(entity.getId());
                }
            }
        }
        return toAssociations;
    }
    
    protected JetspeedPrincipal synchronizeEntity(Entity entity, Set<JetspeedPrincipalAssociationReference> toAssociations) throws SecurityException
    {
        JetspeedPrincipal principal = getJetspeedPrincipal(entity.getType(), entity.getId());
        JetspeedPrincipalManager principalManager = principalManagerProvider.getManager(principalManagerProvider.getPrincipalType(entity.getType()));

        boolean syncAll = false;
        
        if (principal == null)
        {
            // principal does not exist yet, create a new one using the principal manager
            principal = principalManager.newPrincipal(entity.getId(), true);
            principalManager.addPrincipal(principal, toAssociations);
            syncAll = true;
        }
        else if (!principal.isMapped())
        {
            feedbackLogger.warn("Found {} principal: {} is not mapped therefore not synchronized!", principal.getType().getName(),principal.getName());
            return null;
        }
        else
        {
            // sync relations
            for (final SecurityEntityRelationType relationType : securityEntityManager.getSupportedEntityRelationTypes(entity.getType()))
            {
                if (relationType.getFromEntityType().equals(entity.getType()))
                {
                    List<? extends JetspeedPrincipal> associatedFrom = principalManager.getAssociatedFrom(principal.getName(), principal.getType(), relationType.getRelationType());
                    for (JetspeedPrincipal p : associatedFrom)
                    {
                        if (toAssociations.isEmpty() || 
                                        !toAssociations.remove(new JetspeedPrincipalAssociationReference(JetspeedPrincipalAssociationReference.Type.TO, p, relationType.getRelationType())))
                        {
                            principalManager.removeAssociation(principal, p, relationType.getRelationType());
                        }
                    }
                }
            }
            for (JetspeedPrincipalAssociationReference ref : toAssociations)
            {
                principalManager.addAssociation(principal, ref.ref, ref.associationName);
            }
        }
        boolean updated = false;        
        SecurityAttributes principalAttrs = principal.getSecurityAttributes();
        for (AttributeDef attrDef : entity.getAttributeDefinitions())
        {
            if (attrDef.isMapped() && !attrDef.isMultiValue())
            {
                Attribute attr = entity.getAttribute(attrDef.getName());
                if (attr == null)
                {
                    if (!syncAll)
                    {
                        // if principal has attr: remove it
                        SecurityAttribute principalAttr = principalAttrs.getAttribute(attrDef.getMappedName());
                        if (principalAttr != null)
                        {
                            feedbackLogger.debug("Removing attribute {} for principal {}", principalAttr.getName(), principal.getName());
                            principalAttrs.removeAttribute(principalAttr.getName());
                            updated = true;
                        }
                    }
                }
                else if (syncAll)
                {
                    SecurityAttribute principalAttr = principalAttrs.getAttribute(attrDef.getMappedName(), true);
                    if (feedbackLogger.isDebugEnabled())
                    {
                        feedbackLogger.debug("Adding attribute {} for principal {}. Value: {}", 
                                             new String[] {principalAttr.getName(), principal.getName(), attr.getValue()});
                    }
                    principalAttr.setStringValue(attr.getValue());
                    updated = true;
                }
                else
                {
                    SecurityAttribute principalAttr = principalAttrs.getAttribute(attrDef.getMappedName(), true);
                    if (!StringUtils.equals(principalAttr.getStringValue(), attr.getValue()))
                    {
                        if (feedbackLogger.isDebugEnabled())
                        {
                            feedbackLogger.debug("Attribute attribute {} for principal {}. Old value: {}, new value: {}", 
                                                 new String[] {principalAttr.getName(), principal.getName(), (principalAttr.getStringValue()), attr.getValue()});
                        }
                        principalAttr.setStringValue(attr.getValue());
                        updated = true;
                    }
                }
            }
        }
        if (updated)
        {
            feedbackLogger.debug("Storing attribute changes for principal {}", principal.getName());
            principalManager.updatePrincipal(principal);
        }
        feedbackLogger.debug("Synchronized entity {} id: {} mapped attributes", entity.getType(), entity.getId());
        return principal;
    }

    protected JetspeedPrincipal getJetspeedPrincipal(String principalType, String principalName) throws SecurityException
    {
        JetspeedPrincipalManager principalManager = principalManagerProvider.getManager(principalManagerProvider.getPrincipalType(principalType));
        if (principalManager != null)
        {
            return principalManager.getPrincipal(principalName);
        }
        throw new SecurityException(SecurityException.UNKNOWN_PRINCIPAL_TYPE.create(principalType));
    }

    protected void setSynchronizing(boolean sync)
    {
        SynchronizationStateAccess.setSynchronizing(sync ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setPrincipalManagerProvider(JetspeedPrincipalManagerProvider principalManagerProvider)
    {
        this.principalManagerProvider = principalManagerProvider;
    }
}