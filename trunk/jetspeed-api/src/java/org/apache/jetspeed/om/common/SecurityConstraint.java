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
     * @return constraint users list as List of String
     */
    List getUsers();
    
    /**
     * <p>
     * setUsers
     * </p>
     *
     * @param users constraint users list as List of String
     */
    void setUsers(List users);
    
    /**
     * <p>
     * getRoles
     * </p>
     *
     * @return constraint roles list as List of String
     */
    List getRoles();
    
    /**
     * <p>
     * setRoles
     * </p>
     *
     * @param roles constraint roles list as List of String
     */
    void setRoles(List roles);
    
    /**
     * <p>
     * getGroups
     * </p>
     *
     * @return constraint groups list as List of String
     */
    List getGroups();
    
    /**
     * <p>
     * setGroups
     * </p>
     *
     * @param groups constraint groups list as List of String
     */
    void setGroups(List groups);
    
    /**
     * <p>
     * getPermissions
     * </p>
     *
     * @return constraint permissions list as List of String
     */
    List getPermissions();
    
    /**
     * <p>
     * setPermissions
     * </p>
     *
     * @param permissions constraint permissions list as List of String
     */
    void setPermissions(List permissions);
}
