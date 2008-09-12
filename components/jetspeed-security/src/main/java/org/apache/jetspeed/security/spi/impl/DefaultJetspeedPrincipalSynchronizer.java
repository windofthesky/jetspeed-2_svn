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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.AttributeReadOnlyException;
import org.apache.jetspeed.security.AttributeRequiredException;
import org.apache.jetspeed.security.AttributeTypeNotFoundException;
import org.apache.jetspeed.security.AttributesReadOnlyException;
import org.apache.jetspeed.security.JetspeedPrincipal;
import org.apache.jetspeed.security.JetspeedPrincipalManager;
import org.apache.jetspeed.security.JetspeedPrincipalManagerProvider;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.PrincipalUpdateException;
import org.apache.jetspeed.security.SecurityAttribute;
import org.apache.jetspeed.security.SecurityAttributeType;
import org.apache.jetspeed.security.SecurityAttributes;
import org.apache.jetspeed.security.mapping.SecurityEntityManager;
import org.apache.jetspeed.security.mapping.SecurityEntityRelationType;
import org.apache.jetspeed.security.mapping.model.Attribute;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.spi.JetspeedPrincipalSynchronizer;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public class DefaultJetspeedPrincipalSynchronizer implements
        JetspeedPrincipalSynchronizer
{

    private static final Log logger = LogFactory.getLog(DefaultJetspeedPrincipalSynchronizer.class);
    
    JetspeedPrincipalManagerProvider principalManagerProvider;

    SecurityEntityManager securityEntityManager;

    Collection<String> supportedExternalEntityTypes = Collections.emptyList();

    Collection<SecurityEntityRelationType> supportedExternalEntityRelationTypes = Collections
            .emptyList();

    Map<String, Collection<SecurityEntityRelationType>> entityToRelationTypes = Collections
            .emptyMap();

    public void synchronizeAll()
    {

    }

    public void synchronizeUserPrincipal(String name)
    {
        
        JetspeedPrincipalManager manager = principalManagerProvider
                .getManager(principalManagerProvider
                        .getPrincipalType(JetspeedPrincipalType.USER_TYPE_NAME));
        if (manager != null)
        {
            Collection<String> skipTypes = new ArrayList<String>();
            skipTypes.add(JetspeedPrincipalType.USER_TYPE_NAME);
            // TODO do not skip user relations which are required!
            Collection<SecurityEntityRelationType> userRelations = securityEntityManager.getSupportedEntityRelationTypes(JetspeedPrincipalType.USER_TYPE_NAME);
            recursiveSynchronizePrincipal(securityEntityManager.getEntity(JetspeedPrincipalType.USER_TYPE_NAME, name), new SynchronizationState(userRelations));
        }
    }

    public void recursiveSynchronizePrincipal(Entity entity, SynchronizationState syncState) 
    {
        JetspeedPrincipal upToDatePrincipal=null;
        if (entity != null && !syncState.isProcessed(entity)){
            // mark as processed, to avoid nasty loops
            syncState.setProcessed(entity);
            
            Collection<SecurityEntityRelationType> notRequiredRelationTypes=new ArrayList<SecurityEntityRelationType>();
            for(SecurityEntityRelationType relationTypeForThisPrincipal : entityToRelationTypes.get(entity.getType())){
                    String fromType = relationTypeForThisPrincipal.getFromEntityType();
                    String toType = relationTypeForThisPrincipal.getToEntityType();
                    // check at what side of the relationship this entity represents (from or to) and check whether
                    // entities on the other side should be synchronized.Entity entity
                    if (fromType.equals(entity.getType())){
                        if (!syncState.isProcessedFrom(relationTypeForThisPrincipal,entity)){
                            if (isRequiredRelation(relationTypeForThisPrincipal)){
                                syncState.setProcessedFrom(relationTypeForThisPrincipal, entity);
                                Collection<Entity> relatedEntities = securityEntityManager.getRelatedEntitiesTo(entity, relationTypeForThisPrincipal);
                                for (Entity relatedEntity : relatedEntities)
                                {
                                    // first create/update principal
                                    recursiveSynchronizePrincipal(relatedEntity, syncState);
                                    // .. then update associations
                                    synchronizePrincipalAssocation(relationTypeForThisPrincipal,entity,relatedEntity);
                                }
                            } else {
                                // process relation later: a related principal (related either indirect or direct) might be 
                                // dependent on the currently processed entity. So first update/add the current principal.
                                notRequiredRelationTypes.add(relationTypeForThisPrincipal);
                            }
                        }    
                        
                    }
                    // the entity can represent *both* sides of the relationship, so synchronize both ways.
                    if (toType.equals(entity.getType())){
                        if (!syncState.isProcessedTo(relationTypeForThisPrincipal,entity)){
                            Collection<Entity> relatedEntities = securityEntityManager.getRelatedEntitiesTo(entity, relationTypeForThisPrincipal);
                            for (Entity relatedEntity : relatedEntities)
                            {
                                recursiveSynchronizePrincipal(relatedEntity, syncState);
                            }
                        }                            
                    }
            }
                                   
            synchronizePrincipalAttributes(entity);
            
            for(SecurityEntityRelationType relationTypeForThisPrincipal : notRequiredRelationTypes){
                
            }
        }
    }
    
    private boolean isRequiredRelation(SecurityEntityRelationType relationType){
        return false; // TODO: wait until 
    }
    
    protected void synchronizePrincipalAssocation(SecurityEntityRelationType relationType, Entity fromEntity, Entity toEntity){
        JetspeedPrincipalManager principalManager = principalManagerProvider.getManager(principalManagerProvider.getPrincipalType(fromEntity.getType()));
        
        // principalManager.getAssociatedTo(principalToName, to, associationName)
    }
    
    protected void synchronizePrincipalAttributes(Entity entity){ 
        JetspeedPrincipalManager principalManager = principalManagerProvider
        .getManager(principalManagerProvider
                .getPrincipalType(entity.getType()));
        if (principalManager!=null){
            JetspeedPrincipal principal = principalManager.getPrincipal(entity.getId());
            Map<String,Attribute> mappedEntityAttrs = entity.getMappedAttributes();
            Collection<Attribute> attrsToBeUpdated = new ArrayList<Attribute>();
            if (principal == null){
                // principal does not exist yet, create it using the Jetspeed principal manager
                principal = principalManager.newPrincipal(entity.getId(), true);
                attrsToBeUpdated.addAll(mappedEntityAttrs.values());
            } else if (!principal.isReadOnly() && principal.isMapped()) {
                SecurityAttributes principalAttrs = principal.getSecurityAttributes();
                for (Map.Entry<String,Attribute> entityAttrEntry : mappedEntityAttrs.entrySet()){
                    SecurityAttribute principalAttr = principalAttrs.getAttribute(entityAttrEntry.getKey());
                    Attribute entityAttr = entityAttrEntry.getValue();
                    if (principalAttr!=null){
                        if (entityAttr.getDefinition().isMultiValue()){
                            // TODO : multi-valued Principal attrs are not yet supported
                        } else {
                            if (!StringUtils.equals(principalAttr.getStringValue(), entityAttr.getValue())){                                
                                attrsToBeUpdated.add(entityAttr);
                            }
                        }
                    } else {
                        attrsToBeUpdated.add(entityAttr);
                    }
                }
            }

            SecurityAttributes principalAttrs = principal.getSecurityAttributes();
            Map<String,SecurityAttributeType> securityAttrTypes = principalAttrs.getSecurityAttributeTypes().getAttributeTypeMap();
            
            // Step 1. update principal's attributes
            for (Attribute addedEntityAttr : attrsToBeUpdated ){
                if (!addedEntityAttr.getDefinition().isMultiValue()){
                    SecurityAttribute principalAttr = null;                
                    try
                    {
                        SecurityAttributeType securityAttrType =  securityAttrTypes.get(addedEntityAttr.getMappedName());
                        if (securityAttrType!=null && !securityAttrType.isReadOnly()){
                            principalAttr = principalAttrs.getAttribute(addedEntityAttr.getMappedName(),true);
                        }
                        principalAttr.setStringValue(addedEntityAttr.getValue());
                    } catch (AttributesReadOnlyException e)
                    {
                        if (logger.isErrorEnabled()){
                            logger.error("Unexpected read-only exception for attribute "+addedEntityAttr.getMappedName()+".",e);                        
                        }
                    } catch (AttributeTypeNotFoundException e)
                    {
                        if (logger.isErrorEnabled()){
                            logger.error("Unexpected missing type exception for attribute "+addedEntityAttr.getMappedName()+".",e);                        
                        }
                    }
                }
            }
            if (principal.isMapped() && !principal.isReadOnly()){
                boolean updated = (attrsToBeUpdated.size() > 0);
                // Step 2, check whether attributes should be removed.
                for (Map.Entry<String,SecurityAttribute> principalAttrEntry : principalAttrs.getAttributeMap().entrySet() ){
                    // TODO: check whether this attribute is mapped
                    if (!mappedEntityAttrs.containsKey(principalAttrEntry.getKey())){
                        try
                        {
                            principalAttrs.removeAttribute(principalAttrEntry.getKey());
                            updated=true;
                        } catch (AttributesReadOnlyException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (AttributeReadOnlyException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (AttributeRequiredException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                // step 3, update synchronized principal
                if (updated){
                    try
                    {
                        principalManager.updatePrincipal(principal);
                    } catch (PrincipalUpdateException e)
                    {
                        if (logger.isErrorEnabled()){
                            logger.error("Could not synchronize principal "+principal.getName()+" of type "+principal.getType().getName(),e);
                        }
                    } catch (Exception e)
                    {
                        if (logger.isErrorEnabled()){
                            logger.error("Unexpected exception trying to update principal during synchronization.",e);
                        }
                    } 
                }
            }

        } else {
            // TODO throw proper exception
        }
        
    }

    public void synchronizePrincipalsByType(String principalTypeName)
    {

    }

    public void setPrincipalManagerProvider(
            JetspeedPrincipalManagerProvider principalManagerProvider)
    {
        this.principalManagerProvider = principalManagerProvider;
    }

    public void setSecurityEntityManager(
            SecurityEntityManager securityEntityManager)
    {
        this.securityEntityManager = securityEntityManager;
        supportedExternalEntityTypes = securityEntityManager
                .getSupportedEntityTypes();
        supportedExternalEntityRelationTypes = securityEntityManager
                .getSupportedEntityRelationTypes();
        entityToRelationTypes = new HashMap<String, Collection<SecurityEntityRelationType>>();
        for (String entityType : supportedExternalEntityTypes)
        {
            entityToRelationTypes.put(entityType, securityEntityManager
                    .getSupportedEntityRelationTypes(entityType));
        }
    }
    
    private class SynchronizationState {
        
        // entity type to processed entity IDs map
        Map<String, Set<String>> processedEntities=new HashMap<String, Set<String>>();   
        // map from relation type to from entity id
        Map<SecurityEntityRelationType, Collection<String>> processedEntityRelationsFrom = new HashMap<SecurityEntityRelationType, Collection<String>>();
        // map from relation type to "to entity id"
        Map<SecurityEntityRelationType, Collection<String>> processedEntityRelationsTo = new HashMap<SecurityEntityRelationType, Collection<String>>();
        // Relations which are not checked: this can be used when updating a user principal without indirectly updating other users.
        Collection<SecurityEntityRelationType> skipRelations;
        
        SynchronizationState(Collection<SecurityEntityRelationType> skipRelations){
            this.skipRelations=skipRelations;
        }
        
        public boolean isProcessed(Entity entity){
            Set<String> processedEntitiesByType = processedEntities.get(entity.getType());
            return processedEntitiesByType != null && processedEntitiesByType.contains(entity.getId());
        }

        public void setProcessed(Entity entity){
            Set<String> processedEntitiesByType = processedEntities.get(entity.getType());
            if (processedEntitiesByType==null){
                processedEntitiesByType=new HashSet<String>();                
            }
            processedEntitiesByType.add(entity.getId());
        }
        
        public boolean isProcessedFrom(SecurityEntityRelationType relationType, Entity fromEntity){
            if (skipRelations.contains(relationType)){
                return true; // effectively skip the relation by marking it as processed
            }
            Collection<String> fromIds = processedEntityRelationsFrom.get(relationType);
            return fromIds != null && fromIds.contains(fromEntity.getId());
        }
        
        public boolean isProcessedTo(SecurityEntityRelationType relationType, Entity toEntity){
            if (skipRelations.contains(relationType)){
                return true; // effectively skip the relation by marking it as processed
            }
            Collection<String> toIds = processedEntityRelationsFrom.get(relationType);
            return toIds != null && toIds.contains(toEntity.getId());
        }
        
        public void setProcessedFrom(SecurityEntityRelationType relationType, Entity fromEntity){
            Collection<String> fromIds=processedEntityRelationsFrom.get(relationType);
            if (fromIds==null){
                fromIds=new ArrayList<String>();
                processedEntityRelationsFrom.put(relationType,fromIds);
            }            
            fromIds.add(fromEntity.getId());
        }
        
        public void setProcessedTo(SecurityEntityRelationType relationType, Entity toEntity){
            Collection<String> toIds=processedEntityRelationsTo.get(relationType);
            if (toIds==null){
                toIds=new ArrayList<String>();
                processedEntityRelationsTo.put(relationType,toIds);
            }            
            toIds.add(toEntity.getId());
        }

    }

}
