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
package org.apache.jetspeed.security;


/**
 * @author <a href="mailto:vkumar@apache.org">Vivek Kumar</a>
 * @version $Id:
 */
public interface PrincipalManagerEventListener {

    /**
     * newPrincipal  - invoked when new principal is
     *                 created by the principal manager
     *
     * @param principal new managed JetspeedPrincipal 
     */
    void newPrincipal(JetspeedPrincipal principal);

    /**
     * updatePrincipal  - invoked when an principal is
     *                    updated by the principal manager
     *
     * @param principal new managed JetspeedPrincipal 
     */
    void updatePrincipal(JetspeedPrincipal principal);
    
    /**
     * removePrincipal - invoked when an principal is
     *                   removed by the principal manager
     *
     * @param principal new managed JetspeedPrincipal 
     */
    void removePrincipal(JetspeedPrincipal principal);
        
    /**
     * associationAdded - invoked when an association is added 
     * 					  on principal                  
     *
     * @param fromPrincipal new managed JetspeedPrincipal
     * @param associationName Name of association which is added to principal
     */
    void associationAdded(JetspeedPrincipal fromPrincipal, JetspeedPrincipal toPrincipal,String associationName);
    
    /**
     * associationRemoved - invoked when an association is added 
     * 					  on principal                  
     *
     * @param fromPrincipal new managed JetspeedPrincipal
     * @param associationName Name of association which is removed to principal
     */
    void associationRemoved(JetspeedPrincipal fromPrincipal, JetspeedPrincipal toPrincipal,String associationName);
}
