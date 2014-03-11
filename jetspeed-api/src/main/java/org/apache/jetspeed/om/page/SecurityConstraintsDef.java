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
package org.apache.jetspeed.om.page;

import org.apache.jetspeed.om.common.SecurityConstraint;

import java.util.List;

/**
 * <p>
 * SecurityConstraintsDef
 * </p>
 * <p>
 * Used to specify a named collection of SecurityConstraint objects.
 *
 * </p>
 * @author <a href="mailto:rwatler@finali.com">Randy Watler</a>
 * @version $Id$
 *
 */
public interface SecurityConstraintsDef
{   
    /**
     * <p>
     * getName
     * </p>
     *
     * @return constraints name used by references
     */
    String getName();
    
    /**
     * <p>
     * setName
     * </p>
     *
     * @param name constraints name used by references
     */
    void setName(String name);

    /**
     * <p>
     * getSecurityConstraints
     * </p>
     *
     * @return security constraints list for resource
     */
    List<SecurityConstraint> getSecurityConstraints();
    
    /**
     * <p>
     * setSecurityConstraints
     * </p>
     *
     * @param constraints security constraints for resource
     */
    void setSecurityConstraints(List<SecurityConstraint> constraints);
}
