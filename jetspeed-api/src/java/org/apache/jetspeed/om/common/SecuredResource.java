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


/**
 * <p>
 * SecuredResource
 * </p>
 * <p>
 * Implemented by those resources that have a security constraint defined for
 * security purposes.
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface SecuredResource
{   
    String VIEW_ACTION = "view";
    String EDIT_ACTION = "edit";

    /**
     * <p>
     * getConstraintsEnabled
     * </p>
     *
     * @return enabled indicator
     */
    boolean getConstraintsEnabled();
    
    /**
     * <p>
     * getSecurityConstraints
     * </p>
     *
     * @return security constraints for resource
     */
    SecurityConstraints getSecurityConstraints();
    
    /**
     * <p>
     * setSecurityConstraints
     * </p>
     *
     * @param constraints security constraints for resource
     */
    void setSecurityConstraints(SecurityConstraints constraints);

    /**
     * <p>
     * checkConstraints
     * </p>
     *
     * @param actions list to be checked against in CSV string form
     * @throws SecurityException
     */
    void checkConstraints(String actions) throws SecurityException;

    /**
     * <p>
     * getPermissionsEnabled
     * </p>
     *
     * @return enabled indicator
     */
    boolean getPermissionsEnabled();
    
    /**
     * <p>
     * checkPermissions
     * </p>
     *
     * @param actions list to be checked against in CSV string form
     * @throws SecurityException
     */
    void checkPermissions(String actions) throws SecurityException;

    /**
     * <p>
     * checkAccess
     * </p>
     *
     * @param actions list to be checked against in CSV string form
     * @throws SecurityException
     */
    void checkAccess(String actions) throws SecurityException;
}
