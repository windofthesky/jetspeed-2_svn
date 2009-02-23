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

import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;

import org.apache.jetspeed.components.dao.InitablePersistenceBrokerDaoSupport;
import org.apache.jetspeed.i18n.KeyedMessage;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.om.InternalGroupPrincipal;
import org.apache.jetspeed.security.om.InternalRolePrincipal;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalGroupPrincipalImpl;
import org.apache.jetspeed.security.om.impl.InternalRolePrincipalImpl;
import org.apache.jetspeed.security.om.impl.InternalUserPrincipalImpl;
import org.apache.jetspeed.security.spi.SecurityAccess;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;

/**
 * <p>
 * Provides a utility class for common SPI queries.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 */
public class SecurityAccessImpl extends InitablePersistenceBrokerDaoSupport implements SecurityAccess
{
    

    /**
     * 
     * @param repositoryPath
     */
    public SecurityAccessImpl(String repositoryPath)
    {
       super(repositoryPath);
    }
    
    /**
     * <p>
     * Returns if a Internal UserPrincipal is defined for the user name.
     * </p>
     * 
     * @param username The user name.
     * @return true if the user is known
     */
    public boolean isKnownUser(String username)
    {
        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        String fullPath = userPrincipal.getFullPath();
        // Get user.
        Criteria filter = new Criteria();
        filter.addEqualTo("fullPath", fullPath);
        // The isMappingOnly must not be true.
        // We don't need the mapping only user, mapping user can't be authenticated with this provider. 
        // we just need the true user.
        filter.addEqualTo("isMappingOnly", Boolean.FALSE);
        Query query = QueryFactory.newQuery(InternalUserPrincipalImpl.class, filter);
        return getPersistenceBrokerTemplate().getCount(query) == 1;
    }

    /**
     * <p>
     * Returns the {@link InternalUserPrincipal} from the user name.
     * </p>
     * 
     * @param username The user name.
     * @return The {@link InternalUserPrincipal}.
     */
    public InternalUserPrincipal getInternalUserPrincipal(String username)
    {
        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        String fullPath = userPrincipal.getFullPath();
        // Get user.
        Criteria filter = new Criteria();
        filter.addEqualTo("fullPath", fullPath);
        Query query = QueryFactory.newQuery(InternalUserPrincipalImpl.class, filter);
        InternalUserPrincipal internalUser = (InternalUserPrincipal) getPersistenceBrokerTemplate().getObjectByQuery(query);
        return internalUser;
    }
    
    /**
     * <p>
     * Returns the {@link InternalUserPrincipal} from the user name.
     * </p>
     * 
     * @param username The user name.
     * @param isMappingOnly Whether a principal's purpose is for security mappping only.
     * @return The {@link InternalUserPrincipal}.
     */
    public InternalUserPrincipal getInternalUserPrincipal(String username, boolean isMappingOnly)
    {
        UserPrincipal userPrincipal = new UserPrincipalImpl(username);
        String fullPath = userPrincipal.getFullPath();
        // Get user.
        Criteria filter = new Criteria();
        filter.addEqualTo("fullPath", fullPath);
        filter.addEqualTo("isMappingOnly", new Boolean(isMappingOnly));
        Query query = QueryFactory.newQuery(InternalUserPrincipalImpl.class, filter);
        InternalUserPrincipal internalUser = (InternalUserPrincipal) getPersistenceBrokerTemplate().getObjectByQuery(query);
        return internalUser;
    }

    /**
     * <p>
     * Returns a collection of {@link Principal}given the filter.
     * </p>
     * 
     * @param filter The filter.
     * @return Collection of {@link InternalUserPrincipal}.
     */
    public Iterator getInternalUserPrincipals(String filter)
    {
        Criteria queryCriteria = new Criteria();
        queryCriteria.addEqualTo("isMappingOnly", new Boolean(false));
        queryCriteria.addLike("fullPath", UserPrincipal.PREFS_USER_ROOT + filter + "%");
        Query query = QueryFactory.newQuery(InternalUserPrincipalImpl.class, queryCriteria);
        Iterator result = getPersistenceBrokerTemplate().getIteratorByQuery(query);
        return result;
    }

    /**
     * <p>
     * Sets the given {@link InternalUserPrincipal}.
     * </p>
     * 
     * @param internalUser The {@link InternalUserPrincipal}.
     * @param isMappingOnly Whether a principal's purpose is for security mappping only.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public void setInternalUserPrincipal(InternalUserPrincipal internalUser, boolean isMappingOnly) throws SecurityException
    {
        try
        {
            if (isMappingOnly)
            {
                internalUser.setMappingOnly(isMappingOnly);
            }
            getPersistenceBrokerTemplate().store(internalUser);
        }
        catch (Exception e)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("SecurityAccess.setInternalUserPrincipal",
                                                                   "store",
                                                                   e.getMessage());
            logger.error(msg, e);
            throw new SecurityException(msg, e);
        }
    }

    /**
     * <p>
     * Remove the given {@link InternalUserPrincipal}.
     * </p>
     * 
     * @param internalUser The {@link InternalUserPrincipal}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public void removeInternalUserPrincipal(InternalUserPrincipal internalUser) throws SecurityException
    {
        try
        {
            // Remove user.
            getPersistenceBrokerTemplate().delete(internalUser);
            if (logger.isDebugEnabled())
            {
                logger.debug("Deleted user: " + internalUser.getFullPath());
            }

        }
        catch (Exception e)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("SecurityAccess.removeInternalUserPrincipal",
                                                                   "store",
                                                                   e.getMessage());
            logger.error(msg, e);
            throw new SecurityException(msg, e);
        }
    }

    /**
     * <p>
     * Returns the {@link InternalRolePrincipal}from the role full path name.
     * </p>
     * 
     * @param roleFullPathName The role full path name.
     * @return The {@link InternalRolePrincipal}.
     */
    public InternalRolePrincipal getInternalRolePrincipal(String roleFullPathName)
    {
        Criteria filter = new Criteria();
        filter.addEqualTo("fullPath", roleFullPathName);
        Query query = QueryFactory.newQuery(InternalRolePrincipalImpl.class, filter);
        InternalRolePrincipal internalRole = (InternalRolePrincipal) getPersistenceBrokerTemplate().getObjectByQuery(query);
        return internalRole;
    }
    
