/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.persistence.store.Filter;
import org.apache.jetspeed.components.persistence.store.PersistenceStore;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserPrincipal;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.security.om.InternalUserPrincipal;
import org.apache.jetspeed.security.om.impl.InternalUserPrincipalImpl;

/**
 * <p>
 * Provides a utility class for common SPI queries.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class CommonQueries
{
    private static final Log log = LogFactory.getLog(CommonQueries.class);

    /** The persistence store. * */
    private PersistenceStore persistenceStore;

    /**
     * @param persistenceStore The persistence store.
     */
    public CommonQueries(PersistenceStore persistenceStore)
    {
        if (persistenceStore == null)
        {
            throw new IllegalArgumentException("persistenceStore cannot be null for BaseSecurityImpl");
        }

        this.persistenceStore = persistenceStore;
    }

    /**
     * <p>
     * Returns the {@link InternalUserPrincipal}from the user name.
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
        Filter filter = persistenceStore.newFilter();
        filter.addEqualTo("fullPath", fullPath);
        Object query = persistenceStore.newQuery(InternalUserPrincipalImpl.class, filter);
        InternalUserPrincipal omUser = (InternalUserPrincipal) persistenceStore.getObjectByQuery(query);
        return omUser;
    }
    
    /**
     * <p>
     * Returns a collection of {@link Principal} given the filter.
     * </p>
     * 
     * @param filter The filter.
     * @return Collection of {@link InternalUserPrincipal}.
     */
    public Iterator getInternalUserPrincipals(String filter)
    {
        Iterator result = persistenceStore.getExtent(InternalUserPrincipalImpl.class).iterator();
        return result;
    }

    /**
     * <p>
     * Sets the given {@link InternalUserPrincipal}.
     * </p>
     * 
     * @param internalUser The {@link InternalUserPrincipal}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    public void setInternalUserPrincipal(InternalUserPrincipal internalUser) throws SecurityException
    {
        try
        {
            persistenceStore.lockForWrite(internalUser);
            persistenceStore.getTransaction().checkpoint();
        }
        catch (Exception e)
        {
            String msg = "Unable to lock user for update.";
            log.error(msg, e);
            persistenceStore.getTransaction().rollback();
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
            persistenceStore.deletePersistent(internalUser);
            persistenceStore.getTransaction().checkpoint();
            if (log.isDebugEnabled())
            {
                log.debug("Deleted user: " + internalUser.getFullPath());
            }

        }
        catch (Exception e)
        {
            String msg = "Unable to lock User for update.";
            log.error(msg, e);
            persistenceStore.getTransaction().rollback();
            throw new SecurityException(msg, e);
        }
    }
}