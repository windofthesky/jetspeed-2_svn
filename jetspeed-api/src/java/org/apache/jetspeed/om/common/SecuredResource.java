/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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
     * newSecurityConstraints
     * </p>
     *
     * @return a newly created SecurityConstraints object for use in SecuredResource
     */
    SecurityConstraints newSecurityConstraints();

    /**
     * <p>
     * newSecurityConstraint
     * </p>
     *
     * @return a newly created SecurityConstraint object for use in SecuredResource
     */
    SecurityConstraint newSecurityConstraint();

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
     * @param mask Mask of actions to be checked
     * @throws SecurityException
     */
    void checkPermissions(int mask) throws SecurityException;

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
