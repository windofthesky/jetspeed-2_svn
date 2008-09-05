package org.apache.jetspeed.security.mapping;


import java.util.Collection;

import org.apache.jetspeed.security.mapping.model.Entity;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 */
public interface SecurityEntityManager
{

    Entity getEntity(String entityType, String entityId);

    Collection<Entity> getAllEntities(String entityType);

    void update(Entity entity);

    Collection<Entity> getRelatedEntities(Entity sourceEntity,
            String targetEntityType, String relationType);

    void addRelatedEntity(Entity entity, Entity relatedEntity,
            String relationType);

}
