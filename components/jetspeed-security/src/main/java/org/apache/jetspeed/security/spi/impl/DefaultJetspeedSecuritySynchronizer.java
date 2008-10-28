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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.JetspeedPrincipalManagerProvider;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributeType;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.mapping.SecurityEntityManager;
import org.apache.jetspeed.security.mapping.SecurityEntityRelationType;
import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.spi.JetspeedSecuritySynchronizer;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class DefaultJetspeedSecuritySynchronizer implements JetspeedSecuritySynchronizer
{
    private static final Log logger = LogFactory.getLog(DefaultJetspeedSecuritySynchronizer.class);
    JetspeedPrincipalManagerProvider principalManagerProvider;
    SecurityEntityManager securityEntityManager;
    Collection<String> supportedExternalEntityTypes = Collections.emptyList();
    Collection<SecurityEntityRelationType> supportedExternalEntityRelationTypes = Collections.emptyList();
    Map<String, Collection<SecurityEntityRelationType>> entityToRelationTypes = Collections.emptyMap();

    /**
     * @param principalManagerProvider
     * @param securityEntityManager
     */
    public DefaultJetspeedSecuritySynchronizer(JetspeedPrincipalManagerProvider principalManagerProvider, SecurityEntityManager securityEntityManager)
    {
        this.principalManagerProvider = principalManagerProvider;
        this.securityEntityManager = securityEntityManager;
        createRelations();
    }

    public void synchronizeAll()
    {
        setSynchronizing(true);
        try
        {
            // don't skip any entity type when synchronizing all
            Collection<String> skipEntities = new ArrayList<String>();
            InternalSynchronizationState synchronizationState = new InternalSynchronizationState(skipEntities);
            for (String type : securityEntityManager.getSupportedEntityTypes())
            {
                for (Entity entity : securityEntityManager.getAllEntities(type))
                {
                    recursiveSynchronizeEntity(entity, synchronizationState);
                }
            }
        }
        finally
        {
            setSynchronizing(false);
        }
    }

    public void synchronizePrincipalsByType(String type)
    {
        setSynchronizing(true);
        try
        {
            Collection<Entity> entites = securityEntityManager.getAllEntities(type);
            Collection<String> skipEntities = new ArrayList<String>();
            if (!type.equals(JetspeedPrincipalType.USER))
            {
                // skip synchronizing users when not synchronizing the USER type itself
                skipEntities.add(JetspeedPrincipalType.USER);
            }
            InternalSynchronizationState synchronizationState = new InternalSynchronizationState(skipEntities);
            for (Entity entity : entites)
            {
                recursiveSynchronizeEntity(entity, synchronizationState);
            }
        }
        finally
        {
            setSynchronizing(false);
        }
    }

    private void recursiveSynchronizeEntity(Entity entity, InternalSynchronizationState syncState)
    {
        JetspeedPrincipal updatedPrincipal = null;
        if (entity != null && !syncState.isProcessed(entity))
        {
            // mark as processed, to avoid nasty loops
            syncState.setProcessed(entity);
            // update / create corresponding JetspeedPrincipal first
            updatedPrincipal = synchronizePrincipalAttributes(entity);
            if (updatedPrincipal != null)
            {
                // Synchronizing Relations
                synchronizePrincipalRelation(updatedPrincipal, entity, syncState);
            }
        }
    }

    public void synchronizeUserPrincipal(String name)
    {
        setSynchronizing(true);
        try
        {
            // don't process relations going towards users to avoid sync'ing huge
            // amounts of data.
            // TODO: allow processing of required relations towards users.
            Collection<String> skipEntities = Arrays.asList(new String[] { JetspeedPrincipalType.USER });
            recursiveSynchronizePrincipal(securityEntityManager.getEntity(JetspeedPrincipalType.USER, name), new InternalSynchronizationState(skipEntities));
        }
        finally
        {
            setSynchronizing(false);
        }
    }

    public JetspeedPrincipal recursiveSynchronizePrincipal(Entity entity, InternalSynchronizationState syncState)
    {
        JetspeedPrincipal updatedPrincipal = null;
        if (entity != null && !syncState.isProcessed(entity))
        {
            // mark as processed, to avoid nasty loops
            syncState.setProcessed(entity);
            // update / create corresponding JetspeedPrincipal first
            updatedPrincipal = synchronizePrincipalAttributes(entity);
            if (updatedPrincipal != null)
            {
                // Synchronizing Relations
                synchronizePrincipalRelation(updatedPrincipal, entity, syncState);
            }
        }
        return updatedPrincipal;
    }

    protected JetspeedPrincipal synchronizePrincipalRelation(JetspeedPrincipal principal, Entity entity, InternalSynchronizationState syncState)
    {
        if (entityToRelationTypes.values().size() != 0)
            // loop through all relation types for this entity type
            for (SecurityEntityRelationType relationTypeForThisEntity : entityToRelationTypes.get(entity.getType()))
            {
                // check at what side of the relationship this entity
                // represents (from or to) and check whether
                // entities on the other side should be synchronized.Entity
                // entity
                if (relationTypeForThisEntity.getFromEntityType().equals(entity.getType()))
                {
                    if (syncState.shouldFollowRelationTo(relationTypeForThisEntity.getToEntityType()))
                    {
                        Collection<String> updatedAssociationToNames = synchronizeAssociations(relationTypeForThisEntity, entity, principal, true, syncState);
                        synchronizeRemovedAssociations(updatedAssociationToNames, relationTypeForThisEntity.getRelationType(), principal, true);
                    }
                }
                // the entity can represent either side or *both* sides of
                // the relationship, so synchronize both ways.
                if (relationTypeForThisEntity.getToEntityType().equals(entity.getType()))
                {
                    if (syncState.shouldFollowRelationTo(relationTypeForThisEntity.getFromEntityType()))
                    {
                        Collection<String> updatedAssociationFromNames = synchronizeAssociations(relationTypeForThisEntity, entity, principal, false, syncState);
                        synchronizeRemovedAssociations(updatedAssociationFromNames, relationTypeForThisEntity.getRelationType(), principal, false);
                    }
                }
            }
        return principal;
    }

    protected JetspeedPrincipal synchronizePrincipalRelations(JetspeedPrincipal principal, Entity entity, InternalSynchronizationState syncState)
    {
        if (entityToRelationTypes.values().size() != 0)
            // loop through all relation types for this entity type
            for (SecurityEntityRelationType relationTypeForThisEntity : entityToRelationTypes.get(entity.getType()))
            {
                // check at what side of the relationship this entity
                // represents (from or to) and check whether
                // entities on the other side should be synchronized.Entity
                // entity
                if (relationTypeForThisEntity.getFromEntityType().equals(entity.getType()))
                {
                    if (syncState.shouldFollowRelationTo(relationTypeForThisEntity.getToEntityType()))
                    {
                        Collection<String> updatedAssociationToNames = synchronizeAddedAssociations(relationTypeForThisEntity, entity, principal, true,
                                                                                                    syncState);
                        synchronizeRemovedAssociations(updatedAssociationToNames, relationTypeForThisEntity.getRelationType(), principal, true);
                    }
                }
                // the entity can represent either side or *both* sides of
                // the relationship, so synchronize both ways.
                if (relationTypeForThisEntity.getToEntityType().equals(entity.getType()))
                {
                    if (syncState.shouldFollowRelationTo(relationTypeForThisEntity.getFromEntityType()))
                    {
                        Collection<String> updatedAssociationFromNames = synchronizeAddedAssociations(relationTypeForThisEntity, entity, principal, false,
                                                                                                      syncState);
                        synchronizeRemovedAssociations(updatedAssociationFromNames, relationTypeForThisEntity.getRelationType(), principal, false);
                    }
                }
            }
        return principal;
    }

    protected Collection<String> synchronizeAssociations(SecurityEntityRelationType relationTypeForThisEntity, Entity entity, JetspeedPrincipal principal,
                                                         boolean entityIsFromEntity, InternalSynchronizationState syncState)
    {
        Collection<String> externalRelatedEntityIds = null;
        Collection<Entity> relatedEntities = entityIsFromEntity ? securityEntityManager.getRelatedEntitiesFrom(entity, relationTypeForThisEntity)
                                                               : securityEntityManager.getRelatedEntitiesTo(entity, relationTypeForThisEntity);
        externalRelatedEntityIds = new ArrayList<String>();
        for (Entity relatedEntity : relatedEntities)
        {
            Entity fromEntity = entityIsFromEntity ? entity : relatedEntity;
            Entity toEntity = entityIsFromEntity ? relatedEntity : entity;
            if (!syncState.isRelationProcessed(relationTypeForThisEntity, fromEntity, toEntity))
            {
                // first flag the relation as processed to
                // prevent synchronizing the same relation from
                // the other side.
                syncState.setRelationProcessed(relationTypeForThisEntity, fromEntity, toEntity, entityIsFromEntity);
                // first create/update principal
                // JetspeedPrincipal relatedPrincipal = recursiveSynchronizePrincipal(relatedEntity, syncState);
                
                //TODO change for nested level of group and roles.
                JetspeedPrincipal relatedPrincipal = null;
                JetspeedPrincipalManager principalManager = principalManagerProvider
                                                                                    .getManager(principalManagerProvider
                                                                                                                        .getPrincipalType(relatedEntity
                                                                                                                                                       .getType()));
                if (principalManager != null)
                {
                    relatedPrincipal = principalManager.getPrincipal(relatedEntity.getId());
                }
                // .. then update associations to / from it
                JetspeedPrincipal fromPrincipal = entityIsFromEntity ? principal : relatedPrincipal;
                JetspeedPrincipal toPrincipal = entityIsFromEntity ? relatedPrincipal : principal;
                // does association exist in DB ?
                if (relatedPrincipal != null && !associationExists(fromPrincipal, toPrincipal, relationTypeForThisEntity.getRelationType()))
                {
                    synchronizeAddedPrincipalAssocation(fromPrincipal, toPrincipal, relationTypeForThisEntity.getRelationType());
                    externalRelatedEntityIds.add(relatedPrincipal.getName());
                }
            }
        }
        return externalRelatedEntityIds;
    }

    protected Collection<String> synchronizeAddedAssociations(SecurityEntityRelationType relationTypeForThisEntity, Entity entity, JetspeedPrincipal principal,
                                                              boolean entityIsFromEntity, InternalSynchronizationState syncState)
    {
        Collection<String> externalRelatedEntityIds = null;
        Collection<Entity> relatedEntities = entityIsFromEntity ? securityEntityManager.getRelatedEntitiesFrom(entity, relationTypeForThisEntity)
                                                               : securityEntityManager.getRelatedEntitiesTo(entity, relationTypeForThisEntity);
        externalRelatedEntityIds = new ArrayList<String>();
        for (Entity relatedEntity : relatedEntities)
        {
            Entity fromEntity = entityIsFromEntity ? entity : relatedEntity;
            Entity toEntity = entityIsFromEntity ? relatedEntity : entity;
            if (!syncState.isRelationProcessed(relationTypeForThisEntity, fromEntity, toEntity))
            {
                // first flag the relation as processed to
                // prevent synchronizing the same relation from
                // the other side.
                syncState.setRelationProcessed(relationTypeForThisEntity, fromEntity, toEntity, entityIsFromEntity);
                // first create/update principal
                JetspeedPrincipal relatedPrincipal = recursiveSynchronizePrincipal(relatedEntity, syncState);
                // .. then update associations to / from it
                JetspeedPrincipal fromPrincipal = entityIsFromEntity ? principal : relatedPrincipal;
                JetspeedPrincipal toPrincipal = entityIsFromEntity ? relatedPrincipal : principal;
                // does association exist in DB ?
                if (relatedPrincipal != null && !associationExists(fromPrincipal, toPrincipal, relationTypeForThisEntity.getRelationType()))
                {
                    synchronizeAddedPrincipalAssocation(fromPrincipal, toPrincipal, relationTypeForThisEntity.getRelationType());
                }
                externalRelatedEntityIds.add(relatedPrincipal.getName());
            }
        }
        return externalRelatedEntityIds;
    }

    protected void synchronizeRemovedAssociations(Collection<String> externalRelatedEntityIds, String associationName, JetspeedPrincipal principal,
                                                  boolean isFromPrincipal)
    {
        // check whether associations were removed in external store (e.g.
        // LDAP), but still present in the DB
        JetspeedPrincipalManager principalManager = principalManagerProvider.getManager(principal.getType());
        List<? extends JetspeedPrincipal> relatedToPrincipals = null;
        if (isFromPrincipal)
        {
            relatedToPrincipals = principalManager.getAssociatedFrom(principal.getName(), principal.getType(), associationName);
        }
        else
        {
            relatedToPrincipals = principalManager.getAssociatedTo(principal.getName(), principal.getType(), associationName);
        }
        for (JetspeedPrincipal relatedPrincipal : relatedToPrincipals)
        {
            // check whether principal association still exists
            if (!externalRelatedEntityIds.contains(relatedPrincipal.getId()))
            {
                try
                {
                    if (isFromPrincipal)
                    {
                        principalManager.removeAssociation(principal, relatedPrincipal, associationName);
                    }
                    else
                    {
                        principalManager.removeAssociation(relatedPrincipal, principal, associationName);
                    }
                }
                catch (SecurityException e)
                {
                    if (isFromPrincipal)
                    {
                        logger.error("Unexpected SecurityException trying to remove (" + principal.getType().getName() + "," +
                                     relatedPrincipal.getType().getName() + "," + associationName + ") association during synchronization.", e);
                    }
                    else
                    {
                        logger.error("Unexpected SecurityException trying to remove (" + relatedPrincipal.getType().getName() + "," +
                                     principal.getType().getName() + "," + associationName + ") association during synchronization.", e);
                    }
                }
            }
        }
    }

    protected boolean associationExists(JetspeedPrincipal fromPrincipal, JetspeedPrincipal toPrincipal, String associationName)
    {
        JetspeedPrincipalManager principalManager = principalManagerProvider.getManager(fromPrincipal.getType());
        List<String> toPrincipals = principalManager.getAssociatedNamesFrom(fromPrincipal.getName(), fromPrincipal.getType(), associationName);
        return toPrincipals.contains(toPrincipal.getName());
    }

    protected void synchronizeAddedPrincipalAssocation(JetspeedPrincipal fromPrincipal, JetspeedPrincipal toPrincipal, String associationName)
    {
        JetspeedPrincipalManager principalManager = principalManagerProvider.getManager(fromPrincipal.getType());
        try
        {
            principalManager.addAssociation(fromPrincipal, toPrincipal, associationName);
        }
        catch (SecurityException e)
        {
            logger.error("Unexpected SecurityException during synchronization.", e);
        }
    }

    protected JetspeedPrincipal synchronizePrincipalAttributes(Entity entity)
    {
        JetspeedPrincipal updatedPrincipal = null;
        JetspeedPrincipalManager principalManager = principalManagerProvider.getManager(principalManagerProvider.getPrincipalType(entity.getType()));
        if (principalManager != null)
        {
            updatedPrincipal = principalManager.getPrincipal(entity.getId());
            Map<String, Attribute> mappedEntityAttrs = entity.getMappedAttributes();
            Collection<Attribute> attrsToBeUpdated = new ArrayList<Attribute>();
            if (updatedPrincipal == null)
            {
                // principal does not exist yet, create it using the Jetspeed
                // principal manager
                updatedPrincipal = principalManager.newPrincipal(entity.getId(), true);
                try
                {
                    principalManager.addPrincipal(updatedPrincipal, null);
                }
                catch (SecurityException sexp)
                {
                    if (logger.isErrorEnabled())
                    {
                        logger.error("Unexpected exception in adding new pricipal of type " + updatedPrincipal.getType().getName() + ".", sexp);
                    }
                }
                attrsToBeUpdated.addAll(mappedEntityAttrs.values());
            }
            else if (updatedPrincipal.isMapped())
            {
                SecurityAttributes principalAttrs = updatedPrincipal.getSecurityAttributes();
                for (Map.Entry<String, Attribute> entityAttrEntry : mappedEntityAttrs.entrySet())
                {
                    SecurityAttribute principalAttr = principalAttrs.getAttribute(entityAttrEntry.getKey());
                    Attribute entityAttr = entityAttrEntry.getValue();
                    if (principalAttr != null)
                    {
                        if (entityAttr.getDefinition().isMultiValue())
                        {
                            // TODO : multi-valued Principal attrs are not yet
                            // supported
                        }
                        else
                        {
                            if (!StringUtils.equals(principalAttr.getStringValue(), entityAttr.getValue()))
                            {
                                attrsToBeUpdated.add(entityAttr);
                            }
                        }
                    }
                    else
                    {
                        attrsToBeUpdated.add(entityAttr);
                    }
                }
            }
            SecurityAttributes principalAttrs = updatedPrincipal.getSecurityAttributes();
            Map<String, SecurityAttributeType> securityAttrTypes = principalAttrs.getSecurityAttributeTypes().getAttributeTypeMap();
            // Step 1. update principal's attributes
            for (Attribute addedEntityAttr : attrsToBeUpdated)
            {
                if (!addedEntityAttr.getDefinition().isMultiValue())
                {
                    SecurityAttribute principalAttr = null;
                    try
                    {
                        SecurityAttributeType securityAttrType = securityAttrTypes.get(addedEntityAttr.getMappedName());
                        if (securityAttrType != null)
                        {
                            principalAttr = principalAttrs.getAttribute(addedEntityAttr.getMappedName(), true);
                        }
                        if (principalAttr != null)
                            principalAttr.setStringValue(addedEntityAttr.getValue());
                    }
                    catch (SecurityException e)
                    {
                        if (logger.isErrorEnabled())
                        {
                            logger.error("Unexpected exception for attribute " + addedEntityAttr.getMappedName() + ".", e);
                        }
                    }
                }
            }
            if (updatedPrincipal.isMapped())
            {
                boolean updated = (attrsToBeUpdated.size() > 0);
                // Step 2, check whether attributes should be removed.
                for (Map.Entry<String, SecurityAttribute> principalAttrEntry : principalAttrs.getAttributeMap().entrySet())
                {
                    // TODO: check whether this attribute is mapped
                    if (!mappedEntityAttrs.containsKey(principalAttrEntry.getKey()))
                    {
                        try
                        {
                            principalAttrs.removeAttribute(principalAttrEntry.getKey());
                            updated = true;
                        }
                        catch (SecurityException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                // step 3, update synchronized principal
                if (updated)
                {
                    try
                    {
                        principalManager.updatePrincipal(updatedPrincipal);
                    }
                    catch (SecurityException e)
                    {
                        logger.error("Unexpected SecurityException: could not synchronize principal " + updatedPrincipal.getName() + " of type " +
                                     updatedPrincipal.getType().getName(), e);
                    }
                }
            }
        }
        else
        {
            // TODO throw proper exception
        }
        return updatedPrincipal;
    }

    private void setSynchronizing(boolean sync)
    {
        SynchronizationStateAccess.setSynchronizing(sync ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setPrincipalManagerProvider(JetspeedPrincipalManagerProvider principalManagerProvider)
    {
        this.principalManagerProvider = principalManagerProvider;
    }

    private void createRelations()
    {
        supportedExternalEntityTypes = securityEntityManager.getSupportedEntityTypes();
        supportedExternalEntityRelationTypes = securityEntityManager.getSupportedEntityRelationTypes();
        entityToRelationTypes = new HashMap<String, Collection<SecurityEntityRelationType>>();
        for (String entityType : supportedExternalEntityTypes)
        {
            entityToRelationTypes.put(entityType, securityEntityManager.getSupportedEntityRelationTypes(entityType));
        }
    }

    private class InternalSynchronizationState
    {
        // entity type to processed entity IDs map
        Map<String, Set<String>> processedEntities = new HashMap<String, Set<String>>();
        // map relation type to a "from entity" -> "to entity" mapping
        Map<SecurityEntityRelationType, Map<String, Collection<String>>> processedEntityRelationsFromTo = new HashMap<SecurityEntityRelationType, Map<String, Collection<String>>>();
        // Entity types which are not processed
        // This is implemented as not following relations towards entities of
        // these types. E.g. if skipEntities contains the "user" type, and
        // isProcessedFromTo(..) is invoked,
        // where the toEntity is of type "user", then the result of
        // isProcessedFromTo will be "true", to effectively skip the processing
        // of entities of type "user".
        // The same goes for isProcessedToFrom(..) : if the type of fromEntity
        // is "user", the relation is flagged as processed.
        Collection<String> skipEntities;

        InternalSynchronizationState(Collection<String> skipEntities)
        {
            this.skipEntities = skipEntities;
        }

        private boolean isProcessed(Entity entity)
        {
            Set<String> processedEntitiesByType = processedEntities.get(entity.getType());
            return processedEntitiesByType != null && processedEntitiesByType.contains(entity.getId());
        }

        private boolean shouldFollowRelationTo(String relationType)
        {
            return !skipEntities.contains(relationType);
        }

        private void setProcessed(Entity entity)
        {
            Set<String> processedEntitiesByType = processedEntities.get(entity.getType());
            if (processedEntitiesByType == null)
            {
                processedEntitiesByType = new HashSet<String>();
            }
            processedEntitiesByType.add(entity.getId());
        }

        private boolean isRelationProcessed(SecurityEntityRelationType relationType, Entity fromEntity, Entity toEntity)
        {
            Map<String, Collection<String>> e2eMap = processedEntityRelationsFromTo.get(relationType);
            if (e2eMap != null)
            {
                Collection<String> endIds = e2eMap.get(fromEntity.getId());
                return endIds != null && endIds.contains(toEntity.getId());
            }
            return false;
        }

        private void setRelationProcessed(SecurityEntityRelationType relationType, Entity startEntity, Entity endEntity, boolean startEntityIsFrom)
        {
            if (startEntityIsFrom)
            {
                setRelationProcessed(relationType, startEntity, endEntity);
            }
            else
            {
                setRelationProcessed(relationType, endEntity, startEntity);
            }
        }

        private void setRelationProcessed(SecurityEntityRelationType relationType, Entity fromEntity, Entity toEntity)
        {
            Map<String, Collection<String>> e2eMap = processedEntityRelationsFromTo.get(relationType);
            if (e2eMap == null)
            {
                e2eMap = new HashMap<String, Collection<String>>();
                processedEntityRelationsFromTo.put(relationType, e2eMap);
            }
            Collection<String> endIds = e2eMap.get(fromEntity.getId());
            if (endIds == null)
            {
                endIds = new ArrayList<String>();
                e2eMap.put(fromEntity.getId(), endIds);
            }
            endIds.add(toEntity.getId());
        }
    }
}