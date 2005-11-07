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
package org.apache.jetspeed.om.page;

import java.util.List;

/**
 * <p>
 * Security
 * </p>
 * <p>
 * Used to define named collections of SecurityConstraint objects.
 *
 * </p>
 * @author <a href="mailto:rwatler@finali.com">Randy Watler</a>
 * @version $Id$
 *
 */
public interface PageSecurity extends Document
{   
    String DOCUMENT_TYPE = "page.security";
    
    /**
     * <p>
     * getSecurityConstraintsDefs
     * </p>
     *
     * @return security constraints definitions
     */
    List getSecurityConstraintsDefs();
    
    /**
     * <p>
     * setSecurityConstraintsDefs
     * </p>
     *
     * @param defintions security constraints definitions
     */
    void setSecurityConstraintsDefs(List definitions);

    /**
     * <p>
     * getSecurityConstraintsDef
     * </p>
     *
     * @param name of security constraints definition to return
     * @return security constraints definition
     */
    SecurityConstraintsDef getSecurityConstraintsDef(String name);

    /**
     * <p>
     * getGlobalSecurityConstraintsRefs
     * </p>
     *
     * @return global security constraints references of element type String
     */
    List getGlobalSecurityConstraintsRefs();
    
    /**
     * <p>
     * setGlobalSecurityConstraintsRefs
     * </p>
     *
     * @param constraintsRefs global security constraints references
     */
    void setGlobalSecurityConstraintsRefs(List constraintsRefs);
}
