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
package org.apache.jetspeed.om.common;

import java.util.List;

/**
 * <p>
 * SecurityConstraint
 * </p>
 * <p>
 * Used by SecureResource to specify access constraints for
 * security purposes.
 *
 * </p>
 * @author <a href="mailto:rwatler@finali.com">Randy Watler</a>
 * @version $Id$
 *
 */
public interface SecurityConstraint
{   
    String WILD_CHAR = "*";    

    /**
     * <p>
     * getUsers
     * </p>
     *
     * @return constraint users in CSV string form
     */
    String getUsers();
    
    /**
     * <p>
     * getUsersList
     * </p>
     *
     * @return constraint users as List
     */
    List getUsersList();
    
    /**
     * <p>
     * setUsers
     * </p>
     *
     * @param users constraint list in CSV string form
     */
    void setUsers(String users);

    /**
     * <p>
     * getRoles
     * </p>
     *
     * @return constraint roles in CSV string form
     */
    String getRoles();
    
    /**
     * <p>
     * getRolesList
     * </p>
     *
     * @return constraint roles as List
     */
    List getRolesList();
    
    /**
     * <p>
     * setRoles
     * </p>
     *
     * @param roles constraint list in CSV string form
     */
    void setRoles(String roles);

    /**
     * <p>
     * getGroups
     * </p>
     *
     * @return constraint groups in CSV string form
     */
    String getGroups();
    
    /**
     * <p>
     * getGroupsList
     * </p>
     *
     * @return constraint groups as List
     */
    List getGroupsList();
    
    /**
     * <p>
     * setGroups
     * </p>
     *
     * @param groups constraint list in CSV string form
     */
    void setGroups(String groups);

    /**
     * <p>
     * getPermissions
     * </p>
     *
     * @return constraint permissions in CSV string form
     */
    String getPermissions();
    
    /**
     * <p>
     * getPermissionsList
     * </p>
     *
     * @return constraint permissions as List
     */
    List getPermissionsList();
    
    /**
     * <p>
     * setPermissions
     * </p>
     *
     * @param permissions constraint list in CSV string form
     */
    void setPermissions(String permissions);
}