    /**
     * <p>
     * Sets the given {@link InternalRolePrincipal}.
     * </p>
     * 
     * @param internalRole The {@link InternalRolePrincipal}.
     * @param isMappingOnly Whether a principal's purpose is for security mappping only.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public void setInternalRolePrincipal(InternalRolePrincipal internalRole, boolean isMappingOnly) throws SecurityException
    {
        try
        {
            if (isMappingOnly)
            {
                internalRole.setMappingOnly(isMappingOnly);
            }
            getPersistenceBrokerTemplate().store(internalRole);
        }
        catch (Exception e)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("SecurityAccess.setInternalRolePrincipal",
                                                                   "store",
                                                                   e.getMessage());
            logger.error(msg, e);
            throw new SecurityException(msg, e);
        }
    }
    
    /**
     * <p>
     * Remove the given {@link InternalRolePrincipal}.
     * </p>
     * 
     * @param internalRole The {@link InternalRolePrincipal}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public void removeInternalRolePrincipal(InternalRolePrincipal internalRole) throws SecurityException
    {
        try
        {
            // Remove role.

            getPersistenceBrokerTemplate().delete(internalRole);
            if (logger.isDebugEnabled())
            {
                logger.debug("Deleted role: " + internalRole.getFullPath());
            }

        }
        catch (Exception e)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("SecurityAccess.removeInternalRolePrincipal",
                                                                   "store",
                                                                   e.getMessage());
            logger.error(msg, e);
            throw new SecurityException(msg, e);
        }
        
    }

    /**
     * <p>
     * Returns the {@link InternalGroupPrincipal}from the group full path name.
     * </p>
     * 
     * @param groupFullPathName The group full path name.
     * @return The {@link InternalGroupPrincipal}.
     */
    public InternalGroupPrincipal getInternalGroupPrincipal(String groupFullPathName)
    {
        Criteria filter = new Criteria();
        filter.addEqualTo("fullPath", groupFullPathName);
        Query query = QueryFactory.newQuery(InternalGroupPrincipalImpl.class, filter);
        InternalGroupPrincipal internalGroup = (InternalGroupPrincipal) getPersistenceBrokerTemplate().getObjectByQuery(query);
        return internalGroup;
    }
    
    /**
     * <p>
     * Sets the given {@link InternalGroupPrincipal}.
     * </p>
     * 
     * @param internalGroup The {@link InternalGroupPrincipal}.
     * @param isMappingOnly Whether a principal's purpose is for security mappping only.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public void setInternalGroupPrincipal(InternalGroupPrincipal internalGroup, boolean isMappingOnly) throws SecurityException
    {
        try
        {
            
            if (isMappingOnly)
            {
                internalGroup.setMappingOnly(isMappingOnly);
            }
            getPersistenceBrokerTemplate().store(internalGroup);
        }
        catch (Exception e)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("SecurityAccess.setInternalGroupPrincipal",
                                                                   "store",
                                                                   e.getMessage());
            logger.error(msg, e);         
            throw new SecurityException(msg, e);
        }
    }
    
    /**
     * <p>
     * Remove the given {@link InternalGroupPrincipal}.
     * </p>
     * 
     * @param internalGroup The {@link InternalGroupPrincipal}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public void removeInternalGroupPrincipal(InternalGroupPrincipal internalGroup) throws SecurityException
    {
        try
        {
            // Remove role.           
            getPersistenceBrokerTemplate().delete(internalGroup);
       
            if (logger.isDebugEnabled())
            {
                logger.debug("Deleted group: " + internalGroup.getFullPath());
            }

        }
        catch (Exception e)
        {
            KeyedMessage msg = SecurityException.UNEXPECTED.create("SecurityAccess.removeInternalGroupPrincipal",
                                                                   "store",
                                                                   e.getMessage());
            logger.error(msg, e);
            throw new SecurityException(msg, e);
        }
        
    }

    public Iterator getInternalRolePrincipals(String filter)
    {
        Criteria queryCriteria = new Criteria();
        queryCriteria.addEqualTo("isMappingOnly", new Boolean(false));
        queryCriteria.addLike("fullPath", UserPrincipal.PREFS_ROLE_ROOT + filter + "%");
        Query query = QueryFactory.newQuery(InternalRolePrincipalImpl.class, queryCriteria);
        Collection c = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return c.iterator();
    }

    public Iterator getInternalGroupPrincipals(String filter)
    {
      
        Criteria queryCriteria = new Criteria();
        queryCriteria.addEqualTo("isMappingOnly", new Boolean(false));
        queryCriteria.addLike("fullPath", UserPrincipal.PREFS_GROUP_ROOT + filter + "%");        
        Query query = QueryFactory.newQuery(InternalGroupPrincipalImpl.class, queryCriteria);
        Collection c = getPersistenceBrokerTemplate().getCollectionByQuery(query);
        return c.iterator();
    }
    
}