/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.spi;

import java.util.Iterator;

import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.om.InternalGroupPrincipal;
import org.apache.jetspeed.security.om.InternalRolePrincipal;
import org.apache.jetspeed.security.om.InternalUserPrincipal;

/**
 * <p>
 * SecurityAccess
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface SecurityAccess
{
    /**
     * <p>
     * Returns if a Internal UserPrincipal is defined for the user name.
     * </p>
     * 
     * @param username The user name.
     * @return true if the user is known
     */
    public boolean isKnownUser(String username);

    /**
     * <p>
     * Returns the {@link InternalUserPrincipal} from the user name.
     * </p>
     * 
     * @param username The user name.
     * @return The {@link InternalUserPrincipal}.
     */
    InternalUserPrincipal getInternalUserPrincipal( String username );

    /**
     * <p>
     * Returns the {@link InternalUserPrincipal} from the user name.
     * </p>
     * 
     * @param username The user name.
     * @param isMappingOnly Whether a principal's purpose is for security mappping only.
     * @return The {@link InternalUserPrincipal}.
     */
    InternalUserPrincipal getInternalUserPrincipal( String username, boolean isMappingOnly );

    /**
     * <p>
     * Returns a collection of {@link Principal}given the filter.
     * </p>
     * 
     * @param filter The filter.
     * @return Collection of {@link InternalUserPrincipal}.
     */
    Iterator getInternalUserPrincipals( String filter );

    /**
     * <p>
     * Sets the given {@link InternalUserPrincipal}.
     * </p>
     * 
     * @param internalUser The {@link InternalUserPrincipal}.
     * @param isMappingOnly Whether a principal's purpose is for security mappping only.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void setInternalUserPrincipal( InternalUserPrincipal internalUser, boolean isMappingOnly ) throws SecurityException;

    /**
     * <p>
     * Remove the given {@link InternalUserPrincipal}.
     * </p>
     * 
     * @param internalUser The {@link InternalUserPrincipal}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void removeInternalUserPrincipal( InternalUserPrincipal internalUser ) throws SecurityException;

    /**
     * <p>
     * Returns the {@link InternalRolePrincipal}from the role full path name.
     * </p>
     * 
     * @param username The role full path name.
     * @return The {@link InternalRolePrincipal}.
     */
    InternalRolePrincipal getInternalRolePrincipal( String roleFullPathName );

    /**
     * <p>
     * Sets the given {@link InternalRolePrincipal}.
     * </p>
     * 
     * @param internalRole The {@link InternalRolePrincipal}.
     * @param isMappingOnly Whether a principal's purpose is for security mappping only.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void setInternalRolePrincipal( InternalRolePrincipal internalRole, boolean isMappingOnly ) throws SecurityException;

    /**
     * <p>
     * Remove the given {@link InternalRolePrincipal}.
     * </p>
     * 
     * @param internalRole The {@link InternalRolePrincipal}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void removeInternalRolePrincipal( InternalRolePrincipal internalRole ) throws SecurityException;

    /**
     * <p>
     * Returns the {@link InternalGroupPrincipal}from the group full path name.
     * </p>
     * 
     * @param username The group full path name.
     * @return The {@link InternalGroupPrincipal}.
     */
    InternalGroupPrincipal getInternalGroupPrincipal( String groupFullPathName );

    /**
     * <p>
     * Sets the given {@link InternalGroupPrincipal}.
     * </p>
     * 
     * @param internalGroup The {@link internalGroupPrincipal}.
     * @param isMappingOnly Whether a principal's purpose is for security mappping only.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void setInternalGroupPrincipal( InternalGroupPrincipal internalGroup, boolean isMappingOnly )
            throws SecurityException;

    /**
     * <p>
     * Remove the given {@link InternalGroupPrincipal}.
     * </p>
     * 
     * @param internalGroup The {@link InternalGroupPrincipal}.
     * @throws SecurityException Throws a {@link SecurityException}.
     */
    void removeInternalGroupPrincipal( InternalGroupPrincipal internalGroup ) throws SecurityException;
}